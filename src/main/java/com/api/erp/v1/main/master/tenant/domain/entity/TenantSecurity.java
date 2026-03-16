package com.api.erp.v1.main.master.tenant.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Política de Segurança do Tenant
 * <p>
 * Armazena configurações de segurança: requisitos de senha e política de lockout.
 */
@Entity
@Table(name = "tb_tnt_security", uniqueConstraints = {@UniqueConstraint(columnNames = "tenant_id")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSecurity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false, unique = true)
    private Tenant tenant;

    // ===== Política de Senha =====
    @Column(name = "min_password_length")
    @Builder.Default
    private Integer minPasswordLength = 8;

    @Column(name = "require_uppercase", nullable = false)
    @Builder.Default
    private Boolean requireUppercase = true;

    @Column(name = "require_number", nullable = false)
    @Builder.Default
    private Boolean requireNumber = true;

    @Column(name = "require_special", nullable = false)
    @Builder.Default
    private Boolean requireSpecial = false;

    @Column(name = "password_expiration_days")
    @Builder.Default
    private Integer passwordExpirationDays = 90;

    // ===== Política de Lockout =====
    @Column(name = "max_login_attempts")
    @Builder.Default
    private Integer maxLoginAttempts = 5;

    @Column(name = "lockout_duration_min")
    @Builder.Default
    private Integer lockoutDurationMin = 15;

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
