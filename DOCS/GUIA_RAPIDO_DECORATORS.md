# 🎯 Guia Rápido - Decorators Configuráveis da Feature Contato

## 📋 O que mudou?

### Antes ❌
```java
// Hardcoded - precisa recompilar para mudar
service = new ContatoService(repository);
service = new ValidationDecorator(service);
service = new AuditDecorator(service);
service = new CacheDecorator(service);
```

### Depois ✅
```properties
# Arquivo de configuração - muda sem recompilar
contato.decorators.validation.enabled=true
contato.decorators.audit.enabled=true
contato.decorators.cache.enabled=false
```

---

## 🚀 Começar em 3 passos

### 1️⃣ Copiar Configurações

Copie e cole de `application-decorators-example.properties`:

```properties
# application.properties

# Validation - Valida dados de entrada
contato.decorators.validation.enabled=true
contato.decorators.validation.validate-id=true
contato.decorators.validation.validate-request=true

# Audit - Registra operações
contato.decorators.audit.enabled=true
contato.decorators.audit.log-sensitive-data=false
contato.decorators.audit.log-level=INFO

# Cache - Cacheia resultados (opcional)
contato.decorators.cache.enabled=false
contato.decorators.cache.ttl-minutes=10
contato.decorators.cache.max-size=500

# Outros (opcional)
contato.decorators.format-validation.enabled=false
contato.decorators.notification.enabled=false
```

### 2️⃣ Usar o Serviço

```java
@RestController
@RequestMapping("/api/v1/contatos")
public class ContatoController {
    
    private final ContatoServiceInterface service;
    
    // ✓ Automaticamente injetar com decorators
    public ContatoController(ContatoServiceInterface service) {
        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity<ContatoResponse> criar(
            @RequestBody CreateContatoRequest request) {
        // Automático: Validation → Audit → ContatoService
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.criar(request));
    }
}
```

### 3️⃣ Pronto!

```bash
# Development (validação + audit)
java -jar app.jar \
  --contato.decorators.validation.enabled=true \
  --contato.decorators.audit.enabled=true

# Production (com cache)
java -jar app.jar \
  --contato.decorators.cache.enabled=true \
  --contato.decorators.cache.ttl-minutes=30
```

---

## 📚 Decorators Disponíveis

### 1. **VALIDATION** ✅ Padrão
```properties
contato.decorators.validation.enabled=true
```
- Valida request não nulo
- Valida ID > 0
- Valida tipo de contato
- Valida tamanho de campos

### 2. **AUDIT** ✅ Padrão
```properties
contato.decorators.audit.enabled=true
contato.decorators.audit.log-level=INFO
```
Logs:
```
[AUDIT] Criando novo contato: tipo=TELEFONE
[AUDIT] Contato criado com sucesso: id=1
[AUDIT] Erro ao criar contato: erro=...
```

### 3. **CACHE** (Opcional)
```properties
contato.decorators.cache.enabled=true
contato.decorators.cache.ttl-minutes=10
contato.decorators.cache.max-size=500
```
- Cache de leitura (buscarPorId, buscarTodos, etc.)
- Invalidação automática em modificações
- Usa Spring Cache (Caffeine por padrão)

### 4. **FORMAT-VALIDATION** (Opcional)
```properties
contato.decorators.format-validation.enabled=true
contato.decorators.format-validation.validate-email=true
contato.decorators.format-validation.validate-phone=true
```
Valida:
- Email: `nome@dominio.com`
- Telefone: `(11) 99999-9999`
- URL: `https://website.com`

### 5. **NOTIFICATION** (Exemplo Customizado)
```properties
contato.decorators.notification.enabled=true
contato.decorators.notification.recipient-email=admin@empresa.com
```
Envia email quando:
- Contato criado
- Contato atualizado
- Contato deletado

---

## 🎯 Cenários de Uso

### 🔨 Desenvolvimento Local
```properties
# Tudo ligado para debugar
contato.decorators.validation.enabled=true
contato.decorators.audit.enabled=true
contato.decorators.cache.enabled=false
contato.decorators.format-validation.enabled=true
```

### 🚀 Produção
```properties
# Performance + segurança
contato.decorators.validation.enabled=true
contato.decorators.audit.enabled=true
contato.decorators.cache.enabled=true
contato.decorators.cache.ttl-minutes=30
contato.decorators.format-validation.enabled=true
contato.decorators.notification.enabled=true
```

### ⚡ Performance (Cache Intensivo)
```properties
contato.decorators.validation.enabled=true
contato.decorators.audit.enabled=false
contato.decorators.cache.enabled=true
contato.decorators.cache.ttl-minutes=60
contato.decorators.cache.max-size=2000
```

### 🔒 Segurança Máxima
```properties
contato.decorators.validation.enabled=true
contato.decorators.audit.enabled=true
contato.decorators.cache.enabled=false
contato.decorators.format-validation.enabled=true
contato.decorators.notification.enabled=true
```

---

## 📊 Fluxo de Execução

Com configuração padrão (Validation + Audit):

```
POST /api/v1/contatos
    ↓
Audit Decorator [log entrada]
    ↓
Validation Decorator [validar dados]
    ├─ Request nulo? → ❌ Error 400
    ├─ Tipo inválido? → ❌ Error 400
    └─ OK → ✅ Continuar
    ↓
ContatoService [implementação real]
    ├─ Remove principal anterior
    ├─ Salva no banco
    └─ Retorna ContatoResponse
    ↓
Audit Decorator [log sucesso/erro]
    ↓
Resposta HTTP 201 Created
```

---

## 🔧 Criar Seu Próprio Decorator

### 1. Criar Classe

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
        // ANTES
        log.info("Antes de criar contato");
        
        // DURANTE
        ContatoResponse response = service.criar(request);
        
        // DEPOIS
        log.info("Depois de criar contato: {}", response.id());
        
        return response;
    }
    
    // Implementar outros métodos...
}
```

### 2. Adicionar Propriedades

Em `ContatoDecoratorProperties.java`:

```java
private MeuDecorator meuDecorator = new MeuDecorator();

@Getter
@Setter
public static class MeuDecorator {
    private boolean enabled = false;
    private String parametro = "valor";
}
```

### 3. Adicionar em `application.properties`

```properties
contato.decorators.meu-decorator.enabled=false
contato.decorators.meu-decorator.parametro=valor
```

### 4. Adicionar à Factory

Em `ContatoServiceDecoratorFactory.java`:

```java
if (properties.getMeuDecorator().isEnabled()) {
    service = new MeuDecoratorContatoService(service, properties);
    log.info("✓ Decorator MEU-DECORATOR ativado");
}
```

---

## 🧪 Testar Diferentes Configurações

```java
@SpringBootTest
class ContatoComDecoratorTest {
    
    @Test
    @DisplayName("Deve falhar validação com dados inválidos")
    void deveValidar(@Autowired ContatoServiceInterface service) {
        // Service vem com Validation habilitado
        assertThrows(BusinessException.class, () -> {
            service.criar(new CreateContatoRequest(
                "TIPO_INVALIDO",  // ❌ Inválido
                "valor",
                "desc",
                false
            ));
        });
    }
    
    @Test
    @DisplayName("Deve resgistrar operação")
    void deveAuditar(@Autowired ContatoServiceInterface service) {
        // Service vem com Audit habilitado
        // Verificar logs conter [AUDIT]
    }
}
```

---

## 📖 Ver Documentação Completa

- `DECORATORS_CONFIGURAVEL.md` - Documentação detalhada
- `application-decorators-example.properties` - Exemplo com todos os parâmetros
- `ContatoDecoratorProperties.java` - Todas as propriedades disponíveis

---

## ✅ Checklist

- [ ] Copiei `application-decorators-example.properties` para meu `application.properties`
- [ ] Habilitei Validation e Audit
- [ ] Testei criar um contato
- [ ] Verifiquei logs [AUDIT]
- [ ] Habilitei Cache para produção (opcional)
- [ ] Habilitei Format Validation (opcional)
- [ ] Criei meu próprio decorator (opcional)

---

**Dúvidas?** Veja `DECORATORS_CONFIGURAVEL.md` ou `application-decorators-example.properties`
