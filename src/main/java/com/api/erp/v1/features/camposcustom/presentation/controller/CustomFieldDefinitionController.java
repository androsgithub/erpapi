package com.api.erp.v1.features.camposcustom.presentation.controller;

import com.api.erp.v1.features.camposcustom.application.dto.request.ChangeCustomFieldStatusRequest;
import com.api.erp.v1.features.camposcustom.application.dto.request.CreateCustomFieldRequest;
import com.api.erp.v1.features.camposcustom.application.dto.request.UpdateCustomFieldRequest;
import com.api.erp.v1.features.camposcustom.application.dto.response.CustomFieldResponse;
import com.api.erp.v1.features.camposcustom.domain.controller.ICustomFieldDefinitionController;
import com.api.erp.v1.features.camposcustom.domain.entity.CamposCustomPermissions;
import com.api.erp.v1.features.camposcustom.infrastructure.service.CustomFieldDefinitionService;
import com.api.erp.v1.tenant.infrastructure.config.datasource.TenantContext;
import com.api.erp.v1.shared.infrastructure.security.annotations.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/custom-fields")
public class CustomFieldDefinitionController implements ICustomFieldDefinitionController {

    @Autowired
    private CustomFieldDefinitionService service;

    /* ===================== LIST ===================== */

    @GetMapping
    @RequiresPermission(CamposCustomPermissions.VISUALIZAR)
    public List<CustomFieldResponse> getCustomFields(@RequestParam String table) {
        Long tenantId = TenantContext.getTenantId();
        return service.listByTable(tenantId, table);
    }

    /* ===================== CREATE ===================== */

    @PostMapping
    @RequiresPermission(CamposCustomPermissions.CRIAR)
    public CustomFieldResponse create(

            @RequestBody CreateCustomFieldRequest request
    ) {
        Long tenantId = TenantContext.getTenantId();
        return service.create(tenantId, request);
    }

    /* ===================== UPDATE ===================== */

    @PutMapping("/{id}")
    @RequiresPermission(CamposCustomPermissions.ATUALIZAR)
    public CustomFieldResponse update(

            @PathVariable Long id,
            @RequestBody UpdateCustomFieldRequest request
    ) {
        Long tenantId = TenantContext.getTenantId();
        return service.update(tenantId, id, request);
    }

    /* ===================== ENABLE / DISABLE ===================== */

    @PatchMapping("/{id}/status")
    @RequiresPermission(CamposCustomPermissions.ATUALIZAR)
    public CustomFieldResponse changeStatus(

            @PathVariable Long id,
            @RequestBody ChangeCustomFieldStatusRequest request
    ) {
        Long tenantId = TenantContext.getTenantId();
        return service.changeStatus(tenantId, id, request);
    }

    /* ===================== DELETE ===================== */

    @DeleteMapping("/{id}")
    @RequiresPermission(CamposCustomPermissions.DELETAR)
    public void delete(

            @PathVariable Long id
    ) {
        Long tenantId = TenantContext.getTenantId();
        service.delete(tenantId, id);
    }
}
