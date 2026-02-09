package com.api.erp.v1.features.produto.application.dto;

import com.api.erp.v1.features.produto.domain.entity.StatusProduto;
import com.api.erp.v1.features.produto.domain.entity.TipoProduto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO para requisição de criação/atualização de Produto
 * <p>
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
    private Map<String, Object> customData;
}
