package com.api.erp.v1.main.features.product.infrastructure.proxy;

import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.configs.ProductConfig;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.features.product.application.dto.ListaExpandidaResponseDTO;
import com.api.erp.v1.main.features.product.domain.service.IListaExpandidaService;
import com.api.erp.v1.main.features.product.infrastructure.decorator.ListaExpandidaServiceApplyDecorate;
import com.api.erp.v1.main.features.product.infrastructure.service.ListaExpandidaService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class ListaExpandidaServiceProxy implements IListaExpandidaService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ListaExpandidaService listaExpandidaServiceDefault;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ITenantService tenantService;

    private IListaExpandidaService resolverService() {
        IListaExpandidaService response = listaExpandidaServiceDefault;
        ProductConfig productConfig = new ProductConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "listaExpandidaService" + tenantType;
            productConfig = tenant.getConfig().getProductConfig();

            try {
                IListaExpandidaService service = applicationContext.getBean(beanName, IListaExpandidaService.class);
                log.debug("[SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[SERVICE] Service {} not found, using default", beanName);
            }
        } catch (Exception e) {
            log.debug("[SERVICE] Error resolving tenant, using default: {}", e.getMessage());
        }
        return ListaExpandidaServiceApplyDecorate.aplicarDecorators(response, productConfig);
    }


    @Override
    public ListaExpandidaResponseDTO gerarListaExpandida(Long productId, BigDecimal quantidade) {
        return resolverService().gerarListaExpandida(productId, quantidade);
    }

    @Override
    public ListaExpandidaResponseDTO gerarListaCompras(Long productId, BigDecimal quantidade) {
        return resolverService().gerarListaCompras(productId, quantidade);
    }
}
