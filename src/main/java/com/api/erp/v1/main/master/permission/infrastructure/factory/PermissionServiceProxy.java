package com.api.erp.v1.main.master.permission.infrastructure.factory;

import com.api.erp.v1.main.master.permission.domain.service.IPermissionService;
import lombok.RequiredArgsConstructor;

/**
 * Proxy de PermissionService que delega para PermissionServiceHolder.
 */
@RequiredArgsConstructor
public class PermissionServiceProxy implements IPermissionService {

    private final PermissionServiceHolder holder;

    @Override
    public boolean hasPermission(Long userId, String permissionCodigo) {
        return holder.getService().hasPermission(userId, permissionCodigo);
    }
}
