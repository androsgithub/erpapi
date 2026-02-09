package com.api.erp.v1.shared.infrastructure.persistence.listeners;

import com.api.erp.v1.shared.domain.entity.BaseEntity;
import com.api.erp.v1.tenant.infrastructure.config.datasource.TenantContext;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

public class BaseEntityListener {

    // =========================
    // BEFORE INSERT
    // =========================
    @PrePersist
    public void beforeCreate(BaseEntity entity) {
        entity.setTenantId(TenantContext.getTenantId());
        entity.setTenantGroupId(TenantContext.getGroupId());
        entity.setCreatedBy(0L);
    }

    // =========================
    // AFTER INSERT
    // =========================
    @PostPersist
    public void afterCreate(BaseEntity entity) {
        // Ex: publicar evento, log, auditoria externa
    }

    // =========================
    // BEFORE UPDATE
    // =========================
    @PreUpdate
    public void beforeUpdate(BaseEntity entity) {
        entity.setUpdatedBy(0L);
    }

    // =========================
    // AFTER UPDATE
    // =========================
    @PostUpdate
    public void afterUpdate(BaseEntity entity) {
        // Ex: invalidar cache, emitir evento
    }

    // =========================
    // BEFORE DELETE (lógico)
    // =========================
    @PreRemove
    public void beforeDelete(BaseEntity entity) {
        entity.setDeleted(true);
        entity.setDeletedAt(OffsetDateTime.now());
    }

    // =========================
    // AFTER DELETE
    // =========================
    @PostRemove
    public void afterDelete(BaseEntity entity) {
        // Ex: auditoria, histórico, eventos
    }

    // =========================
    // AFTER LOAD
    // =========================
    @PostLoad
    public void afterLoad(BaseEntity entity) {
        // Ex: cálculos derivados, flags temporárias
    }
}
