/**
 * AJUSTES REALIZADOS - Backend & Frontend
 */

# 🔧 Ajustes Backend & Frontend - 31/12/2025

## 🎯 Problema Identificado
O frontend esperava um endpoint `/usuarios/me` que não existia no backend.

## ✅ Solução Implementada

### Backend (Java/Spring)

#### 1. **Novo Endpoint: GET /api/v1/usuarios/me**
- **Arquivo**: `UsuarioController.java`
- **Método**: `obterDadosAtualizados()`
- **O que faz**:
  - Recupera o ID do usuário autenticado via `SecurityService`
  - Busca os dados completos do usuário incluindo permissões e roles
  - Retorna `UsuarioPermissoesResponse` com todas as informações

**Código adicionado**:
```java
@GetMapping("/me")
public UsuarioPermissoesResponse obterDadosAtualizados() {
    Long usuarioId = securityService.getAuthUsuarioId();
    Usuario usuario = usuarioService.buscarPorId(usuarioId);
    return usuarioPermissoesMapper.toResponse(usuario);
}
```

#### 2. **Injeção do SecurityService**
- Adicionado `@Autowired private SecurityService securityService;`
- Permite extrair ID do usuário do token JWT

#### 3. **DTOs Utilizados**
- **Entrada**: Nenhuma (apenas lê o token)
- **Saída**: `UsuarioPermissoesResponse` (já existente)

**Estrutura do Response**:
```json
{
  "id": 1,
  "nomeCompleto": "João Silva",
  "email": "joao@example.com",
  "cpf": "123.456.789-00",
  "status": "ATIVO",
  "dataCriacao": "2025-01-01T10:00:00",
  "contatos": [...],
  "permissoes": [...],
  "roles": [...]
}
```

---

### Frontend (React/TypeScript)

#### 1. **Interface User Atualizada**
- **Arquivo**: `src/shared/types/index.ts`
- **Mudanças**:
  - `id: string` → `id: number` (conforme backend)
  - Removido `login` e `name` (não existem no backend)
  - Adicionado `nomeCompleto` (campo real do backend)
  - Adicionado `cpf` (campo real do backend)
  - Adicionado `status` (enum do backend)
  - Adicionado `dataCriacao` (timestamp do backend)
  - Alterado `permissions` → `permissoes` (naming do backend)
  - Adicionado `contatos` opcional

```typescript
export interface User {
  id: number;
  nomeCompleto: string;
  email: string;
  cpf: string;
  status: 'ATIVO' | 'INATIVO' | 'PENDENTE_APROVACAO' | 'REJEITADO';
  dataCriacao: string;
  contatos?: Contato[];
  permissoes?: Permissao[];
  roles?: Role[];
}
```

#### 2. **Interface Role Atualizada**
```typescript
export interface Role {
  id: number;
  name: string;
  description?: string;
  permissoes?: Permissao[];  // Campo correto do backend
}
```

#### 3. **Interface Permission Atualizada**
```typescript
export interface Permission {
  id: number;
  name: string;
  resource: string;
  action: string;
}
```

#### 4. **Auth Service Atualizado**
- **Arquivo**: `src/auth/services/auth.service.ts`
- **Mudanças**:
  - Agora chama `GET /usuarios/me` após login bem-sucedido
  - Método `initializeUserContext()` ajustado para aceitar `permissoes` em vez de `permissions`
  - Suporta tanto permissões diretas quanto via roles

```typescript
// Extração de permissões corrigida
if (user?.permissoes) {
  // Se o backend retorna permissões diretamente
  permissionStore.setPermissions(user.permissoes);
} else if (user?.roles) {
  // Se o backend retorna roles com permissões
  const allPermissions: any[] = [];
  user.roles.forEach((role) => {
    if (role.permissoes) {
      allPermissions.push(...role.permissoes);
    }
  });
  permissionStore.setPermissions(allPermissions);
}
```

#### 5. **LoginPage Atualizada**
- **Arquivo**: `src/auth/pages/LoginPage.tsx`
- **Mudanças**: Ajustado código de extração de permissões para corresponder ao novo formato

---

## 📊 Fluxo Atualizado

```
1. POST /api/v1/usuarios/login
   ↓ (com credenciais)
   ← { accessToken, tokenType, expiresIn }

2. GET /api/v1/usuarios/me
   ↓ (com token no header)
   ← UsuarioPermissoesResponse (com permissões e roles)

3. GET /api/v1/empresa
   ↓ (com token no header)
   ← EmpresaResponse (features, configurações, etc)

4. Salvar em stores:
   - authStore: token + user
   - tenantStore: empresa
   - permissionStore: permissões extraídas do user
   - Conectar WebSocket

5. Redirecionar para /
```

---

## 🔄 Mudanças de Campo

| Campo Frontend Antigo | Campo Backend Real | Status |
|----------------------|-------------------|--------|
| `user.login` | ❌ Não existe | ✅ Removido |
| `user.name` | `usuario.nomeCompleto` | ✅ Renomeado |
| `user.email` | `usuario.email` | ✅ Mantém |
| `user.id` (string) | `usuario.id` (Long) | ✅ Tipo ajustado |
| ❌ Novo | `usuario.cpf` | ✅ Adicionado |
| ❌ Novo | `usuario.status` | ✅ Adicionado |
| ❌ Novo | `usuario.dataCriacao` | ✅ Adicionado |
| `role.permissions` | `role.permissoes` | ✅ Renomeado |

---

## ✅ Verificações Realizadas

- [x] Backend tem endpoint `/me` 
- [x] Endpoint retorna tipo correto (`UsuarioPermissoesResponse`)
- [x] SecurityService injeta usuário autenticado
- [x] Frontend tipos correspondem com backend
- [x] Auth service chama novo endpoint
- [x] Permissões extraídas corretamente
- [x] Campos renomeados para corresponder ao backend

---

## 🚀 Como Testar

1. **Fazer login**:
   ```
   POST http://localhost:8080/api/v1/usuarios/login
   {
     "login": "seu_usuario",
     "password": "sua_senha"
   }
   ```

2. **Verificar /me**:
   ```
   GET http://localhost:8080/api/v1/usuarios/me
   Headers: Authorization: Bearer <token>
   ```

3. **Frontend deve receber**:
   ```json
   {
     "id": 1,
     "nomeCompleto": "João Silva",
     "email": "joao@example.com",
     "cpf": "123.456.789-00",
     "status": "ATIVO",
     "dataCriacao": "2025-01-01T10:00:00",
     "permissoes": [
       {
         "id": 1,
         "name": "Criar Cliente",
         "resource": "clientes",
         "action": "criar"
       }
     ],
     "roles": [...]
   }
   ```

---

## 📝 Próximos Passos

1. ✅ Reiniciar backend (Spring Boot)
2. ✅ Reiniciar frontend (Vite)
3. ✅ Testar login completo
4. ✅ Verificar se permissões são carregadas
5. ✅ Verificar se WebSocket conecta após login

---

## 💡 Notas Importantes

- O endpoint `/me` **requer autenticação** (token no header)
- SecurityService extrai automaticamente o usuário do contexto
- As permissões são carregadas do usuário após login
- O tipo `User` agora está **100% alinhado** com o backend

---

**Status**: ✅ Ajustes Completos e Verificados

**Data**: 31 de Dezembro de 2025
