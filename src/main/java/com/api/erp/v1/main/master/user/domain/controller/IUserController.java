package com.api.erp.v1.main.master.user.domain.controller;

import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import com.api.erp.v1.main.master.permission.domain.entity.Role;
import com.api.erp.v1.main.master.user.application.dto.request.*;
import com.api.erp.v1.main.master.user.application.dto.response.AuthenticationResponse;
import com.api.erp.v1.main.master.user.application.dto.response.UserPermissionsResponse;
import com.api.erp.v1.main.master.user.application.dto.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IUserController {

    AuthenticationResponse login(LoginRequest request);

    UserResponse criar(CreateUserRequest request);

    UserPermissionsResponse buscar(Long id);

    List<UserResponse> listar();

    UserPermissionsResponse aprovar(
            Long id,
            @RequestParam Long gestorId);

    UserPermissionsResponse rejeitar(
            Long id,
            @RequestParam Long gestorId,
            @RequestParam String motivo);

    UserPermissionsResponse atualizar(
            Long id,
            UpdateUserRequest request);

    void inativar(Long id);

    ResponseEntity<Void> adicionarPermissions(
            Long userId,
            AddPermissionsRequest request);

    ResponseEntity<Void> removerPermission(
            Long userId,
            Long permissionId);

    ResponseEntity<List<Permission>> listarPermissions(
            Long userId);

    ResponseEntity<Void> adicionarRoles(
            Long userId,
            AdicionarRolesRequest request);

    ResponseEntity<Void> removerRole(
            Long userId,
            Long roleId);

    ResponseEntity<List<Role>> listarRoles(
            Long userId);

    UserPermissionsResponse obterDadosAtualizados();
}
