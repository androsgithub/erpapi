package com.api.erp.v1.features.usuario.domain.entity;

import com.api.erp.v1.features.contato.domain.entity.UsuarioContato;
import com.api.erp.v1.features.permissao.domain.entity.UsuarioPermissao;
import com.api.erp.v1.shared.domain.valueobject.CPF;
import com.api.erp.v1.shared.domain.valueobject.Email;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenant_id;

    @Column(nullable = false)
    private String nome_completo;

    @Column(name = "email", nullable = false, unique = true)
    private Email email;

    @Column(name = "cpf", nullable = false, unique = true)
    private CPF cpf;

    @Column(nullable = false)
    private String senha_hash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUsuario status;

    @Column(name = "aprovado_por")
    private Long aprovado_por;

    @Column
    private LocalDateTime data_aprovacao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime data_criacao;

    @Column(nullable = false)
    private LocalDateTime data_atualizacao;

    @OneToMany(
            mappedBy = "usuario",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UsuarioPermissao> permissoes;

    @OneToMany(
            mappedBy = "usuario",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UsuarioContato> contatos;

    // Builder Pattern
    public static class Builder {
        private Usuario usuario = new Usuario();

        public Builder id(Long id) {
            usuario.id = id;
            return this;
        }

        public Builder tenantId(String tenantId) {
            usuario.tenant_id = tenantId;
            return this;
        }

        public Builder nomeCompleto(String nome) {
            usuario.nome_completo = nome;
            return this;
        }

        public Builder email(Email email) {
            usuario.email = email;
            return this;
        }

        public Builder cpf(CPF cpf) {
            usuario.cpf = cpf;
            return this;
        }

        public Builder senhaHash(String senhaHash) {
            usuario.senha_hash = senhaHash;
            return this;
        }

        public Builder status(StatusUsuario status) {
            usuario.status = status;
            return this;
        }

        public Usuario build() {
            usuario.status = usuario.status != null ? usuario.status : StatusUsuario.ATIVO;
            usuario.data_criacao = LocalDateTime.now();
            usuario.data_atualizacao = LocalDateTime.now();
            return usuario;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Métodos de negócio
    public void aprovar(Long gestorId) {
        if (this.status != StatusUsuario.PENDENTE_APROVACAO) {
            throw new IllegalStateException("Usuário não está pendente de aprovação");
        }
        this.status = StatusUsuario.ATIVO;
        this.aprovado_por = gestorId;
        this.data_aprovacao = LocalDateTime.now();
        this.data_atualizacao = LocalDateTime.now();
    }

    public void rejeitar() {
        if (this.status != StatusUsuario.PENDENTE_APROVACAO) {
            throw new IllegalStateException("Usuário não está pendente de aprovação");
        }
        this.status = StatusUsuario.REJEITADO;
        this.data_atualizacao = LocalDateTime.now();
    }

    public void inativar() {
        this.status = StatusUsuario.INATIVO;
        this.data_atualizacao = LocalDateTime.now();
    }

    public boolean isPendente() {
        return this.status == StatusUsuario.PENDENTE_APROVACAO;
    }
}
