package com.api.erp.v1.features.contato.domain.entity;

import com.api.erp.v1.features.customfield.domain.entity.CustomData;
import com.api.erp.v1.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

/**
 * Entidade que representa um contato de uma entidade (Usuário, Empresa, Cliente, etc.)
 * <p>
 * Seguindo o padrão DDD, esta entidade é responsável por gerenciar as informações
 * de contato de uma entidade do sistema.
 */
@Entity
@Table(name = "tb_contatos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE tb_contatos SET deleted = true, deleted_at = now() WHERE id = ?")
public class Contato extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoContato tipo;

    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "principal", nullable = false)
    @Builder.Default
    private boolean principal = false;

    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @OneToMany
    @JoinTable(name = "TB_CUSTOM_DATA", joinColumns = @JoinColumn(name = "entity_id"), inverseJoinColumns = @JoinColumn(name = "custom_data_id"))
    @SQLRestriction("entity_type='tb_contatos'")
    private List<CustomData> customData;

    /**
     * Construtor simplificado para criar um contato básico
     */
    public Contato(TipoContato tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.principal = false;
        this.ativo = true;
    }


}
