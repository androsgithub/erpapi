package com.api.erp.v1.main.features.usuario.domain.controller;

import com.api.erp.v1.main.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.main.features.permissao.domain.entity.Role;
import com.api.erp.v1.main.features.usuario.application.dto.request.*;
import com.api.erp.v1.main.features.usuario.application.dto.response.TokenResponse;
import com.api.erp.v1.main.features.usuario.application.dto.response.UsuarioPermissoesResponse;
import com.api.erp.v1.main.features.usuario.application.dto.response.UsuarioResponse;
import com.api.erp.v1.main.shared.infrastructure.documentation.TenantIdentifierHeader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IUsuarioController {

    @TenantIdentifierHeader
    TokenResponse login(LoginRequest request);

    UsuarioResponse criar(CreateUsuarioRequest request);

    UsuarioPermissoesResponse buscar(Long id);

    List<UsuarioResponse> listar();

    UsuarioPermissoesResponse aprovar(
            Long id,
            @RequestParam Long gestorId);

    UsuarioPermissoesResponse rejeitar(
            Long id,
            @RequestParam Long gestorId,
            @RequestParam String motivo);

    UsuarioPermissoesResponse atualizar(
            Long id,
            UpdateUsuarioRequest request);

    void inativar(Long id);

    ResponseEntity<Void> adicionarPermissoes(
            Long usuarioId,
            AdicionarPermissoesRequest request);

    ResponseEntity<Void> removerPermissao(
            Long usuarioId,
            Long permissaoId);

    ResponseEntity<List<Permissao>> listarPermissoes(
            Long usuarioId);

    ResponseEntity<Void> adicionarRoles(
            Long usuarioId,
            AdicionarRolesRequest request);

    ResponseEntity<Void> removerRole(
            Long usuarioId,
            Long roleId);

    ResponseEntity<List<Role>> listarRoles(
            Long usuarioId);

    UsuarioPermissoesResponse obterDadosAtualizados();
}
