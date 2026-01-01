package com.api.erp.v1.features.endereco.presentation.controller;

import com.api.erp.v1.features.endereco.application.dto.CreateEnderecoRequest;
import com.api.erp.v1.features.endereco.application.dto.EnderecoResponse;
import com.api.erp.v1.features.endereco.application.mapper.EnderecoMapper;
import com.api.erp.v1.features.endereco.domain.controller.IEnderecoController;
import com.api.erp.v1.features.endereco.domain.entity.EnderecoPermissions;
import com.api.erp.v1.features.endereco.domain.service.IEnderecoService;
import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enderecos")
public class EnderecoController implements IEnderecoController {

    @Autowired
    private IEnderecoService enderecoService;
    @Autowired
    private EnderecoMapper enderecoMapper;

    @PostMapping
    @RequiresPermission(EnderecoPermissions.CRIAR)
    public ResponseEntity<EnderecoResponse> criar(@RequestBody CreateEnderecoRequest request) {
        var endereco = enderecoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(enderecoMapper.toResponse(endereco));
    }

    @GetMapping("/{id}")
    @RequiresPermission(EnderecoPermissions.VISUALIZAR)
    public ResponseEntity<EnderecoResponse> buscar(
            @PathVariable @Parameter(description = "ID do endereço") Long id) {
        var endereco = enderecoService.buscarPorId(id);
        return ResponseEntity.ok(enderecoMapper.toResponse(endereco));
    }

    @GetMapping
    @RequiresPermission(EnderecoPermissions.VISUALIZAR)
    public ResponseEntity<List<EnderecoResponse>> listar() {
        var enderecos = enderecoService.buscarTodos();
        return ResponseEntity.ok(enderecoMapper.toResponseList(enderecos));
    }

    @PutMapping("/{id}")
    @RequiresPermission(EnderecoPermissions.ATUALIZAR)
    public ResponseEntity<EnderecoResponse> atualizar(
            @PathVariable @Parameter(description = "ID do endereço") Long id,
            @RequestBody CreateEnderecoRequest request) {
        var endereco = enderecoService.atualizar(id, request);
        return ResponseEntity.ok(enderecoMapper.toResponse(endereco));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(EnderecoPermissions.DELETAR)
    public ResponseEntity<Void> deletar(
            @PathVariable @Parameter(description = "ID do endereço") Long id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
