package com.api.erp.v1.main.dynamic.features.product.application.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para requisição de composição de product
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompositionRequestDTO {
    
    private Long productFabricadoId;
    private Long productComponenteId;
    private BigDecimal quantidadeNecessaria;
    private Integer sequencia;
    private String observacoes;
}
