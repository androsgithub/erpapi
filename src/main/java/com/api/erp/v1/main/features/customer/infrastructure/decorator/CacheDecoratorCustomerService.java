package com.api.erp.v1.main.features.customer.infrastructure.decorator;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.entity.Customer;
import com.api.erp.v1.main.features.customer.domain.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Decorator para adicionar cache ao serviço de Customer
 * 
 * Implementa cache simples para operações de leitura (buscarPorId).
 * O cache é invalidado automaticamente em operações de escrita.
 * 
 * Composição:
 * CustomerService → CacheDecorator → AuditDecorator
 * 
 * Thread-Safe: Sim, usa ConcurrentHashMap e sincronização apropriada
 * Performance: Reduz chamadas ao banco para leituras repetidas
 * 
 * Nota: Cache em memória pode crescer - em produção, considere
 * usar @Cacheable do Spring ou Redis para caches distribuídos
 */
@Slf4j
@RequiredArgsConstructor
public class CacheDecoratorCustomerService implements ICustomerService {

    private final ICustomerService service;
    private final ConcurrentHashMap<Long, Customer> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TIMEOUT_MS = 5 * 60 * 1000L; // 5 minutos
    private final ConcurrentHashMap<Long, Long> cacheTimestamps = new ConcurrentHashMap<>();

    @Override
    public Page<Customer> pegarTodos(Pageable pageable) {
        // Paginação não é cacheada pois pode variar
        return service.pegarTodos(pageable);
    }

    @Override
    public Customer criar(CreateCustomerDto customerDto) {
        Customer customer = service.criar(customerDto);
        // Invalida cache após criação
        cache.clear();
        cacheTimestamps.clear();
        log.debug("[CACHE CUSTOMER] Cache invalidated after creating new customer");
        return customer;
    }

    @Override
    public Customer atualizar(Long id, CreateCustomerDto customerDto) {
        Customer customer = service.atualizar(id, customerDto);
        // Remove do cache o customer atualizado
        cache.remove(id);
        cacheTimestamps.remove(id);
        log.debug("[CACHE CUSTOMER] Cache invalidado para customer: id={}", id);
        return customer;
    }

    @Override
    public Customer pegarPorId(Long id) {
        // Verifica se está em cache e se não expirou
        Customer cached = cache.get(id);
        if (cached != null && !isCacheExpired(id)) {
            log.debug("[CACHE CUSTOMER] Cache hit para customer: id={}", id);
            return cached;
        }

        log.debug("[CACHE CUSTOMER] Cache miss para customer: id={}", id);
        Customer customer = service.pegarPorId(id);
        cache.put(id, customer);
        cacheTimestamps.put(id, System.currentTimeMillis());
        return customer;
    }

    @Override
    public void deletar(Long id) {
        service.deletar(id);
        // Remove do cache após delete
        cache.remove(id);
        cacheTimestamps.remove(id);
        log.debug("[CACHE CUSTOMER] Cache invalidated after deleting customer: id={}", id);
    }

    private boolean isCacheExpired(Long id) {
        Long timestamp = cacheTimestamps.get(id);
        if (timestamp == null) {
            return true;
        }
        return System.currentTimeMillis() - timestamp > CACHE_TIMEOUT_MS;
    }
}
