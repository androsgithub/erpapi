# 📁 Estrutura do Projeto

## Visão Geral da Organização

```
erpapi/
├── src/
│   ├── main/
│   │   ├── java/com/api/erp/
│   │   │   ├── ErpApplication.java          # Classe principal
│   │   │   └── v1/
│   │   │       ├── features/                # Features de negócio
│   │   │       └── shared/                  # Componentes compartilhados
│   │   └── resources/                       # Configurações e assets
│   └── test/
│       └── java/com/api/erp/                # Testes automatizados
│
├── DOCS/                                    # Documentação
├── pom.xml                                  # Configuração Maven
├── README.md                                # Visão geral do projeto
└── [scripts e configurações]
```

## 🗂️ Estrutura de Features

Cada feature segue o padrão:

### `src/main/java/com/api/erp/v1/features/{feature}/`

```
{feature}/
│
├── 📂 domain/                           # CAMADA DE DOMÍNIO (DDD)
│   ├── entity/
│   │   ├── {Entity}.java               # Entidades principais
│   │   ├── {Entity}Permissions.java    # Permissões específicas
│   │   ├── Status{Entity}.java         # Enums de status
│   │   └── ...
│   │
│   ├── service/
│   │   └── {Entity}Service.java        # Serviços de domínio
│   │
│   ├── repository/
│   │   └── {Entity}Repository.java     # Interfaces (contratos)
│   │
│   ├── valueobject/
│   │   └── {ValueObject}.java          # Value Objects
│   │
│   └── factory/
│       └── {Entity}Factory.java        # Factories de criação
│
├── 📂 application/                      # CAMADA DE APLICAÇÃO
│   ├── service/
│   │   ├── {Entity}ServiceImpl.java     # Implementações
│   │   ├── {Entity}Manager.java        # Gerenciadores
│   │   └── ...
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── Create{Entity}Request.java
│   │   │   ├── Update{Entity}Request.java
│   │   │   └── ...
│   │   │
│   │   └── response/
│   │       ├── {Entity}Response.java
│   │       ├── {Entity}DetailResponse.java
│   │       └── ...
│   │
│   ├── validator/
│   │   ├── {Entity}Validator.java
│   │   └── ...
│   │
│   └── mapper/
│       └── {Entity}Mapper.java         # Mapeadores DTO ↔ Entity
│
├── 📂 infrastructure/                   # CAMADA DE INFRAESTRUTURA
│   ├── repository/
│   │   └── Jpa{Entity}Repository.java  # Implementação JPA
│   │
│   ├── adapter/
│   │   └── {Entity}Adapter.java        # Adaptadores
│   │
│   ├── persistence/
│   │   └── {Entity}EntityListener.java # Event Listeners
│   │
│   └── config/
│       └── {Entity}Config.java         # Configurações
│
└── 📂 presentation/                     # CAMADA DE APRESENTAÇÃO
    ├── controller/
    │   └── {Entity}Controller.java     # Controllers REST
    │
    └── response/
        └── {Entity}ResponseFormatter.java # Formatadores

```

## 🔗 Shared - Componentes Compartilhados

### `src/main/java/com/api/erp/v1/shared/`

```
shared/
│
├── 📂 domain/
│   ├── exception/
│   │   ├── BusinessException.java      # Exceção de negócio
│   │   ├── NotFoundException.java      # Recurso não encontrado
│   │   ├── ValidationException.java    # Validação
│   │   └── ...
│   │
│   ├── valueobject/
│   │   ├── Email.java                  # VO: Email
│   │   ├── CPF.java                    # VO: CPF
│   │   └── ...
│   │
│   └── event/
│       └── {Entity}Event.java          # Eventos de domínio
│
├── 📂 infrastructure/
│   ├── config/
│   │   ├── JpaConfig.java
│   │   ├── SecurityConfig.java
│   │   └── ...
│   │
│   ├── persistence/
│   │   ├── AuditListener.java
│   │   └── ...
│   │
│   └── adapter/
│       └── [Adaptadores compartilhados]
│
└── 📂 websocket/
    ├── config/
    │   └── WebSocketConfig.java        # Configuração WebSocket
    │
    ├── controller/
    │   └── WebSocketController.java    # Controladores WebSocket
    │
    └── handler/
        └── [Handlers de mensagem]

```

## 📦 Recursos

### `src/main/resources/`

```
resources/
│
├── application.properties               # Config padrão
├── application-dev.properties           # Config desenvolvimento
├── application-prod.properties          # Config produção
│
├── 📂 static/
│   ├── sockjs.html                     # Exemplo WebSocket
│   ├── css/                            # Estilos CSS
│   ├── js/                             # Scripts JavaScript
│   └── ...
│
└── 📂 templates/
    └── [Templates Thymeleaf, etc]

```

## 🧪 Testes

### `src/test/java/com/api/erp/`

```
test/
│
├── ErpApplicationTests.java            # Testes de integração
├── TestcontainersConfiguration.java    # Config Test Containers
├── TestErpApplication.java             # Setup de testes
│
└── features/
    ├── {feature}/
    │   ├── domain/
    │   │   ├── entity/
    │   │   │   └── {Entity}Test.java
    │   │   │
    │   │   └── service/
    │   │       └── {Entity}ServiceTest.java
    │   │
    │   ├── application/
    │   │   └── service/
    │   │       └── {Entity}ServiceImplTest.java
    │   │
    │   └── presentation/
    │       └── controller/
    │           └── {Entity}ControllerTest.java

```

## 🔍 Detalhamento de Pastas

### 1️⃣ **Domain** (Domínio)

**Propósito**: Contém a lógica de negócio pura, independente de frameworks.

| Subpasta | Conteúdo |
|----------|----------|
| `entity/` | Entidades JPA com comportamento de domínio |
| `service/` | Serviços que orquestram múltiplas entidades |
| `repository/` | Interfaces (contratos) de acesso a dados |
| `valueobject/` | Objetos de valor imutáveis |
| `factory/` | Factories para criação de entidades |

**Exemplo Entity**:
```java
@Entity
@Table(name = "produtos")
public class Produto {
    @Id @GeneratedValue
    private Long id;
    
    @Column(nullable = false)
    private String descricao;
    
    // Comportamentos
    public void ativar() { /* ... */ }
    public boolean estaAtivo() { /* ... */ }
}
```

### 2️⃣ **Application** (Aplicação)

**Propósito**: Orquestra casos de uso, conectando domínio com apresentação.

| Subpasta | Conteúdo |
|----------|----------|
| `service/` | Implementações de serviços de aplicação |
| `dto/` | Data Transfer Objects (request/response) |
| `validator/` | Validadores específicos do caso de uso |
| `mapper/` | Mapeadores entre DTOs e Entities |

**Exemplo DTO Request**:
```java
public class CriarProdutoRequest {
    @NotBlank
    private String codigo;
    
    @NotBlank
    private String descricao;
    
    @NotNull
    private Long unidadeMedidaId;
}
```

### 3️⃣ **Infrastructure** (Infraestrutura)

**Propósito**: Implementa detalhes técnicos (persistência, externa).

| Subpasta | Conteúdo |
|----------|----------|
| `repository/` | Implementações JPA/Spring Data |
| `adapter/` | Adaptadores para sistemas externos |
| `persistence/` | Configurações de BD, listeners |
| `config/` | Configurações técnicas |

**Exemplo Repository JPA**:
```java
@Repository
public class JpaProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByCodigo(String codigo);
}
```

### 4️⃣ **Presentation** (Apresentação)

**Propósito**: Expõe a API via HTTP, formata respostas.

| Subpasta | Conteúdo |
|----------|----------|
| `controller/` | Controllers REST (endpoints) |
| `response/` | Formatadores de resposta |

**Exemplo Controller**:
```java
@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController {
    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(
        @Valid @RequestBody CriarProdutoRequest request
    ) {
        // lógica
    }
}
```

## 🗂️ Padrão de Nomenclatura

### Entidades e Classes
- **Entity**: `Produto`, `Usuario`, `Empresa`
- **Service (Domain)**: `ProdutoService`
- **Service (Application)**: `CriarProdutoService`, `ProdutoServiceImpl`
- **Repository**: `ProdutoRepository`, `JpaProdutoRepository`
- **DTO Request**: `CriarProdutoRequest`, `AtualizarProdutoRequest`
- **DTO Response**: `ProdutoResponse`, `ProdutoDetailResponse`
- **Controller**: `ProdutoController`
- **Validator**: `ProdutoValidator`
- **Permission**: `ProdutoPermissions`

### Value Objects
- `Email` - Para e-mails
- `CPF` - Para CPF
- `Money` - Para valores monetários

## 📊 Dependências entre Camadas

```
presentation/
    ↓ (depende de)
application/
    ↓ (depende de)
domain/
    ↓ (depende de)
infrastructure/
```

**Regra Principal**: Camadas superiores dependem de camadas inferiores, mas não vice-versa.

## 🎯 Exemplo Completo: Feature Produto

```
features/produto/
│
├── domain/
│   ├── entity/
│   │   ├── Produto.java
│   │   ├── ProdutoComposicao.java
│   │   ├── ClassificacaoFiscal.java
│   │   ├── TipoProduto.java
│   │   ├── StatusProduto.java
│   │   └── ProdutoPermissions.java
│   │
│   ├── service/
│   │   ├── ProdutoService.java
│   │   └── ComposicaoProdutoService.java
│   │
│   ├── repository/
│   │   └── ProdutoRepository.java
│   │
│   └── factory/
│       └── ProdutoFactory.java
│
├── application/
│   ├── service/
│   │   ├── CriarProdutoService.java
│   │   ├── AtualizarProdutoService.java
│   │   └── ProdutoServiceImpl.java
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CriarProdutoRequest.java
│   │   │   └── AtualizarProdutoRequest.java
│   │   │
│   │   └── response/
│   │       ├── ProdutoResponse.java
│   │       └── ProdutoDetailResponse.java
│   │
│   ├── validator/
│   │   └── ProdutoValidator.java
│   │
│   └── mapper/
│       └── ProdutoMapper.java
│
├── infrastructure/
│   └── repository/
│       └── JpaProdutoRepository.java
│
└── presentation/
    ├── controller/
    │   └── ProdutoController.java
    │
    └── response/
        └── ProdutoResponseFormatter.java
```

---

**Última atualização:** Dezembro de 2025
