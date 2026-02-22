package com.api.erp.v1.main.migration.async.presentation.dto;

import com.api.erp.v1.main.migration.async.service.MigrationQueueService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para resposta de estatísticas da fila de migrações
 * 
 * @author ERP System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MigrationQueueStatsDTO implements Serializable {
    
    private boolean isRunning;
    private long totalTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long completedTasks;
    private long failedTasks;
    private double progressPercentage;
    
    public static MigrationQueueStatsDTO fromStats(MigrationQueueService.MigrationQueueStats stats) {
        return MigrationQueueStatsDTO.builder()
                .isRunning(stats.isRunning())
                .totalTasks(stats.getTotalTasks())
                .pendingTasks(stats.getPendingTasks())
                .inProgressTasks(stats.getInProgressTasks())
                .completedTasks(stats.getCompletedTasks())
                .failedTasks(stats.getFailedTasks())
                .progressPercentage(stats.getProgress())
                .build();
    }
}
