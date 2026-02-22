package com.api.erp.v1.main.config.startup.seed;

import com.api.erp.v1.main.features.measureunit.domain.entity.MeasureUnit;
import com.api.erp.v1.main.features.measureunit.domain.repository.MeasureUnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * COMPONENT - Seeder de Unidades de Medida Padrão
 * 
 * Inicializa as unidades de medida padrão do sistema.
 * Cada unidade é criada apenas uma vez (não cria duplicatas).
 * 
 * Responsabilidades:
 * - Carregar unidades padrão
 * - Verificar se já existem
 * - Criar em lote ou individual
 * - Logar progresso
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MeasureUnitSeed {

    private final MeasureUnitRepository repository;

    private static final List<UnidadePadrao> UNIDADES_PADRAO = List.of(
            new UnidadePadrao("AMPOLA","AMPOLA"),
            new UnidadePadrao("BALDE","BALDE"),
            new UnidadePadrao("BANDEJ","BANDEJA"),
            new UnidadePadrao("BARRA","BARRA"),
            new UnidadePadrao("BISNAG","BISNAGA"),
            new UnidadePadrao("BLOCO","BLOCO"),
            new UnidadePadrao("BOBINA","BOBINA"),
            new UnidadePadrao("BOMB","BOMBONA"),
            new UnidadePadrao("CAPS","CAPSULA"),
            new UnidadePadrao("CART","CARTELA"),
            new UnidadePadrao("CENTO","CENTO"),
            new UnidadePadrao("CJ","CONJUNTO"),
            new UnidadePadrao("CM","CENTIMETRO"),
            new UnidadePadrao("CM2","CENTIMETRO QUADRADO"),
            new UnidadePadrao("CX","CAIXA"),
            new UnidadePadrao("CX2","CAIXA COM 2 UNIDADES"),
            new UnidadePadrao("CX3","CAIXA COM 3 UNIDADES"),
            new UnidadePadrao("CX5","CAIXA COM 5 UNIDADES"),
            new UnidadePadrao("CX10","CAIXA COM 10 UNIDADES"),
            new UnidadePadrao("CX15","CAIXA COM 15 UNIDADES"),
            new UnidadePadrao("CX20","CAIXA COM 20 UNIDADES"),
            new UnidadePadrao("CX25","CAIXA COM 25 UNIDADES"),
            new UnidadePadrao("CX50","CAIXA COM 50 UNIDADES"),
            new UnidadePadrao("CX100","CAIXA COM 100 UNIDADES"),
            new UnidadePadrao("DISP","DISPLAY"),
            new UnidadePadrao("DUZIA","DUZIA"),
            new UnidadePadrao("EMBAL","EMBALAGEM"),
            new UnidadePadrao("FARDO","FARDO"),
            new UnidadePadrao("FOLHA","FOLHA"),
            new UnidadePadrao("FRASCO","FRASCO"),
            new UnidadePadrao("GALAO","GALÃO"),
            new UnidadePadrao("GF","GARRAFA"),
            new UnidadePadrao("GRAMAS","GRAMAS"),
            new UnidadePadrao("JOGO","JOGO"),
            new UnidadePadrao("KG","QUILOGRAMA"),
            new UnidadePadrao("KIT","KIT"),
            new UnidadePadrao("LATA","LATA"),
            new UnidadePadrao("LITRO","LITRO"),
            new UnidadePadrao("M","METRO"),
            new UnidadePadrao("M2","METRO QUADRADO"),
            new UnidadePadrao("M3","METRO CÚBICO"),
            new UnidadePadrao("MILHEI","MILHEIRO"),
            new UnidadePadrao("ML","MILILITRO"),
            new UnidadePadrao("MWH","MEGAWATT HORA"),
            new UnidadePadrao("PACOTE","PACOTE"),
            new UnidadePadrao("PALETE","PALETE"),
            new UnidadePadrao("PARES","PARES"),
            new UnidadePadrao("PC","PEÇA"),
            new UnidadePadrao("POTE","POTE"),
            new UnidadePadrao("K","QUILATE"),
            new UnidadePadrao("RESMA","RESMA"),
            new UnidadePadrao("ROLO","ROLO"),
            new UnidadePadrao("SACO","SACO"),
            new UnidadePadrao("SACOLA","SACOLA"),
            new UnidadePadrao("TAMBOR","TAMBOR"),
            new UnidadePadrao("TANQUE","TANQUE"),
            new UnidadePadrao("TON","TONELADA"),
            new UnidadePadrao("TUBO","TUBO"),
            new UnidadePadrao("UNID","UNIDADE"),
            new UnidadePadrao("VASIL","VASILHAME"),
            new UnidadePadrao("VIDRO","VIDRO")
    );

    public void executar() {
        try {
            if (repository.count() > 0) {
                log.info("⏭️  Unidades de medida já inicializadas, pulando seed.");
                return;
            }

            log.info("📏 Inicializando unidades de medida padrão...");

            // 1. Busca todas as siglas existentes de uma só vez (1 query)
            Set<String> siglasExistentes = repository.findAll()
                    .stream()
                    .map(MeasureUnit::getSigla)
                    .collect(Collectors.toSet());

            // 2. Filtra apenas as que não existem
            List<MeasureUnit> paracriar = UNIDADES_PADRAO.stream()
                    .filter(u -> !siglasExistentes.contains(u.sigla()))
                    .map(u -> MeasureUnit.builder()
                            .sigla(u.sigla())
                            .descricao(u.descricao())
                            .build())
                    .toList();

            if (paracriar.isEmpty()) {
                log.info("⏭️  Todas as unidades já existem, nada a criar.");
                return;
            }

            // 3. Salva tudo em lote (1 query ou poucos batches)
            repository.saveAll(paracriar);

            log.info("✅ {} unidades criadas, {} já existiam.",
                    paracriar.size(),
                    siglasExistentes.size());

        } catch (Exception e) {
            log.error("❌ Erro ao inicializar unidades de medida:", e);
            throw new RuntimeException("Falha ao inicializar unidades de medida", e);
        }
    }

    /* ===================== RECORD AUXILIAR ===================== */

    private record UnidadePadrao(String sigla, String descricao) {}
}
