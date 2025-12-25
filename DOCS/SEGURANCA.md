# 🔐 Segurança e Autenticação

## 📋 Visão Geral

Este documento descreve os mecanismos de segurança implementados no ERP API para proteger dados e funcionalidades.

## 🎯 Estratégias de Segurança

### 1. Autenticação (Quem é você?)

**JWT Token-Based Authentication**

```java
// Login
POST /api/v1/auth/login
{
    \"email\": \"usuario@empresa.com\",
    \"senha\": \"senha123\"
}

// Response
{
    \"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",
    \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",
    \"expiresIn\": 3600,
    \"usuario\": {
        \"id\": 1,
        \"nomeCompleto\": \"João Silva\",
        \"email\": \"joao@empresa.com\"
    }
}

// Requisição autenticada
GET /api/v1/usuarios/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Implementação**

```java
@Component
public class JwtTokenProvider {
    
    private final String secretKey = \"sua-chave-secreta-muito-longa\";
    private final int jwtExpirationMs = 86400000; // 24 horas
    
    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
            .setSubject(usuario.getId().toString())
            .claim(\"email\", usuario.getEmail().getValor())
            .claim(\"nomeCompleto\", usuario.getNomeCompleto())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
    }
    
    public Long extrairUsuarioId(String token) {
        return Long.parseLong(
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
        );
    }
    
    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}

@Component
public class JwtFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String token = extrairToken(request);
        
        if (token != null && jwtTokenProvider.validarToken(token)) {
            Long usuarioId = jwtTokenProvider.extrairUsuarioId(token);
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            
            if (usuario != null) {
                // Autenticar no Spring Security
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        usuario.getAuthorities()
                    );
                
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extrairToken(HttpServletRequest request) {
        String header = request.getHeader(\"Authorization\");
        if (header != null && header.startsWith(\"Bearer \")) {
            return header.substring(7);
        }
        return null;
    }
}
```

### 2. Autorização (Qual sua permissão?)

**Autorização Baseada em Roles**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(\"/api/v1/auth/**\").permitAll()
                .antMatchers(\"/api/v1/produtos\").permitAll() // GET
                .antMatchers(HttpMethod.POST, \"/api/v1/produtos\")
                    .hasAnyRole(\"ADMIN\", \"GERENCIADOR\")
                .antMatchers(HttpMethod.PUT, \"/api/v1/produtos/**\")
                    .hasAnyRole(\"ADMIN\", \"GERENCIADOR\")
                .antMatchers(HttpMethod.DELETE, \"/api/v1/produtos/**\")
                    .hasRole(\"ADMIN\")
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

**Autorização Granular (Permissões)**

```java
@Component
@Aspect
public class PermissaoValidadorAspect {
    
    @Around(\"@annotation(requierePermissao)\")
    public Object validarPermissao(
        ProceedingJoinPoint pjp,
        RequierePermissao requierePermissao
    ) throws Throwable {
        
        Usuario usuario = getCurrentUser();
        String recurso = requierePermissao.recurso();
        TipoAcao acao = requierePermissao.acao();
        
        if (!permissaoService.verificar(usuario, recurso, acao)) {
            throw new AccessDeniedException(
                String.format(\"Acesso negado: %s.%s\", recurso, acao)
            );
        }
        
        return pjp.proceed();
    }
}

// Uso
@PostMapping
@RequierePermissao(recurso = \"PRODUTO\", acao = TipoAcao.CRIAR)
public ResponseEntity<ProdutoResponse> criar(
    @Valid @RequestBody CriarProdutoRequest request
) {
    return ResponseEntity.ok(service.executar(request));
}
```

### 3. Criptografia

**Senha (BCrypt)**

```java
@Component
public class PasswordEncoderService {
    
    private final PasswordEncoder encoder = new BCryptPasswordEncoder(10);
    
    public String codificar(String senhaPlana) {
        return encoder.encode(senhaPlana);
    }
    
    public boolean verificar(String senhaPlana, String senhaHash) {
        return encoder.matches(senhaPlana, senhaHash);
    }
}

// Uso
@Service
public class AutenticacaoService {
    
    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthenticationException(\"Credenciais inválidas\"));
        
        if (!passwordEncoder.verificar(request.getSenha(), usuario.getSenhaHash())) {
            throw new AuthenticationException(\"Credenciais inválidas\");
        }
        
        String token = jwtTokenProvider.gerarToken(usuario);
        return new LoginResponse(token);
    }
}
```

## 🔒 Checklist de Segurança

- ✅ HTTPS obrigatório (em produção)
- ✅ Senhas com hash forte (BCrypt)
- ✅ JWT token com expiração
- ✅ CORS configurado
- ✅ CSRF protection
- ✅ Rate limiting em endpoints críticos
- ✅ Validação de entrada
- ✅ Sanitização de output
- ✅ Logs de segurança
- ✅ Gestão de secrets (variáveis de ambiente)
- ✅ Soft delete (dados não deletados)
- ✅ Auditoria completa

## 📊 Rate Limiting

```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public RateLimiter loginRateLimiter() {
        return RateLimiter.create(5.0); // 5 requisições por segundo
    }
}

@RestController
@RequestMapping(\"/api/v1/auth\")
public class AutenticacaoController {
    
    private final RateLimiter rateLimiter;
    
    @PostMapping(\"/login\")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(\"Muitas tentativas de login. Tente novamente mais tarde.\");
        }
        
        return ResponseEntity.ok(autenticacaoService.autenticar(request));
    }
}
```

## 🚨 Tratamento de Erros Seguros

```java
// ❌ Inseguro - Expõe detalhes
{
    \"erro\": \"Email 'joao@empresa.com' não encontrado na tabela usuarios\"
}

// ✅ Seguro - Genérico
{
    \"erro\": \"Credenciais inválidas\"
}
```

## 📚 Referências Relacionadas

- [FEATURE_USUARIO.md](FEATURE_USUARIO.md) - Gerenciamento de usuários
- [FEATURE_PERMISSAO.md](FEATURE_PERMISSAO.md) - Sistema de permissões
- [AUTORIZACAO_ACESSO.md](AUTORIZACAO_ACESSO.md) - Detalhes de autorização

---

**Última atualização:** Dezembro de 2025
