package com.api.erp.v1.features.cliente.infrastructure.proxy;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.features.cliente.infrastructure.validator.ClienteValidatorDefault;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.tenant.domain.entity.Tenant;
import com.api.erp.v1.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Primary
public class ClienteValidatorProxy implements IClienteValidator {
    private final ApplicationContext applicationContext;
    private final SecurityService securityService;
    private final ClienteValidatorDefault clienteValidatorDefault;
    private final ITenantService tenantService;

    @Autowired
    public ClienteValidatorProxy(ApplicationContext applicationContext, SecurityService securityService, ClienteValidatorDefault clienteValidatorDefault, ITenantService tenantService) {
        this.applicationContext = applicationContext;
        this.securityService = securityService;
        this.clienteValidatorDefault = clienteValidatorDefault;
        this.tenantService = tenantService;
    }


    private IClienteValidator resolverValidator() {
        IClienteValidator response = clienteValidatorDefault;
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "clienteValidator_" + tenantType;

            IClienteValidator validator = applicationContext.getBean(beanName, IClienteValidator.class);
            log.debug("[CLIENTE VALIDATOR] Validator resolvido para tenant {}: {}", tenantId, beanName);
            response = validator;
        } catch (Exception e) {
            log.debug("[CLIENTE VALIDATOR] Erro ao resolver tenant, usando padrão: {}", e.getMessage());
        }
        return response;
    }


    @Override
    public void validarCriacao(CreateClienteDto dto) {
        resolverValidator().validarCriacao(dto);
    }

    @Override
    public void validarAtualizacao(Long id, CreateClienteDto dto) {
        resolverValidator().validarAtualizacao(id, dto);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        resolverValidator().validarPageable(pageable);
    }

    @Override
    public void validarDTO(CreateClienteDto clienteDto) {
        resolverValidator().validarDTO(clienteDto);
    }

    @Override
    public void validarId(Long id) {
        resolverValidator().validarId(id);
    }
}
