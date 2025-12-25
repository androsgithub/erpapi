# 🧩 Padrões de Projeto Utilizados

## 📋 Visão Geral

Este documento descreve os padrões de design implementados no projeto ERP API.

## 🏗️ Padrões Arquiteturais

### 1. Domain-Driven Design (DDD)

**Objetivo**: Modelar o domínio de negócio de forma clara e expressa.

**Componentes**:
- **Entities**: Objetos com identidade
- **Value Objects**: Objetos sem identidade, imutáveis
- **Aggregates**: Cluster de entidades
- **Repositories**: Contrato de acesso a dados
- **Services**: Lógica que não pertence a uma entidade

**Exemplo**:
```java
// Entity
@Entity
public class Pedido {
    @Id private Long id;
    
    // Aggregate root
    public void adicionarItem(Produto produto, int quantidade) {
        if (!produto.estaDisponivel()) {
            throw new BusinessException(\"Produto indisponível\");
        }
        items.add(new ItemPedido(produto, quantidade));
    }
}

// Value Object
@Embeddable
public class Email {
    private final String valor;
    // Imutável, sem identidade
}

// Repository
public interface PedidoRepository {
    Pedido save(Pedido pedido);
    Optional<Pedido> findById(Long id);
}

// Domain Service
@Service
public class ProcessarPedidoService {
    public void processar(Pedido pedido) {
        // Lógica complexa envolvendo múltiplas entidades
    }
}
```

### 2. Layered Architecture (Arquitetura em Camadas)

**Objetivo**: Separar responsabilidades em camadas bem definidas.

**Camadas**:
1. **Presentation**: HTTP endpoints
2. **Application**: Casos de uso
3. **Domain**: Lógica de negócio
4. **Infrastructure**: Persistência e externos

```
Presentation → Application → Domain ← Infrastructure
```

### 3. CQRS (Command Query Responsibility Segregation)

**Objetivo**: Separar operações de leitura (Query) e escrita (Command).

**Implementação Simples**:
```java
// Commands - Escrita
@Service
public class CriarProdutoCommand {
    public ProdutoResponse executar(CriarProdutoRequest request) { }
}

// Queries - Leitura
@Service
public class ListarProdutosQuery {
    public Page<ProdutoResponse> executar(Pageable pageable) { }
}
```

## 🎯 Padrões de Design

### 1. Repository Pattern

Abstrai acesso a dados.

```java
// Interface (Domain)
public interface ProdutoRepository {
    Produto save(Produto produto);
    Optional<Produto> findById(Long id);
}

// Implementação (Infrastructure)
@Repository
public class JpaProdutoRepository 
    extends JpaRepository<Produto, Long>
    implements ProdutoRepository {
}
```

### 2. Factory Pattern

Encapsula criação de objetos complexos.

```java
public class ProdutoFactory {
    public static Produto criar(CriarProdutoRequest dto) {
        // Validações
        if (!dto.isValido()) {
            throw new BusinessException(\"Dados inválidos\");
        }
        
        // Criação
        return Produto.builder()
            .codigo(dto.getCodigo())
            .descricao(dto.getDescricao())
            .status(StatusProduto.ATIVO)
            .build();
    }
}
```

### 3. Strategy Pattern

Define família de algoritmos intercambiáveis.

```java
public interface ValidadorProduto {
    ValidationResult validar(Produto produto);
}

public class ValidadorProdutoComprado implements ValidadorProduto {
    @Override
    public ValidationResult validar(Produto produto) {
        // Validação específica para produto comprado
    }
}

public class ValidadorProdutoFabricavel implements ValidadorProduto {
    @Override
    public ValidationResult validar(Produto produto) {
        // Validação específica para produto fabricável
    }
}
```

### 4. Decorator Pattern

Adiciona responsabilidades dinamicamente.

```java
// Serviço base
public interface UsuarioService {
    Usuario criar(CriarUsuarioRequest dto);
}

// Decorator: Validação
public class ValidacaoDecoratorUsuarioService implements UsuarioService {
    private UsuarioService service;
    
    public Usuario criar(CriarUsuarioRequest dto) {
        validar(dto); // Validação
        return service.criar(dto);
    }
}

// Decorator: Auditoria
public class AuditoriaDecoratorUsuarioService implements UsuarioService {
    private UsuarioService service;
    
    public Usuario criar(CriarUsuarioRequest dto) {
        Usuario usuario = service.criar(dto);
        auditarCriacao(usuario); // Auditoria
        return usuario;
    }
}

// Composição
UsuarioService service = new AuditoriaDecoratorUsuarioService(
    new ValidacaoDecoratorUsuarioService(
        new UsuarioServiceImpl()
    )
);
```

### 5. Observer Pattern (Events)

Desacopla comunicação entre objetos.

```java
// Evento
public class UsuarioCriadoEvent extends ApplicationEvent {
    private final Usuario usuario;
    
    public UsuarioCriadoEvent(Object source, Usuario usuario) {
        super(source);
        this.usuario = usuario;
    }
}

// Publisher
@Service
public class CriarUsuarioService {
    private final ApplicationEventPublisher eventPublisher;
    
    public Usuario criar(CriarUsuarioRequest dto) {
        Usuario usuario = new Usuario(dto);
        repository.save(usuario);
        
        eventPublisher.publishEvent(new UsuarioCriadoEvent(this, usuario));
        return usuario;
    }
}

// Subscriber
@Component
public class EnviarEmailUsuarioCriadoListener {
    
    @EventListener
    public void handle(UsuarioCriadoEvent evento) {
        emailService.enviarBemVindo(evento.getUsuario());
    }
}
```

### 6. Specification Pattern

Define predicados reutilizáveis.

```java
public class ProdutoSpecifications {
    
    public static Specification<Produto> ativa() {
        return (root, query, cb) -> 
            cb.equal(root.get(\"status\"), StatusProduto.ATIVO);
    }
    
    public static Specification<Produto> comCodigo(String codigo) {
        return (root, query, cb) -> 
            cb.like(root.get(\"codigo\"), \"%\" + codigo + \"%\");
    }
    
    // Composição
    public static Specification<Produto> ativosComCodigo(String codigo) {
        return ativa().and(comCodigo(codigo));
    }
}

// Uso
repository.findAll(
    ProdutoSpecifications.ativosComCodigo(\"PROD\")
);
```

### 7. Adapter Pattern

Adapta interface incompatível.

```java
// Interface externa (ViaCEP)
public class ViaCepDto {
    public String logradouro;
    public String bairro;
}

// Adapter
public class ViaCepAdapter {
    private RestTemplate restTemplate;
    
    public Endereco buscarPorCep(String cep) {
        ViaCepDto dto = restTemplate.getForObject(...);
        
        // Adaptar ViaCepDto para Endereco
        return new Endereco(
            dto.logradouro,
            dto.bairro,
            // ... mapping
        );
    }
}
```

## 🔄 Padrões de Transação

### 1. Unit of Work

Gerencia transações e mudanças.

```java
@Service
@Transactional
public class CriarProdutoService {
    
    public ProdutoResponse executar(CriarProdutoRequest request) {
        // Toda a transação é gerenciada automaticamente
        
        Produto produto = criar(request);
        repository.save(produto); // Flush automático
        
        return mapper.toResponse(produto);
    } // Commit aqui
}
```

### 2. Aggregate Pattern

Garante consistência dentro de um agregado.

```java
@Entity
public class Pedido {
    @OneToMany(cascade = CascadeType.ALL)
    private List<ItemPedido> items;
    
    // Operações no agregado
    public void adicionarItem(ItemPedido item) {
        items.add(item);
        recalcularTotal(); // Manter consistência
    }
    
    // Nunca acessar ItemPedido diretamente
    // Sempre através de Pedido
}
```

## 🧪 Padrões de Teste

### 1. Arrange-Act-Assert (AAA)

```java
@Test
void deveCriarProdutoComSucesso() {
    // Arrange
    CriarProdutoRequest request = new CriarProdutoRequest(
        \"PROD001\", \"Produto Teste\", 1L
    );
    when(repository.findByCodigo(\"PROD001\"))
        .thenReturn(Optional.empty());
    
    // Act
    ProdutoResponse response = service.executar(request);
    
    // Assert
    assertNotNull(response);
    assertEquals(\"PROD001\", response.getCodigo());
}
```

### 2. Test Doubles

```java
// Mock
@MockBean
private ProdutoRepository mockRepository;

// Stub
when(mockRepository.findById(1L))
    .thenReturn(Optional.of(produto));

// Spy
Spy
ProdutoService spyService = spy(new ProdutoService());

// Fake
public class FakeProdutoRepository implements ProdutoRepository {
    private List<Produto> produtos = new ArrayList<>();
    
    @Override
    public Produto save(Produto p) {
        produtos.add(p);
        return p;
    }
}
```

## 📊 Padrões de Resposta HTTP

### Success Pattern
```json
{
    \"success\": true,
    \"data\": { \"id\": 1, ... },
    \"timestamp\": \"2025-12-25T10:00:00\"
}
```

### Error Pattern
```json
{
    \"success\": false,
    \"error\": {
        \"code\": \"VALIDATION_ERROR\",
        \"message\": \"Dados inválidos\",
        \"details\": { \"codigo\": \"Código é obrigatório\" }
    },
    \"timestamp\": \"2025-12-25T10:00:00\"
}
```

## 🎯 Quando Usar Cada Padrão

| Padrão | Quando Usar | Exemplo |
|--------|-----------|---------|
| Factory | Criação complexa | ProdutoFactory |
| Repository | Abstrair persistência | ProdutoRepository |
| Strategy | Múltiplos algoritmos | Validadores |
| Decorator | Adicionar comportamento | Logs, cache |
| Observer | Desacoplar eventos | UsuarioCriadoEvent |
| Specification | Queries complexas | ProdutoSpecifications |
| Adapter | Integrar externos | ViaCepAdapter |

## 🚀 Anti-Padrões a Evitar

❌ **God Object** - Classe que faz tudo
```java
// Ruim
public class ProdutoService {
    public void fazer101Coisas() { }
}

// Bom
public class CriarProdutoService { }
public class AtualizarProdutoService { }
```

❌ **Tight Coupling** - Acoplamento forte
```java
// Ruim
public class ProdutoService {
    private JpaProdutoRepository repository;
}

// Bom
public class ProdutoService {
    private ProdutoRepository repository;
}
```

❌ **Anemic Domain Model** - Entidade sem lógica
```java
// Ruim
@Entity
public class Produto {
    private String codigo;
    // Apenas getters/setters
}

// Bom
@Entity
public class Produto {
    private String codigo;
    
    public void ativar() { /* lógica */ }
    public boolean estaDisponivel() { /* lógica */ }
}
```

## 📚 Referências

- [DDD - Domain-Driven Design](https://domainlanguage.com/ddd/)
- [Clean Architecture](https://blog.cleancoder.com/)
- [Design Patterns - Gang of Four](https://refactoring.guru/design-patterns)

---

**Última atualização:** Dezembro de 2025
