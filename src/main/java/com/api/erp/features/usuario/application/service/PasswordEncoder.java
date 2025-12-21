package com.api.erp.features.usuario.application.service;

public interface PasswordEncoder {
    String encode(String rawPassword);
}
