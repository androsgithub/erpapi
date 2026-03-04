package com.api.erp.v1.external.observability.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request genérico para paginação e filtros.
 * 
 * Exemplo de uso:
 * {
 *   "page": 0,
 *   "pageSize": 20,
 *   "sortBy": "timestamp",
 *   "sortDirection": "DESC",
 *   "filters": {
 *     "status": "ERROR",
 *     "stepName": "validateUser",
 *     "minExecutionTime": 100,
 *     "maxExecutionTime": 5000
 *   }
 * }
 * 
 * Princípios:
 * - Genérico para qualquer tipo de busca
 * - Filtros dinâmicos via Map
 * - Suporta múltiplas ordenações
 * - Validação de limites
 */
public class PageableRequest {

    @Schema(description = "Número da página (começando em 0)", example = "0", defaultValue = "0")
    private int page = 0;

    @Schema(description = "Quantidade de registros por página", example = "20", defaultValue = "20")
    private int pageSize = 20;

    @Schema(description = "Campo para ordenação", example = "timestamp")
    private String sortBy = "timestamp";

    @Schema(description = "Direção de ordenação (ASC ou DESC)", example = "DESC", defaultValue = "DESC")
    private String sortDirection = "DESC";

    @Schema(description = "Query de busca (texto livre)", example = "validateUser")
    private String query;

    @Schema(description = "Filtros dinâmicos (status, stepName, etc)", example = "{\"status\": \"ERROR\"}")
    private java.util.Map<String, Object> filters;

    // Construtores
    public PageableRequest() {
        this.filters = new java.util.HashMap<>();
    }

    public PageableRequest(int page, int pageSize) {
        this();
        this.page = page;
        this.pageSize = pageSize;
    }

    public PageableRequest(int page, int pageSize, String sortBy, String sortDirection) {
        this(page, pageSize);
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    // Getters e Setters
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(0, page);
    }

    public int getPageSize() {
        // Limitar a 100 registros por página
        return Math.min(pageSize, 100);
    }

    public void setPageSize(int pageSize) {
        this.pageSize = Math.max(1, Math.min(pageSize, 100));
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection != null && sortDirection.equalsIgnoreCase("ASC") ? "ASC" : "DESC";
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public java.util.Map<String, Object> getFilters() {
        if (filters == null) {
            filters = new java.util.HashMap<>();
        }
        return filters;
    }

    public void setFilters(java.util.Map<String, Object> filters) {
        this.filters = filters != null ? filters : new java.util.HashMap<>();
    }

    /**
     * Calcula o offset para JPA
     */
    public int getOffset() {
        return page * getPageSize();
    }

    /**
     * Adiciona um filtro específico
     */
    public void addFilter(String key, Object value) {
        if (filters == null) {
            filters = new java.util.HashMap<>();
        }
        filters.put(key, value);
    }

    /**
     * Remove um filtro específico
     */
    public void removeFilter(String key) {
        if (filters != null) {
            filters.remove(key);
        }
    }

    /**
     * Verifica se tem query de busca
     */
    public boolean hasQuery() {
        return query != null && !query.trim().isEmpty();
    }

    /**
     * Verifica se tem filtros
     */
    public boolean hasFilters() {
        return filters != null && !filters.isEmpty();
    }

    /**
     * Valida a requisição
     */
    public boolean isValid() {
        return page >= 0 && pageSize > 0 && pageSize <= 100;
    }

    @Override
    public String toString() {
        return "PageableRequest{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                ", query='" + query + '\'' +
                ", filters=" + filters +
                '}';
    }
}
