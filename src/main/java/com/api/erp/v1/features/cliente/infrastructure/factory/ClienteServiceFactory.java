package com.api.erp.v1.features.cliente.infrastructure.factory;

import com.api.erp.v1.features.cliente.domain.repository.ClienteRepository;
import com.api.erp.v1.features.cliente.domain.service.IClienteService;
import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.features.cliente.infrastructure.decorator.*;
import com.api.erp.v1.features.cliente.infrastructure.service.ClienteService;
import com.api.erp.v1.features.empresa.domain.entity.ClienteConfig;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ClienteServiceFactory {

    private final ClienteRepository repository;
    private final IClienteValidator validator;
    private final IEmpresaService empresaService;

    public IClienteService create() {
        log.info("[CLIENTE FACTORY] Construindo ClienteService com decorators");
        
        ClienteConfig config = obterConfiguracao();
        IClienteService service = new ClienteService(repository, validator);
        
        service = aplicarDecorators(service, config);
        
        log.info("[CLIENTE FACTORY] ClienteService construído com sucesso");
        return service;
    }

    private IClienteService aplicarDecorators(IClienteService service, ClienteConfig config) {
        // Cache vem depois de validação mas antes de auditoria
        if (config != null && config.isClienteCacheEnabled()) {
            service = new CacheDecoratorClienteService(service);
            log.debug("[CLIENTE FACTORY] CacheDecorator aplicado");
        }

        // Auditoria vem depois de cache
        if (config != null && config.isClienteAuditEnabled()) {
            service = new AuditDecoratorClienteService(service);
            log.debug("[CLIENTE FACTORY] AuditDecorator aplicado");
        }

        // Notificação é a última na chain
        if (config != null && config.isClienteNotificationEnabled()) {
            service = new NotificationDecoratorClienteService(service);
            log.debug("[CLIENTE FACTORY] NotificationDecorator aplicado");
        }

        return service;
    }

    private ClienteConfig obterConfiguracao() {
        try {
            return empresaService.getEmpresaConfig().getClienteConfig();
        } catch (Exception e) {
            log.warn("[CLIENTE FACTORY] Não foi possível obter ClienteConfig, " +
                    "usando apenas serviço base. Erro: {}", e.getMessage());
            return null;
        }
    }
}
