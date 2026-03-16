package com.api.erp.v1.main.dynamic.features.product.application.dto;

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
    
    private Long productId;
    private String codigoProduct;
    private String descricaoProduct;
    private String tipoProduct;
    private String measureUnit;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal quantidadeNecessaria;
    
    private String observacoes;
}
