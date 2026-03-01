package com.api.erp.v1.main.features.user.domain.entity;

import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.features.permission.domain.entity.Permission;
import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import com.api.erp.v1.main.shared.domain.valueobject.CPF;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
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
@Table(name = "tb_user")
public class User extends BaseEntity {

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
    private StatusUser status;

    @Column(name = "aprovado_por")
    private Long aprovado_por;

    @Column
    private LocalDateTime approved_at;

    @OneToMany
    @JoinTable(
            name="tb_user_permission",
            joinColumns = @JoinColumn( name="user_id"),
            inverseJoinColumns = @JoinColumn( name="permission_id")
    )
    private Set<Permission> permissions;

    @OneToMany
    @JoinTable(
            name="tb_user_role",
            joinColumns = @JoinColumn( name="user_id"),
            inverseJoinColumns = @JoinColumn( name="role_id")
    )
    private Set<Role> roles;

    @OneToMany
    @JoinTable(
            name="tb_user_contact",
            joinColumns = @JoinColumn( name="user_id"),
            inverseJoinColumns = @JoinColumn( name="contact_id")
    )
    private Set<Contact> contacts;

    // Builder Pattern
    public static class Builder {
        private User user = new User();

        public Builder id(Long id) {
            user.setId(id);
            return this;
        }

        public Builder tenantId(Long tenantId) {
            user.setTenantId(tenantId);
            return this;
        }

        public Builder nomeCompleto(String nome) {
            user.nome_completo = nome;
            return this;
        }

        public Builder email(Email email) {
            user.email = email;
            return this;
        }

        public Builder cpf(CPF cpf) {
            user.cpf = cpf;
            return this;
        }

        public Builder senhaHash(String senhaHash) {
            user.senha_hash = senhaHash;
            return this;
        }

        public Builder status(StatusUser status) {
            user.status = status;
            return this;
        }

        public User build() {
            user.status = user.status != null ? user.status : StatusUser.ATIVO;
            return user;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Métodos de negócio
    public void aprovar(Long gestorId) {
        if (this.status != StatusUser.PENDENTE_APROVACAO) {
            throw new IllegalStateException("User is not pending approval");
        }
        this.status = StatusUser.ATIVO;
        this.aprovado_por = gestorId;
    }

    public void rejeitar() {
        if (this.status != StatusUser.PENDENTE_APROVACAO) {
            throw new IllegalStateException("User is not pending approval");
        }
        this.status = StatusUser.REJEITADO;
    }

    public void inativar() {
        this.status = StatusUser.INATIVO;
    }

    public boolean isPendente() {
        return this.status == StatusUser.PENDENTE_APROVACAO;
    }
}
