package com.api.erp.v1.features.cliente.infrastructure.proxy;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.domain.entity.Cliente;
import com.api.erp.v1.features.cliente.domain.service.IClienteService;
import com.api.erp.v1.features.cliente.infrastructure.decorator.ClienteServiceApplyDecorate;
import com.api.erp.v1.features.cliente.infrastructure.service.ClienteService;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.tenant.domain.entity.Tenant;
import com.api.erp.v1.tenant.domain.entity.configs.ClienteConfig;
import com.api.erp.v1.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClienteServiceProxy implements IClienteService {

    private final ApplicationContext applicationContext;

    private final ClienteService clienteServiceDefault;

    private final SecurityService securityService;

    private final ITenantService tenantService;

    @Autowired
    public ClienteServiceProxy(ApplicationContext applicationContext, ClienteService clienteServiceDefault, SecurityService securityService, ITenantService tenantService) {
        this.applicationContext = applicationContext;
        this.clienteServiceDefault = clienteServiceDefault;
        this.securityService = securityService;
        this.tenantService = tenantService;
    }

    private IClienteService resolverService() {
        IClienteService response = clienteServiceDefault;
        ClienteConfig clienteConfig = new ClienteConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "clienteService_" + tenantType;

            clienteConfig = tenant.getConfig().getClienteConfig();
            
            IClienteService service = applicationContext.getBean(beanName, IClienteService.class);
            log.debug("[CLIENTE SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
            response = service;

        } catch (Exception e) {
            log.debug("[CLIENTE SERVICE] Erro ao resolver tenant, usando padrão: {}", e.getMessage());
        }
        return ClienteServiceApplyDecorate.aplicarDecorators(response, clienteConfig);
    }

    @Override
    public Page<Cliente> pegarTodos(Pageable pageable) {
        return resolverService().pegarTodos(pageable);
    }

    @Override
    public Cliente criar(CreateClienteDto clienteDto) {
        return resolverService().criar(clienteDto);
    }

    @Override
    public Cliente atualizar(Long id, CreateClienteDto clienteDto) {
        return resolverService().atualizar(id, clienteDto);
    }

    @Override
    public Cliente pegarPorId(Long id) {
        return resolverService().pegarPorId(id);
    }

    @Override
    public void deletar(Long id) {
        resolverService().deletar(id);
    }
}
