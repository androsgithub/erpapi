package com.api.erp.v1.main.dynamic.features.product.infrastructure.proxy;

import com.api.erp.v1.main.dynamic.features.product.domain.entity.Product;
import com.api.erp.v1.main.dynamic.features.product.domain.service.IListaExpandidaProducaoService;
import com.api.erp.v1.main.dynamic.features.product.infrastructure.service.ListaExpandidaProducaoService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.master.tenant.infrastructure.service.TenantBeanResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ListaExpandidaProducaoServiceProxy
 * <p>
 * Proxy que resolve qual ListaExpandidaProducaoService usar baseado em tb_tnt_features.
 * Feature key: "listaExpandidaProducaoService"
 * <p>
 * Fluxo:
 * 1. Obtém o tenant atual via SecurityService
 * 2. Resolve o bean via TenantBeanResolver (consulta tb_tnt_features)
 * 3. Delega para o serviço resolvido
 */
@Service
@Slf4j
public class ListaExpandidaProducaoServiceProxy implements IListaExpandidaProducaoService {
    
    static final String FEATURE_KEY = "listaExpandidaProducaoService";
    
    private final ListaExpandidaProducaoService listaExpandidaProducaoServiceDefault;
    private final SecurityService securityService;
    private final TenantBeanResolver tenantBeanResolver;

    public ListaExpandidaProducaoServiceProxy(
            ListaExpandidaProducaoService listaExpandidaProducaoServiceDefault,
            SecurityService securityService,
            TenantBeanResolver tenantBeanResolver) {
        this.listaExpandidaProducaoServiceDefault = listaExpandidaProducaoServiceDefault;
        this.securityService = securityService;
        this.tenantBeanResolver = tenantBeanResolver;
    }

    /**
     * Resolve o ListaExpandidaProducaoService para o tenant atual
     * <p>
     * Se not authenticated, retorna o serviço padrão.
     * Se autenticado, consulta tb_tnt_features com TenantBeanResolver.
     */
    private IListaExpandidaProducaoService resolverService() {
        String strTenantId = securityService.getAuthTenantId();
        
        // Se não tem autenticação, usa serviço padrão
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[ListaExpandidaProducaoServiceProxy] No authentication, using default service");
            return listaExpandidaProducaoServiceDefault;
        }
        
        Long tenantId = Long.valueOf(strTenantId);
        
        try {
            // Resolve via TenantBeanResolver (consulta tb_tnt_features)
            IListaExpandidaProducaoService service = tenantBeanResolver.resolve(tenantId, FEATURE_KEY, IListaExpandidaProducaoService.class);
            log.debug("[ListaExpandidaProducaoServiceProxy] Service resolvido para tenant {}: {} (feature key={})", 
                      tenantId, service.getClass().getSimpleName(), FEATURE_KEY);
            return service;
        } catch (TenantBeanResolver.TenantFeatureNotFoundException | 
                 TenantBeanResolver.TenantBeanNotFoundException e) {
            log.warn("[ListaExpandidaProducaoServiceProxy] Falha ao resolver bean via TenantBeanResolver, usando default: {}", 
                     e.getMessage());
            return listaExpandidaProducaoServiceDefault;
        }
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
