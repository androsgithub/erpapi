package com.api.erp.v1.features.contato.domain.entity;

import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.shared.domain.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

/**
 * Entidade que representa a associação entre um Usuário e seus Contatos
 * <p>
 * Tabela de junção que conecta usuários a contatos já existentes.
 * Permite que um usuário possua múltiplos contatos de diferentes tipos
 * (telefone, email, WhatsApp, etc.)
 * <p>
 * Estrutura:
 * - usuario_id (FK para usuarios)
 * - contato_id (FK para contatos)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_usuario_contato", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "contato_id"})
})
@SQLDelete(sql = "UPDATE tb_usuario_contato SET deleted = true, deleted_at = now() WHERE id = ?")
public class UsuarioContato extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contato_id", nullable = false)
    private Contato contato;

}
