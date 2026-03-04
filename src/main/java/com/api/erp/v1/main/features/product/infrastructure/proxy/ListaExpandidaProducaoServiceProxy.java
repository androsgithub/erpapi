package com.api.erp.v1.main.features.product.infrastructure.proxy;

import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.configs.ProductConfig;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.features.product.domain.entity.Product;
import com.api.erp.v1.main.features.product.domain.service.IListaExpandidaProducaoService;
import com.api.erp.v1.main.features.product.infrastructure.decorator.ListaExpandidaProducaoServiceApplyDecorate;
import com.api.erp.v1.main.features.product.infrastructure.service.ListaExpandidaProducaoService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ListaExpandidaProducaoServiceProxy implements IListaExpandidaProducaoService {
    private final ApplicationContext applicationContext;
    private final ListaExpandidaProducaoService listaExpandidaProducaoServiceDefault;
    private final SecurityService securityService;
    private final ITenantService tenantService;

    public ListaExpandidaProducaoServiceProxy(
            ApplicationContext applicationContext,
            ListaExpandidaProducaoService listaExpandidaProducaoServiceDefault,
            SecurityService securityService,
            ITenantService tenantService) {
        this.applicationContext = applicationContext;
        this.listaExpandidaProducaoServiceDefault = listaExpandidaProducaoServiceDefault;
        this.securityService = securityService;
        this.tenantService = tenantService;
    }

    private IListaExpandidaProducaoService resolverService() {
        IListaExpandidaProducaoService response = listaExpandidaProducaoServiceDefault;
        ProductConfig productConfig = new ProductConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "listaExpandidaProducaoService" + tenantType;
            productConfig = tenant.getConfig().getProductConfig();

            try {
                IListaExpandidaProducaoService service = applicationContext.getBean(beanName, IListaExpandidaProducaoService.class);
                log.debug("[SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[SERVICE] Service {} not found, using default", beanName);
            }
        } catch (Exception e) {
            log.debug("[SERVICE] Error resolving tenant, using default: {}", e.getMessage());
        }
        return ListaExpandidaProducaoServiceApplyDecorate.aplicarDecorators(response, productConfig);
    }

    @Override
    public Map<Product, BigDecimal> calcularListaExpandida(Product product, BigDecimal quantidadeRequerida) {
        return resolverService().calcularListaExpandida(product, quantidadeRequerida);
    }

    @Override
    public Map<Product, BigDecimal> obterListaCompras(Product product, BigDecimal quantidadeRequerida) {
        return resolverService().obterListaCompras(product, quantidadeRequerida);
    }

    @Override
    public List<Map.Entry<Product, BigDecimal>> obterListaOrdenada(Product product, BigDecimal quantidadeRequerida) {
        return resolverService().obterListaOrdenada(product, quantidadeRequerida);
    }
}
