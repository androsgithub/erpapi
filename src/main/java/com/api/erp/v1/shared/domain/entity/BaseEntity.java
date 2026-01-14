package com.api.erp.v1.shared.domain.entity;

import com.api.erp.v1.shared.domain.valueobject.CustomData;
import com.api.erp.v1.shared.infrastructure.persistence.converters.CustomDataAttributeConverter;
import com.api.erp.v1.shared.infrastructure.persistence.listeners.BaseEntityListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(BaseEntityListener.class)
@SQLRestriction("deleted = false")
public abstract class BaseEntity {

    // =========================
    // ID
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // MULTI-TENANT
    // =========================

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private Long tenantId;

    @Convert(converter = CustomDataAttributeConverter.class)
    @Column(name = "custom_data", columnDefinition = "json")
    private CustomData customData;

    // =========================
    // AUDITORIA - TEMPO
    // =========================

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    // =========================
    // AUDITORIA - USUÁRIO
    // =========================

    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    // =========================
    // SOFT DELETE
    // =========================

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    // =========================
    // CONTROLE DE CONCORRÊNCIA
    // =========================

    @Version
    private Long version;
}
