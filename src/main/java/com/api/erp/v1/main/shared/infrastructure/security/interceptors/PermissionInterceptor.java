package com.api.erp.v1.main.shared.infrastructure.security.interceptors;

import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.security.util.PermissionEvaluator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionEvaluator permissionEvaluator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();

            if (method.isAnnotationPresent(RequiresPermission.class)) {
                RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
                for (String permissaoCodigo : annotation.value()) {
                    if (!permissionEvaluator.hasPermission(permissaoCodigo)) {
                        throw new AccessDeniedException("Acesso negado para a permissão: " + permissaoCodigo);
                    }
                }
            }
        }
        return true;
    }
}
