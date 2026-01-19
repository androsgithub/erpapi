package com.api.erp.v1.features.usuario.presentation.controller;

import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.features.usuario.application.dto.request.*;
import com.api.erp.v1.features.usuario.application.dto.response.TokenResponse;
import com.api.erp.v1.features.usuario.application.dto.response.UsuarioPermissoesResponse;
import com.api.erp.v1.features.usuario.application.dto.response.UsuarioResponse;
import com.api.erp.v1.features.usuario.application.mapper.IUsuarioMapper;
import com.api.erp.v1.features.usuario.application.mapper.IUsuarioPermissoesMapper;
import com.api.erp.v1.features.usuario.domain.controller.IUsuarioController;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.entity.UsuarioPermissions;
import com.api.erp.v1.features.usuario.domain.service.IUsuarioService;
import com.api.erp.v1.features.usuario.infrastructure.service.AuthenticationService;
import com.api.erp.v1.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.shared.infrastructure.service.SecurityService;
import com.dros.observability.application.annotation.TrackFlow;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController implements IUsuarioController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private IUsuarioMapper usuarioMapper;
    @Autowired
    private IUsuarioPermissoesMapper usuarioPermissoesMapper;
    @Autowired
    @Qualifier("usuarioServiceProxy")
    private IUsuarioService usuarioService;
    @Autowired
    private SecurityService securityService;


    @PostMapping("/login")
    @TrackFlow("USER_LOGIN")
    public TokenResponse login(@RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @GetMapping("/me")
    @TrackFlow("READ_USER_ME")
    public UsuarioPermissoesResponse obterDadosAtualizados() {
        Long usuarioId = Long.valueOf(securityService.getAuthUsuarioId());
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        return usuarioPermissoesMapper.toResponse(usuario);
    }

    @PostMapping
    @RequiresPermission(UsuarioPermissions.CRIAR)
    @TrackFlow("CREATE_USER")
    public UsuarioResponse criar(@RequestBody CreateUsuarioRequest request) {
        Usuario usuario = usuarioService.criar(request);
        return usuarioMapper.toResponse(usuario);
    }

    @GetMapping("/{id}")
    @RequiresPermission(UsuarioPermissions.VISUALIZAR_OUTRO_USUARIO)
    @TrackFlow("READ_OTHER_USER")
    public UsuarioPermissoesResponse buscar(Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return usuarioPermissoesMapper.toResponse(usuario);
    }

    @GetMapping
    @RequiresPermission(UsuarioPermissions.LISTAR)
    @TrackFlow("LIST_USERS")
    public List<UsuarioResponse> listar() {
        return usuarioMapper.toResponseList(usuarioService.listarTodos());
    }

    @PatchMapping("/{id}/aprovar")
    @RequiresPermission(UsuarioPermissions.APROVAR)
    @TrackFlow("APPROVE_USER")
    public UsuarioPermissoesResponse aprovar(Long id, Long gestorId) {
        Usuario usuario = usuarioService.aprovar(id, gestorId);
        return usuarioPermissoesMapper.toResponse(usuario);
    }

    @PatchMapping("/{id}/rejeitar")
    @RequiresPermission(UsuarioPermissions.REJEITAR)
    @TrackFlow("REJECT_USER")
    public UsuarioPermissoesResponse rejeitar(Long id, Long gestorId, String motivo) {

        Usuario usuario = usuarioService.rejeitar(id, gestorId, motivo);
        return usuarioPermissoesMapper.toResponse(usuario);
    }

    @PutMapping("/{id}")
    @RequiresPermission(UsuarioPermissions.ATUALIZAR)
    @TrackFlow("UPDATE_USER")
    public UsuarioPermissoesResponse atualizar(
            @PathVariable Long id,
            @RequestBody UpdateUsuarioRequest request) {

        Usuario usuario = usuarioService.atualizar(id, request);
        return usuarioPermissoesMapper.toResponse(usuario);
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(UsuarioPermissions.DESATIVAR)
    @TrackFlow("DISABLE_USER")
    public void inativar(@PathVariable Long id) {
        usuarioService.inativar(id);
    }

    // NOVOS ENDPOINTS PARA GERENCIAMENTO DE PERMISSÕES

    @PostMapping("/{usuarioId}/permissoes")
    @RequiresPermission(UsuarioPermissions.GERENCIAR_PERMISSOES)
    @TrackFlow("ADD_USER_PERMS")
    public ResponseEntity<Void> adicionarPermissoes(
            @PathVariable Long usuarioId,
            @RequestBody AdicionarPermissoesRequest request) {
        usuarioService.adicionarPermissoes(usuarioId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usuarioId}/permissoes/{permissaoId}")
    @RequiresPermission(UsuarioPermissions.REMOVER_PERMISSAO)
    @TrackFlow("REMOVE_USER_PERMS")
    public ResponseEntity<Void> removerPermissao(
            @PathVariable Long usuarioId,
            @PathVariable Long permissaoId) {
        usuarioService.removerPermissao(usuarioId, permissaoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{usuarioId}/permissoes")
    @TrackFlow("LIST_USER_PERMS")
    public ResponseEntity<List<Permissao>> listarPermissoes(
            @PathVariable Long usuarioId) {
        List<Permissao> permissoes = usuarioService.listarPermissoes(usuarioId);
        return ResponseEntity.ok(permissoes);
    }

    // NOVOS ENDPOINTS PARA GERENCIAMENTO DE ROLES

    @PostMapping("/{usuarioId}/roles")
    @RequiresPermission(UsuarioPermissions.GERENCIAR_ROLES)
    @TrackFlow("LIST_USER_ROLES")
    public ResponseEntity<Void> adicionarRoles(
            @PathVariable Long usuarioId,
            @RequestBody AdicionarRolesRequest request) {
        usuarioService.adicionarRoles(usuarioId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usuarioId}/roles/{roleId}")
    @RequiresPermission(UsuarioPermissions.REMOVER_ROLE)
    @TrackFlow("REMOVE_USER_ROLE")
    public ResponseEntity<Void> removerRole(
            @PathVariable Long usuarioId,
            @PathVariable @Parameter(description = "ID da role") Long roleId) {
        usuarioService.removerRole(usuarioId, roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{usuarioId}/roles")
    @RequiresPermission(UsuarioPermissions.LISTAR_ROLES)
    @TrackFlow("LIST_USER_ROLES")
    public ResponseEntity<List<Role>> listarRoles(
            @PathVariable Long usuarioId) {
        List<Role> roles = usuarioService.listarRoles(usuarioId);
        return ResponseEntity.ok(roles);
    }
}