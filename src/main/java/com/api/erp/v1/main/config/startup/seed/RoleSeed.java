package com.api.erp.v1.main.config.startup.seed;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.master.permission.domain.entity.Role;
import com.api.erp.v1.main.master.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.master.permission.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleSeed {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void execute() {
        /* ===================== ROLES ===================== */

        List<Role> rolesExistentes = roleRepository.findAll();
        Set<String> nomesRolesExistentes = rolesExistentes.stream()
                .filter(role -> role.getTenantId().equals(TenantContext.getTenantId()))
                .map(Role::getName)
                .collect(Collectors.toSet());

        List<Role> novasRoles = Stream.of("USER", "GESTOR", "ADMIN")
                .filter(nome -> !nomesRolesExistentes.contains(nome))
                .map(nome -> Role.builder().name(nome).build())
                .toList();

        if (!novasRoles.isEmpty()) {
            roleRepository.saveAll(novasRoles);
            log.info("✅ {} roles created", novasRoles.size());
        } else {
            log.info("⏭️  No new role to create");
        }
    }
}
