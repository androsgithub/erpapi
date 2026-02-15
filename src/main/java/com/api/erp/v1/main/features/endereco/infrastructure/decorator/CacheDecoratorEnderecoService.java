package com.api.erp.v1.main.features.endereco.infrastructure.decorator;

import com.api.erp.v1.main.features.endereco.application.dto.request.CreateEnderecoRequest;
import com.api.erp.v1.main.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.main.features.endereco.domain.service.IEnderecoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Decorator para adicionar cache ao serviço de Endereco
 * 
 * Melhora performance cachando resultados de leitura.
 * Invalida cache automaticamente em operações de escrita.
 * 
 * Exemplo de composição com Spring Cache:
 * EnderecoService → CacheDecorator → AuditDecorator
 * 
 * Nota: Requer configuração de cache no Spring (ex: @EnableCaching)
 */
@Slf4j
public class CacheDecoratorEnderecoService implements IEnderecoService {

    private final IEnderecoService service;

    public CacheDecoratorEnderecoService(IEnderecoService service) {
        this.service = service;
    }

    @Override
    @CacheEvict(value = "enderecos", allEntries = true)
    public Endereco criar(CreateEnderecoRequest request) {
        log.debug("[CACHE] Cache invalidado - novo endereço criado");
        return service.criar(request);
    }

    @Override
    @Cacheable(value = "enderecoPorId", key = "#id")
    public Endereco buscarPorId(Long id) {
        log.debug("[CACHE] Buscando endereço do banco de dados (sem cache): id={}", id);
        return service.buscarPorId(id);
    }

    @Override
    @Cacheable(value = "enderecos")
    public List<Endereco> buscarTodos() {
        log.debug("[CACHE] Buscando todos endereços do banco de dados (sem cache)");
        return service.buscarTodos();
    }

    @Override
    @CacheEvict(value = "enderecos", allEntries = true)
    public Endereco atualizar(Long id, CreateEnderecoRequest request) {
        log.debug("[CACHE] Cache invalidado - endereço atualizado: id={}", id);
        return service.atualizar(id, request);
    }

    @Override
    @CacheEvict(value = "enderecos", allEntries = true)
    public void deletar(Long id) {
        log.debug("[CACHE] Cache invalidado - endereço deletado: id={}", id);
        service.deletar(id);
    }
}
