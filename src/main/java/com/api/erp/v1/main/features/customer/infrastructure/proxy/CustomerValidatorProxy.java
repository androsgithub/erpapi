package com.api.erp.v1.main.features.customer.infrastructure.proxy;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.validator.ICustomerValidator;
import com.api.erp.v1.main.features.customer.infrastructure.validator.CustomerValidatorDefault;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Primary
public class CustomerValidatorProxy implements ICustomerValidator {
    private final ApplicationContext applicationContext;
    private final SecurityService securityService;
    private final CustomerValidatorDefault customerValidatorDefault;
    private final ITenantService tenantService;

    @Autowired
    public CustomerValidatorProxy(ApplicationContext applicationContext, SecurityService securityService, CustomerValidatorDefault customerValidatorDefault, ITenantService tenantService) {
        this.applicationContext = applicationContext;
        this.securityService = securityService;
        this.customerValidatorDefault = customerValidatorDefault;
        this.tenantService = tenantService;
    }


    private ICustomerValidator resolverValidator() {
        ICustomerValidator response = customerValidatorDefault;
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "customerValidator" + tenantType;

            ICustomerValidator validator = applicationContext.getBean(beanName, ICustomerValidator.class);
            log.debug("[CUSTOMER VALIDATOR] Validator resolvido para tenant {}: {}", tenantId, beanName);
            response = validator;
        } catch (Exception e) {
            log.debug("[CUSTOMER VALIDATOR] Error resolving tenant, using default: {}", e.getMessage());
        }
        return response;
    }


    @Override
    public void validarCriacao(CreateCustomerDto dto) {
        resolverValidator().validarCriacao(dto);
    }

    @Override
    public void validarAtualizacao(Long id, CreateCustomerDto dto) {
        resolverValidator().validarAtualizacao(id, dto);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        resolverValidator().validarPageable(pageable);
    }

    @Override
    public void validarDTO(CreateCustomerDto customerDto) {
        resolverValidator().validarDTO(customerDto);
    }

    @Override
    public void validarId(Long id) {
        resolverValidator().validarId(id);
    }
}
