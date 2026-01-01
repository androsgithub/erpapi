package com.api.erp.v1.features.cliente.tenants.hece.validator;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.features.cliente.infrastructure.validator.ClienteValidatorDefault;
import com.api.erp.v1.shared.domain.enums.TenantCode;
import com.api.erp.v1.shared.domain.enums.TenantType;
import com.api.erp.v1.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteValidatorHece implements IClienteValidator {

    private final ClienteValidatorDefault defaultValidator;

    @Override
    public void validarCriacao(CreateClienteDto dto) {
        // 1. Validações padrão
        defaultValidator.validarCriacao(dto);

        String email_interno = dto.customData().getOptional("email_interno", String.class).orElseThrow(() -> new BusinessException("Deve ser informado o e-mail interno"));

        // 2. Validações específicas Empresa A
        validarEmailInterno(email_interno);
    }

    @Override
    public void validarAtualizacao(Long id, CreateClienteDto dto) {
        defaultValidator.validarAtualizacao(id, dto);
        String email_interno = dto.customData().getOptional("email_interno", String.class).orElseThrow(() -> new BusinessException("Deve ser informado o e-mail interno"));
        validarEmailInterno(email_interno);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        defaultValidator.validarPageable(pageable);
    }

    @Override
    public void validarDTO(CreateClienteDto clienteDto) {
        defaultValidator.validarDTO(clienteDto);
    }

    @Override
    public void validarId(Long id) {
        defaultValidator.validarId(id);
    }

    private void validarEmailInterno(String emailInterno) {
        if (emailInterno == null || emailInterno.isBlank()) {
            throw new BusinessException(
                    "[HECE] Email interno é obrigatório");
        }

        if (!emailInterno.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException(
                    "[HECE] Email interno inválido");
        }

        // Validação extra: domínio corporativo
        if (!emailInterno.endsWith("@hece.com")) {
            throw new BusinessException(
                    "[HECE] Email deve ser do domínio @hece.com");
        }
    }

    @Override
    public TenantCode getTenantCode() {
        return TenantCode.HECE;
    }

    @Override
    public TenantType getTenantType() {
        return TenantType.HECE;
    }

    @Override
    public int getPriority() {
        return 10; // Alta prioridade
    }
}
