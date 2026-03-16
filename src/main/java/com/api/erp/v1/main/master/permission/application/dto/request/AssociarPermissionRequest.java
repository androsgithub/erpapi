package com.api.erp.v1.main.master.permission.application.dto.request;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AssociarPermissionRequest(
        UUID userId,
        Set<Long> permissionIds,
        Set<Long> roleIds,
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {
}
