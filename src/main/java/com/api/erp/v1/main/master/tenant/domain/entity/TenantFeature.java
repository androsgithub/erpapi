package com.api.erp.v1.main.master.tenant.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Override de Beans Spring por Tenant
 * <p>
 * Central para resolução de beans por tenant. Cada linha define:
 * "Para este tenant, quando pedir a feature 'featureKey', injete o bean 'beanName'".
 * <p>
 * Se não existir linha ativa para (tenant_id, feature_key), o sistema usa o bean @Primary padrão.
 */
@Entity
@Table(name = "tb_tnt_features",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"tenant_id", "feature_key"})},
        indexes = {
                @Index(name = "idx_tenant_features_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_tenant_features_active", columnList = "active")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "feature_key", nullable = false, length = 100)
    private String featureKey;

    @Column(name = "bean_name", nullable = false, length = 200)
    private String beanName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
