package com.api.erp.v1.main.master.user.infrastructure.proxy;

import com.api.erp.v1.main.master.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.master.user.application.validator.BasicUserValidator;
import com.api.erp.v1.main.master.user.domain.validator.IUserValidator;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.master.tenant.infrastructure.service.TenantBeanResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * UserValidatorProxy
 * <p>
 * Proxy que resolve qual UserValidator usar baseado em tb_tnt_features.
 * Feature key: "userValidator"
 * <p>
 * Fluxo:
 * 1. Obtém o tenant atual via SecurityService
 * 2. Resolve o bean via TenantBeanResolver (consulta tb_tnt_features)
 * 3. Delega para o validador resolvido
 */
@Component
@Slf4j
public class UserValidatorProxy implements IUserValidator {

    static final String FEATURE_KEY = "userValidator";

    private final BasicUserValidator userValidatorDefault;
    private final SecurityService securityService;
    private final TenantBeanResolver tenantBeanResolver;

    public UserValidatorProxy(
            BasicUserValidator userValidatorDefault,
            SecurityService securityService,
            TenantBeanResolver tenantBeanResolver) {
        this.userValidatorDefault = userValidatorDefault;
        this.securityService = securityService;
        this.tenantBeanResolver = tenantBeanResolver;
    }

    /**
     * Resolve o UserValidator para o tenant atual
     */
    private IUserValidator resolverService() {
        String strTenantId = securityService.getAuthTenantId();
        
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[UserValidatorProxy] No authentication, using default validator");
            return userValidatorDefault;
        }
        
        Long tenantId = Long.valueOf(strTenantId);
        
        try {
            // Resolve via TenantBeanResolver (consulta tb_tnt_features)
            IUserValidator validator = tenantBeanResolver.resolve(tenantId, FEATURE_KEY, IUserValidator.class);
            log.debug("[UserValidatorProxy] Validator resolvido para tenant {}: {} (feature key={})", 
                      tenantId, validator.getClass().getSimpleName(), FEATURE_KEY);
            return validator;
        } catch (TenantBeanResolver.TenantFeatureNotFoundException | 
                 TenantBeanResolver.TenantBeanNotFoundException e) {
            log.warn("[UserValidatorProxy] Falha ao resolver bean via TenantBeanResolver, usando default: {}", 
                     e.getMessage());
            return userValidatorDefault;
        }
    }

    @Override
    public void validar(CreateUserRequest request) {
        resolverService().validar(request);
    }
}
