package com.api.erp.v1.main.shared.infrastructure.persistence.listeners;

import com.api.erp.v1.main.shared.domain.entity.CoreEntity;
import jakarta.persistence.PreRemove;

import java.time.OffsetDateTime;

public class CoreEntityListener {
    // =========================
    // BEFORE DELETE (lógico)
    // =========================
    @PreRemove
    public void beforeDelete(CoreEntity entity) {
        entity.setDeleted(true);
        entity.setDeletedAt(OffsetDateTime.now());
    }
}
