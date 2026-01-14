package com.api.erp.v1.features.cliente.infrastructure.proxy;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.features.cliente.infrastructure.validator.ClienteValidatorDefault;
import com.api.erp.v1.features.tenant.domain.entity.Tenant;
import com.api.erp.v1.features.tenant.domain.service.ITenantService;
import com.api.erp.v1.shared.domain.enums.TenantCode;
import com.api.erp.v1.shared.domain.enums.TenantType;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClienteValidatorProxy implements IClienteValidator {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ClienteValidatorDefault clienteValidatorDefault;
    @Autowired
    private ITenantService tenantService;

    private IClienteValidator resolverValidator() {
        IClienteValidator response = clienteValidatorDefault;
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "clienteValidator_" + tenantType;

            try {
                IClienteValidator validator = applicationContext.getBean(beanName, IClienteValidator.class);
                log.debug("[CLIENTE VALIDATOR] Validator resolvido para tenant {}: {}", tenantId, beanName);
                response = validator;
            } catch (Exception e) {
                log.debug("[CLIENTE VALIDATOR] Validator {} não encontrado, usando padrão", beanName);
            }
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
