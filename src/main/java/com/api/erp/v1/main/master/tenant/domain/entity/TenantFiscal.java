package com.api.erp.v1.main.master.tenant.domain.entity;

import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.shared.infrastructure.persistence.converters.CNPJConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Dados Fiscais do Tenant
 * <p>
 * Armazena informações fiscais: CNPJ, razão social, inscrições estadual/municipal, regime tributário.
 */
@Entity
@Table(name = "tb_tnt_fiscal", uniqueConstraints = {@UniqueConstraint(columnNames = "tenant_id")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false, unique = true)
    private Tenant tenant;

    @Convert(converter = CNPJConverter.class)
    @Column(name = "cnpj", nullable = false, length = 14, unique = true)
    private CNPJ cnpj;

    @Column(name = "legal_name", nullable = false, length = 150)
    private String legalName;

    @Column(name = "trade_name", length = 150)
    private String tradeName;

    @Column(name = "state_registration", length = 20)
    private String stateRegistration;

    @Column(name = "city_registration", length = 20)
    private String cityRegistration;

    @Column(name = "tax_regime", length = 50)
    private String taxRegime;

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
