package com.api.erp.v1.features.produto.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para resposta da lista expandida de produção
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemListaExpandidaDTO {
    
    private Long produtoId;
    private String codigoProduto;
    private String descricaoProduto;
    private String tipoProduto;
    private String unidadeMedida;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal quantidadeNecessaria;
    
    private String observacoes;
}
