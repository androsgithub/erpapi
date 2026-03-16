package com.api.erp.v1.main.config.startup.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * COMPONENT - Initialization Seeders Coordinator
 * <p>
 * Orchestrates execution of all seeders in correct order.
 * Each seeder is responsible for initializing specific module data.
 * <p>
 * Responsibilities:
 * - Execute permissions before users
 * - Execute users before units
 * - Tratamento centralizado de erros
 * - Logging de progresso
 *
 * @author ERP System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MainSeed {

    private final PermissionSeed permissionSeed;
    private final UserAdminSeed userAdminSeed;
    private final MeasureUnitSeed measureUnitSeed;
    private final RoleSeed roleSeed;

    @Value("${dros.erpapi.bootstrap.permission:false}")
    private boolean bootstrapPermission;
    @Value("${dros.erpapi.bootstrap.user-admin:false}")
    private boolean bootstrapUserAdmin;

    public void execute() {
        if (!(bootstrapUserAdmin || bootstrapPermission)) return;

        log.info("📋 Starting initialization seeders execution...");
        int sucessos = 0;
        int erros = 0;

        if (bootstrapPermission) {
            try {
                log.debug("1️⃣  Running roles seeder...");
                roleSeed.execute();
                log.info("✅ Roles initialized successfully");
                sucessos++;
            } catch (Exception e) {
                log.error("❌ Error executing roles seeder:", e);
                erros++;
            }
        }

        if (bootstrapPermission) {
            try {
                log.debug("1️⃣  Running permissions seeder...");
                permissionSeed.execute();
                log.info("✅ Permissions initialized successfully");
                sucessos++;
            } catch (Exception e) {
                log.error("❌ Error executing permissions seeder:", e);
                erros++;
            }
        }

        if (bootstrapUserAdmin) {
            try {
                log.debug("2️⃣  Running admin user seeder...");
                userAdminSeed.execute();
                log.info("✅ Admin user initialized successfully");
                sucessos++;
            } catch (Exception e) {
                log.error("❌ Error executing admin user seeder:", e);
                erros++;
            }
        }

        if (erros == 0) {
            log.info("✅ All {} seeders were executed successfully", sucessos);
        } else {
            log.warn("⚠️  Seeder execution completed: {} success(es), {} error(s)", sucessos, erros);
        }
    }
}
