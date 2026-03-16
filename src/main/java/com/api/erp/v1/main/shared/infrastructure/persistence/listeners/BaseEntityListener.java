package com.api.erp.v1.main.shared.infrastructure.persistence.listeners;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.shared.domain.entity.TenantScopeEntity;
import jakarta.persistence.*;

public class BaseEntityListener {

    // =========================
    // BEFORE INSERT
    // =========================
    @PrePersist
    public void beforeCreate(TenantScopeEntity entity) {
        entity.setTenantId(TenantContext.getTenantId());
        entity.setCreatedBy(0L);
    }

    // =========================
    // AFTER INSERT
    // =========================
    @PostPersist
    public void afterCreate(TenantScopeEntity entity) {
        // Ex: publicar evento, log, auditoria externa
    }

    // =========================
    // BEFORE UPDATE
    // =========================
    @PreUpdate
    public void beforeUpdate(TenantScopeEntity entity) {
        entity.setUpdatedBy(0L);
    }

    // =========================
    // AFTER UPDATE
    // =========================
    @PostUpdate
    public void afterUpdate(TenantScopeEntity entity) {
        // Ex: invalidar cache, emitir evento
    }

    // =========================
    // AFTER DELETE
    // =========================
    @PostRemove
    public void afterDelete(TenantScopeEntity entity) {
        // Ex: auditoria, histórico, eventos
    }

    // =========================
    // AFTER LOAD
    // =========================
    @PostLoad
    public void afterLoad(TenantScopeEntity entity) {
        // Ex: cálculos derivados, flags temporárias
    }
}
