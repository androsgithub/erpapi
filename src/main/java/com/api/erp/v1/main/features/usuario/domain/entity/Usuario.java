package com.api.erp.v1.main.features.usuario.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import com.api.erp.v1.main.shared.domain.valueobject.CPF;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_usuario")
public class Usuario extends BaseEntity {

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
    private LocalDateTime approved_at;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioPermissao> permissoes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioRole> roles;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioContato> contatos;

    // Builder Pattern
    public static class Builder {
        private Usuario usuario = new Usuario();

        public Builder id(Long id) {
            usuario.setId(id);
            return this;
        }

        public Builder tenantId(Long tenantId) {
            usuario.setTenantId(tenantId);
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
    }

    public void rejeitar() {
        if (this.status != StatusUsuario.PENDENTE_APROVACAO) {
            throw new IllegalStateException("Usuário não está pendente de aprovação");
        }
        this.status = StatusUsuario.REJEITADO;
    }

    public void inativar() {
        this.status = StatusUsuario.INATIVO;
    }

    public boolean isPendente() {
        return this.status == StatusUsuario.PENDENTE_APROVACAO;
    }
}
