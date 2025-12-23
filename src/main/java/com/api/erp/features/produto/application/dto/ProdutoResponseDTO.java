package com.api.erp.features.produto.application.dto;

import com.api.erp.features.produto.domain.entity.StatusProduto;
import com.api.erp.features.produto.domain.entity.TipoProduto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de Produto
 * 
 * Expõe todos os dados do produto incluindo sua classificação fiscal completa
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoResponseDTO {
    
    private Long id;
    private String codigo;
    private String descricao;
    private String descricaoDetalhada;
    private StatusProduto status;
    private TipoProduto tipo;
    private UnidadeMedidaSimplificadaDTO unidadeMedida;
    
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
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAtualizacao;
    
    /**
     * DTO simplificado para UnidadeMedida dentro da resposta de Produto
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UnidadeMedidaSimplificadaDTO {
        private Long id;
        private String sigla;
        private String descricao;
    }
}
