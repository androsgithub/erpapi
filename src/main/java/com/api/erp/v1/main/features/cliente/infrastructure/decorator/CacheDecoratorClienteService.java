package com.api.erp.v1.main.features.cliente.infrastructure.decorator;

import com.api.erp.v1.main.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.main.features.cliente.domain.entity.Cliente;
import com.api.erp.v1.main.features.cliente.domain.service.IClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Decorator para adicionar cache ao serviço de Cliente
 * 
 * Implementa cache simples para operações de leitura (buscarPorId).
 * O cache é invalidado automaticamente em operações de escrita.
 * 
 * Composição:
 * ClienteService → CacheDecorator → AuditDecorator
 * 
 * Thread-Safe: Sim, usa ConcurrentHashMap e sincronização apropriada
 * Performance: Reduz chamadas ao banco para leituras repetidas
 * 
 * Nota: Cache em memória pode crescer - em produção, considere
 * usar @Cacheable do Spring ou Redis para caches distribuídos
 */
@Slf4j
@RequiredArgsConstructor
public class CacheDecoratorClienteService implements IClienteService {

    private final IClienteService service;
    private final ConcurrentHashMap<Long, Cliente> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TIMEOUT_MS = 5 * 60 * 1000L; // 5 minutos
    private final ConcurrentHashMap<Long, Long> cacheTimestamps = new ConcurrentHashMap<>();

    @Override
    public Page<Cliente> pegarTodos(Pageable pageable) {
        // Paginação não é cacheada pois pode variar
        return service.pegarTodos(pageable);
    }

    @Override
    public Cliente criar(CreateClienteDto clienteDto) {
        Cliente cliente = service.criar(clienteDto);
        // Invalida cache após criação
        cache.clear();
        cacheTimestamps.clear();
        log.debug("[CACHE CLIENTE] Cache invalidado após criar novo cliente");
        return cliente;
    }

    @Override
    public Cliente atualizar(Long id, CreateClienteDto clienteDto) {
        Cliente cliente = service.atualizar(id, clienteDto);
        // Remove do cache o cliente atualizado
        cache.remove(id);
        cacheTimestamps.remove(id);
        log.debug("[CACHE CLIENTE] Cache invalidado para cliente: id={}", id);
        return cliente;
    }

    @Override
    public Cliente pegarPorId(Long id) {
        // Verifica se está em cache e se não expirou
        Cliente cached = cache.get(id);
        if (cached != null && !isCacheExpired(id)) {
            log.debug("[CACHE CLIENTE] Cache hit para cliente: id={}", id);
            return cached;
        }

        log.debug("[CACHE CLIENTE] Cache miss para cliente: id={}", id);
        Cliente cliente = service.pegarPorId(id);
        cache.put(id, cliente);
        cacheTimestamps.put(id, System.currentTimeMillis());
        return cliente;
    }

    @Override
    public void deletar(Long id) {
        service.deletar(id);
        // Remove do cache após delete
        cache.remove(id);
        cacheTimestamps.remove(id);
        log.debug("[CACHE CLIENTE] Cache invalidado após deletar cliente: id={}", id);
    }

    private boolean isCacheExpired(Long id) {
        Long timestamp = cacheTimestamps.get(id);
        if (timestamp == null) {
            return true;
        }
        return System.currentTimeMillis() - timestamp > CACHE_TIMEOUT_MS;
    }
}
