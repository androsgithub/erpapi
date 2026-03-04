package com.api.erp.v1.external.observability.presentation.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO para representação de estatísticas de observability.
 */
public record ObservabilityStatsDto(
    long totalEvents,
    long totalErrors,
    long successRate,
    Map<String, Long> errorsByType,
    long averageExecutionTimeMs,
    List<FlowEventDto> recentEvents
) {
}
