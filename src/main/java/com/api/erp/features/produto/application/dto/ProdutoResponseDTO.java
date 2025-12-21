package com.api.erp.features.produto.application.dto;

import com.api.erp.features.produto.domain.entity.StatusProduto;
import com.api.erp.features.produto.domain.entity.TipoProduto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de Produto
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
    private String ncm;
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
