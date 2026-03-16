package com.api.erp.v1.main.dynamic.features.product.application.dto;

import com.api.erp.v1.main.dynamic.features.product.domain.entity.ProductStatus;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.ProductType;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO para requisição de criação/atualização de Product
 * <p>
 * Incluindo suporte completo a Classificação Fiscal
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    private String codigo;
    private String descricao;
    private String descricaoDetalhada;
    private ProductStatus status;
    private ProductType tipo;
    private Long measureUnitId;
    private ClassificacaoFiscalDTO classificacaoFiscal;
    private BigDecimal precoVenda;
    private BigDecimal precoCusto;
    private Map<String, Object> customData;
}
