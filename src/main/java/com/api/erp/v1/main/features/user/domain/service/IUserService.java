package com.api.erp.v1.main.features.user.domain.service;

import com.api.erp.v1.main.features.permission.domain.entity.Permission;
import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.user.application.dto.request.AdicionarPermissionsRequest;
import com.api.erp.v1.main.features.user.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.main.features.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.features.user.application.dto.request.UpdateUserRequest;
import com.api.erp.v1.main.features.user.domain.entity.User;

import java.util.List;

public interface IUserService {
    User criar(CreateUserRequest request);

    User atualizar(Long id, UpdateUserRequest request);

    User buscarPorId(Long id);

    List<User> listarTodos();

    List<User> listarPendentes();

    void inativar(Long id);

    public User aprovar(Long userId, Long gestorId);

    public User rejeitar(Long userId, Long gestorId, String motivo);

    void adicionarPermissions(Long userId, AdicionarPermissionsRequest request);

    void removerPermission(Long userId, Long permissionId);

    List<Permission> listarPermissions(Long userId);

    void adicionarRoles(Long userId, AdicionarRolesRequest request);

    void removerRole(Long userId, Long roleId);

    List<Role> listarRoles(Long userId);
}
