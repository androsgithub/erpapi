package com.api.erp.v1.main.master.tenant.domain.entity;

import com.api.erp.v1.main.shared.domain.valueobject.Email;
import com.api.erp.v1.main.shared.infrastructure.persistence.converters.EmailConverter;
import com.api.erp.v1.main.shared.infrastructure.persistence.converters.TelefoneConverter;
import com.api.erp.v1.main.shared.domain.valueobject.Telefone;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade de Tenant Central
 * <p>
 * Representa um tenant (empresa) no sistema. Cada tenant tem múltiplos relacionamentos
 * com suas configurações, datasource, dados fiscais, segurança, etc.
 * <p>
 * O tenant principal (master) também pode ser um usuário do sistema (trial, active, etc).
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_tnt")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Convert(converter = EmailConverter.class)
    @Column(name = "email", nullable = false, length = 150)
    private Email email;

    @Convert(converter = TelefoneConverter.class)
    @Column(name = "phone", length = 20)
    private Telefone phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnore
    private TenantPlan plan;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "trial", nullable = false)
    @Builder.Default
    private Boolean trial = false;

    @Column(name = "trial_expires_at")
    private LocalDateTime trialExpiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== Relacionamentos com as tabelas de configuração =====

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private TenantConfig config;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private TenantVisual visual;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private TenantDatasource datasource;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private TenantFiscal fiscal;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private TenantSecurity security;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<TenantAllowDomain> allowedDomains = List.of();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<TenantFeature> features = List.of();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

