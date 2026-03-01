package com.api.erp.v1.main.features.customer.infrastructure.proxy;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.entity.Customer;
import com.api.erp.v1.main.features.customer.domain.service.ICustomerService;
import com.api.erp.v1.main.features.customer.infrastructure.decorator.CustomerServiceApplyDecorate;
import com.api.erp.v1.main.features.customer.infrastructure.service.CustomerService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.configs.CustomerConfig;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerServiceProxy implements ICustomerService {

    private final ApplicationContext applicationContext;

    private final CustomerService customerServiceDefault;

    private final SecurityService securityService;

    private final ITenantService tenantService;

    @Autowired
    public CustomerServiceProxy(ApplicationContext applicationContext, CustomerService customerServiceDefault, SecurityService securityService, ITenantService tenantService) {
        this.applicationContext = applicationContext;
        this.customerServiceDefault = customerServiceDefault;
        this.securityService = securityService;
        this.tenantService = tenantService;
    }

    private ICustomerService resolverService() {
        ICustomerService response = customerServiceDefault;
        CustomerConfig customerConfig = new CustomerConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "customerService" + tenantType;

            customerConfig = tenant.getConfig().getCustomerConfig();
            
            ICustomerService service = applicationContext.getBean(beanName, ICustomerService.class);
            log.debug("[CUSTOMER SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
            response = service;

        } catch (Exception e) {
            log.debug("[CUSTOMER SERVICE] Error resolving tenant, using default: {}", e.getMessage());
        }
        return CustomerServiceApplyDecorate.aplicarDecorators(response, customerConfig);
    }

    @Override
    public Page<Customer> pegarTodos(Pageable pageable) {
        return resolverService().pegarTodos(pageable);
    }

    @Override
    public Customer criar(CreateCustomerDto customerDto) {
        return resolverService().criar(customerDto);
    }

    @Override
    public Customer atualizar(Long id, CreateCustomerDto customerDto) {
        return resolverService().atualizar(id, customerDto);
    }

    @Override
    public Customer pegarPorId(Long id) {
        return resolverService().pegarPorId(id);
    }

    @Override
    public void deletar(Long id) {
        resolverService().deletar(id);
    }
}
