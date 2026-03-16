package com.api.erp.v1.main.master.permission.application.dto.request.edit;

import java.util.Set;

public record AddPermissionToUserRequest(
        Long userId,
        Set<Long> permissionIds,
        Set<Long> roleIds
) {
}
