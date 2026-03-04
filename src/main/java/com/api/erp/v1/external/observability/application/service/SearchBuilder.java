package com.api.erp.v1.external.observability.application.service;

import com.api.erp.v1.external.observability.presentation.dto.ObservabilityFilter;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Helper para construir queries de busca com filtros e paginação.
 * 
 * Exemplo de uso:
 * SearchBuilder builder = SearchBuilder.create()
 *     .withStatus("ERROR")
 *     .withStepName("validateUser")
 *     .withMinExecutionTime(100)
 *     .withPeriod(24, 0)
 *     .withSort("timestamp", Sort.Direction.DESC)
 *     .withPage(0, 20);
 * 
 * Depois use os parâmetros para construir a query JPA.
 */
public class SearchBuilder {

    private ObservabilityFilter filter;
    private int page = 0;
    private int pageSize = 20;
    private String sortBy = "timestamp";
    private Sort.Direction sortDirection = Sort.Direction.DESC;

    private SearchBuilder() {
        this.filter = new ObservabilityFilter();
    }

    /**
     * Cria novo builder
     */
    public static SearchBuilder create() {
        return new SearchBuilder();
    }

    /**
     * Define filtro de status
     */
    public SearchBuilder withStatus(String status) {
        filter.setStatus(status);
        return this;
    }

    /**
     * Define filtro de step name
     */
    public SearchBuilder withStepName(String stepName) {
        filter.setStepName(stepName);
        return this;
    }

    /**
     * Define filtro de trace ID
     */
    public SearchBuilder withTraceId(String traceId) {
        filter.setTraceId(traceId);
        return this;
    }

    /**
     * Define filtro de tempo mínimo
     */
    public SearchBuilder withMinExecutionTime(Integer minTime) {
        filter.setMinExecutionTime(minTime);
        return this;
    }

    /**
     * Define filtro de tempo máximo
     */
    public SearchBuilder withMaxExecutionTime(Integer maxTime) {
        filter.setMaxExecutionTime(maxTime);
        return this;
    }

    /**
     * Define filtro de período
     */
    public SearchBuilder withPeriod(Integer startHours, Integer endHours) {
        filter.setStartHours(startHours);
        filter.setEndHours(endHours);
        return this;
    }

    /**
     * Define apenas erros
     */
    public SearchBuilder errorsOnly() {
        filter.setErrorsOnly(true);
        return this;
    }

    /**
     * Define paginação
     */
    public SearchBuilder withPage(int page, int pageSize) {
        this.page = Math.max(0, page);
        this.pageSize = Math.max(1, Math.min(pageSize, 100));
        return this;
    }

    /**
     * Define ordenação
     */
    public SearchBuilder withSort(String sortBy, String direction) {
        this.sortBy = sortBy != null ? sortBy : "timestamp";
        this.sortDirection = "ASC".equalsIgnoreCase(direction) 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;
        return this;
    }

    /**
     * Define ordenação com Sort.Direction
     */
    public SearchBuilder withSort(String sortBy, Sort.Direction direction) {
        this.sortBy = sortBy;
        this.sortDirection = direction;
        return this;
    }

    /**
     * Constrói o filtro
     */
    public ObservabilityFilter buildFilter() {
        return filter;
    }

    /**
     * Retorna página
     */
    public int getPage() {
        return page;
    }

    /**
     * Retorna tamanho da página
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Calcula offset
     */
    public int getOffset() {
        return page * pageSize;
    }

    /**
     * Retorna campo de ordenação
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Retorna direção de ordenação
     */
    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    /**
     * Retorna Sort do Spring Data
     */
    public Sort getSort() {
        return Sort.by(sortDirection, sortBy);
    }

    /**
     * Calcula Instant para início do período
     */
    public Instant getStartInstant() {
        return Instant.now().minus(filter.getStartHours(), ChronoUnit.HOURS);
    }

    /**
     * Calcula Instant para fim do período
     */
    public Instant getEndInstant() {
        return Instant.now().minus(filter.getEndHours(), ChronoUnit.HOURS);
    }

    /**
     * Verifica se tem filtros aplicados
     */
    public boolean hasFilters() {
        return filter.hasAnyFilter();
    }

    /**
     * Verifica se quer apenas erros
     */
    public boolean isErrorsOnly() {
        return filter.getErrorsOnly();
    }

    /**
     * Verifica se tem período definido
     */
    public boolean hasPeriod() {
        return filter.getStartHours() != null || filter.getEndHours() != null;
    }

    /**
     * Retorna descrição da busca
     */
    @Override
    public String toString() {
        return "SearchBuilder{" +
                "filter=" + filter +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection=" + sortDirection +
                '}';
    }
}
