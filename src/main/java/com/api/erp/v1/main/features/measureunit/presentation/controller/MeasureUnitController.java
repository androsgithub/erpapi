package com.api.erp.v1.main.features.measureunit.presentation.controller;

import com.api.erp.v1.main.features.measureunit.application.dto.request.MeasureUnitRequestDTO;
import com.api.erp.v1.main.features.measureunit.application.dto.response.MeasureUnitResponseDTO;
import com.api.erp.v1.main.features.measureunit.domain.controller.IMeasureUnitController;
import com.api.erp.v1.docs.openapi.features.measureunit.MeasureUnitOpenApiDocumentation;
import com.api.erp.v1.main.features.measureunit.domain.entity.MeasureUnitPermissions;
import com.api.erp.v1.main.features.measureunit.domain.service.IMeasureUnitService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/src/test/java/com/api/v1/unidades-medida")
public class MeasureUnitController implements IMeasureUnitController, MeasureUnitOpenApiDocumentation {

    @Autowired
    private IMeasureUnitService service;

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(MeasureUnitPermissions.CRIAR)
    public ResponseEntity<MeasureUnitResponseDTO> criar(
            @RequestBody MeasureUnitRequestDTO dto) {
        MeasureUnitResponseDTO resposta = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(MeasureUnitPermissions.ATUALIZAR)
    public ResponseEntity<MeasureUnitResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody MeasureUnitRequestDTO dto) {
        MeasureUnitResponseDTO resposta = service.atualizar(id, dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(MeasureUnitPermissions.VISUALIZAR)
    public ResponseEntity<MeasureUnitResponseDTO> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @GetMapping
    @RequiresXTenantId
    @RequiresPermission(MeasureUnitPermissions.VISUALIZAR)
    public ResponseEntity<Page<MeasureUnitResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "descricao") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(service.listar(pageable));
    }

    @PatchMapping("/{id}/ativar")
    @RequiresXTenantId
    @RequiresPermission(MeasureUnitPermissions.ATIVAR)
    public ResponseEntity<MeasureUnitResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    @RequiresXTenantId
    @RequiresPermission(MeasureUnitPermissions.DESATIVAR)
    public ResponseEntity<MeasureUnitResponseDTO> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.desativar(id));
    }

    @DeleteMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(MeasureUnitPermissions.DELETAR)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
