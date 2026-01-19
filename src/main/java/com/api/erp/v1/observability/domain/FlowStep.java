package com.api.erp.v1.observability.domain;

import java.util.Objects;

/**
 * Representa um passo lógico no fluxo de observabilidade.
 * Imutável e sem dependências de Spring.
 */
public final class FlowStep {
    private final int id;
    private final String name;

    public FlowStep(int id, String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name não pode ser nulo");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowStep flowStep = (FlowStep) o;
        return id == flowStep.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FlowStep{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
