package com.api.erp.v1.main.dynamic.features.product.infrastructure.proxy;

import com.api.erp.v1.main.dynamic.features.product.application.dto.ListaExpandidaResponseDTO;
import com.api.erp.v1.main.dynamic.features.product.domain.service.IListaExpandidaService;
import com.api.erp.v1.main.dynamic.features.product.infrastructure.service.ListaExpandidaService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.master.tenant.infrastructure.service.TenantBeanResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * ListaExpandidaServiceProxy
 * <p>
 * Proxy que resolve qual ListaExpandidaService usar baseado em tb_tnt_features.
 * Feature key: "listaExpandidaService"
 * <p>
 * Fluxo:
 * 1. Obtém o tenant atual via SecurityService
 * 2. Resolve o bean via TenantBeanResolver (consulta tb_tnt_features)
 * 3. Delega para o serviço resolvido
 */
@Service
@Slf4j
public class ListaExpandidaServiceProxy implements IListaExpandidaService {
    
    static final String FEATURE_KEY = "listaExpandidaService";
    
    private final ListaExpandidaService listaExpandidaServiceDefault;
    private final SecurityService securityService;
    private final TenantBeanResolver tenantBeanResolver;

    public ListaExpandidaServiceProxy(
            ListaExpandidaService listaExpandidaServiceDefault,
            SecurityService securityService,
            TenantBeanResolver tenantBeanResolver) {
        this.listaExpandidaServiceDefault = listaExpandidaServiceDefault;
        this.securityService = securityService;
        this.tenantBeanResolver = tenantBeanResolver;
    }

    /**
     * Resolve o ListaExpandidaService para o tenant atual
     * <p>
     * Se not authenticated, retorna o serviço padrão.
     * Se autenticado, consulta tb_tnt_features com TenantBeanResolver.
     */
    private IListaExpandidaService resolverService() {
        String strTenantId = securityService.getAuthTenantId();
        
        // Se não tem autenticação, usa serviço padrão
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[ListaExpandidaServiceProxy] No authentication, using default service");
            return listaExpandidaServiceDefault;
        }
        
        Long tenantId = Long.valueOf(strTenantId);
        
        try {
            // Resolve via TenantBeanResolver (consulta tb_tnt_features)
            IListaExpandidaService service = tenantBeanResolver.resolve(tenantId, FEATURE_KEY, IListaExpandidaService.class);
            log.debug("[ListaExpandidaServiceProxy] Service resolvido para tenant {}: {} (feature key={})", 
                      tenantId, service.getClass().getSimpleName(), FEATURE_KEY);
            return service;
        } catch (TenantBeanResolver.TenantFeatureNotFoundException | 
                 TenantBeanResolver.TenantBeanNotFoundException e) {
            log.warn("[ListaExpandidaServiceProxy] Falha ao resolver bean via TenantBeanResolver, usando default: {}", 
                     e.getMessage());
            return listaExpandidaServiceDefault;
        }
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
