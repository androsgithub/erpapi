package com.api.erp.v1.main.features.contact.infrastructure.decorator;

import com.api.erp.v1.main.features.contact.domain.service.IContactService;
import com.api.erp.v1.main.tenant.domain.entity.configs.ContactConfig;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ContactServiceApplyDecorate{
    public static IContactService aplicarDecorators(IContactService service, ContactConfig config) {
        // Cache vem depois de validação mas antes de auditoria
        if (config != null && config.isContactCacheEnabled()) {
            service = new CacheDecoratorContactService(service);
            log.debug("[CONTACT APPLYING DECORATE] CacheDecorator aplicado");
        }

        // Auditoria vem depois de cache
        if (config != null && config.isContactAuditEnabled()) {
            service = new AuditDecoratorContactService(service);
            log.debug("[CONTACT APPLYING DECORATE] AuditDecorator aplicado");
        }

        // Notificação é a última na chain
        if (config != null && config.isContactNotificationEnabled()) {
            service = new NotificationDecoratorContactService(service);
            log.debug("[CONTACT APPLYING DECORATE] NotificationDecorator aplicado");
        }

        return service;
    }
}
