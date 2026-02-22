package com.api.erp.v1.main.features.user.application.dto.request;

public record CreateUserRequest(
        Long tenantId,
        String nomeCompleto,
        String email,
        String cpf,
        String senha
) {
}
