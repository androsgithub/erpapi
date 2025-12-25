package com.api.erp.v1.features.usuario.application.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderImpl implements PasswordEncoder {
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public String encode(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }
}
