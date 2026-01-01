# 📞 Gerenciamento de Contatos por Usuário

## Visão Geral

Implementação de um sistema para cadastrar **múltiplos contatos** para cada usuário, seguindo o padrão utilizado na feature de **Permissões**. 

Agora é possível:
- Associar diversos contatos a um usuário (email, telefone, WhatsApp, etc.)
- Marcar um contato como principal
- Ativar/desativar contatos
- Remover contatos específicos

## 🏗️ Arquitetura Implementada

### Entidades

#### UsuarioContato
Entidade de relacionamento que conecta um Usuário a múltiplos Contatos:

```java
@Entity
@Table(name = "usuario_contato")
public class UsuarioContato {
    @Id
    private Long id;
    
    @ManyToOne
    private Usuario usuario;
    
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Contato> contatos;
    
    // Métodos auxiliares
    public void adicionarContato(Contato contato)
    public void removerContato(Contato contato)
    public Contato obterContatoPrincipal()
    public Set<Contato> obterContatosPorTipo(TipoContato tipo)
    public Set<Contato> obterContatosAtivos()
}
```

#### Usuario (atualizada)
Adicionado relacionamento OneToMany com UsuarioContato:

```java
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private Set<UsuarioContato> contatos;
```

### Repository

```java
@Repository
public interface UsuarioContatoRepository extends JpaRepository<UsuarioContato, Long> {
    Optional<UsuarioContato> findByUsuarioId(Long usuarioId);
}
```

### Serviço de Aplicação

Interface: `GerenciamentoContatoServiceInterface`
Implementação: `GerenciamentoContatoServiceImpl`

Métodos disponíveis:
- `associarContatos(AssociarContatosRequest)` - Associa múltiplos contatos
- `adicionarContato(Long usuarioId, CreateContatoRequest)` - Adiciona um contato
- `removerContato(RemoverContatoRequest)` - Remove um contato
- `buscarContatosUsuario(Long usuarioId)` - Lista todos os contatos do usuário
- `marcarComoPrincipal(Long usuarioId, Long contatoId)` - Define contato principal
- `desativarContato(Long usuarioId, Long contatoId)` - Desativa um contato
- `ativarContato(Long usuarioId, Long contatoId)` - Ativa um contato

## 📡 API REST - Endpoints

### Associar Múltiplos Contatos

**POST** `/api/v1/contatos/usuario/associar`

```json
{
  "usuarioId": 1,
  "contatos": [
    {
      "tipo": "EMAIL",
      "valor": "usuario@example.com",
      "descricao": "Email corporativo",
      "principal": true
    },
    {
      "tipo": "CELULAR",
      "valor": "+55 11 98765-4321",
      "descricao": "Celular pessoal",
      "principal": false
    },
    {
      "tipo": "WHATSAPP",
      "valor": "+55 11 98765-4321",
      "descricao": "WhatsApp",
      "principal": false
    }
  ]
}
```

**Response (201):**
```json
{
  "usuarioContatoId": 1,
  "usuarioId": 1,
  "contatos": [
    {
      "id": 101,
      "tipo": "EMAIL",
      "valor": "usuario@example.com",
      "descricao": "Email corporativo",
      "principal": true,
      "ativo": true,
      "dataCriacao": "2025-12-25T10:30:00",
      "dataAtualizacao": "2025-12-25T10:30:00"
    },
    // ... mais contatos
  ],
  "dataCriacao": "2025-12-25T10:30:00",
  "dataAtualizacao": "2025-12-25T10:30:00"
}
```

### Adicionar Contato a Usuário Existente

**POST** `/api/v1/contatos/usuario/{usuarioId}/contato`

```json
{
  "tipo": "TELEFONE",
  "valor": "(11) 3456-7890",
  "descricao": "Telefone comercial",
  "principal": false
}
```

### Buscar Contatos do Usuário

**GET** `/api/v1/contatos/usuario/{usuarioId}`

**Response (200):**
```json
{
  "usuarioContatoId": 1,
  "usuarioId": 1,
  "contatos": [ /* ... */ ],
  "dataCriacao": "2025-12-25T10:30:00",
  "dataAtualizacao": "2025-12-25T10:30:00"
}
```

### Marcar Contato como Principal

**PATCH** `/api/v1/contatos/usuario/{usuarioId}/contato/{contatoId}/principal`

Nota: Desmarca automaticamente outros contatos como principais.

### Desativar Contato

**PATCH** `/api/v1/contatos/usuario/{usuarioId}/contato/{contatoId}/desativar`

### Ativar Contato

**PATCH** `/api/v1/contatos/usuario/{usuarioId}/contato/{contatoId}/ativar`

### Remover Contato

**DELETE** `/api/v1/contatos/usuario/remover`

```json
{
  "usuarioId": 1,
  "contatoId": 101
}
```

## 🗂️ Estrutura de Diretórios

```
features/contato/
├── domain/
│   ├── entity/
│   │   ├── Contato.java
│   │   ├── TipoContato.java
│   │   ├── ContatoPermissions.java
│   │   └── UsuarioContato.java ✨ NEW
│   │
│   ├── repository/
│   │   ├── ContatoRepository.java
│   │   └── UsuarioContatoRepository.java ✨ NEW
│   │
│   └── validator/
│
├── application/
│   ├── service/
│   │   ├── ContatoServiceInterface.java
│   │   ├── ContatoService.java
│   │   ├── GerenciamentoContatoServiceInterface.java ✨ NEW
│   │   └── GerenciamentoContatoServiceImpl.java ✨ NEW
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CreateContatoRequest.java
│   │   │   ├── AssociarContatosRequest.java ✨ NEW
│   │   │   └── RemoverContatoRequest.java ✨ NEW
│   │   │
│   │   └── response/
│   │       ├── ContatoResponse.java
│   │       └── UsuarioContatosResponse.java ✨ NEW
│   │
│   └── validator/
│
├── infrastructure/
│   ├── repository/
│   │   └── JpaUsuarioContatoRepository.java ✨ NEW
│   │
│   └── factory/
│
└── presentation/
    └── controller/
        └── ContatoController.java ✨ (atualizado com novos endpoints)
```

## 🔐 Permissões Requeridas

Todos os endpoints requerem as permissões da feature Contato:
- `CRIAR` - Associar/adicionar contatos
- `VISUALIZAR` - Buscar contatos
- `ATUALIZAR` - Marcar como principal, ativar/desativar
- `DELETAR` - Remover contatos

## 📊 Banco de Dados

Novas tabelas:
- `usuario_contato` - Relacionamento usuário-contatos

Alterações em tabelas existentes:
- `contatos` - Adicionada coluna `usuario_contato_id` (chave estrangeira)

## 💡 Exemplo de Uso

### 1. Associar contatos a um novo usuário

```bash
curl -X POST http://localhost:8080/api/v1/contatos/usuario/associar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "usuarioId": 1,
    "contatos": [
      {"tipo": "EMAIL", "valor": "joao@example.com", "principal": true},
      {"tipo": "CELULAR", "valor": "+5511987654321", "principal": false}
    ]
  }'
```

### 2. Adicionar contato extra

```bash
curl -X POST http://localhost:8080/api/v1/contatos/usuario/1/contato \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "tipo": "WHATSAPP",
    "valor": "+5511987654321",
    "descricao": "WhatsApp pessoal",
    "principal": false
  }'
```

### 3. Buscar contatos do usuário

```bash
curl -X GET http://localhost:8080/api/v1/contatos/usuario/1 \
  -H "Authorization: Bearer TOKEN"
```

### 4. Marcar contato como principal

```bash
curl -X PATCH http://localhost:8080/api/v1/contatos/usuario/1/contato/102/principal \
  -H "Authorization: Bearer TOKEN"
```

## 🔄 Diferenças com o Padrão de Permissões

| Aspecto | Permissões | Contatos |
|---------|-----------|----------|
| **Entidade de Relacionamento** | UsuarioPermissao | UsuarioContato |
| **Associação** | ManyToMany para Permissão e Role | OneToMany para Contato |
| **Principal** | Sem conceito | Um contato marcado como principal |
| **Ativo/Inativo** | Baseado em datas | Campo booleano |
| **Remoção** | Desassociação | Delete cascade |

## ⚠️ Considerações Importantes

1. **Apenas um contato principal**: Quando um contato é marcado como principal, os outros são desmarcados automaticamente.

2. **Cascata de exclusão**: Quando um usuário é deletado, todos os contatos são deletados automaticamente.

3. **Soft delete**: Contatos podem ser desativados (soft delete) sem serem removidos do banco.

4. **Transações**: Todas as operações utilizam `@Transactional` para garantir consistência.

5. **Validação**: DTOs utilizam validação do Spring Validation framework (pode ser estendida conforme necessário).

## 🚀 Próximos Passos

- [ ] Adicionar testes unitários para GerenciamentoContatoServiceImpl
- [ ] Adicionar testes de integração para os endpoints
- [ ] Implementar validação personalizada de contatos
- [ ] Adicionar busca por tipo de contato do usuário
- [ ] Implementar paginação para listar contatos
- [ ] Adicionar filtros avançados (ativo/inativo, principal, etc.)
- [ ] Integração com notificações para mudanças de contato principal
