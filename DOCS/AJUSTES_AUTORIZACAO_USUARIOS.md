# 🔐 Ajustes de Autorização - Gerenciamento de Usuários

## Resumo das Mudanças

Esta documentação descreve os ajustes necessários para restringir o gerenciamento de usuários apenas a **GESTORES** e **ADMIN**, com capacidade de adicionar/remover permissões e roles.

---

## 📋 Permissões Novas

Adicione as seguintes permissões à classe `UsuarioPermissions.java`:

```java
public static final String GERENCIAR_PERMISSOES = PREFIX + ".gerenciar.permissoes";
public static final String ADICIONAR_PERMISSAO = PREFIX + ".adicionar.permissao";
public static final String REMOVER_PERMISSAO = PREFIX + ".remover.permissao";

public static final String GERENCIAR_ROLES = PREFIX + ".gerenciar.roles";
public static final String ADICIONAR_ROLE = PREFIX + ".adicionar.role";
public static final String REMOVER_ROLE = PREFIX + ".remover.role";
```

---

## 🔗 Novos Endpoints da API

### Gerenciamento de Permissões de Usuário

#### Adicionar Permissões a Usuário
```
POST /api/v1/usuarios/{usuarioId}/permissoes

Requer: usuario.gerenciar.permissoes (ADMIN ou GESTOR)

Request Body:
{
  "permissaoIds": [1, 2, 3],
  "dataInicio": "2025-01-01T00:00:00",
  "dataFim": "2025-12-31T23:59:59"
}

Response: 204 No Content
```

#### Remover Permissão de Usuário
```
DELETE /api/v1/usuarios/{usuarioId}/permissoes/{permissaoId}

Requer: usuario.remover.permissao (ADMIN ou GESTOR)

Response: 204 No Content
```

#### Listar Permissões de Usuário
```
GET /api/v1/usuarios/{usuarioId}/permissoes

Requer: Autenticado

Response:
[
  {
    "id": 1,
    "codigo": "usuario.criar",
    "nome": "Criar Usuário",
    "modulo": "usuario",
    "acao": "CRIAR",
    "ativo": true
  }
]
```

### Gerenciamento de Roles de Usuário

#### Adicionar Roles a Usuário
```
POST /api/v1/usuarios/{usuarioId}/roles

Requer: usuario.gerenciar.roles (ADMIN ou GESTOR)

Request Body:
{
  "roleIds": [1, 2],
  "dataInicio": "2025-01-01T00:00:00",
  "dataFim": "2025-12-31T23:59:59"
}

Response: 204 No Content
```

#### Remover Role de Usuário
```
DELETE /api/v1/usuarios/{usuarioId}/roles/{roleId}

Requer: usuario.remover.role (ADMIN ou GESTOR)

Response: 204 No Content
```

#### Listar Roles de Usuário
```
GET /api/v1/usuarios/{usuarioId}/roles

Requer: Autenticado

Response:
[
  {
    "id": 1,
    "nome": "GESTOR",
    "permissoes": [...]
  }
]
```

---

## 🏗️ Implementação no Backend (Java)

### 1. Atualizar UsuarioPermissions

```java
package com.api.erp.v1.features.usuario.domain.entity;

public final class UsuarioPermissions {

    private UsuarioPermissions() {
    }

    public static final String PREFIX = "usuario";
    
    // Permissões existentes
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String LISTAR = PREFIX + ".listar";
    public static final String DELETAR = PREFIX + ".deletar";
    public static final String ATIVAR = PREFIX + ".ativar";
    public static final String DESATIVAR = PREFIX + ".desativar";
    public static final String APROVAR = PREFIX + ".aprovar";
    public static final String REJEITAR = PREFIX + ".rejeitar";
    
    // NOVAS PERMISSÕES
    public static final String GERENCIAR_PERMISSOES = PREFIX + ".gerenciar.permissoes";
    public static final String ADICIONAR_PERMISSAO = PREFIX + ".adicionar.permissao";
    public static final String REMOVER_PERMISSAO = PREFIX + ".remover.permissao";
    
    public static final String GERENCIAR_ROLES = PREFIX + ".gerenciar.roles";
    public static final String ADICIONAR_ROLE = PREFIX + ".adicionar.role";
    public static final String REMOVER_ROLE = PREFIX + ".remover.role";
}
```

### 2. Criar DTOs para Requisições

```java
// AdicionarPermissoesRequest.java
package com.api.erp.v1.features.usuario.application.dto.request;

import java.time.LocalDateTime;
import java.util.Set;

public record AdicionarPermissoesRequest(
    Set<Long> permissaoIds,
    LocalDateTime dataInicio,
    LocalDateTime dataFim
) {}
```

```java
// AdicionarRolesRequest.java
package com.api.erp.v1.features.usuario.application.dto.request;

import java.time.LocalDateTime;
import java.util.Set;

public record AdicionarRolesRequest(
    Set<Long> roleIds,
    LocalDateTime dataInicio,
    LocalDateTime dataFim
) {}
```

### 3. Atualizar UsuarioController

```java
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuários", description = "Gestão de usuários do sistema")
public class UsuarioController {
    // ... código existente ...

    // NOVOS ENDPOINTS PARA GERENCIAMENTO DE PERMISSÕES

    @PostMapping("/{usuarioId}/permissoes")
    @Operation(summary = "Adicionar permissões a usuário", 
               description = "Adiciona uma ou mais permissões a um usuário")
    @RequiresPermission(UsuarioPermissions.GERENCIAR_PERMISSOES)
    public ResponseEntity<Void> adicionarPermissoes(
            @PathVariable Long usuarioId,
            @RequestBody AdicionarPermissoesRequest request) {
        usuarioService.adicionarPermissoes(usuarioId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usuarioId}/permissoes/{permissaoId}")
    @Operation(summary = "Remover permissão de usuário",
               description = "Remove uma permissão específica de um usuário")
    @RequiresPermission(UsuarioPermissions.REMOVER_PERMISSAO)
    public ResponseEntity<Void> removerPermissao(
            @PathVariable Long usuarioId,
            @PathVariable Long permissaoId) {
        usuarioService.removerPermissao(usuarioId, permissaoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{usuarioId}/permissoes")
    @Operation(summary = "Listar permissões de usuário",
               description = "Lista todas as permissões de um usuário")
    public ResponseEntity<List<PermissaoResponse>> listarPermissoes(
            @PathVariable Long usuarioId) {
        List<Permissao> permissoes = usuarioService.listarPermissoes(usuarioId);
        return ResponseEntity.ok(permissaoMapper.toResponseList(permissoes));
    }

    // NOVOS ENDPOINTS PARA GERENCIAMENTO DE ROLES

    @PostMapping("/{usuarioId}/roles")
    @Operation(summary = "Adicionar roles a usuário",
               description = "Adiciona uma ou mais roles a um usuário")
    @RequiresPermission(UsuarioPermissions.GERENCIAR_ROLES)
    public ResponseEntity<Void> adicionarRoles(
            @PathVariable Long usuarioId,
            @RequestBody AdicionarRolesRequest request) {
        usuarioService.adicionarRoles(usuarioId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usuarioId}/roles/{roleId}")
    @Operation(summary = "Remover role de usuário",
               description = "Remove uma role específica de um usuário")
    @RequiresPermission(UsuarioPermissions.REMOVER_ROLE)
    public ResponseEntity<Void> removerRole(
            @PathVariable Long usuarioId,
            @PathVariable Long roleId) {
        usuarioService.removerRole(usuarioId, roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{usuarioId}/roles")
    @Operation(summary = "Listar roles de usuário",
               description = "Lista todas as roles de um usuário")
    public ResponseEntity<List<RoleResponse>> listarRoles(
            @PathVariable Long usuarioId) {
        List<Role> roles = usuarioService.listarRoles(usuarioId);
        return ResponseEntity.ok(roleMapper.toResponseList(roles));
    }
}
```

### 4. Atualizar IUsuarioService

```java
public interface IUsuarioService {
    // ... métodos existentes ...

    void adicionarPermissoes(Long usuarioId, AdicionarPermissoesRequest request);
    void removerPermissao(Long usuarioId, Long permissaoId);
    List<Permissao> listarPermissoes(Long usuarioId);

    void adicionarRoles(Long usuarioId, AdicionarRolesRequest request);
    void removerRole(Long usuarioId, Long roleId);
    List<Role> listarRoles(Long usuarioId);
}
```

### 5. Implementação em UsuarioServiceImpl

```java
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements IUsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PermissaoRepository permissaoRepository;
    private final RoleRepository roleRepository;
    private final UsuarioPermissaoRepository usuarioPermissaoRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;

    // ... métodos existentes ...

    @Override
    public void adicionarPermissoes(Long usuarioId, AdicionarPermissoesRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        for (Long permissaoId : request.permissaoIds()) {
            Permissao permissao = permissaoRepository.findById(permissaoId)
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada"));

            UsuarioPermissao usuarioPermissao = UsuarioPermissao.builder()
                .usuario(usuario)
                .permissao(permissao)
                .dataInicio(request.dataInicio())
                .dataFim(request.dataFim())
                .build();

            usuarioPermissaoRepository.save(usuarioPermissao);
        }
    }

    @Override
    public void removerPermissao(Long usuarioId, Long permissaoId) {
        usuarioPermissaoRepository.deleteByUsuarioIdAndPermissaoId(usuarioId, permissaoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permissao> listarPermissoes(Long usuarioId) {
        return usuarioPermissaoRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(UsuarioPermissao::getPermissao)
            .distinct()
            .toList();
    }

    @Override
    public void adicionarRoles(Long usuarioId, AdicionarRolesRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        for (Long roleId : request.roleIds()) {
            Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada"));

            UsuarioRole usuarioRole = UsuarioRole.builder()
                .usuario(usuario)
                .role(role)
                .dataInicio(request.dataInicio())
                .dataFim(request.dataFim())
                .build();

            usuarioRoleRepository.save(usuarioRole);
        }
    }

    @Override
    public void removerRole(Long usuarioId, Long roleId) {
        usuarioRoleRepository.deleteByUsuarioIdAndRoleId(usuarioId, roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> listarRoles(Long usuarioId) {
        return usuarioRoleRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(UsuarioRole::getRole)
            .distinct()
            .toList();
    }
}
```

---

## 🔐 Matriz de Autorização

| Operação | Admin | Gestor | Usuário | Sem Autenticação |
|----------|-------|--------|---------|------------------|
| Criar Usuário | ✓ | ✓ | ✗ | ✗ |
| Listar Usuários | ✓ | ✓ | ✗ | ✗ |
| Atualizar Usuário | ✓ | ✓ | ✗ | ✗ |
| Deletar Usuário | ✓ | ✓ | ✗ | ✗ |
| Aprovar Usuário | ✓ | ✓ | ✗ | ✗ |
| Rejeitar Usuário | ✓ | ✓ | ✗ | ✗ |
| **Adicionar Permissões** | **✓** | **✓** | **✗** | **✗** |
| **Remover Permissões** | **✓** | **✓** | **✗** | **✗** |
| **Adicionar Roles** | **✓** | **✓** | **✗** | **✗** |
| **Remover Roles** | **✓** | **✓** | **✗** | **✗** |

---

## ✅ Checklist de Implementação

- [ ] Atualizar `UsuarioPermissions.java` com novas permissões
- [ ] Criar `AdicionarPermissoesRequest.java`
- [ ] Criar `AdicionarRolesRequest.java`
- [ ] Atualizar `UsuarioController.java` com novos endpoints
- [ ] Atualizar `IUsuarioService.java` com novos métodos
- [ ] Atualizar `UsuarioServiceImpl.java` com implementações
- [ ] Criar/Atualizar `UsuarioPermissaoRepository` com métodos específicos
- [ ] Criar/Atualizar `UsuarioRoleRepository` com métodos específicos
- [ ] Adicionar testes unitários para novos métodos
- [ ] Adicionar testes de integração para novos endpoints
- [ ] Atualizar `PermissaoSeed.java` para incluir novas permissões
- [ ] Atualizar Swagger/OpenAPI com novos endpoints

---

## 📚 Documentação Relacionada

- [FEATURE_PERMISSAO.md](./FEATURE_PERMISSAO.md)
- [AUTORIZACAO_ACESSO.md](./AUTORIZACAO_ACESSO.md)
- [SEGURANCA.md](./SEGURANCA.md)
- [FEATURE_USUARIO.md](./FEATURE_USUARIO.md)

---

## 🧪 Exemplo de Uso no Frontend

```typescript
import { 
  adicionarPermissoesAoUsuario, 
  removerPermissaoDoUsuario,
  adicionarRolesAoUsuario,
  removerRoleDoUsuario 
} from '@/features/usuarios/infrastructure/usuariosApiService';

// Adicionar permissões
await adicionarPermissoesAoUsuario({
  usuarioId: '123',
  permissaoIds: [1, 2, 3],
  dataInicio: '2025-01-01T00:00:00',
  dataFim: '2025-12-31T23:59:59'
});

// Remover permissão
await removerPermissaoDoUsuario({
  usuarioId: '123',
  permissaoId: 1
});

// Adicionar roles
await adicionarRolesAoUsuario({
  usuarioId: '123',
  roleIds: [1, 2],
  dataInicio: '2025-01-01T00:00:00',
  dataFim: '2025-12-31T23:59:59'
});

// Remover role
await removerRoleDoUsuario({
  usuarioId: '123',
  roleId: 1
});
```

---

**Data:** 31/12/2025  
**Status:** Implementação Pendente  
**Responsável:** Equipe Backend/Frontend
