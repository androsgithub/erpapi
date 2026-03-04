package com.api.erp.v1.main.shared.infrastructure.security.jwt;

import com.api.erp.v1.main.shared.domain.entity.UserAutenticado;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class BearerTokenAuthentication extends AbstractAuthenticationToken {

    private final String token;
    private final String username;

    private final UserAutenticado userAutenticado;

    public BearerTokenAuthentication(String token, String username, UserAutenticado userAutenticado) {
        super(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        this.token = token;
        this.username = username;
        this.userAutenticado = userAutenticado;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public UserAutenticado getPrincipal() {
        return userAutenticado;
    }

    @Override
    public String getName() {
        return username;
    }
}
