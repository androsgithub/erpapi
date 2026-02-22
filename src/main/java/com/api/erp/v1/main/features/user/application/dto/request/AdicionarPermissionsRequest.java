package com.api.erp.v1.main.features.user.application.dto.request;

import java.time.LocalDateTime;
import java.util.Set;

public record AdicionarPermissionsRequest(
    Set<Long> permissionIds,
    LocalDateTime dataInicio,
    LocalDateTime dataFim
) {}
