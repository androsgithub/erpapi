# 🔐 Ajustes de Autorização - Gerenciamento de Usuários

## 📝 Resumo das Mudanças

Este documento descreve os ajustes implementados para **restringir o gerenciamento de usuários apenas a GESTORES e ADMIN**, com suporte para adicionar/remover permissões e roles.

---

## ✨ O que foi implementado no Frontend

### 1. **Tipos TypeScript Expandidos** (`domain/types.ts`)
- ✅ `Permissao` - Interface para permissões
- ✅ `Role` - Interface para roles
- ✅ `AdicionarPermissoesInput` - DTO para adicionar permissões
- ✅ `AdicionarRolesInput` - DTO para adicionar roles
- ✅ `RemoverPermissaoInput` - DTO para remover permissão
- ✅ `RemoverRoleInput` - DTO para remover role

### 2. **Autorização e Permissões** (`domain/authorization.ts`)
- ✅ Constantes de permissões do usuário
- ✅ Função `canManageUsuarios()` - Verifica se pode gerenciar usuários
- ✅ Função `canManagePermissoes()` - Verifica se pode gerenciar permissões
- ✅ Função `canManageRoles()` - Verifica se pode gerenciar roles
- ✅ Função `canApproveUsuarios()` - Verifica se pode aprovar/rejeitar

### 3. **API Service Atualizado** (`infrastructure/usuariosApiService.ts`)
- ✅ `adicionarPermissoesAoUsuario()` - Adiciona permissões a usuário
- ✅ `removerPermissaoDoUsuario()` - Remove permissão de usuário
- ✅ `getPermissoesUsuario()` - Lista permissões de usuário
- ✅ `adicionarRolesAoUsuario()` - Adiciona roles a usuário
- ✅ `removerRoleDoUsuario()` - Remove role de usuário
- ✅ `getRolesUsuario()` - Lista roles de usuário
- ✅ `aprovarUsuario()` - Aprova usuário pendente
- ✅ `rejeitarUsuario()` - Rejeita usuário pendente

### 4. **Hook Personalizado** (`application/hooks/useUsuariosManagement.ts`)
- ✅ Centraliza toda a lógica de gerenciamento de usuários
- ✅ Verifica permissões automaticamente
- ✅ Lança exceções descritivas quando não autorizado
- ✅ Facilita o uso em componentes React

### 5. **Exportações Atualizadas** (`index.ts`)
- ✅ Novos tipos exportados
- ✅ Funções de autorização exportadas
- ✅ Hook `useUsuariosManagement` exportado

---

## 🔐 Matriz de Controle de Acesso

| Operação | Admin | Gestor | Usuário | Não Autenticado |
|----------|:-----:|:------:|:-------:|:---------------:|
| Listar Usuários | ✓ | ✓ | ✗ | ✗ |
| Buscar Usuário | ✓ | ✓ | ✗ | ✗ |
| Criar Usuário | ✓ | ✓ | ✗ | ✗ |
| Atualizar Usuário | ✓ | ✓ | ✗ | ✗ |
| Inativar Usuário | ✓ | ✓ | ✗ | ✗ |
| Aprovar Usuário | ✓ | ✓ | ✗ | ✗ |
| Rejeitar Usuário | ✓ | ✓ | ✗ | ✗ |
| **Adicionar Permissões** | **✓** | **✓** | **✗** | **✗** |
| **Remover Permissões** | **✓** | **✓** | **✗** | **✗** |
| **Adicionar Roles** | **✓** | **✓** | **✗** | **✗** |
| **Remover Roles** | **✓** | **✓** | **✗** | **✗** |

---

## 💻 Exemplos de Uso

### Usando o Hook `useUsuariosManagement`

```typescript
import { useUsuariosManagement } from '@/features/usuarios';

function MeuComponente() {
  const {
    // Leitura (sempre permitido)
    listarUsuarios,
    buscarUsuario,
    listarPermissoes,
    listarRoles,

    // Escrita (restrito)
    criarUsuario,
    atualizarUsuario,
    inativarUsuario,

    // Aprovação (restrito)
    aprovarUsuarioFn,
    rejeitarUsuarioFn,

    // Permissões (restrito)
    adicionarPermissoes,
    removerPermissao,

    // Roles (restrito)
    adicionarRoles,
    removerRole,

    // Verificação
    podeGerenciarUsuarios,
    podeGerenciarPermissoes,
    podeGerenciarRoles,
    podeAprovarUsuarios,
  } = useUsuariosManagement();

  // Adicionar permissões (com verificação automática)
  const handleAdicionarPermissoes = async () => {
    try {
      await adicionarPermissoes({
        usuarioId: '123',
        permissaoIds: [1, 2, 3],
        dataInicio: '2025-01-01T00:00:00',
        dataFim: '2025-12-31T23:59:59',
      });
      console.log('Permissões adicionadas!');
    } catch (error) {
      console.error('Erro:', error.message);
      // Mensagem será algo como:
      // "Sem permissão para adicionar permissões. Apenas ADMIN ou GESTOR pode realizar esta ação."
    }
  };

  // Remover role (com verificação automática)
  const handleRemoverRole = async () => {
    try {
      await removerRole({
        usuarioId: '123',
        roleId: 2,
      });
      console.log('Role removida!');
    } catch (error) {
      console.error('Erro:', error.message);
    }
  };

  // Mostrar/ocultar botões baseado em permissão
  return (
    <div>
      {podeGerenciarPermissoes && (
        <button onClick={handleAdicionarPermissoes}>
          Adicionar Permissões
        </button>
      )}

      {podeGerenciarRoles && (
        <button onClick={handleRemoverRole}>
          Remover Role
        </button>
      )}
    </div>
  );
}
```

### Usando Funções Diretas de Autorização

```typescript
import { canManageUsuarios, canManagePermissoes } from '@/features/usuarios';

function verificarAcesso() {
  const userRoles = ['GESTOR']; // Obtido do contexto de autenticação

  if (canManageUsuarios(userRoles)) {
    console.log('Usuário pode gerenciar usuários');
  }

  if (canManagePermissoes(userRoles)) {
    console.log('Usuário pode gerenciar permissões');
  }
}
```

### Usando API Service Diretamente

```typescript
import {
  adicionarPermissoesAoUsuario,
  removerPermissaoDoUsuario,
  adicionarRolesAoUsuario,
  removerRoleDoUsuario,
} from '@/features/usuarios';

// Adicionar múltiplas permissões
await adicionarPermissoesAoUsuario({
  usuarioId: '123',
  permissaoIds: [1, 2, 3],
  dataInicio: '2025-01-01T00:00:00',
  dataFim: '2025-12-31T23:59:59',
});

// Remover uma permissão
await removerPermissaoDoUsuario({
  usuarioId: '123',
  permissaoId: 1,
});

// Adicionar roles
await adicionarRolesAoUsuario({
  usuarioId: '123',
  roleIds: [1, 2],
});

// Remover role
await removerRoleDoUsuario({
  usuarioId: '123',
  roleId: 1,
});
```

---

## 📋 O que Precisa ser Implementado no Backend

> **IMPORTANTE**: Os endpoints do frontend já estão prontos, mas o backend Java precisa ser atualizado para suportar essas operações.

### Permissões Novas em `UsuarioPermissions.java`
```java
public static final String GERENCIAR_PERMISSOES = PREFIX + ".gerenciar.permissoes";
public static final String ADICIONAR_PERMISSAO = PREFIX + ".adicionar.permissao";
public static final String REMOVER_PERMISSAO = PREFIX + ".remover.permissao";

public static final String GERENCIAR_ROLES = PREFIX + ".gerenciar.roles";
public static final String ADICIONAR_ROLE = PREFIX + ".adicionar.role";
public static final String REMOVER_ROLE = PREFIX + ".remover.role";
```

### Novos Endpoints da API

| Método | Endpoint | Descrição | Autorização |
|--------|----------|-----------|-------------|
| POST | `/api/v1/usuarios/{id}/permissoes` | Adicionar permissões | ADMIN, GESTOR |
| DELETE | `/api/v1/usuarios/{id}/permissoes/{permissaoId}` | Remover permissão | ADMIN, GESTOR |
| GET | `/api/v1/usuarios/{id}/permissoes` | Listar permissões | Autenticado |
| POST | `/api/v1/usuarios/{id}/roles` | Adicionar roles | ADMIN, GESTOR |
| DELETE | `/api/v1/usuarios/{id}/roles/{roleId}` | Remover role | ADMIN, GESTOR |
| GET | `/api/v1/usuarios/{id}/roles` | Listar roles | Autenticado |

**Veja**: [AJUSTES_AUTORIZACAO_USUARIOS.md](./AJUSTES_AUTORIZACAO_USUARIOS.md) para detalhes completos de implementação do backend.

---

## 🎯 Próximos Passos

### Backend (Java)
1. [ ] Atualizar `UsuarioPermissions.java` com novas permissões
2. [ ] Criar DTOs: `AdicionarPermissoesRequest`, `AdicionarRolesRequest`
3. [ ] Implementar novos endpoints no `UsuarioController`
4. [ ] Criar métodos na interface `IUsuarioService`
5. [ ] Implementar lógica em `UsuarioServiceImpl`
6. [ ] Atualizar repositórios com métodos específicos
7. [ ] Adicionar novas permissões ao seed de permissões
8. [ ] Testes unitários e de integração

### Frontend (React/TypeScript)
1. [ ] Criar componente de gerenciamento de permissões
2. [ ] Criar componente de gerenciamento de roles
3. [ ] Integrar com UI de usuários existente
4. [ ] Adicionar testes
5. [ ] Documentação de componentes

### Documentação
1. [ ] Atualizar API Swagger/OpenAPI
2. [ ] Criar guias de uso
3. [ ] Exemplos de integração

---

## 📚 Documentação Relacionada

- [AJUSTES_AUTORIZACAO_USUARIOS.md](./AJUSTES_AUTORIZACAO_USUARIOS.md) - Implementação no backend
- [FEATURE_USUARIO.md](./FEATURE_USUARIO.md) - Feature de usuários
- [FEATURE_PERMISSAO.md](./FEATURE_PERMISSAO.md) - Feature de permissões
- [AUTORIZACAO_ACESSO.md](./AUTORIZACAO_ACESSO.md) - Sistema de autorização
- [SEGURANCA.md](./SEGURANCA.md) - Estratégias de segurança

---

## 🔍 Verificação de Implementação

### Checklist Frontend ✅
- [x] Tipos TypeScript criados
- [x] Autorização implementada
- [x] API Service atualizado
- [x] Hook personalizado criado
- [x] Exportações atualizadas
- [x] Exemplos de uso documentados

### Checklist Backend ⏳
- [ ] Permissões criadas
- [ ] DTOs criados
- [ ] Controller atualizado
- [ ] Service atualizado
- [ ] Repositórios atualizados
- [ ] Permissões no Seed
- [ ] Testes adicionados
- [ ] Swagger atualizado

---

**Data de Criação**: 31/12/2025  
**Status**: 50% Completo (Frontend pronto, Backend pendente)  
**Responsável**: Equipe Full Stack
