package com.api.erp.v1.features.permissao.presentation.controller;

import com.api.erp.v1.features.permissao.application.dto.request.CreatePermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.response.PermissaoResponse;
import com.api.erp.v1.features.permissao.domain.entity.PermissaoPermissions;
import com.api.erp.v1.features.permissao.domain.service.GerenciamentoPermissaoService;
import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissoes")
@RequiredArgsConstructor
public class PermissaoController {

    private final GerenciamentoPermissaoService gerenciamentoPermissaoService;

    @PostMapping
    @RequiresPermission(PermissaoPermissions.CRIAR)
    public ResponseEntity<PermissaoResponse> createPermissao(@RequestBody CreatePermissaoRequest request) {
        PermissaoResponse response = gerenciamentoPermissaoService.createPermissao(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @RequiresPermission(PermissaoPermissions.VISUALIZAR)
    public ResponseEntity<List<PermissaoResponse>> getAllPermissoes() {
        List<PermissaoResponse> response = gerenciamentoPermissaoService.getAllPermissoes();
        return ResponseEntity.ok(response);
    }
}
