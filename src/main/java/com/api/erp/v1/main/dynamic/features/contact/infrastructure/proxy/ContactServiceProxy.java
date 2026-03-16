package com.api.erp.v1.main.dynamic.features.contact.infrastructure.proxy;

import com.api.erp.v1.main.dynamic.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.dynamic.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.dynamic.features.contact.domain.service.IContactService;
import com.api.erp.v1.main.dynamic.features.contact.infrastructure.service.ContactService;
import com.api.erp.v1.main.master.tenant.infrastructure.service.TenantBeanResolver;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * ContactServiceProxy
 * <p>
 * Proxy que resolve qual ContactService usar baseado em tb_tnt_features.
 * Feature key: "contactService"
 * <p>
 * Fluxo:
 * 1. Obtém o tenant atual via SecurityService
 * 2. Resolve o bean via TenantBeanResolver (consulta tb_tnt_features)
 * 3. Delega para o serviço resolvido
 */
@Service
@Slf4j
public class ContactServiceProxy implements IContactService {

    static final String FEATURE_KEY = "contactService";

    private final ContactService contactServiceDefault;
    private final SecurityService securityService;
    private final TenantBeanResolver tenantBeanResolver;

    public ContactServiceProxy(
            ContactService contactServiceDefault,
            SecurityService securityService,
            TenantBeanResolver tenantBeanResolver) {
        this.contactServiceDefault = contactServiceDefault;
        this.securityService = securityService;
        this.tenantBeanResolver = tenantBeanResolver;
    }

    /**
     * Resolve o ContactService para o tenant atual
     * <p>
     * Se not authenticated, retorna o serviço padrão.
     * Se autenticado, consulta tb_tnt_features com TenantBeanResolver.
     */
    private IContactService resolverService() {
        String strTenantId = securityService.getAuthTenantId();

        // Se não tem autenticação, usa serviço padrão
        if (strTenantId == null || strTenantId.isEmpty()) {
            log.debug("[ContactServiceProxy] No authentication, using default service");
            return contactServiceDefault;
        }

        Long tenantId = Long.valueOf(strTenantId);

        try {
            // Resolve via TenantBeanResolver (consulta tb_tnt_features)
            IContactService service = tenantBeanResolver.resolve(tenantId, FEATURE_KEY, IContactService.class);
            log.debug("[ContactServiceProxy] Service resolvido para tenant {}: {} (feature key={})",
                    tenantId, service.getClass().getSimpleName(), FEATURE_KEY);
            return service;
        } catch (TenantBeanResolver.TenantFeatureNotFoundException |
                 TenantBeanResolver.TenantBeanNotFoundException e) {
            log.warn("[ContactServiceProxy] Falha ao resolver bean via TenantBeanResolver, usando default: {}",
                    e.getMessage());
            return contactServiceDefault;
        }
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
    public Set<Contact> buscarTodos() {
        return resolverService().buscarTodos();
    }

    @Override
    public Set<Contact> buscarAtivos() {
        return resolverService().buscarAtivos();
    }

    @Override
    public Set<Contact> buscarInativos() {
        return resolverService().buscarInativos();
    }

    @Override
    public Set<Contact> buscarPorTipo(String tipo) {
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
