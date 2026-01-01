package com.api.erp.v1.features.camposcustom.presentation.controller;

import com.api.erp.v1.features.camposcustom.application.dto.request.ChangeCustomFieldStatusRequest;
import com.api.erp.v1.features.camposcustom.application.dto.request.CreateCustomFieldRequest;
import com.api.erp.v1.features.camposcustom.application.dto.request.UpdateCustomFieldRequest;
import com.api.erp.v1.features.camposcustom.application.dto.response.CustomFieldResponse;
import com.api.erp.v1.features.camposcustom.domain.controller.ICustomFieldDefinitionController;
import com.api.erp.v1.features.camposcustom.domain.entity.CamposCustomPermissions;
import com.api.erp.v1.features.camposcustom.infrastructure.service.CustomFieldDefinitionService;
import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    public List<CustomFieldResponse> getCustomFields(
            @RequestParam String table
    ) {
        return service.listByTable(table);
    }

    /* ===================== CREATE ===================== */

    @PostMapping
    @RequiresPermission(CamposCustomPermissions.CRIAR)
    public CustomFieldResponse create(
            @RequestBody CreateCustomFieldRequest request
    ) {
        return service.create(request);
    }

    /* ===================== UPDATE ===================== */

    @PutMapping("/{id}")
    @RequiresPermission(CamposCustomPermissions.ATUALIZAR)
    public CustomFieldResponse update(
            @PathVariable Long id,
            @RequestBody UpdateCustomFieldRequest request
    ) {
        Long tenantId = null; // resolvido no service
        return service.update(id, tenantId, request);
    }

    /* ===================== ENABLE / DISABLE ===================== */

    @PatchMapping("/{id}/status")
    @RequiresPermission(CamposCustomPermissions.ATUALIZAR)
    public CustomFieldResponse changeStatus(
            @PathVariable Long id,
            @RequestBody ChangeCustomFieldStatusRequest request
    ) {
        Long tenantId = null;
        return service.changeStatus(id, tenantId, request);
    }

    /* ===================== DELETE ===================== */

    @DeleteMapping("/{id}")
    @RequiresPermission(CamposCustomPermissions.DELETAR)
    public void delete(
            @PathVariable Long id
    ) {
        Long tenantId = null;
        service.delete(id, tenantId);
    }
}
