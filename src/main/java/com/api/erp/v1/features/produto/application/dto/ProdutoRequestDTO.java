package com.api.erp.v1.features.produto.application.dto;

import com.api.erp.v1.features.produto.domain.entity.StatusProduto;
import com.api.erp.v1.features.produto.domain.entity.TipoProduto;
import com.api.erp.v1.shared.domain.valueobject.CustomData;
import com.api.erp.v1.shared.infrastructure.persistence.converters.CustomDataAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para requisição de criação/atualização de Produto
 * 
 * Incluindo suporte completo a Classificação Fiscal
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoRequestDTO {
    
    private String codigo;
    private String descricao;
    private String descricaoDetalhada;
    private StatusProduto status;
    private TipoProduto tipo;
    private Long unidadeMedidaId;
    private ClassificacaoFiscalDTO classificacaoFiscal;
    private BigDecimal precoVenda;
    private BigDecimal precoCusto;
    private CustomData customData;
}
