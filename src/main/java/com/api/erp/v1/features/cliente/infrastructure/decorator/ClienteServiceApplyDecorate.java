package com.api.erp.v1.features.cliente.infrastructure.decorator;

import com.api.erp.v1.features.cliente.domain.service.IClienteService;
import com.api.erp.v1.tenant.domain.entity.configs.ClienteConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClienteServiceApplyDecorate {
    public static IClienteService aplicarDecorators(IClienteService service, ClienteConfig config) {
        // Cache vem depois de validação mas antes de auditoria
        if (config != null && config.isClienteCacheEnabled()) {
            service = new CacheDecoratorClienteService(service);
            log.debug("[CLIENTE APPLYING DECORATE] CacheDecorator aplicado");
        }

        // Auditoria vem depois de cache
        if (config != null && config.isClienteAuditEnabled()) {
            service = new AuditDecoratorClienteService(service);
            log.debug("[CLIENTE APPLYING DECORATE] AuditDecorator aplicado");
        }

        // Notificação é a última na chain
        if (config != null && config.isClienteNotificationEnabled()) {
            service = new NotificationDecoratorClienteService(service);
            log.debug("[CLIENTE APPLYING DECORATE] NotificationDecorator aplicado");
        }

        return service;
    }
}
