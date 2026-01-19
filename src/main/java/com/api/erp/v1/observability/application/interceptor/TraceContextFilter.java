package com.api.erp.v1.observability.application.interceptor;

import com.api.erp.v1.observability.application.context.TraceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TraceContextFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String traceId = extractOrGenerateTraceId(request);
        TraceContext.setTraceId(traceId);

        // Propagar traceId na resposta
        response.setHeader(TRACE_ID_HEADER, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Limpar contexto ao final da requisição
            TraceContext.clear();
        }
    }

    private String extractOrGenerateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        
        if (traceId == null || traceId.isBlank()) {
            // Gera um novo traceId se não existir
            traceId = TraceContext.getTraceId();
        } else {
            // Usa o traceId do header
            TraceContext.setTraceId(traceId);
        }
        
        return traceId;
    }
}
