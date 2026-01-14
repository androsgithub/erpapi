package com.api.erp.v1.features.usuario.application.dto.request;


public record LoginRequest(
        String login,
        String password
) {
}
