package com.api.erp.v1.main.master.user.domain.service;

import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import com.api.erp.v1.main.master.permission.domain.entity.Role;
import com.api.erp.v1.main.master.user.application.dto.request.AddPermissionsRequest;
import com.api.erp.v1.main.master.user.application.dto.request.AdicionarRolesRequest;
import com.api.erp.v1.main.master.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.master.user.application.dto.request.UpdateUserRequest;
import com.api.erp.v1.main.master.user.domain.entity.User;

import java.util.List;

public interface IUserService {
    User create(CreateUserRequest request);

    User update(Long id, UpdateUserRequest request);

    User findById(Long id);

    List<User> listAll();

    List<User> listPending();

    void disable(Long id);

    public User approve(Long userId, Long gestorId);

    public User reject(Long userId, Long gestorId, String motivo);

    void addPermissions(Long userId, AddPermissionsRequest request);

    void removePermission(Long userId, Long permissionId);

    List<Permission> listPermissions(Long userId);

    void addRoles(Long userId, AdicionarRolesRequest request);

    void removeRole(Long userId, Long roleId);

    List<Role> listRoles(Long userId);
}
