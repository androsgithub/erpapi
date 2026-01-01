# 🎨 Arquitetura com Decorators Configuráveis - Feature Contato

## 📌 Mudança Principal

A arquitetura de decorators foi **refatorada para ser configurável via propriedades do sistema**, não via código hardcoded.

## 🔧 Como Funciona

### 1. Propriedades de Configuração

Arquivo: `application.properties` ou `application-decorators.properties`

```properties
# Habilitar/Desabilitar Decorators
contato.decorators.validation.enabled=true
contato.decorators.audit.enabled=true
contato.decorators.cache.enabled=false
contato.decorators.format-validation.enabled=false
contato.decorators.notification.enabled=false
```

### 2. Classe de Propriedades

`ContatoDecoratorProperties.java` mapeia as configurações:

```java
@ConfigurationProperties(prefix = "contato.decorators")
public class ContatoDecoratorProperties {
    private Validation validation;
    private Audit audit;
    private Cache cache;
    private FormatValidation formatValidation;
    private Notification notification;
    // ...
}
```

### 3. Factory Condicional

`ContatoServiceDecoratorFactory.java` lê as propriedades e adiciona apenas os decorators habilitados:

```java
@Bean
@Primary
public ContatoServiceInterface contatoServiceComDecorators(
        ContatoRepository repository,
        ContatoDecoratorProperties properties) {
    
    ContatoServiceInterface service = new ContatoService(repository);
    
    if (properties.getValidation().isEnabled()) {
        service = new ValidationDecoratorContatoService(service);
    }
    if (properties.getAudit().isEnabled()) {
        service = new AuditDecoratorContatoService(service);
    }
    // ... etc
    
    return service;
}
```

## 📦 Decorators Disponíveis

| Decorator | Classe | Configuração | Status |
|-----------|--------|--------------|--------|
| **Validation** | `ValidationDecoratorContatoService` | `contato.decorators.validation.enabled` | ✅ Pronto |
| **Audit** | `AuditDecoratorContatoService` | `contato.decorators.audit.enabled` | ✅ Pronto |
| **Cache** | `CacheDecoratorContatoService` | `contato.decorators.cache.enabled` | ✅ Pronto |
| **Format Validation** | `FormatValidationDecoratorContatoService` | `contato.decorators.format-validation.enabled` | ✅ Pronto |
| **Notification** | `NotificationDecoratorContatoService` | `contato.decorators.notification.enabled` | ✅ Exemplo |

## 🚀 Como Usar

### Passo 1: Copiar Propriedades

De `application-decorators-example.properties` para seu `application.properties`:

```properties
# Desenvolvimento
contato.decorators.validation.enabled=true
contato.decorators.audit.enabled=true
contato.decorators.cache.enabled=false
contato.decorators.format-validation.enabled=true
```

### Passo 2: Usar o Serviço

```java
@RestController
public class ContatoController {
    // Injeta automaticamente com decorators configurados
    private final ContatoServiceInterface service;
    
    public ContatoController(ContatoServiceInterface service) {
        this.service = service;
    }
}
```

### Passo 3 (Opcional): Mudar Configuração em Runtime

```bash
# Development com tudo
java -jar app.jar \
  --contato.decorators.validation.enabled=true \
  --contato.decorators.audit.enabled=true \
  --contato.decorators.cache.enabled=false

# Production com cache
java -jar app.jar \
  --contato.decorators.validation.enabled=true \
  --contato.decorators.audit.enabled=true \
  --contato.decorators.cache.enabled=true \
  --contato.decorators.cache.ttl-minutes=30
```

## 🔧 Criar Novo Decorator

### Template

```java
@Slf4j
public class MeuDecoratorContatoService implements ContatoServiceInterface {
    
    private final ContatoServiceInterface service;
    private final ContatoDecoratorProperties properties;
    
    public MeuDecoratorContatoService(
            ContatoServiceInterface service,
            ContatoDecoratorProperties properties) {
        this.service = service;
        this.properties = properties;
    }
    
    @Override
    public ContatoResponse criar(CreateContatoRequest request) {
        // Sua lógica aqui
        return service.criar(request);
    }
    
    // ... implementar outros métodos
}
```

### Adicionar Propriedade

Em `ContatoDecoratorProperties.java`:

```java
@Getter
@Setter
public static class MeuDecorator {
    private boolean enabled = false;
    private String parametro1 = "valor";
}

// Classe principal
private MeuDecorator meuDecorator = new MeuDecorator();
```

### Adicionar em `application.properties`

```properties
contato.decorators.meu-decorator.enabled=false
contato.decorators.meu-decorator.parametro1=valor
```

### Adicionar à Factory

Em `ContatoServiceDecoratorFactory.java`:

```java
if (properties.getMeuDecorator().isEnabled()) {
    service = new MeuDecoratorContatoService(service, properties);
    log.info("✓ Decorator MEU-DECORATOR ativado");
}
```

## 📊 Exemplo: Decorator de Notificação

`NotificationDecoratorContatoService.java` é um exemplo completo:

```java
@Override
public ContatoResponse criar(CreateContatoRequest request) {
    ContatoResponse response = service.criar(request);
    
    if (shouldNotifyOn("CREATE")) {
        enviarNotificacao(
            "Novo contato criado",
            String.format("Tipo: %s", response.tipo())
        );
    }
    
    return response;
}

private void enviarNotificacao(String assunto, String mensagem) {
    String destinatario = properties.getNotification().getRecipientEmail();
    log.info("[NOTIFICATION] Enviando para {}: {}", destinatario, assunto);
    // emailService.enviar(destinatario, assunto, mensagem);
}
```

Configuração:

```properties
contato.decorators.notification.enabled=true
contato.decorators.notification.recipient-email=admin@empresa.com
contato.decorators.notification.email-notification-on=CREATE,UPDATE,DELETE
```

## 🏢 Por Ambiente

### Desenvolvimento

```properties
# application-dev.properties
contato.decorators.validation.enabled=true
contato.decorators.audit.enabled=true
contato.decorators.cache.enabled=false
contato.decorators.format-validation.enabled=true
contato.decorators.notification.enabled=false
```

Executar:
```bash
java -jar app.jar --spring.profiles.active=dev
```

### Produção

```properties
# application-prod.properties
contato.decorators.validation.enabled=true
contato.decorators.audit.enabled=true
contato.decorators.cache.enabled=true
contato.decorators.cache.ttl-minutes=30
contato.decorators.cache.max-size=1000
contato.decorators.format-validation.enabled=true
contato.decorators.notification.enabled=true
contato.decorators.notification.recipient-email=ops@empresa.com
```

Executar:
```bash
java -jar app.jar --spring.profiles.active=prod
```

## 📈 Ordem de Composição

A factory aplica decorators nesta ordem (de dentro para fora):

1. **Validation** - Falha rápido em dados inválidos
2. **Format Validation** - Valida formato específico
3. **Notification** - Envia notificações de mudanças
4. **Audit** - Registra em logs
5. **Cache** - Cacheia resultados (no topo)

```
Requisição
    ↓
Cache [se habilitado]
    ├─ Hit → Retorna
    └─ Miss ↓
Audit [se habilitado] - log de entrada
    ↓
Notification [se habilitado] - prepara notificação
    ↓
FormatValidation [se habilitado] - valida formato
    ↓
Validation [se habilitado] - valida dados
    ↓
ContatoService - implementação real
    ↓
[volta pela cadeia]
Notification - envia notificação
    ↓
Audit - log de sucesso/erro
    ↓
Cache - invalida se modificação
    ↓
Resposta
```

## 🧪 Testes

Ativar apenas alguns decorators:

```java
@Test
void testeComApenasValidacao() {
    // Criar propriedades customizadas
    ContatoDecoratorProperties props = new ContatoDecoratorProperties();
    props.getValidation().setEnabled(true);
    props.getAudit().setEnabled(false);
    props.getCache().setEnabled(false);
    
    // Usar factory com propriedades custom
    ContatoRepository repo = mock(ContatoRepository.class);
    ContatoServiceInterface service = new ContatoService(repo);
    if (props.getValidation().isEnabled()) {
        service = new ValidationDecoratorContatoService(service);
    }
    
    // Testar
    assertThrows(BusinessException.class, () -> {
        service.criar(new CreateContatoRequest(null, "11999999999", "", false));
    });
}
```

## 🎯 Vantagens

✅ **Flexível** - Ativar/desativar decorators sem recompilação
✅ **Configurável** - Diferentes configs por ambiente
✅ **Rastreável** - Log de quais decorators estão ativos
✅ **Testável** - Fácil testar com diferentes combinações
✅ **Extensível** - Adicionar novos decorators sem modificar código existente
✅ **Performance** - Só adiciona decorators necessários

## 📚 Arquivos Relacionados

- `ContatoDecoratorProperties.java` - Propriedades de configuração
- `ContatoServiceDecoratorFactory.java` - Factory que compõe decorators
- `application-decorators-example.properties` - Exemplo de configuração
- Decorators:
  - `ValidationDecoratorContatoService.java`
  - `AuditDecoratorContatoService.java`
  - `CacheDecoratorContatoService.java`
  - `FormatValidationDecoratorContatoService.java`
  - `NotificationDecoratorContatoService.java`

---

**Data**: Dezembro de 2025
**Status**: Pronto para produção
**Padrão**: Decorator Pattern + Configuration Properties
