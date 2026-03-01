package com.api.erp.v1.main.config.startup;

import com.api.erp.v1.main.migration.startup.TenantMigrationStartupWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * INFRASTRUCTURE - Listener de Startup da Aplicação
 * 
 * Responsável pela inicialização da fila unificada de migrações.
 * 
 * Fluxo:
 * 1. Master DataSource é criado (FlywayConfig.java)
 * 2. Migrações Master são executadas (síncrono)
 * 3. ApplicationReadyEvent é disparado
 * 4. Este listener inicializa a fila unificada de tenants
 * 5. Enfileira todos os tenants ativos
 * 6. Inicia o consumidor que processa continuamente
 * 
 * Refatoração v2.0:
 * - Substituiu duplicação de código (Job + Fila assíncrona)
 * - Unificou em um único mecanismo baseado em fila
 * - Centralizado logs e controle de execução
 * 
 * @author ERP System
 * @version 2.0 (Refatorado para Fila Unificada)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupListener {
    
    private final TenantMigrationStartupWorker migrationStartupWorker;

    /**
     * Executa na inicialização da aplicação (após Spring Boot estar pronto)
     * 
     * Inicia a fila unificada de migrações
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeMigrationQueueOnStartup() {
        migrationStartupWorker.initializeAndStart();
    }
}
