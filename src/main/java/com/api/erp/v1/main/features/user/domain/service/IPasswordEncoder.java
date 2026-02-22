package com.api.erp.v1.main.features.user.domain.service;

public interface IPasswordEncoder {
    String encode(String rawPassword);
}
