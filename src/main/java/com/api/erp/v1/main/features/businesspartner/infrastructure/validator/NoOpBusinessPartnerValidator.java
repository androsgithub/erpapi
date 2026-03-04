package com.api.erp.v1.main.features.businesspartner.infrastructure.validator;

import com.api.erp.v1.main.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.features.businesspartner.domain.validator.IBusinessPartnerValidator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class NoOpBusinessPartnerValidator implements IBusinessPartnerValidator {
    @Override
    public void validarCriacao(CreateBusinessPartnerDto dto) {
        // No operation
    }

    @Override
    public void validarAtualizacao(Long id, CreateBusinessPartnerDto dto) {
        // No operation
    }

    @Override
    public void validarPageable(Pageable pageable) {
        // No operation
    }

    @Override
    public void validarDTO(CreateBusinessPartnerDto businesspartnerDto) {
        // No operation
    }

    @Override
    public void validarId(Long id) {
        // No operation
    }
}
