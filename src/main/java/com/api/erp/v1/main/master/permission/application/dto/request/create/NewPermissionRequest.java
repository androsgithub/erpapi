package com.api.erp.v1.main.master.permission.application.dto.request.create;

import com.api.erp.v1.main.master.permission.domain.entity.PermissionAction;

public record NewPermissionRequest(
        String codigo,
        String nome,
        String modulo,
        PermissionAction acao
) {
}
