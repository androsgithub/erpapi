package com.api.erp.v1.main.features.permissao.presentation.controller;

import com.api.erp.v1.main.features.permissao.application.dto.request.CreatePermissaoRequest;
import com.api.erp.v1.main.features.permissao.application.dto.response.PermissaoResponse;
import com.api.erp.v1.main.features.permissao.application.mapper.PermissaoMapper;
import com.api.erp.v1.main.features.permissao.domain.controller.IPermissaoController;
import com.api.erp.v1.docs.openapi.features.permissao.PermissaoOpenApiDocumentation;
import com.api.erp.v1.main.features.permissao.domain.entity.PermissaoPermissions;
import com.api.erp.v1.main.features.permissao.domain.service.IGerenciamentoPermissaoService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissoes")
public class PermissaoController implements IPermissaoController, PermissaoOpenApiDocumentation {

    @Autowired
    private IGerenciamentoPermissaoService gerenciamentoPermissaoService;
    @Autowired
    private PermissaoMapper permissaoMapper;

    @PostMapping
    @RequiresPermission(PermissaoPermissions.CRIAR)
    public ResponseEntity<PermissaoResponse> createPermissao(@RequestBody CreatePermissaoRequest request) {
        var permissao = gerenciamentoPermissaoService.createPermissao(request);
        return new ResponseEntity<>(permissaoMapper.toResponse(permissao), HttpStatus.CREATED);
    }

    @GetMapping
    @RequiresPermission(PermissaoPermissions.VISUALIZAR)
    public ResponseEntity<List<PermissaoResponse>> getAllPermissoes() {
        var permissoes = gerenciamentoPermissaoService.getAllPermissoes();
        return ResponseEntity.ok(permissaoMapper.toResponseList(permissoes));
    }
}
