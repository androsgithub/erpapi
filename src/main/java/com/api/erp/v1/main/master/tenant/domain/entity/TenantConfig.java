package com.api.erp.v1.main.master.tenant.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Configurações gerais do Tenant
 * <p>
 * Armazena: slug, app_name, módulos, locale, flags de notificação, auth e features.
 */
@Entity
@Table(name = "tb_tnt_config", uniqueConstraints = {@UniqueConstraint(columnNames = "tenant_id")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false, unique = true)
    private Tenant tenant;

    @Column(name = "slug", length = 100, nullable = false)
    private String slug;

    @Column(name = "app_name", length = 150, nullable = false)
    private String appName;

    @Column(name = "app_description", length = 500)
    private String appDescription;

    @Column(name = "support_email", length = 150)
    private String supportEmail;

    // ===== Configurações funcionais =====
    @Column(name = "multi_branch", nullable = false)
    @Builder.Default
    private Boolean multiBranch = false;

    @Column(name = "allow_api_access", nullable = false)
    @Builder.Default
    private Boolean allowApiAccess = true;

    @Column(name = "white_label", nullable = false)
    @Builder.Default
    private Boolean whiteLabel = false;

    // ===== Status =====
    @Column(name = "maintenance_mode", nullable = false)
    @Builder.Default
    private Boolean maintenanceMode = false;

    @Column(name = "onboarding_done", nullable = false)
    @Builder.Default
    private Boolean onboardingDone = false;

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
