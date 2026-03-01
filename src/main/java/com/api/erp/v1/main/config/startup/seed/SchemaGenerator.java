package com.api.erp.v1.main.config.startup.seed;

import com.api.erp.v1.main.tenant.domain.entity.DBType;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Gerador de schemas SQL a partir de entidades JPA/Hibernate.
 * <p>
 * Esta classe escaneia as entidades anotadas com @Entity no pacote base,
 * gera o DDL correspondente para cada banco de dados especificado e organiza
 * os scripts SQL em arquivos separados por tabela e feature (opcional).
 * </p>
 * <p>
 * <b>Ordenação por Dependências:</b> As migrations são numeradas respeitando
 * as dependências entre tabelas (foreign keys), garantindo que tabelas
 * referenciadas sejam criadas antes das tabelas que as referenciam.
 * </p>
 */
@Slf4j
public class SchemaGenerator {

    // ================= CONFIGURAÇÕES =================

    private static final String OUTPUT_DIR = "schemas";
    private static final String BASE_PACKAGE = "com.api.erp.v1";
    private static final String TEMP_SCHEMA_FILE = "_schemas.sql";
    private static final String MIGRATION_PREFIX = "V";
    private static final String TABLE_PREFIX = "__CREATE_TB_";
    private static final String SQL_EXTENSION = ".sql";
    private static final String FEATURES_PACKAGE = ".features.";
    private static final String SEQUENCE_SUFFIX = "_seq";

    private static final boolean ORGANIZAR_POR_FEATURE = false;

    // ================= REGEX PATTERNS =================

    private static final Pattern CREATE_TABLE = Pattern.compile("create\\s+table\\s+(if\\s+not\\s+exists\\s+)?([`\"\\[]?\\w+[`\"\\]]?)", Pattern.CASE_INSENSITIVE);

    private static final Pattern ALTER_TABLE = Pattern.compile("alter\\s+table\\s+(if\\s+exists\\s+)?([`\"\\[]?\\w+[`\"\\]]?)", Pattern.CASE_INSENSITIVE);

    private static final Pattern CREATE_SEQUENCE = Pattern.compile("create\\s+sequence\\s+([`\"\\[]?\\w+[`\"\\]]?)", Pattern.CASE_INSENSITIVE);

    private static final Pattern CREATE_INDEX = Pattern.compile("create\\s+(unique\\s+)?index\\s+\\w+\\s+on\\s+([`\"\\[]?\\w+[`\"\\]]?)", Pattern.CASE_INSENSITIVE);

    private static final Pattern REFERENCES = Pattern.compile("references\\s+([`\"\\[]?\\w+[`\"\\]]?)", Pattern.CASE_INSENSITIVE);

    // ================= MÉTODOS PÚBLICOS =================

    /**
     * Executa a geração de schemas para todos os tipos de banco de dados disponíveis.
     */
    public static void executar() {
        executar(DBType.values());
    }

    /**
     * Executa a geração de schemas para os tipos de banco de dados especificados.
     *
     * @param dbTypes tipos de banco de dados para gerar os schemas
     */
    public static void executar(DBType... dbTypes) {
        log.info("=== Iniciando geração de schemas ===");

        for (DBType dbType : dbTypes) {
            try {
                log.info("Gerando schemas para banco: {}", dbType.getNome());
                gerarSchemaPorTabela(dbType);
                log.info("Schemas gerados com sucesso para: {}", dbType.getNome());
            } catch (Exception e) {
                log.error("Erro ao gerar schema para banco {}: {}", dbType.getNome(), e.getMessage(), e);
            }
        }

        log.info("=== Geração de schemas finalizada ===");
    }

    // ================= GERAÇÃO DE SCHEMA =================

    /**
     * Gera os schemas SQL organizados por tabela para um tipo específico de banco.
     *
     * @param dbType tipo de banco de dados
     */
    private static void gerarSchemaPorTabela(DBType dbType) {
        StandardServiceRegistry registry = criarServiceRegistry(dbType);

        try {
            Path pastaOutput = prepararDiretorioOutput(dbType);
            Metadata metadata = construirMetadata(registry);
            Path arquivoTemp = gerarSchemaCompleto(dbType, pastaOutput, metadata);

            dividirSchemaEmArquivos(arquivoTemp, pastaOutput, metadata);

//            Files.deleteIfExists(arquivoTemp);

        } catch (IOException e) {
            log.error("Erro de I/O ao gerar schema: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na geração do schema", e);
        } finally {
            if (registry != null) {
                StandardServiceRegistryBuilder.destroy(registry);
            }
        }
    }

    /**
     * Cria e configura o ServiceRegistry do Hibernate.
     */
    private static StandardServiceRegistry criarServiceRegistry(DBType dbType) {
        return new StandardServiceRegistryBuilder().applySetting("hibernate.connection.driver_class", dbType.getDriver()).applySetting("hibernate.dialect", dbType.getDialeto()).build();
    }

    /**
     * Prepara o diretório de output, limpando conteúdo anterior se existir.
     */
    private static Path prepararDiretorioOutput(DBType dbType) throws IOException {
        Path pastaOutput = Paths.get(OUTPUT_DIR, dbType.getNome());

        if (Files.exists(pastaOutput)) {
            log.debug("Limpando diretório existente: {}", pastaOutput);
            deletarDiretorioRecursivamente(pastaOutput.toFile());
        }

        Files.createDirectories(pastaOutput);
        log.debug("Diretório criado: {}", pastaOutput);

        return pastaOutput;
    }

    /**
     * Constrói os metadados do Hibernate escaneando as entidades.
     */
    private static Metadata construirMetadata(StandardServiceRegistry registry) {
        MetadataSources sources = new MetadataSources(registry);
        Reflections reflections = new Reflections(BASE_PACKAGE);

        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
        Set<Class<?>> converters = reflections.getTypesAnnotatedWith(Converter.class);

        log.debug("Encontradas {} entidades e {} conversores", entities.size(), converters.size());

        entities.forEach(sources::addAnnotatedClass);
        converters.forEach(sources::addAnnotatedClass);

        return sources.buildMetadata();
    }

    /**
     * Gera o arquivo SQL completo antes da divisão.
     */
    private static Path gerarSchemaCompleto(DBType dbType, Path pastaOutput, Metadata metadata) {
        Path arquivoTemp = pastaOutput.resolve(TEMP_SCHEMA_FILE);

        SchemaExport export = new SchemaExport();
        export.setFormat(true);
        export.setDelimiter(";");
        export.setOutputFile(arquivoTemp.toString());
        export.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.CREATE, metadata);

        log.debug("Schema completo gerado em: {}", arquivoTemp);
        return arquivoTemp;
    }

    // ================= DIVISÃO DO SCHEMA =================

    /**
     * Divide o schema completo em arquivos individuais por tabela.
     */
    private static void dividirSchemaEmArquivos(Path arquivoSchema, Path pastaOutput, Metadata metadata) throws IOException {

        String sqlCompleto = Files.readString(arquivoSchema);
        List<String> statements = parseStatements(sqlCompleto);

        Map<String, EntityInfo> metadataInfo = extrairInformacoesEntidades(metadata);
        Map<String, List<String>> statementsPorTabela = agruparStatementsPorTabela(statements, metadataInfo);

        // NOVA LÓGICA: Analisar dependências e ordenar
        Map<String, Set<String>> dependencias = analisarDependencias(statementsPorTabela);
        List<String> ordemTabelas = ordenarPorDependencias(dependencias);

        log.info("Ordem de criação das tabelas (respeitando dependências):");
        ordemTabelas.forEach(tabela -> log.info("  → {}", tabela));

        // Escrever arquivos na ordem correta
        int versao = 1;
        for (String tabela : ordemTabelas) {
            List<String> stmts = statementsPorTabela.get(tabela);
            if (stmts != null && !stmts.isEmpty()) {
                escreverArquivoMigracao(pastaOutput, tabela, stmts, metadataInfo.get(tabela), versao++);
            }
        }
    }

    /**
     * Faz o parse dos statements SQL do arquivo completo.
     */
    private static List<String> parseStatements(String sql) {
        return Arrays.stream(sql.split(";")).map(String::trim).filter(stmt -> !stmt.isEmpty()).collect(Collectors.toList());
    }

    /**
     * Agrupa os statements SQL por tabela relacionada.
     */
    private static Map<String, List<String>> agruparStatementsPorTabela(List<String> statements, Map<String, EntityInfo> metadataInfo) {

        Map<String, List<String>> agrupamento = new HashMap<>();

        for (String statement : statements) {
            Set<String> tabelasRelacionadas = extrairTabelasDoStatement(statement);

            for (String tabela : tabelasRelacionadas) {
                if (metadataInfo.containsKey(tabela)) {
                    agrupamento.computeIfAbsent(tabela, k -> new ArrayList<>()).add(statement + ";");
                }
            }
        }

        return agrupamento;
    }

    // ================= ANÁLISE DE DEPENDÊNCIAS =================

    /**
     * Analisa as dependências entre tabelas baseado em foreign keys (REFERENCES).
     *
     * @param statementsPorTabela statements agrupados por tabela
     * @return mapa onde a chave é a tabela e o valor são as tabelas das quais ela depende
     */
    private static Map<String, Set<String>> analisarDependencias(Map<String, List<String>> statementsPorTabela) {
        Map<String, Set<String>> dependencias = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : statementsPorTabela.entrySet()) {
            String tabela = entry.getKey();
            Set<String> deps = new HashSet<>();

            for (String statement : entry.getValue()) {
                // Extrair tabelas referenciadas (REFERENCES)
                Matcher matcher = REFERENCES.matcher(statement);
                while (matcher.find()) {
                    String tabelaReferenciada = normalizarNomeTabela(matcher.group(1));

                    // Só adiciona se não for auto-referência
                    if (!tabelaReferenciada.equals(tabela) && statementsPorTabela.containsKey(tabelaReferenciada)) {
                        deps.add(tabelaReferenciada);
                    }
                }
            }

            dependencias.put(tabela, deps);
        }

        log.debug("Dependências encontradas:");
        dependencias.forEach((tabela, deps) -> {
            if (!deps.isEmpty()) {
                log.debug("  {} -> {}", tabela, deps);
            }
        });

        return dependencias;
    }

    /**
     * Ordena as tabelas usando algoritmo de ordenação topológica (Kahn's Algorithm).
     * Garante que tabelas referenciadas sejam criadas antes das tabelas que as referenciam.
     *
     * @param dependencias mapa de dependências entre tabelas
     * @return lista de tabelas na ordem correta de criação
     * @throws IllegalStateException se houver dependência circular
     */
    private static List<String> ordenarPorDependencias(Map<String, Set<String>> dependencias) {
        List<String> ordemFinal = new ArrayList<>();
        Map<String, Integer> grauEntrada = calcularGrauEntrada(dependencias);
        Queue<String> fila = new LinkedList<>();

        // Adiciona na fila as tabelas sem dependências
        for (Map.Entry<String, Integer> entry : grauEntrada.entrySet()) {
            if (entry.getValue() == 0) {
                fila.offer(entry.getKey());
            }
        }

        // Processa a fila
        while (!fila.isEmpty()) {
            String tabela = fila.poll();
            ordemFinal.add(tabela);

            // Para cada tabela que depende desta
            for (Map.Entry<String, Set<String>> entry : dependencias.entrySet()) {
                if (entry.getValue().contains(tabela)) {
                    String dependente = entry.getKey();
                    int novoGrau = grauEntrada.get(dependente) - 1;
                    grauEntrada.put(dependente, novoGrau);

                    if (novoGrau == 0) {
                        fila.offer(dependente);
                    }
                }
            }
        }

        // Verifica se todas as tabelas foram processadas (detecta ciclos)
        if (ordemFinal.size() != dependencias.size()) {
            Set<String> tabelasNaoProcessadas = new HashSet<>(dependencias.keySet());
            tabelasNaoProcessadas.removeAll(ordemFinal);

            log.warn("⚠️  ATENÇÃO: Dependência circular detectada!");
            log.warn("Tabelas com dependência circular: {}", tabelasNaoProcessadas);
            log.warn("Estas tabelas serão adicionadas ao final da ordem:");

            // Adiciona as tabelas restantes ao final (melhor que falhar completamente)
            ordemFinal.addAll(tabelasNaoProcessadas);
        }

        return ordemFinal;
    }

    /**
     * Calcula o grau de entrada de cada tabela (quantas outras tabelas ela depende).
     */
    private static Map<String, Integer> calcularGrauEntrada(Map<String, Set<String>> dependencias) {
        Map<String, Integer> grauEntrada = new HashMap<>();

        // Inicializa todas com 0
        for (String tabela : dependencias.keySet()) {
            grauEntrada.put(tabela, 0);
        }

        // Conta quantas dependências cada tabela tem
        for (Set<String> deps : dependencias.values()) {
            for (String dep : deps) {
                grauEntrada.merge(dep, 0, (old, val) -> old); // Garante que existe
            }
        }

        for (Map.Entry<String, Set<String>> entry : dependencias.entrySet()) {
            grauEntrada.put(entry.getKey(), entry.getValue().size());
        }

        return grauEntrada;
    }

    // ================= ESCRITA DE ARQUIVOS =================

    /**
     * Escreve o arquivo de migração para uma tabela específica.
     */
    private static void escreverArquivoMigracao(Path pastaBase, String nomeTabela, List<String> statements, EntityInfo entityInfo, int versao) throws IOException {

        Path pastaDestino = determinarPastaDestino(pastaBase, entityInfo);
        Files.createDirectories(pastaDestino);

        String nomeArquivo = construirNomeArquivo(nomeTabela, versao);
        Path arquivoFinal = pastaDestino.resolve(nomeArquivo);

        escreverConteudoArquivo(arquivoFinal, nomeTabela, entityInfo, statements);

        log.info("  ✓ {}", nomeArquivo);
    }

    /**
     * Determina a pasta de destino baseado na feature (se organização por feature estiver ativa).
     */
    private static Path determinarPastaDestino(Path pastaBase, EntityInfo entityInfo) {
        if (ORGANIZAR_POR_FEATURE && entityInfo.feature() != null) {
            return pastaBase.resolve(entityInfo.feature());
        }
        return pastaBase;
    }

    /**
     * Constrói o nome do arquivo de migração seguindo o padrão Flyway.
     */
    private static String construirNomeArquivo(String nomeTabela, int versao) {
        return MIGRATION_PREFIX + versao + TABLE_PREFIX + nomeTabela.toUpperCase() + SQL_EXTENSION;
    }

    /**
     * Escreve o conteúdo completo do arquivo de migração.
     */
    private static void escreverConteudoArquivo(Path arquivo, String nomeTabela, EntityInfo entityInfo, List<String> statements) throws IOException {

        StringBuilder conteudo = new StringBuilder();

        // Cabeçalho com metadados
        conteudo.append("-- Tabela: ").append(nomeTabela).append("\n");
        conteudo.append("-- Entidade: ").append(entityInfo.nomeClasse()).append("\n");

        if (entityInfo.feature() != null) {
            conteudo.append("-- Feature: ").append(entityInfo.feature()).append("\n");
        }

        conteudo.append("\n");

        // Statements SQL
        for (String statement : statements) {
            conteudo.append(statement).append("\n\n");
        }

        Files.writeString(arquivo, conteudo.toString());
    }

    // ================= EXTRAÇÃO DE TABELAS =================

    /**
     * Extrai todos os nomes de tabelas referenciados em um statement SQL.
     */
    private static Set<String> extrairTabelasDoStatement(String statement) {
        Set<String> tabelas = new HashSet<>();

        extrairTabelasComPattern(tabelas, CREATE_TABLE, statement, 2);
        extrairTabelasComPattern(tabelas, ALTER_TABLE, statement, 2);
        extrairTabelasComPattern(tabelas, CREATE_INDEX, statement, 2);
        extrairTabelasDeSequences(tabelas, statement);
        extrairTabelasDeReferences(tabelas, statement);

        return tabelas;
    }

    /**
     * Extrai tabelas usando um pattern regex específico.
     */
    private static void extrairTabelasComPattern(Set<String> tabelas, Pattern pattern, String statement, int grupoCaptura) {

        Matcher matcher = pattern.matcher(statement);
        while (matcher.find()) {
            String nomeTabela = normalizarNomeTabela(matcher.group(grupoCaptura));
            tabelas.add(nomeTabela);
        }
    }

    /**
     * Extrai tabelas relacionadas a sequences (removendo sufixo _seq).
     */
    private static void extrairTabelasDeSequences(Set<String> tabelas, String statement) {
        Matcher matcher = CREATE_SEQUENCE.matcher(statement);

        if (matcher.find()) {
            String nomeSequence = normalizarNomeTabela(matcher.group(1));

            if (nomeSequence.endsWith(SEQUENCE_SUFFIX)) {
                String nomeTabela = nomeSequence.substring(0, nomeSequence.length() - SEQUENCE_SUFFIX.length());
                tabelas.add(nomeTabela);
            }
        }
    }

    /**
     * Extrai tabelas de cláusulas REFERENCES (foreign keys).
     */
    private static void extrairTabelasDeReferences(Set<String> tabelas, String statement) {
        Matcher matcher = REFERENCES.matcher(statement);

        while (matcher.find()) {
            String nomeTabela = normalizarNomeTabela(matcher.group(1));
            tabelas.add(nomeTabela);
        }
    }

    /**
     * Normaliza o nome da tabela removendo delimitadores e convertendo para lowercase.
     */
    private static String normalizarNomeTabela(String nome) {
        return nome.replace("`", "").replace("\"", "").replace("[", "").replace("]", "").toLowerCase().trim();
    }

    // ================= METADADOS DAS ENTIDADES =================

    /**
     * Extrai informações das entidades a partir dos metadados do Hibernate.
     */
    private static Map<String, EntityInfo> extrairInformacoesEntidades(Metadata metadata) {
        Map<String, EntityInfo> mapa = new HashMap<>();

        for (PersistentClass persistentClass : metadata.getEntityBindings()) {
            String nomeTabela = persistentClass.getTable().getName().toLowerCase();
            String nomeCompletoClasse = persistentClass.getClassName();
            String nomeSimpllesClasse = extrairNomeSimpllesClasse(nomeCompletoClasse);
            String feature = extrairFeatureDaClasse(nomeCompletoClasse);

            EntityInfo info = new EntityInfo(nomeTabela, nomeSimpllesClasse, feature);
            mapa.put(nomeTabela, info);
        }

        log.debug("Extraídas informações de {} entidades", mapa.size());
        return mapa;
    }

    /**
     * Extrai o nome simples da classe (sem o pacote completo).
     */
    private static String extrairNomeSimpllesClasse(String nomeCompletoClasse) {
        int ultimoPonto = nomeCompletoClasse.lastIndexOf('.');
        return ultimoPonto >= 0 ? nomeCompletoClasse.substring(ultimoPonto + 1) : nomeCompletoClasse;
    }

    /**
     * Extrai o nome da feature a partir do nome completo da classe.
     * <p>
     * Procura por '.features.' no package e retorna o próximo segmento.
     * </p>
     */
    private static String extrairFeatureDaClasse(String nomeCompletoClasse) {
        if (!nomeCompletoClasse.contains(FEATURES_PACKAGE)) {
            return null;
        }

        String[] segmentos = nomeCompletoClasse.split("\\.");

        for (int i = 0; i < segmentos.length - 1; i++) {
            if ("features".equals(segmentos[i])) {
                return segmentos[i + 1];
            }
        }

        return null;
    }

    // ================= UTILITÁRIOS =================

    /**
     * Deleta um diretório e todo seu conteúdo recursivamente.
     */
    private static void deletarDiretorioRecursivamente(File diretorio) throws IOException {
        if (!diretorio.exists()) {
            return;
        }

        if (diretorio.isDirectory()) {
            File[] arquivos = diretorio.listFiles();
            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    deletarDiretorioRecursivamente(arquivo);
                }
            }
        }

        Files.deleteIfExists(diretorio.toPath());
    }

    // ================= CLASSES INTERNAS =================

    /**
     * Record que armazena informações sobre uma entidade JPA.
     *
     * @param nomeTabela nome da tabela no banco de dados
     * @param nomeClasse nome simples da classe da entidade
     * @param feature    nome da feature/módulo (pode ser null)
     */
    record EntityInfo(String nomeTabela, String nomeClasse, String feature) {
    }
}
