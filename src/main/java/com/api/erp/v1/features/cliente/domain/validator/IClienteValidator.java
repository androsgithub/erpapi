package com.api.erp.v1.features.cliente.domain.validator;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.shared.domain.enums.TenantCode;
import com.api.erp.v1.shared.domain.enums.TenantType;
import org.springframework.data.domain.Pageable;

public interface IClienteValidator {
    void validarCriacao(CreateClienteDto dto);
    void validarAtualizacao(Long id, CreateClienteDto dto);

    void validarPageable(Pageable pageable);
    void validarDTO(CreateClienteDto clienteDto);
    void validarId(Long id);
}
