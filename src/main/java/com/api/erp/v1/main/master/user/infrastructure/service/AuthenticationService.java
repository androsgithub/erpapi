package com.api.erp.v1.main.master.user.infrastructure.service;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.master.tenant.application.mapper.TenantMapper;
import com.api.erp.v1.main.master.user.application.dto.request.LoginRequest;
import com.api.erp.v1.main.master.user.application.dto.response.AuthStatus;
import com.api.erp.v1.main.master.user.application.dto.response.AuthenticationResponse;
import com.api.erp.v1.main.master.user.application.dto.response.TokenResponse;
import com.api.erp.v1.main.master.user.domain.entity.User;
import com.api.erp.v1.main.master.user.domain.repository.UserRepository;
import com.api.erp.v1.main.master.user.domain.service.IPasswordEncoder;
import com.api.erp.v1.main.shared.common.error.AuthenticationErrorMessage;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import com.api.erp.v1.main.shared.infrastructure.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TenantMapper tenantMapper;

    public AuthenticationService(
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider,
            IPasswordEncoder passwordEncoder, TenantMapper tenantMapper
    ) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tenantMapper = tenantMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthenticationResponse login(LoginRequest request) {
        log.info("Tenant id: {}; Tentativa de login para email: {}", TenantContext.getTenantId(), request.login());

        User user = userRepository.findByEmail(new Email(request.login())).orElse(null);

        if (user == null) {
            log.warn("User not found: {}", request.login());
            throw new ErrorHandler(AuthenticationErrorMessage.INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", request.login());
            throw new ErrorHandler(AuthenticationErrorMessage.INVALID_CREDENTIALS);
        }

        if (TenantContext.getTenantId() == null) {
            return new AuthenticationResponse(AuthStatus.TENANT_SELECTION_REQUIRED, tenantMapper.toResponseSet(user.getTenants()), null);
        }

        String token = jwtTokenProvider.generateToken(user.getEmail().getValor(), user.getId().toString(), TenantContext.getTenantId());

        log.info("Successful login for user: {}", request.login());
        TokenResponse tokenResponse = new TokenResponse(token, "Bearer", 86400);

        return new AuthenticationResponse(AuthStatus.AUTHENTICATED, null, tokenResponse);
    }
}
