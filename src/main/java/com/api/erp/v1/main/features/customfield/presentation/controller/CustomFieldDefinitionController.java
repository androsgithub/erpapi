package com.api.erp.v1.main.features.customfield.presentation.controller;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.features.customfield.application.dto.request.ChangeCustomFieldStatusRequest;
import com.api.erp.v1.main.features.customfield.application.dto.request.CreateCustomFieldRequest;
import com.api.erp.v1.main.features.customfield.application.dto.request.UpdateCustomFieldRequest;
import com.api.erp.v1.main.features.customfield.application.dto.response.CustomFieldResponse;
import com.api.erp.v1.main.features.customfield.domain.controller.ICustomFieldDefinitionController;
import com.api.erp.v1.docs.openapi.features.customfield.CustomFieldDefinitionOpenApiDocumentation;
import com.api.erp.v1.main.features.customfield.domain.entity.CustomFieldPermissions;
import com.api.erp.v1.main.features.customfield.infrastructure.service.CustomFieldDefinitionService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/src/test/java/com/api/v1/custom-fields")
public class CustomFieldDefinitionController implements ICustomFieldDefinitionController, CustomFieldDefinitionOpenApiDocumentation {


    private final CustomFieldDefinitionService service;

    @Autowired
    public CustomFieldDefinitionController(CustomFieldDefinitionService service) {
        this.service = service;
    }

    /* ===================== LIST ===================== */

    @GetMapping
    @RequiresXTenantId
    @RequiresPermission(CustomFieldPermissions.VISUALIZAR)
    public List<CustomFieldResponse> getCustomFields(@RequestParam String table) {
        Long tenantId = TenantContext.getTenantId();
        return service.listByTable(tenantId, table);
    }

    /* ===================== CREATE ===================== */

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(CustomFieldPermissions.CRIAR)
    public CustomFieldResponse create(

            @RequestBody CreateCustomFieldRequest request
    ) {
        Long tenantId = TenantContext.getTenantId();
        return service.create(tenantId, request);
    }

    /* ===================== UPDATE ===================== */

    @PutMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(CustomFieldPermissions.ATUALIZAR)
    public CustomFieldResponse update(

            @PathVariable Long id,
            @RequestBody UpdateCustomFieldRequest request
    ) {
        Long tenantId = TenantContext.getTenantId();
        return service.update(tenantId, id, request);
    }

    /* ===================== ENABLE / DISABLE ===================== */

    @PatchMapping("/{id}/status")
    @RequiresXTenantId
    @RequiresPermission(CustomFieldPermissions.ATUALIZAR)
    public CustomFieldResponse changeStatus(

            @PathVariable Long id,
            @RequestBody ChangeCustomFieldStatusRequest request
    ) {
        Long tenantId = TenantContext.getTenantId();
        return service.changeStatus(tenantId, id, request);
    }

    /* ===================== DELETE ===================== */

    @DeleteMapping("/{id}")
    @RequiresXTenantId
    @RequiresPermission(CustomFieldPermissions.DELETAR)
    public void delete(

            @PathVariable Long id
    ) {
        Long tenantId = TenantContext.getTenantId();
        service.delete(tenantId, id);
    }
}
