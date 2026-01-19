package com.api.erp.v1.observability.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Filtros específicos para eventos de observabilidade.
 * 
 * Exemplo de uso:
 * {
 *   "status": "ERROR",
 *   "stepName": "validateUser",
 *   "traceId": "550e8400-e29b-41d4-a716-446655440000",
 *   "minExecutionTime": 100,
 *   "maxExecutionTime": 5000,
 *   "startHours": 24,
 *   "endHours": 0
 * }
 */
@Schema(description = "Filtros para busca de eventos de observabilidade")
public class ObservabilityFilter {

    @Schema(description = "Status do evento (SUCCESS, ERROR, VALIDATION_ERROR, etc)", example = "ERROR")
    private String status;

    @Schema(description = "Nome do passo/função", example = "validateUser")
    private String stepName;

    @Schema(description = "ID único da requisição", example = "550e8400-e29b-41d4-a716-446655440000")
    private String traceId;

    @Schema(description = "Tempo mínimo de execução em ms", example = "100")
    private Integer minExecutionTime;

    @Schema(description = "Tempo máximo de execução em ms", example = "5000")
    private Integer maxExecutionTime;

    @Schema(description = "Horas no passado para começar a busca", example = "24")
    private Integer startHours;

    @Schema(description = "Horas no passado para terminar a busca", example = "0")
    private Integer endHours;

    @Schema(description = "Apenas erros?", example = "false")
    private Boolean errorsOnly;

    // Construtores
    public ObservabilityFilter() {
    }

    public ObservabilityFilter(String status) {
        this.status = status;
    }

    public ObservabilityFilter(String status, String stepName) {
        this.status = status;
        this.stepName = stepName;
    }

    // Getters e Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Integer getMinExecutionTime() {
        return minExecutionTime;
    }

    public void setMinExecutionTime(Integer minExecutionTime) {
        this.minExecutionTime = minExecutionTime;
    }

    public Integer getMaxExecutionTime() {
        return maxExecutionTime;
    }

    public void setMaxExecutionTime(Integer maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    public Integer getStartHours() {
        return startHours != null ? startHours : 24;
    }

    public void setStartHours(Integer startHours) {
        this.startHours = startHours;
    }

    public Integer getEndHours() {
        return endHours != null ? endHours : 0;
    }

    public void setEndHours(Integer endHours) {
        this.endHours = endHours;
    }

    public Boolean getErrorsOnly() {
        return errorsOnly != null && errorsOnly;
    }

    public void setErrorsOnly(Boolean errorsOnly) {
        this.errorsOnly = errorsOnly;
    }

    // Métodos de validação
    public boolean hasStatus() {
        return status != null && !status.trim().isEmpty();
    }

    public boolean hasStepName() {
        return stepName != null && !stepName.trim().isEmpty();
    }

    public boolean hasTraceId() {
        return traceId != null && !traceId.trim().isEmpty();
    }

    public boolean hasExecutionTimeRange() {
        return minExecutionTime != null || maxExecutionTime != null;
    }

    public boolean hasAnyFilter() {
        return hasStatus() || hasStepName() || hasTraceId() || hasExecutionTimeRange() || getErrorsOnly();
    }

    /**
     * Converte o status string para código numérico do FlowStatus.
     * Exemplo: "ERROR" → 2
     * 
     * @return código numérico do status, ou -1 se inválido/não definido
     */
    public Integer getStatusCode() {
        if (!hasStatus()) return -1;
        try {
            com.api.erp.v1.observability.domain.FlowStatus flowStatus = 
                com.api.erp.v1.observability.domain.FlowStatus.valueOf(status.toUpperCase());
            return flowStatus.getCode();
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    /**
     * Verifica se o status é válido
     */
    public boolean isStatusValid() {
        if (!hasStatus()) return false;
        try {
            com.api.erp.v1.observability.domain.FlowStatus.valueOf(status.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Cria um filtro apenas para erros
     */
    public static ObservabilityFilter errorsOnly() {
        ObservabilityFilter filter = new ObservabilityFilter();
        filter.setErrorsOnly(true);
        return filter;
    }

    /**
     * Cria um filtro por status
     */
    public static ObservabilityFilter byStatus(String status) {
        return new ObservabilityFilter(status);
    }

    /**
     * Cria um filtro por passo
     */
    public static ObservabilityFilter byStep(String stepName) {
        ObservabilityFilter filter = new ObservabilityFilter();
        filter.setStepName(stepName);
        return filter;
    }

    /**
     * Cria um filtro por trace
     */
    public static ObservabilityFilter byTrace(String traceId) {
        ObservabilityFilter filter = new ObservabilityFilter();
        filter.setTraceId(traceId);
        return filter;
    }

    /**
     * Cria um filtro por tempo de execução
     */
    public static ObservabilityFilter byExecutionTime(Integer min, Integer max) {
        ObservabilityFilter filter = new ObservabilityFilter();
        filter.setMinExecutionTime(min);
        filter.setMaxExecutionTime(max);
        return filter;
    }

    /**
     * Cria um filtro por período
     */
    public static ObservabilityFilter byPeriod(Integer startHours, Integer endHours) {
        ObservabilityFilter filter = new ObservabilityFilter();
        filter.setStartHours(startHours);
        filter.setEndHours(endHours);
        return filter;
    }

    @Override
    public String toString() {
        return "ObservabilityFilter{" +
                "status='" + status + '\'' +
                ", stepName='" + stepName + '\'' +
                ", traceId='" + traceId + '\'' +
                ", minExecutionTime=" + minExecutionTime +
                ", maxExecutionTime=" + maxExecutionTime +
                ", startHours=" + getStartHours() +
                ", endHours=" + getEndHours() +
                ", errorsOnly=" + getErrorsOnly() +
                '}';
    }
}
