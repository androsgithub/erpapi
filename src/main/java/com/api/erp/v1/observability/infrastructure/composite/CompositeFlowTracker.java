package com.api.erp.v1.observability.infrastructure.composite;

import com.api.erp.v1.observability.domain.FlowStatus;
import com.api.erp.v1.observability.domain.FlowStep;
import com.api.erp.v1.observability.domain.FlowTracker;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class CompositeFlowTracker implements FlowTracker {
    private final List<FlowTracker> trackers = new CopyOnWriteArrayList<>();

    public void register(FlowTracker tracker) {
        if (tracker != null && !trackers.contains(tracker)) {
            trackers.add(tracker);
        }
    }

    public void unregister(FlowTracker tracker) {
        trackers.remove(tracker);
    }

    public int getTrackersCount() {
        return trackers.size();
    }

    @Override
    @Async
    public void onStart(String traceId, FlowStep step) {
        trackers.forEach(tracker -> emitOnStart(tracker, traceId, step));
    }

    @Override
    @Async
    public void onFinish(String traceId, FlowStep step, FlowStatus status, int executionTimeMs) {
        trackers.forEach(tracker -> emitOnFinish(tracker, traceId, step, status, executionTimeMs));
    }

    private void emitOnStart(FlowTracker tracker, String traceId, FlowStep step) {
        try {
            tracker.onStart(traceId, step);
        } catch (Exception e) {
            logTrackerError(tracker, "onStart", e);
        }
    }

    private void emitOnFinish(FlowTracker tracker, String traceId, FlowStep step, FlowStatus status, int executionTimeMs) {
        try {
            tracker.onFinish(traceId, step, status, executionTimeMs);
        } catch (Exception e) {
            logTrackerError(tracker, "onFinish", e);
        }
    }

    private void logTrackerError(FlowTracker tracker, String method, Exception e) {
        System.err.printf("Erro ao emitir evento %s em tracker %s: %s%n",
                method, tracker.getClass().getSimpleName(), e.getMessage());
    }
}
