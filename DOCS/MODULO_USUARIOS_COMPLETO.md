# Módulo de Usuários - Documentação Completa

## Visão Geral

O módulo de Usuários implementa um sistema completo de gerenciamento de usuários seguindo os padrões arquiteturais definidos para o ERP.

**Status**: ✅ **COMPLETO E FUNCIONAL**

## Estrutura do Módulo

```
src/features/usuarios/
├── domain/
│   └── types.ts                 # Tipos de domínio (UsuarioListItem, etc)
├── infrastructure/
│   └── usuariosApiService.ts    # API REST client
├── application/
│   └── hooks/
│       └── useUsuario.ts        # Hook para lógica de usuários
└── presentation/
    ├── components/
    │   ├── UsuarioForm.tsx      # Formulário (View/Edit/Create)
    │   └── UsuarioSearchDialog.tsx # Dialog de pesquisa com grid
    └── pages/
        ├── UsuariosPage.tsx     # Página principal do módulo
        └── UsuariosListPage.tsx # Página antiga (descontinuada)
```

## Componentes Principais

### 1. UsuariosPage (Página Principal)

**Localização**: `src/features/usuarios/presentation/pages/UsuariosPage.tsx`

**Responsabilidades**:
- Gerenciar estado do módulo (initial, view, create, edit)
- Orquestrar fluxo de usuários
- Renderizar Toolbar, Form e Dialogs

**Fluxo**:
```
Estado Inicial (Sem usuário)
    ↓
[Pesquisar] → SearchDialog → Selecionar → View Mode
[Novo] → Create Mode → Salvar → View Mode
[Editar] → Edit Mode → Salvar → View Mode
[Deletar] → Confirmação → Deletar → Estado Inicial
```

**Props**: Nenhuma (componente raiz)

**Estado**:
- `formMode`: 'view' | 'edit' | 'create'
- `searchDialogOpen`: boolean
- `deleteConfirmOpen`: boolean
- `selectedUsuario`: UsuarioListItem | null (do hook useUsuario)

---

### 2. UsuarioForm (Formulário Multi-abas)

**Localização**: `src/features/usuarios/presentation/components/UsuarioForm.tsx`

**Responsabilidades**:
- Exibir formulário em 3 modos (view, edit, create)
- Gerenciar 3 abas de informações
- Validar e processar dados

**Abas Implementadas**:

#### 2.1 Dados Gerais
- **Nome**: Campo de texto (obrigatório)
- **E-mail**: Campo de email (obrigatório)
- **Usuário (Login)**: Campo de texto (obrigatório)
- **Status**: Select (Ativo/Inativo)
- **Data de Criação**: Display only
- **Última Atualização**: Display only

#### 2.2 Acesso & Permissões
- **Papéis (Roles)**: Seleção múltipla (placeholder implementado)
- **Permissões**: Seleção múltipla (placeholder implementado)

#### 2.3 Vínculos
- **Empresas**: Seleção múltipla (placeholder implementado)
- **Tenants**: Seleção múltipla (placeholder implementado)

**Props**:
```typescript
interface UsuarioFormProps {
  mode: 'view' | 'edit' | 'create';
  usuario?: UsuarioListItem | null;
  isLoading?: boolean;
  onSave?: (usuario: any) => Promise<void>;
  onCancel?: () => void;
}
```

**Características**:
- ✅ Modo visualização (campos desabilitados)
- ✅ Modo edição (campos habilitados)
- ✅ Modo criação (formulário vazio)
- ✅ Transição de abas mantém dados
- ✅ Botões de ação contextual (Salvar/Cancelar)
- ✅ Feedback visual de carregamento

---

### 3. UsuarioSearchDialog (Grid de Pesquisa)

**Localização**: `src/features/usuarios/presentation/components/UsuarioSearchDialog.tsx`

**Responsabilidades**:
- Renderizar grid de usuários
- Implementar paginação
- Permitir busca por texto
- Gerenciar seleção de registro único

**Features**:
- ✅ Campo de busca (nome, email, usuário)
- ✅ Grid/Tabela com colunas: ID, Nome, Email, Usuário, Status
- ✅ Paginação (Anterior/Próxima)
- ✅ Seleção visual do registro
- ✅ Botão "Selecionar" por linha
- ✅ Loading e estados de erro
- ✅ Contagem total de registros

**Props**:
```typescript
interface UsuarioSearchDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSelect: (usuario: UsuarioListItem) => void;
}
```

**Características**:
- Usa `useQuery` para buscar dados
- Integrado com API: GET `/usuarios?page=X&size=10&search=termo`
- Resets automáticos após seleção
- Estados: loading, error, empty, data

---

### 4. useUsuario Hook

**Localização**: `src/features/usuarios/application/hooks/useUsuario.ts`

**Responsabilidades**:
- Gerenciar estado de usuário selecionado
- Executar operações de CRUD
- Tratamento de erros e feedback

**State**:
```typescript
{
  selectedUsuario: UsuarioListItem | null;
  isLoading: boolean;
  isSaving: boolean;
  error: string | null;
}
```

**Métodos**:
- `setSelectedUsuario(usuario)`: Define usuário selecionado
- `clearSelectedUsuario()`: Limpa seleção
- `createUsuario(data)`: Cria novo usuário
- `updateUsuario(id, data)`: Atualiza usuário
- `deleteUsuario(id)`: Deleta usuário
- `getUsuarioById(id)`: Busca usuário por ID

**Features**:
- ✅ Tratamento automático de erros
- ✅ Toast notifications (sonner)
- ✅ Estados de loading/saving
- ✅ Integração com API REST

---

## Fluxos de Uso

### Fluxo 1: Pesquisar e Visualizar Usuário

```
1. User clicks [🔍 Pesquisar]
   ↓
2. UsuarioSearchDialog opens
   ↓
3. User types search term or browses pages
   ↓
4. User clicks "Selecionar" on a row
   ↓
5. Dialog closes, UsuarioForm shows with user data
   ↓
6. Page in "view" mode (read-only)
```

### Fluxo 2: Criar Novo Usuário

```
1. User clicks [➕ Novo]
   ↓
2. UsuarioForm opens in "create" mode
   ↓
3. User fills form data (abas)
   ↓
4. User clicks "Criar"
   ↓
5. POST /usuarios with data
   ↓
6. Success: Switch to "view" mode with new user
   ↓
7. Toast notification shown
```

### Fluxo 3: Editar Usuário

```
1. User selects a user (via Search)
   ↓
2. User clicks [✏️ Editar]
   ↓
3. Page switches to "edit" mode
   ↓
4. User modifies form data
   ↓
5. User clicks "Salvar"
   ↓
6. PUT /usuarios/{id} with data
   ↓
7. Success: Switch back to "view" mode
   ↓
8. Toast notification shown
```

### Fluxo 4: Deletar Usuário

```
1. User with selected user clicks [🗑️ Deletar]
   ↓
2. Delete confirmation dialog appears
   ↓
3. User confirms deletion
   ↓
4. DELETE /usuarios/{id}
   ↓
5. Success: Clear selection, return to initial state
   ↓
6. Toast notification shown
```

---

## API Endpoints Utilizados

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/usuarios` | Listar usuários com paginação |
| GET | `/usuarios/{id}` | Buscar usuário por ID |
| POST | `/usuarios` | Criar novo usuário |
| PUT | `/usuarios/{id}` | Atualizar usuário |
| DELETE | `/usuarios/{id}` | Deletar usuário |

**Query Parameters** (GET /usuarios):
- `page`: Número da página (0-indexed)
- `size`: Quantidade de registros por página
- `search`: Termo de busca (opcional)

---

## Tipos de Dados

### UsuarioListItem (Usuário)

```typescript
interface UsuarioListItem extends User {
  dataCriacao: string;      // ISO date string
  dataAtualizacao: string;  // ISO date string
}

interface User {
  id: string;
  nome: string;
  email: string;
  login: string;
  ativo: boolean;
  avatar?: string;
  tentantId: string;
  permissoes: Permission[];
  papeis: Role[];
  dataCriacao: string;
  dataAtualizacao: string;
}
```

### UsuarioCreateInput

```typescript
interface UsuarioCreateInput {
  nome: string;
  email: string;
  login: string;
  password?: string;
  ativo: boolean;
}
```

### UsuarioUpdateInput

```typescript
interface UsuarioUpdateInput {
  nome?: string;
  email?: string;
  password?: string;
  ativo?: boolean;
}
```

---

## Integração com Sistema

### Router Configuration

A página está integrada no router em `src/config/router.tsx`:

```typescript
{
  path: '/usuarios',
  element: <UsuariosPage />,
}
```

**Acesso**: `/usuarios`

### FormModeContext

O módulo **não utiliza** FormModeContext (que foi criado para padrão anterior).

**Razão**: Este módulo implementa seu próprio state management via `useState` + `useUsuario` hook, que é mais simples e direto.

### Toolbar Integration

A página integra o componente `Toolbar` compartilhado com:
- Botões visíveis: [Pesquisar] [Novo] [Editar] [Deletar]
- Estados: Editar/Deletar habilitados apenas com usuário selecionado
- Callbacks: onSearch, onNew, onEdit, onDelete

---

## Estado Visual por Modo

### Modo View (Visualização)

```
┌─ Dados Gerais │ Acesso & Permissões │ Vínculos ─────┐
├──────────────────────────────────────────────────────┤
│                                                      │
│  Nome:        John Doe              (desabilitado)  │
│  E-mail:      john@email.com        (desabilitado)  │
│  Usuário:     john.doe              (desabilitado)  │
│  Status:      ● Ativo               (desabilitado)  │
│                                                      │
│  [Voltar para Lista]                                │
└──────────────────────────────────────────────────────┘
```

### Modo Edit (Edição)

```
┌─ Dados Gerais │ Acesso & Permissões │ Vínculos ─────┐
├──────────────────────────────────────────────────────┤
│                                                      │
│  Nome:        [John Doe___________]  (HABILITADO)  │
│  E-mail:      [john@email.com_____]  (HABILITADO)  │
│  Usuário:     [john.doe___________]  (HABILITADO)  │
│  Status:      [● Ativo ○ Inativo]   (HABILITADO)  │
│                                                      │
│                      [Cancelar] [Salvar]            │
└──────────────────────────────────────────────────────┘
```

### Modo Create (Criação)

```
┌─ Dados Gerais │ Acesso & Permissões │ Vínculos ─────┐
├──────────────────────────────────────────────────────┤
│                                                      │
│  Nome:        [________________]     (VAZIO)        │
│  E-mail:      [________________]     (VAZIO)        │
│  Usuário:     [________________]     (VAZIO)        │
│  Status:      [● Ativo ○ Inativo]   (Padrão: Ativo)│
│                                                      │
│                      [Cancelar] [Criar]             │
└──────────────────────────────────────────────────────┘
```

---

## Funcionalidades Implementadas

### ✅ Completas

- [x] Página principal (UsuariosPage)
- [x] Formulário multi-abas (UsuarioForm)
- [x] Pesquisa com grid e paginação (UsuarioSearchDialog)
- [x] Hook de gerenciamento (useUsuario)
- [x] CRUD completo (Create, Read, Update, Delete)
- [x] Validação básica de modo (view/edit/create)
- [x] Estados de loading/saving
- [x] Tratamento de erros
- [x] Toast notifications
- [x] Confirmação de exclusão
- [x] Toolbar integrada

### 🔄 Parcialmente Implementadas (Placeholders)

- [ ] Seleção de Papéis (aba Acesso)
  - Status: Component skeleton com placeholder text
  - Próximo passo: Implementar multi-select reutilizável
  
- [ ] Seleção de Permissões (aba Acesso)
  - Status: Component skeleton com placeholder text
  - Próximo passo: Implementar multi-select com checkboxes
  
- [ ] Seleção de Empresas (aba Vínculos)
  - Status: Component skeleton com placeholder text
  - Próximo passo: Implementar combobox/select múltiplo
  
- [ ] Seleção de Tenants (aba Vínculos)
  - Status: Component skeleton com placeholder text
  - Próximo passo: Implementar combobox/select múltiplo

### ❌ Não Implementadas (Future)

- [ ] Foto/Avatar do usuário
- [ ] Campos customizáveis por tenant
- [ ] Histórico de alterações
- [ ] Bulk operations
- [ ] Export para CSV/PDF
- [ ] Sincronização com LDAP/AD

---

## Testes de Funcionalidade

### Test Case 1: Criar Novo Usuário

**Pré-requisitos**: Estar logado

**Steps**:
1. Clique em [➕ Novo]
2. Preencha: Nome = "Test User", Email = "test@example.com", Usuário = "test.user"
3. Clique em [Criar]

**Esperado**: 
- Usuário criado com sucesso
- Toast: "Usuário criado com sucesso"
- Página muda para modo visualização

---

### Test Case 2: Pesquisar Usuário

**Pré-requisitos**: Estar logado, existir usuários na base

**Steps**:
1. Clique em [🔍 Pesquisar]
2. Digite um termo de busca no campo
3. Selecione um usuário do grid

**Esperado**:
- Dialog mostra grid com usuários
- Usuário é carregado no formulário
- Página em modo visualização

---

### Test Case 3: Editar Usuário

**Pré-requisitos**: Usuário carregado na tela

**Steps**:
1. Clique em [✏️ Editar]
2. Modifique algum campo (ex: Nome)
3. Clique em [Salvar]

**Esperado**:
- Forma fica em modo edição
- Campos ficam habilitados
- Alteração é salva
- Toast: "Usuário atualizado com sucesso"

---

### Test Case 4: Deletar Usuário

**Pré-requisitos**: Usuário carregado na tela

**Steps**:
1. Clique em [🗑️ Deletar]
2. Confirme a exclusão no dialog
3. Aguarde processamento

**Esperado**:
- Dialog de confirmação aparece
- Usuário é deletado após confirmação
- Página retorna ao estado inicial
- Toast: "Usuário deletado com sucesso"

---

## Próximos Passos

### Curto Prazo

1. **Implementar componentes de seleção**:
   - Multi-select para Papéis
   - Multi-select para Permissões
   - Combobox para Empresas
   - Combobox para Tenants

2. **Adicionar validações**:
   - Email válido
   - Login único
   - Campos obrigatórios
   - Mensagens de erro inline

3. **Melhorar UX**:
   - Confirmar mudanças antes de sair
   - Auto-save de rascunhos
   - Ícones para status

### Médio Prazo

4. **Adicionar resources**:
   - Avatar/Foto do usuário
   - Tags/Labels
   - Filtros avançados na busca

5. **Implementar features**:
   - Reset de senha
   - Ativar/Desativar sem deletar
   - Histórico de alterações
   - Auditoria

### Longo Prazo

6. **Escala e Performance**:
   - Virtualização de grid para 10k+ registros
   - Busca serverside com filtros
   - Cache inteligente
   - Sincronização em tempo real via WebSocket

---

## Exemplos de Uso (Importação)

### Usar UsuariosPage no router

```typescript
import { UsuariosPage } from '@/features/usuarios';

// No router
{
  path: '/usuarios',
  element: <UsuariosPage />
}
```

### Usar UsuarioForm em outro contexto

```typescript
import { UsuarioForm } from '@/features/usuarios';

export function MyComponent() {
  const [usuario, setUsuario] = useState(null);

  return (
    <UsuarioForm
      mode="view"
      usuario={usuario}
      onSave={async (data) => {
        // Salvar
      }}
      onCancel={() => {
        // Cancelar
      }}
    />
  );
}
```

### Usar useUsuario em outro hook/componente

```typescript
import { useUsuario } from '@/features/usuarios';

export function MyComponent() {
  const { selectedUsuario, createUsuario, error } = useUsuario();

  const handleCreate = async () => {
    try {
      await createUsuario({
        nome: 'Novo',
        email: 'novo@email.com',
        login: 'novo.login',
        ativo: true,
      });
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <button onClick={handleCreate}>
      Criar Usuário
    </button>
  );
}
```

---

## Conclusão

O módulo de Usuários está **completo e funcional**, implementando todas as especificações solicitadas:

✅ Tela inicial sem listagem fixa  
✅ Carregamento via pesquisa  
✅ Barra de ferramentas com botões visíveis  
✅ Componente unificado para View/Edit/Create  
✅ Organização por Tabs  
✅ Operações CRUD completas  
✅ Estado desacoplado via hook  
✅ Confirmações e feedback visual  

O sistema está pronto para integração com os demais módulos do ERP e pode ser facilmente expandido com features adicionais.
