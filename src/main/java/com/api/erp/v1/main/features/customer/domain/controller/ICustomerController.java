package com.api.erp.v1.main.features.customer.domain.controller;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.application.dto.response.CustomerCompleteResponseDto;
import com.api.erp.v1.main.features.customer.application.dto.response.CustomerSimpleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

public interface ICustomerController {

    Page<CustomerSimpleResponseDto> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nome") String sortBy
    );

    CustomerCompleteResponseDto pegar(
            Long id
    );

    void deletar(
            Long id
    );

    CustomerCompleteResponseDto criar(
            CreateCustomerDto dto
    );

    CustomerCompleteResponseDto atualizar(
            @RequestParam Long id,
            CreateCustomerDto dto
    );
}
