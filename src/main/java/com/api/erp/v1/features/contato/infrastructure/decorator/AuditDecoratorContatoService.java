package com.api.erp.v1.features.contato.infrastructure.decorator;

import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.domain.entity.Contato;
import com.api.erp.v1.features.contato.domain.service.IContatoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Decorator para adicionar auditoria ao serviço de Contato
 * <p>
 * Registra todas as operações de contato para fins de rastreabilidade.
 * Útil para compliance e investigação de problemas.
 * <p>
 * Exemplo de composição:
 * ContatoService → AuditDecorator → ValidationDecorator
 */
@Slf4j
@RequiredArgsConstructor
public class AuditDecoratorContatoService implements IContatoService {

    private final IContatoService service;


    @Override
    public Contato criar(CreateContatoRequest request) {
        log.info("[AUDIT] Criando novo contato: tipo={}, valor={}, principal={}",
                request.tipo(), request.valor(), request.principal());

        try {
            Contato contato = service.criar(request);
            log.info("[AUDIT] Contato criado com sucesso: id={}, tipo={}",
                    contato.getId(), contato.getTipo());
            return contato;
        } catch (Exception e) {
            log.error("[AUDIT] Erro ao criar contato: tipo={}, valor={}, erro={}",
                    request.tipo(), request.valor(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Contato buscarPorId(Long id) {
        log.debug("[AUDIT] Buscando contato por ID: id={}", id);

        try {
            Contato contato = service.buscarPorId(id);
            log.debug("[AUDIT] Contato encontrado: id={}, tipo={}", id, contato.getTipo());
            return contato;
        } catch (Exception e) {
            log.warn("[AUDIT] Contato não encontrado: id={}", id);
            throw e;
        }
    }

    @Override
    public List<Contato> buscarTodos() {
        log.debug("[AUDIT] Listando todos os contatos");
        List<Contato> response = service.buscarTodos();
        log.debug("[AUDIT] Total de contatos encontrados: {}", response.size());
        return response;
    }

    @Override
    public List<Contato> buscarAtivos() {
        log.debug("[AUDIT] Listando contatos ativos");
        List<Contato> response = service.buscarAtivos();
        log.debug("[AUDIT] Total de contatos ativos encontrados: {}", response.size());
        return response;
    }

    @Override
    public List<Contato> buscarInativos() {
        log.debug("[AUDIT] Listando contatos inativos");
        List<Contato> response = service.buscarInativos();
        log.debug("[AUDIT] Total de contatos inativos encontrados: {}", response.size());
        return response;
    }

    @Override
    public List<Contato> buscarPorTipo(String tipo) {
        log.debug("[AUDIT] Buscando contatos por tipo: tipo={}", tipo);
        List<Contato> response = service.buscarPorTipo(tipo);
        log.debug("[AUDIT] Contatos encontrados para tipo {}: {}", tipo, response.size());
        return response;
    }

    @Override
    public Contato buscarPrincipal() {
        log.debug("[AUDIT] Buscando contato principal");

        try {
            Contato contato = service.buscarPrincipal();
            log.debug("[AUDIT] Contato principal encontrado: id={}", contato.getId());
            return contato;
        } catch (Exception e) {
            log.warn("[AUDIT] Nenhum contato principal encontrado");
            throw e;
        }
    }

    @Override
    public Contato atualizar(Long id, CreateContatoRequest request) {
        log.info("[AUDIT] Atualizando contato: id={}, tipo={}, valor={}",
                id, request.tipo(), request.valor());

        try {
            Contato contato = service.atualizar(id, request);
            log.info("[AUDIT] Contato atualizado com sucesso: id={}", id);
            return contato;
        } catch (Exception e) {
            log.error("[AUDIT] Erro ao atualizar contato: id={}, erro={}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public Contato ativar(Long id) {
        log.info("[AUDIT] Ativando contato: id={}", id);

        try {
            Contato contato = service.ativar(id);
            log.info("[AUDIT] Contato ativado com sucesso: id={}", id);
            return contato;
        } catch (Exception e) {
            log.error("[AUDIT] Erro ao ativar contato: id={}, erro={}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public Contato desativar(Long id) {
        log.info("[AUDIT] Desativando contato: id={}", id);

        try {
            Contato contato = service.desativar(id);
            log.info("[AUDIT] Contato desativado com sucesso: id={}", id);
            return contato;
        } catch (Exception e) {
            log.error("[AUDIT] Erro ao desativar contato: id={}, erro={}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deletar(Long id) {
        log.info("[AUDIT] Deletando contato: id={}", id);

        try {
            service.deletar(id);
            log.info("[AUDIT] Contato deletado com sucesso: id={}", id);
        } catch (Exception e) {
            log.error("[AUDIT] Erro ao deletar contato: id={}, erro={}", id, e.getMessage());
            throw e;
        }
    }
}
