# 🎨 Camada de Apresentação (Presentation Layer)

## 📋 Visão Geral

A camada de apresentação é responsável por **expor a API através de HTTP**, receber requisições, validar entrada e retornar respostas no formato apropriado.

## 🎯 Responsabilidades

- Receber requisições HTTP
- Validar entrada com @Valid
- Delegar para Application Services
- Formatar e retornar respostas
- Gerenciar códigos HTTP
- Documentar endpoints (Swagger/OpenAPI)
- Implementar tratamento de erros

## 🏗️ Estrutura

```
features/{feature}/presentation/
├── controller/         # REST Controllers
└── response/          # Response formatters
```

## 🔑 Conceitos-Chave

### 1️⃣ REST Controller

Expõe endpoints HTTP.

```java
@RestController
@RequestMapping("/api/v1/produtos")
@Slf4j
public class ProdutoController {
    
    private final CriarProdutoService criarService;
    private final ListarProdutoService listarService;
    private final AtualizarProdutoService atualizarService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProdutoResponse> criar(
        @Valid @RequestBody CriarProdutoRequest request
    ) {
        log.info("Criando novo produto: {}", request.getCodigo());
        ProdutoResponse response = criarService.executar(request);
        return ResponseEntity
            .created(URI.create("/api/v1/produtos/" + response.getId()))
            .body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> obter(@PathVariable Long id) {
        ProdutoResponse response = listarService.obterPorId(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> listar(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String codigo
    ) {
        Page<ProdutoResponse> response = listarService.listar(page, size, codigo);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(
        @PathVariable Long id,
        @Valid @RequestBody AtualizarProdutoRequest request
    ) {
        ProdutoResponse response = atualizarService.executar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        atualizarService.deletar(id);
    }
}
```

**Melhores Práticas**:
- ✓ Nomes descritivos em português
- ✓ Métodos pequenos e focados
- ✓ Logging apropriado
- ✓ Status HTTP corretos (201, 204, etc)
- ✓ URI nos responses (Location header)

### 2️⃣ Padrões HTTP

```java
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    
    // GET - Listar
    @GetMapping
    public List<UsuarioResponse> listar() { }
    
    // GET - Obter um
    @GetMapping("/{id}")
    public UsuarioResponse obter(@PathVariable Long id) { }
    
    // POST - Criar
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UsuarioResponse> criar(
        @Valid @RequestBody CriarUsuarioRequest request
    ) { }
    
    // PUT - Atualizar completo
    @PutMapping("/{id}")
    public UsuarioResponse atualizar(
        @PathVariable Long id,
        @Valid @RequestBody AtualizarUsuarioRequest request
    ) { }
    
    // PATCH - Atualizar parcial
    @PatchMapping("/{id}/ativar")
    public UsuarioResponse ativar(@PathVariable Long id) { }
    
    // DELETE - Deletar
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) { }
}
```

### 3️⃣ Tratamento de Erros

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage()
            ));
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessError(BusinessException ex) {
        log.warn("Erro de negócio: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BUSINESS_ERROR",
                ex.getMessage()
            ));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acesso negado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "ACCESS_DENIED",
                "Você não tem permissão para esta operação"
            ));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Erro na validação dos dados",
                errors
            ));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Erro inesperado", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "Erro interno do servidor"
            ));
    }
}
```

### 4️⃣ Response Formatter

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String code;
    private String message;
    private Map<String, String> errors;
    
    public ErrorResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    public static <T> ApiResponse<T> erro(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
```

### 5️⃣ Documentação com Swagger

```java
@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController {
    
    @PostMapping
    @Operation(
        summary = "Criar novo produto",
        description = "Cria um novo produto no catálogo",
        tags = {"Produtos"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Produto criado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Código de produto já existe"
        )
    })
    public ResponseEntity<ProdutoResponse> criar(
        @Valid @RequestBody CriarProdutoRequest request
    ) {
        // implementação
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter produto por ID")
    @Parameter(name = "id", description = "ID do produto", required = true)
    public ResponseEntity<ProdutoResponse> obter(@PathVariable Long id) {
        // implementação
    }
}
```

## 🔄 Fluxo de um Request

```
1. HTTP Request chega
   GET /api/v1/produtos/1
                ↓
2. Spring encontra Controller
                ↓
3. Valida @PathVariable, @RequestBody com @Valid
                ↓
4. Se inválido → ExceptionHandler → 400 Bad Request
                ↓
5. Controller chama Application Service
                ↓
6. Service executa lógica
                ↓
7. Se erro → ExceptionHandler → erro apropriado
                ↓
8. Controller formata resposta
                ↓
9. HTTP Response retorna ao cliente
   200 OK
   Content-Type: application/json
   {...response body...}
```

## ✅ Boas Práticas em Apresentação

1. **Controllers magros**: Apenas delegam
2. **DTOs específicos**: Por endpoint
3. **Status HTTP corretos**:
   - 200 OK - Sucesso com body
   - 201 Created - Recurso criado
   - 204 No Content - Sucesso sem body
   - 400 Bad Request - Dados inválidos
   - 401 Unauthorized - Não autenticado
   - 403 Forbidden - Sem permissão
   - 404 Not Found - Recurso não existe
   - 409 Conflict - Conflito (ex: código duplicado)
   - 500 Internal Server Error - Erro do servidor

4. **Validação em camadas**:
   - @Valid no controller (estrutura)
   - Business validation na aplicação (regras)
   - Database constraints (segurança final)

5. **Logging apropriado**:
   ```java
   log.info("Criando produto: {}", request.getCodigo());
   log.warn("Acesso negado para usuário: {}", userId);
   log.error("Erro ao processar pedido", ex);
   ```

6. **Versionamento de API**: `/api/v1/`
7. **Paginação para listas**:
   ```java
   Page<ProdutoResponse> listar(
       @RequestParam(defaultValue = "0") int page,
       @RequestParam(defaultValue = "20") int size
   )
   ```

8. **Filtros reutilizáveis**:
   ```java
   @GetMapping
   public Page<ProdutoResponse> listar(
       Pageable pageable,
       @RequestParam(required = false) String codigo,
       @RequestParam(required = false) StatusProduto status
   )
   ```

## 📡 Padrões de Endpoint

### Coleção
```
GET    /api/v1/produtos              # Listar
POST   /api/v1/produtos              # Criar
```

### Recurso Específico
```
GET    /api/v1/produtos/{id}         # Obter
PUT    /api/v1/produtos/{id}         # Atualizar completo
PATCH  /api/v1/produtos/{id}         # Atualizar parcial
DELETE /api/v1/produtos/{id}         # Deletar
```

### Operações Customizadas
```
PATCH  /api/v1/produtos/{id}/ativar
PATCH  /api/v1/produtos/{id}/bloquear
POST   /api/v1/produtos/{id}/composicao
GET    /api/v1/produtos/{id}/composicao
```

## 🧪 Testes de Controller

```java
@SpringBootTest
@AutoConfigureMockMvc
class ProdutoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CriarProdutoService criarService;
    
    @Test
    void deveCriarProdutoComSucesso() throws Exception {
        ProdutoResponse response = new ProdutoResponse();
        response.setId(1L);
        response.setCodigo("PROD001");
        
        when(criarService.executar(any()))
            .thenReturn(response);
        
        mockMvc.perform(post("/api/v1/produtos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "codigo": "PROD001",
                    "descricao": "Produto Teste",
                    "unidadeMedidaId": 1,
                    "tipo": "COMPRADO"
                }
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.codigo").value("PROD001"));
    }
}
```

## 📚 Referências Relacionadas

- [CAMADA_APPLICATION.md](CAMADA_APPLICATION.md) - Aplicação
- [SEGURANCA.md](SEGURANCA.md) - Autenticação e autorização

---

**Última atualização:** Dezembro de 2025
