package com.api.erp.v1.main.master.user.application.dto.request;


public record LoginRequest(
        String login,
        String password
) {
}
