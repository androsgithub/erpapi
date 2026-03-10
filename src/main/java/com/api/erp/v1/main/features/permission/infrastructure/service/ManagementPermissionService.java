package com.api.erp.v1.main.features.permission.infrastructure.service;

import com.api.erp.v1.main.features.permission.application.dto.request.AssociarPermissionRequest;
import com.api.erp.v1.main.features.permission.application.dto.request.CreatePermissionRequest;
import com.api.erp.v1.main.features.permission.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.main.features.permission.domain.entity.Permission;
import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.features.permission.domain.repository.RoleRepository;
import com.api.erp.v1.main.features.permission.domain.service.IManagementPermissionService;
import com.api.erp.v1.main.features.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class ManagementPermissionService implements IManagementPermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Permission createPermission(CreatePermissionRequest request) {
        Permission permission = Permission.builder()
                .codigo(request.codigo())
                .nome(request.nome())
                .modulo(request.modulo())
                .acao(request.acao())
                .build();
        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public Role createRole(CreateRoleRequest request) {
        Role role = Role.builder()
                .nome(request.nome())
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
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", keyGenerator = "principalKeyGenerator")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public void associarPermission(AssociarPermissionRequest request) {
        // Lógica para associar permissões/roles a um usuário
    }
}
