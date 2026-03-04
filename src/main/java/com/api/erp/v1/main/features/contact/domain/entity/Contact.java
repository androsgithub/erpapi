package com.api.erp.v1.main.features.contact.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

/**
 * Entidade que representa um contact de uma entidade (Usuário, Empresa, BusinessPartner, etc.)
 * <p>
 * Seguindo o padrão DDD, esta entidade é responsável por gerenciar as informações
 * de contact de uma entidade do sistema.
 */
@Entity
@Table(name = "tb_contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE tb_contacts SET deleted = true, deleted_at = now() WHERE id = ?")
public class Contact extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoContact tipo;

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

    /**
     * Construtor simplificado para criar um contact básico
     */
    public Contact(TipoContact tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.principal = false;
        this.ativo = true;
    }


}
