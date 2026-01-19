package com.api.erp.v1.observability.application.registry;

import com.api.erp.v1.observability.domain.FlowStep;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class StepRegistry {
    private final Map<String, Integer> nameToId = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public FlowStep getOrRegisterStep(String stepName) {
        int id = nameToId.computeIfAbsent(stepName, k -> nextId.getAndIncrement());
        return new FlowStep(id, stepName);
    }

    public FlowStep getStep(String stepName) {
        Integer id = nameToId.get(stepName);
        return id != null ? new FlowStep(id, stepName) : null;
    }
    public int getRegisteredStepsCount() {
        return nameToId.size();
    }
}
