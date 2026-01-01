package com.api.erp.v1.features.usuario.domain.service;

public interface IPasswordEncoder {
    String encode(String rawPassword);
}
