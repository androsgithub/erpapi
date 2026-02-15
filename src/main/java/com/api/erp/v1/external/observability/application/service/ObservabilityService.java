package com.api.erp.v1.external.observability.application.service;

import com.api.erp.v1.external.observability.presentation.dto.*;
import com.dros.observability.tracker.database.entity.FlowEventEntity;
import com.dros.observability.tracker.database.repository.FlowEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service para consulta de eventos de observability.
 * <p>
 * Responsável por:
 * - Buscar eventos pelo trace ID
 * - Obter estatísticas de observability
 * - Filtrar eventos por período
 * - Encontrar erros recentes
 * <p>
 * SOLID: Single Responsibility - apenas gerencia dados de observability
 * DDD: Application Service que orquestra domain e infrastructure
 */
@Service
@Transactional(readOnly = true)
public class ObservabilityService {

    private final FlowEventRepository flowEventRepository;

    @Autowired
    public ObservabilityService(FlowEventRepository flowEventRepository) {
        this.flowEventRepository = flowEventRepository;
    }

    /**
     * Busca todos os eventos de um trace ID.
     *
     * @param traceId identificador único da requisição
     * @return lista de eventos do trace
     */
    public List<FlowEventDto> findEventsByTraceId(String traceId) {
        return flowEventRepository.findByTraceIdOrderByTimestamp(traceId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca eventos entre dois momentos no tempo.
     *
     * @param startHoursAgo horas no passado para começar
     * @param endHoursAgo   horas no passado para terminar
     * @return lista de eventos no período
     */
    public List<FlowEventDto> findEventsBetween(int startHoursAgo, int endHoursAgo) {
        Instant end = Instant.now().minus(endHoursAgo, ChronoUnit.HOURS);
        Instant start = Instant.now().minus(startHoursAgo, ChronoUnit.HOURS);

        return flowEventRepository.findEventsBetween(start, end)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca eventos com erro entre dois momentos.
     *
     * @param startHoursAgo horas no passado para começar
     * @param endHoursAgo   horas no passado para terminar
     * @return lista de eventos com erro
     */
    public List<FlowEventDto> findErrorsBetween(int startHoursAgo, int endHoursAgo) {
        Instant end = Instant.now().minus(endHoursAgo, ChronoUnit.HOURS);
        Instant start = Instant.now().minus(startHoursAgo, ChronoUnit.HOURS);

        return flowEventRepository.findErrorsBetween(start, end)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtém estatísticas de observability das últimas horas.
     *
     * @param hoursAgo quantas horas no passado considerar
     * @return estatísticas de observability
     */
    public ObservabilityStatsDto getStats(int hoursAgo) {
        Instant start = Instant.now().minus(hoursAgo, ChronoUnit.HOURS);

        List<FlowEventDto> events = flowEventRepository
                .findEventsBetween(start, Instant.now())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        long totalEvents = events.size();
        long totalErrors = events.stream()
                .filter(e -> e.status().contains("Erro"))
                .count();

        long successRate = totalEvents > 0
                ? ((totalEvents - totalErrors) * 100) / totalEvents
                : 0;

        Map<String, Long> errorsByType = events.stream()
                .filter(e -> e.status().contains("Erro"))
                .collect(Collectors.groupingBy(
                        FlowEventDto::status,
                        Collectors.counting()
                ));

        long averageExecutionTime = (long) events.stream()
                .mapToLong(FlowEventDto::executionTimeMs)
                .average()
                .orElse(0);

        List<FlowEventDto> recent = events.stream()
                .sorted((a, b) -> b.timestamp().compareTo(a.timestamp()))
                .limit(10)
                .collect(Collectors.toList());

        return new ObservabilityStatsDto(
                totalEvents,
                totalErrors,
                successRate,
                errorsByType,
                averageExecutionTime,
                recent
        );
    }

    /**
     * Busca eventos recentes.
     *
     * @param limit quantidade máxima de eventos
     * @return lista de eventos recentes
     */
    public List<FlowEventDto> getRecentEvents(int limit) {
        Instant oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS);

        return flowEventRepository.findEventsBetween(oneDayAgo, Instant.now())
                .stream()
                .limit(limit)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca eventos com paginação e filtros.
     * <p>
     * Exemplo:
     * SearchBuilder builder = SearchBuilder.create()
     * .withStatus("ERROR")
     * .withStepName("validateUser")
     * .withPage(0, 20)
     * .withSort("timestamp", "DESC");
     * <p>
     * PageableResponse<FlowEventDto> response = searchEvents(builder);
     *
     * @param searchBuilder builder com filtros e paginação
     * @return resposta paginada com eventos filtrados
     */
    public PageableResponse<FlowEventDto> searchEvents(SearchBuilder searchBuilder) {
        ObservabilityFilter filter = searchBuilder.buildFilter();

        // Buscar eventos com filtro de período
        Instant start = searchBuilder.getStartInstant();
        Instant end = searchBuilder.getEndInstant();

        List<FlowEventEntity> allEvents = searchBuilder.isErrorsOnly()
                ? flowEventRepository.findErrorsBetween(start, end)
                : flowEventRepository.findEventsBetween(start, end);

        // Aplicar filtros adicionais
        List<FlowEventEntity> filtered = allEvents.stream()
                .filter(e -> {
                    if (!filter.hasStatus()) return true;
                    if (e.getStatus() == null) return false;
                    // Comparar código numérico do status
                    Integer statusCode = filter.getStatusCode();
                    return statusCode >= 0 && e.getStatus().equals(statusCode);
                })
                .filter(e -> filter.hasStepName() ? e.getStepName() != null && e.getStepName().contains(filter.getStepName()) : true)
                .filter(e -> filter.hasTraceId() ? e.getTraceId() != null && e.getTraceId().equals(filter.getTraceId()) : true)
                .filter(e -> filter.getMinExecutionTime() != null && e.getExecutionTimeMs() != null ? e.getExecutionTimeMs() >= filter.getMinExecutionTime() : true)
                .filter(e -> filter.getMaxExecutionTime() != null && e.getExecutionTimeMs() != null ? e.getExecutionTimeMs() <= filter.getMaxExecutionTime() : true)
                .collect(Collectors.toList());

        long totalElements = filtered.size();

        // Aplicar ordenação e paginação
        List<FlowEventDto> page = filtered.stream()
                .sorted((a, b) -> {
                    int comparison = 0;
                    if ("timestamp".equals(searchBuilder.getSortBy())) {
                        comparison = b.getTimestamp().compareTo(a.getTimestamp());
                    } else if ("executionTimeMs".equals(searchBuilder.getSortBy())) {
                        comparison = Integer.compare(b.getExecutionTimeMs(), a.getExecutionTimeMs());
                    } else if ("stepName".equals(searchBuilder.getSortBy())) {
                        comparison = b.getStepName().compareTo(a.getStepName());
                    } else if ("status".equals(searchBuilder.getSortBy())) {
                        comparison = b.getStatus().compareTo(a.getStatus());
                    }

                    if (searchBuilder.getSortDirection().isAscending()) {
                        comparison = -comparison;
                    }
                    return comparison;
                })
                .skip((long) searchBuilder.getPage() * searchBuilder.getPageSize())
                .limit(searchBuilder.getPageSize())
                .map(this::toDto)
                .collect(Collectors.toList());

        return PageableResponse.of(page, searchBuilder.getPage(), searchBuilder.getPageSize(), totalElements);
    }

    /**
     * Busca eventos com paginação simples.
     *
     * @param pageableRequest requisição com paginação
     * @return resposta paginada com eventos
     */
    public PageableResponse<FlowEventDto> searchEvents(PageableRequest pageableRequest) {
        SearchBuilder builder = SearchBuilder.create()
                .withPage(pageableRequest.getPage(), pageableRequest.getPageSize())
                .withSort(pageableRequest.getSortBy(), pageableRequest.getSortDirection());

        // Aplicar filtros dinâmicos
        Map<String, Object> filters = pageableRequest.getFilters();
        if (filters != null) {
            if (filters.containsKey("status")) {
                builder.withStatus(filters.get("status").toString());
            }
            if (filters.containsKey("stepName")) {
                builder.withStepName(filters.get("stepName").toString());
            }
            if (filters.containsKey("traceId")) {
                builder.withTraceId(filters.get("traceId").toString());
            }
            if (filters.containsKey("minExecutionTime")) {
                builder.withMinExecutionTime(((Number) filters.get("minExecutionTime")).intValue());
            }
            if (filters.containsKey("maxExecutionTime")) {
                builder.withMaxExecutionTime(((Number) filters.get("maxExecutionTime")).intValue());
            }
            if (filters.containsKey("startHours")) {
                int startHours = ((Number) filters.get("startHours")).intValue();
                int endHours = filters.containsKey("endHours") ? ((Number) filters.get("endHours")).intValue() : 0;
                builder.withPeriod(startHours, endHours);
            }
            if (filters.containsKey("errorsOnly") && ((Boolean) filters.get("errorsOnly"))) {
                builder.errorsOnly();
            }
        }

        return searchEvents(builder);
    }

    /**
     * Busca eventos com filtro específico e paginação.
     *
     * @param filter     filtro de observabilidade
     * @param pageNumber número da página (começando em 0)
     * @param pageSize   tamanho da página
     * @return resposta paginada com eventos filtrados
     */
    public PageableResponse<FlowEventDto> findWithFilter(ObservabilityFilter filter, int pageNumber, int pageSize) {
        SearchBuilder builder = SearchBuilder.create()
                .withPage(pageNumber, pageSize);

        if (filter.hasStatus()) {
            builder.withStatus(filter.getStatus());
        }
        if (filter.hasStepName()) {
            builder.withStepName(filter.getStepName());
        }
        if (filter.hasTraceId()) {
            builder.withTraceId(filter.getTraceId());
        }
        if (filter.getMinExecutionTime() != null) {
            builder.withMinExecutionTime(filter.getMinExecutionTime());
        }
        if (filter.getMaxExecutionTime() != null) {
            builder.withMaxExecutionTime(filter.getMaxExecutionTime());
        }
        builder.withPeriod(filter.getStartHours(), filter.getEndHours());
        if (filter.getErrorsOnly()) {
            builder.errorsOnly();
        }

        return searchEvents(builder);
    }

    /**
     * Busca todas as páginas de eventos.
     *
     * @param pageSize tamanho da página
     * @return resposta paginada com todos os eventos da primeira página
     */
    public PageableResponse<FlowEventDto> findAll(int pageSize) {
        return findWithFilter(new ObservabilityFilter(), 0, pageSize);
    }

    /**
     * Busca erros recentes.
     *
     * @param hoursAgo quantas horas no passado considerar
     * @return lista de erros recentes
     */
    public List<FlowEventDto> getRecentErrors(int hoursAgo) {
        Instant start = Instant.now().minus(hoursAgo, ChronoUnit.HOURS);

        return flowEventRepository.findErrorsBetween(start, Instant.now())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Converte entidade JPA para DTO.
     */
    private FlowEventDto toDto(FlowEventEntity entity) {
        return FlowEventDto.of(
                entity.getId(),
                entity.getTraceId(),
                entity.getStepName(),
                entity.getStatus(),
                entity.getExecutionTimeMs(),
                entity.getTimestamp()
        );
    }
}
