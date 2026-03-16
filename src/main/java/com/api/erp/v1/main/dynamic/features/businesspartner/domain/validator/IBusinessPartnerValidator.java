package com.api.erp.v1.main.dynamic.features.businesspartner.domain.validator;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import org.springframework.data.domain.Pageable;

public interface IBusinessPartnerValidator {
    void validarCriacao(CreateBusinessPartnerDto dto);

    void validarAtualizacao(Long id, CreateBusinessPartnerDto dto);

    void validarPageable(Pageable pageable);

    void validarDTO(CreateBusinessPartnerDto businesspartnerDto);

    void validarId(Long id);
}
