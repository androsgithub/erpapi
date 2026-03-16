package com.api.erp.v1.main.master.user.application.dto.request;

public record CreateUserRequest(
        Long tenantId,
        String nomeCompleto,
        String email,
        String cpf,
        String senha
) {
}
