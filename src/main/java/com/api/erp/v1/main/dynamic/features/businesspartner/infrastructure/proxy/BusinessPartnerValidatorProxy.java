package com.api.erp.v1.main.dynamic.features.businesspartner.infrastructure.proxy;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.validator.IBusinessPartnerValidator;
import com.api.erp.v1.main.dynamic.features.businesspartner.infrastructure.validator.BusinessPartnerValidatorDefault;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.master.tenant.infrastructure.service.TenantBeanResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * BusinessPartnerValidatorProxy
 * <p>
 * Proxy que resolve qual BusinessPartnerValidator usar baseado em tb_tnt_features.
 * Feature key: "businessPartnerValidator"
 * <p>
 * Fluxo:
 * 1. Obtém o tenant atual via SecurityService
 * 2. Resolve o bean via TenantBeanResolver (consulta tb_tnt_features)
 * 3. Delega para o validador resolvido
 */
@Component
@Slf4j
@Primary
public class BusinessPartnerValidatorProxy implements IBusinessPartnerValidator {
    
    static final String FEATURE_KEY = "businessPartnerValidator";
    
    private final SecurityService securityService;
    private final BusinessPartnerValidatorDefault businessPartnerValidatorDefault;
    private final TenantBeanResolver tenantBeanResolver;

    public BusinessPartnerValidatorProxy(
            SecurityService securityService,
            BusinessPartnerValidatorDefault businessPartnerValidatorDefault,
            TenantBeanResolver tenantBeanResolver) {
        this.securityService = securityService;
        this.businessPartnerValidatorDefault = businessPartnerValidatorDefault;
        this.tenantBeanResolver = tenantBeanResolver;
    }

    /**
     * Resolve o BusinessPartnerValidator para o tenant atual
     * <p>
     * Se not authenticated, retorna o validador padrão.
     * Se autenticado, consulta tb_tnt_features com TenantBeanResolver.
     */
    private IBusinessPartnerValidator resolverValidator() {
        String strTenantId = securityService.getAuthTenantId();
        
        // Se não tem autenticação, usa validador padrão
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[BusinessPartnerValidatorProxy] No authentication, using default validator");
            return businessPartnerValidatorDefault;
        }
        
        Long tenantId = Long.valueOf(strTenantId);
        
        try {
            // Resolve via TenantBeanResolver (consulta tb_tnt_features)
            IBusinessPartnerValidator validator = tenantBeanResolver.resolve(tenantId, FEATURE_KEY, IBusinessPartnerValidator.class);
            log.debug("[BusinessPartnerValidatorProxy] Validator resolvido para tenant {}: {} (feature key={})", 
                      tenantId, validator.getClass().getSimpleName(), FEATURE_KEY);
            return validator;
        } catch (TenantBeanResolver.TenantFeatureNotFoundException | 
                 TenantBeanResolver.TenantBeanNotFoundException e) {
            log.warn("[BusinessPartnerValidatorProxy] Falha ao resolver bean via TenantBeanResolver, usando default: {}", 
                     e.getMessage());
            return businessPartnerValidatorDefault;
        }
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
