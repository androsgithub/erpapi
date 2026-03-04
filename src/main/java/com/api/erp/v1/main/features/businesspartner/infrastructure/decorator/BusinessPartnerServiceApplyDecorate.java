package com.api.erp.v1.main.features.businesspartner.infrastructure.decorator;

import com.api.erp.v1.main.features.businesspartner.domain.service.IBusinessPartnerService;
import com.api.erp.v1.main.tenant.domain.entity.configs.BusinessPartnerConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessPartnerServiceApplyDecorate {
    public static IBusinessPartnerService aplicarDecorators(IBusinessPartnerService service, BusinessPartnerConfig config) {
        // Cache vem depois de validação mas antes de auditoria
        if (config != null && config.isBusinessPartnerCacheEnabled()) {
            service = new CacheDecoratorBusinessPartnerService(service);
            log.debug("[BUSINESSPARTNER APPLYING DECORATE] CacheDecorator aplicado");
        }

        // Auditoria vem depois de cache
        if (config != null && config.isBusinessPartnerAuditEnabled()) {
            service = new AuditDecoratorBusinessPartnerService(service);
            log.debug("[BUSINESSPARTNER APPLYING DECORATE] AuditDecorator aplicado");
        }

        // Notificação é a última na chain
        if (config != null && config.isBusinessPartnerNotificationEnabled()) {
            service = new NotificationDecoratorBusinessPartnerService(service);
            log.debug("[BUSINESSPARTNER APPLYING DECORATE] NotificationDecorator aplicado");
        }

        return service;
    }
}
