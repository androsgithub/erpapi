package com.api.erp.v1.main.features.permission.infrastructure.factory;

import com.api.erp.v1.main.features.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.features.permission.domain.repository.RoleRepository;
import com.api.erp.v1.main.features.permission.domain.service.IGerenciamentoPermissionService;
import com.api.erp.v1.main.features.permission.infrastructure.service.GerenciamentoPermissionService;
import com.api.erp.v1.main.features.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GerenciamentoPermissionServiceFactory {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public GerenciamentoPermissionServiceFactory(PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }


    public IGerenciamentoPermissionService create() {
        IGerenciamentoPermissionService service = new GerenciamentoPermissionService(permissionRepository, roleRepository, userRepository);
        return service;
    }
}
