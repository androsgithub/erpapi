package com.api.erp.v1.features.produto.domain.entity;

import com.api.erp.v1.features.unidademedida.domain.entity.UnidadeMedida;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa um Produto.
 * 
 * Responsabilidades:
 * - Armazenar dados do produto
 * - Validar estado do produto através de métodos de comportamento
 * - Representar as regras de negócio do domínio
 * 
 * Atributos principais:
 * - Descrição: nome/descrição do produto
 * - Status: ativo, inativo, bloqueado ou descontinuado
 * - Tipo: comprado ou fabricável
 * - Unidade de Medida: referência à unidade padrão
 * - Informações Fiscais: NCM e preparação para futuras regras
 * 
 * SRP: Representar e gerenciar o estado de um produto
 */
@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;
    
    @Column(nullable = false, length = 255)
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProduto status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProduto tipo;
    
    /**
     * Referência à Unidade de Medida - evita valores soltos
     * Toda unidade deve ser uma entidade válida do sistema
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_medida_id", nullable = false)
    private UnidadeMedida unidadeMedida;


    @Embedded
    private ClassificacaoFiscal classificacaoFiscal;
    
    // Campos adicionais úteis
    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal precoVenda;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal precoCusto;
    
    @Column(columnDefinition = "TEXT")
    private String descricaoDetalhada;
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataAtualizacao = LocalDateTime.now();
    
    /**
     * Ativa o produto
     */
    public void ativar() {
        this.status = StatusProduto.ATIVO;
        atualizarDataAtualizacao();
    }
    
    /**
     * Desativa o produto
     */
    public void desativar() {
        this.status = StatusProduto.INATIVO;
        atualizarDataAtualizacao();
    }
    
    /**
     * Bloqueia o produto para uso
     */
    public void bloquear() {
        this.status = StatusProduto.BLOQUEADO;
        atualizarDataAtualizacao();
    }
    
    /**
     * Descontinua o produto
     */
    public void descontinuar() {
        this.status = StatusProduto.DESCONTINUADO;
        atualizarDataAtualizacao();
    }
    
    /**
     * Verifica se o produto pode ser utilizado
     */
    public boolean podeSerUtilizado() {
        return this.status.podeSerUtilizado();
    }
    
    /**
     * Verifica se é um produto fabricável
     */
    public boolean ehFabricavel() {
        return this.tipo == TipoProduto.FABRICAVEL;
    }
    
    /**
     * Verifica se é um produto comprado
     */
    public boolean ehComprado() {
        return this.tipo == TipoProduto.COMPRADO;
    }
    
    /**
     * Atualiza a data de atualização
     */
    public void atualizarDataAtualizacao() {
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
