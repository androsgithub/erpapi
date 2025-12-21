package com.api.erp.shared.infrastructure.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

public class BearerTokenAuthentication extends AbstractAuthenticationToken {
    
    private final String token;
    private final String username;
    
    public BearerTokenAuthentication(String token, String username) {
        super(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        this.token = token;
        this.username = username;
        setAuthenticated(true);
    }
    
    @Override
    public Object getCredentials() {
        return token;
    }
    
    @Override
    public Object getPrincipal() {
        return username;
    }
    
    @Override
    public String getName() {
        return username;
    }
}
