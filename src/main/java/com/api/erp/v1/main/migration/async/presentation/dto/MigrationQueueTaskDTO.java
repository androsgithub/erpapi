package com.api.erp.v1.main.migration.async.presentation.dto;

import com.api.erp.v1.main.migration.async.domain.MigrationQueueTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para visualizar detalhes de uma tarefa de migração
 * 
 * @author ERP System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MigrationQueueTaskDTO implements Serializable {
    
    private String taskId;
    private Long tenantId;
    private String tenantName;
    private String status;
    private String statusDescription;
    private LocalDateTime enqueuedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private long waitTimeSeconds;
    private long executionTimeSeconds;
    private int migrationsExecuted;
    private String errorMessage;
    
    public static MigrationQueueTaskDTO fromTask(MigrationQueueTask task) {
        return MigrationQueueTaskDTO.builder()
                .taskId(task.getTaskId())
                .tenantId(task.getTenantId())
                .tenantName(task.getTenantName())
                .status(task.getStatus().name())
                .statusDescription(task.getStatus().getDescription())
                .enqueuedAt(task.getEnqueuedAt())
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .waitTimeSeconds(task.getWaitTimeSeconds())
                .executionTimeSeconds(task.getExecutionTimeSeconds())
                .migrationsExecuted(task.getMigrationsExecuted())
                .errorMessage(task.getErrorMessage())
                .build();
    }
}
