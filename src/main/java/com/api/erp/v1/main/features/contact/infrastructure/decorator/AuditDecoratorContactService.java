package com.api.erp.v1.main.features.contact.infrastructure.decorator;

import com.api.erp.v1.main.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.features.contact.domain.service.IContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Decorator para adicionar auditoria ao serviço de Contact
 * <p>
 * Logs todas as operações de contact para fins de rastreabilidade.
 * Útil para compliance e investigação de problemas.
 * <p>
 * Exemplo de composição:
 * ContactService → AuditDecorator → ValidationDecorator
 */
@Slf4j
@RequiredArgsConstructor
public class AuditDecoratorContactService implements IContactService {

    private final IContactService service;


    @Override
    public Contact criar(CreateContactRequest request) {
        log.info("[AUDIT] Criando novo contact: tipo={}, valor={}, principal={}",
                request.tipo(), request.valor(), request.principal());

        try {
            Contact contact = service.criar(request);
            log.info("[AUDIT] Contact criado com sucesso: id={}, tipo={}",
                    contact.getId(), contact.getTipo());
            return contact;
        } catch (Exception e) {
            log.error("[AUDIT] Erro ao criar contact: tipo={}, valor={}, erro={}",
                    request.tipo(), request.valor(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Contact buscarPorId(Long id) {
        log.debug("[AUDIT] Buscando contact por ID: id={}", id);

        try {
            Contact contact = service.buscarPorId(id);
            log.debug("[AUDIT] Contact encontrado: id={}, tipo={}", id, contact.getTipo());
            return contact;
        } catch (Exception e) {
            log.warn("[AUDIT] Contact not found: id={}", id);
            throw e;
        }
    }

    @Override
    public List<Contact> buscarTodos() {
        log.debug("[AUDIT] Listando todos os contacts");
        List<Contact> response = service.buscarTodos();
        log.debug("[AUDIT] Total de contacts encontrados: {}", response.size());
        return response;
    }

    @Override
    public List<Contact> buscarAtivos() {
        log.debug("[AUDIT] Listando contacts ativos");
        List<Contact> response = service.buscarAtivos();
        log.debug("[AUDIT] Total de contacts ativos encontrados: {}", response.size());
        return response;
    }

    @Override
    public List<Contact> buscarInativos() {
        log.debug("[AUDIT] Listando contacts inativos");
        List<Contact> response = service.buscarInativos();
        log.debug("[AUDIT] Total de contacts inativos encontrados: {}", response.size());
        return response;
    }

    @Override
    public List<Contact> buscarPorTipo(String tipo) {
        log.debug("[AUDIT] Buscando contacts por tipo: tipo={}", tipo);
        List<Contact> response = service.buscarPorTipo(tipo);
        log.debug("[AUDIT] Contacts encontrados para tipo {}: {}", tipo, response.size());
        return response;
    }

    @Override
    public Contact buscarPrincipal() {
        log.debug("[AUDIT] Buscando contact principal");

        try {
            Contact contact = service.buscarPrincipal();
            log.debug("[AUDIT] Contact principal encontrado: id={}", contact.getId());
            return contact;
        } catch (Exception e) {
            log.warn("[AUDIT] Nenhum contact principal encontrado");
            throw e;
        }
    }

    @Override
    public Contact atualizar(Long id, CreateContactRequest request) {
        log.info("[AUDIT] Atualizando contact: id={}, tipo={}, valor={}",
                id, request.tipo(), request.valor());

        try {
            Contact contact = service.atualizar(id, request);
            log.info("[AUDIT] Contact atualizado com sucesso: id={}", id);
            return contact;
        } catch (Exception e) {
            log.error("[AUDIT] Erro ao atualizar contact: id={}, erro={}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public Contact ativar(Long id) {
        log.info("[AUDIT] Ativando contact: id={}", id);

        try {
            Contact contact = service.ativar(id);
            log.info("[AUDIT] Contact ativado com sucesso: id={}", id);
            return contact;
        } catch (Exception e) {
            log.error("[AUDIT] Erro ao ativar contact: id={}, erro={}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public Contact desativar(Long id) {
        log.info("[AUDIT] Desativando contact: id={}", id);

        try {
            Contact contact = service.desativar(id);
            log.info("[AUDIT] Contact desativado com sucesso: id={}", id);
            return contact;
        } catch (Exception e) {
            log.error("[AUDIT] Erro ao desativar contact: id={}, erro={}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deletar(Long id) {
        log.info("[AUDIT] Deletando contact: id={}", id);

        try {
            service.deletar(id);
            log.info("[AUDIT] Contact deletado com sucesso: id={}", id);
        } catch (Exception e) {
            log.error("[AUDIT] Erro ao deletar contact: id={}, erro={}", id, e.getMessage());
            throw e;
        }
    }
}
