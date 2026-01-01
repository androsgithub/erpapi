package com.api.erp.v1.shared.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UsuarioAutenticado {
    private final Long usuarioId;
    private final Long tenantId;
}
