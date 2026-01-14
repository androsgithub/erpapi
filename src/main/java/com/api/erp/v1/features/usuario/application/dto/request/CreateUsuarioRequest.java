package com.api.erp.v1.features.usuario.application.dto.request;

public record CreateUsuarioRequest(
        String tenantId,
        String nomeCompleto,
        String email,
        String cpf,
        String senha
) {
}
