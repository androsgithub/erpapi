# Ajuste da Classificação Fiscal do Produto

## 📋 Resumo das Alterações

Este documento descreve as alterações implementadas para integrar completamente a classificação fiscal do produto na API ERP.

---

## 🔧 Alterações Realizadas

### 1. **Novo DTO: ClassificacaoFiscalDTO**
**Arquivo:** `src/main/java/com/api/erp/features/produto/application/dto/ClassificacaoFiscalDTO.java`

Novo Data Transfer Object que expõe todos os campos de classificação fiscal:
- **ncm**: Nomenclatura Comum do Mercosul (8 dígitos)
- **descricaoFiscal**: Descrição fiscal (máximo 120 caracteres)
- **origemMercadoria**: Código da origem (0-8 conforme RFB)
- **codigoBeneficioFiscal**: Código de benefício fiscal (opcional, máximo 10 caracteres)
- **cest**: Código de Substituição Tributária (opcional, 7 dígitos)
- **unidadeTributavelCodigo**: Código da unidade tributável (obrigatório)
- **unidadeTributavelDescricao**: Descrição da unidade tributável (obrigatório)

### 2. **Atualização: ProdutoRequestDTO**
**Arquivo:** `src/main/java/com/api/erp/features/produto/application/dto/ProdutoRequestDTO.java`

- ✅ Adicionado novo campo: `classificacaoFiscal` (ClassificacaoFiscalDTO)
- ⚠️ Marcado como **@Deprecated** os campos `ncm` e `informacoesFiscais`
- ✅ Documentação sobre migração para o novo modelo

**Estrutura anterior:**
```json
{
  "codigo": "PROD-001",
  "descricao": "Produto A",
  "ncm": "12345678",
  "informacoesFiscais": "..."
}
```

**Estrutura nova:**
```json
{
  "codigo": "PROD-001",
  "descricao": "Produto A",
  "classificacaoFiscal": {
    "ncm": "12345678",
    "descricaoFiscal": "Descrição fiscal",
    "origemMercadoria": 0,
    "unidadeTributavelCodigo": "UN",
    "unidadeTributavelDescricao": "Unidade"
  }
}
```

### 3. **Atualização: ProdutoResponseDTO**
**Arquivo:** `src/main/java/com/api/erp/features/produto/application/dto/ProdutoResponseDTO.java`

- ✅ Adicionado novo campo: `classificacaoFiscal` (ClassificacaoFiscalDTO)
- ⚠️ Campos `ncm` e `informacoesFiscais` marcados como **@Deprecated**
- ✅ Retorna completa informação de classificação fiscal

### 4. **Atualização: ProdutoService**
**Arquivo:** `src/main/java/com/api/erp/features/produto/application/service/ProdutoService.java`

#### Importações adicionadas:
```java
import com.api.erp.features.produto.domain.entity.ClassificacaoFiscal;
import com.api.erp.shared.domain.valueobject.*;
```

#### Novos métodos:

**`converterClassificacaoFiscalParaDTO(ClassificacaoFiscal clf)`**
- Converte entidade de classificação fiscal para DTO
- Tratamento de null para opcional

**`criarClassificacaoFiscal(ClassificacaoFiscalDTO dto)`**
- Cria instância de ClassificacaoFiscal usando os Value Objects
- Valida os dados através dos Value Objects (NCM, OrigemMercadoria, etc)

**`validarEObterClassificacaoFiscal(ProdutoRequestDTO dto)`**
- Valida presença obrigatória de campos
- Suporta compatibilidade com campo legado `ncm`
- Lança exceção descritiva com campos faltantes
- Validações:
  - NCM é obrigatório
  - Descrição fiscal é obrigatória
  - Origem da mercadoria é obrigatória (0-8)
  - Código da unidade tributável é obrigatório
  - Descrição da unidade tributável é obrigatória

#### Métodos atualizados:

**`criar(ProdutoRequestDTO dto)`**
- Agora obtém e valida classificação fiscal completa
- Cria instância de ClassificacaoFiscal
- Vincula ao produto antes de salvar

**`atualizar(Long id, ProdutoRequestDTO dto)`**
- Atualiza classificação fiscal do produto existente
- Mesmo fluxo de validação de criação

**`converterParaResponseDTO(Produto produto)`**
- Agora converte e inclui classificação fiscal na resposta

### 5. **Atualização: ProdutoValidator**
**Arquivo:** `src/main/java/com/api/erp/features/produto/domain/validator/ProdutoValidator.java`

- ✅ Removida validação individual de NCM do método `validarCriacao()`
- ℹ️ Validação de NCM agora ocorre via Value Object no serviço
- Validações de código e descrição mantidas

### 6. **Atualização: ListaExpandidaProducaoServiceTest**
**Arquivo:** `src/test/java/com/api/erp/features/produto/domain/service/ListaExpandidaProducaoServiceTest.java`

- ✅ Removidos chamadas a `.ncm()` no builder de Produto
- ✅ Corrigidas comparações de BigDecimal usando `.compareTo()`
- ✅ Testes de domínio compilam e passam com sucesso

---

## 📊 Mapeamento de Value Objects

A classificação fiscal agora aproveita os Value Objects já implementados:

| Campo | Value Object | Localização |
|-------|--------------|-------------|
| NCM | `NCM` | `shared/domain/valueobject/NCM.java` |
| Origem Mercadoria | `OrigemMercadoria` | `shared/domain/valueobject/OrigemMercadoria.java` |
| Código Benefício Fiscal | `CodigoBeneficioFiscal` | `shared/domain/valueobject/CodigoBeneficioFiscal.java` |
| CEST | `CEST` | `shared/domain/valueobject/CEST.java` |
| Unidade Tributável | `UnidadeTributavel` | `shared/domain/valueobject/UnidadeTributavel.java` |

---

## 🔒 Validações Implementadas

### Validações Obrigatórias (em nível DTO):
- ✅ NCM não pode ser vazio
- ✅ Descrição fiscal não pode ser vazia
- ✅ Origem da mercadoria é obrigatória
- ✅ Código da unidade tributável é obrigatório
- ✅ Descrição da unidade tributável é obrigatória

### Validações de Value Object (em nível de domínio):
- ✅ NCM deve ter exatamente 8 dígitos
- ✅ OrigemMercadoria deve estar no intervalo 0-8
- ✅ CodigoBeneficioFiscal máximo 10 caracteres, apenas letras e números
- ✅ CEST deve ter 7 dígitos
- ✅ UnidadeTributavel valida código e descrição

---

## 🔄 Compatibilidade

### Backward Compatibility
- ⚠️ Campos `ncm` e `informacoesFiscais` marcados como **@Deprecated** (forRemoval = true)
- ⚠️ Suporte temporário para campo legado `ncm` em requisições
- ✅ Transição bem planejada para futuras remoções

### Mensagens de Erro
Quando a classificação fiscal não é fornecida:
```
Classificação fiscal é obrigatória. Forneça classificacaoFiscal com todos os campos necessários
```

Quando campos obrigatórios estão faltando:
```
NCM é obrigatório
Descrição fiscal é obrigatória
Origem da mercadoria é obrigatória (código 0-8)
Código da unidade tributável é obrigatório
Descrição da unidade tributável é obrigatória
```

---

## 📝 Exemplos de Requisição

### Criar Produto com Classificação Fiscal Completa

```bash
POST /api/v1/produtos
Content-Type: application/json

{
  "codigo": "AÇO-001",
  "descricao": "Aço Carbono ABNT 1020",
  "descricaoDetalhada": "Aço carbono de qualidade, apropriado para peças...",
  "status": "ATIVO",
  "tipo": "COMPRADO",
  "unidadeMedidaId": 1,
  "classificacaoFiscal": {
    "ncm": "72081599",
    "descricaoFiscal": "Perfis, varões e arames de ferro ou aço, não ligados, simplesmente enfiados a quente",
    "origemMercadoria": 0,
    "codigoBeneficioFiscal": "AP01",
    "cest": "1401100",
    "unidadeTributavelCodigo": "KG",
    "unidadeTributavelDescricao": "Quilograma"
  },
  "precoVenda": 150.00,
  "precoCusto": 100.00
}
```

### Resposta

```json
{
  "id": 1,
  "codigo": "AÇO-001",
  "descricao": "Aço Carbono ABNT 1020",
  "descricaoDetalhada": "Aço carbono de qualidade, apropriado para peças...",
  "status": "ATIVO",
  "tipo": "COMPRADO",
  "unidadeMedida": {
    "id": 1,
    "sigla": "KG",
    "descricao": "Quilograma"
  },
  "classificacaoFiscal": {
    "ncm": "72081599",
    "descricaoFiscal": "Perfis, varões e arames de ferro ou aço, não ligados, simplesmente enfiados a quente",
    "origemMercadoria": 0,
    "codigoBeneficioFiscal": "AP01",
    "cest": "1401100",
    "unidadeTributavelCodigo": "KG",
    "unidadeTributavelDescricao": "Quilograma"
  },
  "precoVenda": 150.00,
  "precoCusto": 100.00,
  "dataCriacao": "2025-12-21T17:10:00",
  "dataAtualizacao": "2025-12-21T17:10:00"
}
```

---

## ✅ Testes

### Testes Implementados
- ✅ Testes de domínio para ListaExpandidaProducaoService passam
- ✅ Compilação sem erros
- ✅ Todos os Value Objects validam corretamente

### Como Executar
```bash
# Compilar o projeto
mvn clean compile

# Executar testes de domínio
mvn test -Dtest=ListaExpandidaProducaoServiceTest
```

---

## 🚀 Próximas Etapas

1. **Remoção de Campos Legados**: Em versão futura, remover `ncm` e `informacoesFiscais`
2. **Migrações de Banco de Dados**: Se necessário, criar migração para consolidar dados em `classificacao_fiscal`
3. **Testes de Integração**: Implementar testes com Docker quando ambiente permitir
4. **Documentação Swagger**: Atualizar documentação OpenAPI com novo DTO

---

## 📌 Notas Importantes

- A classificação fiscal é **Embeddable** na entidade Produto (não é uma entidade separada)
- Todos os campos obrigatórios são validados tanto em nível de DTO quanto de Value Object
- O sistema mantém compatibilidade com código legado durante transição
- Origem da mercadoria usa os valores padrão conforme legislação RFB (Receita Federal do Brasil)

---

**Data:** 21 de Dezembro de 2025  
**Status:** ✅ Implementação Completa
