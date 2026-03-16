package com.api.erp.v1.main.migration.domain;

import com.api.erp.v1.main.master.tenant.domain.entity.TenantDatasource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DOMAIN - Evento de Migração de Tenant (Modelo Unificado)
 * 
 * Representa um evento de migração que pode ser disparado tanto na inicialização
 * da aplicação quanto na criação de um novo tenant em runtime.
 * 
 * Este modelo substitui a duplicação entre:
 * - MigrationJobLauncher + TenantMigrationProcessor (inicialização)
 * - MigrationQueueService + MigrationQueueTask (criação de novo tenant)
 * 
 * Responsibilities:
 * - Encapsular dados do tenant a ser migrado
 * - Rastrear estado da migração
 * - Manter logs de execução
 * 
 * @author ERP System
 * @version 2.0 (Refatorado para Fila Unificada)
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class TenantMigrationEvent {
    
    // Identificadores
    private final String eventId;
    private final Long tenantId;
    private final String tenantName;
    
    // Dados
    private final TenantDatasource datasource;
    
    // Controle de Execução
    private volatile MigrationEventStatus status;
    private volatile LocalDateTime enqueuedAt;
    private volatile LocalDateTime startedAt;
    private volatile LocalDateTime completedAt;
    
    // Resultado
    private volatile String errorMessage;
    private volatile MigrationEventResult result;
    
    // Rastreamento de Fases
    private volatile int migrationsExecuted;
    private volatile int seedersExecuted;
    private volatile long executionTimeMs;
    
    // Origem do Evento
    private final MigrationEventSource source;
    private volatile Integer retryCount;
    
    /**
     * Factory method para criar um evento de migração
     */
    public static TenantMigrationEvent create(Long tenantId, String tenantName, 
                                               TenantDatasource datasource,
                                               MigrationEventSource source) {
        return TenantMigrationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .tenantName(tenantName)
                .datasource(datasource)
                .status(MigrationEventStatus.PENDING)
                .enqueuedAt(LocalDateTime.now())
                .source(source)
                .retryCount(0)
                .build();
    }
    
    /**
     * Marca o evento como iniciado
     */
    public synchronized void markStarted() {
        this.status = MigrationEventStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }
    
    /**
     * Marca o evento como sucesso
     */
    public synchronized void markSuccess(int migrationsExecuted, int seedersExecuted) {
        this.status = MigrationEventStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.result = MigrationEventResult.SUCCESS;
        this.migrationsExecuted = migrationsExecuted;
        this.seedersExecuted = seedersExecuted;
        this.executionTimeMs = calculateExecutionTime();
    }
    
    /**
     * Marca o evento como falha
     */
    public synchronized void markFailed(String errorMessage) {
        this.status = MigrationEventStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.result = MigrationEventResult.FAILURE;
        this.errorMessage = errorMessage;
        this.executionTimeMs = calculateExecutionTime();
    }
    
    /**
     * Marca como retry
     */
    public synchronized void markForRetry() {
        this.status = MigrationEventStatus.PENDING;
        this.startedAt = null;
        this.completedAt = null;
        this.errorMessage = null;
        this.result = null;
        this.retryCount = (this.retryCount != null ? this.retryCount : 0) + 1;
    }
    
    /**
     * Calcula tempo de execução em ms
     */
    private long calculateExecutionTime() {
        if (startedAt != null && completedAt != null) {
            return java.time.Duration.between(startedAt, completedAt).toMillis();
        }
        return 0;
    }
    
    /**
     * Returns tempo de espera (desde enfileiramento até início)
     */
    public long getWaitTimeMs() {
        if (enqueuedAt != null && startedAt != null) {
            return java.time.Duration.between(enqueuedAt, startedAt).toMillis();
        }
        return 0;
    }
    
    /**
     * Enum de Status do Evento
     */
    public enum MigrationEventStatus {
        PENDING("Waits forndo"),
        IN_PROGRESS("Em Progresso"),
        COMPLETED("Concluído"),
        FAILED("Falha"),
        CANCELLED("Cancelado");
        
        private final String label;
        
        MigrationEventStatus(String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
        
        public boolean isTerminal() {
            return this == COMPLETED || this == FAILED || this == CANCELLED;
        }
    }
    
    /**
     * Enum de Resultado
     */
    public enum MigrationEventResult {
        SUCCESS("Sucesso"),
        FAILURE("Falha");
        
        private final String label;
        
        MigrationEventResult(String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
    }
    
    /**
     * Enum de Origem do Evento
     */
    public enum MigrationEventSource {
        APPLICATION_STARTUP("Initialization da Aplicação"),
        TENANT_CREATION("Creation de Novo Tenant"),
        MANUAL_REQUEST("Requisição Manual de Suporte");
        
        private final String label;
        
        MigrationEventSource(String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
    }
}
