# 📍 Feature: Endereço

## 📋 Visão Geral

A feature **Endereço** é responsável pelo gerenciamento centralizado de endereços no sistema, incluindo endereços de empresas, filiais, clientes e outros.

## 🎯 Responsabilidades

- Gerenciar endereços (criar, atualizar, deletar, listar)
- Validar dados de endereço (CEP, cidade, estado)
- Suportar múltiplos endereços por entidade
- Endereço de cobrança e entrega
- Complementos de endereço
- Geocodificação (opcional)

## 📊 Entidades Principais

### **Endereco**
Entidade que representa um endereço.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | Identificador único |
| `logradouro` | String | Rua, avenida, etc |
| `numero` | String | Número do endereço |
| `complemento` | String | Complemento (opcional) |
| `bairro` | String | Bairro |
| `cidade` | String | Cidade |
| `estado` | String | UF (estado) |
| `cep` | String | CEP |
| `pais` | String | País |
| `tipo` | TipoEndereco | COMERCIAL, RESIDENCIAL, ENTREGA, COBRANÇA |
| `principal` | Boolean | Se é endereço principal |
| `ativa` | Boolean | Se está ativa |
| `dataCriacao` | LocalDateTime | Data de criação |
| `dataAtualizacao` | LocalDateTime | Data de atualização |

### **TipoEndereco** (Enum)
```java
COMERCIAL      // Endereço comercial da empresa
RESIDENCIAL    // Endereço residencial
ENTREGA       // Endereço de entrega de pedidos
COBRANCA      // Endereço de cobrança
FILIAL        // Endereço de filial
```

### **EnderecoPermissions** (Enum)
```java
ENDERECO_CRIAR          // Criar endereço
ENDERECO_VISUALIZAR     // Visualizar endereços
ENDERECO_ATUALIZAR      // Atualizar endereço
ENDERECO_DELETAR        // Deletar endereço
ENDERECO_DEFINIR_PRINCIPAL  // Definir como principal
```

## 🏗️ Estrutura de Diretórios

```
features/endereco/
├── domain/
│   ├── entity/
│   │   ├── Endereco.java
│   │   ├── TipoEndereco.java
│   │   └── EnderecoPermissions.java
│   │
│   ├── service/
│   │   └── EnderecoService.java
│   │
│   ├── validator/
│   │   └── EnderecoValidator.java
│   │
│   └── repository/
│       └── EnderecoRepository.java
│
├── application/
│   ├── service/
│   │   ├── CriarEnderecoService.java
│   │   ├── AtualizarEnderecoService.java
│   │   └── EnderecoServiceImpl.java
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CriarEnderecoRequest.java
│   │   │   ├── AtualizarEnderecoRequest.java
│   │   │   └── DefinirPrincipalRequest.java
│   │   │
│   │   └── response/
│   │       ├── EnderecoResponse.java
│   │       └── EnderecoListResponse.java
│   │
│   └── validator/
│       └── EnderecoValidator.java
│
├── infrastructure/
│   └── repository/
│       └── JpaEnderecoRepository.java
│
└── presentation/
    └── controller/
        └── EnderecoController.java
```

## 🔄 Fluxos Principais

### 1️⃣ Criar Endereço
```
1. Controller recebe CriarEnderecoRequest
2. Valida campos (CEP, estado, cidade)
3. Se CEP, tenta carregar dados via API (ViaCEP)
4. Cria entidade Endereco
5. Se é principal, desativa outros
6. Persiste no banco
7. Retorna EnderecoResponse
```

### 2️⃣ Definir como Principal
```
1. Controller recebe ID do endereço
2. Busca endereço
3. Desativa principal anterior
4. Ativa este como principal
5. Persiste
6. Retorna sucesso
```

### 3️⃣ Listar por Tipo
```
1. Filtra endereços por tipo e status
2. Ordena por principal primeiro
3. Retorna lista paginada
4. Inclui informações de validação
```

## 📡 Endpoints da API

### Básico
```
GET    /api/v1/enderecos              # Listar todos
GET    /api/v1/enderecos/{id}         # Obter por ID
POST   /api/v1/enderecos              # Criar novo
PUT    /api/v1/enderecos/{id}         # Atualizar
DELETE /api/v1/enderecos/{id}         # Deletar
```

### Gerenciamento
```
GET    /api/v1/enderecos/tipo/{tipo}       # Listar por tipo
PATCH  /api/v1/enderecos/{id}/principal    # Definir como principal
GET    /api/v1/enderecos/principal         # Obter principal
```

### Validação
```
GET    /api/v1/enderecos/cep/{cep}    # Buscar por CEP (ViaCEP)
POST   /api/v1/enderecos/validar      # Validar endereço
```

## ✅ Validações

### Ao Criar
- ✓ Logradouro não pode estar em branco
- ✓ Número não pode estar em branco
- ✓ Bairro não pode estar em branco
- ✓ Cidade não pode estar em branco
- ✓ Estado deve ser UF válida (2 caracteres)
- ✓ CEP deve ser válido (formato)
- ✓ País deve ser válido

### Validação de CEP
- ✓ Formato: 5 dígitos + hífen + 3 dígitos (XX.XXX-XXX)
- ✓ Estado deve corresponder ao CEP (opcional)

## 🔗 Relacionamentos

```
Endereco
    ├── Empresa (muitos-para-um)
    ├── Usuario (muitos-para-um) [opcional]
    └── TipoEndereco (enum)
```

## 🌐 Integração com ViaCEP

```java
// Buscar dados de CEP automaticamente
public class ViaCepAdapter {
    public EnderecoData buscarPorCep(String cep) {
        // GET https://viacep.com.br/ws/{cep}/json/
        // Retorna: logradouro, bairro, cidade, uf
    }
}
```

## 🧪 Testes

```java
EnderecoEntityTest.java
EnderecoServiceTest.java
CriarEnderecoServiceTest.java
EnderecoControllerTest.java
ViaCepAdapterTest.java
```

## 🚀 Boas Práticas

1. **Validar CEP** antes de persistir
2. **Usar APIs públicas** para dados de localidade
3. **Cache de CEPs** consultados
4. **Apenas um principal** por tipo de endereço
5. **Soft delete** para manter histórico
6. **Complemento é opcional** mas útil
7. **Aceitar formatos variados** de CEP (com/sem hífen)

## 🔐 Segurança

- ✓ Usuários acessam apenas endereços permitidos (por empresa)
- ✓ Logs de alteração em endereços principais
- ✓ Validação de entrada contra injection

## 📚 Referências Relacionadas

- [FEATURE_EMPRESA.md](FEATURE_EMPRESA.md) - Empresas com endereços
- [FEATURE_USUARIO.md](FEATURE_USUARIO.md) - Usuários com endereço
- [CAMADA_INFRASTRUCTURE.md](CAMALA_INFRASTRUCTURE.md) - Adapters

---

**Última atualização:** Dezembro de 2025
