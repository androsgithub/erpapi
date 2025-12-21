package com.api.erp.features.produto.application.dto;

import com.api.erp.features.produto.domain.entity.StatusProduto;
import com.api.erp.features.produto.domain.entity.TipoProduto;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para requisição de criação/atualização de Produto
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
    private String ncm;
    private String informacoesFiscais;
    private BigDecimal precoVenda;
    private BigDecimal precoCusto;
}
