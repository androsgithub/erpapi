package com.api.erp.v1.main.features.user.infrastructure.service;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.features.user.application.dto.request.LoginRequest;
import com.api.erp.v1.main.features.user.application.dto.response.TokenResponse;
import com.api.erp.v1.main.features.user.domain.entity.User;
import com.api.erp.v1.main.features.user.domain.repository.UserRepository;
import com.api.erp.v1.main.features.user.domain.service.IPasswordEncoder;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import com.api.erp.v1.main.shared.infrastructure.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationService(
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider,
            IPasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public TokenResponse login(LoginRequest request) {
        log.info("Tenant id: {}; Tentativa de login para email: {}", TenantContext.getTenantId(), request.login());

        Optional<User> userOpt = userRepository.findByEmail(new Email(request.login()));

        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", request.login());
            throw new BusinessException("Invalid email or password");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.password(), user.getSenha_hash())) {
            log.warn("Invalid password for user: {}", request.login());
            throw new BusinessException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail().getValor(), user.getId().toString(), TenantContext.getTenantId());

        log.info("Successful login for user: {}", request.login());

        return new TokenResponse(token, "Bearer", 86400);
    }
}
