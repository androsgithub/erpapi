package com.api.erp.v1.features.contato.domain.entity;

import com.api.erp.v1.shared.domain.entity.BaseEntity;
import com.api.erp.v1.shared.domain.valueobject.CustomData;
import com.api.erp.v1.shared.infrastructure.persistence.converters.CustomDataAttributeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entidade que representa um contato de uma entidade (Usuário, Empresa, Cliente, etc.)
 * 
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

    @OneToMany(
            mappedBy = "contato",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private Set<UsuarioContato> usuariosContatos;

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
