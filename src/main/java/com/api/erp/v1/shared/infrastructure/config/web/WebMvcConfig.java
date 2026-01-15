package com.api.erp.v1.shared.infrastructure.config.web;

import com.api.erp.v1.shared.infrastructure.security.interceptors.PermissionInterceptor;
import com.api.erp.v1.shared.infrastructure.security.interceptors.TenantContextInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final PermissionInterceptor permissionInterceptor;
    private final TenantContextInterceptor tenantContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ✅ TenantContext PRIMEIRO (ativa filtro)
        // Deve ser antes de PermissionInterceptor para garantir contexto
        registry.addInterceptor(tenantContextInterceptor)
                .addPathPatterns("/**");
        
        // Depois PermissionInterceptor (valida permissões)
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**");
    }
}
