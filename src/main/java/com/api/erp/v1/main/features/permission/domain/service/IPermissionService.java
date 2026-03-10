package com.api.erp.v1.main.features.permission.domain.service;

import java.util.Map;

public interface IPermissionService {
    boolean hasPermission(Long userId, String permissionCodigo);
}
