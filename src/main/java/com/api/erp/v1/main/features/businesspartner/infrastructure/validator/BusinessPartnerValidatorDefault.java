package com.api.erp.v1.main.features.businesspartner.infrastructure.validator;

import com.api.erp.v1.main.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.features.businesspartner.domain.validator.IBusinessPartnerValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class BusinessPartnerValidatorDefault implements IBusinessPartnerValidator {

    @Override
    public void validarCriacao(CreateBusinessPartnerDto dto) {
        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new IllegalArgumentException("BusinessPartner name is required");
        }

        if (dto.nome().length() < 3) {
            throw new IllegalArgumentException("Name must have at least 3 characters");
        }
    }

    @Override
    public void validarAtualizacao(Long id, CreateBusinessPartnerDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("BusinessPartner ID is required");
        }
        validarCriacao(dto);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        if (pageable == null) {
            throw new BusinessException("Pagination configuration for businesspartner not found");
        }
    }

    @Override
    public void validarDTO(CreateBusinessPartnerDto businesspartnerDto) {
        if (businesspartnerDto == null) {
            throw new BusinessException("CreateBusinessPartnerDto cannot be null");
        }
        if (businesspartnerDto.nome() == null || businesspartnerDto.nome().isBlank()) {
            throw new BusinessException("BusinessPartner name is required");
        }
        if (businesspartnerDto.dadosFiscais() == null) {
            throw new BusinessException("Tax data is required");
        }

        // Validações de dados fiscais
        var df = businesspartnerDto.dadosFiscais();
        if (df.cnpj() == null || df.cnpj().isBlank()) {
            throw new BusinessException("CNPJ is required");
        }
        if (df.razaoSocial() == null || df.razaoSocial().isBlank()) {
            throw new BusinessException("Company name is required");
        }
    }

    @Override
    public void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Invalid businesspartner ID");
        }
    }
}
