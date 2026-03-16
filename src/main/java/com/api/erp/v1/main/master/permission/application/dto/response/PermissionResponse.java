package com.api.erp.v1.main.master.permission.application.dto.response;

import com.api.erp.v1.main.master.permission.domain.entity.PermissionAction;

public record PermissionResponse(
        String code,
        String name,
        String module,
        PermissionAction action
) {
}
