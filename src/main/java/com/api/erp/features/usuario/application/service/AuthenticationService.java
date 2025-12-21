package com.api.erp.features.usuario.application.service;

import com.api.erp.features.usuario.application.dto.request.LoginRequest;
import com.api.erp.features.usuario.application.dto.response.TokenResponse;
import com.api.erp.features.usuario.domain.entity.Usuario;
import com.api.erp.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.shared.domain.exception.BusinessException;
import com.api.erp.shared.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    public TokenResponse login(LoginRequest request) {
        logger.info("Tentativa de login para email: {}", request.getEmail());
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());
        
        if (usuarioOpt.isEmpty()) {
            logger.warn("Usuário não encontrado: {}", request.getEmail());
            throw new BusinessException("Email ou senha inválidos");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            logger.warn("Senha inválida para usuário: {}", request.getEmail());
            throw new BusinessException("Email ou senha inválidos");
        }
        
        String token = jwtTokenProvider.generateToken(usuario.getEmail().getValor(), usuario.getId().toString());
        
        logger.info("Login bem-sucedido para usuário: {}", request.getEmail());
        
        return new TokenResponse(token, "Bearer", 86400);
    }
}
