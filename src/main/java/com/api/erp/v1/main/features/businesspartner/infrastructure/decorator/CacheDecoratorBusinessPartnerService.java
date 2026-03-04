package com.api.erp.v1.main.features.businesspartner.infrastructure.decorator;

import com.api.erp.v1.main.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.features.businesspartner.domain.entity.BusinessPartner;
import com.api.erp.v1.main.features.businesspartner.domain.service.IBusinessPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Decorator para adicionar cache ao serviço de BusinessPartner
 * 
 * Implementa cache simples para operações de leitura (buscarPorId).
 * O cache é invalidado automaticamente em operações de escrita.
 * 
 * Composição:
 * BusinessPartnerService → CacheDecorator → AuditDecorator
 * 
 * Thread-Safe: Sim, usa ConcurrentHashMap e sincronização apropriada
 * Performance: Reduz chamadas ao banco para leituras repetidas
 * 
 * Nota: Cache em memória pode crescer - em produção, considere
 * usar @Cacheable do Spring ou Redis para caches distribuídos
 */
@Slf4j
@RequiredArgsConstructor
public class CacheDecoratorBusinessPartnerService implements IBusinessPartnerService {

    private final IBusinessPartnerService service;
    private final ConcurrentHashMap<Long, BusinessPartner> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TIMEOUT_MS = 5 * 60 * 1000L; // 5 minutos
    private final ConcurrentHashMap<Long, Long> cacheTimestamps = new ConcurrentHashMap<>();

    @Override
    public Page<BusinessPartner> pegarTodos(Pageable pageable) {
        // Paginação não é cacheada pois pode variar
        return service.pegarTodos(pageable);
    }

    @Override
    public BusinessPartner criar(CreateBusinessPartnerDto businesspartnerDto) {
        BusinessPartner businesspartner = service.criar(businesspartnerDto);
        // Invalida cache após criação
        cache.clear();
        cacheTimestamps.clear();
        log.debug("[CACHE BUSINESSPARTNER] Cache invalidated after creating new businesspartner");
        return businesspartner;
    }

    @Override
    public BusinessPartner atualizar(Long id, CreateBusinessPartnerDto businesspartnerDto) {
        BusinessPartner businesspartner = service.atualizar(id, businesspartnerDto);
        // Remove do cache o businesspartner atualizado
        cache.remove(id);
        cacheTimestamps.remove(id);
        log.debug("[CACHE BUSINESSPARTNER] Cache invalidado para businesspartner: id={}", id);
        return businesspartner;
    }

    @Override
    public BusinessPartner pegarPorId(Long id) {
        // Verifica se está em cache e se não expirou
        BusinessPartner cached = cache.get(id);
        if (cached != null && !isCacheExpired(id)) {
            log.debug("[CACHE BUSINESSPARTNER] Cache hit para businesspartner: id={}", id);
            return cached;
        }

        log.debug("[CACHE BUSINESSPARTNER] Cache miss para businesspartner: id={}", id);
        BusinessPartner businesspartner = service.pegarPorId(id);
        cache.put(id, businesspartner);
        cacheTimestamps.put(id, System.currentTimeMillis());
        return businesspartner;
    }

    @Override
    public void deletar(Long id) {
        service.deletar(id);
        // Remove do cache após delete
        cache.remove(id);
        cacheTimestamps.remove(id);
        log.debug("[CACHE BUSINESSPARTNER] Cache invalidated after deleting businesspartner: id={}", id);
    }

    private boolean isCacheExpired(Long id) {
        Long timestamp = cacheTimestamps.get(id);
        if (timestamp == null) {
            return true;
        }
        return System.currentTimeMillis() - timestamp > CACHE_TIMEOUT_MS;
    }
}
