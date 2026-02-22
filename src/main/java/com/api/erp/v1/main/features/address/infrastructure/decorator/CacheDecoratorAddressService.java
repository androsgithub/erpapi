package com.api.erp.v1.main.features.address.infrastructure.decorator;

import com.api.erp.v1.main.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.features.address.domain.entity.Address;
import com.api.erp.v1.main.features.address.domain.service.IAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Decorator para adicionar cache ao serviço de Address
 * 
 * Melhora performance cachando resultados de leitura.
 * Invalida cache automaticamente em operações de escrita.
 * 
 * Exemplo de composição com Spring Cache:
 * AddressService → CacheDecorator → AuditDecorator
 * 
 * Nota: Requer configuração de cache no Spring (ex: @EnableCaching)
 */
@Slf4j
public class CacheDecoratorAddressService implements IAddressService {

    private final IAddressService service;

    public CacheDecoratorAddressService(IAddressService service) {
        this.service = service;
    }

    @Override
    @CacheEvict(value = "addresss", allEntries = true)
    public Address criar(CreateAddressRequest request) {
        log.debug("[CACHE] Cache invalidated - new address created");
        return service.criar(request);
    }

    @Override
    @Cacheable(value = "addressPorId", key = "#id")
    public Address buscarPorId(Long id) {
        log.debug("[CACHE] Buscando endereço do banco de dados (sem cache): id={}", id);
        return service.buscarPorId(id);
    }

    @Override
    @Cacheable(value = "addresss")
    public List<Address> buscarTodos() {
        log.debug("[CACHE] Buscando todos endereços do banco de dados (sem cache)");
        return service.buscarTodos();
    }

    @Override
    @CacheEvict(value = "addresss", allEntries = true)
    public Address atualizar(Long id, CreateAddressRequest request) {
        log.debug("[CACHE] Cache invalidado - endereço atualizado: id={}", id);
        return service.atualizar(id, request);
    }

    @Override
    @CacheEvict(value = "addresss", allEntries = true)
    public void deletar(Long id) {
        log.debug("[CACHE] Cache invalidado - endereço deletado: id={}", id);
        service.deletar(id);
    }
}
