package com.api.erp.v1.main.tenant.application.dto;

import com.api.erp.v1.main.shared.domain.enums.TenantCode;
import com.api.erp.v1.main.shared.domain.enums.TenantType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * UNIFIED CONFIG REQUEST - Consolida todas as configurações de tenant
 * 
 * Substitui os 6 PUTs antigos:
 * - PUT /config/customer
 * - PUT /config/user
 * - PUT /config/permission
 * - PUT /config/tenant
 * - PUT /config/address
 * - PUT /config/contact
 * 
 * Agora: PATCH /config
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UnifiedTenantConfigRequest(
        // === CUSTOMER CONFIG ===
        Boolean customerValidationEnabled,
        Boolean customerAuditEnabled,
        Boolean customerCacheEnabled,
        Boolean customerNotificationEnabled,
        Boolean customerTenantCustomizationEnabled,
        
        // === USER CONFIG ===
        Boolean userApprovalRequired,
        Boolean userCorporateEmailRequired,
        List<String> allowedEmailDomains,
        
        // === PERMISSION CONFIG ===
        Boolean permissionValidationEnabled,
        Boolean permissionCacheEnabled,
        Boolean permissionAuditEnabled,
        
        // === INTERNAL TENANT CONFIG ===
        TenantType tenantType,
        String tenantSubdomain,
        TenantCode tenantCustomCode,
        Boolean tenantFeaturesEnabled,
        
        // === ADDRESS CONFIG ===
        Boolean addressValidationEnabled,
        Boolean addressAuditEnabled,
        Boolean addressCacheEnabled,
        
        // === CONTACT CONFIG ===
        Boolean contactValidationEnabled,
        Boolean contactAuditEnabled,
        Boolean contactCacheEnabled,
        Boolean contactFormatValidationEnabled,
        Boolean contactNotificationEnabled
) {
}
