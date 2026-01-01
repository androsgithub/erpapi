# 🚀 Guia Rápido - Ajustes de Autorização para Usuários

## 📌 TL;DR (Resumo Executivo)

Foram implementadas restrições de autorização para o gerenciamento de usuários. **Apenas ADMIN e GESTOR** podem:
- Criar, atualizar e inativar usuários
- Aprovar e rejeitar usuários
- Adicionar/remover permissões
- Adicionar/remover roles

---

## 🎯 Para Desenvolvedores Frontend

### Importar e Usar

```typescript
import { useUsuariosManagement } from '@/features/usuarios';

function MeuComponente() {
  const {
    adicionarPermissoes,
    removerPermissao,
    adicionarRoles,
    removerRole,
    podeGerenciarPermissoes,
    podeGerenciarRoles,
  } = useUsuariosManagement();

  // Seu código aqui
}
```

### Mostrar/Ocultar Botões por Autorização

```typescript
{podeGerenciarPermissoes && (
  <button onClick={() => adicionarPermissoes(data)}>
    Adicionar Permissões
  </button>
)}
```

### Tratar Erros de Autorização

```typescript
try {
  await adicionarPermissoes(data);
} catch (error) {
  // Mensagem será clara:
  // "Sem permissão para adicionar permissões. Apenas ADMIN ou GESTOR..."
  alert(error.message);
}
```

---

## 🏗️ Para Desenvolvedores Backend

### 1. Adicionar Permissões (`UsuarioPermissions.java`)

```java
public static final String GERENCIAR_PERMISSOES = PREFIX + ".gerenciar.permissoes";
public static final String REMOVER_PERMISSAO = PREFIX + ".remover.permissao";
public static final String GERENCIAR_ROLES = PREFIX + ".gerenciar.roles";
public static final String REMOVER_ROLE = PREFIX + ".remover.role";
```

### 2. Criar Endpoints no Controller

```java
@PostMapping("/{usuarioId}/permissoes")
@RequiresPermission(UsuarioPermissions.GERENCIAR_PERMISSOES)
public ResponseEntity<Void> adicionarPermissoes(
    @PathVariable Long usuarioId,
    @RequestBody AdicionarPermissoesRequest request) {
  usuarioService.adicionarPermissoes(usuarioId, request);
  return ResponseEntity.noContent().build();
}
```

### 3. Implementar Métodos no Service

```java
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
```

**→ Veja**: [AJUSTES_AUTORIZACAO_USUARIOS.md](./AJUSTES_AUTORIZACAO_USUARIOS.md) para implementação completa

---

## 🔐 Matriz de Permissões

```
┌─────────────────────┬───────┬────────┬─────────┐
│ Operação            │ ADMIN │ GESTOR │ USUÁRIO │
├─────────────────────┼───────┼────────┼─────────┤
│ Listar Usuários     │  ✓    │   ✓    │   ✗     │
│ Criar Usuário       │  ✓    │   ✓    │   ✗     │
│ Atualizar Usuário   │  ✓    │   ✓    │   ✗     │
│ Inativar Usuário    │  ✓    │   ✓    │   ✗     │
│ Aprovar Usuário     │  ✓    │   ✓    │   ✗     │
│ Rejeitar Usuário    │  ✓    │   ✓    │   ✗     │
├─────────────────────┼───────┼────────┼─────────┤
│ Adicionar Permissão │  ✓    │   ✓    │   ✗     │
│ Remover Permissão   │  ✓    │   ✓    │   ✗     │
│ Adicionar Role      │  ✓    │   ✓    │   ✗     │
│ Remover Role        │  ✓    │   ✓    │   ✗     │
└─────────────────────┴───────┴────────┴─────────┘
```

---

## 📁 Arquivos Criados/Modificados

### ✅ Criados
- `DOCS/AJUSTES_AUTORIZACAO_USUARIOS.md` - Documentação completa
- `DOCS/README_AJUSTES_USUARIOS.md` - Guia detalhado
- `erpwebapp/src/features/usuarios/domain/authorization.ts` - Autorização
- `erpwebapp/src/features/usuarios/application/hooks/useUsuariosManagement.ts` - Hook

### 🔄 Modificados
- `erpwebapp/src/features/usuarios/domain/types.ts` - Novos tipos
- `erpwebapp/src/features/usuarios/infrastructure/usuariosApiService.ts` - Novos endpoints
- `erpwebapp/src/features/usuarios/index.ts` - Exportações
- `DOCS/00_INDICE.md` - Referência adicionada

---

## 🧪 Como Testar

### Teste 1: Verificar Autorização (Frontend)
```typescript
import { canManageUsuarios } from '@/features/usuarios';

const roles = ['ADMIN'];
console.log(canManageUsuarios(roles)); // true

const roles2 = ['USUARIO'];
console.log(canManageUsuarios(roles2)); // false
```

### Teste 2: Usar Hook com Autorização
```typescript
const { podeGerenciarPermissoes } = useUsuariosManagement();
console.log(podeGerenciarPermissoes); // true ou false
```

### Teste 3: Chamada da API (quando backend estiver pronto)
```typescript
await adicionarPermissoesAoUsuario({
  usuarioId: '123',
  permissaoIds: [1, 2],
});
```

---

## 📞 Próximos Passos

1. **Backend**: Implementar os novos endpoints conforme documentação
2. **Testes**: Adicionar testes de autorização
3. **UI**: Criar componentes de gerenciamento
4. **Documentação**: Atualizar Swagger/OpenAPI

---

## 💡 Dicas

- Use `useUsuariosManagement()` para todas as operações de usuário
- Sempre verifique `podeGerenciar*` antes de mostrar botões
- Os erros são claros e explicam o motivo da negação
- Tudo está type-safe com TypeScript

---

## 📚 Links Úteis

- [Documentação Completa](./AJUSTES_AUTORIZACAO_USUARIOS.md)
- [Feature de Usuários](./FEATURE_USUARIO.md)
- [Feature de Permissões](./FEATURE_PERMISSAO.md)
- [Segurança](./SEGURANCA.md)

---

**Status**: ✅ Frontend Completo | ⏳ Backend Pendente  
**Data**: 31/12/2025
