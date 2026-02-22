package com.api.erp.v1.main.features.product.application.dto;

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
    
    private Long productId;
    private String codigoProduct;
    private String descricaoProduct;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal quantidadeRequerida;
    
    private String measureUnitProduct;
    private List<ItemListaExpandidaDTO> itens;
    private Integer totalItens;
}
