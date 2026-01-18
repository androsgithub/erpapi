package com.api.erp.v1.observability.domain;

import java.time.Instant;
import java.util.Objects;

public final class FlowEvent {
    private final String traceId;
    private final FlowStep step;
    private final FlowStatus status;
    private final int executionTimeMs;
    private final Instant timestamp;

    public FlowEvent(String traceId, FlowStep step, FlowStatus status, int executionTimeMs) {
        this.traceId = Objects.requireNonNull(traceId, "traceId não pode ser nulo");
        this.step = Objects.requireNonNull(step, "step não pode ser nulo");
        this.status = Objects.requireNonNull(status, "status não pode ser nulo");
        this.executionTimeMs = executionTimeMs;
        this.timestamp = Instant.now();
    }

    public String getTraceId() {
        return traceId;
    }

    public FlowStep getStep() {
        return step;
    }

    public FlowStatus getStatus() {
        return status;
    }

    public int getExecutionTimeMs() {
        return executionTimeMs;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isSuccess() {
        return status.isSuccess();
    }

    public boolean isError() {
        return status.isError();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowEvent flowEvent = (FlowEvent) o;
        return executionTimeMs == flowEvent.executionTimeMs &&
                Objects.equals(traceId, flowEvent.traceId) &&
                Objects.equals(step, flowEvent.step) &&
                status == flowEvent.status &&
                Objects.equals(timestamp, flowEvent.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traceId, step, status, executionTimeMs, timestamp);
    }

    @Override
    public String toString() {
        return "FlowEvent{" +
                "traceId='" + traceId + '\'' +
                ", step=" + step +
                ", status=" + status +
                ", executionTimeMs=" + executionTimeMs +
                ", timestamp=" + timestamp +
                '}';
    }
}
