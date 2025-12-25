package com.api.erp.v1.features.empresa.presentation.controller;

import com.api.erp.v1.features.empresa.application.dto.EmpresaRequest;
import com.api.erp.v1.features.empresa.application.dto.EmpresaResponse;
import com.api.erp.v1.features.empresa.domain.entity.Empresa;
import com.api.erp.v1.features.empresa.domain.entity.EmpresaPermissions;
import com.api.erp.v1.features.empresa.domain.service.EmpresaService;
import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/empresa")
@Tag(name = "Empresa", description = "Configurações da Empresa")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    @Operation(summary = "Obter dados da empresa", description = "Retorna os dados da empresa cadastrada (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados da empresa retornados"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "400", description = "Empresa não configurada")
    })
    @RequiresPermission(EmpresaPermissions.BUSCAR)
    public ResponseEntity<EmpresaResponse> obter() {
        EmpresaResponse empresa = toResponse(empresaService.getDadosEmpresa());
        return ResponseEntity.ok(empresa);
    }

    @PutMapping
    @Operation(summary = "Atualizar dados da empresa", description = "Atualiza os dados da empresa (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "400", description = "Empresa não configurada")
    })
    @RequiresPermission(EmpresaPermissions.ATUALIZAR)
    public ResponseEntity<EmpresaResponse> atualizar(
            @RequestBody EmpresaRequest request) {
        EmpresaResponse empresa = toResponse(empresaService.updateDadosEmpresa(request));
        return ResponseEntity.ok(empresa);
    }

    public static EmpresaResponse toResponse(Empresa empresa) {
        return new EmpresaResponse(
                empresa.getId().toString(),
                empresa.getNome(),
                empresa.getCnpj(),
                empresa.getEmail(),
                empresa.getTelefone(),
                empresa.getEndereco() != null
                        ? empresa.getEndereco().getId().toString()
                        : null,
                empresa.isAtiva(),
                empresa.isRequerAprovacaoGestor(),
                empresa.isRequerEmailCorporativo(),
                empresa.getDominiosPermitidos(),
                empresa.getDataCriacao(),
                empresa.getDataAtualizacao()
        );
    }
}
