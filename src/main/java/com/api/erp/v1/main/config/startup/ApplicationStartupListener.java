package com.api.erp.v1.main.config.startup;

import com.api.erp.v1.main.config.startup.seed.MainSeed;
import com.api.erp.v1.main.config.startup.seed.SchemaGenerator;
import com.api.erp.v1.main.migration.startup.TenantMigrationStartupWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * INFRASTRUCTURE - Application Startup Listener
 * <p>
 * Responsible for initializing the unified migration queue.
 * <p>
 * Fluxo:
 * 1. Master DataSource is created (FlywayConfig.java)
 * 2. Master Migrations are executed (synchronously)
 * 3. ApplicationReadyEvent is fired
 * 4. Este listener inicializa a fila unificada de tenants
 * 5. Enfileira todos os tenants ativos
 * 6. Inicia o consumidor que processa continuamente
 * <p>
 * Refactoring v2.0:
 * - Replaced code duplication (Job + Async Queue)
 * - Unificou em um único mecanismo baseado em fila
 * - Centralized logs and execution control
 *
 * @author ERP System
 * @version 2.0 (Refatorado para Fila Unificada)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupListener {

    private final TenantMigrationStartupWorker migrationStartupWorker;
    private final MainSeed mainSeed;

    /**
     * Executes on application initialization (after Spring Boot is ready)
     * <p>
     * Starts the unified migration queue
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeMigrationQueueOnStartup() {
//        SchemaGenerator.executar();
        migrationStartupWorker.initializeAndStart();
    }
}
