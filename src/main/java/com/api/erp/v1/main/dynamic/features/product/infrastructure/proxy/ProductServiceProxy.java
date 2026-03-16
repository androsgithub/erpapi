package com.api.erp.v1.main.dynamic.features.product.infrastructure.proxy;

import com.api.erp.v1.main.dynamic.features.product.application.dto.ProductRequestDTO;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.Product;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.ProductType;
import com.api.erp.v1.main.dynamic.features.product.domain.service.IProductService;
import com.api.erp.v1.main.dynamic.features.product.infrastructure.service.ProductService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.master.tenant.infrastructure.service.TenantBeanResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * ProductServiceProxy
 * <p>
 * Proxy que resolve qual ProductService usar baseado em tb_tnt_features.
 * Feature key: "productService"
 * <p>
 * Fluxo:
 * 1. Obtém o tenant atual via SecurityService
 * 2. Resolve o bean via TenantBeanResolver (consulta tb_tnt_features)
 * 3. Delega para o serviço resolvido
 */
@Service
@Slf4j
public class ProductServiceProxy implements IProductService {
    
    static final String FEATURE_KEY = "productService";
    
    private final ProductService productServiceDefault;
    private final SecurityService securityService;
    private final TenantBeanResolver tenantBeanResolver;

    public ProductServiceProxy(
            ProductService productServiceDefault,
            SecurityService securityService,
            TenantBeanResolver tenantBeanResolver) {
        this.productServiceDefault = productServiceDefault;
        this.securityService = securityService;
        this.tenantBeanResolver = tenantBeanResolver;
    }

    /**
     * Resolve o ProductService para o tenant atual
     */
    private IProductService resolverService() {
        String strTenantId = securityService.getAuthTenantId();
        
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[ProductServiceProxy] No authentication, using default service");
            return productServiceDefault;
        }
        
        Long tenantId = Long.valueOf(strTenantId);
        
        try {
            // Resolve via TenantBeanResolver (consulta tb_tnt_features)
            IProductService service = tenantBeanResolver.resolve(tenantId, FEATURE_KEY, IProductService.class);
            log.debug("[ProductServiceProxy] Service resolvido para tenant {}: {} (feature key={})", 
                      tenantId, service.getClass().getSimpleName(), FEATURE_KEY);
            return service;
        } catch (TenantBeanResolver.TenantFeatureNotFoundException | 
                 TenantBeanResolver.TenantBeanNotFoundException e) {
            log.warn("[ProductServiceProxy] Falha ao resolver bean via TenantBeanResolver, usando default: {}", 
                     e.getMessage());
            return productServiceDefault;
        }
    }

    @Override
    public Product criar(ProductRequestDTO dto) {
        return resolverService().criar(dto);
    }

    @Override
    public Product atualizar(Long id, ProductRequestDTO productModificado) {
        return resolverService().atualizar(id, productModificado);
    }

    @Override
    public Product obter(Long id) {
        return resolverService().obter(id);
    }

    @Override
    public Page<Product> listar(Pageable pageable) {
        return resolverService().listar(pageable);
    }

    @Override
    public Page<Product> listarPorTipo(ProductType tipo, Pageable pageable) {
        return resolverService().listarPorTipo(tipo, pageable);
    }

    @Override
    public Product ativar(Long id) {
        return resolverService().ativar(id);
    }

    @Override
    public Product desativar(Long id) {
        return resolverService().desativar(id);
    }

    @Override
    public Product bloquear(Long id) {
        return resolverService().bloquear(id);
    }

    @Override
    public Product descontinuar(Long id) {
        return resolverService().descontinuar(id);
    }

    @Override
    public void deletar(Long id) {
        resolverService().deletar(id);
    }
}
