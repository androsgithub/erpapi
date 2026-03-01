package com.api.erp.v1.main.migration.jobs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MigrationResult {

    private final Long tenantId;
    private final boolean success;
    private final String errorMessage;

    public static MigrationResult success(Long tenantId) {
        return new MigrationResult(tenantId, true, null);
    }

    public static MigrationResult failure(Long tenantId, String errorMessage) {
        return new MigrationResult(tenantId, false, errorMessage);
    }
}
