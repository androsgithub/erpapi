package com.api.erp.v1.main.features.contact.infrastructure.decorator;

import com.api.erp.v1.main.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.features.contact.domain.service.IContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Decorator para adicionar cache ao serviço de Contact
 * 
 * Melhora performance cachando resultados de leitura.
 * Invalida cache automaticamente em operações de escrita.
 * 
 * Exemplo de composição com Spring Cache:
 * ContactService → CacheDecorator → AuditDecorator
 * 
 * Nota: Requer configuração de cache no Spring (ex: @EnableCaching)
 */
@Slf4j
public class CacheDecoratorContactService implements IContactService {

    private final IContactService service;

    public CacheDecoratorContactService(IContactService service) {
        this.service = service;
    }

    @Override
    @CacheEvict(value = "contacts", allEntries = true)
    public Contact criar(CreateContactRequest request) {
        log.debug("[CACHE] Cache invalidado - novo contact criado");
        return service.criar(request);
    }

    @Override
    @Cacheable(value = "contactPorId", key = "#id")
    public Contact buscarPorId(Long id) {
        log.debug("[CACHE] Buscando contact do banco de dados (sem cache): id={}", id);
        return service.buscarPorId(id);
    }

    @Override
    @Cacheable(value = "contacts")
    public List<Contact> buscarTodos() {
        log.debug("[CACHE] Buscando todos contacts do banco de dados (sem cache)");
        return service.buscarTodos();
    }

    @Override
    @Cacheable(value = "contactsAtivos")
    public List<Contact> buscarAtivos() {
        log.debug("[CACHE] Buscando contacts ativos do banco de dados (sem cache)");
        return service.buscarAtivos();
    }

    @Override
    @Cacheable(value = "contactsInativos")
    public List<Contact> buscarInativos() {
        log.debug("[CACHE] Buscando contacts inativos do banco de dados (sem cache)");
        return service.buscarInativos();
    }

    @Override
    @Cacheable(value = "contactsPorTipo", key = "#tipo")
    public List<Contact> buscarPorTipo(String tipo) {
        log.debug("[CACHE] Buscando contacts por tipo do banco de dados (sem cache): tipo={}", tipo);
        return service.buscarPorTipo(tipo);
    }

    @Override
    @Cacheable(value = "contactPrincipal")
    public Contact buscarPrincipal() {
        log.debug("[CACHE] Buscando contact principal do banco de dados (sem cache)");
        return service.buscarPrincipal();
    }

    @Override
    @CacheEvict(value = {"contacts", "contactAtivos", "contactsInativos", "contactsPorTipo", "contactPrincipal"}, allEntries = true)
    public Contact atualizar(Long id, CreateContactRequest request) {
        log.debug("[CACHE] Cache invalidado - contact atualizado: id={}", id);
        return service.atualizar(id, request);
    }

    @Override
    @CacheEvict(value = {"contacts", "contactAtivos", "contactsPorTipo", "contactPrincipal"}, allEntries = true)
    public Contact ativar(Long id) {
        log.debug("[CACHE] Cache invalidado - contact ativado: id={}", id);
        return service.ativar(id);
    }

    @Override
    @CacheEvict(value = {"contacts", "contactAtivos", "contactsInativos", "contactsPorTipo", "contactPrincipal"}, allEntries = true)
    public Contact desativar(Long id) {
        log.debug("[CACHE] Cache invalidado - contact desativado: id={}", id);
        return service.desativar(id);
    }

    @Override
    @CacheEvict(value = {"contacts", "contactAtivos", "contactsInativos", "contactsPorTipo", "contactPrincipal"}, allEntries = true)
    public void deletar(Long id) {
        log.debug("[CACHE] Cache invalidado - contact deletado: id={}", id);
        service.deletar(id);
    }
}
