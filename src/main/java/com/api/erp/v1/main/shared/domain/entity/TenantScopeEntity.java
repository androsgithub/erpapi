package com.api.erp.v1.main.shared.domain.entity;

import com.api.erp.v1.main.shared.infrastructure.persistence.listeners.BaseEntityListener;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
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
public abstract class TenantScopeEntity extends CoreEntity implements Serializable {
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private Long tenantId;

    @Column(name = "tenant_group_id", updatable = false)
    protected Long tenantGroupId;

    @Enumerated(EnumType.STRING)
    private TenantScope scope = TenantScope.TENANT;
}
