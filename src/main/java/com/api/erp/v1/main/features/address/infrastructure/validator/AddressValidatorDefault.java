package com.api.erp.v1.main.features.address.infrastructure.validator;

import com.api.erp.v1.main.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.features.address.domain.entity.AddressTipo;
import com.api.erp.v1.main.features.address.domain.validator.IAddressValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class AddressValidatorDefault implements IAddressValidator {

    @Override
    public void validarCriacao(CreateAddressRequest dto) {
        validarDTO(dto);
    }

    @Override
    public void validarAtualizacao(Long id, CreateAddressRequest dto) {
        validarId(id);
        validarDTO(dto);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        if (pageable == null) {
            throw new BusinessException("Pagination is required");
        }
        if (pageable.getPageSize() <= 0) {
            throw new BusinessException("Page size must be greater than zero");
        }
    }

    @Override
    public void validarDTO(CreateAddressRequest dto) {
        if (dto == null) {
            throw new BusinessException("Address DTO cannot be null");
        }
        validarRua(dto.rua());
        validarNumero(dto.numero());
        validarBairro(dto.bairro());
        validarCidade(dto.cidade());
        validarEstado(dto.estado());
        validarCep(dto.cep());
        validarTipo(dto.tipo());
        validarPrincipal(dto.principal());
    }

    @Override
    public void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Address ID is required and must be greater than zero");
        }
    }

    private void validarRua(String rua) {
        if (rua == null || rua.isBlank()) {
            throw new BusinessException("Street is required");
        }
        if (rua.length() < 3) {
            throw new BusinessException("Street must have at least 3 characters");
        }
        if (rua.length() > 255) {
            throw new BusinessException("Street cannot exceed 255 characters");
        }
    }

    private void validarNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new BusinessException("Number is required");
        }
    }

    private void validarBairro(String bairro) {
        if (bairro == null || bairro.isBlank()) {
            throw new BusinessException("Neighborhood is required");
        }
        if (bairro.length() < 2) {
            throw new BusinessException("Neighborhood must have at least 2 characters");
        }
    }

    private void validarCidade(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            throw new BusinessException("City is required");
        }
        if (cidade.length() < 2) {
            throw new BusinessException("City must have at least 2 characters");
        }
    }

    private void validarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new BusinessException("State is required");
        }
        if (estado.length() != 2) {
            throw new BusinessException("State must have exactly 2 characters (UF)");
        }
    }

    private void validarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new BusinessException("ZIP code is required");
        }
        if (!cep.matches("\\d{5}-?\\d{3}")) {
            throw new BusinessException("Invalid ZIP code format. Use: 12345-678 or 12345678");
        }
    }

    private void validarTipo(AddressTipo tipo) {
        if (tipo == null) {
            throw new BusinessException("Address type is required");
        }
    }

    private void validarPrincipal(Boolean principal) {
        if (principal == null) {
            throw new BusinessException("'Main' field is required");
        }
    }
}
