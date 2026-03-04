package com.api.erp.v1.main.features.businesspartner.infrastructure.proxy;

import com.api.erp.v1.main.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.features.businesspartner.domain.entity.BusinessPartner;
import com.api.erp.v1.main.features.businesspartner.domain.service.IBusinessPartnerService;
import com.api.erp.v1.main.features.businesspartner.infrastructure.decorator.BusinessPartnerServiceApplyDecorate;
import com.api.erp.v1.main.features.businesspartner.infrastructure.service.BusinessPartnerService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.configs.BusinessPartnerConfig;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessPartnerServiceProxy implements IBusinessPartnerService {

    private final ApplicationContext applicationContext;

    private final BusinessPartnerService businesspartnerServiceDefault;

    private final SecurityService securityService;

    private final ITenantService tenantService;

    @Autowired
    public BusinessPartnerServiceProxy(ApplicationContext applicationContext, BusinessPartnerService businesspartnerServiceDefault, SecurityService securityService, ITenantService tenantService) {
        this.applicationContext = applicationContext;
        this.businesspartnerServiceDefault = businesspartnerServiceDefault;
        this.securityService = securityService;
        this.tenantService = tenantService;
    }

    private IBusinessPartnerService resolverService() {
        IBusinessPartnerService response = businesspartnerServiceDefault;
        BusinessPartnerConfig businesspartnerConfig = new BusinessPartnerConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "businesspartnerService" + tenantType;

            businesspartnerConfig = tenant.getConfig().getBusinessPartnerConfig();
            
            IBusinessPartnerService service = applicationContext.getBean(beanName, IBusinessPartnerService.class);
            log.debug("[BUSINESSPARTNER SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
            response = service;

        } catch (Exception e) {
            log.debug("[BUSINESSPARTNER SERVICE] Error resolving tenant, using default: {}", e.getMessage());
        }
        return BusinessPartnerServiceApplyDecorate.aplicarDecorators(response, businesspartnerConfig);
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
