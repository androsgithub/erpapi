# 🎨 Decorator Pattern - Feature Contato

## 📋 Visão Geral

A feature Contato implementa o **Decorator Pattern** para adicionar comportamentos como validação, auditoria e cache **sem modificar a classe original**.

## 🏗️ Arquitetura

```
ContatoServiceInterface (Interface)
    ↑
    └─ ContatoService (Implementação Base)
    └─ ValidationDecorator
    └─ AuditDecorator
    └─ CacheDecorator
```

## 📦 Componentes

### 1. **ContatoServiceInterface**
Interface que define o contrato do serviço. Todos os decorators implementam esta interface.

```java
public interface ContatoServiceInterface {
    ContatoResponse criar(CreateContatoRequest request);
    ContatoResponse buscarPorId(Long id);
    // ... outros métodos
}
```

### 2. **ContatoService**
Implementação base com a lógica de negócio principal.

```java
public class ContatoService implements ContatoServiceInterface {
    private final ContatoRepository repository;
    // ... implementação
}
```

### 3. **ValidationDecoratorContatoService**
Decorator que adiciona validações extras.

- Valida se o request é nulo
- Valida IDs (não pode ser nulo ou ≤ 0)
- Valida tipos de contato
- Valida tamanho de campos

### 4. **AuditDecoratorContatoService**
Decorator que registra todas as operações em logs.

- Log de criações
- Log de buscas
- Log de atualizações
- Log de exclusões
- Log de erros

### 5. **CacheDecoratorContatoService**
Decorator que utiliza Spring Cache para melhorar performance.

- Cache de leituras
- Invalidação automática em modificações
- Suporta `@Cacheable` e `@CacheEvict`

### 6. **ContatoServiceDecoratorFactory**
Factory que orquestra a composição dos decorators.

## 🚀 Como Usar

### Composição Padrão (Com Todos os Decorators)

```java
@RestController
public class ContatoController {
    private final ContatoServiceInterface service;
    
    // O serviço é injetado já com todos os decorators aplicados
    public ContatoController(ContatoServiceInterface service) {
        this.service = service;
    }
}
```

A injeção automática usa `@Primary`, que aponta para a composição completa:

```
Cache → Audit → Validation → ContatoService
```

### Composição Customizada

Para usar uma composição diferente, especifique qual bean deseja:

```java
@RestController
public class ContatoController {
    // Apenas com validação
    @Qualifier("contatoServiceComValidacao")
    private final ContatoServiceInterface service;
    
    public ContatoController(ContatoServiceInterface service) {
        this.service = service;
    }
}
```

#### Opções Disponíveis

| Bean | Descrição | Composição |
|------|-----------|-----------|
| `contatoServiceBase` | Sem decorators | ContatoService |
| `contatoServiceComValidacao` | Apenas validação | Validation → ContatoService |
| `contatoServiceComValidacaoEAuditoria` | Validação + Audit | Audit → Validation → ContatoService |
| `contatoServiceCompleto` | Todos os decorators | Cache → Audit → Validation → ContatoService |

### Fluxo de Execução

Ao chamar `criar()` com a composição completa:

```
1. CacheDecorator.criar()
   ├─ Invalida caches de contatos
   └─ Chama próxima camada

2. AuditDecorator.criar()
   ├─ Log: "Criando novo contato..."
   └─ Chama próxima camada

3. ValidationDecorator.criar()
   ├─ Valida request (não nulo)
   ├─ Valida tipo
   ├─ Valida valor
   └─ Chama próxima camada

4. ContatoService.criar()
   ├─ Lógica de negócio real
   ├─ Remove principal se necessário
   ├─ Salva no banco
   └─ Retorna ContatoResponse

5. De volta pela cadeia...
   ├─ AuditDecorator: Log de sucesso
   ├─ CacheDecorator: (Fica para próxima leitura)
   └─ Retorna ao Controller
```

## 🔧 Criar Novo Decorator

Exemplo: Decorator para Criptografia

```java
@Service
public class EncryptionDecoratorContatoService implements ContatoServiceInterface {
    
    private final ContatoServiceInterface service;
    
    public EncryptionDecoratorContatoService(ContatoServiceInterface service) {
        this.service = service;
    }
    
    @Override
    public ContatoResponse criar(CreateContatoRequest request) {
        // Criptografar valor do contato
        String valorCriptografado = encriptar(request.valor());
        
        CreateContatoRequest requestCriptografado = new CreateContatoRequest(
            request.tipo(),
            valorCriptografado,
            request.descricao(),
            request.principal()
        );
        
        return service.criar(requestCriptografado);
    }
    
    // ... outros métodos
}
```

Depois, adicionar à factory:

```java
@Bean
@Primary
public ContatoServiceInterface contatoServiceComDecorators(ContatoRepository repository) {
    ContatoServiceInterface service = new ContatoService(repository);
    service = new ValidationDecoratorContatoService(service);
    service = new EncryptionDecoratorContatoService(service);  // ← Novo
    service = new AuditDecoratorContatoService(service);
    service = new CacheDecoratorContatoService(service);
    return service;
}
```

## 📊 Ordem de Decorators - Importante!

A **ordem importa**. Considere o que deve acontecer primeiro:

```
// Ordem recomendada:
Cache → Audit → Validation → Business Logic

// Lógica:
1. Cache - Se está em cache, retorna imediatamente (mais rápido)
2. Audit - Log de quem acessou (mesmo com cache)
3. Validation - Garante que dados inválidos não cheguem ao negócio
4. Business Logic - Implementação real
```

## 🧪 Testando Decorators

```java
@ExtendWith(MockitoExtension.class)
class ContatoServiceDecoratorTest {
    
    @Test
    void deveValidarAntesDeCriar() {
        // Arrange
        ContatoServiceInterface service = new ContatoService(repository);
        ContatoServiceInterface validatedService = 
            new ValidationDecoratorContatoService(service);
        
        CreateContatoRequest request = new CreateContatoRequest(
            null, // Tipo inválido
            "11999999999",
            "Teste",
            false
        );
        
        // Act & Assert
        assertThrows(BusinessException.class, 
            () -> validatedService.criar(request));
    }
}
```

## 💾 Cache - Estratégia

Os nomes de cache definem qual estratégia usar:

```java
@Cacheable(value = "contatoPorId", key = "#id")
public ContatoResponse buscarPorId(Long id) { }
```

Para configurar cache no `application.properties`:

```properties
# Usar cache em memória (Caffeine)
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=500,expireAfterAccess=10m

# Ou usar Redis
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

## ⚡ Performance

Com decorators bem configurados:

- **Primeira requisição**: ~50ms (validação + negócio + log)
- **Requisição com cache**: ~1ms (retorna do cache)
- **Requisição com erro de validação**: ~5ms (falha rápido)

## 🎯 Boas Práticas

✅ **Faça**:
- Ordem: Cache → Audit → Validation → Business
- Separe responsabilidades em diferentes decorators
- Reutilize decorators em múltiplos serviços
- Teste decorators isoladamente

❌ **Evite**:
- Decorators com lógica de negócio
- Muito aninhamento (máximo 3-4 decorators)
- Compartilhar estado entre decorators
- Decorators que ignoram exceções

## 📚 Referências

- [Decorator Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/decorator)
- [Spring Cache Abstraction](https://spring.io/guides/gs/caching/)
- [Composition over Inheritance](https://en.wikipedia.org/wiki/Composition_over_inheritance)

---

**Última atualização**: Dezembro de 2025
