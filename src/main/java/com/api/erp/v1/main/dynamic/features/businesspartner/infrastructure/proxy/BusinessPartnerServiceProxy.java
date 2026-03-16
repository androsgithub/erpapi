package com.api.erp.v1.main.dynamic.features.businesspartner.infrastructure.proxy;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartner;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.service.IBusinessPartnerService;
import com.api.erp.v1.main.dynamic.features.businesspartner.infrastructure.service.BusinessPartnerService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.master.tenant.infrastructure.service.TenantBeanResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * BusinessPartnerServiceProxy
 * <p>
 * Proxy que resolve qual BusinessPartnerService usar baseado em tb_tnt_features.
 * Feature key: "businessPartnerService"
 * <p>
 * Fluxo:
 * 1. Obtém o tenant atual via SecurityService
 * 2. Resolve o bean via TenantBeanResolver (consulta tb_tnt_features)
 * 3. Delega para o serviço resolvido
 */
@Service
@Slf4j
public class BusinessPartnerServiceProxy implements IBusinessPartnerService {

    static final String FEATURE_KEY = "businessPartnerService";
    
    private final BusinessPartnerService businessPartnerServiceDefault;
    private final SecurityService securityService;
    private final TenantBeanResolver tenantBeanResolver;

    public BusinessPartnerServiceProxy(
            BusinessPartnerService businessPartnerServiceDefault,
            SecurityService securityService,
            TenantBeanResolver tenantBeanResolver) {
        this.businessPartnerServiceDefault = businessPartnerServiceDefault;
        this.securityService = securityService;
        this.tenantBeanResolver = tenantBeanResolver;
    }

    /**
     * Resolve o BusinessPartnerService para o tenant atual
     * <p>
     * Se not authenticated, retorna o serviço padrão.
     * Se autenticado, consulta tb_tnt_features com TenantBeanResolver.
     */
    private IBusinessPartnerService resolverService() {
        String strTenantId = securityService.getAuthTenantId();
        
        // Se não tem autenticação, usa serviço padrão
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[BusinessPartnerServiceProxy] No authentication, using default service");
            return businessPartnerServiceDefault;
        }
        
        Long tenantId = Long.valueOf(strTenantId);
        
        try {
            // Resolve via TenantBeanResolver (consulta tb_tnt_features)
            IBusinessPartnerService service = tenantBeanResolver.resolve(tenantId, FEATURE_KEY, IBusinessPartnerService.class);
            log.debug("[BusinessPartnerServiceProxy] Service resolvido para tenant {}: {} (feature key={})", 
                      tenantId, service.getClass().getSimpleName(), FEATURE_KEY);
            return service;
        } catch (TenantBeanResolver.TenantFeatureNotFoundException | 
                 TenantBeanResolver.TenantBeanNotFoundException e) {
            log.warn("[BusinessPartnerServiceProxy] Falha ao resolver bean via TenantBeanResolver, usando default: {}", 
                     e.getMessage());
            return businessPartnerServiceDefault;
        }
    }

    @Override
    public Page<BusinessPartner> pegarTodos(Pageable pageable) {
        return resolverService().pegarTodos(pageable);
    }

    @Override
    public BusinessPartner criar(CreateBusinessPartnerDto businesspartnerDto) {
        return resolverService().criar(businesspartnerDto);
    }

    @Override
    public BusinessPartner atualizar(Long id, CreateBusinessPartnerDto businesspartnerDto) {
        return resolverService().atualizar(id, businesspartnerDto);
    }

    @Override
    public BusinessPartner pegarPorId(Long id) {
        return resolverService().pegarPorId(id);
    }

    @Override
    public void deletar(Long id) {
        resolverService().deletar(id);
    }
}
