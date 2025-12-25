# 🔐 Autorização e Controle de Acesso

## 📋 Visão Geral

Este documento detalha o sistema de autorização e controle de acesso granular implementado no ERP API.

## 🎯 Níveis de Controle

```
1. Autenticação
   ↓
2. Autorização (Role-based)
   ↓
3. Controle de Acesso (Permission-based)
   ↓
4. Autorização em Nível de Recurso
```

## 🔑 Role-Based Access Control (RBAC)

### Roles Predefinidas

| Role | Descrição | Permissões |
|------|-----------|-----------|
| ADMIN | Administrador completo | Todas |
| GERENCIADOR | Gerencia operações | CRUD operações |
| CONSULTOR | Apenas leitura | READ apenas |
| OPERADOR | Operador de sistema | CRUD limitado |
| VENDEDOR | Vendedor | CREATE pedidos, READ produtos |

### Configuração

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // Público
                .antMatchers(\"/api/v1/auth/**\").permitAll()
                
                // Apenas leitura
                .antMatchers(HttpMethod.GET, \"/api/v1/**\").permitAll()
                
                // Criar - ADMIN e GERENCIADOR
                .antMatchers(HttpMethod.POST, \"/api/v1/**\")
                    .hasAnyRole(\"ADMIN\", \"GERENCIADOR\")
                
                // Atualizar - ADMIN e GERENCIADOR
                .antMatchers(HttpMethod.PUT, \"/api/v1/**\")
                    .hasAnyRole(\"ADMIN\", \"GERENCIADOR\")
                
                // Deletar - ADMIN apenas
                .antMatchers(HttpMethod.DELETE, \"/api/v1/**\")
                    .hasRole(\"ADMIN\")
                
                .anyRequest().authenticated();
    }
}
```

## 🎛️ Permission-Based Access Control

### Permissões Granulares

```java
// Anotação customizada
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String recurso();
    String acao();
}

// Uso
@PostMapping
@RequirePermission(recurso = \"PRODUTO\", acao = \"CRIAR\")
public ResponseEntity<ProdutoResponse> criar(
    @Valid @RequestBody CriarProdutoRequest request
) {
    return ResponseEntity.ok(service.executar(request));
}
```

### Implementação do Validador

```java
@Component
@Aspect
@Slf4j
public class PermissaoValidadorAspect {
    
    private final PermissaoService permissaoService;
    
    @Around(\"@annotation(permission)\")
    public Object validarPermissao(
        ProceedingJoinPoint pjp,
        RequirePermission permission
    ) throws Throwable {
        
        // 1. Obter usuário atual
        Usuario usuario = getCurrentUser();
        
        if (usuario == null) {
            throw new AccessDeniedException(\"Usuário não autenticado\");
        }
        
        // 2. Verificar permissão
        boolean temPermissao = permissaoService.verificar(
            usuario,
            permission.recurso(),
            permission.acao()
        );
        
        if (!temPermissao) {
            log.warn(\"Acesso negado: {}. Recurso: {}, Ação: {}\",
                usuario.getId(),
                permission.recurso(),
                permission.acao()
            );
            throw new AccessDeniedException(
                String.format(\"Você não tem permissão: %s.%s\",
                    permission.recurso(),
                    permission.acao())
            );
        }
        
        // 3. Executar operação
        log.debug(\"Permitido: {}. Recurso: {}, Ação: {}\",
            usuario.getId(),
            permission.recurso(),
            permission.acao()
        );
        
        return pjp.proceed();
    }
    
    private Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder
            .getContext()
            .getAuthentication();
        
        return (Usuario) auth.getPrincipal();
    }
}
```

## 📊 Matriz de Controle de Acesso

### Produtos

| Ação | Admin | Gerenciador | Consultor | Operador | Vendedor |
|------|-------|-----------|-----------|----------|----------|
| CREATE | ✓ | ✓ | ✗ | ✓ | ✗ |
| READ | ✓ | ✓ | ✓ | ✓ | ✓ |
| UPDATE | ✓ | ✓ | ✗ | ✓ | ✗ |
| DELETE | ✓ | ✗ | ✗ | ✗ | ✗ |

### Usuários

| Ação | Admin | Gerenciador | Consultor | Operador |
|------|-------|-----------|-----------|----------|
| CREATE | ✓ | ✗ | ✗ | ✗ |
| READ | ✓ | ✓ | ✗ | ✗ |
| UPDATE | ✓ | ✗ | ✗ | ✗ |
| DELETE | ✓ | ✗ | ✗ | ✗ |
| APPROVE | ✓ | ✓ | ✗ | ✗ |

## 🔒 Multi-tenancy Authorization

```java
@Component
@Aspect
public class TenantAwareSecurityAspect {
    
    @Before(\"execution(* com.api.erp.v1.features.*.*.*(..)) && args(id, ..)\")
    public void validarAcessoEmpresa(JoinPoint jp, Long id) {
        Usuario usuario = getCurrentUser();
        Long empresaId = EmpresaContextHolder.getEmpresaId();
        
        // Verificar se usuário tem acesso à empresa
        if (!usuarioEmpresaService.temAcesso(usuario.getId(), empresaId)) {
            throw new AccessDeniedException(
                \"Você não tem acesso a esta empresa\"
            );
        }
    }
}
```

## 🧪 Testes de Autorização

```java
@SpringBootTest
@AutoConfigureMockMvc
public class AutorizacaoTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CriarProdutoService service;
    
    @Test
    void deveNegarAcessoSemPermissao() throws Exception {
        mockMvc.perform(post(\"/api/v1/produtos\")
            .with(user(\"consultor\").roles(\"CONSULTOR\")))
            .andExpect(status().isForbidden());
    }
    
    @Test
    void devePermitirAcessoComPermissao() throws Exception {
        when(service.executar(any()))
            .thenReturn(new ProdutoResponse());
        
        mockMvc.perform(post(\"/api/v1/produtos\")
            .with(user(\"gerenciador\").roles(\"GERENCIADOR\")))
            .andExpect(status().isCreated());
    }
}
```

## 🔐 Row-Level Security (Segurança por Linha)

```java
@Component
public class ProdutoRowLevelSecurityService {
    
    public Specification<Produto> filterByUserAccess(Usuario usuario) {
        // Admin vê todos
        if (usuario.temRole(\"ADMIN\")) {
            return (root, query, cb) -> cb.conjunction();
        }
        
        // Vendedor vê apenas produtos da sua empresa
        return (root, query, cb) -> 
            cb.equal(root.get(\"empresa\").get(\"id\"), usuario.getEmpresa().getId());
    }
}

// Uso no service
@Service
public class ListarProdutoService {
    
    public Page<ProdutoResponse> listar(Pageable pageable) {
        Usuario usuario = getCurrentUser();
        Specification<Produto> spec = 
            rowLevelSecurityService.filterByUserAccess(usuario);
        
        return repository.findAll(spec, pageable);
    }
}
```

## ⏱️ Time-based Authorization

```java
@Component
public class TimeBasedAuthorizationService {
    
    public boolean podeAcesar(Usuario usuario) {
        // Verificar horário de funcionamento
        LocalTime agora = LocalTime.now();
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fim = LocalTime.of(18, 0);
        
        if (usuario.ehAdmin()) {
            return true; // Admin acessa sempre
        }
        
        return agora.isAfter(inicio) && agora.isBefore(fim);
    }
}
```

## 🚀 Boas Práticas

1. **Princípio do Menor Privilégio**: Dar apenas o necessário
2. **Separação de Deveres**: Algumas ações precisam de aprovação
3. **Auditoria**: Log de acesso negado
4. **Expiração**: Permissões com data de validade
5. **MFA**: Multi-factor authentication para admin
6. **Invalidação**: Cache de permissões com TTL
7. **Reviewer**: Aprovador para mudanças críticas

## 📚 Referências Relacionadas

- [SEGURANCA.md](SEGURANCA.md) - Segurança geral
- [FEATURE_PERMISSAO.md](FEATURE_PERMISSAO.md) - Feature de permissão
- [FEATURE_USUARIO.md](FEATURE_USUARIO.md) - Gestão de usuários

---

**Última atualização:** Dezembro de 2025
