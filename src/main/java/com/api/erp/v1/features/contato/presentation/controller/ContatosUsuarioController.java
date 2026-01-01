package com.api.erp.v1.features.contato.presentation.controller;

import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.application.dto.request.AssociarContatosRequest;
import com.api.erp.v1.features.contato.application.dto.request.RemoverContatoRequest;
import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.features.contato.application.dto.response.UsuarioContatosResponse;
import com.api.erp.v1.features.contato.application.mapper.IContatoMapper;
import com.api.erp.v1.features.contato.application.mapper.UsuarioContatoMapper;
import com.api.erp.v1.features.contato.domain.controller.IContatosUsuarioController;
import com.api.erp.v1.features.contato.domain.entity.ContatoPermissions;
import com.api.erp.v1.features.contato.domain.service.IGerenciamentoContatoService;
import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gerenciar contatos de Usuário
 * Fornece endpoints especializados para operações de contatos associados a usuários
 * <p>
 * Este controller é responsável por operações que envolvem a relação
 * entre Usuário e Contato, como:
 * - Associar múltiplos contatos a um usuário
 * - Adicionar/remover contatos de um usuário
 * - Marcar contato como principal
 * - Ativar/desativar contatos de um usuário
 * <p>
 * Para operações básicas de contato (CRUD global), use ContatosController.
 * Para futuras features como "Cliente", crie ContatosClienteController seguindo o mesmo padrão.
 * <p>
 * PADRÃO DE ARQUITETURA:
 * - Controller orquestra chamadas ao service de gerenciamento
 * - Service retorna apenas entidades de domínio
 * - Mapper converte entidades para DTOs de response
 * - Controller não contém lógica de negócio, apenas orquestração
 */
@RestController
@RequestMapping("/api/v1/contatos/usuario")
public class ContatosUsuarioController implements IContatosUsuarioController {

    @Autowired

    private IGerenciamentoContatoService gerenciamentoContatoService;
    @Autowired
    private IContatoMapper contatoMapper;
    @Autowired
    private UsuarioContatoMapper usuarioContatoMapper;

    @PostMapping("/associar")
    @RequiresPermission(ContatoPermissions.CRIAR)
    public ResponseEntity<UsuarioContatosResponse> associarContatos(
            @RequestBody AssociarContatosRequest request) {
        var usuarioContato = gerenciamentoContatoService.associarContatos(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioContatoMapper.toResponse(usuarioContato));
    }

    @PostMapping("/{usuarioId}/contato")
    @RequiresPermission(ContatoPermissions.CRIAR)
    public ResponseEntity<ContatoResponse> adicionarContato(
            @PathVariable @Parameter(description = "ID do usuário") Long usuarioId,
            @RequestBody CreateContatoRequest request) {
        var contato = gerenciamentoContatoService.adicionarContato(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(contatoMapper.toResponse(contato));
    }

    @GetMapping("/{usuarioId}")
    @RequiresPermission(ContatoPermissions.VISUALIZAR)
    public ResponseEntity<UsuarioContatosResponse> buscarContatosUsuario(
            @PathVariable @Parameter(description = "ID do usuário") Long usuarioId) {
        var usuarioContato = gerenciamentoContatoService.buscarContatosUsuario(usuarioId);
        return ResponseEntity.ok(usuarioContatoMapper.toResponse(usuarioContato));
    }

    @DeleteMapping("/remover")
    @RequiresPermission(ContatoPermissions.DELETAR)
    public ResponseEntity<Void> removerContato(
            @RequestBody RemoverContatoRequest request) {
        gerenciamentoContatoService.removerContato(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{usuarioId}/contato/{contatoId}/principal")
    @RequiresPermission(ContatoPermissions.ATUALIZAR)
    public ResponseEntity<ContatoResponse> marcarComoPrincipal(
            @PathVariable @Parameter(description = "ID do usuário") Long usuarioId,
            @PathVariable @Parameter(description = "ID do contato") Long contatoId) {
        var contato = gerenciamentoContatoService.marcarComoPrincipal(usuarioId, contatoId);
        return ResponseEntity.ok(contatoMapper.toResponse(contato));
    }

    @PatchMapping("/{usuarioId}/contato/{contatoId}/desativar")
    @RequiresPermission(ContatoPermissions.ATUALIZAR)
    public ResponseEntity<ContatoResponse> desativarContato(
            @PathVariable @Parameter(description = "ID do usuário") Long usuarioId,
            @PathVariable @Parameter(description = "ID do contato") Long contatoId) {
        var contato = gerenciamentoContatoService.desativarContato(usuarioId, contatoId);
        return ResponseEntity.ok(contatoMapper.toResponse(contato));
    }

    @PatchMapping("/{usuarioId}/contato/{contatoId}/ativar")
    @RequiresPermission(ContatoPermissions.ATUALIZAR)
    public ResponseEntity<ContatoResponse> ativarContato(
            @PathVariable @Parameter(description = "ID do usuário") Long usuarioId,
            @PathVariable @Parameter(description = "ID do contato") Long contatoId) {
        var contato = gerenciamentoContatoService.ativarContato(usuarioId, contatoId);
        return ResponseEntity.ok(contatoMapper.toResponse(contato));
    }
}
