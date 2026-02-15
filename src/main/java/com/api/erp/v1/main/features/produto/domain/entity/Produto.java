package com.api.erp.v1.main.features.produto.domain.entity;

import com.api.erp.v1.main.features.customfield.domain.entity.CustomData;
import com.api.erp.v1.main.features.unidademedida.domain.entity.UnidadeMedida;
import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entidade de domínio que representa um Produto.
 * <p>
 * Responsabilidades:
 * - Armazenar dados do produto
 * - Validar estado do produto através de métodos de comportamento
 * - Representar as regras de negócio do domínio
 * <p>
 * Atributos principais:
 * - Descrição: nome/descrição do produto
 * - Status: ativo, inativo, bloqueado ou descontinuado
 * - Tipo: comprado ou fabricável
 * - Unidade de Medida: referência à unidade padrão
 * - Informações Fiscais: NCM e preparação para futuras regras
 * <p>
 * SRP: Representar e gerenciar o estado de um produto
 */
@Entity
@Table(name = "tb_produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE tb_produto SET deleted = true, deleted_at = now() WHERE id = ?")
public class Produto extends BaseEntity {

    @OneToMany
    @JoinTable(name = "TB_CUSTOM_DATA", joinColumns = @JoinColumn(name = "entity_id"), inverseJoinColumns = @JoinColumn(name = "custom_data_id"))
    @SQLRestriction("entity_type='TB_PRODUTO'")
    private List<CustomData> customData;

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

    public void ativar() {
        this.status = StatusProduto.ATIVO;
    }

    public void desativar() {
        this.status = StatusProduto.INATIVO;
    }

    public void bloquear() {
        this.status = StatusProduto.BLOQUEADO;
    }

    public void descontinuar() {
        this.status = StatusProduto.DESCONTINUADO;
    }

    public boolean podeSerUtilizado() {
        return this.status.podeSerUtilizado();
    }

    public boolean ehFabricavel() {
        return this.tipo == TipoProduto.FABRICAVEL;
    }

    public boolean ehComprado() {
        return this.tipo == TipoProduto.COMPRADO;
    }
}
