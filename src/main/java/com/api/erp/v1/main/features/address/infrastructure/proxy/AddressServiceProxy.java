package com.api.erp.v1.main.features.address.infrastructure.proxy;

import com.api.erp.v1.main.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.configs.AddressConfig;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.features.address.domain.entity.Address;
import com.api.erp.v1.main.features.address.domain.service.IAddressService;
import com.api.erp.v1.main.features.address.infrastructure.decorator.AddressServiceApplyDecorate;
import com.api.erp.v1.main.features.address.infrastructure.service.AddressService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AddressServiceProxy implements IAddressService {
    private final ApplicationContext applicationContext;
    private final AddressService addressServiceDefault;
    private final SecurityService securityService;
    private final ITenantService tenantService;

    @Autowired
    public AddressServiceProxy(ApplicationContext applicationContext, AddressService addressServiceDefault, SecurityService securityService, ITenantService tenantService) {
        this.applicationContext = applicationContext;
        this.addressServiceDefault = addressServiceDefault;
        this.securityService = securityService;
        this.tenantService = tenantService;
    }

    private IAddressService resolverService() {
        IAddressService response = addressServiceDefault;
        AddressConfig addressConfig = new AddressConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "addressService" + tenantType;
            addressConfig = tenant.getConfig().getAddressConfig();

            try {
                IAddressService service = applicationContext.getBean(beanName, IAddressService.class);
                log.debug("[CUSTOMER SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[CUSTOMER SERVICE] Service {} não encontrado, usando padrão", beanName);
            }
        } catch (Exception e) {
            log.debug("[CUSTOMER SERVICE] Erro ao resolver tenant, usando padrão: {}", e.getMessage());
        }
        return AddressServiceApplyDecorate.aplicarDecorators(response, addressConfig);
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
