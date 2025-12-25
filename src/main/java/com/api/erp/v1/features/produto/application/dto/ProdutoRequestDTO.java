package com.api.erp.v1.features.produto.application.dto;

import com.api.erp.v1.features.produto.domain.entity.StatusProduto;
import com.api.erp.v1.features.produto.domain.entity.TipoProduto;
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
    
    /**
     * Classificação fiscal completa do produto
     * Inclui NCM, origem, CEST, benefício fiscal e unidade tributável
     */
    private ClassificacaoFiscalDTO classificacaoFiscal;
    
    /**
     * @deprecated Use classificacaoFiscal.ncm em seu lugar
     */
    @Deprecated(since = "1.0", forRemoval = true)
    private String ncm;
    
    /**
     * @deprecated Não utilizado. Use classificacaoFiscal em seu lugar
     */
    @Deprecated(since = "1.0", forRemoval = true)
    private String informacoesFiscais;
    
    private BigDecimal precoVenda;
    private BigDecimal precoCusto;
}
