package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.response.TenantConfigResponse;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantConfig;
import org.springframework.stereotype.Component;

/**
 * Mapper for UseCase Response DTOs
 * 
 * Converts domain entities to application response DTOs
 * Used by all tenant UseCases to map domain objects before returning
 */
@Component
public class TenantUseCaseMapper {

    /**
     * Convert Tenant entity to TenantResponse DTO
     * 
     * @param entity Tenant domain entity
     * @return TenantResponse record
     */
    public TenantResponse tenantToResponse(Tenant entity) {
        if (entity == null) {
            return null;
        }

        return new TenantResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail() != null ? entity.getEmail().getValor() : null,
                entity.getPhone() != null ? entity.getPhone().getValor() : null,
                entity.getActive(),
                entity.getTrial(),
                entity.getTrialExpiresAt(),
                entity.getCreatedAt()
        );
    }

    /**
     * Convert TenantConfig entity to TenantConfigResponse DTO
     * 
     * @param entity TenantConfig domain entity
     * @return TenantConfigResponse record
     */
    public TenantConfigResponse tenantConfigToResponse(TenantConfig entity) {
        if (entity == null) {
            return null;
        }

        return new TenantConfigResponse(
                entity.getSlug(),
                entity.getAppName(),
                entity.getAppDescription(),
                entity.getSupportEmail(),
                entity.getMultiBranch(),
                entity.getAllowApiAccess(),
                entity.getWhiteLabel(),
                entity.getMaintenanceMode(),
                entity.getOnboardingDone(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
