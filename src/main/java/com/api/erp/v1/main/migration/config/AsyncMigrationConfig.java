package com.api.erp.v1.main.migration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * INFRASTRUCTURE - Configuração de Async para Sistema de Fila Unificada
 * 
 * Define o executor de threads para processamento assíncrono de migrações.
 * 
 * Features:
 * - Pool de threads configurável (core: 2, max: 5)
 * - Queue capacity de 100 eventos
 * - Prefix "tenant-migration-" para identificação
 * - Graceful shutdown com timeout de 5 minutos
 * 
 * @author ERP System
 * @version 1.0
 */
@Configuration
@EnableAsync
public class AsyncMigrationConfig {
    
    /**
     * Define o executor para tarefas de migração de tenants
     * 
     * Configuração:
     * - Core threads: 2 (threads sempre ativas)
     * - Max threads: 5 (máximo em picos de carga)
     * - Queue capacity: 100 eventos
     * - Shutdown elegante: 5 minutos para finalizar
     * 
     * @return Executor configurado para migrações
     */
    @Bean(name = "migrationTaskExecutor")
    public Executor migrationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Configuração de threads
        executor.setCorePoolSize(2);           // 2 threads por padrão
        executor.setMaxPoolSize(5);            // Máximo 5 em picos
        executor.setQueueCapacity(100);        // Até 100 eventos aguardando
        
        // Configuração de nomes e shutdown
        executor.setThreadNamePrefix("tenant-migration-");
        executor.setAwaitTerminationSeconds(300);  // 5 minutos para finalizar
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Decision: RejectedExecutionHandler.CallerRunsPolicy()
        // Se exceder capacidade, executa na thread chamadora (Fall-back)
        executor.setRejectedExecutionHandler(
                new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        executor.initialize();
        return executor;
    }
}
