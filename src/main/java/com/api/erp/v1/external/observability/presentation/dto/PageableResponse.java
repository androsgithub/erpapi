package com.api.erp.v1.external.observability.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response genérica para paginação.
 * 
 * Exemplo de resposta:
 * {
 *   "data": [...],
 *   "page": 0,
 *   "pageSize": 20,
 *   "totalElements": 150,
 *   "totalPages": 8,
 *   "hasNext": true,
 *   "hasPrevious": false,
 *   "isFirst": true,
 *   "isLast": false
 * }
 * 
 * Genérico para qualquer tipo de conteúdo:
 * - PageableResponse<FlowEventDto>
 * - PageableResponse<ObservabilityStatsDto>
 * - etc
 */
@Schema(description = "Resposta paginada genérica")
public class PageableResponse<T> {

    @Schema(description = "Lista de dados da página")
    private List<T> data;

    @Schema(description = "Número da página atual (começando em 0)", example = "0")
    private int page;

    @Schema(description = "Quantidade de registros por página", example = "20")
    private int pageSize;

    @Schema(description = "Total de registros disponíveis", example = "150")
    private long totalElements;

    @Schema(description = "Total de páginas", example = "8")
    private int totalPages;

    @Schema(description = "Tem próxima página?", example = "true")
    private boolean hasNext;

    @Schema(description = "Tem página anterior?", example = "false")
    private boolean hasPrevious;

    @Schema(description = "É a primeira página?", example = "true")
    private boolean isFirst;

    @Schema(description = "É a última página?", example = "false")
    private boolean isLast;

    // Construtor padrão
    public PageableResponse() {
    }

    // Construtor completo
    public PageableResponse(List<T> data, int page, int pageSize, long totalElements) {
        this.data = data;
        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.hasNext = page < (totalPages - 1);
        this.hasPrevious = page > 0;
        this.isFirst = page == 0;
        this.isLast = page == (totalPages - 1);
    }

    // Getters
    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    /**
     * Factory method para criar resposta paginada
     */
    public static <T> PageableResponse<T> of(List<T> data, int page, int pageSize, long totalElements) {
        return new PageableResponse<>(data, page, pageSize, totalElements);
    }

    /**
     * Factory method para resposta vazia
     */
    public static <T> PageableResponse<T> empty(int page, int pageSize) {
        return new PageableResponse<>(List.of(), page, pageSize, 0);
    }

    @Override
    public String toString() {
        return "PageableResponse{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                ", isFirst=" + isFirst +
                ", isLast=" + isLast +
                ", dataSize=" + (data != null ? data.size() : 0) +
                '}';
    }
}
