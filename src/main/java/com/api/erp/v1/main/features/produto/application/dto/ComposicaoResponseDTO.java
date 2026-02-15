package com.api.erp.v1.main.features.produto.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de composição de produto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComposicaoResponseDTO {
    
    private Long id;
    private ProdutoSimplificadoDTO produtoFabricado;
    private ProdutoSimplificadoDTO produtoComponente;
    private BigDecimal quantidadeNecessaria;
    private Integer sequencia;
    private String observacoes;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAtualizacao;
    
    /**
     * Produto simplificado para evitar dados redundantes
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProdutoSimplificadoDTO {
        private Long id;
        private String codigo;
        private String descricao;
    }
}
