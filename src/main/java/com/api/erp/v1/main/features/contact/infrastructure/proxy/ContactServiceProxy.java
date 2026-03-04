package com.api.erp.v1.main.features.contact.infrastructure.proxy;

import com.api.erp.v1.main.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.features.contact.domain.service.IContactService;
import com.api.erp.v1.main.features.contact.infrastructure.decorator.ContactServiceApplyDecorate;
import com.api.erp.v1.main.features.contact.infrastructure.service.ContactService;
import com.api.erp.v1.main.tenant.domain.entity.configs.ContactConfig;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ContactServiceProxy implements IContactService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ContactService contactServiceDefault;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ITenantService tenantService;

    private IContactService resolverService() {
        IContactService response = contactServiceDefault;
        ContactConfig contactConfig = new ContactConfig();
        try {
            String strTenantId = securityService.getAuthTenantId();
            Long tenantId = Long.valueOf(strTenantId);
            Tenant tenant = tenantService.getDadosTenant(tenantId);
            String tenantType = tenant.getConfig().getInternalTenantConfig().getTenantType().name();
            String beanName = "contactService" + tenantType;

            contactConfig = tenant.getConfig().getContactConfig();

            try {
                IContactService service = applicationContext.getBean(beanName, IContactService.class);
                log.debug("[BUSINESSPARTNER SERVICE] Service resolvido para tenant {}: {}", tenantId, beanName);
                response = service;
            } catch (Exception e) {
                log.debug("[BUSINESSPARTNER SERVICE] Service {} not found, using default", beanName);
            }
        } catch (Exception e) {
            log.debug("[BUSINESSPARTNER SERVICE] Error resolving tenant, using default: {}", e.getMessage());
        }
        return ContactServiceApplyDecorate.aplicarDecorators(response, contactConfig);
    }

    @Override
    public Contact criar(CreateContactRequest request) {
        return resolverService().criar(request);
    }

    @Override
    public Contact buscarPorId(Long id) {
        return resolverService().buscarPorId(id);
    }

    @Override
    public List<Contact> buscarTodos() {
        return resolverService().buscarTodos();
    }

    @Override
    public List<Contact> buscarAtivos() {
        return resolverService().buscarAtivos();
    }

    @Override
    public List<Contact> buscarInativos() {
        return resolverService().buscarInativos();
    }

    @Override
    public List<Contact> buscarPorTipo(String tipo) {
        return resolverService().buscarPorTipo(tipo);
    }

    @Override
    public Contact buscarPrincipal() {
        return resolverService().buscarPrincipal();
    }

    @Override
    public Contact atualizar(Long id, CreateContactRequest request) {
        return resolverService().atualizar(id, request);
    }

    @Override
    public Contact ativar(Long id) {
        return resolverService().ativar(id);
    }

    @Override
    public Contact desativar(Long id) {
        return resolverService().desativar(id);
    }

    @Override
    public void deletar(Long id) {
        resolverService().deletar(id);
    }
}
