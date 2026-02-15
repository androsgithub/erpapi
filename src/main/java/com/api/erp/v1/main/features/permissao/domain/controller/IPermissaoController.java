package com.api.erp.v1.main.features.permissao.domain.controller;

import com.api.erp.v1.main.features.permissao.application.dto.request.CreatePermissaoRequest;
import com.api.erp.v1.main.features.permissao.application.dto.response.PermissaoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IPermissaoController {

    ResponseEntity<PermissaoResponse> createPermissao(
            @RequestBody CreatePermissaoRequest request
    );

    ResponseEntity<List<PermissaoResponse>> getAllPermissoes();
}
