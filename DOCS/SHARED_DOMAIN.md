# 📚 Componentes Compartilhados (Shared Domain)

## 📋 Visão Geral

A camada Shared contém componentes reutilizáveis em todo o projeto, independentes de features específicas.

## 🏗️ Estrutura

```
shared/
├── domain/
│   ├── exception/          # Exceções de domínio
│   ├── valueobject/        # Value Objects compartilhados
│   └── event/             # Eventos de domínio
│
├── infrastructure/
│   ├── config/            # Configurações gerais
│   ├── persistence/       # Listeners, auditoria
│   └── adapter/          # Adapters globais
│
└── websocket/
    ├── config/            # Configuração WebSocket
    ├── controller/        # Controladores WebSocket
    └── handler/          # Handlers de mensagem
```

## 🔑 Exceções Compartilhadas

### BusinessException

Para erros de lógica de negócio.

```java
public class BusinessException extends RuntimeException {
    public BusinessException(String mensagem) {
        super(mensagem);
    }
}

// Uso
if (codigoJaExiste) {
    throw new BusinessException(\"Código de produto já existe\");
}
```

### NotFoundException

Para recursos não encontrados.

```java
public class NotFoundException extends RuntimeException {
    public NotFoundException(String mensagem) {
        super(mensagem);
    }
}

// Uso
return repository.findById(id)
    .orElseThrow(() -> new NotFoundException(
        String.format(\"Produto com ID %d não encontrado\", id)
    ));
```

### ValidationException

Para erros de validação.

```java
public class ValidationException extends RuntimeException {
    private final Map<String, String> errors;
    
    public ValidationException(Map<String, String> errors) {
        super(\"Erro de validação\");
        this.errors = errors;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
}

// Uso
Map<String, String> erros = new HashMap<>();
if (request.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
    erros.put(\"preco\", \"Preço deve ser positivo\");
}
if (!erros.isEmpty()) {
    throw new ValidationException(erros);
}
```

## 💾 Value Objects Compartilhados

### Email

Encapsula validação e imutabilidade de email.

```java
@Embeddable
public class Email {
    
    @Column(name = \"email\")
    private String valor;
    
    public Email(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException(\"Email não pode ser vazio\");
        }
        
        if (!isValido(valor)) {
            throw new BusinessException(\"Email inválido: \" + valor);
        }
        
        this.valor = valor.toLowerCase();
    }
    
    public Email() {} // Para JPA
    
    public String getValor() { return valor; }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Email)) return false;
        return valor.equals(((Email) o).valor);
    }
    
    @Override
    public int hashCode() {
        return valor.hashCode();
    }
    
    private static boolean isValido(String email) {
        return Pattern.matches(
            \"^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}$\",
            email
        );
    }
}

// Uso em Entity
@Entity
public class Usuario {
    
    @Embedded
    private Email email;
    
    public Usuario(String email) {
        this.email = new Email(email);
    }
}
```

### CPF

Encapsula validação de CPF.

```java
@Embeddable
public class CPF {
    
    @Column(name = \"cpf\", length = 11)
    private String valor;
    
    public CPF(String valor) {
        String limpo = valor.replaceAll(\"[^0-9]\", \"\");
        
        if (limpo.length() != 11) {
            throw new BusinessException(\"CPF deve ter 11 dígitos\");
        }
        
        if (!isValido(limpo)) {
            throw new BusinessException(\"CPF inválido\");
        }
        
        this.valor = limpo;
    }
    
    public CPF() {} // Para JPA
    
    public String getValor() { return valor; }
    
    public String getFormatado() {
        return valor.substring(0, 3) + \".\" +
               valor.substring(3, 6) + \".\" +
               valor.substring(6, 9) + \"-\" +
               valor.substring(9);
    }
    
    private static boolean isValido(String cpf) {
        // Validação de dígito verificador
        int soma = 0;
        int resto;
        
        if (cpf.equals(cpf.charAt(0) + \"\".repeat(11))) return false;
        
        // Primeiro dígito
        for (int i = 0; i < 9; i++) {
            soma += (10 - i) * Character.getNumericValue(cpf.charAt(i));
        }
        resto = 11 - (soma % 11);
        if (resto == 10 || resto == 11) resto = 0;
        if (resto != Character.getNumericValue(cpf.charAt(9))) return false;
        
        // Segundo dígito
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (11 - i) * Character.getNumericValue(cpf.charAt(i));
        }
        resto = 11 - (soma % 11);
        if (resto == 10 || resto == 11) resto = 0;
        if (resto != Character.getNumericValue(cpf.charAt(10))) return false;
        
        return true;
    }
}
```

### Dinheiro/Money (Opcional)

Para operações monetárias.

```java
@Embeddable
public class Money {
    
    @Column(precision = 15, scale = 2)
    private BigDecimal valor;
    
    @Column(length = 3)
    private String moeda; // \"BRL\", \"USD\"
    
    public Money(BigDecimal valor, String moeda) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(\"Valor não pode ser negativo\");
        }
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
        this.moeda = moeda;
    }
    
    public Money() {}
    
    public Money add(Money outro) {
        if (!moeda.equals(outro.moeda)) {
            throw new BusinessException(\"Moedas diferentes\");
        }
        return new Money(valor.add(outro.valor), moeda);
    }
    
    public Money multiply(BigDecimal fator) {
        return new Money(valor.multiply(fator), moeda);
    }
}
```

## 🎯 Eventos de Domínio

Para comunicação entre agregados sem acoplamento.

```java
public abstract class DomainEvent {
    private final LocalDateTime ocorreuEm;
    
    public DomainEvent() {
        this.ocorreuEm = LocalDateTime.now();
    }
    
    public LocalDateTime getOcorreuEm() {
        return ocorreuEm;
    }
}

public class ProdutoCriadoEvent extends DomainEvent {
    private final Long produtoId;
    private final String codigo;
    
    public ProdutoCriadoEvent(Long produtoId, String codigo) {
        this.produtoId = produtoId;
        this.codigo = codigo;
    }
    
    public Long getProdutoId() { return produtoId; }
    public String getCodigo() { return codigo; }
}

// Listener
@Component
@Transactional
public class ProdutoCriadoEventListener {
    
    private final ApplicationEventPublisher eventPublisher;
    
    @EventListener
    public void handle(ProdutoCriadoEvent evento) {
        // Fazer algo quando produto é criado
        log.info(\"Produto criado: {}\", evento.getCodigo());
    }
}
```

## ⚙️ Configurações Globais

### JPA Configuration

```java
@Configuration
public class JpaConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName()
        );
    }
}
```

### Jackson Configuration

```java
@Configuration
public class JacksonConfig {
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
```

## 🌐 WebSocket

### Configuração

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(\"/ws\").setAllowedOrigins(\"*\");
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(\"/topic\");
        config.setApplicationDestinationPrefixes(\"/app\");
    }
}

@Controller
public class WebSocketController {
    
    @MessageMapping(\"/sendMessage\")
    @SendTo(\"/topic/messages\")
    public MessageResponse sendMessage(MessageRequest request) {
        return new MessageResponse(request.getMessage());
    }
}
```

## 🧪 Testes Compartilhados

```java
public class EmailTest {
    
    @Test
    void deveCriarEmailValido() {
        Email email = new Email(\"usuario@empresa.com\");
        assertEquals(\"usuario@empresa.com\", email.getValor());
    }
    
    @Test
    void naoDeveCriarEmailInvalido() {
        assertThrows(BusinessException.class, () -> {
            new Email(\"email-invalido\");
        });
    }
}

public class CPFTest {
    
    @Test
    void deveCriarCPFValido() {
        CPF cpf = new CPF(\"123.456.789-09\");
        assertEquals(\"12345678909\", cpf.getValor());
    }
    
    @Test
    void deveFormatarCPF() {
        CPF cpf = new CPF(\"12345678909\");
        assertEquals(\"123.456.789-09\", cpf.getFormatado());
    }
}
```

## 🚀 Boas Práticas

1. **Value Objects imutáveis**: Use `final` fields
2. **Encapsular validação**: No construtor
3. **Usar @Embeddable**: Para Value Objects em JPA
4. **Igualdade por valor**: Implemente equals/hashCode
5. **Sem getters mutáveis**: Retorne cópias se necessário
6. **Eventos desacoplam**: Use ApplicationEventPublisher
7. **Exceções específicas**: Herdar de RuntimeException

## 📚 Referências Relacionadas

- [CAMADA_DOMAIN.md](CAMADA_DOMAIN.md) - Padrões DDD
- [FEATURE_USUARIO.md](FEATURE_USUARIO.md) - Exemplo com Email/CPF
- [SEGURANCA.md](SEGURANCA.md) - Tratamento de erros

---

**Última atualização:** Dezembro de 2025
