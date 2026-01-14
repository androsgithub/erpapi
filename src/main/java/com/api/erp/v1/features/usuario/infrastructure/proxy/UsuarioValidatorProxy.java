package com.api.erp.v1.features.usuario.infrastructure.proxy;

import com.api.erp.v1.features.tenant.domain.entity.Tenant;
import com.api.erp.v1.features.tenant.domain.entity.UsuarioConfig;
import com.api.erp.v1.features.tenant.domain.service.ITenantService;
import com.api.erp.v1.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.features.usuario.application.validator.BasicUsuarioValidator;
import com.api.erp.v1.features.usuario.domain.validator.IUsuarioValidator;
import com.api.erp.v1.features.usuario.infrastructure.decorator.UsuarioValidatorApplyDecorate;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsuarioValidatorProxy implements IUsuarioValidator {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BasicUsuarioValidator usuarioValidatorDefault;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ITenantService tenantService;

    private IUsuarioValidator resolverService() {
        IUsuarioValidator response = usuarioValidatorDefault;
        UsuarioConfig usuarioConfig = new UsuarioConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "usuarioValidator_" + tenantType;
            usuarioConfig = tenant.getConfig().getUsuarioConfig();

            try {
                IUsuarioValidator service = applicationContext.getBean(beanName, IUsuarioValidator.class);
                log.debug("[SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[SERVICE] Service {} não encontrado, usando padrão", beanName);
            }
        } catch (Exception e) {
            log.debug("[SERVICE] Erro ao resolver tenant, usando padrão: {}", e.getMessage());
        }
        return UsuarioValidatorApplyDecorate.aplicarDecorators(response, usuarioConfig);
    }

    @Override
    public void validar(CreateUsuarioRequest request) {
        resolverService().validar(request);
    }
}
