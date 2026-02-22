package com.api.erp.v1.main.features.user.infrastructure.proxy;

import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.configs.UserConfig;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.features.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.features.user.application.validator.BasicUserValidator;
import com.api.erp.v1.main.features.user.domain.validator.IUserValidator;
import com.api.erp.v1.main.features.user.infrastructure.decorator.UserValidatorApplyDecorate;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserValidatorProxy implements IUserValidator {

    private final ApplicationContext applicationContext;
    private final BasicUserValidator userValidatorDefault;
    private final SecurityService securityService;
    private final ITenantService tenantService;

    @Autowired
    public UserValidatorProxy(ApplicationContext applicationContext, BasicUserValidator userValidatorDefault, SecurityService securityService, ITenantService tenantService) {
        this.applicationContext = applicationContext;
        this.userValidatorDefault = userValidatorDefault;
        this.securityService = securityService;
        this.tenantService = tenantService;
    }

    private IUserValidator resolverService() {
        IUserValidator response = userValidatorDefault;
        UserConfig userConfig = new UserConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "userValidator_" + tenantType;
            userConfig = tenant.getConfig().getUserConfig();

            try {
                IUserValidator service = applicationContext.getBean(beanName, IUserValidator.class);
                log.debug("[SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[SERVICE] Service {} not found, using default", beanName);
            }
        } catch (Exception e) {
            log.debug("[SERVICE] Error resolving tenant, using default: {}", e.getMessage());
        }
        return UserValidatorApplyDecorate.aplicarDecorators(response, userConfig);
    }

    @Override
    public void validar(CreateUserRequest request) {
        resolverService().validar(request);
    }
}
