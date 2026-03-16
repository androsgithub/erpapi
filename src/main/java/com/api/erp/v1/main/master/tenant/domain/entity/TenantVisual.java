package com.api.erp.v1.main.master.tenant.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Identidade Visual do Tenant
 * <p>
 * Armazena tema, cores, assets, tipografia e configurações de aparência.
 */
@Entity
@Table(name = "tb_tnt_visual", uniqueConstraints = {@UniqueConstraint(columnNames = "tenant_id")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantVisual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false, unique = true)
    private Tenant tenant;

    // ===== Tema e Layout =====
    @Column(name = "theme", length = 50)
    @Builder.Default
    private String theme = "light";

    @Column(name = "border_radius", length = 20)
    @Builder.Default
    private String borderRadius = "4px";

    @Column(name = "sidebar_collapsed", nullable = false)
    @Builder.Default
    private Boolean sidebarCollapsed = false;

    // ===== Cores =====
    @Column(name = "color_primary", length = 7)
    private String colorPrimary;

    @Column(name = "color_secondary", length = 7)
    private String colorSecondary;

    @Column(name = "color_accent", length = 7)
    private String colorAccent;

    @Column(name = "color_background", length = 7)
    private String colorBackground;

    @Column(name = "color_text", length = 7)
    private String colorText;

    @Column(name = "color_danger", length = 7)
    private String colorDanger;

    @Column(name = "color_success", length = 7)
    private String colorSuccess;

    @Column(name = "color_warning", length = 7)
    private String colorWarning;

    // ===== Assets (URLs) =====
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "logo_large_url", length = 500)
    private String logoLargeUrl;

    @Column(name = "favicon_url", length = 500)
    private String faviconUrl;

    @Column(name = "email_logo_url", length = 500)
    private String emailLogoUrl;

    @Column(name = "background_image_url", length = 500)
    private String backgroundImageUrl;

    // ===== Tipografia =====
    @Column(name = "font_family", length = 100)
    private String fontFamily;

    @Column(name = "font_cdn_url", length = 500)
    private String fontCdnUrl;

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
