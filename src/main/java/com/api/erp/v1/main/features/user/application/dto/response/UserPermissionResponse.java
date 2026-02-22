package com.api.erp.v1.main.features.user.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
public class UserPermissionResponse {
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Set<Long> permissionsDiretas;
    private Set<Long> roles;
}


