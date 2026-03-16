package com.api.erp.v1.main.master.user.application.dto.request;

import java.util.Set;

public record AddPermissionsRequest(
    Set<Long> permissionIds
) {}
