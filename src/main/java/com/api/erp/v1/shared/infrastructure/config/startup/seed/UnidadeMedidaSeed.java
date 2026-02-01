package com.api.erp.v1.shared.infrastructure.config.startup.seed;

import com.api.erp.v1.features.unidademedida.domain.entity.UnidadeMedida;
import com.api.erp.v1.features.unidademedida.domain.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnidadeMedidaSeed {

    private static final Logger logger = LoggerFactory.getLogger(UnidadeMedidaSeed.class);
    private final UnidadeMedidaRepository repository;

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
        logger.info("Inicializando Unidades de Medida padrão...");

        for (UnidadePadrao unidade : UNIDADES_PADRAO) {
            repository.findBySigla(unidade.sigla())
                    .orElseGet(() -> {
                        logger.info("Criando unidade: {}", unidade.sigla());

                        return repository.save(
                                UnidadeMedida.builder()
                                        .sigla(unidade.sigla())
                                        .descricao(unidade.descricao())
                                        .build()
                        );
                    });
        }
    }

    /* ===================== RECORD AUXILIAR ===================== */

    private record UnidadePadrao(String sigla, String descricao) {}
}
