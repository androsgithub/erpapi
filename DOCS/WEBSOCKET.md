# 💬 WebSocket - Comunicação em Tempo Real

## 📋 Visão Geral

A aplicação suporta WebSocket para comunicação bidirecional em tempo real entre cliente e servidor.

## 🎯 Casos de Uso

- Notificações em tempo real
- Chat entre usuários
- Atualizações de status de operações
- Sincronização de dados
- Dashboard em tempo real

## ⚙️ Configuração

### WebSocket Config

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint(\"/ws\")
            .setAllowedOrigins(\"*\")
            .withSockJS();
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Destinação para client → server
        config.setApplicationDestinationPrefixes(\"/app\");
        
        // Destinação para server → client (message broker)
        config.enableSimpleBroker(\"/topic\", \"/queue\");
    }
}
```

## 🔌 Controller WebSocket

```java
@Controller
@Slf4j
public class NotificacaoController {
    
    // Client envia para /app/notificacoes/enviar
    // Server processa e envia para /topic/notificacoes
    
    @MessageMapping(\"/notificacoes/enviar\")
    @SendTo(\"/topic/notificacoes\")
    public NotificacaoMessage enviarNotificacao(
        NotificacaoRequest request,
        Principal principal
    ) {
        log.info(\"Notificação de {}: {}\",
            principal.getName(),
            request.getMessage()
        );
        
        return new NotificacaoMessage(
            principal.getName(),
            request.getMessage(),
            LocalDateTime.now()
        );
    }
    
    // Notificar usuário específico
    @MessageMapping(\"/notificacoes/privado/{userId}\")
    public void enviarPrivado(
        @DestinationVariable Long userId,
        NotificacaoRequest request,
        SimpMessagingTemplate messagingTemplate
    ) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            \"/queue/notificacoes\",
            new NotificacaoMessage(
                \"sistema\",
                request.getMessage(),
                LocalDateTime.now()
            )
        );
    }
}
```

## 📨 DTOs

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoRequest {
    private String message;
    private String tipo; // INFO, WARNING, ERROR, SUCCESS
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoMessage {
    private String remetente;
    private String mensagem;
    private LocalDateTime timestamp;
    private String tipo;
}
```

## 🌐 Cliente JavaScript

### HTML

```html
<!DOCTYPE html>
<html>
<head>
    <title>WebSocket Demo</title>
    <script src=\"https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js\"></script>
    <script src=\"https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js\"></script>
</head>
<body>
    <h1>Notificações em Tempo Real</h1>
    <div id=\"messages\"></div>
    <input type=\"text\" id=\"messageInput\" placeholder=\"Digite mensagem\">
    <button onclick=\"enviarMensagem()\">Enviar</button>
</body>
</html>
```

### JavaScript

```javascript
let stompClient = null;

function conectar() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function(frame) {
        console.log('Conectado: ' + frame.headers['server']);
        
        // Subscrever a mensagens gerais
        stompClient.subscribe('/topic/notificacoes', function(message) {
            const notificacao = JSON.parse(message.body);
            exibirNotificacao(notificacao);
        });
        
        // Subscrever a mensagens privadas
        stompClient.subscribe('/user/queue/notificacoes', function(message) {
            const notificacao = JSON.parse(message.body);
            exibirNotificacaoPrivada(notificacao);
        });
    });
}

function enviarMensagem() {
    const mensagem = document.getElementById('messageInput').value;
    
    stompClient.send('/app/notificacoes/enviar', {}, JSON.stringify({
        message: mensagem,
        tipo: 'INFO'
    }));
    
    document.getElementById('messageInput').value = '';
}

function exibirNotificacao(notificacao) {
    const div = document.getElementById('messages');
    const p = document.createElement('p');
    p.textContent = notificacao.remetente + ': ' + notificacao.mensagem;
    div.appendChild(p);
}

function exibirNotificacaoPrivada(notificacao) {
    alert('Mensagem privada: ' + notificacao.mensagem);
}

// Conectar quando página carrega
window.addEventListener('load', conectar);
```

## 🔐 Segurança WebSocket

```java
@Configuration
public class WebSocketSecurityConfig {
    
    @Bean
    public ChannelInterceptor customChannelInterceptor() {
        return new ChannelInterceptor() {
            
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader(\"Authorization\");
                    
                    if (token == null || !validarToken(token)) {
                        throw new AccessDeniedException(\"Token WebSocket inválido\");
                    }
                }
                
                return message;
            }
            
            private boolean validarToken(String token) {
                // Implementar validação de JWT
                return true;
            }
        };
    }
}
```

## 📊 Notificações de Progresso

```java
@Component
@Slf4j
public class ProcessamentoService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void processarComNotificacao(Long usuarioId, Runnable tarefa) {
        try {
            notificar(usuarioId, \"Iniciando processamento...\", \"INFO\");
            
            tarefa.run();
            
            notificar(usuarioId, \"Processamento concluído com sucesso!\", \"SUCCESS\");
        } catch (Exception ex) {
            log.error(\"Erro no processamento\", ex);
            notificar(usuarioId, \"Erro: \" + ex.getMessage(), \"ERROR\");
        }
    }
    
    private void notificar(Long usuarioId, String mensagem, String tipo) {
        messagingTemplate.convertAndSendToUser(
            usuarioId.toString(),
            \"/queue/notificacoes\",
            NotificacaoMessage.builder()
                .remetente(\"sistema\")
                .mensagem(mensagem)
                .tipo(tipo)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
}
```

## 🧪 Testes WebSocket

```java
@SpringBootTest
@AutoConfigureMockMvc
public class WebSocketTest {
    
    @Autowired
    private WebSocketStompClient stompClient;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void deveEnviarNotificacaoWebSocket() throws Exception {
        // Conectar
        String url = \"ws://localhost:\" + port + \"/ws\";
        StompSession session = stompClient.connect(url,
            new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
        
        // Subscrever
        CompletableFuture<String> future = new CompletableFuture<>();
        session.subscribe(\"/topic/notificacoes\",
            new DefaultStompFrameHandler() {
                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    future.complete((String) payload);
                }
            });
        
        // Enviar
        session.send(\"/app/notificacoes/enviar\",
            \"{\\\"message\\\":\\\"teste\\\"}\");
        
        // Verificar
        String resultado = future.get(1, TimeUnit.SECONDS);
        assertNotNull(resultado);
    }
}
```

## 📚 Referências Relacionadas

- [Spring WebSocket](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [STOMP Protocol](https://stomp.github.io/)
- [SockJS](https://sockjs.org/)

---

**Última atualização:** Dezembro de 2025
