package com.api.erp.v1.main.shared.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UsuarioAutenticado {
    private final String usuarioId;
    private final String tenantId;
}
