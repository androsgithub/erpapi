package com.api.erp.v1.main.migration.async.service;

import com.api.erp.v1.main.migration.async.domain.MigrationQueueTask;
import com.api.erp.v1.main.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.main.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.tenant.domain.repository.TenantRepository;
import com.api.erp.v1.main.tenant.infrastructure.config.TenantMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DOMAIN - Serviço de Fila de Migrações
 * 
 * Gerencia a fila assíncrona de migrações de tenants.
 * Enfileira tasks de migração para serem executadas em background
 * sem bloquear a inicialização da aplicação.
 * 
 * Responsabilidades:
 * - Enfileirar migrações de tenants
 * - Executar migrações assincronamente
 * - Rastrear status de cada migração
 * - Fornecer relatório de execução
 * 
 * @author ERP System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationQueueService {
    
    private final TenantMigrationService tenantMigrationService;
    private final TenantRepository tenantRepository;
    private final TenantDatasourceRepository tenantDatasourceRepository;
    
    // Fila sincronizada para rastrear tasks
    private final Map<String, MigrationQueueTask> taskRegistry = new ConcurrentHashMap<>();
    private final Queue<MigrationQueueTask> taskQueue = new LinkedList<>();
    
    private volatile boolean isRunning = false;
    
    /**
     * Enfileira migrações para todos os tenants ativos
     */
    public synchronized void enqueueAllTenantMigrations() {
        log.info("⏳ Enfileirando migrações de tenants...");
        
        try {
            var tenants = tenantRepository.findAllByAtivaTrue();
            
            if (tenants.isEmpty()) {
                log.warn("⚠️ Nenhum tenant ativo encontrado");
                return;
            }
            
            int enqueued = 0;
            for (var tenant : tenants) {
                try {
                    TenantDatasource datasource = tenantDatasourceRepository
                            .findByTenantIdAndStatus(tenant.getId(), true);
                    
                    if (datasource == null) {
                        log.warn("Tenant {} não possui datasource configurado", tenant.getNome());
                        continue;
                    }
                    
                    MigrationQueueTask task = new MigrationQueueTask(
                            tenant.getId(),
                            tenant.getNome(),
                            datasource
                    );
                    
                    taskQueue.offer(task);
                    taskRegistry.put(task.getTaskId(), task);
                    enqueued++;
                    
                    log.debug("✅ Tarefa enfileirada: {} ({})", tenant.getNome(), task.getTaskId());
                    
                } catch (Exception e) {
                    log.error("Erro ao enfileirar migração para tenant: {}", tenant.getNome(), e);
                }
            }
            
            log.info("📊 Total de tarefas enfileiradas: {}/{}", enqueued, tenants.size());
            
        } catch (Exception e) {
            log.error("Erro crítico ao enfileirar migrações", e);
        }
    }
    
    /**
     * Enfileira migração de um tenant específico
     */
    public MigrationQueueTask enqueueTenantMigration(Long tenantId) {
        var tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant não encontrado: " + tenantId));
        
        TenantDatasource datasource = tenantDatasourceRepository
                .findByTenantIdAndStatus(tenantId, true);
        
        if (datasource == null) {
            throw new IllegalArgumentException("Datasource não configurado para tenant: " + tenantId);
        }
        
        MigrationQueueTask task = new MigrationQueueTask(tenantId, tenant.getNome(), datasource);
        taskQueue.offer(task);
        taskRegistry.put(task.getTaskId(), task);
        
        log.info("✅ Tarefa de migração enfileirada para tenant: {} ({})", tenant.getNome(), task.getTaskId());
        
        return task;
    }
    
    /**
     * Inicia o processamento assíncrono da fila
     */
    @Async("migrationTaskExecutor")
    public void processMigrationQueue() {
        if (isRunning) {
            log.warn("Processamento da fila já está em execução");
            return;
        }
        
        synchronized (this) {
            if (isRunning) return;
            isRunning = true;
        }
        
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║     INICIANDO PROCESSAMENTO ASSÍNCRONO DA FILA DE MIGRAÇÕES    ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("");
        
        try {
            int totalTasks = taskQueue.size();
            int processedTasks = 0;
            int successCount = 0;
            int failureCount = 0;
            
            while (!taskQueue.isEmpty()) {
                MigrationQueueTask task = taskQueue.poll();
                if (task == null) break;
                
                processedTasks++;
                log.info("");
                log.info("───────────────────────────────────────────────────────────────");
                log.info("▶ Processando tarefa {} de {}: {} [TaskID: {}]", 
                        processedTasks, totalTasks, task.getTenantName(), task.getTaskId());
                log.info("───────────────────────────────────────────────────────────────");
                
                try {
                    task.markStarted();
                    processMigrationTask(task);
                    successCount++;
                    
                } catch (Exception e) {
                    failureCount++;
                    task.markFailed(e.getMessage());
                    log.error("❌ Erro ao processar migração de {}: {}", task.getTenantName(), e.getMessage());
                }
                
                // Log de progresso
                long waitTime = task.getWaitTimeSeconds();
                long execTime = task.getExecutionTimeSeconds();
                log.info("⏱️ Tempo de espera: {}s | Tempo de execução: {}s", waitTime, execTime);
                log.info("📊 Status: {} | Migrações aplicadas: {}", task.getStatus(), task.getMigrationsExecuted());
            }
            
            log.info("");
            log.info("╔════════════════════════════════════════════════════════════════╗");
            log.info("║     FILA DE MIGRAÇÕES PROCESSADA COM SUCESSO                   ║");
            log.info("╚════════════════════════════════════════════════════════════════╝");
            log.info("✅ Sucesso: {} | ❌ Falha: {} | 📊 Total: {}", 
                    successCount, failureCount, totalTasks);
            
        } finally {
            synchronized (this) {
                isRunning = false;
            }
            log.info("🏁 Processamento da fila finalizado");
        }
    }
    
    /**
     * Processa uma única tarefa de migração
     */
    private void processMigrationTask(MigrationQueueTask task) throws Exception {
        log.info("🔄 Executando migração para tenant: {}", task.getTenantName());
        
        try {
            tenantMigrationService.migrateTenantById(task.getTenantId());
            
            // Simula obtenção do número de migrações (seria melhor ter isso no serviço)
            task.markCompleted(0);
            log.info("✅ Migração concluída com sucesso: {}", task.getTenantName());
            
        } catch (Exception e) {
            log.error("❌ Erro ao migrar tenant {}: {}", task.getTenantName(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * Retorna informações sobre uma tarefa específica
     */
    public Optional<MigrationQueueTask> getTask(String taskId) {
        return Optional.ofNullable(taskRegistry.get(taskId));
    }
    
    /**
     * Retorna todas as tasks registradas
     */
    public Collection<MigrationQueueTask> getAllTasks() {
        return Collections.unmodifiableCollection(taskRegistry.values());
    }
    
    /**
     * Retorna tasks que estão em progresso
     */
    public Collection<MigrationQueueTask> getInProgressTasks() {
        return taskRegistry.values().stream()
                .filter(task -> task.getStatus() == MigrationQueueTask.MigrationStatus.IN_PROGRESS)
                .toList();
    }
    
    /**
     * Retorna tasks que falharam
     */
    public Collection<MigrationQueueTask> getFailedTasks() {
        return taskRegistry.values().stream()
                .filter(task -> task.getStatus() == MigrationQueueTask.MigrationStatus.FAILED)
                .toList();
    }
    
    /**
     * Retorna estatísticas da fila
     */
    public MigrationQueueStats getStats() {
        Collection<MigrationQueueTask> allTasks = taskRegistry.values();
        
        long totalTasks = allTasks.size();
        long pendingTasks = allTasks.stream()
                .filter(t -> t.getStatus() == MigrationQueueTask.MigrationStatus.PENDING)
                .count();
        long inProgressTasks = allTasks.stream()
                .filter(t -> t.getStatus() == MigrationQueueTask.MigrationStatus.IN_PROGRESS)
                .count();
        long completedTasks = allTasks.stream()
                .filter(t -> t.getStatus() == MigrationQueueTask.MigrationStatus.COMPLETED)
                .count();
        long failedTasks = allTasks.stream()
                .filter(t -> t.getStatus() == MigrationQueueTask.MigrationStatus.FAILED)
                .count();
        
        return new MigrationQueueStats(totalTasks, pendingTasks, inProgressTasks, completedTasks, failedTasks, isRunning);
    }
    
    /**
     * Classe para estatísticas da fila
     */
    @lombok.Getter
    @lombok.RequiredArgsConstructor
    public static class MigrationQueueStats {
        private final long totalTasks;
        private final long pendingTasks;
        private final long inProgressTasks;
        private final long completedTasks;
        private final long failedTasks;
        private final boolean isRunning;
        
        public double getProgress() {
            if (totalTasks == 0) return 100.0;
            return ((double) (completedTasks + failedTasks) / totalTasks) * 100.0;
        }
    }
}
