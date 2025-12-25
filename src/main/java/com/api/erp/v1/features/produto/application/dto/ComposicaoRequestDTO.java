package com.api.erp.v1.features.produto.application.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para requisição de composição de produto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComposicaoRequestDTO {
    
    private Long produtoFabricadoId;
    private Long produtoComponenteId;
    private BigDecimal quantidadeNecessaria;
    private Integer sequencia;
    private String observacoes;
}
