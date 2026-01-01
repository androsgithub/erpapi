package com.api.erp.v1.shared.infrastructure.security;

import com.api.erp.v1.shared.domain.entity.UsuarioAutenticado;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class BearerTokenAuthentication extends AbstractAuthenticationToken {

    private final String token;
    private final String username;

    private final UsuarioAutenticado usuarioAutenticado;

    public BearerTokenAuthentication(String token, String username, UsuarioAutenticado usuarioAutenticado) {
        super(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        this.token = token;
        this.username = username;
        this.usuarioAutenticado = usuarioAutenticado;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public UsuarioAutenticado getPrincipal() {
        return usuarioAutenticado;
    }

    @Override
    public String getName() {
        return username;
    }
}
