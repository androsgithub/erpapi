package com.api.erp.v1.main.migration.async.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * INFRASTRUCTURE - Configuração de Execução Assíncrona de Migrações
 * 
 * Configura thread pool para executar migrações de tenants em paralelo.
 * O Master continua síncrono para garantir que a app esteja pronta.
 * 
 * @author ERP System
 * @version 1.0
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncMigrationConfig {

    /**
     * ThreadPoolTaskExecutor para migrações de tenants
     * 
     * Propriedades:
     * - Core Size: 2 threads (para não sobrecarregar)
     * - Max Size: 5 threads (máximo de migrações paralelas)
     * - Queue Capacity: 100 (fila de espera)
     * - Thread Name: tenant-migration-
     * - Await termination: true (aguarda todas as tasks completarem)
     */
    @Bean(name = "migrationTaskExecutor")
    public Executor migrationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("tenant-migration-");
        executor.setAwaitTerminationSeconds(300);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        
        log.info("ThreadPoolTaskExecutor configurado para migrações de tenants:");
        log.info("  - Core Pool Size: 2");
        log.info("  - Max Pool Size: 5");
        log.info("  - Queue Capacity: 100");
        
        return executor;
    }
}
