package com.api.erp.v1.main.features.cliente.domain.controller;

import com.api.erp.v1.main.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.main.features.cliente.application.dto.response.ClienteCompleteResponseDto;
import com.api.erp.v1.main.features.cliente.application.dto.response.ClienteSimpleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

public interface IClienteController {

    Page<ClienteSimpleResponseDto> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nome") String sortBy
    );

    ClienteCompleteResponseDto pegar(
            Long id
    );

    void deletar(
            Long id
    );

    ClienteCompleteResponseDto criar(
            CreateClienteDto dto
    );

    ClienteCompleteResponseDto atualizar(
            @RequestParam Long id,
            CreateClienteDto dto
    );
}
