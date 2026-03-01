package com.api.erp.v1.external.observability.presentation.controller;

import com.api.erp.v1.external.observability.application.service.ObservabilityService;
import com.api.erp.v1.external.observability.domain.entity.ObservabilityPermissions;
import com.api.erp.v1.external.observability.presentation.dto.FlowEventDto;
import com.api.erp.v1.external.observability.presentation.dto.ObservabilityFilter;
import com.api.erp.v1.external.observability.presentation.dto.PageableResponse;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Implementação do controller para visualização de eventos de observability.
 *
 * SOLID: Single Responsibility - apenas implementa a lógica dos endpoints
 * Clean Architecture: Controller → Service → Repository
 */
@RestController
@RequestMapping("/api/v1/observability")
public class ObservabilityController implements IObservabilityController {

    private final ObservabilityService observabilityService;

    public ObservabilityController(ObservabilityService observabilityService) {
        this.observabilityService = observabilityService;
    }

    @GetMapping("/trace/{traceId}")
    @RequiresPermission(ObservabilityPermissions.VISUALIZAR)
    @Override
    public ResponseEntity<List<FlowEventDto>> getEventsByTraceId(String traceId) {
        List<FlowEventDto> events = observabilityService.findEventsByTraceId(traceId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/health")
    @RequiresPermission(ObservabilityPermissions.VISUALIZAR)
    @Override
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Observability",
            "timestamp", java.time.Instant.now().toString()
        ));
    }

    @GetMapping("/trace")
    @RequiresPermission(ObservabilityPermissions.VISUALIZAR)
    @Override
    public ResponseEntity<PageableResponse<FlowEventDto>> getAll(
        String status,
        String stepName,
        String traceId,
        Integer minExecutionTime,
        Integer maxExecutionTime,
        int page,
        int pageSize
    ) {
        // Se tem filtros, usar o filtro builder
        boolean hasFilters = status != null || stepName != null || traceId != null || minExecutionTime != null || maxExecutionTime != null;

        if (hasFilters) {
            ObservabilityFilter filter = new ObservabilityFilter();
            if (status != null) filter.setStatus(status);
            if (stepName != null) filter.setStepName(stepName);
            if (traceId != null) filter.setTraceId(traceId);
            if (minExecutionTime != null) filter.setMinExecutionTime(minExecutionTime);
            if (maxExecutionTime != null) filter.setMaxExecutionTime(maxExecutionTime);

            PageableResponse<FlowEventDto> response = observabilityService.findWithFilter(filter, page, pageSize);
            return ResponseEntity.ok(response);
        }

        // Sem filtros, retornar todos com paginação
        PageableResponse<FlowEventDto> response = observabilityService.findAll(pageSize);
        return ResponseEntity.ok(response);
    }
}
