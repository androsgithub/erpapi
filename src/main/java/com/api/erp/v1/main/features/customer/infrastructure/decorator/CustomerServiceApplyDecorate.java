package com.api.erp.v1.main.features.customer.infrastructure.decorator;

import com.api.erp.v1.main.features.customer.domain.service.ICustomerService;
import com.api.erp.v1.main.tenant.domain.entity.configs.CustomerConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerServiceApplyDecorate {
    public static ICustomerService aplicarDecorators(ICustomerService service, CustomerConfig config) {
        // Cache vem depois de validação mas antes de auditoria
        if (config != null && config.isCustomerCacheEnabled()) {
            service = new CacheDecoratorCustomerService(service);
            log.debug("[CUSTOMER APPLYING DECORATE] CacheDecorator aplicado");
        }

        // Auditoria vem depois de cache
        if (config != null && config.isCustomerAuditEnabled()) {
            service = new AuditDecoratorCustomerService(service);
            log.debug("[CUSTOMER APPLYING DECORATE] AuditDecorator aplicado");
        }

        // Notificação é a última na chain
        if (config != null && config.isCustomerNotificationEnabled()) {
            service = new NotificationDecoratorCustomerService(service);
            log.debug("[CUSTOMER APPLYING DECORATE] NotificationDecorator aplicado");
        }

        return service;
    }
}
