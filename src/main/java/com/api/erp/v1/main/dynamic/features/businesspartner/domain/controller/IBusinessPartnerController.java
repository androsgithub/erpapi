package com.api.erp.v1.main.dynamic.features.businesspartner.domain.controller;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.response.BusinessPartnerCompleteResponseDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.response.BusinessPartnerSimpleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

public interface IBusinessPartnerController {

    Page<BusinessPartnerSimpleResponseDto> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nome") String sortBy
    );

    BusinessPartnerCompleteResponseDto pegar(
            Long id
    );

    void deletar(
            Long id
    );

    BusinessPartnerCompleteResponseDto criar(
            CreateBusinessPartnerDto dto
    );

    BusinessPartnerCompleteResponseDto atualizar(
            @RequestParam Long id,
            CreateBusinessPartnerDto dto
    );
}
