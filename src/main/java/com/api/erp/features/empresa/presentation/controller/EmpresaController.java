package com.api.erp.features.empresa.presentation.controller;

import com.api.erp.features.empresa.application.dto.CriarEmpresaRequest;
import com.api.erp.features.empresa.application.dto.EmpresaResponse;
import com.api.erp.features.empresa.application.service.CriarEmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/empresa")
@Tag(name = "Empresa", description = "Configurações da Empresa")
public class EmpresaController {
    
    private final CriarEmpresaService empresaService;
    
    public EmpresaController(CriarEmpresaService empresaService) {
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
    public ResponseEntity<EmpresaResponse> obter() {
        EmpresaResponse empresa = empresaService.buscarTodas()
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Empresa não configurada"));
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
    public ResponseEntity<EmpresaResponse> atualizar(
            @RequestBody CriarEmpresaRequest request) {
        EmpresaResponse empresa = empresaService.buscarTodas()
            .stream()
            .findFirst()
            .map(e -> empresaService.atualizarEmpresa(e.getId(), request))
            .orElseThrow(() -> new RuntimeException("Empresa não configurada"));
        return ResponseEntity.ok(empresa);
    }
}
