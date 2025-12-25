# 🔐 Feature: Permissão/Acesso

## 📋 Visão Geral

A feature **Permissão** implementa um sistema granular de controle de acesso baseado em roles (papéis) e permissões específicas. Permite definir quem pode fazer o quê no sistema.

## 🎯 Responsabilidades

- Gerenciar permissões (criar, atualizar, deletar)
- Criar e gerenciar roles (papéis)
- Associar permissões a roles
- Atribuir roles a usuários
- Validar acesso em cada operação
- Auditoria de acesso negado
- Suportar permissões granulares

## 📊 Entidades Principais

### **UsuarioPermissao**
Associação entre usuário e permissão/role.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | Identificador único |
| `usuario` | Usuario | Usuário que tem a permissão |
| `tipoAcao` | TipoAcao | Tipo de ação permitida |
| `recurso` | String | Recurso (feature) |
| `ativa` | Boolean | Se está ativa |
| `dataCriacao` | LocalDateTime | Quando foi atribuída |
| `dataValidade` | LocalDateTime | Quando expira (opcional) |

### **TipoAcao** (Enum)
```java
CRIAR       // Criar novo recurso
LER         // Visualizar recurso
ATUALIZAR   // Modificar recurso
DELETAR     // Remover recurso
APROVAR     // Aprovar operação
REJEITAR    // Rejeitar operação
EXECUTAR    // Executar ação especial
GERENCIAR   // Gerenciar configuração
```

### **RolePermissions** (Enum)
```java
ROLE_ADMIN          // Administrador (acesso total)
ROLE_GERENCIADOR    // Gerenciador de operações
ROLE_CONSULTOR      // Apenas leitura
ROLE_OPERADOR       // Operador de sistema
ROLE_GESTOR_RH      // Gestor de Recursos Humanos
ROLE_GESTOR_FINANCEIRO  // Gestor Financeiro
ROLE_GESTOR_ESTOQUE // Gestor de Estoque
ROLE_VENDEDOR       // Vendedor
```

## 🏗️ Estrutura de Diretórios

```
features/permissao/
├── domain/
│   ├── entity/
│   │   ├── UsuarioPermissao.java
│   │   ├── TipoAcao.java
│   │   ├── RolePermissions.java
│   │   └── Permissao.java
│   │
│   ├── service/
│   │   ├── PermissaoService.java
│   │   └── PermissaoValidator.java
│   │
│   └── repository/
│       ├── UsuarioPermissaoRepository.java
│       ├── PermissaoRepository.java
│       └── RoleRepository.java
│
├── application/
│   ├── service/
│   │   ├── AtribuirPermissaoService.java
│   │   ├── RevogarPermissaoService.java
│   │   ├── ListarPermissoesService.java
│   │   └── PermissaoServiceImpl.java
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── AtribuirPermissaoRequest.java
│   │   │   ├── RevogarPermissaoRequest.java
│   │   │   └── VerificarAcessoRequest.java
│   │   │
│   │   └── response/
│   │       ├── PermissaoResponse.java
│   │       ├── PermissoesUsuarioResponse.java
│   │       └── ValidacaoAcessoResponse.java
│   │
│   └── validator/
│       └── PermissaoValidator.java
│
├── infrastructure/
│   ├── repository/
│   │   ├── JpaUsuarioPermissaoRepository.java
│   │   ├── JpaPermissaoRepository.java
│   │   └── JpaRoleRepository.java
│   │
│   └── adapter/
│       └── PermissaoFilterAdapter.java
│
└── presentation/
    └── controller/
        └── PermissaoController.java
```

## 🔄 Fluxos Principais

### 1️⃣ Atribuir Permissão a Usuário
```
1. Controller recebe AtribuirPermissaoRequest
2. Verifica se operador tem permissão para atribuir
3. Valida se usuário e permissão existem
4. Cria UsuarioPermissao
5. Registra quem atribuiu e quando
6. Persiste no banco
7. Se usuário logado, invalida cache de permissions
8. Retorna sucesso
```

### 2️⃣ Verificar Acesso (Filter/Interceptor)
```
1. Request chega no endpoint protegido
2. Extrai usuário do JWT/Session
3. Carrega permissões do usuário (cache)
4. Verifica se tem acesso a recurso/ação
5. Se não tem, lança AccessDeniedException
6. Se tem, continua execução
7. Audit registra acesso
```

### 3️⃣ Listar Permissões do Usuário
```
1. Busca todas UsuarioPermissao do usuário
2. Filtra permissões ativas
3. Carrega detalhes (recurso, ação)
4. Agrupa por recurso
5. Retorna PermissoesUsuarioResponse
```

## 📡 Endpoints da API

### Gerenciamento de Permissões
```
GET    /api/v1/permissoes                    # Listar todas
GET    /api/v1/permissoes/{id}               # Obter por ID
POST   /api/v1/permissoes                    # Criar
PUT    /api/v1/permissoes/{id}               # Atualizar
DELETE /api/v1/permissoes/{id}               # Deletar
```

### Usuário e Permissões
```
GET    /api/v1/usuarios/{id}/permissoes           # Listar permissões do usuário
POST   /api/v1/usuarios/{id}/permissoes           # Atribuir permissão
DELETE /api/v1/usuarios/{id}/permissoes/{permId}  # Remover permissão
```

### Validação
```
POST   /api/v1/permissoes/validar/{recurso}/{acao}  # Validar acesso
GET    /api/v1/permissoes/meu-acesso                # Listar acesso do logado
```

### Roles
```
GET    /api/v1/roles                    # Listar roles
GET    /api/v1/roles/{id}               # Obter role
POST   /api/v1/roles                    # Criar role
PUT    /api/v1/roles/{id}               # Atualizar role
POST   /api/v1/roles/{id}/permissoes    # Adicionar permissão a role
```

## ✅ Validações

### Ao Atribuir Permissão
- ✓ Usuário deve existir e estar ativo
- ✓ Permissão deve ser válida
- ✓ Operador deve ter permissão para atribuir
- ✓ Não pode ter permissão duplicada
- ✓ Data de validade deve ser maior que hoje (se fornecida)

## 🔐 Estratégia de Segurança

### Controle de Acesso Baseado em Roles (RBAC)
```
Usuário → Roles → Permissões → Recurso
```

### Verificação em Múltiplas Camadas

1. **Authentication**: Quem é você? (JWT)
2. **Authorization**: Qual sua role? (RBAC)
3. **Resource-Level**: Pode acessar este recurso? (Permissão)

### Exemplo de Decorator Pattern
```java
@Component
public class PermissaoValidadorDecorator {
    
    @Around("@annotation(RequiresPermission)")
    public Object validarPermissao(ProceedingJoinPoint pjp) 
        throws Throwable {
        
        Usuario usuario = SecurityContextHolder.getAuthentication();
        String recurso = getRecurso(pjp);
        String acao = getAcao(pjp);
        
        if (!temPermissao(usuario, recurso, acao)) {
            throw new AccessDeniedException();
        }
        
        return pjp.proceed();
    }
}
```

## 🧪 Testes

```java
PermissaoEntityTest.java
PermissaoServiceTest.java
AtribuirPermissaoServiceTest.java
PermissaoValidatorDecoratorTest.java
PermissaoControllerTest.java
```

## 🔗 Relacionamentos

```
Usuario (muitos-para-muitos) Permissao
    via UsuarioPermissao

Permissao
    ├── TipoAcao (enum)
    ├── Recurso (string)
    └── RolePermissions (enum opcional)
```

## 📊 Matriz de Permissões

| Role | Produto | Usuário | Empresa | Permissão |
|------|---------|---------|---------|-----------|
| ADMIN | CRUD | CRUD | CRUD | GERENCIAR |
| GERENCIADOR | CRU | R | R | GERENCIAR |
| CONSULTOR | R | R | R | - |
| OPERADOR | CRU | - | - | - |
| GESTOR_ESTOQUE | CRU | - | R | - |

## 🚀 Boas Práticas

1. **Princípio do Menor Privilégio**: Dar apenas o necessário
2. **Cache de Permissões**: Para performance em cada request
3. **Invalidar Cache**: Quando permissão mudar
4. **Auditoria Completa**: Log de acesso negado
5. **Expiração de Permissão**: Data de validade
6. **Reviewer Obrigatório**: Para certos acessos críticos
7. **MFA para ADMIN**: Acesso administrativo com 2FA
8. **Segregação de Deveres**: Alguns roles não podem ser combinados

## 🔍 Exemplo de Uso

```java
// Controller
@PostMapping("/criar")
@RequiresPermission(recurso = "PRODUTO", acao = TipoAcao.CRIAR)
public ResponseEntity<?> criar(@Valid @RequestBody CriarProdutoRequest dto) {
    return ResponseEntity.ok(service.criar(dto));
}

// Implementação da validação (Aspect)
@Component
@Aspect
public class PermissaoAspect {
    
    @Before("@annotation(permission)")
    public void checkPermission(JoinPoint jp, RequiresPermission permission) {
        Usuario usuario = getCurrentUser();
        if (!temPermissao(usuario, permission.recurso(), permission.acao())) {
            throw new AccessDeniedException(
                String.format("Acesso negado: %s.%s", 
                    permission.recurso(), permission.acao())
            );
        }
    }
}
```

## 📚 Referências Relacionadas

- [FEATURE_USUARIO.md](FEATURE_USUARIO.md) - Usuários e autenticação
- [SEGURANCA.md](SEGURANCA.md) - Estratégia de segurança
- [AUTORIZACAO_ACESSO.md](AUTORIZACAO_ACESSO.md) - Detalhes de autorização

---

**Última atualização:** Dezembro de 2025
