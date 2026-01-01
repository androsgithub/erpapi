# 📞 Exemplos de Uso - Gerenciamento de Contatos por Usuário

## Cenários de Uso Prático

### Cenário 1: Novo Usuário com Múltiplos Contatos

Você quer cadastrar um novo usuário ("João Silva") com vários contatos de uma vez.

#### Request: Associar Contatos
```bash
POST /api/v1/contatos/usuario/associar
Content-Type: application/json
Authorization: Bearer <seu_token_jwt>

{
  "usuarioId": 1,
  "contatos": [
    {
      "tipo": "EMAIL",
      "valor": "joao.silva@empresa.com.br",
      "descricao": "Email corporativo",
      "principal": true
    },
    {
      "tipo": "CELULAR",
      "valor": "+55 11 98765-4321",
      "descricao": "Celular da empresa",
      "principal": false
    },
    {
      "tipo": "WHATSAPP",
      "valor": "+55 11 98765-4321",
      "descricao": "WhatsApp para atendimento",
      "principal": false
    },
    {
      "tipo": "TELEFONE",
      "valor": "(11) 3456-7890",
      "descricao": "Telefone da sala",
      "principal": false
    },
    {
      "tipo": "LINKEDIN",
      "valor": "https://linkedin.com/in/joao-silva",
      "descricao": "Perfil LinkedIn",
      "principal": false
    }
  ]
}
```

#### Response: 201 Created
```json
{
  "usuarioContatoId": 5,
  "usuarioId": 1,
  "contatos": [
    {
      "id": 201,
      "tipo": "EMAIL",
      "valor": "joao.silva@empresa.com.br",
      "descricao": "Email corporativo",
      "principal": true,
      "ativo": true,
      "dataCriacao": "2025-12-25T10:30:00",
      "dataAtualizacao": "2025-12-25T10:30:00"
    },
    {
      "id": 202,
      "tipo": "CELULAR",
      "valor": "+55 11 98765-4321",
      "descricao": "Celular da empresa",
      "principal": false,
      "ativo": true,
      "dataCriacao": "2025-12-25T10:30:00",
      "dataAtualizacao": "2025-12-25T10:30:00"
    },
    {
      "id": 203,
      "tipo": "WHATSAPP",
      "valor": "+55 11 98765-4321",
      "descricao": "WhatsApp para atendimento",
      "principal": false,
      "ativo": true,
      "dataCriacao": "2025-12-25T10:30:00",
      "dataAtualizacao": "2025-12-25T10:30:00"
    },
    {
      "id": 204,
      "tipo": "TELEFONE",
      "valor": "(11) 3456-7890",
      "descricao": "Telefone da sala",
      "principal": false,
      "ativo": true,
      "dataCriacao": "2025-12-25T10:30:00",
      "dataAtualizacao": "2025-12-25T10:30:00"
    },
    {
      "id": 205,
      "tipo": "LINKEDIN",
      "valor": "https://linkedin.com/in/joao-silva",
      "descricao": "Perfil LinkedIn",
      "principal": false,
      "ativo": true,
      "dataCriacao": "2025-12-25T10:30:00",
      "dataAtualizacao": "2025-12-25T10:30:00"
    }
  ],
  "dataCriacao": "2025-12-25T10:30:00",
  "dataAtualizacao": "2025-12-25T10:30:00"
}
```

---

### Cenário 2: Adicionar Contato Extra

Depois de algum tempo, João Silva consegue um novo número de celular e você quer adicionar.

#### Request: Adicionar Contato
```bash
POST /api/v1/contatos/usuario/1/contato
Content-Type: application/json
Authorization: Bearer <seu_token_jwt>

{
  "tipo": "CELULAR",
  "valor": "+55 21 99999-8888",
  "descricao": "Novo celular corporativo",
  "principal": false
}
```

#### Response: 201 Created
```json
{
  "id": 206,
  "tipo": "CELULAR",
  "valor": "+55 21 99999-8888",
  "descricao": "Novo celular corporativo",
  "principal": false,
  "ativo": true,
  "dataCriacao": "2025-12-25T11:15:00",
  "dataAtualizacao": "2025-12-25T11:15:00"
}
```

---

### Cenário 3: Consultar Contatos de um Usuário

Você quer ver todos os contatos cadastrados para João Silva.

#### Request
```bash
GET /api/v1/contatos/usuario/1
Authorization: Bearer <seu_token_jwt>
```

#### Response: 200 OK
```json
{
  "usuarioContatoId": 5,
  "usuarioId": 1,
  "contatos": [
    {
      "id": 201,
      "tipo": "EMAIL",
      "valor": "joao.silva@empresa.com.br",
      "descricao": "Email corporativo",
      "principal": true,
      "ativo": true,
      "dataCriacao": "2025-12-25T10:30:00",
      "dataAtualizacao": "2025-12-25T10:30:00"
    },
    {
      "id": 202,
      "tipo": "CELULAR",
      "valor": "+55 11 98765-4321",
      "descricao": "Celular da empresa",
      "principal": false,
      "ativo": true,
      "dataCriacao": "2025-12-25T10:30:00",
      "dataAtualizacao": "2025-12-25T10:30:00"
    },
    {
      "id": 206,
      "tipo": "CELULAR",
      "valor": "+55 21 99999-8888",
      "descricao": "Novo celular corporativo",
      "principal": false,
      "ativo": true,
      "dataCriacao": "2025-12-25T11:15:00",
      "dataAtualizacao": "2025-12-25T11:15:00"
    }
  ],
  "dataCriacao": "2025-12-25T10:30:00",
  "dataAtualizacao": "2025-12-25T11:15:00"
}
```

---

### Cenário 4: Mudar Contato Principal

João Silva quer que seu novo celular seja o contato principal para receber chamadas de atendimento.

#### Request: Marcar como Principal
```bash
PATCH /api/v1/contatos/usuario/1/contato/206/principal
Authorization: Bearer <seu_token_jwt>
```

#### Response: 200 OK
```json
{
  "id": 206,
  "tipo": "CELULAR",
  "valor": "+55 21 99999-8888",
  "descricao": "Novo celular corporativo",
  "principal": true,
  "ativo": true,
  "dataCriacao": "2025-12-25T11:15:00",
  "dataAtualizacao": "2025-12-25T11:45:00"
}
```

**Nota:** Automaticamente, o email (id: 201) foi desmarcado como principal.

---

### Cenário 5: Desativar Contato

O antigo número de celular (+55 11 98765-4321) não está mais em uso, mas você não quer deletá-lo (soft delete).

#### Request: Desativar
```bash
PATCH /api/v1/contatos/usuario/1/contato/202/desativar
Authorization: Bearer <seu_token_jwt>
```

#### Response: 200 OK
```json
{
  "id": 202,
  "tipo": "CELULAR",
  "valor": "+55 11 98765-4321",
  "descricao": "Celular da empresa",
  "principal": false,
  "ativo": false,
  "dataCriacao": "2025-12-25T10:30:00",
  "dataAtualizacao": "2025-12-25T12:00:00"
}
```

---

### Cenário 6: Reativar Contato

Descobri que o antigo número é ainda utilizado. Vou reativá-lo.

#### Request: Ativar
```bash
PATCH /api/v1/contatos/usuario/1/contato/202/ativar
Authorization: Bearer <seu_token_jwt>
```

#### Response: 200 OK
```json
{
  "id": 202,
  "tipo": "CELULAR",
  "valor": "+55 11 98765-4321",
  "descricao": "Celular da empresa",
  "principal": false,
  "ativo": true,
  "dataCriacao": "2025-12-25T10:30:00",
  "dataAtualizacao": "2025-12-25T12:05:00"
}
```

---

### Cenário 7: Remover Contato Definitivamente

O antigo email não é mais necessário. Vou removê-lo do sistema.

#### Request: Remover
```bash
DELETE /api/v1/contatos/usuario/remover
Content-Type: application/json
Authorization: Bearer <seu_token_jwt>

{
  "usuarioId": 1,
  "contatoId": 201
}
```

#### Response: 204 No Content
```
(sem conteúdo no corpo)
```

---

## Fluxo Completo - Integração com Sistema

### Exemplo em TypeScript/JavaScript

```typescript
// Serviço para gerenciar contatos de usuário
class ContatoUsuarioService {
  private baseUrl = 'http://localhost:8080/api/v1/contatos';
  private token = localStorage.getItem('token');

  private getHeaders() {
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.token}`
    };
  }

  // Associar múltiplos contatos
  async associarContatos(usuarioId: number, contatos: ContatoDTO[]) {
    const response = await fetch(`${this.baseUrl}/usuario/associar`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ usuarioId, contatos })
    });
    return response.json();
  }

  // Adicionar contato
  async adicionarContato(usuarioId: number, contato: ContatoDTO) {
    const response = await fetch(`${this.baseUrl}/usuario/${usuarioId}/contato`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(contato)
    });
    return response.json();
  }

  // Buscar contatos
  async buscarContatosUsuario(usuarioId: number) {
    const response = await fetch(`${this.baseUrl}/usuario/${usuarioId}`, {
      headers: this.getHeaders()
    });
    return response.json();
  }

  // Marcar como principal
  async marcarComoPrincipal(usuarioId: number, contatoId: number) {
    const response = await fetch(
      `${this.baseUrl}/usuario/${usuarioId}/contato/${contatoId}/principal`,
      {
        method: 'PATCH',
        headers: this.getHeaders()
      }
    );
    return response.json();
  }

  // Desativar contato
  async desativarContato(usuarioId: number, contatoId: number) {
    const response = await fetch(
      `${this.baseUrl}/usuario/${usuarioId}/contato/${contatoId}/desativar`,
      {
        method: 'PATCH',
        headers: this.getHeaders()
      }
    );
    return response.json();
  }

  // Remover contato
  async removerContato(usuarioId: number, contatoId: number) {
    const response = await fetch(`${this.baseUrl}/usuario/remover`, {
      method: 'DELETE',
      headers: this.getHeaders(),
      body: JSON.stringify({ usuarioId, contatoId })
    });
    return response.ok;
  }
}

// Uso
const service = new ContatoUsuarioService();

// Cadastrar contatos para novo usuário
await service.associarContatos(1, [
  { tipo: 'EMAIL', valor: 'joao@example.com', principal: true },
  { tipo: 'CELULAR', valor: '+5511987654321', principal: false }
]);

// Consultar contatos
const contatos = await service.buscarContatosUsuario(1);
console.log(contatos);

// Marcar novo contato como principal
await service.marcarComoPrincipal(1, 206);

// Remover contato
await service.removerContato(1, 201);
```

---

## Códigos de Erro Esperados

| Status | Situação |
|--------|----------|
| **201** | Contatos/contato criado(s) com sucesso |
| **200** | Operação realizada com sucesso |
| **204** | Contato removido com sucesso |
| **400** | Dados inválidos ou usuário não encontrado |
| **401** | Não autenticado (falta token JWT) |
| **403** | Não autorizado (falta permissão) |
| **404** | Usuário ou contato não encontrado |
| **500** | Erro interno do servidor |

---

## Validações Automáticas

O sistema realiza as seguintes validações:

1. **Usuário deve existir**: Se o usuarioId for inválido, erro 404
2. **Tipo de contato válido**: Deve ser um dos valores do enum TipoContato
3. **Valor não vazio**: O campo valor é obrigatório
4. **Principal único**: Apenas um contato pode ser marcado como principal
5. **Contato deve existir**: Ao tentar atualizar/remover, deve existir

---

## Boas Práticas

1. **Sempre ter um contato principal** para consultas rápidas
2. **Desativar em vez de remover** quando possível (auditoria)
3. **Validar formato de contatos** (email, telefone, etc.)
4. **Usar soft delete** para manter histórico
5. **Implementar cache** para listar contatos frequentemente consultados
