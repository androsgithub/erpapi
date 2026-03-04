package com.api.erp.v1.main.features.businesspartner.tenants.hece.validator;

import com.api.erp.v1.main.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.features.businesspartner.domain.validator.IBusinessPartnerValidator;
import com.api.erp.v1.main.features.businesspartner.infrastructure.validator.BusinessPartnerValidatorDefault;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component("businesspartnerValidatorHECE")
public class BusinessPartnerValidatorHece implements IBusinessPartnerValidator {

    private final BusinessPartnerValidatorDefault defaultValidator;

    @Autowired
    public BusinessPartnerValidatorHece(BusinessPartnerValidatorDefault defaultValidator) {
        this.defaultValidator = defaultValidator;
    }

    @Override
    public void validarCriacao(CreateBusinessPartnerDto dto) {
        // 1. Validações padrão
        defaultValidator.validarCriacao(dto);

//        String emailInterno = dto.customData().getOptional("email_interno", String.class).orElseThrow(() -> new BusinessException("Deve ser informado o e-mail interno"));

        // 2. Validações específicas Empresa A
//        validarEmailInterno(emailInterno);
    }

    @Override
    public void validarAtualizacao(Long id, CreateBusinessPartnerDto dto) {
        defaultValidator.validarAtualizacao(id, dto);
//        String emailInterno = dto.customData().getOptional("email_interno", String.class).orElseThrow(() -> new BusinessException("Deve ser informado o e-mail interno"));
//        validarEmailInterno(emailInterno);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        defaultValidator.validarPageable(pageable);
    }

    @Override
    public void validarDTO(CreateBusinessPartnerDto businesspartnerDto) {
        defaultValidator.validarDTO(businesspartnerDto);
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

        // Validation extra: domínio corporativo
        if (!emailInterno.endsWith("@hece.com")) {
            throw new BusinessException(
                    "[HECE] Email deve ser do domínio @hece.com");
        }
    }
}
