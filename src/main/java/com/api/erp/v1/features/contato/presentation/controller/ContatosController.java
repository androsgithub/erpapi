package com.api.erp.v1.features.contato.presentation.controller;

import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.features.contato.application.mapper.IContatoMapper;
import com.api.erp.v1.features.contato.domain.controller.IContatosController;
import com.api.erp.v1.features.contato.domain.entity.ContatoPermissions;
import com.api.erp.v1.features.contato.domain.service.IContatoService;
import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/contatos")
public class ContatosController implements IContatosController {

    @Autowired
    private IContatoService contatoService;
    @Autowired
    private IContatoMapper contatoMapper;

    @PostMapping
    @RequiresPermission(ContatoPermissions.CRIAR)
    public ResponseEntity<ContatoResponse> criar(@RequestBody CreateContatoRequest request) {
        var contato = contatoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(contatoMapper.toResponse(contato));
    }

    @GetMapping("/{id}")
    @RequiresPermission(ContatoPermissions.VISUALIZAR)
    public ResponseEntity<ContatoResponse> buscar(
            @PathVariable @Parameter(description = "ID do contato") Long id) {
        var contato = contatoService.buscarPorId(id);
        return ResponseEntity.ok(contatoMapper.toResponse(contato));
    }

    @GetMapping
    @RequiresPermission(ContatoPermissions.VISUALIZAR)
    public ResponseEntity<List<ContatoResponse>> listar() {
        var contatos = contatoService.buscarTodos();
        return ResponseEntity.ok(contatoMapper.toResponseList(contatos));
    }

    @GetMapping("/status/ativos")
    @RequiresPermission(ContatoPermissions.VISUALIZAR)
    public ResponseEntity<List<ContatoResponse>> listarAtivos() {
        var contatos = contatoService.buscarAtivos();
        return ResponseEntity.ok(contatoMapper.toResponseList(contatos));
    }


    @GetMapping("/status/inativos")
    @RequiresPermission(ContatoPermissions.VISUALIZAR)
    public ResponseEntity<List<ContatoResponse>> listarInativos() {
        var contatos = contatoService.buscarInativos();
        return ResponseEntity.ok(contatoMapper.toResponseList(contatos));
    }

    @GetMapping("/tipo/{tipo}")
    @RequiresPermission(ContatoPermissions.VISUALIZAR)
    public ResponseEntity<List<ContatoResponse>> listarPorTipo(
            @PathVariable @Parameter(description = "Tipo de contato (TELEFONE, EMAIL, WHATSAPP, etc.)") String tipo) {
        var contatos = contatoService.buscarPorTipo(tipo);
        return ResponseEntity.ok(contatoMapper.toResponseList(contatos));
    }

    @GetMapping("/principal")
    @RequiresPermission(ContatoPermissions.VISUALIZAR)
    public ResponseEntity<ContatoResponse> buscarPrincipal() {
        var contato = contatoService.buscarPrincipal();
        return ResponseEntity.ok(contatoMapper.toResponse(contato));
    }

    @PutMapping("/{id}")
    @RequiresPermission(ContatoPermissions.ATUALIZAR)
    public ResponseEntity<ContatoResponse> atualizar(
            @PathVariable @Parameter(description = "ID do contato") Long id,
            @RequestBody CreateContatoRequest request) {
        var contato = contatoService.atualizar(id, request);
        return ResponseEntity.ok(contatoMapper.toResponse(contato));
    }


    @PatchMapping("/{id}/ativar")
    @RequiresPermission(ContatoPermissions.ATUALIZAR)
    public ResponseEntity<ContatoResponse> ativar(
            @PathVariable @Parameter(description = "ID do contato") Long id) {
        var contato = contatoService.ativar(id);
        return ResponseEntity.ok(contatoMapper.toResponse(contato));
    }

    @PatchMapping("/{id}/desativar")
    @RequiresPermission(ContatoPermissions.ATUALIZAR)
    public ResponseEntity<ContatoResponse> desativar(
            @PathVariable @Parameter(description = "ID do contato") Long id) {
        var contato = contatoService.desativar(id);
        return ResponseEntity.ok(contatoMapper.toResponse(contato));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(ContatoPermissions.DELETAR)
    public ResponseEntity<Void> deletar(
            @PathVariable @Parameter(description = "ID do contato") Long id) {
        contatoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
