package com.api.erp.v1.main.features.unidademedida.presentation.controller;

import com.api.erp.v1.main.features.unidademedida.application.dto.request.UnidadeMedidaRequestDTO;
import com.api.erp.v1.main.features.unidademedida.application.dto.response.UnidadeMedidaResponseDTO;
import com.api.erp.v1.main.features.unidademedida.domain.controller.IUnidadeMedidaController;
import com.api.erp.v1.docs.openapi.features.unidademedida.UnidadeMedidaOpenApiDocumentation;
import com.api.erp.v1.main.features.unidademedida.domain.entity.UnidadeMedidaPermissions;
import com.api.erp.v1.main.features.unidademedida.domain.service.IUnidadeMedidaService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/unidades-medida")
public class UnidadeMedidaController implements IUnidadeMedidaController, UnidadeMedidaOpenApiDocumentation {

    @Autowired
    private IUnidadeMedidaService service;

    @PostMapping
    @RequiresPermission(UnidadeMedidaPermissions.CRIAR)
    public ResponseEntity<UnidadeMedidaResponseDTO> criar(
            @RequestBody UnidadeMedidaRequestDTO dto) {
        UnidadeMedidaResponseDTO resposta = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    @RequiresPermission(UnidadeMedidaPermissions.ATUALIZAR)
    public ResponseEntity<UnidadeMedidaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody UnidadeMedidaRequestDTO dto) {
        UnidadeMedidaResponseDTO resposta = service.atualizar(id, dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    @RequiresPermission(UnidadeMedidaPermissions.VISUALIZAR)
    public ResponseEntity<UnidadeMedidaResponseDTO> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @GetMapping
    @RequiresPermission(UnidadeMedidaPermissions.VISUALIZAR)
    public ResponseEntity<Page<UnidadeMedidaResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "descricao") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(service.listar(pageable));
    }

    @PatchMapping("/{id}/ativar")
    @RequiresPermission(UnidadeMedidaPermissions.ATIVAR)
    public ResponseEntity<UnidadeMedidaResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    @RequiresPermission(UnidadeMedidaPermissions.DESATIVAR)
    public ResponseEntity<UnidadeMedidaResponseDTO> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.desativar(id));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(UnidadeMedidaPermissions.DELETAR)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
