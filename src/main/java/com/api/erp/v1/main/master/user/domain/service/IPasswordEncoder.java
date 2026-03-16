package com.api.erp.v1.main.master.user.domain.service;

public interface IPasswordEncoder {
    String encode(String rawPassword);
}
