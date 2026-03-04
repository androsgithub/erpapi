package com.api.erp.v1.main.features.address.infrastructure.decorator;

import com.api.erp.v1.main.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.features.address.domain.service.IAddressService;
import com.api.erp.v1.main.features.address.domain.entity.Address;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;

import java.util.List;

/**
 * Decorator para adicionar validações extras ao serviço de Address
 * 
 * Exemplo de uso do Decorator Pattern para adicionar comportamento
 * sem modificar a classe original.
 * 
 * Pode ser utilizado em conjunto com outros decorators:
 * AddressService base → ValidationDecorator → AuditDecorator → CacheDecorator
 */
public class ValidationDecoratorAddressService implements IAddressService {

    private final IAddressService service;

    public ValidationDecoratorAddressService(IAddressService service) {
        this.service = service;
    }

    @Override
    public Address criar(CreateAddressRequest request) {
        // Validações extras antes de criar
        validarRequest(request);
        return service.criar(request);
    }

    @Override
    public Address buscarPorId(Long id) {
        validarId(id);
        return service.buscarPorId(id);
    }

    @Override
    public List<Address> buscarTodos() {
        return service.buscarTodos();
    }

    @Override
    public Address atualizar(Long id, CreateAddressRequest request) {
        validarId(id);
        validarRequest(request);
        return service.atualizar(id, request);
    }

    @Override
    public void deletar(Long id) {
        validarId(id);
        service.deletar(id);
    }

    /**
     * Valida o request completo
     */
    private void validarRequest(CreateAddressRequest request) {
        if (request == null) {
            throw new BusinessException("Address request cannot be null");
        }

        if (request.rua() == null || request.rua().isBlank()) {
            throw new BusinessException("Street is required");
        }

        if (request.numero() == null || request.numero().isBlank()) {
            throw new BusinessException("Number is required");
        }

        if (request.cep() == null || request.cep().isBlank()) {
            throw new BusinessException("ZIP code is required");
        }

        if (request.cidade() == null || request.cidade().isBlank()) {
            throw new BusinessException("City is required");
        }

        if (request.estado() == null || request.estado().isBlank()) {
            throw new BusinessException("State is required");
        }
    }

    /**
     * Valida o ID
     */
    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Address ID must be a positive number");
        }
    }
}
