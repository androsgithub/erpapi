# 👤 Feature: Usuário

## 📋 Visão Geral

A feature **Usuário** é responsável pelo gerenciamento completo de usuários do sistema, incluindo criação, autenticação, autorização e controle de permissões.

## 🎯 Responsabilidades

- Gerenciar usuários (criar, atualizar, deletar, listar)
- Validar dados de usuário (email, CPF)
- Gerenciar senha (hash, resetar)
- Controlar status de usuário (ativo, inativo, bloqueado)
- Validar aprovação de novos usuários
- Gerenciar associações com permissões
- Implementar segurança de acesso

## 📊 Entidades Principais

### **Usuario**
Entidade core que representa um usuário.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | Identificador único |
| `nomeCompleto` | String | Nome completo do usuário |
| `email` | Email (VO) | Email único (Value Object) |
| `cpf` | CPF (VO) | CPF único (Value Object) |
| `senhaHash` | String | Senha criptografada |
| `status` | StatusUsuario | Estado do usuário |
| `aprovadoPor` | Long | ID do usuário que aprovou |
| `dataAprovacao` | LocalDateTime | Quando foi aprovado |
| `dataCriacao` | LocalDateTime | Data de criação |
| `dataAtualizacao` | LocalDateTime | Data de última atualização |
| `usuarioPermissoes` | Set<UsuarioPermissao> | Permissões associadas |

### **StatusUsuario** (Enum)
```java
PENDENTE_APROVACAO   // Aguardando aprovação
ATIVO                // Usuário ativo
INATIVO              // Usuário inativo temporariamente
BLOQUEADO            // Usuário bloqueado (suspeita de segurança)
DELETADO             // Usuário deletado (soft delete)
```

### **UsuarioPermissao**
Associação entre usuário e permissões.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | Identificador único |
| `usuario` | Usuario | Referência ao usuário |
| `permissao` | Role/Permission | Permissão atribuída |
| `ativa` | Boolean | Se está ativa |
| `dataCriacao` | LocalDateTime | Quando foi atribuída |

### **UsuarioPermissions** (Enum)
```java
USUARIO_CRIAR           // Criar novos usuários
USUARIO_VISUALIZAR      // Visualizar usuários
USUARIO_ATUALIZAR       // Atualizar dados de usuário
USUARIO_DELETAR         // Deletar usuário
USUARIO_BLOQUEAR        // Bloquear usuário
USUARIO_DESBLOQUEAR     // Desbloquear usuário
USUARIO_APROVAR         // Aprovar usuário pendente
USUARIO_PERMISSOES      // Gerenciar permissões
```

## 💾 Value Objects

### **Email**
Encapsula validação de email e garante imutabilidade.

```java
public class Email {
    private final String valor;
    
    public Email(String valor) {
        if (!isValido(valor)) {
            throw new BusinessException("Email inválido");
        }
        this.valor = valor;
    }
    
    public String getValor() { return valor; }
}
```

### **CPF**
Encapsula validação de CPF e garante imutabilidade.

```java
public class CPF {
    private final String valor;
    
    public CPF(String valor) {
        String limpo = valor.replaceAll("[^0-9]", "");
        if (!isValido(limpo)) {
            throw new BusinessException("CPF inválido");
        }
        this.valor = limpo;
    }
    
    public String getValor() { return valor; }
    public String getFormatado() { 
        return valor.substring(0,3) + "." + 
               valor.substring(3,6) + "." + 
               valor.substring(6,9) + "-" + 
               valor.substring(9);
    }
}
```

## 🏗️ Estrutura de Diretórios

```
features/usuario/
├── domain/
│   ├── entity/
│   │   ├── Usuario.java
│   │   ├── StatusUsuario.java
│   │   ├── UsuarioPermissao.java
│   │   └── UsuarioPermissions.java
│   │
│   ├── service/
│   │   ├── UsuarioService.java
│   │   ├── UsuarioValidator.java
│   │   └── PasswordService.java
│   │
│   └── repository/
│       ├── UsuarioRepository.java
│       └── UsuarioPermissaoRepository.java
│
├── application/
│   ├── service/
│   │   ├── CriarUsuarioService.java
│   │   ├── AtualizarUsuarioService.java
│   │   ├── AprovarUsuarioService.java
│   │   ├── AutenticacaoService.java
│   │   └── UsuarioServiceImpl.java
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CriarUsuarioRequest.java
│   │   │   ├── AtualizarUsuarioRequest.java
│   │   │   ├── AlterarSenhaRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   └── AprovarUsuarioRequest.java
│   │   │
│   │   └── response/
│   │       ├── UsuarioResponse.java
│   │       ├── UsuarioDetailResponse.java
│   │       ├── LoginResponse.java
│   │       └── MeuPerfilResponse.java
│   │
│   └── validator/
│       ├── UsuarioValidator.java
│       ├── CriarUsuarioValidator.java
│       └── SenhaValidator.java
│
├── infrastructure/
│   ├── repository/
│   │   ├── JpaUsuarioRepository.java
│   │   └── JpaUsuarioPermissaoRepository.java
│   │
│   └── security/
│       ├── PasswordEncoder.java (adapter)
│       └── JwtTokenProvider.java
│
└── presentation/
    ├── controller/
    │   ├── UsuarioController.java
    │   └── AutenticacaoController.java
    │
    └── response/
        └── UsuarioResponseFormatter.java
```

## 🔄 Fluxos Principais

### 1️⃣ Criar Usuário
```
1. Controller recebe CriarUsuarioRequest
2. Valida dados (email, CPF, senha força)
3. Verifica se email/CPF já existem
4. Cria entidade Usuario com Status = PENDENTE_APROVACAO
5. Hash a senha
6. Persiste no banco
7. Retorna UsuarioResponse (sem senha)
8. (Opcional) Envia email de aprovação
```

### 2️⃣ Autenticação (Login)
```
1. Controller recebe LoginRequest (email + senha)
2. Busca usuário por email
3. Verifica se existe e está ativo
4. Compara senha fornecida com hash armazenado
5. Se válido, gera JWT token
6. Retorna LoginResponse (token + userData)
7. Registra login no audit
```

### 3️⃣ Aprovar Usuário
```
1. Controller recebe AprovarUsuarioRequest
2. Verifica permissão USUARIO_APROVAR do aprovador
3. Busca usuário em PENDENTE_APROVACAO
4. Atualiza status para ATIVO
5. Registra quem aprovou e quando
6. (Opcional) Envia email de aprovação
7. Retorna sucesso
```

### 4️⃣ Atribuir Permissão
```
1. Controller recebe PermissãoRequest
2. Verifica permissão USUARIO_PERMISSOES do operador
3. Busca usuário
4. Cria UsuarioPermissao
5. Persiste associação
6. Verifica se precisa reautenticar
7. Retorna sucesso
```

## 📡 Endpoints da API

### Básico
```
GET    /api/v1/usuarios              # Listar usuários
GET    /api/v1/usuarios/{id}         # Obter por ID
POST   /api/v1/usuarios              # Criar novo
PUT    /api/v1/usuarios/{id}         # Atualizar
DELETE /api/v1/usuarios/{id}         # Deletar
```

### Autenticação
```
POST   /api/v1/auth/login            # Login
POST   /api/v1/auth/logout           # Logout
POST   /api/v1/auth/refresh          # Refresh token
GET    /api/v1/auth/me               # Perfil do usuário logado
```

### Gerenciamento
```
GET    /api/v1/usuarios/pendentes     # Listar pendentes de aprovação
PATCH  /api/v1/usuarios/{id}/aprovar  # Aprovar usuário
PATCH  /api/v1/usuarios/{id}/bloquear # Bloquear usuário
POST   /api/v1/usuarios/{id}/permissoes    # Atribuir permissão
GET    /api/v1/usuarios/{id}/permissoes    # Listar permissões
DELETE /api/v1/usuarios/{id}/permissoes/{perm} # Remover permissão
```

### Senha
```
POST   /api/v1/usuarios/{id}/alterar-senha  # Alterar senha
POST   /api/v1/auth/reset-senha             # Resetar senha (forgot)
```

## ✅ Validações

### Ao Criar
- ✓ Nome não pode estar em branco
- ✓ Email deve ser válido e único
- ✓ CPF deve ser válido (dígitos verificadores) e único
- ✓ Senha deve ter mínimo 8 caracteres
- ✓ Senha deve ter maiúscula, minúscula, número, caractere especial

### Ao Autenticar
- ✓ Email deve existir no sistema
- ✓ Usuário não pode estar bloqueado
- ✓ Usuário deve estar ativo (ou pendente de aprovação com permissão)
- ✓ Senha deve estar correta

### Ao Aprovar
- ✓ Usuário deve estar em PENDENTE_APROVACAO
- ✓ Aprovador deve ter permissão USUARIO_APROVAR
- ✓ Aprovador não pode ser o próprio usuário

## 🔐 Segurança

### Hashing de Senha
- **Algoritmo**: BCrypt (Spring Security default)
- **Força**: 10+ (configurável)
- **Never**: Armazenar senha em plain text

### JWT Token
- **Header**: Authorization: Bearer {token}
- **TTL**: Configurável (ex: 1 hora)
- **Refresh**: Token de refresh com TTL maior

### Checklist de Segurança
- ✓ Senhas com hash forte (BCrypt)
- ✓ HTTPS obrigatório em produção
- ✓ Rate limiting em endpoints de autenticação
- ✓ Log de falhas de autenticação
- ✓ Bloqueio após N tentativas falhadas
- ✓ Emails confirmados antes de ativar

## 🧪 Testes

```java
UsuarioEntityTest.java              // Testes da entidade
UsuarioServiceTest.java             // Testes de domínio
CriarUsuarioServiceTest.java        // Testes de caso de uso
AutenticacaoServiceTest.java        // Testes de autenticação
UsuarioControllerTest.java          // Testes de API
```

## 🔗 Relacionamentos

```
Usuario
    ├── StatusUsuario (enum)
    ├── Email (value object)
    ├── CPF (value object)
    ├── UsuarioPermissao (um-para-muitos)
    └── UsuarioPermissions (enum de permissões)
```

## 📚 Detalhes de Implementação

### CPF Validator
```java
public class CPFValidator {
    public static boolean isValido(String cpf) {
        String limpo = cpf.replaceAll("[^0-9]", "");
        if (limpo.length() != 11) return false;
        if (limpo.equals(limpo.charAt(0) + "".repeat(11))) return false;
        
        // Validação dos dígitos verificadores
        // ... implementação
        
        return true;
    }
}
```

### Email Validator
```java
public class EmailValidator {
    private static final String REGEX = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    public static boolean isValido(String email) {
        return Pattern.matches(REGEX, email);
    }
}
```

## 🚀 Boas Práticas

1. **Nunca retornar senha** na resposta
2. **Usar JWT** para stateless authentication
3. **Rate limiting** em login (máx 5 tentativas/min)
4. **Audit trail** de todas as ações críticas
5. **MFA** opcional para usuários admin
6. **Senha temporary** na criação, obriga trocar no primeiro login
7. **Validação de email** com link de confirmação
8. **Session timeout** configurável

## 📚 Referências Relacionadas

- [FEATURE_PERMISSAO.md](FEATURE_PERMISSAO.md) - Sistema de permissões
- [SEGURANCA.md](SEGURANCA.md) - Detalhes de segurança
- [CAMADA_DOMAIN.md](CAMADA_DOMAIN.md) - Value Objects
- [SHARED_DOMAIN.md](SHARED_DOMAIN.md) - Componentes compartilhados

---

**Última atualização:** Dezembro de 2025
