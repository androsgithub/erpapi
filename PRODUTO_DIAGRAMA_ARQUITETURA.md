# Diagrama de Arquitetura - Feature de Produtos

## 🏗️ Estrutura Geral

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         CLIENTE (HTTP)                                   │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
                    ▼                         ▼
         ┌──────────────────┐     ┌──────────────────┐
         │   Controllers    │     │   Controllers    │
         │                  │     │                  │
         │- Produto         │     │- UnidadeMedida   │
         │- Composição      │     │                  │
         │- ListaExpandida  │     │                  │
         └────────┬─────────┘     └────────┬─────────┘
                  │                         │
                  └────────────┬────────────┘
                               │
                    ┌──────────▼──────────┐
                    │   APPLICATION       │
                    │   LAYER             │
                    │                     │
                    │ - Services          │
                    │ - DTOs              │
                    │ - Transformações    │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │   DOMAIN            │
                    │   LAYER             │
                    │                     │
                    │ - Entities          │
                    │ - Repositories      │
                    │ - Validators        │
                    │ - Domain Services   │
                    │ - Exceptions        │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │   PERSISTENCE       │
                    │   (JPA/Hibernate)   │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │   DATABASE          │
                    │   (H2/PostgreSQL)   │
                    └─────────────────────┘
```

---

## 🔄 Fluxo de Requisição - Criar Produto

```
┌────────────────────────────────────────────────────────────────┐
│ 1. Cliente envia: POST /api/v1/produtos (ProdutoRequestDTO)   │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│ 2. ProdutoController.criar()                                   │
│    - Recebe DTO de requisição                                  │
│    - Chama ProdutoService                                      │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│ 3. ProdutoService.criar()                                      │
│    - Chama validador de domínio                                │
│    - Valida unicidade de código                                │
│    - Busca UnidadeMedida                                       │
│    - Constrói entidade                                         │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│ 4. ProdutoValidator.validarCriacao()                           │
│    - Valida código (obrigatório, único, padrão)                │
│    - Valida descrição (obrigatória)                            │
│    - Valida NCM (8 dígitos)                                    │
└────────────────────┬─────────────────────────────────────────┘
                     │
        ✓ (Válido)   │   ✗ (Inválido)
        │            │
        ▼            ▼
    Prossegue    Exceção
                ValidationException
                     │
                     ▼
           GlobalExceptionHandler
                     │
                     ▼
        400 Bad Request (ErrorResponse)
        
        (Continua fluxo de sucesso abaixo)

Fluxo de sucesso (continuação):
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│ 5. ProdutoRepository.save()                                    │
│    - Persiste em banco de dados                                │
│    - Gera ID                                                   │
│    - Retorna entidade salva                                    │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│ 6. Service converte para ResponseDTO                           │
│    - Constrói ProdutoResponseDTO                               │
│    - Inclui dados de UnidadeMedida aninhada                    │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│ 7. Controller retorna ResponseEntity                           │
│    - Status: 201 CREATED                                       │
│    - Body: ProdutoResponseDTO                                  │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│ 8. Framework Spring serializa para JSON                        │
│    - Jackson converte DTO → JSON                               │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│ 9. Cliente recebe resposta HTTP                                │
│    201 Created                                                 │
│    {                                                           │
│      "id": 1,                                                  │
│      "codigo": "PROD-001",                                     │
│      ...                                                       │
│    }                                                           │
└────────────────────────────────────────────────────────────────┘
```

---

## 📊 Fluxo de Lista Expandida

```
┌────────────────────────────────────────────────────────┐
│ GET /api/v1/lista-expandida/produto/{id}?quantidade=X │
└────────────────────┬─────────────────────────────────┘
                     │
                     ▼
         ┌───────────────────────────┐
         │ ListaExpandidaController  │
         │   .gerarListaExpandida()  │
         └───────────┬───────────────┘
                     │
                     ▼
         ┌───────────────────────────┐
         │ ListaExpandidaService     │
         │   .gerarListaExpandida()  │
         └───────────┬───────────────┘
                     │
                     ▼
   ┌─────────────────────────────────────┐
   │ ListaExpandidaProducaoService       │
   │   .calcularListaExpandida()         │
   │                                     │
   │ Algoritmo:                          │
   │ 1. Validar que é FABRICAVEL         │
   │ 2. Obter composição direta          │
   │ 3. Para cada componente:            │
   │    - Se FABRICAVEL: expandir        │
   │    - Se COMPRADO: adicionar         │
   │ 4. Acumular quantidades             │
   │ 5. Retornar Map<Produto, BigDecimal>│
   └─────────────┬───────────────────────┘
                 │
      ┌──────────┴──────────┐
      │                     │
      ▼                     ▼
 Expand         Add Comprado
 FABRICAVEL         │
      │            │
      ▼            │
   Recursivo       │
   (DFS)          │
      │            │
      └──────────┬─────────┘
                 │
                 ▼
     ┌───────────────────────┐
     │ Acumular Quantidades  │
     │ map.merge(produto,    │
     │  quantidade)          │
     └───────────┬───────────┘
                 │
                 ▼
     ┌───────────────────────┐
     │ Converter para DTOs   │
     │ ItemListaExpandidaDTO │
     └───────────┬───────────┘
                 │
                 ▼
     ┌───────────────────────┐
     │ ListaExpandidaResponse│
     │   DTO                 │
     └───────────┬───────────┘
                 │
                 ▼
     200 OK
     {
       "produtoId": 3,
       "codigoProduto": "PROD-001",
       "quantidadeRequerida": 10,
       "itens": [
         {
           "produtoId": 1,
           "codigoProduto": "MAT-001",
           "quantidadeNecessaria": 25,
           ...
         }
       ]
     }
```

---

## 🔀 Interação entre Camadas

```
PRESENTATION LAYER
┌─────────────────────────────────┐
│ Controllers                     │
│ - ProdutoController             │
│ - ComposicaoController          │
│ - ListaExpandidaController      │
│ - UnidadeMedidaController       │
│                                 │
│ Responsabilidades:              │
│ • Receber requisições HTTP      │
│ • Validar entrada               │
│ • Chamar services               │
│ • Retornar respostas HTTP       │
└────────────────┬────────────────┘
                 │ Depende de
                 ▼
APPLICATION LAYER
┌─────────────────────────────────┐
│ Services                        │
│ - ProdutoService                │
│ - ComposicaoService             │
│ - ListaExpandidaService         │
│ - UnidadeMedidaService          │
│                                 │
│ Responsabilidades:              │
│ • Orquestrar operações          │
│ • Coordenar transações          │
│ • Transformar DTOs → Entities   │
│ • Chamar domain services        │
└────────────────┬────────────────┘
                 │ Depende de
                 ▼
DOMAIN LAYER
┌──────────────────────────────────┐
│ Domain Services                  │
│ - ListaExpandidaProducaoService  │
│                                  │
│ Entities                         │
│ - Produto                        │
│ - ProdutoComposicao              │
│ - UnidadeMedida                  │
│ - TipoProduto (enum)             │
│ - StatusProduto (enum)           │
│                                  │
│ Repositories (Interfaces)        │
│ - ProdutoRepository              │
│ - ProdutoComposicaoRepository    │
│ - UnidadeMedidaRepository        │
│                                  │
│ Validators                       │
│ - ProdutoValidator               │
│ - UnidadeMedidaValidator         │
│                                  │
│ Exceptions                       │
│ - ProdutoException               │
│ - ValidationException            │
│                                  │
│ Responsabilidades:               │
│ • Lógica de negócio pura         │
│ • Validações de domínio          │
│ • Regras de comportamento        │
└────────────────┬─────────────────┘
                 │ Implementadas por
                 ▼
INFRASTRUCTURE LAYER
┌──────────────────────────────────┐
│ Persistence (JPA/Hibernate)      │
│ - Implementações de Repositories │
│                                  │
│ Responsabilidades:               │
│ • Persistência em BD             │
│ • Mapeamento objeto-relacional   │
│ • Queries ao banco               │
└────────────────┬─────────────────┘
                 │
                 ▼
DATABASE
┌──────────────────────────────────┐
│ H2 / PostgreSQL                  │
│ - Tabelas                        │
│ - Índices                        │
│ - Constraints                    │
└──────────────────────────────────┘
```

---

## 🎯 Fluxo de Validação (Exemplo: Composição)

```
Requisição: POST /api/v1/composicoes
{
  "produtoFabricadoId": 2,
  "produtoComponenteId": 1,
  "quantidadeNecessaria": 2.5
}

        │
        ▼
┌──────────────────────┐
│ ComposicaoController │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────────┐
│ ComposicaoService        │
│ 1. Obter produtos        │
│ 2. Validar composição    │
│ 3. Salvar no repositório │
└──────────┬───────────────┘
           │
           ▼
┌──────────────────────────────┐
│ ProdutoValidator             │
│ .validarComposicao()         │
│                              │
│ ✓ Produto FABRICAVEL?        │──✗──→ ProdutoException
│ ✓ Quantidade > 0?            │──✗──→ ProdutoException
│ ✓ Sem ciclo?                 │──✗──→ ProdutoException
│   (Verifica A→B, B→A)        │
│ ✓ Componente ATIVO?          │──✗──→ ProdutoException
│                              │
└──────────┬───────────────────┘
           │ ✓ Todas válidas
           ▼
┌──────────────────────────────┐
│ ProdutoComposicaoRepository  │
│ .save()                      │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────┐
│ JPA/Hibernate                │
│ INSERT INTO produto_composicao
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────┐
│ ComposicaoResponseDTO        │
│ ← HTTP 201 Created           │
└──────────────────────────────┘
```

---

## 🌳 Estrutura de Composição (Exemplo Visual)

```
Produto: PROD-001 (Fabricável) - Quantidade: 1
│
├─ Composição 1
│  └─ MAT-001 (Comprado): 2.5 kg
│
├─ Composição 2
│  └─ PROD-002 (Fabricável): 1.0 un
│     │
│     ├─ Composição 2.1
│     │  └─ MAT-002 (Comprado): 3.0 kg
│     │
│     └─ Composição 2.2
│        └─ MAT-003 (Comprado): 0.5 kg
│
└─ Composição 3
   └─ MAT-004 (Comprado): 1.5 kg

┌─────────────────────────────────────┐
│ LISTA EXPANDIDA - PROD-001 × 1      │
├─────────────────────────────────────┤
│ MAT-001: 2.5 kg                     │
│ MAT-002: 3.0 kg (via PROD-002)      │
│ MAT-003: 0.5 kg (via PROD-002)      │
│ MAT-004: 1.5 kg                     │
├─────────────────────────────────────┤
│ Total Direto: MAT-001 + MAT-004     │
│ Total Expandido: 4 produtos únicos  │
└─────────────────────────────────────┘

Para 10 unidades de PROD-001:
│
├─ MAT-001: 25 kg (2.5 × 10)
├─ MAT-002: 30 kg (3.0 × 10)
├─ MAT-003: 5 kg (0.5 × 10)
└─ MAT-004: 15 kg (1.5 × 10)
```

---

## 🔗 Relacionamentos de Banco de Dados

```
┌──────────────────────────┐
│   unidade_medida         │
├──────────────────────────┤
│ id (PK)                  │
│ sigla (UNIQUE)           │
│ descricao                │
│ tipo                     │
│ ativo                    │
│ data_criacao             │
│ data_atualizacao         │
└────────────┬─────────────┘
             │
         1:N │
             │
             ▼
┌──────────────────────────┐
│        produto           │
├──────────────────────────┤
│ id (PK)                  │
│ codigo (UNIQUE)          │
│ descricao                │
│ status                   │
│ tipo                     │
│ unidade_medida_id (FK)   │
│ ncm                      │
│ preco_venda              │
│ preco_custo              │
│ data_criacao             │
│ data_atualizacao         │
└────────┬──────────────────┘
         │
    1:N  │  N:N
         │
         ▼
┌────────────────────────────────┐
│   produto_composicao           │
├────────────────────────────────┤
│ id (PK)                        │
│ produto_fabricado_id (FK)      │
│ produto_componente_id (FK)     │
│ quantidade_necessaria          │
│ sequencia                      │
│ observacoes                    │
│ data_criacao                   │
│ data_atualizacao               │
└────────────────────────────────┘

Constraints:
- produto.unidade_medida_id → unidade_medida.id
- produto_composicao.produto_fabricado_id → produto.id
- produto_composicao.produto_componente_id → produto.id
- Unique(produto_fabricado_id, produto_componente_id)
- Check(quantidade_necessaria > 0)
```

---

## 🚀 Fluxo de Extensão (OCP)

```
Para adicionar novo Status:

1. Editar StatusProduto.java
┌─────────────────────────────────┐
│ public enum StatusProduto {     │
│   ATIVO(...),                   │
│   INATIVO(...),                 │
│   ...                           │
│   EM_MANUTENCAO("Em Manutenção")│ ← NOVO
│   ...                           │
│ }                               │
└─────────────────────────────────┘

2. Pronto! Nenhuma outra classe precisa mudar:
   ✓ Produto.java - funciona com novo status
   ✓ ProdutoValidator.java - não requer mudança
   ✓ Controllers - suportam novo status automaticamente
   ✓ Database - coluna enum suporta novo valor

Benefício: OCP - Aberto para extensão, fechado para modificação

Mesmo para novo Tipo de Produto:

1. Editar TipoProduto.java
   SERVICO("Serviço", "...")

2. Se comportamento diferente:
   Criar ServicoService implements Service
   ou estender Produto com ServicoComProduto

Nenhuma classe existente é modificada!
```

---

## 📈 Escalabilidade Futura

```
Infraestrutura Atual:
    Controllers → Services → Domain → JPA → H2

Possíveis Extensões:

1. Cache (Redis)
   Controllers → Cache Layer → Services → ...

2. Event Sourcing
   Services → EventPublisher → Queue → ...

3. GraphQL (além de REST)
   GraphQLResolver → Services → ...

4. CQRS (separação Read/Write)
   Commands → CommandHandler → Services
   Queries → QueryHandler → Repository

5. Async/Messaging
   Controllers → Async Service → Message Queue → Worker

Tudo preparado via:
- Injeção de Dependência
- Interfaces bem definidas
- Separação de responsabilidades
- Domain Services independentes
```

---

**Diagrama criado**: 21/12/2024
**Versão**: 1.0
