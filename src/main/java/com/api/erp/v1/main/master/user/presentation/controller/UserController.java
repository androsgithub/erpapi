package com.api.erp.v1.main.master.user.presentation.controller;

import com.api.erp.v1.docs.openapi.features.user.UserOpenApiDocumentation;
import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import com.api.erp.v1.main.master.permission.domain.entity.Role;
import com.api.erp.v1.main.master.user.application.dto.request.*;
import com.api.erp.v1.main.master.user.application.dto.response.AuthenticationResponse;
import com.api.erp.v1.main.master.user.application.dto.response.UserPermissionsResponse;
import com.api.erp.v1.main.master.user.application.dto.response.UserResponse;
import com.api.erp.v1.main.master.user.application.mapper.IUserMapper;
import com.api.erp.v1.main.master.user.application.mapper.IUserPermissionsMapper;
import com.api.erp.v1.main.master.user.domain.controller.IUserController;
import com.api.erp.v1.main.master.user.domain.entity.User;
import com.api.erp.v1.main.master.user.domain.entity.UserPermissions;
import com.api.erp.v1.main.master.user.domain.service.IUserService;
import com.api.erp.v1.main.master.user.infrastructure.service.AuthenticationService;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.service.SecurityService;
import com.dros.observability.core.annotation.TrackFlow;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController implements IUserController, UserOpenApiDocumentation {
    private final AuthenticationService authenticationService;
    private final IUserMapper userMapper;
    private final IUserPermissionsMapper userPermissionsMapper;
    private final IUserService userService;
    private final SecurityService securityService;

    public UserController(
            AuthenticationService authenticationService,
            IUserMapper userMapper,
            IUserPermissionsMapper userPermissionsMapper,
            @Qualifier("userServiceProxy") IUserService userService,
            SecurityService securityService) {
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
        this.userPermissionsMapper = userPermissionsMapper;
        this.userService = userService;
        this.securityService = securityService;
    }


    @PostMapping("/login")
    @TrackFlow("USER_LOGIN")
    public AuthenticationResponse login(@RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @GetMapping("/me")
    @RequiresXTenantId
    @TrackFlow("READ_USER_ME")
    public UserPermissionsResponse obterDadosAtualizados() {
        Long userId = Long.valueOf(securityService.getAuthUserId());
        User user = userService.findById(userId);
        return userPermissionsMapper.toResponse(user);
    }

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.CREATE)
    @TrackFlow("CREATE_USER")
    public UserResponse criar(@RequestBody CreateUserRequest request) {
        User user = userService.create(request);
        return userMapper.toResponse(user);
    }

    @GetMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.VIEW_OTHER_USER)
    @TrackFlow("READ_OTHER_USER")
    public UserPermissionsResponse buscar(Long id) {
        User user = userService.findById(id);
        return userPermissionsMapper.toResponse(user);
    }

    @GetMapping
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.LIST)
    @TrackFlow("LIST_USERS")
    public List<UserResponse> listar() {
        return userMapper.toResponseList(userService.listAll());
    }

    @PatchMapping("/{id}/aprovar")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.APPROVE)
    @TrackFlow("APPROVE_USER")
    public UserPermissionsResponse aprovar(Long id, Long gestorId) {
        User user = userService.approve(id, gestorId);
        return userPermissionsMapper.toResponse(user);
    }

    @PatchMapping("/{id}/rejeitar")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.REJECT)
    @TrackFlow("REJECT_USER")
    public UserPermissionsResponse rejeitar(Long id, Long gestorId, String motivo) {

        User user = userService.reject(id, gestorId, motivo);
        return userPermissionsMapper.toResponse(user);
    }

    @PutMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.UPDATE)
    @TrackFlow("UPDATE_USER")
    public UserPermissionsResponse atualizar(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        User user = userService.update(id, request);
        return userPermissionsMapper.toResponse(user);
    }

    @DeleteMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.DEACTIVATE)
    @TrackFlow("DISABLE_USER")
    public void inativar(@PathVariable Long id) {
        userService.disable(id);
    }

    // NOVOS ENDPOINTS PARA GERENCIAMENTO DE PERMISSÕES

    @PostMapping("/{userId}/permissions")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.MANAGE_PERMISSIONS)
    @TrackFlow("ADD_USER_PERMS")
    public ResponseEntity<Void> adicionarPermissions(
            @PathVariable Long userId,
            @RequestBody AddPermissionsRequest request) {
        userService.addPermissions(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/permissions/{permissionId}")
    @RequiresXTenantId
    @RequiresPermission(UserPermissions.REMOVE_PERMISSION)
    @TrackFlow("REMOVE_USER_PERMS")
    public ResponseEntity<Void> removerPermission(
            @PathVariable Long userId,
            @PathVariable Long permissionId) {
        userService.removePermission(userId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/permissions")
    @RequiresXTenantId
    @TrackFlow("LIST_USER_PERMS")
    public ResponseEntity<List<Permission>> listarPermissions(
            @PathVariable Long userId) {
        List<Permission> permissions = userService.listPermissions(userId);
        return ResponseEntity.ok(permissions);
    }

    // NOVOS ENDPOINTS PARA GERENCIAMENTO DE ROLES

    @PostMapping("/{userId}/roles")
    @RequiresPermission(UserPermissions.MANAGE_ROLES)
    @TrackFlow("LIST_USER_ROLES")
    public ResponseEntity<Void> adicionarRoles(
            @PathVariable Long userId,
            @RequestBody AdicionarRolesRequest request) {
        userService.addRoles(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @RequiresPermission(UserPermissions.REMOVE_ROLE)
    @TrackFlow("REMOVE_USER_ROLE")
    public ResponseEntity<Void> removerRole(
            @PathVariable Long userId,
            @PathVariable @Parameter(description = "ID da role") Long roleId) {
        userService.removeRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/roles")
    @RequiresPermission(UserPermissions.LIST_ROLES)
    @TrackFlow("LIST_USER_ROLES")
    public ResponseEntity<List<Role>> listarRoles(
            @PathVariable Long userId) {
        List<Role> roles = userService.listRoles(userId);
        return ResponseEntity.ok(roles);
    }
}