package com.api.erp.v1.features.contato.infrastructure.decorator;

import com.api.erp.v1.features.contato.domain.service.IContatoService;
import com.api.erp.v1.tenant.domain.entity.configs.ContatoConfig;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ContatoServiceApplyDecorate{
    public static IContatoService aplicarDecorators(IContatoService service, ContatoConfig config) {
        // Cache vem depois de validação mas antes de auditoria
        if (config != null && config.isContatoCacheEnabled()) {
            service = new CacheDecoratorContatoService(service);
            log.debug("[CONTATO APPLYING DECORATE] CacheDecorator aplicado");
        }

        // Auditoria vem depois de cache
        if (config != null && config.isContatoAuditEnabled()) {
            service = new AuditDecoratorContatoService(service);
            log.debug("[CONTATO APPLYING DECORATE] AuditDecorator aplicado");
        }

        // Notificação é a última na chain
        if (config != null && config.isContatoNotificationEnabled()) {
            service = new NotificationDecoratorContatoService(service);
            log.debug("[CONTATO APPLYING DECORATE] NotificationDecorator aplicado");
        }

        return service;
    }
}
