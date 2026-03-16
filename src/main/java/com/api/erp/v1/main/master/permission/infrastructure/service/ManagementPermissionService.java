package com.api.erp.v1.main.master.permission.infrastructure.service;

import com.api.erp.v1.main.master.permission.application.dto.request.create.NewPermissionRequest;
import com.api.erp.v1.main.master.permission.application.dto.request.create.NewRoleRequest;
import com.api.erp.v1.main.master.permission.application.dto.request.edit.AddPermissionToUserRequest;
import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import com.api.erp.v1.main.master.permission.domain.entity.Role;
import com.api.erp.v1.main.master.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.master.permission.domain.repository.RoleRepository;
import com.api.erp.v1.main.master.permission.domain.service.IManagementPermissionService;
import com.api.erp.v1.main.master.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class ManagementPermissionService implements IManagementPermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Permission createPermission(NewPermissionRequest request) {
        Permission permission = Permission.builder()
                .code(request.codigo())
                .name(request.nome())
                .module(request.modulo())
                .action(request.acao())
                .build();
        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public Role createRole(NewRoleRequest request) {
        Role role = Role.builder()
                .name(request.name())
                .build();
        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "permissions",
            keyGenerator = "principalKeyGenerator",
            condition = "@tenantService.getTenantConfig().getPermissionConfig().isPermissionCacheEnabled()",
            unless = "#result == null"
    )
    public Set<Permission> getAllPermissions() {
        return new HashSet<>(permissionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", keyGenerator = "principalKeyGenerator")
    public Set<Role> getAllRoles() {
        return new HashSet<>(roleRepository.findAll());
    }

    @Override
    @Transactional
    public void associarPermission(AddPermissionToUserRequest request) {
        // Lógica para associar permissões/roles a um usuário
    }
}
