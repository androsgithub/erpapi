package com.api.erp.v1.features.produto.presentation.controller;

import com.api.erp.v1.features.produto.application.dto.ComposicaoRequestDTO;
import com.api.erp.v1.features.produto.application.dto.ComposicaoResponseDTO;
import com.api.erp.v1.features.produto.domain.controller.IComposicaoController;
import com.api.erp.v1.features.produto.domain.entity.ComposicaoPermissions;
import com.api.erp.v1.features.produto.infrastructure.service.ComposicaoService;
import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/composicoes")
public class ComposicaoController implements IComposicaoController {

    @Autowired
    private ComposicaoService service;

    @PostMapping
    @RequiresPermission(ComposicaoPermissions.CRIAR)
    public ResponseEntity<ComposicaoResponseDTO> criar(
            @RequestBody ComposicaoRequestDTO dto) {
        ComposicaoResponseDTO resposta = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    @RequiresPermission(ComposicaoPermissions.ATUALIZAR)
    public ResponseEntity<ComposicaoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody ComposicaoRequestDTO dto) {
        ComposicaoResponseDTO resposta = service.atualizar(id, dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    @RequiresPermission(ComposicaoPermissions.VISUALIZAR)
    public ResponseEntity<ComposicaoResponseDTO> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @GetMapping("/produto/{produtoFabricadoId}")
    @RequiresPermission(ComposicaoPermissions.VISUALIZAR)
    public ResponseEntity<List<ComposicaoResponseDTO>> listarComposicoesPor(
            @PathVariable Long produtoFabricadoId) {
        return ResponseEntity.ok(service.listarComposicoesPor(produtoFabricadoId));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(ComposicaoPermissions.DELETAR)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/produto/{produtoFabricadoId}/limpar")
    @RequiresPermission(ComposicaoPermissions.DELETAR)
    public ResponseEntity<Void> deletarComposicoesDeProduto(
            @PathVariable Long produtoFabricadoId) {
        service.deletarComposicoesDeProduto(produtoFabricadoId);
        return ResponseEntity.noContent().build();
    }
}
