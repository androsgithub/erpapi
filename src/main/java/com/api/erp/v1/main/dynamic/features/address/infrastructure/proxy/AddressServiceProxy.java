package com.api.erp.v1.main.dynamic.features.address.infrastructure.proxy;

import com.api.erp.v1.main.dynamic.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.dynamic.features.address.domain.entity.Address;
import com.api.erp.v1.main.dynamic.features.address.domain.service.IAddressService;
import com.api.erp.v1.main.dynamic.features.address.infrastructure.service.AddressService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.master.tenant.infrastructure.service.TenantBeanResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AddressServiceProxy
 * <p>
 * Proxy que resolve qual AddressService usar baseado em tb_tnt_features.
 * Feature key: "addressService"
 * <p>
 * Fluxo:
 * 1. Obtém o tenant atual via SecurityService
 * 2. Resolve o bean via TenantBeanResolver (consulta tb_tnt_features)
 * 3. Delega para o serviço resolvido
 */
@Service
@Slf4j
public class AddressServiceProxy implements IAddressService {
    
    static final String FEATURE_KEY = "addressService";
    
    private final AddressService addressServiceDefault;
    private final SecurityService securityService;
    private final TenantBeanResolver tenantBeanResolver;

    public AddressServiceProxy(
            AddressService addressServiceDefault,
            SecurityService securityService,
            TenantBeanResolver tenantBeanResolver) {
        this.addressServiceDefault = addressServiceDefault;
        this.securityService = securityService;
        this.tenantBeanResolver = tenantBeanResolver;
    }

    /**
     * Resolve o AddressService para o tenant atual
     * <p>
     * Se not authenticated, retorna o serviço padrão.
     * Se autenticado, consulta tb_tnt_features com TenantBeanResolver.
     */
    private IAddressService resolverService() {
        String strTenantId = securityService.getAuthTenantId();
        
        // Se não tem autenticação, usa serviço padrão
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[AddressServiceProxy] No authentication, using default service");
            return addressServiceDefault;
        }
        
        Long tenantId = Long.valueOf(strTenantId);
        
        try {
            // Resolve via TenantBeanResolver (consulta tb_tnt_features)
            IAddressService service = tenantBeanResolver.resolve(tenantId, FEATURE_KEY, IAddressService.class);
            log.debug("[AddressServiceProxy] Service resolvido para tenant {}: {} (feature key={})", 
                      tenantId, service.getClass().getSimpleName(), FEATURE_KEY);
            return service;
        } catch (TenantBeanResolver.TenantFeatureNotFoundException | 
                 TenantBeanResolver.TenantBeanNotFoundException e) {
            log.warn("[AddressServiceProxy] Falha ao resolver bean via TenantBeanResolver, usando default: {}", 
                     e.getMessage());
            return addressServiceDefault;
        }
    }

    @Override
    public Address criar(CreateAddressRequest request) {
        return resolverService().criar(request);
    }

    @Override
    public Address buscarPorId(Long id) {
        return resolverService().buscarPorId(id);
    }

    @Override
    public List<Address> buscarTodos() {
        return resolverService().buscarTodos();
    }

    @Override
    public Address atualizar(Long id, CreateAddressRequest request) {
        return resolverService().atualizar(id, request);
    }

    @Override
    public void deletar(Long id) {
        resolverService().deletar(id);
    }
}
