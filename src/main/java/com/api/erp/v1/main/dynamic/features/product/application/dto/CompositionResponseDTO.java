package com.api.erp.v1.main.dynamic.features.product.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de composição de product
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompositionResponseDTO {
    
    private Long id;
    private ProductSimplificadoDTO productFabricado;
    private ProductSimplificadoDTO productComponente;
    private BigDecimal quantidadeNecessaria;
    private Integer sequencia;
    private String observacoes;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAtualizacao;
    
    /**
     * Product simplificado para evitar dados redundantes
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductSimplificadoDTO {
        private Long id;
        private String codigo;
        private String descricao;
    }
}
