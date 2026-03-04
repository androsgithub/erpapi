package com.api.erp.v1.main.tenant.domain.entity.configs;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfig {

    @Embedded
    @Builder.Default
    private UserConfig userConfig = new UserConfig();

    @Embedded
    @Builder.Default
    private ContactConfig contactConfig = new ContactConfig();

    @Embedded
    @Builder.Default
    private BusinessPartnerConfig businessPartnerConfig = new BusinessPartnerConfig();

    @Embedded
    @Builder.Default
    private AddressConfig addressConfig = new AddressConfig();

    @Embedded
    @Builder.Default
    private PermissionConfig permissionConfig = new PermissionConfig();

    @Embedded
    @Builder.Default
    private MeasureUnitConfig measureUnitConfig = new MeasureUnitConfig();

    @Embedded
    @Builder.Default
    private ProductConfig productConfig = new ProductConfig();

    @Embedded
    @Builder.Default
    private InternalTenantConfig internalTenantConfig = new InternalTenantConfig();
}


