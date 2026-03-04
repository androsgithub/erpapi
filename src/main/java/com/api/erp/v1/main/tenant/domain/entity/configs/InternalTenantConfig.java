package com.api.erp.v1.main.tenant.domain.entity.configs;

import com.api.erp.v1.main.shared.domain.enums.TenantCode;
import com.api.erp.v1.main.shared.domain.enums.TenantType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class InternalTenantConfig {
    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_type")
    private TenantType tenantType = TenantType.DEFAULT;

    @Column(name = "tenant_subdomain")
    private String tenantSubdomain;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_custom_code")
    private TenantCode tenantCustomCode;

    // Flags de features customizadas por tenant
    @Column(name = "tenant_features_enabled", nullable = false)
    private Boolean tenantFeaturesEnabled = true;
}
