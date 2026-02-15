package com.api.erp.v1.main.features.contato.infrastructure.decorator;

import com.api.erp.v1.main.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.main.features.contato.domain.entity.Contato;
import com.api.erp.v1.main.features.contato.domain.service.IContatoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Decorator para adicionar cache ao serviço de Contato
 * 
 * Melhora performance cachando resultados de leitura.
 * Invalida cache automaticamente em operações de escrita.
 * 
 * Exemplo de composição com Spring Cache:
 * ContatoService → CacheDecorator → AuditDecorator
 * 
 * Nota: Requer configuração de cache no Spring (ex: @EnableCaching)
 */
@Slf4j
public class CacheDecoratorContatoService implements IContatoService {

    private final IContatoService service;

    public CacheDecoratorContatoService(IContatoService service) {
        this.service = service;
    }

    @Override
    @CacheEvict(value = "contatos", allEntries = true)
    public Contato criar(CreateContatoRequest request) {
        log.debug("[CACHE] Cache invalidado - novo contato criado");
        return service.criar(request);
    }

    @Override
    @Cacheable(value = "contatoPorId", key = "#id")
    public Contato buscarPorId(Long id) {
        log.debug("[CACHE] Buscando contato do banco de dados (sem cache): id={}", id);
        return service.buscarPorId(id);
    }

    @Override
    @Cacheable(value = "contatos")
    public List<Contato> buscarTodos() {
        log.debug("[CACHE] Buscando todos contatos do banco de dados (sem cache)");
        return service.buscarTodos();
    }

    @Override
    @Cacheable(value = "contatosAtivos")
    public List<Contato> buscarAtivos() {
        log.debug("[CACHE] Buscando contatos ativos do banco de dados (sem cache)");
        return service.buscarAtivos();
    }

    @Override
    @Cacheable(value = "contatosInativos")
    public List<Contato> buscarInativos() {
        log.debug("[CACHE] Buscando contatos inativos do banco de dados (sem cache)");
        return service.buscarInativos();
    }

    @Override
    @Cacheable(value = "contatosPorTipo", key = "#tipo")
    public List<Contato> buscarPorTipo(String tipo) {
        log.debug("[CACHE] Buscando contatos por tipo do banco de dados (sem cache): tipo={}", tipo);
        return service.buscarPorTipo(tipo);
    }

    @Override
    @Cacheable(value = "contatoPrincipal")
    public Contato buscarPrincipal() {
        log.debug("[CACHE] Buscando contato principal do banco de dados (sem cache)");
        return service.buscarPrincipal();
    }

    @Override
    @CacheEvict(value = {"contatos", "contatoAtivos", "contatosInativos", "contatosPorTipo", "contatoPrincipal"}, allEntries = true)
    public Contato atualizar(Long id, CreateContatoRequest request) {
        log.debug("[CACHE] Cache invalidado - contato atualizado: id={}", id);
        return service.atualizar(id, request);
    }

    @Override
    @CacheEvict(value = {"contatos", "contatoAtivos", "contatosPorTipo", "contatoPrincipal"}, allEntries = true)
    public Contato ativar(Long id) {
        log.debug("[CACHE] Cache invalidado - contato ativado: id={}", id);
        return service.ativar(id);
    }

    @Override
    @CacheEvict(value = {"contatos", "contatoAtivos", "contatosInativos", "contatosPorTipo", "contatoPrincipal"}, allEntries = true)
    public Contato desativar(Long id) {
        log.debug("[CACHE] Cache invalidado - contato desativado: id={}", id);
        return service.desativar(id);
    }

    @Override
    @CacheEvict(value = {"contatos", "contatoAtivos", "contatosInativos", "contatosPorTipo", "contatoPrincipal"}, allEntries = true)
    public void deletar(Long id) {
        log.debug("[CACHE] Cache invalidado - contato deletado: id={}", id);
        service.deletar(id);
    }
}
