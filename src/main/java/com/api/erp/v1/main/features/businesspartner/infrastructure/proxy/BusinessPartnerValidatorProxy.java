package com.api.erp.v1.main.features.businesspartner.infrastructure.proxy;

import com.api.erp.v1.main.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.features.businesspartner.domain.validator.IBusinessPartnerValidator;
import com.api.erp.v1.main.features.businesspartner.infrastructure.validator.BusinessPartnerValidatorDefault;
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
public class BusinessPartnerValidatorProxy implements IBusinessPartnerValidator {
    private final ApplicationContext applicationContext;
    private final SecurityService securityService;
    private final BusinessPartnerValidatorDefault businesspartnerValidatorDefault;
    private final ITenantService tenantService;

    @Autowired
    public BusinessPartnerValidatorProxy(ApplicationContext applicationContext, SecurityService securityService, BusinessPartnerValidatorDefault businesspartnerValidatorDefault, ITenantService tenantService) {
        this.applicationContext = applicationContext;
        this.securityService = securityService;
        this.businesspartnerValidatorDefault = businesspartnerValidatorDefault;
        this.tenantService = tenantService;
    }


    private IBusinessPartnerValidator resolverValidator() {
        IBusinessPartnerValidator response = businesspartnerValidatorDefault;
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "businesspartnerValidator" + tenantType;

            IBusinessPartnerValidator validator = applicationContext.getBean(beanName, IBusinessPartnerValidator.class);
            log.debug("[BUSINESSPARTNER VALIDATOR] Validator resolvido para tenant {}: {}", tenantId, beanName);
            response = validator;
        } catch (Exception e) {
            log.debug("[BUSINESSPARTNER VALIDATOR] Error resolving tenant, using default: {}", e.getMessage());
        }
        return response;
    }


    @Override
    public void validarCriacao(CreateBusinessPartnerDto dto) {
        resolverValidator().validarCriacao(dto);
    }

    @Override
    public void validarAtualizacao(Long id, CreateBusinessPartnerDto dto) {
        resolverValidator().validarAtualizacao(id, dto);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        resolverValidator().validarPageable(pageable);
    }

    @Override
    public void validarDTO(CreateBusinessPartnerDto businesspartnerDto) {
        resolverValidator().validarDTO(businesspartnerDto);
    }

    @Override
    public void validarId(Long id) {
        resolverValidator().validarId(id);
    }
}
