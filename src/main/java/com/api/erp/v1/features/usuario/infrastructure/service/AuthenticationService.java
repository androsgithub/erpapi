package com.api.erp.v1.features.usuario.infrastructure.service;

import com.api.erp.v1.features.usuario.application.dto.request.LoginRequest;
import com.api.erp.v1.features.usuario.application.dto.response.TokenResponse;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.v1.features.usuario.domain.service.IPasswordEncoder;
import com.api.erp.v1.shared.domain.exception.BusinessException;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.infrastructure.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UsuarioRepository usuarioRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationService(
            UsuarioRepository usuarioRepository,
            JwtTokenProvider jwtTokenProvider,
            IPasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public TokenResponse login(LoginRequest request) {
        logger.info("Tentativa de login para email: {}", request.login());

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(new Email(request.login()));

        if (usuarioOpt.isEmpty()) {
            logger.warn("Usuário não encontrado: {}", request.login());
            throw new BusinessException("Email ou senha inválidos");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.password(), usuario.getSenhaHash())) {
            logger.warn("Senha inválida para usuário: {}", request.login());
            throw new BusinessException("Email ou senha inválidos");
        }

        String token = jwtTokenProvider.generateToken(usuario.getEmail().getValor(), usuario.getId(), usuario.getTenantId());

        logger.info("Login bem-sucedido para usuário: {}", request.login());

        return new TokenResponse(token, "Bearer", 86400);
    }
}
