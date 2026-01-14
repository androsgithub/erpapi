package com.api.erp.v1.features.camposcustom.presentation.controller;

import com.api.erp.v1.features.camposcustom.application.dto.request.ChangeCustomFieldStatusRequest;
import com.api.erp.v1.features.camposcustom.application.dto.request.CreateCustomFieldRequest;
import com.api.erp.v1.features.camposcustom.application.dto.request.UpdateCustomFieldRequest;
import com.api.erp.v1.features.camposcustom.application.dto.response.CustomFieldResponse;
import com.api.erp.v1.features.camposcustom.domain.controller.ICustomFieldDefinitionController;
import com.api.erp.v1.features.camposcustom.domain.entity.CamposCustomPermissions;
import com.api.erp.v1.features.camposcustom.infrastructure.service.CustomFieldDefinitionService;
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

    @GetMapping("/{tenantId}")
    @RequiresPermission(CamposCustomPermissions.VISUALIZAR)
    public List<CustomFieldResponse> getCustomFields(
            @PathVariable Long tenantId,
            @RequestParam String table
    ) {
        return service.listByTable(tenantId, table);
    }

    /* ===================== CREATE ===================== */

    @PostMapping("/{tenantId}")
    @RequiresPermission(CamposCustomPermissions.CRIAR)
    public CustomFieldResponse create(
            @PathVariable Long tenantId,
            @RequestBody CreateCustomFieldRequest request
    ) {
        return service.create(tenantId, request);
    }

    /* ===================== UPDATE ===================== */

    @PutMapping("/{tenantId}/{id}")
    @RequiresPermission(CamposCustomPermissions.ATUALIZAR)
    public CustomFieldResponse update(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestBody UpdateCustomFieldRequest request
    ) {
        return service.update(tenantId, id, request);
    }

    /* ===================== ENABLE / DISABLE ===================== */

    @PatchMapping("/{tenantId}/{id}/status")
    @RequiresPermission(CamposCustomPermissions.ATUALIZAR)
    public CustomFieldResponse changeStatus(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestBody ChangeCustomFieldStatusRequest request
    ) {
        return service.changeStatus(tenantId, id, request);
    }

    /* ===================== DELETE ===================== */

    @DeleteMapping("/{tenantId}/{id}")
    @RequiresPermission(CamposCustomPermissions.DELETAR)
    public void delete(
            @PathVariable Long tenantId,
            @PathVariable Long id
    ) {
        service.delete(tenantId, id);
    }
}
