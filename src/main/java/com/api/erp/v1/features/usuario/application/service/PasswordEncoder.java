package com.api.erp.v1.features.usuario.application.service;

public interface PasswordEncoder {
    String encode(String rawPassword);
}
