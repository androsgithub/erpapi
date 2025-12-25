# 📦 Feature: Produto

## 📋 Visão Geral

A feature **Produto** é responsável pelo gerenciamento completo do catálogo de produtos do ERP. Inclui criação, atualização, consulta, composição de produtos e classificação fiscal.

## 🎯 Responsabilidades

- Gerenciar informações de produtos (código, descrição, tipo)
- Controlar status de produtos (ativo, inativo, bloqueado, descontinuado)
- Definir tipos de produto (Comprado ou Fabricável)
- Gerenciar composição de produtos (BOM - Bill of Materials)
- Organizar classificação fiscal (NCM - Nomenclatura Comum do Mercosul)
- Associar unidades de medida a produtos
- Controlar permissões de acesso por usuário

## 📊 Entidades Principais

### **Produto**
Entidade core que representa um produto no catálogo.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | Identificador único |
| `codigo` | String | Código único do produto |
| `descricao` | String | Descrição/nome do produto |
| `status` | StatusProduto | Estado (ATIVO, INATIVO, BLOQUEADO, DESCONTINUADO) |
| `tipo` | TipoProduto | COMPRADO ou FABRICAVEL |
| `unidadeMedida` | UnidadeMedida | Referência para unidade padrão |
| `margemLucro` | BigDecimal | Margem de lucro em % |
| `dataFabricacao` | LocalDateTime | Data de fabricação |
| `dataValidade` | LocalDateTime | Data de validade |
| `dataCriacao` | LocalDateTime | Data de criação no sistema |
| `dataAtualizacao` | LocalDateTime | Data da última atualização |

### **StatusProduto** (Enum)
```java
ATIVO           // Produto ativo no catálogo
INATIVO         // Produto inativo temporariamente
BLOQUEADO       // Produto bloqueado para uso
DESCONTINUADO   // Produto descontinuado
```

### **TipoProduto** (Enum)
```java
COMPRADO        // Produto comprado de terceiros
FABRICAVEL      // Produto fabricado internamente
```

### **ProdutoComposicao**
Representa a estrutura de composição de um produto (BOM).

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | Identificador único |
| `produtoPai` | Produto | Produto que é composto |
| `produtoFilho` | Produto | Produto que compõe |
| `quantidade` | BigDecimal | Quantidade necessária |
| `unidade` | UnidadeMedida | Unidade de medida |

### **ClassificacaoFiscal**
Informações de classificação fiscal do produto.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | Identificador único |
| `produto` | Produto | Referência ao produto |
| `ncm` | String | Nomenclatura Comum do Mercosul |
| `cest` | String | Código Especificador de Substituição Tributária |
| `icmsAliquota` | BigDecimal | Alíquota ICMS |
| `ipiAliquota` | BigDecimal | Alíquota IPI |

## 🔐 Permissões

### **ProdutoPermissions** (Enum)
Define granularidade de permissões por ação:

```java
PRODUTO_CRIAR          // Criar novo produto
PRODUTO_VISUALIZAR     // Visualizar produtos
PRODUTO_ATUALIZAR      // Atualizar produto
PRODUTO_DELETAR        // Deletar produto
PRODUTO_ATIVAR         // Ativar produto
PRODUTO_INATIVAR       // Inativar produto
PRODUTO_BLOQUEAR       // Bloquear produto
COMPOSICAO_GERENCIAR   // Gerenciar composição
```

## 🏗️ Estrutura de Diretórios

```
features/produto/
├── domain/
│   ├── entity/
│   │   ├── Produto.java
│   │   ├── ProdutoComposicao.java
│   │   ├── ClassificacaoFiscal.java
│   │   ├── TipoProduto.java
│   │   ├── StatusProduto.java
│   │   ├── ProdutoPermissions.java
│   │   ├── ComposicaoPermissions.java
│   │   └── ListaExpandidaPermissions.java
│   │
│   ├── service/
│   │   ├── ProdutoService.java
│   │   ├── ComposicaoProdutoService.java
│   │   └── ListaExpandidaProducaoService.java
│   │
│   └── repository/
│       ├── ProdutoRepository.java
│       ├── ProdutoComposicaoRepository.java
│       └── ClassificacaoFiscalRepository.java
│
├── application/
│   ├── service/
│   │   ├── CriarProdutoService.java
│   │   ├── AtualizarProdutoService.java
│   │   ├── ListarProdutoService.java
│   │   └── ComposicaoProdutoManager.java
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CriarProdutoRequest.java
│   │   │   ├── AtualizarProdutoRequest.java
│   │   │   ├── AdicionarComposicaoRequest.java
│   │   │   └── ...
│   │   │
│   │   └── response/
│   │       ├── ProdutoResponse.java
│   │       ├── ProdutoDetailResponse.java
│   │       ├── ComposicaoResponse.java
│   │       └── ...
│   │
│   └── validator/
│       ├── ProdutoValidator.java
│       └── ComposicaoValidator.java
│
├── infrastructure/
│   └── repository/
│       ├── JpaProdutoRepository.java
│       ├── JpaProdutoComposicaoRepository.java
│       └── JpaClassificacaoFiscalRepository.java
│
└── presentation/
    └── controller/
        └── ProdutoController.java
```

## 🔄 Fluxos Principais

### 1️⃣ Criar Produto
```
1. Controller recebe CriarProdutoRequest
2. Valida dados (CriarProdutoValidator)
3. Verifica se código é único (repository)
4. Cria entidade Produto (ProdutoFactory ou Service)
5. Persiste no banco (ProdutoRepository)
6. Retorna ProdutoResponse
```

### 2️⃣ Adicionar Composição
```
1. Controller recebe AdicionarComposicaoRequest
2. Valida produtos (ProdutoService)
3. Verifica permissão de composição
4. Cria ProdutoComposicao
5. Persiste associação
6. Retorna sucesso/erro
```

### 3️⃣ Listar Expandida para Produção
```
1. Carrega produto principal
2. Carrega todas as composições recursivamente
3. Expande até produtos finais (não compostos)
4. Calcula quantidades totais
5. Retorna estrutura hierárquica
```

## 📡 Endpoints da API

### Básico
```
GET    /api/v1/produtos              # Listar produtos
GET    /api/v1/produtos/{id}         # Obter por ID
POST   /api/v1/produtos              # Criar novo
PUT    /api/v1/produtos/{id}         # Atualizar
DELETE /api/v1/produtos/{id}         # Deletar
```

### Status
```
PATCH  /api/v1/produtos/{id}/ativar      # Ativar
PATCH  /api/v1/produtos/{id}/inativar    # Inativar
PATCH  /api/v1/produtos/{id}/bloquear    # Bloquear
```

### Composição
```
POST   /api/v1/produtos/{id}/composicao           # Adicionar composição
GET    /api/v1/produtos/{id}/composicao           # Listar composições
DELETE /api/v1/produtos/{id}/composicao/{compId}  # Remover composição
```

### Listagem Expandida
```
GET    /api/v1/produtos/{id}/composicao/expandida  # Hierarquia completa
GET    /api/v1/produtos/expandida/producao         # Lista para produção
```

## ✅ Validações

### Ao Criar
- ✓ Código não pode estar em branco
- ✓ Código deve ser único
- ✓ Descrição não pode estar em branco
- ✓ Tipo de produto deve ser válido
- ✓ Unidade de medida deve existir

### Ao Atualizar Composição
- ✓ Produto pai deve existir
- ✓ Produto filho deve existir
- ✓ Quantidade deve ser positiva
- ✓ Não pode referenciar a si mesmo
- ✓ Unidade de medida deve ser compatível

### Ao Bloquear/Deletar
- ✓ Não pode bloquear produto com composições ativas
- ✓ Não pode deletar se está sendo usado em vendas
- ✓ Permissão deve estar ativa

## 🧪 Testes

```java
// Tests em src/test/java/com/api/erp/features/produto/

ProdutoEntityTest.java              // Testes da entidade
ProdutoServiceTest.java             // Testes de domínio
ComposicaoProdutoServiceTest.java   // Testes de composição
CriarProdutoServiceTest.java        // Testes de aplicação
ProdutoControllerTest.java          // Testes de API
```

## 🔗 Relacionamentos

```
Produto
    ├── UnidadeMedida (muitos-para-um)
    ├── ProdutoComposicao (um-para-muitos)
    ├── ClassificacaoFiscal (um-para-um)
    ├── ProdutoPermissions (um-para-muitos)
    └── ListaExpandidaPermissions (um-para-muitos)
```

## 🚀 Boas Práticas

1. **Sempre validar código único** antes de persistir
2. **Usar factory ou service** para criar entidades
3. **Verificar permissões** antes de operações críticas
4. **Lazy load** para composições em listas grandes
5. **Usar DTOs** para transferência de dados
6. **Adicionar audit** (criação/atualização) automaticamente
7. **Ativar produto** deve ser ação explícita
8. **Documentar NCM** corretamente para emissão de NF

## 📚 Referências Relacionadas

- [FEATURE_UNIDADEMEDIDA.md](FEATURE_UNIDADEMEDIDA.md) - Unidades de medida
- [FEATURE_EMPRESA.md](FEATURE_EMPRESA.md) - Contexto de empresa
- [CAMADA_DOMAIN.md](CAMADA_DOMAIN.md) - Padrões de domínio
- [SEGURANCA.md](SEGURANCA.md) - Autorizações

---

**Última atualização:** Dezembro de 2025
