package com.api.erp.v1.features.usuario.application.dto.request;

public record CreateUsuarioRequest(
        Long tenantId,
        String nomeCompleto,
        String email,
        String cpf,
        String senha
) {
}
