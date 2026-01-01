# Refatoração de Controllers de Contato - Padrão de Separação

## Visão Geral

A feature de Contato foi refatorada para separar os controllers em:
- **ContatosController** - Operações básicas de contato (CRUD global)
- **ContatosUsuarioController** - Operações específicas de contato para usuários

Este padrão permite extensão futura para outras entidades (Cliente, Fornecedor, etc.) seguindo a mesma estrutura.

## Estrutura de Controllers

### 1. ContatosController (Base)
**Rota Base:** `/api/v1/contatos`

Responsável por operações fundamentais de contato:

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/` | Criar novo contato |
| GET | `/{id}` | Buscar contato por ID |
| GET | `/` | Listar todos os contatos |
| GET | `/status/ativos` | Listar contatos ativos |
| GET | `/status/inativos` | Listar contatos inativos |
| GET | `/tipo/{tipo}` | Listar por tipo (TELEFONE, EMAIL, WHATSAPP) |
| GET | `/principal` | Buscar contato marcado como principal |
| PUT | `/{id}` | Atualizar contato |
| PATCH | `/{id}/ativar` | Ativar contato |
| PATCH | `/{id}/desativar` | Desativar contato |
| DELETE | `/{id}` | Deletar contato (físico) |

### 2. ContatosUsuarioController (Especializado)
**Rota Base:** `/api/v1/contatos/usuario`

Responsável por operações de contatos associados a usuários:

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/associar` | Associar múltiplos contatos a um usuário |
| POST | `/{usuarioId}/contato` | Adicionar contato a usuário |
| GET | `/{usuarioId}` | Buscar contatos de um usuário |
| DELETE | `/remover` | Remover contato de usuário |
| PATCH | `/{usuarioId}/contato/{contatoId}/principal` | Marcar como principal |
| PATCH | `/{usuarioId}/contato/{contatoId}/desativar` | Desativar contato |
| PATCH | `/{usuarioId}/contato/{contatoId}/ativar` | Ativar contato |

## Padrão para Novas Entidades

Para criar suporte a contatos de uma nova entidade (ex: Cliente), siga este padrão:

### 1. Criar novo Controller
Arquivo: `src/main/java/com/api/erp/v1/features/contato/presentation/controller/ContatosClienteController.java`

```java
@RestController
@RequestMapping("/api/v1/contatos/cliente")
@Tag(name = "Contatos do Cliente", description = "Gestão de Contatos - Operações de Cliente")
@RequiredArgsConstructor
public class ContatosClienteController {

    private final IGerenciamentoContatoService gerenciamentoContatoService;
    private final IContatoMapper contatoMapper;
    private final ClienteContatoMapper clienteContatoMapper;

    // Implementar endpoints similares ao ContatosUsuarioController
    // adaptando para Cliente em vez de Usuário
}
```

### 2. Criar Mapper especializado (se necessário)
Arquivo: `src/main/java/com/api/erp/v1/features/contato/application/mapper/ClienteContatoMapper.java`

```java
@Component
public class ClienteContatoMapper {
    // Mapear ClienteContato para ClienteContatosResponse
}
```

### 3. Criar DTOs de Response (se necessário)
Arquivo: `src/main/java/com/api/erp/v1/features/contato/application/dto/response/ClienteContatosResponse.java`

```java
public record ClienteContatosResponse(
    Long clienteId,
    String nomeCliente,
    List<ContatoResponse> contatos
) {}
```

## Benefícios da Refatoração

✅ **Separação de Responsabilidades** - Controllers específicos para contextos diferentes

✅ **Reutilização de Código** - Services e mappers compartilhados entre controllers

✅ **Escalabilidade** - Padrão claro para adicionar novas entidades

✅ **Manutenibilidade** - Código organizado e fácil de entender

✅ **Documentação Automática** - Swagger organiza endpoints por tags

## Migração de Clientes

Se você tinha endpoints que chamavam `ContatoController`, atualize para:

### Antes:
```
POST   /api/v1/contatos
GET    /api/v1/contatos/{id}
POST   /api/v1/contatos/usuario/associar
GET    /api/v1/contatos/usuario/{usuarioId}
```

### Depois:
```
POST   /api/v1/contatos
GET    /api/v1/contatos/{id}
POST   /api/v1/contatos/usuario/associar
GET    /api/v1/contatos/usuario/{usuarioId}
```

✨ **As rotas continuam as mesmas!** A mudança é apenas interna (separação de controllers).

## Estrutura de Diretórios

```
features/contato/presentation/controller/
├── ContatosController.java              (Operações básicas)
├── ContatosUsuarioController.java       (Contatos de usuário)
└── ContatosClienteController.java       (Futuro: Contatos de cliente)
```

## Próximos Passos

1. ✅ Refatoração realizada - Controllers separados
2. 📝 Testar todos os endpoints da API
3. 🚀 Adicionar novas entidades seguindo o padrão
4. 📚 Documentação atualizada

## Referências

- [ContatosController.java](../controller/ContatosController.java) - Base
- [ContatosUsuarioController.java](../controller/ContatosUsuarioController.java) - Especializado
