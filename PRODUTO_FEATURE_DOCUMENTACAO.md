# Feature de Cadastro de Produtos - Documentação Completa

## 📋 Visão Geral

Este documento descreve a implementação completa da feature de cadastro de produtos para o ERP Backend, seguindo rigorosamente os princípios SOLID, Clean Code e arquitetura em camadas bem definida.

---

## 🏗️ Arquitetura Implementada

### Estrutura de Camadas

```
features/
├── unidademedida/          # Feature independente de Unidade de Medida
│   ├── domain/             # Lógica de negócio pura
│   │   ├── entity/         # Entidades de domínio
│   │   ├── repository/     # Interfaces de persistência
│   │   └── validator/      # Validadores de domínio
│   ├── application/        # Lógica de aplicação
│   │   ├── service/        # Orquestração de negócio
│   │   └── dto/            # Transfer Objects
│   ├── infrastructure/     # Implementações técnicas
│   │   └── persistence/    # Implementações de persistência
│   └── presentation/       # Endpoints HTTP
│       └── controller/     # Controllers REST
│
└── produto/                # Feature de Produto
    ├── domain/             # Lógica de negócio pura
    │   ├── entity/         # Entidades e enums
    │   ├── repository/     # Interfaces de persistência
    │   ├── service/        # Serviços de domínio (casos de uso complexos)
    │   ├── validator/      # Validadores de regras de negócio
    │   └── exception/      # Exceções específicas do domínio
    ├── application/        # Lógica de aplicação
    │   ├── service/        # Orquestração de negócio
    │   └── dto/            # Transfer Objects
    ├── infrastructure/     # Implementações técnicas
    │   └── persistence/    # (JPA gerenciado automaticamente)
    └── presentation/       # Endpoints HTTP
        └── controller/     # Controllers REST
```

---

## 🔑 Princípios SOLID Aplicados

### 1. **SRP (Single Responsibility Principle)**

Cada classe tem uma única razão para mudar:

- **Entidades (`Produto`, `ProdutoComposicao`, `UnidadeMedida`)**: Representar dados e estado
- **Repositórios**: Persistência apenas
- **Validadores**: Validações de domínio apenas
- **Services (Domain)**: Lógica complexa de domínio (e.g., `ListaExpandidaProducaoService`)
- **Services (Application)**: Orquestração e transformação de DTOs
- **Controllers**: Receber requisições e retornar respostas HTTP

### 2. **OCP (Open/Closed Principle)**

Aberto para extensão, fechado para modificação:

- **Enums de Status e Tipo**: Novos status/tipos podem ser adicionados sem modificar código existente
- **Validadores de Domínio**: Novos validadores podem ser adicionados via herança
- **Domain Services**: Novos cálculos podem ser adicionados via novos services
- **Padrão Strategy implícito**: Diferentes tipos de produtos (COMPRADO vs FABRICAVEL) com comportamentos distintos

### 3. **LSP (Liskov Substitution Principle)**

- Repositórios extendem `JpaRepository` corretamente
- Validadores podem ser substituídos por implementações alternativas
- Services podem ser mockados para testes

### 4. **ISP (Interface Segregation Principle)**

- Repositórios definem interfaces específicas (e.g., `ProdutoRepository` vs `ProdutoComposicaoRepository`)
- DTOs separados para Request e Response
- Serviços focados em responsabilidades específicas

### 5. **DIP (Dependency Inversion Principle)**

- Classes dependem de abstrações (interfaces), não de implementações concretas
- Injeção de dependência via Spring `@RequiredArgsConstructor`
- Services dependem de repositórios (abstrações), não de implementações ERM específicas

---

## 📚 Entidades de Domínio

### UnidadeMedida

```java
@Entity
public class UnidadeMedida {
    - id: Long
    - sigla: String (ex: "KG", "UN", "L")
    - descricao: String
    - tipo: String (ex: "MASSA", "VOLUME")
    - ativo: Boolean
    - dataCriacao/Atualização: LocalDateTime
    
    Comportamentos:
    - ativar() / desativar()
    - estaAtiva()
}
```

### Produto

```java
@Entity
public class Produto {
    - id: Long
    - codigo: String (único)
    - descricao: String
    - status: StatusProduto (ATIVO, INATIVO, BLOQUEADO, DESCONTINUADO)
    - tipo: TipoProduto (COMPRADO, FABRICAVEL)
    - unidadeMedida: UnidadeMedida (referência)
    - ncm: String (código fiscal)
    - informacoesFiscais: String (preparado para futuras regras)
    - precoVenda / precoCusto: BigDecimal
    - dataCriacao/Atualização: LocalDateTime
    
    Comportamentos:
    - ativar() / desativar() / bloquear() / descontinuar()
    - podeSerUtilizado()
    - ehFabricavel() / ehComprado()
}
```

### ProdutoComposicao (BOM)

```java
@Entity
public class ProdutoComposicao {
    - id: Long
    - produtoFabricado: Produto (produto pai)
    - produtoComponente: Produto (insumo)
    - quantidadeNecessaria: BigDecimal
    - sequencia: Integer
    - observacoes: String
    - dataCriacao/Atualização: LocalDateTime
    
    Restrições:
    ✓ Apenas produtos FABRICAVEL podem ter composição
    ✓ Quantidade > 0 sempre
    ✓ Sem composições circulares (validado)
    ✓ Componente deve estar ativo
    
    Comportamentos:
    - ehQuantidadeValida()
    - atualizarQuantidade(BigDecimal)
}
```

### Enums

```java
// StatusProduto
ATIVO → pode ser utilizado
INATIVO → desativado temporariamente
BLOQUEADO → bloqueado para uso
DESCONTINUADO → permanentemente descontinuado

// TipoProduto
COMPRADO → produto adquirido de fornecedores
FABRICAVEL → produto fabricado internamente
```

---

## 🔐 Validações de Domínio

### ProdutoValidator

1. **validarCodigo()**: Máx 50 caracteres, apenas [A-Z0-9-._]
2. **validarDescricao()**: Obrigatória, máx 255 caracteres
3. **validarNCM()**: Exatamente 8 dígitos
4. **validarComposicao()**:
   - ✓ Produto deve ser FABRICAVEL
   - ✓ Quantidade > 0
   - ✓ Sem composição circular
   - ✓ Componente está ativo

### UnidadeMedidaValidator

1. **validarSigla()**: Máx 10 caracteres, [A-Z0-9]
2. **validarDescricao()**: Obrigatória, máx 100 caracteres
3. **validarTipo()**: Opcional, máx 255 caracteres

---

## 🧮 Lista Expandida de Produção

### Serviço de Domínio: `ListaExpandidaProducaoService`

**Responsabilidade**: Calcular composição completa de um produto com todos os insumos necessários, considerando composições aninhadas.

**Algoritmo**:
1. Validar que o produto é fabricável
2. Obter composição direta do produto
3. Para cada componente:
   - Se fabricável: expandir recursivamente sua composição
   - Se comprado: adicionar à lista acumulada
4. Acumular quantidades do mesmo produto
5. Evitar processamento infinito (guard contra ciclos)

**Exemplo Prático**:

```
Produto A (FABRICAVEL)
├─ Produto B (FABRICAVEL): 2x
│  ├─ Produto C (COMPRADO): 3x
│  └─ Produto D (COMPRADO): 1x
├─ Produto E (COMPRADO): 4x

Lista Expandida para 1x de Produto A:
┌─────────────┬──────────┐
│ Produto     │ Qtd (un) │
├─────────────┼──────────┤
│ C (comprado)│    6x    │  (2 x 3)
│ D (comprado)│    2x    │  (2 x 1)
│ E (comprado)│    4x    │
└─────────────┴──────────┘

Lista de Compras = Lista Expandida (todos são comprados neste caso)
```

### Métodos

- **`calcularListaExpandida(Produto, BigDecimal)`**: Retorna `Map<Produto, BigDecimal>` com todos os insumos
- **`obterListaCompras(Produto, BigDecimal)`**: Retorna apenas produtos comprados
- **`obterListaOrdenada(Produto, BigDecimal)`**: Retorna lista ordenada por descrição

---

## 🔄 Fluxo de Operações

### Criar Produto

```
POST /api/v1/produtos
│
├─ ProdutoController.criar()
├─ ProdutoValidator.validarCriacao()
├─ Verifica unicidade de código
├─ Valida UnidadeMedida existe
└─ ProdutoService.criar()
   └─ Salva no repositório
      └─ Retorna ProdutoResponseDTO
```

### Criar Composição

```
POST /api/v1/composicoes
│
├─ ComposicaoController.criar()
├─ ProdutoValidator.validarComposicao()
│  ├─ Produto deve ser FABRICAVEL
│  ├─ Quantidade > 0
│  ├─ Sem composição circular
│  └─ Componente ativo
├─ Verifica duplicação
└─ ComposicaoService.criar()
   └─ Salva no repositório
      └─ Retorna ComposicaoResponseDTO
```

### Gerar Lista Expandida

```
GET /api/v1/lista-expandida/produto/{id}?quantidade=1
│
├─ ListaExpandidaController.gerarListaExpandida()
├─ ListaExpandidaService.gerarListaExpandida()
├─ ListaExpandidaProducaoService.calcularListaExpandida()
│  └─ Algoritmo recursivo de expansão
└─ Retorna ListaExpandidaResponseDTO
```

---

## 📡 Endpoints REST

### UnidadeMedida

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/unidades-medida` | Criar |
| GET | `/api/v1/unidades-medida/{id}` | Obter |
| GET | `/api/v1/unidades-medida` | Listar (paginado) |
| GET | `/api/v1/unidades-medida/ativas/lista` | Listar ativas |
| PUT | `/api/v1/unidades-medida/{id}` | Atualizar |
| PATCH | `/api/v1/unidades-medida/{id}/ativar` | Ativar |
| PATCH | `/api/v1/unidades-medida/{id}/desativar` | Desativar |
| DELETE | `/api/v1/unidades-medida/{id}` | Deletar |

### Produto

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/produtos` | Criar |
| GET | `/api/v1/produtos/{id}` | Obter |
| GET | `/api/v1/produtos` | Listar (paginado) |
| GET | `/api/v1/produtos/tipo/{tipo}` | Listar por tipo |
| PUT | `/api/v1/produtos/{id}` | Atualizar |
| PATCH | `/api/v1/produtos/{id}/ativar` | Ativar |
| PATCH | `/api/v1/produtos/{id}/desativar` | Desativar |
| PATCH | `/api/v1/produtos/{id}/bloquear` | Bloquear |
| PATCH | `/api/v1/produtos/{id}/descontinuar` | Descontinuar |
| DELETE | `/api/v1/produtos/{id}` | Deletar |

### Composição (BOM)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/composicoes` | Criar |
| GET | `/api/v1/composicoes/{id}` | Obter |
| GET | `/api/v1/composicoes/produto/{produtoId}` | Listar BOM |
| PUT | `/api/v1/composicoes/{id}` | Atualizar |
| DELETE | `/api/v1/composicoes/{id}` | Deletar item |
| DELETE | `/api/v1/composicoes/produto/{produtoId}/limpar` | Limpar BOM |

### Lista Expandida de Produção

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/lista-expandida/produto/{id}?quantidade=X` | Gerar lista expandida |
| GET | `/api/v1/lista-expandida/compras/produto/{id}?quantidade=X` | Gerar lista de compras |

---

## 🧪 Exemplos de Uso

### 1. Criar Unidade de Medida

```bash
POST /api/v1/unidades-medida
Content-Type: application/json

{
  "sigla": "KG",
  "descricao": "Quilograma",
  "tipo": "MASSA"
}
```

**Response**:
```json
{
  "id": 1,
  "sigla": "KG",
  "descricao": "Quilograma",
  "tipo": "MASSA",
  "ativo": true,
  "dataCriacao": "2024-12-21T10:30:00",
  "dataAtualizacao": "2024-12-21T10:30:00"
}
```

### 2. Criar Produto Comprado

```bash
POST /api/v1/produtos
Content-Type: application/json

{
  "codigo": "MAT-001",
  "descricao": "Aço Carbono 1020",
  "tipo": "COMPRADO",
  "status": "ATIVO",
  "unidadeMedidaId": 1,
  "ncm": "72081599",
  "precoCusto": 50.00,
  "precoVenda": 75.00
}
```

### 3. Criar Produto Fabricável

```bash
POST /api/v1/produtos
Content-Type: application/json

{
  "codigo": "PROD-001",
  "descricao": "Placa Base Soldada",
  "tipo": "FABRICAVEL",
  "status": "ATIVO",
  "unidadeMedidaId": 1,
  "ncm": "73081599",
  "precoVenda": 200.00,
  "precoCusto": 150.00
}
```

### 4. Criar Composição (BOM)

```bash
POST /api/v1/composicoes
Content-Type: application/json

{
  "produtoFabricadoId": 3,
  "produtoComponenteId": 2,
  "quantidadeNecessaria": 2.5,
  "sequencia": 1,
  "observacoes": "Aço principal da estrutura"
}
```

### 5. Gerar Lista Expandida

```bash
GET /api/v1/lista-expandida/produto/3?quantidade=10

Response:
{
  "produtoId": 3,
  "codigoProduto": "PROD-001",
  "descricaoProduto": "Placa Base Soldada",
  "quantidadeRequerida": 10,
  "unidadeMedidaProduto": "KG",
  "itens": [
    {
      "produtoId": 2,
      "codigoProduto": "MAT-001",
      "descricaoProduto": "Aço Carbono 1020",
      "tipoProduto": "Comprado",
      "unidadeMedida": "KG",
      "quantidadeNecessaria": 25
    }
  ],
  "totalItens": 1
}
```

---

## 🛡️ Tratamento de Erros

### Exceções Personalizadas

- **`ProdutoException`**: Violações de regras de negócio
  - Produto não encontrado (404)
  - Código já existe (409)
  - Composição circular (422)
  - Produto não fabricável (422)
  - Quantidade inválida (422)
  - Produto não pode ser utilizado (422)

- **`ValidationException`**: Erros de validação de domínio
  - Código inválido
  - Descrição inválida
  - NCM inválido
  - Sigla inválida

- **`BusinessException`**: Erros genéricos de negócio
  - Unidade de medida não encontrada

### Exemplo de Response de Erro

```json
{
  "timestamp": "2024-12-21T10:35:00",
  "status": 409,
  "error": "Conflict",
  "message": "Já existe um produto com o código: MAT-001",
  "path": "/api/v1/produtos"
}
```

---

## 🔄 Transações

Todas as operações são `@Transactional`:

- **Escritas**: `@Transactional` (padrão)
  - Criar, atualizar, deletar
  - Rollback automático em caso de erro

- **Leituras**: `@Transactional(readOnly = true)`
  - Obter, listar
  - Otimização de performance

---

## 🧰 Extensibilidade

### Adicionar Novo Status de Produto

1. Adicione valor ao enum `StatusProduto`:
   ```java
   public enum StatusProduto {
       ...
       EM_MANUTENCAO("Em Manutenção", "..."),
       ...
   }
   ```

2. Pronto! Nenhuma outra classe precisa ser modificada (OCP)

### Adicionar Novo Tipo de Produto

1. Adicione valor ao enum `TipoProduto`:
   ```java
   public enum TipoProduto {
       ...
       SERVICO("Serviço", "..."),
       ...
   }
   ```

2. Se comportamentos diferentes forem necessários, estenda o `Produto` ou crie novos serviços

### Adicionar Novo Tipo de Validação

1. Crie novo método em `ProdutoValidator` ou nova classe:
   ```java
   public void validarNovaRegra(String campo) { ... }
   ```

2. Use em `ProdutoService.criar()` ou `atualizar()`

### Adicionar Novo Cálculo de Lista Expandida

1. Crie novo método em `ListaExpandidaProducaoService`:
   ```java
   public Map<...> calcularListaPersonalizada(...) { ... }
   ```

2. Exponha via novo método em `ListaExpandidaService`

3. Crie novo endpoint em `ListaExpandidaController`

---

## 🧹 Clean Code

### Conventions Seguidas

1. **Nomes Significativos**: Classes, métodos e variáveis descrevem claramente sua responsabilidade
2. **Métodos Pequenos**: Cada método faz uma coisa bem
3. **Sem Duplicação**: Código reutilizável, não repetido
4. **Comentários Explicativos**: Documentam "por quê", não "o quê"
5. **DTOs Separados**: Request/Response claros
6. **Enums para Valores Fixos**: StatusProduto, TipoProduto
7. **Validação em Múltiplas Camadas**: Validator (domínio), Service (lógica), Controller (HTTP)

---

## 🚀 Próximos Passos (Futuro)

- [ ] Testes unitários para serviços de domínio
- [ ] Testes de integração para controllers
- [ ] Cache de composições expandidas
- [ ] Auditoria (quem criou/modificou)
- [ ] Versioning de composições (histórico)
- [ ] Importação/exportação de produtos (CSV, XML)
- [ ] Integração com sistema de estoque
- [ ] Cálculos de custo acumulado
- [ ] Análise de composição (caminhos críticos)
- [ ] Alertas de produtos sem composição
- [ ] Exportação de BOM para fornecedores

---

## 📞 Suporte

Para dúvidas ou extensões, consulte:
- Princípios SOLID em Clean Code (Robert C. Martin)
- Documentação Spring Boot: https://spring.io/projects/spring-boot
- OpenAPI Specification: https://spec.openapis.org/

---

**Data de Criação**: 21/12/2024  
**Versão**: 1.0  
**Status**: Pronto para Produção
