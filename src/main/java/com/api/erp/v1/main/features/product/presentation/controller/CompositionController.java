package com.api.erp.v1.main.features.product.presentation.controller;

import com.api.erp.v1.main.features.product.application.dto.CompositionRequestDTO;
import com.api.erp.v1.main.features.product.application.dto.CompositionResponseDTO;
import com.api.erp.v1.main.features.product.domain.controller.ICompositionController;
import com.api.erp.v1.docs.openapi.features.product.CompositionOpenApiDocumentation;
import com.api.erp.v1.main.features.product.domain.entity.CompositionPermissions;
import com.api.erp.v1.main.features.product.infrastructure.service.CompositionService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/composicoes")
public class CompositionController implements ICompositionController, CompositionOpenApiDocumentation {

    @Autowired
    private CompositionService service;

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(CompositionPermissions.CRIAR)
    public ResponseEntity<CompositionResponseDTO> criar(
            @RequestBody CompositionRequestDTO dto) {
        CompositionResponseDTO resposta = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(CompositionPermissions.ATUALIZAR)
    public ResponseEntity<CompositionResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody CompositionRequestDTO dto) {
        CompositionResponseDTO resposta = service.atualizar(id, dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(CompositionPermissions.VISUALIZAR)
    public ResponseEntity<CompositionResponseDTO> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @GetMapping("/product/{productFabricadoId}")
    @RequiresXTenantId
    @RequiresPermission(CompositionPermissions.VISUALIZAR)
    public ResponseEntity<List<CompositionResponseDTO>> listarComposicoesPor(
            @PathVariable Long productFabricadoId) {
        return ResponseEntity.ok(service.listarComposicoesPor(productFabricadoId));
    }

    @DeleteMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(CompositionPermissions.DELETAR)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/product/{productFabricadoId}/limpar")
    @RequiresXTenantId
    @RequiresPermission(CompositionPermissions.DELETAR)
    public ResponseEntity<Void> deletarComposicoesDeProduct(
            @PathVariable Long productFabricadoId) {
        service.deletarComposicoesDeProduct(productFabricadoId);
        return ResponseEntity.noContent().build();
    }
}
