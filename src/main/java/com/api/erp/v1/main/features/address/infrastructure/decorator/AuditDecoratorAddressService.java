package com.api.erp.v1.main.features.address.infrastructure.decorator;

import com.api.erp.v1.main.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.features.address.domain.entity.Address;
import com.api.erp.v1.main.features.address.domain.service.IAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Decorator para adicionar auditoria ao serviço de Address
 * 
 * Logs todas as operações de endereço para fins de rastreabilidade.
 * Útil para compliance e investigação de problemas.
 * 
 * Exemplo de composição:
 * AddressService → AuditDecorator → ValidationDecorator
 */
@Slf4j
@RequiredArgsConstructor
public class AuditDecoratorAddressService implements IAddressService {

    private final IAddressService service;

    @Override
    public Address criar(CreateAddressRequest request) {
        log.info("[AUDIT ADDRESS] Creating new address: street={}, number={}, zip_code={}",
                request.rua(), request.numero(), request.cep());

        try {
            Address address = service.criar(request);
            log.info("[AUDIT ADDRESS] Address created successfully: id={}, street={}, number={}",
                    address.getId(), address.getRua(), address.getNumero());
            return address;
        } catch (Exception e) {
            log.error("[AUDIT ADDRESS] Error creating address: street={}, number={}, error={}",
                    request.rua(), request.numero(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Address buscarPorId(Long id) {
        log.debug("[AUDIT ADDRESS] Getting address by ID: id={}", id);

        try {
            Address address = service.buscarPorId(id);
            log.debug("[AUDIT ADDRESS] Address found: id={}, street={}", id, address.getRua());
            return address;
        } catch (Exception e) {
            log.warn("[AUDIT ADDRESS] Address not found: id={}", id);
            throw e;
        }
    }

    @Override
    public List<Address> buscarTodos() {
        log.debug("[AUDIT ADDRESS] Listing all addresses");
        List<Address> response = service.buscarTodos();
        log.debug("[AUDIT ADDRESS] Total addresses found: {}", response.size());
        return response;
    }

    @Override
    public Address atualizar(Long id, CreateAddressRequest request) {
        log.info("[AUDIT ADDRESS] Updating address: id={}, street={}, number={}",
                id, request.rua(), request.numero());

        try {
            Address address = service.atualizar(id, request);
            log.info("[AUDIT ADDRESS] Address updated successfully: id={}, street={}",
                    id, address.getRua());
            return address;
        } catch (Exception e) {
            log.error("[AUDIT ADDRESS] Error updating address: id={}, error={}",
                    id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deletar(Long id) {
        log.info("[AUDIT ADDRESS] Deleting address: id={}", id);

        try {
            service.deletar(id);
            log.info("[AUDIT ADDRESS] Address deleted successfully: id={}", id);
        } catch (Exception e) {
            log.error("[AUDIT ADDRESS] Error deleting address: id={}, error={}",
                    id, e.getMessage());
            throw e;
        }
    }
}
