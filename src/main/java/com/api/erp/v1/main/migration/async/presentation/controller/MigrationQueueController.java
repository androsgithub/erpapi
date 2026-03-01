package com.api.erp.v1.main.migration.async.presentation.controller;

import com.api.erp.v1.main.migration.async.domain.MigrationQueueTask;
import com.api.erp.v1.main.migration.async.service.MigrationQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller para monitoramento da Fila de Migrações
 * 
 * Endpoints para consultar status das migrações assíncronas.
 * 
 * @author ERP System
 * @version 1.0
 */
@RestController
@RequestMapping("/src/test/java/com/api/v1/migrations/queue")
@RequiredArgsConstructor
@Slf4j
public class MigrationQueueController {
    
    private final MigrationQueueService migrationQueueService;
    
    /**
     * GET /api/v1/migrations/queue/stats
     * Retorna estatísticas gerais da fila
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getQueueStats() {
        var stats = migrationQueueService.getStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("isRunning", stats.isRunning());
        response.put("totalTasks", stats.getTotalTasks());
        response.put("pendingTasks", stats.getPendingTasks());
        response.put("inProgressTasks", stats.getInProgressTasks());
        response.put("completedTasks", stats.getCompletedTasks());
        response.put("failedTasks", stats.getFailedTasks());
        response.put("progressPercentage", String.format("%.2f%%", stats.getProgress()));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/migrations/queue/tasks
     * Lista todas as tasks registradas
     */
    @GetMapping("/tasks")
    public ResponseEntity<Map<String, Object>> getAllTasks() {
        Collection<MigrationQueueTask> tasks = migrationQueueService.getAllTasks();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalTasks", tasks.size());
        response.put("tasks", tasks.stream().map(this::taskToDTO).toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/migrations/queue/tasks/in-progress
     * Lista tasks em progresso
     */
    @GetMapping("/tasks/in-progress")
    public ResponseEntity<Map<String, Object>> getInProgressTasks() {
        Collection<MigrationQueueTask> tasks = migrationQueueService.getInProgressTasks();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalTasks", tasks.size());
        response.put("tasks", tasks.stream().map(this::taskToDTO).toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/migrations/queue/tasks/failed
     * Lista tasks que falharam
     */
    @GetMapping("/tasks/failed")
    public ResponseEntity<Map<String, Object>> getFailedTasks() {
        Collection<MigrationQueueTask> tasks = migrationQueueService.getFailedTasks();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalTasks", tasks.size());
        response.put("tasks", tasks.stream().map(this::taskToDTO).toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/migrations/queue/tasks/{taskId}
     * Retorna detalhes de uma task específica
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<?> getTask(@PathVariable String taskId) {
        return migrationQueueService.getTask(taskId)
                .map(task -> ResponseEntity.ok((Object) taskToDTO(task)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/v1/migrations/queue/tasks/tenant/{tenantId}
     * Retorna tasks de um tenant específico
     */
    @GetMapping("/tasks/tenant/{tenantId}")
    public ResponseEntity<Map<String, Object>> getTenantTasks(@PathVariable Long tenantId) {
        var tasks = migrationQueueService.getAllTasks().stream()
                .filter(t -> t.getTenantId().equals(tenantId))
                .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("tenantId", tenantId);
        response.put("totalTasks", tasks.size());
        response.put("tasks", tasks.stream().map(this::taskToDTO).toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Converte MigrationQueueTask para DTO
     */
    private Map<String, Object> taskToDTO(MigrationQueueTask task) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("taskId", task.getTaskId());
        dto.put("tenantId", task.getTenantId());
        dto.put("tenantName", task.getTenantName());
        dto.put("status", task.getStatus().name());
        dto.put("statusDescription", task.getStatus().getDescription());
        dto.put("enqueuedAt", task.getEnqueuedAt());
        dto.put("startedAt", task.getStartedAt());
        dto.put("completedAt", task.getCompletedAt());
        dto.put("waitTimeSeconds", task.getWaitTimeSeconds());
        dto.put("executionTimeSeconds", task.getExecutionTimeSeconds());
        dto.put("migrationsExecuted", task.getMigrationsExecuted());
        
        if (task.getErrorMessage() != null) {
            dto.put("errorMessage", task.getErrorMessage());
        }
        
        return dto;
    }
}
