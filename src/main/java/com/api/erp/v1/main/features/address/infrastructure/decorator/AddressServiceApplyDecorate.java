package com.api.erp.v1.main.features.address.infrastructure.decorator;

import com.api.erp.v1.main.tenant.domain.entity.configs.AddressConfig;
import com.api.erp.v1.main.features.address.domain.service.IAddressService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddressServiceApplyDecorate {
    public static IAddressService aplicarDecorators(IAddressService service, AddressConfig config) {
        // Ordem: ValidationDecorator é aplicado primeiro (validação early-fail)
        if (config != null && config.isAddressValidationEnabled()) {
            log.debug("[ADDRESS FACTORY] ValidationDecorator aplicado");
            service = new ValidationDecoratorAddressService(service);
        }

        // Cache é aplicado antes de auditoria
        if (config != null && config.isAddressCacheEnabled()) {
            log.debug("[ADDRESS FACTORY] CacheDecorator aplicado");
            service = new CacheDecoratorAddressService(service);
        }

        // Auditoria é a penúltima na chain
        if (config != null && config.isAddressAuditEnabled()) {
            log.debug("[ADDRESS FACTORY] AuditDecorator aplicado");
            service = new AuditDecoratorAddressService(service);
        }

        return service;
    }
}
