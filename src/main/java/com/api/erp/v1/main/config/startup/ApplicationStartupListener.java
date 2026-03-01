//package com.api.erp.v1.main.config.startup;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//
///**
// * INFRASTRUCTURE - Listener de Startup da Aplicação
// * <p>
// * Responsável pela inicialização da fila de migrações.
// * <p>
// * Fluxo:
// * 1. Master DataSource é criado (FlywayConfig.java)
// * 2. Migrações Master são executadas (síncrono)
// * 3. ApplicationReadyEvent é disparado
// * 4. Este listener enfileira migrações de tenants (assíncrono)
// * 5. Fila processa tenants em background
// *
// * @author ERP System
// * @version 1.0
// */
//@Component
//@Slf4j
//public class ApplicationStartupListener {
//    private final MTMigrationBoostrap mtMigrationBoostrap;
//    private final MTSeedBootstrap mtSeedBootstrap;
//
//    @Autowired
//    public ApplicationStartupListener(MTMigrationBoostrap mtMigrationBoostrap, MTSeedBootstrap mtSeedBootstrap) {
//        this.mtMigrationBoostrap = mtMigrationBoostrap;
//        this.mtSeedBootstrap = mtSeedBootstrap;
//    }
//
//    /**
//     * Executa na inicialização da aplicação (após Spring Boot estar pronto)
//     * <p>
//     * Estratégia de duas fases:
//     * - Fase 1: Migrações Master (síncrono) - já executadas pelo FlywayConfig
//     * - Fase 2: Migrações de Tenants (assíncrono) - enfileiradas neste método
//     */
//    @EventListener(ApplicationReadyEvent.class)
//    public void runMigrationsOnStartup() {
//        mtMigrationBoostrap.execute();
//        mtSeedBootstrap.execute();
//    }
//}
