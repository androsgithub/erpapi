package com.api.erp.v1.main.migration.async.domain;

import com.api.erp.v1.main.tenant.domain.entity.TenantDatasource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DOMAIN - Tarefa de Migração na Fila
 * 
 * Representa uma tarefa de migração de tenant que aguarda processamento
 * ou está sendo processada na fila assíncrona.
 * 
 * @author ERP System
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public class MigrationQueueTask {
    
    private final String taskId = UUID.randomUUID().toString();
    private final Long tenantId;
    private final String tenantName;
    private final TenantDatasource datasource;
    private final LocalDateTime enqueuedAt = LocalDateTime.now();
    private final boolean executeSeedAfterMigration;
    
    private MigrationStatus status = MigrationStatus.PENDING;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private int migrationsExecuted = 0;
    private int seedersExecuted = 0;
    private boolean seedCompleted = false;
    
    /**
     * Constructor para compatibilidade com código legado (sem seed)
     */
    public MigrationQueueTask(Long tenantId, String tenantName, TenantDatasource datasource) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.datasource = datasource;
        this.executeSeedAfterMigration = false;
    }
    
    /**
     * Marca a task como iniciada
     */
    public void markStarted() {
        this.status = MigrationStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }
    
    /**
     * Marca a task como concluída com sucesso
     */
    public void markCompleted(int migrationsExecuted) {
        this.status = MigrationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.migrationsExecuted = migrationsExecuted;
    }
    
    /**
     * Marca a task como falhada
     */
    public void markFailed(String errorMessage) {
        this.status = MigrationStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }
    
    /**
     * Marca o seed como completado
     */
    public void markSeedCompleted(int seedersExecuted) {
        this.seedCompleted = true;
        this.seedersExecuted = seedersExecuted;
    }
    
    /**
     * Marca o seed como falhado
     */
    public void markSeedFailed(String errorMessage) {
        this.seedCompleted = false;
        this.errorMessage = "Seed falhou: " + errorMessage;
        this.status = MigrationStatus.FAILED;
    }
    
    /**
     * Calcula o tempo de espera em segundos
     */
    public long getWaitTimeSeconds() {
        return java.time.temporal.ChronoUnit.SECONDS.between(enqueuedAt, LocalDateTime.now());
    }
    
    /**
     * Calcula o tempo de execução em segundos (se completada)
     */
    public long getExecutionTimeSeconds() {
        if (startedAt == null || completedAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.SECONDS.between(startedAt, completedAt);
    }
    
    public enum MigrationStatus {
        PENDING("Aguardando execução"),
        IN_PROGRESS("Em progresso"),
        COMPLETED("Concluída com sucesso"),
        FAILED("Falhada");
        
        @Getter
        private final String description;
        
        MigrationStatus(String description) {
            this.description = description;
        }
    }
    
    @Override
    public String toString() {
        return "MigrationQueueTask{" +
                "taskId='" + taskId + '\'' +
                ", tenantId=" + tenantId +
                ", tenantName='" + tenantName + '\'' +
                ", status=" + status +
                ", enqueuedAt=" + enqueuedAt +
                ", waitTimeSeconds=" + getWaitTimeSeconds() +
                '}';
    }
}
