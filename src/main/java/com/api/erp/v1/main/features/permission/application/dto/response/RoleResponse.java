package com.api.erp.v1.main.features.permission.application.dto.response;

import com.api.erp.v1.main.features.permission.domain.entity.Permission;

import java.util.List;
import java.util.Set;

public record RoleResponse(
        Long id,
        String nome,
        Set<Permission> permissions
) {
}
