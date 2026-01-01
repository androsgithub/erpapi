package com.api.erp.v1.features.contato.domain.entity;

import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa a associação entre um Usuário e seus Contatos
 * 
 * Tabela de junção que conecta usuários a contatos já existentes.
 * Permite que um usuário possua múltiplos contatos de diferentes tipos
 * (telefone, email, WhatsApp, etc.)
 * 
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
@Table(name = "usuario_contato", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "contato_id"})
})
public class UsuarioContato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contato_id", nullable = false)
    private Contato contato;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataAtualizacao = LocalDateTime.now();
}
