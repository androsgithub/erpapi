package com.api.erp.v1.main.shared.infrastructure.messaging;

import com.api.erp.v1.main.shared.domain.entity.UsuarioAutenticado;
import com.api.erp.v1.main.shared.infrastructure.security.jwt.BearerTokenAuthentication;
import com.api.erp.v1.main.shared.infrastructure.security.jwt.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketAuthChannelInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);

            logger.info("STOMP CONNECT - Authorization header: {}", authHeader);

            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.substring(BEARER_PREFIX.length()).trim();

                if (jwtTokenProvider.isTokenValid(token)) {
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    String usuarioId = jwtTokenProvider.getUsuarioIdFromToken(token);
                    String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
                    UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado(usuarioId, tenantId);

                    logger.info("Usuário autenticado via WebSocket: {} ({})", email, usuarioId);

                    BearerTokenAuthentication authentication =
                            new BearerTokenAuthentication(token, email, usuarioAutenticado);
                    authentication.setDetails(usuarioId);

                    // Define a autenticação no contexto do STOMP
                    accessor.setUser(authentication);
                } else {
                    logger.warn("Token JWT inválido no WebSocket");
                    throw new IllegalArgumentException("Token inválido");
                }
            } else {
                logger.warn("Header Authorization ausente ou inválido no CONNECT");
                throw new IllegalArgumentException("Autenticação obrigatória");
            }
        }

        return message;
    }
}