package com.api.erp.v1.main.master.tenant.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domínios Aceitos pelo Tenant
 * <p>
 * Armazena domínios permitidos para auto-registro de e-mail, CORS e SSO.
 */
@Entity
@Table(name = "tb_tnt_allow_domains",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"tenant_id", "domain"})},
       indexes = {@Index(name = "idx_tenant_allow_domains_tenant_id", columnList = "tenant_id")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantAllowDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "domain", nullable = false, length = 255)
    private String domain;

    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DomainType type;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Enum dos tipos de domínio
     */
    public enum DomainType {
        EMAIL,   // Auto-registro de e-mail
        CORS,    // CORS (Cross-Origin)
        SSO      // Single Sign-On
    }
}
