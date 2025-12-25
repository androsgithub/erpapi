# 📏 Feature: Unidade de Medida

## 📋 Visão Geral

A feature **Unidade de Medida** é responsável pelo gerenciamento de unidades de medida utilizadas no sistema, como quilogramas, litros, unidades, caixas, etc.

## 🎯 Responsabilidades

- Gerenciar unidades de medida (criar, atualizar, deletar, listar)
- Definir unidades padrão para categorias
- Suportar conversão entre unidades
- Manter tabela de conversão
- Validar unidades em operações
- Controlar permissões de acesso

## 📊 Entidades Principais

### **UnidadeMedida**
Entidade que representa uma unidade de medida.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | Identificador único |
| `codigo` | String | Código único (ex: KG, L, UN) |
| `descricao` | String | Descrição (ex: Quilograma) |
| `simbolo` | String | Símbolo (ex: kg) |
| `tipo` | TipoUnidade | MASSA, VOLUME, COMPRIMENTO, QUANTIDADE |
| `ativa` | Boolean | Se está ativa |
| `padrao` | Boolean | Se é a padrão para o tipo |
| `dataCriacao` | LocalDateTime | Data de criação |
| `dataAtualizacao` | LocalDateTime | Data de atualização |

### **TipoUnidade** (Enum)
```java
MASSA               // Quilograma, grama, tonelada
VOLUME              // Litro, mililitro, metro cúbico
COMPRIMENTO         // Metro, centímetro, quilômetro
QUANTIDADE          // Unidade, dúzia, dezena
AREA                // Metro quadrado, hectare
VELOCIDADE          // Metros por segundo, quilômetros por hora
TEMPERATURA         // Graus Celsius, Fahrenheit
PRESSAO             // Pascal, bar, psi
```

### **ConversaoUnidade**
Tabela de conversão entre unidades.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | Identificador único |
| `unidadeOrigem` | UnidadeMedida | Unidade de origem |
| `unidadeDestino` | UnidadeMedida | Unidade de destino |
| `fator` | BigDecimal | Fator de conversão |
| `exemplo` | String | Exemplo: "1 KG = 1000 G" |

### **UnidadeMedidaPermissions** (Enum)
```java
UNIDADE_CRIAR       // Criar unidade
UNIDADE_VISUALIZAR  // Visualizar unidades
UNIDADE_ATUALIZAR   // Atualizar unidade
UNIDADE_DELETAR     // Deletar unidade
CONVERSAO_GERENCIAR // Gerenciar conversões
```

## 🏗️ Estrutura de Diretórios

```
features/unidademedida/
├── domain/
│   ├── entity/
│   │   ├── UnidadeMedida.java
│   │   ├── ConversaoUnidade.java
│   │   ├── TipoUnidade.java
│   │   └── UnidadeMedidaPermissions.java
│   │
│   ├── service/
│   │   ├── UnidadeMedidaService.java
│   │   └── ConversaoService.java
│   │
│   └── repository/
│       ├── UnidadeMedidaRepository.java
│       └── ConversaoUnidadeRepository.java
│
├── application/
│   ├── service/
│   │   ├── CriarUnidadeService.java
│   │   ├── AtualizarUnidadeService.java
│   │   ├── ConverterUnidadeService.java
│   │   └── UnidadeMedidaServiceImpl.java
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CriarUnidadeRequest.java
│   │   │   ├── AtualizarUnidadeRequest.java
│   │   │   ├── ConverterUnidadeRequest.java
│   │   │   └── AdicionarConversaoRequest.java
│   │   │
│   │   └── response/
│   │       ├── UnidadeResponse.java
│   │       ├── ConversaoResponse.java
│   │       └── ResultadoConversaoResponse.java
│   │
│   └── validator/
│       └── UnidadeValidator.java
│
├── infrastructure/
│   └── repository/
│       ├── JpaUnidadeMedidaRepository.java
│       └── JpaConversaoUnidadeRepository.java
│
└── presentation/
    └── controller/
        └── UnidadeMedidaController.java
```

## 🔄 Fluxos Principais

### 1️⃣ Criar Unidade de Medida
```
1. Controller recebe CriarUnidadeRequest
2. Valida dados (código único, tipo válido)
3. Se é padrão, remove padrão anterior do tipo
4. Cria entidade UnidadeMedida
5. Persiste no banco
6. Retorna UnidadeResponse
```

### 2️⃣ Adicionar Conversão
```
1. Controller recebe AdicionarConversaoRequest
2. Valida unidades de origem e destino
3. Verifica se são do mesmo tipo
4. Cria ConversaoUnidade
5. Cria conversão inversa automaticamente
6. Persiste no banco
7. Invalida cache de conversões
8. Retorna sucesso
```

### 3️⃣ Converter Valor
```
1. Controller recebe ConverterUnidadeRequest
2. Busca fator de conversão no cache
3. Se não encontrar, busca no banco
4. Calcula: valor_destino = valor_origem * fator
5. Arredonda para precisão apropriada
6. Retorna ResultadoConversaoResponse
```

## 📡 Endpoints da API

### Básico
```
GET    /api/v1/unidades              # Listar todas
GET    /api/v1/unidades/{id}         # Obter por ID
POST   /api/v1/unidades              # Criar nova
PUT    /api/v1/unidades/{id}         # Atualizar
DELETE /api/v1/unidades/{id}         # Deletar
```

### Filtros
```
GET    /api/v1/unidades/tipo/{tipo}      # Listar por tipo
GET    /api/v1/unidades/tipo/{tipo}/padrao  # Obter padrão do tipo
GET    /api/v1/unidades/ativas           # Listar apenas ativas
```

### Conversão
```
GET    /api/v1/conversoes                # Listar conversões
POST   /api/v1/conversoes                # Adicionar conversão
DELETE /api/v1/conversoes/{id}           # Remover conversão
POST   /api/v1/unidades/converter        # Converter valor
GET    /api/v1/unidades/{id}/conversoes  # Conversões de uma unidade
```

## ✅ Validações

### Ao Criar
- ✓ Código não pode estar em branco
- ✓ Código deve ser único
- ✓ Descrição não pode estar em branco
- ✓ Símbolo não pode estar em branco
- ✓ Tipo deve ser válido
- ✓ Se padrão, verifica se já há padrão para tipo

### Ao Adicionar Conversão
- ✓ Ambas unidades devem existir
- ✓ Devem ser do mesmo tipo
- ✓ Fator deve ser número positivo
- ✓ Não pode converter para si mesma (fator 1)
- ✓ Conversão não pode ser duplicada

### Ao Deletar
- ✓ Não pode deletar se está sendo usada em produtos
- ✓ Não pode deletar se é padrão do tipo (desativar primeiro)
- ✓ Não pode deletar se há conversões associadas

## 🔄 Tabela de Conversões Padrão

| De | Para | Fator | Exemplo |
|----|------|-------|---------|
| KG | G | 1000 | 1 KG = 1000 G |
| G | KG | 0.001 | 1 G = 0.001 KG |
| KG | T | 0.001 | 1 KG = 0.001 T |
| L | ML | 1000 | 1 L = 1000 ML |
| M | CM | 100 | 1 M = 100 CM |
| M² | KM² | 0.000001 | 1 M² = 0.000001 KM² |

## 💾 Cache de Conversões

```java
@Component
public class ConversaoUnidadeCache {
    
    private Map<String, BigDecimal> cache = new ConcurrentHashMap<>();
    
    public BigDecimal obterFator(String origem, String destino) {
        String chave = origem + "->" + destino;
        return cache.computeIfAbsent(chave, k -> {
            return buscarDoRepositorio(origem, destino);
        });
    }
    
    public void invalidar() {
        cache.clear();
    }
}
```

## 🧪 Testes

```java
UnidadeMedidaEntityTest.java
UnidadeMedidaServiceTest.java
ConversaoServiceTest.java
ConverterUnidadeServiceTest.java
UnidadeMedidaControllerTest.java
```

## 🔗 Relacionamentos

```
UnidadeMedida
    ├── Produto (muitos-para-um)
    ├── ProdutoComposicao (muitos-para-um)
    ├── ConversaoUnidade (um-para-muitos)
    └── TipoUnidade (enum)
```

## 🚀 Boas Práticas

1. **Cache de conversões** para performance
2. **Precisão decimal** apropriada (BigDecimal)
3. **Conversão bidirecional** automática
4. **Validar tipo** na conversão (não converter massa para volume)
5. **Documentar exemplo** de conversão
6. **Padrão por tipo** para facilitar seleção
7. **Rounding apropriado** (half_up)
8. **Não permitir deleção** se usada

## 📊 Exemplo de Conversão

```java
// Entrada: 2.5 KG para G
ConversaoRequest request = new ConversaoRequest(
    2.5, 
    "KG", 
    "G"
);

// Processamento
UnidadeMedida origem = buscar("KG");
UnidadeMedida destino = buscar("G");
BigDecimal fator = 1000;
BigDecimal resultado = new BigDecimal("2.5")
    .multiply(fator);  // 2500

// Resposta
ResultadoConversaoResponse {
    valor_origem: 2.5,
    unidade_origem: "KG",
    valor_destino: 2500,
    unidade_destino: "G",
    fator: 1000
}
```

## 📚 Referências Relacionadas

- [FEATURE_PRODUTO.md](FEATURE_PRODUTO.md) - Produtos com unidade
- [CAMADA_INFRASTRUCTURE.md](CAMADA_INFRASTRUCTURE.md) - Cache
- [SHARED_DOMAIN.md](SHARED_DOMAIN.md) - Value Objects

---

**Última atualização:** Dezembro de 2025
