package com.api.erp.v1.main.features.usuario.application.dto.request;


public record LoginRequest(
        String login,
        String password
) {
}
