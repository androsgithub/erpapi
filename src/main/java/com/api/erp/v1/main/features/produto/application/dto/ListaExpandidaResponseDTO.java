package com.api.erp.v1.main.features.produto.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para resposta da lista expandida de produção completa
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaExpandidaResponseDTO {
    
    private Long produtoId;
    private String codigoProduto;
    private String descricaoProduto;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal quantidadeRequerida;
    
    private String unidadeMedidaProduto;
    private List<ItemListaExpandidaDTO> itens;
    private Integer totalItens;
}
