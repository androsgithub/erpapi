package com.api.erp.v1.main.master.permission.infrastructure.factory;

import com.api.erp.v1.main.master.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.master.permission.domain.repository.RoleRepository;
import com.api.erp.v1.main.master.permission.domain.service.IManagementPermissionService;
import com.api.erp.v1.main.master.permission.infrastructure.service.ManagementPermissionService;
import com.api.erp.v1.main.master.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ManagementPermissionServiceFactory {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public ManagementPermissionServiceFactory(PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }


    public IManagementPermissionService create() {
        IManagementPermissionService service = new ManagementPermissionService(permissionRepository, roleRepository, userRepository);
        return service;
    }
}
