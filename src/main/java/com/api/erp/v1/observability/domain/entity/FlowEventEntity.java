package com.api.erp.v1.observability.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "flow_events", indexes = {
        @Index(name = "idx_trace_id", columnList = "trace_id"),
        @Index(name = "idx_step_id", columnList = "step_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class FlowEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trace_id", nullable = false, length = 36)
    private String traceId;

    @Column(name = "step_id", nullable = false)
    private Integer stepId;

    @Column(name = "step_name", nullable = false, length = 255)
    private String stepName;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "execution_time_ms", nullable = false)
    private Integer executionTimeMs;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Construtores
    public FlowEventEntity() {
    }

    public FlowEventEntity(String traceId, Integer stepId, String stepName, 
                          Integer status, Integer executionTimeMs, Instant timestamp) {
        this.traceId = traceId;
        this.stepId = stepId;
        this.stepName = stepName;
        this.status = status;
        this.executionTimeMs = executionTimeMs;
        this.timestamp = timestamp;
        this.createdAt = Instant.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Integer getStepId() {
        return stepId;
    }

    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Integer executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
