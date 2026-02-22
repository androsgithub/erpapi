package com.api.erp.v1.main.shared.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserAutenticado {
    private final String userId;
    private final String tenantId;
}
