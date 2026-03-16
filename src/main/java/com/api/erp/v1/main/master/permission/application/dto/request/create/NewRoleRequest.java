package com.api.erp.v1.main.master.permission.application.dto.request.create;

import java.util.Set;

public record NewRoleRequest(
        String name,
        Set<Long> permissionIds
) {
}
