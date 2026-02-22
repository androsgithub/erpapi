package com.api.erp.v1.main.shared.domain.entity;

import com.api.erp.v1.main.shared.infrastructure.persistence.listeners.BaseEntityListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(BaseEntityListener.class)
@SQLRestriction("deleted = false")
@FilterDef(
        name = "tenantIdFilter",
        parameters = {
                @ParamDef(name = "tenantId", type = Long.class)
        }
)
@Filter(
        name = "tenantIdFilter",
        condition = """
                  (
                    scope = 'GLOBAL'
                    OR tenant_id = :tenantId
                  )
                """
)
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private Long tenantId;

    @Column(name = "tenant_group_id", updatable = false)
    protected Long tenantGroupId;

    @Enumerated(EnumType.STRING)
    private TenantScope scope = TenantScope.TENANT;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Version
    private Long version;
}
