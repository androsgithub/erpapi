package com.api.erp.v1.main.config.startup.seed;

import com.api.erp.v1.main.config.startup.util.PermissionReflectionUtil;
import com.api.erp.v1.main.features.address.domain.entity.AddressPermissions;
import com.api.erp.v1.main.features.contact.domain.entity.ContactPermissions;
import com.api.erp.v1.main.features.businesspartner.domain.entity.BusinessPartnerPermissions;
import com.api.erp.v1.main.features.permission.domain.entity.*;
import com.api.erp.v1.main.features.permission.domain.repository.PermissionRepository;
import com.api.erp.v1.main.features.permission.domain.repository.RoleRepository;
import com.api.erp.v1.main.features.product.domain.entity.CompositionPermissions;
import com.api.erp.v1.main.features.product.domain.entity.ExpandedListPermissions;
import com.api.erp.v1.main.features.product.domain.entity.ProductPermissions;
import com.api.erp.v1.main.features.user.domain.entity.UserPermissions;
import com.api.erp.v1.main.migration.domain.MigrationPermissions;
import com.api.erp.v1.main.shared.domain.entity.BaseEntity;
import com.api.erp.v1.main.shared.domain.entity.TenantScope;
import com.api.erp.v1.main.tenant.domain.entity.TenantPermissions;
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
    public void executar() {
        try {
            log.info("📋 Initializing default permissions and roles...");

            /* ===================== PERMISSÕES ===================== */

            List<Permission> permissionsExistentes = permissionRepository.findAll();
            Map<String, Permission> permissionsPorCodigo = permissionsExistentes.stream()
                    .collect(Collectors.toMap(Permission::getCodigo, p -> p));

            List<Permission> novasPermissions = new ArrayList<>();

            for (Class<?> permissionClass : PERMISSION_CLASSES) {
                try {
                    List<String> codigos = PermissionReflectionUtil.extrairPermissions(permissionClass);
                    String modulo = extrairNomeModulo(permissionClass);

                    for (String codigo : codigos) {
                        if (!permissionsPorCodigo.containsKey(codigo)) {
                            TipoAcao acao = extrairTipoAcao(codigo);
                            Permission novaPermission = Permission.builder()
                                    .codigo(codigo)
                                    .nome(gerarNomeAmigavel(codigo))
                                    .modulo(modulo)
                                    .acao(acao)
                                    .build();
                            novaPermission.setScope(TenantScope.GLOBAL);
                            novasPermissions.add(novaPermission);
                        }
                    }
                } catch (Exception e) {
                    log.warn("⚠️  Error processing class {}: {}", permissionClass.getSimpleName(), e.getMessage());
                }
            }

            if (!novasPermissions.isEmpty()) {
                List<Permission> salvas = permissionRepository.saveAll(novasPermissions);
                salvas.forEach(p -> permissionsPorCodigo.put(p.getCodigo(), p));
                log.info("✅ {} permissions created", novasPermissions.size());
            } else {
                log.info("⏭️  No new permissions to create");
            }

            /* ===================== ROLES ===================== */

            List<Role> rolesExistentes = roleRepository.findAll();
            Set<String> nomesRolesExistentes = rolesExistentes.stream()
                    .map(Role::getNome)
                    .collect(Collectors.toSet());

            List<Role> novasRoles = List.of("USER", "GESTOR", "ADMIN").stream()
                    .filter(nome -> !nomesRolesExistentes.contains(nome))
                    .map(nome -> Role.builder().nome(nome).build())
                    .toList();

            if (!novasRoles.isEmpty()) {
                roleRepository.saveAll(novasRoles);
                log.info("✅ {} roles created", novasRoles.size());
            } else {
                log.info("⏭️  No new role to create");
            }

            /* ===================== ADMIN: só atribui permissões novas ===================== */

            roleRepository.findByNome("ADMIN").ifPresent(roleAdmin -> {

                // Como é LAZY, isso está dentro da @Transactional — funciona normalmente
                Set<Long> permissionsJaVinculadas = roleAdmin.getPermissions()
                        .stream()
                        .map(BaseEntity::getId)
                        .collect(Collectors.toSet());

                List<Permission> newPermissions = permissionsPorCodigo.values().stream()
                        .filter(p -> !permissionsJaVinculadas.contains(p.getId()))
                        .toList();


                if (!newPermissions.isEmpty()) {
                    roleAdmin.getPermissions().addAll(newPermissions);
                    roleRepository.save(roleAdmin);
                    log.info("✅ {} new permissions assigned to ADMIN role", novasPermissions.size());
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

    private String extrairNomeModulo(Class<?> clazz) {
        return clazz.getSimpleName().replace("Permissions", "");
    }

    private TipoAcao extrairTipoAcao(String codigo) {
        if (codigo.endsWith(".criar")) return TipoAcao.CRIAR;
        if (codigo.endsWith(".atualizar")) return TipoAcao.EDITAR;
        if (codigo.endsWith(".visualizar")) return TipoAcao.VISUALIZAR;
        if (codigo.endsWith(".deletar")) return TipoAcao.EXCLUIR;
        return TipoAcao.OUTRO;
    }

    private String gerarNomeAmigavel(String codigo) {
        String[] partes = codigo.split("\\.");
        String acao = partes[1];
        return acao.substring(0, 1).toUpperCase() + acao.substring(1) + " " + partes[0];
    }
}
