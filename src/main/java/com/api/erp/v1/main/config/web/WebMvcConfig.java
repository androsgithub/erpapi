package com.api.erp.v1.main.config.web;

import com.api.erp.v1.main.shared.infrastructure.security.interceptors.PermissionInterceptor;
import com.api.erp.v1.main.shared.infrastructure.security.interceptors.TenantValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TenantValidationInterceptor tenantValidationInterceptor;
    private final PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ✅ Validation de Tenant PRIMEIRO (valida e seta contexto)
        // Deve ser antes de PermissionInterceptor para garantir contexto
        registry.addInterceptor(tenantValidationInterceptor)
                .addPathPatterns("/**");
        
        // Depois PermissionInterceptor (valida permissões)
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**");
    }
}
