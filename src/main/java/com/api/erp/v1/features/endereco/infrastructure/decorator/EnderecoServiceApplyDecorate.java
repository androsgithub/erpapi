package com.api.erp.v1.features.endereco.infrastructure.decorator;

import com.api.erp.v1.tenant.domain.entity.EnderecoConfig;
import com.api.erp.v1.features.endereco.domain.service.IEnderecoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnderecoServiceApplyDecorate {
    public static IEnderecoService aplicarDecorators(IEnderecoService service, EnderecoConfig config) {
        // Ordem: ValidationDecorator é aplicado primeiro (validação early-fail)
        if (config != null && config.isEnderecoValidationEnabled()) {
            log.debug("[ENDERECO FACTORY] ValidationDecorator aplicado");
            service = new ValidationDecoratorEnderecoService(service);
        }

        // Cache é aplicado antes de auditoria
        if (config != null && config.isEnderecoCacheEnabled()) {
            log.debug("[ENDERECO FACTORY] CacheDecorator aplicado");
            service = new CacheDecoratorEnderecoService(service);
        }

        // Auditoria é a penúltima na chain
        if (config != null && config.isEnderecoAuditEnabled()) {
            log.debug("[ENDERECO FACTORY] AuditDecorator aplicado");
            service = new AuditDecoratorEnderecoService(service);
        }

        return service;
    }
}
