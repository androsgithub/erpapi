package com.api.erp.v1.main.features.user.presentation.controller;

import com.api.erp.v1.main.features.permission.domain.entity.Permission;
import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.user.application.dto.request.*;
import com.api.erp.v1.main.features.user.application.dto.response.TokenResponse;
import com.api.erp.v1.main.features.user.application.dto.response.UserPermissionsResponse;
import com.api.erp.v1.main.features.user.application.dto.response.UserResponse;
import com.api.erp.v1.main.features.user.application.mapper.IUserMapper;
import com.api.erp.v1.main.features.user.application.mapper.IUserPermissionsMapper;
import com.api.erp.v1.main.features.user.domain.controller.IUserController;
import com.api.erp.v1.docs.openapi.features.user.UserOpenApiDocumentation;
import com.api.erp.v1.main.features.user.domain.entity.User;
import com.api.erp.v1.main.features.user.domain.entity.UserPermissions;
import com.api.erp.v1.main.features.user.domain.service.IUserService;
import com.api.erp.v1.main.features.user.infrastructure.service.AuthenticationService;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.dros.observability.core.annotation.TrackFlow;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/src/test/java/com/api/v1/users")
public class UserController implements IUserController, UserOpenApiDocumentation {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IUserPermissionsMapper userPermissionsMapper;
    @Autowired
    @Qualifier("userServiceProxy")
    private IUserService userService;
    @Autowired
    private SecurityService securityService;


    @PostMapping("/login")
    @TrackFlow("USER_LOGIN")
    public TokenResponse login(@RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @GetMapping("/me")
    @RequiresXTenantId
    @TrackFlow("READ_USER_ME")
    public UserPermissionsResponse obterDadosAtualizados() {
        Long userId = Long.valueOf(securityService.getAuthUserId());
        User user = userService.buscarPorId(userId);
        return userPermissionsMapper.toResponse(user);
    }

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.CRIAR)
    @TrackFlow("CREATE_USER")
    public UserResponse criar(@RequestBody CreateUserRequest request) {
        User user = userService.criar(request);
        return userMapper.toResponse(user);
    }

    @GetMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.VISUALIZAR_OUTRO_USER)
    @TrackFlow("READ_OTHER_USER")
    public UserPermissionsResponse buscar(Long id) {
        User user = userService.buscarPorId(id);
        return userPermissionsMapper.toResponse(user);
    }

    @GetMapping
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.LISTAR)
    @TrackFlow("LIST_USERS")
    public List<UserResponse> listar() {
        return userMapper.toResponseList(userService.listarTodos());
    }

    @PatchMapping("/{id}/aprovar")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.APROVAR)
    @TrackFlow("APPROVE_USER")
    public UserPermissionsResponse aprovar(Long id, Long gestorId) {
        User user = userService.aprovar(id, gestorId);
        return userPermissionsMapper.toResponse(user);
    }

    @PatchMapping("/{id}/rejeitar")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.REJEITAR)
    @TrackFlow("REJECT_USER")
    public UserPermissionsResponse rejeitar(Long id, Long gestorId, String motivo) {

        User user = userService.rejeitar(id, gestorId, motivo);
        return userPermissionsMapper.toResponse(user);
    }

    @PutMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.ATUALIZAR)
    @TrackFlow("UPDATE_USER")
    public UserPermissionsResponse atualizar(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        User user = userService.atualizar(id, request);
        return userPermissionsMapper.toResponse(user);
    }

    @DeleteMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.DESATIVAR)
    @TrackFlow("DISABLE_USER")
    public void inativar(@PathVariable Long id) {
        userService.inativar(id);
    }

    // NOVOS ENDPOINTS PARA GERENCIAMENTO DE PERMISSÕES

    @PostMapping("/{userId}/permissions")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.GERENCIAR_PERMISSIONS)
    @TrackFlow("ADD_USER_PERMS")
    public ResponseEntity<Void> adicionarPermissions(
            @PathVariable Long userId,
            @RequestBody AdicionarPermissionsRequest request) {
        userService.adicionarPermissions(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/permissions/{permissionId}")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.REMOVER_PERMISSION)
    @TrackFlow("REMOVE_USER_PERMS")
    public ResponseEntity<Void> removerPermission(
            @PathVariable Long userId,
            @PathVariable Long permissionId) {
        userService.removerPermission(userId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/permissions")
    @RequiresXTenantId
    @TrackFlow("LIST_USER_PERMS")
    public ResponseEntity<List<Permission>> listarPermissions(
            @PathVariable Long userId) {
        List<Permission> permissions = userService.listarPermissions(userId);
        return ResponseEntity.ok(permissions);
    }

    // NOVOS ENDPOINTS PARA GERENCIAMENTO DE ROLES

    @PostMapping("/{userId}/roles")
    @RequiresPermission(UserPermissions.GERENCIAR_ROLES)
    @TrackFlow("LIST_USER_ROLES")
    public ResponseEntity<Void> adicionarRoles(
            @PathVariable Long userId,
            @RequestBody AdicionarRolesRequest request) {
        userService.adicionarRoles(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @RequiresPermission(UserPermissions.REMOVER_ROLE)
    @TrackFlow("REMOVE_USER_ROLE")
    public ResponseEntity<Void> removerRole(
            @PathVariable Long userId,
            @PathVariable @Parameter(description = "ID da role") Long roleId) {
        userService.removerRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/roles")
    @RequiresPermission(UserPermissions.LISTAR_ROLES)
    @TrackFlow("LIST_USER_ROLES")
    public ResponseEntity<List<Role>> listarRoles(
            @PathVariable Long userId) {
        List<Role> roles = userService.listarRoles(userId);
        return ResponseEntity.ok(roles);
    }
}