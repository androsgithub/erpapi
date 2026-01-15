package com.api.erp.v1.shared.infrastructure.config.security;

import com.api.erp.v1.shared.infrastructure.security.filter.BearerTokenFilter;
import com.api.erp.v1.shared.infrastructure.security.filter.TenantFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final BearerTokenFilter bearerTokenFilter;
    private final TenantFilter tenantContextFilter;

    public SecurityConfig(BearerTokenFilter bearerTokenFilter, TenantFilter tenantContextFilter) {
        this.bearerTokenFilter = bearerTokenFilter;
        this.tenantContextFilter = tenantContextFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas do Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs/**",
                                                  "/swagger-ui/**",
                                                  "/swagger-ui.html",
                                                  "/swagger-resources/**",
                                                  "/webjars/**").permitAll()
                        // Rotas públicas da autenticação
                        .requestMatchers("/api/v1/usuarios/login").permitAll()
                        .requestMatchers("/api/v1/usuarios/health").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // Rotas que requerem autenticação
//                        .requestMatchers("/api/v1/usuarios/**").authenticated()
                        .requestMatchers("/api/v1/usuarios/**").permitAll()
                        .requestMatchers("/api/v1/empresa/**").authenticated()
                        .requestMatchers("/api/v1/unidades-medida/**").authenticated()
                        .requestMatchers("/api/v1/composicoes/**").authenticated()
                        .requestMatchers("/api/v1/lista-expandida/**").authenticated()
                        .requestMatchers("/api/v1/produtos/**").authenticated()
                        // Websocket
                        .requestMatchers("/ws/**").permitAll() // Permite WebSocket
                        .anyRequest().permitAll())
                .addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // ← CRUCIAL para WebSocket
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
