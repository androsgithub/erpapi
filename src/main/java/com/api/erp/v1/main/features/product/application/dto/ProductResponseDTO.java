package com.api.erp.v1.main.features.product.application.dto;

import com.api.erp.v1.main.features.product.domain.entity.ProductStatus;
import com.api.erp.v1.main.features.product.domain.entity.ProductType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de Product
 * 
 * Expõe todos os dados do product incluindo sua classificação fiscal completa
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    
    private Long id;
    private String codigo;
    private String descricao;
    private String descricaoDetalhada;
    private ProductStatus status;
    private ProductType tipo;
    private MeasureUnitSimplificadaDTO measureUnit;
    
    /**
     * Classificação fiscal completa do product
     * Inclui NCM, origem, CEST, benefício fiscal e unidade tributável
     */
    private ClassificacaoFiscalDTO classificacaoFiscal;
    
    /**
     * @deprecated Use classificacaoFiscal.ncm em seu lugar
     */
    @Deprecated(since = "1.0", forRemoval = true)
    private String ncm;
    
    /**
     * @deprecated Não utilizado. Use classificacaoFiscal em seu lugar
     */
    @Deprecated(since = "1.0", forRemoval = true)
    private String informacoesFiscais;
    
    private BigDecimal precoVenda;
    private BigDecimal precoCusto;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAtualizacao;
    
    /**
     * DTO simplificado para MeasureUnit dentro da resposta de Product
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MeasureUnitSimplificadaDTO {
        private Long id;
        private String sigla;
        private String descricao;
    }
}
