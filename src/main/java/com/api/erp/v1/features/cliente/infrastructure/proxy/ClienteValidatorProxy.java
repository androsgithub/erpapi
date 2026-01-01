package com.api.erp.v1.features.cliente.infrastructure.proxy;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.shared.domain.enums.TenantCode;
import com.api.erp.v1.shared.domain.enums.TenantType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ClienteValidatorProxy implements IClienteValidator {

    private final ClienteValidatorHolder holder;


    @Override
    public void validarCriacao(CreateClienteDto dto) {
        holder.getValidator().validarCriacao(dto);
    }

    @Override
    public void validarAtualizacao(Long id, CreateClienteDto dto) {
        holder.getValidator().validarAtualizacao(id, dto);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        holder.getValidator().validarPageable(pageable);
    }

    @Override
    public void validarDTO(CreateClienteDto clienteDto) {
        holder.getValidator().validarDTO(clienteDto);
    }

    @Override
    public void validarId(Long id) {
        holder.getValidator().validarId(id);
    }

    @Override
    public TenantCode getTenantCode() {
        return holder.getValidator().getTenantCode();
    }

    @Override
    public TenantType getTenantType() {
        return holder.getValidator().getTenantType();
    }

    @Override
    public int getPriority() {
        return holder.getValidator().getPriority();
    }
}
