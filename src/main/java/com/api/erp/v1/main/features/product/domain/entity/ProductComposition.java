package com.api.erp.v1.main.features.product.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa uma Composição de Product (Bill of Materials - BOM).
 * <p>
 * Responsabilidades:
 * - Representar os insumos necessários para fabricar um product
 * - Validar quantidade e unidade de medida compatível
 * - Manter integridade de composições
 * <p>
 * Restrições:
 * - Apenas products fabricáveis podem ter composição
 * - Quantidade deve ser sempre positiva
 * - Unidade de medida deve ser compatível com o product componente
 * <p>
 * SRP: Representar a relação entre products na BOM
 */
@Entity
@Table(name = "tb_product_composition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductComposition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Product que está sendo fabricado (product pai)
     * Deve ser do tipo FABRICAVEL
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_fabricado_id", nullable = false)
    private Product productFabricado;

    /**
     * Product que é um componente/insumo (product componente)
     * Pode ser do tipo COMPRADO ou FABRICAVEL
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_componente_id", nullable = false)
    private Product productComponente;

    /**
     * Quantidade necessária do componente para fabricar uma unidade do product pai
     * Deve ser sempre > 0
     */
    @Column(nullable = false, columnDefinition = "DECIMAL(10,4)")
    private BigDecimal quantidadeNecessaria;

    /**
     * Sequência de ordenação da BOM
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer sequencia = 0;

    /**
     * Observações adicionais sobre o componente
     */
    @Column(columnDefinition = "VARCHAR(500)")
    private String observacoes;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    /**
     * Verifica se a quantidade é válida
     */
    public boolean ehQuantidadeValida() {
        return quantidadeNecessaria != null && quantidadeNecessaria.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Atualiza a quantidade necessária
     */
    public void atualizarQuantidade(BigDecimal novaQuantidade) {
        if (novaQuantidade == null || novaQuantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.quantidadeNecessaria = novaQuantidade;
        atualizarDataAtualizacao();
    }

    /**
     * Atualiza data de modificação
     */
    public void atualizarDataAtualizacao() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
