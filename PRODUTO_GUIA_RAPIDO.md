# Feature de Produtos do ERP - Guia de Uso Rápido

## 🚀 Início Rápido

### 1. Criar uma Unidade de Medida

```bash
curl -X POST http://localhost:8080/api/v1/unidades-medida \
  -H "Content-Type: application/json" \
  -d '{
    "sigla": "KG",
    "descricao": "Quilograma",
    "tipo": "MASSA"
  }'
```

### 2. Criar um Produto Comprado

```bash
curl -X POST http://localhost:8080/api/v1/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "MAT-AÇO-001",
    "descricao": "Aço Carbono 1020",
    "tipo": "COMPRADO",
    "status": "ATIVO",
    "unidadeMedidaId": 1,
    "ncm": "72081599",
    "precoCusto": 50.00,
    "precoVenda": 75.00
  }'
```

### 3. Criar um Produto Fabricável

```bash
curl -X POST http://localhost:8080/api/v1/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "PROD-PLACA-001",
    "descricao": "Placa Base Soldada",
    "descricaoDetalhada": "Placa base principal do equipamento",
    "tipo": "FABRICAVEL",
    "status": "ATIVO",
    "unidadeMedidaId": 1,
    "ncm": "73081599",
    "precoVenda": 200.00,
    "precoCusto": 150.00
  }'
```

**Response** (exemplo):
```json
{
  "id": 2,
  "codigo": "PROD-PLACA-001",
  "descricao": "Placa Base Soldada",
  "status": "ATIVO",
  "tipo": "FABRICAVEL",
  "unidadeMedida": {
    "id": 1,
    "sigla": "KG",
    "descricao": "Quilograma"
  },
  "ncm": "73081599",
  "precoVenda": 200.00,
  "precoCusto": 150.00,
  "dataCriacao": "2024-12-21T10:30:00",
  "dataAtualizacao": "2024-12-21T10:30:00"
}
```

### 4. Criar uma Composição (BOM)

```bash
curl -X POST http://localhost:8080/api/v1/composicoes \
  -H "Content-Type: application/json" \
  -d '{
    "produtoFabricadoId": 2,
    "produtoComponenteId": 1,
    "quantidadeNecessaria": 2.5,
    "sequencia": 1,
    "observacoes": "Aço principal para solda"
  }'
```

### 5. Gerar Lista Expandida de Produção

```bash
curl -X GET "http://localhost:8080/api/v1/lista-expandida/produto/2?quantidade=10"
```

**Response**:
```json
{
  "produtoId": 2,
  "codigoProduto": "PROD-PLACA-001",
  "descricaoProduto": "Placa Base Soldada",
  "quantidadeRequerida": 10,
  "unidadeMedidaProduto": "KG",
  "itens": [
    {
      "produtoId": 1,
      "codigoProduto": "MAT-AÇO-001",
      "descricaoProduto": "Aço Carbono 1020",
      "tipoProduto": "Comprado",
      "unidadeMedida": "KG",
      "quantidadeNecessaria": 25
    }
  ],
  "totalItens": 1
}
```

### 6. Gerar Lista de Compras

```bash
curl -X GET "http://localhost:8080/api/v1/lista-expandida/compras/produto/2?quantidade=10"
```

---

## 📚 Casos de Uso Comuns

### Ativar/Desativar um Produto

```bash
# Ativar
curl -X PATCH http://localhost:8080/api/v1/produtos/2/ativar

# Desativar
curl -X PATCH http://localhost:8080/api/v1/produtos/2/desativar

# Bloquear
curl -X PATCH http://localhost:8080/api/v1/produtos/2/bloquear

# Descontinuar
curl -X PATCH http://localhost:8080/api/v1/produtos/2/descontinuar
```

### Listar Produtos

```bash
# Todos (paginado)
curl -X GET "http://localhost:8080/api/v1/produtos?page=0&size=20&sortBy=descricao"

# Por tipo
curl -X GET "http://localhost:8080/api/v1/produtos/tipo/FABRICAVEL?page=0&size=20"
```

### Obter Detalhes

```bash
# Produto
curl -X GET http://localhost:8080/api/v1/produtos/2

# BOM (composições de um produto)
curl -X GET http://localhost:8080/api/v1/composicoes/produto/2

# Unidade de medida
curl -X GET http://localhost:8080/api/v1/unidades-medida/1
```

### Atualizar Dados

```bash
# Produto
curl -X PUT http://localhost:8080/api/v1/produtos/2 \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "PROD-PLACA-001",
    "descricao": "Placa Base Soldada Revisão 2",
    "tipo": "FABRICAVEL",
    "status": "ATIVO",
    "unidadeMedidaId": 1,
    "ncm": "73081599",
    "precoVenda": 220.00,
    "precoCusto": 160.00
  }'

# Composição
curl -X PUT http://localhost:8080/api/v1/composicoes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "produtoFabricadoId": 2,
    "produtoComponenteId": 1,
    "quantidadeNecessaria": 3.0,
    "sequencia": 1,
    "observacoes": "Ajuste de quantidade"
  }'
```

### Deletar

```bash
# Produto
curl -X DELETE http://localhost:8080/api/v1/produtos/2

# Composição (item único)
curl -X DELETE http://localhost:8080/api/v1/composicoes/1

# Limpar BOM inteira
curl -X DELETE http://localhost:8080/api/v1/composicoes/produto/2/limpar

# Unidade de medida
curl -X DELETE http://localhost:8080/api/v1/unidades-medida/1
```

---

## 🔍 Validações e Restrições

### Produto
- ✅ **Código**: Obrigatório, único, máx 50 caracteres, apenas [A-Z0-9-._]
- ✅ **Descrição**: Obrigatória, máx 255 caracteres
- ✅ **NCM**: Obrigatório, exatamente 8 dígitos
- ✅ **Unidade de Medida**: Deve existir no sistema
- ✅ **Status**: ATIVO, INATIVO, BLOQUEADO ou DESCONTINUADO
- ✅ **Tipo**: COMPRADO ou FABRICAVEL

### Composição
- ✅ **Produto Fabricado**: Deve ser do tipo FABRICAVEL
- ✅ **Componente**: Deve estar ATIVO
- ✅ **Quantidade**: Deve ser > 0
- ✅ **Sem Composição Circular**: A → B não pode ter B → A
- ✅ **Sem Duplicação**: Não pode existir mesma composição 2x

### Unidade de Medida
- ✅ **Sigla**: Obrigatória, única, máx 10 caracteres, [A-Z0-9]
- ✅ **Descrição**: Obrigatória, máx 100 caracteres
- ✅ **Tipo**: Opcional, máx 255 caracteres

---

## ⚠️ Mensagens de Erro Comuns

| Erro | Causa | Solução |
|------|-------|---------|
| `409 Conflict` | Código do produto já existe | Use outro código único |
| `404 Not Found` | Produto/UnidadeMedida não existe | Verifique o ID |
| `422 Unprocessable Entity` | Composição circular | Evite A→B e B→A |
| `422 Unprocessable Entity` | Produto não é fabricável | Mude tipo para FABRICAVEL |
| `400 Bad Request` | NCM com menos de 8 dígitos | Verifique NCM |

---

## 🔗 Exemplo Completo de Workflow

```bash
# 1. Criar unidades de medida
UNIDADE_KG=$(curl -s -X POST http://localhost:8080/api/v1/unidades-medida \
  -H "Content-Type: application/json" \
  -d '{"sigla":"KG","descricao":"Quilograma","tipo":"MASSA"}' \
  | jq -r '.id')

# 2. Criar produtos comprados
ACAO=$(curl -s -X POST http://localhost:8080/api/v1/produtos \
  -H "Content-Type: application/json" \
  -d "{
    \"codigo\":\"MAT-ACAO-001\",
    \"descricao\":\"Aço Carbono\",
    \"tipo\":\"COMPRADO\",
    \"status\":\"ATIVO\",
    \"unidadeMedidaId\":$UNIDADE_KG,
    \"ncm\":\"72081599\",
    \"precoCusto\":50.00,
    \"precoVenda\":75.00
  }" \
  | jq -r '.id')

TINTA=$(curl -s -X POST http://localhost:8080/api/v1/produtos \
  -H "Content-Type: application/json" \
  -d "{
    \"codigo\":\"MAT-TINTA-001\",
    \"descricao\":\"Tinta Epóxi\",
    \"tipo\":\"COMPRADO\",
    \"status\":\"ATIVO\",
    \"unidadeMedidaId\":$UNIDADE_KG,
    \"ncm\":\"32081599\",
    \"precoCusto\":30.00,
    \"precoVenda\":50.00
  }" \
  | jq -r '.id')

# 3. Criar produto fabricável
PLACA=$(curl -s -X POST http://localhost:8080/api/v1/produtos \
  -H "Content-Type: application/json" \
  -d "{
    \"codigo\":\"PROD-PLACA-001\",
    \"descricao\":\"Placa Base Soldada\",
    \"tipo\":\"FABRICAVEL\",
    \"status\":\"ATIVO\",
    \"unidadeMedidaId\":$UNIDADE_KG,
    \"ncm\":\"73081599\",
    \"precoVenda\":200.00,
    \"precoCusto\":150.00
  }" \
  | jq -r '.id')

# 4. Criar composição
curl -X POST http://localhost:8080/api/v1/composicoes \
  -H "Content-Type: application/json" \
  -d "{
    \"produtoFabricadoId\":$PLACA,
    \"produtoComponenteId\":$ACAO,
    \"quantidadeNecessaria\":2.5,
    \"sequencia\":1
  }"

curl -X POST http://localhost:8080/api/v1/composicoes \
  -H "Content-Type: application/json" \
  -d "{
    \"produtoFabricadoId\":$PLACA,
    \"produtoComponenteId\":$TINTA,
    \"quantidadeNecessaria\":0.5,
    \"sequencia\":2
  }"

# 5. Gerar lista expandida
curl -X GET "http://localhost:8080/api/v1/lista-expandida/produto/$PLACA?quantidade=100"
```

---

## 🔐 Boas Práticas

1. **Sempre validar NCM**: Use formatadores externos se necessário
2. **Usar descrições claras**: Facilita identificação em relatórios
3. **Manter histórico**: Não delete produtos ativos, apenas desative
4. **Revisar composições**: Antes de aplicar em produção
5. **Testar lista expandida**: Com quantidades variadas
6. **Documentar componentes**: Use o campo de observações

---

## 📖 Documentação Completa

Para documentação detalhada, consulte:
- [PRODUTO_FEATURE_DOCUMENTACAO.md](PRODUTO_FEATURE_DOCUMENTACAO.md)
- Swagger/OpenAPI: `http://localhost:8080/swagger-ui.html`

---

## 🐛 Troubleshooting

### Produto não aparece na lista
- Verifique se está ATIVO
- Use `/lista-expandida/compras` para apenas comprados

### Composição circular rejeitada
- Verificar se A usa B e B usa A
- Remove uma das composições

### Lista expandida vazia
- Produto é FABRICAVEL?
- Tem composição definida?
- Componentes estão ATIVOS?

---

**Última atualização**: 21/12/2024
