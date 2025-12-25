package com.api.erp.v1.shared.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionEvaluator permissionEvaluator;
    private static final Logger logger = LoggerFactory.getLogger(BearerTokenFilter.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            logger.info(method.getName());

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
