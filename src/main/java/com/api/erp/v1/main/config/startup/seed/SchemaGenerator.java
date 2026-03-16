package com.api.erp.v1.main.config.startup.seed;

import com.api.erp.v1.main.master.tenant.domain.entity.DBType;
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
 * <b>Dependency Ordering:</b> Migrations are numbered respecting
 * table dependencies (foreign keys), ensuring that tables
 * referenced are created before the tables that reference them.
 * </p>
 */
@Slf4j
public class SchemaGenerator {

    // ================= CONFIGURATIONS =================

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

    // ================= PUBLIC METHODS =================

    /**
     * Executes schema generation for all available database types.
     */
    public static void execute() {
        execute(DBType.values());
    }

    /**
     * Executes schema generation for specified database types.
     *
     * @param dbTypes database types for which to generate schemas
     */
    public static void execute(DBType... dbTypes) {
        log.info("=== Starting schema generation ===");

        for (DBType dbType : dbTypes) {
            try {
                log.info("Generating schemas for database: {}", dbType.getNome());
                gerarSchemaPorTabela(dbType);
                log.info("Schemas generated successfully for: {}", dbType.getNome());
            } catch (Exception e) {
                log.error("Error generating schema for database {}: {}", dbType.getNome(), e.getMessage(), e);
            }
        }

        log.info("=== Schema generation completed ===");
    }

    // ================= SCHEMA GENERATION =================

    /**
     * Generates SQL schemas organized by table for a specific database type.
     *
     * @param dbType database type
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
            log.error("I/O error generating schema: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate schema", e);
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
     * Prepares output directory, cleaning previous content if it exists.
     */
    private static Path prepararDiretorioOutput(DBType dbType) throws IOException {
        Path pastaOutput = Paths.get(OUTPUT_DIR, dbType.getNome());

        if (Files.exists(pastaOutput)) {
            log.debug("Cleaning existing directory: {}", pastaOutput);
            deletarDiretorioRecursivamente(pastaOutput.toFile());
        }

        Files.createDirectories(pastaOutput);
        log.debug("Directory created: {}", pastaOutput);

        return pastaOutput;
    }

    /**
     * Builds Hibernate metadata by scanning entities.
     */
    private static Metadata construirMetadata(StandardServiceRegistry registry) {
        MetadataSources sources = new MetadataSources(registry);
        Reflections reflections = new Reflections(BASE_PACKAGE);

        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
        Set<Class<?>> converters = reflections.getTypesAnnotatedWith(Converter.class);

        log.debug("Found {} entities and {} converters", entities.size(), converters.size());

        entities.forEach(sources::addAnnotatedClass);
        converters.forEach(sources::addAnnotatedClass);

        return sources.buildMetadata();
    }

    /**
     * Generates the complete SQL file before splitting.
     */
    private static Path gerarSchemaCompleto(DBType dbType, Path pastaOutput, Metadata metadata) {
        Path arquivoTemp = pastaOutput.resolve(TEMP_SCHEMA_FILE);

        SchemaExport export = new SchemaExport();
        export.setFormat(true);
        export.setDelimiter(";");
        export.setOutputFile(arquivoTemp.toString());
        export.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.CREATE, metadata);

        log.debug("Full schema generated in: {}", arquivoTemp);
        return arquivoTemp;
    }

    // ================= SCHEMA SPLIT =================

    /**
     * Splits the complete schema into individual files per table.
     */
    private static void dividirSchemaEmArquivos(Path arquivoSchema, Path pastaOutput, Metadata metadata) throws IOException {

        String sqlCompleto = Files.readString(arquivoSchema);
        List<String> statements = parseStatements(sqlCompleto);

        Map<String, EntityInfo> metadataInfo = extrairInformacoesEntidades(metadata);
        Map<String, List<String>> statementsPorTabela = agruparStatementsPorTabela(statements, metadataInfo);

        // NEW LOGIC: Analyze dependencies and sort
        Map<String, Set<String>> dependencias = analisarDependencias(statementsPorTabela);
        List<String> ordemTabelas = ordenarPorDependencias(dependencias);

        log.info("Table creation order (respecting dependencies):");
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

    // ================= DEPENDENCY ANALYSIS =================

    /**
     * Analisa as dependências entre tabelas baseado em foreign keys (REFERENCES).
     *
     * @param statementsPorTabela statements grouped by table
     * @return map where the key is the table and the value are the tables from which it depends
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

                    // Only add if not a self-reference
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
     * Sorts tables using topological sorting algorithm (Kahn's Algorithm).
     * Garante que tabelas referenced are created before the tables that reference them.
     *
     * @param dependencias map of dependencies between tables
     * @return list of tables in the correct creation order
     * @throws IllegalStateException if there is a circular dependency
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

        // Processes a fila
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
            Set<String> tabelasNaoProcessesdas = new HashSet<>(dependencias.keySet());
            tabelasNaoProcessesdas.removeAll(ordemFinal);

            log.warn("⚠️  ATTENTION: Circular dependency detected!");
            log.warn("Tables with circular dependency: {}", tabelasNaoProcessesdas);
            log.warn("These tables will be added at the end of the order:");

            // Adiciona as tabelas restantes ao final (melhor que falhar completamente)
            ordemFinal.addAll(tabelasNaoProcessesdas);
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

    // ================= FILE WRITING =================

    /**
     * Writes the migration file for a specific table.
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
     * Determines the destination folder based on the feature (if feature organization is active).
     */
    private static Path determinarPastaDestino(Path pastaBase, EntityInfo entityInfo) {
        if (ORGANIZAR_POR_FEATURE && entityInfo.feature() != null) {
            return pastaBase.resolve(entityInfo.feature());
        }
        return pastaBase;
    }

    /**
     * Builds the migration file name following the Flyway pattern.
     */
    private static String construirNomeArquivo(String nomeTabela, int versao) {
        return MIGRATION_PREFIX + versao + TABLE_PREFIX + nomeTabela.toUpperCase() + SQL_EXTENSION;
    }

    /**
     * Writes the complete content of the migration file.
     */
    private static void escreverConteudoArquivo(Path arquivo, String nomeTabela, EntityInfo entityInfo, List<String> statements) throws IOException {

        StringBuilder conteudo = new StringBuilder();

        // Header with metadata
        conteudo.append("-- Table: ").append(nomeTabela).append("\n");
        conteudo.append("-- Entity: ").append(entityInfo.nomeClasse()).append("\n");

        if (entityInfo.feature() != null) {
            conteudo.append("-- Feature: ").append(entityInfo.feature()).append("\n");
        }

        conteudo.append("\n");

        // SQL statements
        for (String statement : statements) {
            conteudo.append(statement).append("\n\n");
        }

        Files.writeString(arquivo, conteudo.toString());
    }

    // ================= TABLE EXTRACTION =================

    /**
     * Extracts all table names referenced in an SQL statement.
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
     * Extracts tables using a specific regex pattern.
     */
    private static void extrairTabelasComPattern(Set<String> tabelas, Pattern pattern, String statement, int grupoCaptura) {

        Matcher matcher = pattern.matcher(statement);
        while (matcher.find()) {
            String nomeTabela = normalizarNomeTabela(matcher.group(grupoCaptura));
            tabelas.add(nomeTabela);
        }
    }

    /**
     * Extracts tables related to sequences (removing _seq suffix).
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
     * Extracts tables from REFERENCES clauses (foreign keys).
     */
    private static void extrairTabelasDeReferences(Set<String> tabelas, String statement) {
        Matcher matcher = REFERENCES.matcher(statement);

        while (matcher.find()) {
            String nomeTabela = normalizarNomeTabela(matcher.group(1));
            tabelas.add(nomeTabela);
        }
    }

    /**
     * Normalizes table name by removing delimiters and converting to lowercase.
     */
    private static String normalizarNomeTabela(String nome) {
        return nome.replace("`", "").replace("\"", "").replace("[", "").replace("]", "").toLowerCase().trim();
    }

    // ================= ENTITY METADATA =================

    /**
     * Extracts entity information from Hibernate metadata.
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

        log.debug("Extracted information from {} entities", mapa.size());
        return mapa;
    }

    /**
     * Extracts the simple class name (without the full package).
     */
    private static String extrairNomeSimpllesClasse(String nomeCompletoClasse) {
        int ultimoPonto = nomeCompletoClasse.lastIndexOf('.');
        return ultimoPonto >= 0 ? nomeCompletoClasse.substring(ultimoPonto + 1) : nomeCompletoClasse;
    }

    /**
     * Extracts the feature name from the complete class name.
     * <p>
     * Searches for '.features.' in the package and returns the next segment.
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

    // ================= UTILITIES =================

    /**
     * Deletes a directory and all its content recursively.
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
     * Record that stores information about a JPA entity.
     *
     * @param nomeTabela table name in the database
     * @param nomeClasse simple name of the entity class
     * @param feature    feature/module name (can be null)
     */
    record EntityInfo(String nomeTabela, String nomeClasse, String feature) {
    }
}
