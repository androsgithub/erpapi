# 📋 Camada de Aplicação (Application Layer)

## 📋 Visão Geral

A camada de aplicação é responsável por **orquestrar casos de uso**, conectando a camada de domínio com a apresentação. Implementa a lógica de negócio em termo de processos/workflows.

## 🎯 Responsabilidades

- Orquestrar serviços de domínio
- Implementar casos de uso
- Validar entrada de usuários
- Converter entre DTOs e entidades
- Gerenciar transações
- Implementar autorização

## 🏗️ Estrutura

```
features/{feature}/application/
├── service/          # Application services (use cases)
├── dto/              # Data Transfer Objects
│   ├── request/      # Input DTOs
│   └── response/     # Output DTOs
├── mapper/           # DTO ↔ Entity mappers
├── validator/        # Validadores específicos
└── usecase/          # Casos de uso explícitos
```

## 🔑 Conceitos-Chave

### 1️⃣ Application Service

Orquestra um caso de uso completo.

```java
@Service
@Transactional
public class CriarProdutoService {
    
    private final ProdutoRepository produtoRepository;
    private final UnidadeMedidaRepository unidadeRepository;
    private final ProdutoValidator validator;
    private final ProdutoMapper mapper;
    
    public ProdutoResponse executar(CriarProdutoRequest request) {
        
        // 1. Validação de entrada
        validator.validarCriacao(request);
        
        // 2. Verificar pré-condições
        if (produtoRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new BusinessException("Código de produto já existe");
        }
        
        // 3. Converter DTO em Entity
        Produto produto = mapper.requestToEntity(request);
        
        // 4. Carregar dependências
        UnidadeMedida unidade = unidadeRepository
            .findById(request.getUnidadeMedidaId())
            .orElseThrow(() -> new NotFoundException("Unidade não encontrada"));
        
        // 5. Executar domínio
        produto.setUnidade(unidade);
        // ... aplicar lógica de domínio
        
        // 6. Persistir
        Produto criado = produtoRepository.save(produto);
        
        // 7. Converter para response
        return mapper.entityToResponse(criado);
    }
}
```

**Características**:
- ✓ Métodos específicos para cada caso de uso
- ✓ @Transactional para garantir consistência
- ✓ Orquestra múltiplos domínios se necessário
- ✓ Uma responsabilidade clara

### 2️⃣ DTO (Data Transfer Object)

Objeto que transfere dados entre camadas.

```java
// Request DTO - entrada do usuário
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarProdutoRequest {
    
    @NotBlank(message = "Código é obrigatório")
    @Size(min = 3, max = 50)
    private String codigo;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 5, max = 255)
    private String descricao;
    
    @NotNull(message = "Unidade de medida é obrigatória")
    private Long unidadeMedidaId;
    
    @NotNull(message = "Tipo de produto é obrigatório")
    private TipoProduto tipo;
    
    @DecimalMin("0.01")
    private BigDecimal margemLucro;
}

// Response DTO - saída para o cliente
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoResponse {
    
    private Long id;
    private String codigo;
    private String descricao;
    private StatusProduto status;
    private TipoProduto tipo;
    private UnidadeMedidaResponse unidade;
    private BigDecimal margemLucro;
    private LocalDateTime dataCriacao;
}
```

**Características**:
- ✓ Validações com anotações (Bean Validation)
- ✓ Imutáveis (usar @Data ou records)
- ✓ Não contêm lógica de negócio
- ✓ Específicos para cada endpoint/caso de uso

### 3️⃣ Mapper

Converte entre DTOs e Entities.

```java
@Component
@Slf4j
public class ProdutoMapper {
    
    public Produto requestToEntity(CriarProdutoRequest request) {
        return Produto.builder()
            .codigo(request.getCodigo())
            .descricao(request.getDescricao())
            .tipo(request.getTipo())
            .margemLucro(request.getMargemLucro())
            .status(StatusProduto.ATIVO)
            .dataCriacao(LocalDateTime.now())
            .build();
    }
    
    public ProdutoResponse entityToResponse(Produto produto) {
        return ProdutoResponse.builder()
            .id(produto.getId())
            .codigo(produto.getCodigo())
            .descricao(produto.getDescricao())
            .status(produto.getStatus())
            .tipo(produto.getTipo())
            .unidade(mapearUnidade(produto.getUnidade()))
            .margemLucro(produto.getMargemLucro())
            .dataCriacao(produto.getDataCriacao())
            .build();
    }
    
    private UnidadeMedidaResponse mapearUnidade(UnidadeMedida unidade) {
        if (unidade == null) return null;
        return UnidadeMedidaResponse.builder()
            .id(unidade.getId())
            .codigo(unidade.getCodigo())
            .descricao(unidade.getDescricao())
            .build();
    }
}
```

### 4️⃣ Validator

Valida regras de negócio específicas.

```java
@Component
public class ProdutoValidator {
    
    private final ProdutoRepository repository;
    
    public void validarCriacao(CriarProdutoRequest request) {
        // Validações customizadas além de @Valid
        
        if (repository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new BusinessException("Código já existe");
        }
        
        if (request.getMargemLucro().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Margem não pode ser negativa");
        }
        
        // Validações complexas que dependem de repositórios
    }
    
    public void validarAtualizacao(Long id, AtualizarProdutoRequest request) {
        Produto produto = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Produto não encontrado"));
        
        if (!produto.podeSerAtualizado()) {
            throw new BusinessException("Produto não pode ser atualizado agora");
        }
    }
}
```

## 🔄 Fluxo de um Caso de Uso

```
1. Controller recebe HTTP request
                ↓
2. Spring valida @Valid (Bean Validation)
                ↓
3. Controller chama Application Service
                ↓
4. Aplicação Service valida (Validator)
                ↓
5. Carrega dependências (repositories)
                ↓
6. Executa lógica de domínio (Domain Services)
                ↓
7. Persiste mudanças (JPA)
                ↓
8. Mapeia para Response DTO
                ↓
9. Controller retorna HTTP response
```

## 🔄 Padrões Comuns

### Padrão CQRS Simples

Separar leitura (Query) de escrita (Command):

```java
// Commands - Modificam estado
@Service
public class CriarProdutoCommand {
    public ProdutoResponse executar(CriarProdutoRequest request) { }
}

// Queries - Apenas leem
@Service
public class ListarProdutosQuery {
    public List<ProdutoResponse> executar(FiltroRequest filtro) { }
}
```

### Padrão Builder para DTOs

```java
ProdutoResponse response = ProdutoResponse.builder()
    .id(1L)
    .codigo("PROD001")
    .descricao("Produto Teste")
    .status(StatusProduto.ATIVO)
    .build();
```

## ✅ Validações em Camadas

```
┌─────────────────────────────────────────┐
│ 1. Bean Validation (@Valid, @NotNull)   │
│    - Estrutura do DTO                   │
├─────────────────────────────────────────┤
│ 2. Application Validator                │
│    - Regras de negócio                  │
├─────────────────────────────────────────┤
│ 3. Domain Service                       │
│    - Lógica complexa                    │
├─────────────────────────────────────────┤
│ 4. Database Constraints                 │
│    - Segurança final                    │
└─────────────────────────────────────────┘
```

## 🧪 Testes de Aplicação

```java
@SpringBootTest
class CriarProdutoServiceTest {
    
    @MockBean
    private ProdutoRepository repository;
    
    @Autowired
    private CriarProdutoService service;
    
    @Test
    void deveCriarProdutoComValoresValidos() {
        // Arrange
        CriarProdutoRequest request = new CriarProdutoRequest(
            "PROD001", "Produto Teste", 1L, TipoProduto.COMPRADO
        );
        
        Produto produtoEsperado = new Produto();
        when(repository.save(any())).thenReturn(produtoEsperado);
        
        // Act
        ProdutoResponse response = service.executar(request);
        
        // Assert
        assertNotNull(response);
        assertEquals("PROD001", response.getCodigo());
        verify(repository, times(1)).save(any());
    }
    
    @Test
    void naoDeveCriarProdutoComCodigoDuplicado() {
        // Arrange
        CriarProdutoRequest request = new CriarProdutoRequest(
            "PROD001", "Produto Teste", 1L, TipoProduto.COMPRADO
        );
        
        when(repository.findByCodigo("PROD001"))
            .thenReturn(Optional.of(new Produto()));
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            service.executar(request);
        });
    }
}
```

## 🚀 Boas Práticas

1. **Uma responsabilidade**: Cada service = um caso de uso
2. **@Transactional obrigatório**: Para manter consistência
3. **Validação em dois pontos**:
   - Bean Validation (@Valid)
   - Business Validation (Validator)
4. **DTOs separados**: request vs response
5. **Mappers centralizados**: Uma fonte de verdade
6. **Nenhuma lógica de banco**: Use repositories
7. **Nenhuma lógica HTTP**: Use controllers
8. **Testes unitários**: Com mocks de dependências
9. **Nomes descritivos**: `CriarProdutoService` vs `ProdutoManager`

## 📊 Exemplo Completo de Caso de Uso

```java
@Service
@Transactional
@Slf4j
public class AprovarUsuarioService {
    
    private final UsuarioRepository repository;
    private final PermissaoService permissaoService;
    private final EmailService emailService;
    
    public UsuarioResponse executar(Long usuarioId, Long aprovadorId) {
        
        // 1. Validar permissão do aprovador
        Usuario aprovador = repository.findById(aprovadorId).orElseThrow();
        if (!permissaoService.tem(aprovador, "USUARIO_APROVAR")) {
            throw new AccessDeniedException("Sem permissão para aprovar");
        }
        
        // 2. Buscar usuário pendente
        Usuario usuario = repository.findById(usuarioId).orElseThrow();
        if (usuario.getStatus() != StatusUsuario.PENDENTE_APROVACAO) {
            throw new BusinessException("Usuário não está pendente de aprovação");
        }
        
        // 3. Executar lógica de domínio
        usuario.aprovar(aprovador);
        
        // 4. Persistir
        Usuario aprovado = repository.save(usuario);
        
        // 5. Efeitos colaterais (email)
        emailService.enviarAprovacao(aprovado);
        
        // 6. Retornar
        log.info("Usuário {} aprovado por {}", usuarioId, aprovadorId);
        return mapper.toResponse(aprovado);
    }
}
```

## 📚 Referências Relacionadas

- [CAMADA_DOMAIN.md](CAMADA_DOMAIN.md) - Domínio
- [CAMADA_PRESENTATION.md](CAMADA_PRESENTATION.md) - Apresentação
- [FEATURE_PRODUTO.md](FEATURE_PRODUTO.md) - Exemplo prático

---

**Última atualização:** Dezembro de 2025
