package com.api.erp.v1.features.usuario.domain.entity;

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
    private String nomeCompleto;

    @Column(name = "email", nullable = false, unique = true)
    private Email email;

    @Column(name = "cpf", nullable = false, unique = true)
    private CPF cpf;

    @Column(nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUsuario status;

    private Long aprovadoPor;
    private LocalDateTime dataAprovacao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @OneToMany(
            mappedBy = "usuario",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UsuarioPermissao> permissoes;

    // Builder Pattern
    public static class Builder {
        private Usuario usuario = new Usuario();

        public Builder id(Long id) {
            usuario.id = id;
            return this;
        }

        public Builder nomeCompleto(String nome) {
            usuario.nomeCompleto = nome;
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
            usuario.senhaHash = senhaHash;
            return this;
        }

        public Builder status(StatusUsuario status) {
            usuario.status = status;
            return this;
        }

        public Usuario build() {
            usuario.status = usuario.status != null ? usuario.status : StatusUsuario.ATIVO;
            usuario.dataCriacao = LocalDateTime.now();
            usuario.dataAtualizacao = LocalDateTime.now();
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
        this.aprovadoPor = gestorId;
        this.dataAprovacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void rejeitar() {
        if (this.status != StatusUsuario.PENDENTE_APROVACAO) {
            throw new IllegalStateException("Usuário não está pendente de aprovação");
        }
        this.status = StatusUsuario.REJEITADO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void inativar() {
        this.status = StatusUsuario.INATIVO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public boolean isPendente() {
        return this.status == StatusUsuario.PENDENTE_APROVACAO;
    }
}
