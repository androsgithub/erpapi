package com.api.erp.v1.main.features.product.infrastructure.proxy;

import com.api.erp.v1.main.features.product.domain.entity.ProductType;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.configs.ProductConfig;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.features.product.application.dto.ProductRequestDTO;
import com.api.erp.v1.main.features.product.domain.entity.Product;
import com.api.erp.v1.main.features.product.domain.service.IProductService;
import com.api.erp.v1.main.features.product.infrastructure.decorator.ProductServiceApplyDecorate;
import com.api.erp.v1.main.features.product.infrastructure.service.ProductService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductServiceProxy implements IProductService {
    private final ApplicationContext applicationContext;
    private final ProductService productServiceDefault;
    private final SecurityService securityService;
    private final ITenantService tenantService;

    @Autowired
    public ProductServiceProxy(ApplicationContext applicationContext, ProductService productServiceDefault, SecurityService securityService, ITenantService tenantService) {
        this.applicationContext = applicationContext;
        this.productServiceDefault = productServiceDefault;
        this.securityService = securityService;
        this.tenantService = tenantService;
    }

    private IProductService resolverService() {
        IProductService response = productServiceDefault;
        ProductConfig productConfig = new ProductConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "productService" + tenantType;
            productConfig = tenant.getConfig().getProductConfig();

            try {
                IProductService service = applicationContext.getBean(beanName, IProductService.class);
                log.debug("[CUSTOMER SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[PRODUCT SERVICE] Service {} not found, using default", beanName);
            }
        } catch (Exception e) {
            log.debug("[PRODUCT SERVICE] Error resolving tenant, using default: {}", e.getMessage());
        }
        return ProductServiceApplyDecorate.aplicarDecorators(response, productConfig);
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
