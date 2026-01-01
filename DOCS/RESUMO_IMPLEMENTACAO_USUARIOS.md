# 📋 Resumo de Implementação - Ajustes de Autorização para Usuários

## 🎯 Objetivo
Restringir o gerenciamento de usuários apenas a **ADMIN** e **GESTOR**, com suporte para adicionar/remover permissões e roles.

## ✅ O que foi Implementado (Frontend)

### 1. Tipos TypeScript (`domain/types.ts`)
- **Permissao** - Interface para representar permissões
- **Role** - Interface para representar roles
- **AdicionarPermissoesInput** - DTO para adicionar permissões
- **AdicionarRolesInput** - DTO para adicionar roles
- **RemoverPermissaoInput** - DTO para remover permissão
- **RemoverRoleInput** - DTO para remover role

**Mudanças**:
```diff
- interface UsuarioListItem
+ interface UsuarioListItem {
+   permissoes?: Permissao[];
+   roles?: Role[];
+ }
```

### 2. Autorização (`domain/authorization.ts`) - ✨ NOVO
- Constantes `USUARIO_PERMISSIONS`
- Lista `AUTHORIZED_ROLES_FOR_USUARIO_MANAGEMENT`
- Funções de verificação de autorização:
  - `canManageUsuarios(roles)`
  - `canManagePermissoes(roles)`
  - `canManageRoles(roles)`
  - `canApproveUsuarios(roles)`

### 3. API Service (`infrastructure/usuariosApiService.ts`)
**Novos Endpoints**:
- `adicionarPermissoesAoUsuario()` - POST
- `removerPermissaoDoUsuario()` - DELETE
- `getPermissoesUsuario()` - GET
- `adicionarRolesAoUsuario()` - POST
- `removerRoleDoUsuario()` - DELETE
- `getRolesUsuario()` - GET
- `aprovarUsuario()` - PATCH
- `rejeitarUsuario()` - PATCH

**Estrutura**:
```
POST   /api/v1/usuarios/{id}/permissoes
DELETE /api/v1/usuarios/{id}/permissoes/{permissaoId}
GET    /api/v1/usuarios/{id}/permissoes

POST   /api/v1/usuarios/{id}/roles
DELETE /api/v1/usuarios/{id}/roles/{roleId}
GET    /api/v1/usuarios/{id}/roles

PATCH  /api/v1/usuarios/{id}/aprovar
PATCH  /api/v1/usuarios/{id}/rejeitar
```

### 4. Hook Personalizado (`application/hooks/useUsuariosManagement.ts`) - ✨ NOVO
Centraliza toda a lógica com:
- Verificação de autorização automática
- Erros descritivos
- Interface type-safe
- Métodos agrupados por categoria

**Categorias**:
- Leitura (sempre permitido)
- Escrita (ADMIN/GESTOR)
- Aprovação (ADMIN/GESTOR)
- Permissões (ADMIN/GESTOR)
- Roles (ADMIN/GESTOR)

### 5. Exportações (`index.ts`)
```typescript
export { useUsuariosManagement }
export type { 
  Permissao, Role, 
  AdicionarPermissoesInput, AdicionarRolesInput,
  RemoverPermissaoInput, RemoverRoleInput 
}
export { 
  canManageUsuarios, canManagePermissoes, 
  canManageRoles, canApproveUsuarios 
}
```

---

## ⏳ O que Precisa ser Implementado (Backend)

### 1. Permissões (`UsuarioPermissions.java`)
```java
// Adicionar constantes
public static final String GERENCIAR_PERMISSOES = PREFIX + ".gerenciar.permissoes";
public static final String ADICIONAR_PERMISSAO = PREFIX + ".adicionar.permissao";
public static final String REMOVER_PERMISSAO = PREFIX + ".remover.permissao";

public static final String GERENCIAR_ROLES = PREFIX + ".gerenciar.roles";
public static final String ADICIONAR_ROLE = PREFIX + ".adicionar.role";
public static final String REMOVER_ROLE = PREFIX + ".remover.role";
```

### 2. DTOs
- `AdicionarPermissoesRequest`
- `AdicionarRolesRequest`

### 3. Controller (`UsuarioController.java`)
6 novos endpoints com decoradores `@RequiresPermission`

### 4. Service Interface (`IUsuarioService`)
6 novos métodos:
- `adicionarPermissoes()`
- `removerPermissao()`
- `listarPermissoes()`
- `adicionarRoles()`
- `removerRole()`
- `listarRoles()`

### 5. Service Implementation (`UsuarioServiceImpl`)
Implementação dos 6 métodos com validações

### 6. Repositórios
- `UsuarioPermissaoRepository` - métodos de busca/exclusão
- `UsuarioRoleRepository` - métodos de busca/exclusão

### 7. Seed de Permissões
Adicionar novas permissões ao `PermissaoSeed.java`

---

## 📊 Matriz de Autorizações

| Operação | Permissão | Admin | Gestor | Usuário | Público |
|----------|-----------|:-----:|:------:|:-------:|:-------:|
| Listar Usuários | usuario.listar | ✓ | ✓ | ✗ | ✗ |
| Criar Usuário | usuario.criar | ✓ | ✓ | ✗ | ✗ |
| Atualizar Usuário | usuario.atualizar | ✓ | ✓ | ✗ | ✗ |
| Deletar Usuário | usuario.deletar | ✓ | ✓ | ✗ | ✗ |
| Aprovar Usuário | usuario.aprovar | ✓ | ✓ | ✗ | ✗ |
| Rejeitar Usuário | usuario.rejeitar | ✓ | ✓ | ✗ | ✗ |
| Adicionar Permissão | **usuario.gerenciar.permissoes** | **✓** | **✓** | **✗** | **✗** |
| Remover Permissão | **usuario.remover.permissao** | **✓** | **✓** | **✗** | **✗** |
| Adicionar Role | **usuario.gerenciar.roles** | **✓** | **✓** | **✗** | **✗** |
| Remover Role | **usuario.remover.role** | **✓** | **✓** | **✗** | **✗** |

---

## 📁 Estrutura de Arquivos

```
erpwebapp/src/features/usuarios/
├── domain/
│   ├── types.ts                          [MODIFICADO]
│   └── authorization.ts                  [NOVO]
├── infrastructure/
│   └── usuariosApiService.ts             [MODIFICADO]
├── application/
│   └── hooks/
│       └── useUsuariosManagement.ts      [NOVO]
└── index.ts                              [MODIFICADO]

DOCS/
├── 00_INDICE.md                          [MODIFICADO]
├── AJUSTES_AUTORIZACAO_USUARIOS.md       [NOVO]
├── README_AJUSTES_USUARIOS.md            [NOVO]
└── GUIA_RAPIDO_AJUSTES_USUARIOS.md       [NOVO]
```

---

## 🔄 Fluxo de Autorização

```
┌─────────────────┐
│  User Action    │
└────────┬────────┘
         │
         ▼
┌──────────────────────────┐
│ Check User Roles         │
│ - ADMIN?                 │
│ - GESTOR?                │
└────────┬─────────────────┘
         │
    ┌────┴─────┐
    │           │
    ▼           ▼
  [YES]       [NO]
    │           │
    ▼           ▼
┌────────┐  ┌──────────────────┐
│ Allow  │  │ Throw Error:     │
│ Action │  │ "Unauthorized... │
└────────┘  │  Only ADMIN or   │
            │  GESTOR..."      │
            └──────────────────┘
```

---

## 💡 Exemplo de Uso Prático

```typescript
// 1. Importar
import { useUsuariosManagement } from '@/features/usuarios';

// 2. Usar no componente
function UsuariosManagement() {
  const {
    adicionarPermissoes,
    removerRole,
    podeGerenciarPermissoes,
  } = useUsuariosManagement();

  // 3. Verificar permissão
  if (!podeGerenciarPermissoes) {
    return <div>Sem permissão</div>;
  }

  // 4. Usar função
  const handleAdd = async () => {
    try {
      await adicionarPermissoes({
        usuarioId: '123',
        permissaoIds: [1, 2, 3],
      });
    } catch (error) {
      alert(error.message);
    }
  };

  return <button onClick={handleAdd}>Adicionar</button>;
}
```

---

## ✅ Checklist de Implementação

### Frontend (Completo ✓)
- [x] Tipos criados
- [x] Autorização implementada
- [x] API Service atualizado
- [x] Hook criado
- [x] Exportações atualizadas
- [x] Documentação criada

### Backend (Pendente ⏳)
- [ ] Permissões em UsuarioPermissions.java
- [ ] DTOs criados
- [ ] Controller atualizado
- [ ] Service atualizado
- [ ] Repositórios atualizados
- [ ] Seed atualizado
- [ ] Testes adicionados
- [ ] Swagger atualizado

---

## 🚀 Próximas Ações

1. **Backend**: Seguir documentação em `AJUSTES_AUTORIZACAO_USUARIOS.md`
2. **Testes**: Criar testes unitários e de integração
3. **UI**: Implementar componentes de gerenciamento
4. **Validação**: Testar fluxos completos
5. **Deploy**: Atualizar e fazer release

---

## 📚 Documentação de Referência

- [AJUSTES_AUTORIZACAO_USUARIOS.md](./AJUSTES_AUTORIZACAO_USUARIOS.md) - Implementação completa
- [README_AJUSTES_USUARIOS.md](./README_AJUSTES_USUARIOS.md) - Guia detalhado
- [GUIA_RAPIDO_AJUSTES_USUARIOS.md](./GUIA_RAPIDO_AJUSTES_USUARIOS.md) - Guia rápido
- [FEATURE_USUARIO.md](./FEATURE_USUARIO.md) - Feature de usuários
- [FEATURE_PERMISSAO.md](./FEATURE_PERMISSAO.md) - Feature de permissões

---

## 👥 Responsáveis

- **Frontend**: ✅ Implementado
- **Backend**: ⏳ Pendente
- **Testes**: ⏳ Pendente
- **Documentação**: ✅ Completa

---

**Data**: 31/12/2025  
**Status**: 50% Concluído (Frontend pronto, Backend pendente)  
**Versão**: 1.0.0
