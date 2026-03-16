package com.api.erp.v1.main.config.startup.seed;

import com.api.erp.v1.main.config.startup.util.PermissionReflectionUtil;
import com.api.erp.v1.main.dynamic.features.address.domain.entity.AddressPermissions;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartnerPermissions;
import com.api.erp.v1.main.dynamic.features.contact.domain.entity.ContactPermissions;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.CompositionPermissions;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.ExpandedListPermissions;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.ProductPermissions;
import com.api.erp.v1.main.master.permission.domain.entity.*;
import com.api.erp.v1.main.master.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.master.permission.domain.repository.RoleRepository;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.main.master.user.domain.entity.UserPermissions;
import com.api.erp.v1.main.migration.domain.MigrationPermissions;
import com.api.erp.v1.main.shared.domain.entity.TenantScope;
import com.api.erp.v1.main.shared.domain.entity.TenantScopeEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * COMPONENT - Seeder de Permissões e Roles
 * <p>
 * Inicializa as permissões e roles do sistema durante o bootstrap.
 * Extracts permissions from annotated classes and creates default roles.
 * <p>
 * Fluxo:
 * 1. Extrai permissões de classes UserPermissions, TenantPermissions, etc
 * 2. Creates non-existing permissions in batch
 * 3. Creates default roles (USER, MANAGER, ADMIN)
 * <p>
 * Responsibilities:
 * - Levantar permissões via reflection
 * - Create non-existing permissions
 * - Create default roles
 * - Logging detalhado de progresso
 *
 * @author ERP System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionSeed {

    private static final List<Class<?>> PERMISSION_CLASSES = List.of(
            UserPermissions.class,
            TenantPermissions.class,
            AddressPermissions.class,
            PermissionPermissions.class,
            RolePermissions.class,
            ProductPermissions.class,
            CompositionPermissions.class,
            ExpandedListPermissions.class,
            BusinessPartnerPermissions.class,
            MigrationPermissions.class,
            ContactPermissions.class
    );

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void execute() {
        try {
            log.info("📋 Initializing default permissions and roles...");

            /* ===================== PERMISSÕES ===================== */

            List<Permission> permissionsExistentes = permissionRepository.findAll();
            Map<String, Permission> permissionsPorCodigo = permissionsExistentes.stream()
                    .collect(Collectors.toMap(Permission::getCode, p -> p));

            List<Permission> newPermissions = new ArrayList<>();

            for (Class<?> permissionClass : PERMISSION_CLASSES) {
                Set<String> codes = PermissionReflectionUtil.extractPermissions(permissionClass);
                String module = extractModuleName(permissionClass);

                for (String code : codes) {
                    if (!permissionsPorCodigo.containsKey(code)) {
                        PermissionAction action = extractPermissionAction(code);
                        Permission newPermission = Permission.builder()
                                .code(code)
                                .name(generateFriendlyName(code))
                                .module(module)
                                .action(action)
                                .build();
                        newPermission.setScope(TenantScope.GLOBAL);
                        newPermissions.add(newPermission);
                    }
                }
            }

            if (!newPermissions.isEmpty()) {
                List<Permission> salvas = permissionRepository.saveAll(newPermissions);
                salvas.forEach(p -> permissionsPorCodigo.put(p.getCode(), p));
                log.info("✅ {} permissions created", newPermissions.size());
            } else {
                log.info("⏭️  No new permissions to create");
            }

            /* ===================== ADMIN: só atribui permissões novas ===================== */

            roleRepository.findByName("ADMIN").ifPresent(roleAdmin -> {

                // Como é LAZY, isso está dentro da @Transactional — funciona normalmente
                Set<Long> permissionsJaVinculadas = roleAdmin.getPermissions()
                        .stream()
                        .map(TenantScopeEntity::getId)
                        .collect(Collectors.toSet());

                List<Permission> newPermissionsToAdd = permissionsPorCodigo.values().stream()
                        .filter(p -> !permissionsJaVinculadas.contains(p.getId()))
                        .toList();


                if (!newPermissionsToAdd.isEmpty()) {
                    roleAdmin.getPermissions().addAll(newPermissionsToAdd);
                    roleRepository.save(roleAdmin);
                    log.info("✅ {} new permissions assigned to ADMIN role", newPermissionsToAdd.size());
                } else {
                    log.info("⏭️  ADMIN role already has all permissions");
                }
            });

            log.info("✅ Permissions and roles initialized successfully");

        } catch (Exception e) {
            log.error("❌ Error initializing permissions and roles:", e);
            throw new RuntimeException("Falha ao inicializar permissões e roles", e);
        }
    }

    /* ===================== MÉTODOS AUXILIARES ===================== */

    private String extractModuleName(Class<?> clazz) {
        return clazz.getSimpleName().replace("Permissions", "");
    }

    private PermissionAction extractPermissionAction(String code) {
        if (code.endsWith(BasePermissions.OPERATION_CREATE)) return PermissionAction.CREATE;
        if (code.endsWith(BasePermissions.OPERATION_UPDATE)) return PermissionAction.EDIT;
        if (code.endsWith(BasePermissions.OPERATION_VIEW)) return PermissionAction.VIEW;
        if (code.endsWith(BasePermissions.OPERATION_DELETE)) return PermissionAction.DELETE;
        return PermissionAction.OTHER;
    }

    private String generateFriendlyName(String codigo) {
        String[] partes = codigo.split("\\.");
        String acao = partes[1];
        return acao.substring(0, 1).toUpperCase() + acao.substring(1) + " " + partes[0];
    }
}
