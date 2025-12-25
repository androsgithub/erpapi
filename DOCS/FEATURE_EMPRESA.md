# 🏢 Feature: Empresa

## 📋 Visão Geral

A feature **Empresa** é responsável pelo gerenciamento de informações de empresas/filiais no ERP, suportando multi-tenancy e configurações específicas por empresa.

## 🎯 Responsabilidades

- Gerenciar dados básicos de empresas
- Suportar múltiplas empresas (multi-tenancy)
- Armazenar informações fiscais
- Gerenciar filiais/unidades
- Associar endereços
- Controlar configurações por empresa
- Gerenciar usuários por empresa

## 📊 Entidades Principais

### **Empresa**
Entidade core que representa uma empresa.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | Identificador único |
| `nomeFantasia` | String | Nome fantasia da empresa |
| `razaoSocial` | String | Razão social |
| `cnpj` | String | CNPJ único |
| `inscricaoEstadual` | String | Inscrição estadual |
| `inscricaoMunicipal` | String | Inscrição municipal |
| `tipo` | TipoEmpresa | MATRIZ ou FILIAL |
| `empresaMae` | Empresa | Referência para empresa mãe (se filial) |
| `endereco` | Endereco | Endereço principal |
| `telefonePrincipal` | String | Telefone de contato |
| `emailPrincipal` | Email | Email principal |
| `ativa` | Boolean | Se está ativa |
| `dataCriacao` | LocalDateTime | Data de criação |
| `dataAtualizacao` | LocalDateTime | Data de atualização |

### **TipoEmpresa** (Enum)
```java
MATRIZ       // Empresa matriz
FILIAL       // Filial de uma matriz
```

### **EmpresaPermissions** (Enum)
```java
EMPRESA_CRIAR       // Criar empresa
EMPRESA_VISUALIZAR  // Visualizar dados
EMPRESA_ATUALIZAR   // Atualizar dados
EMPRESA_DELETAR     // Deletar empresa
EMPRESA_ATIVAR      // Ativar empresa
EMPRESA_INATIVAR    // Inativar empresa
CONFIGURACAO_GERENCIAR  // Gerenciar configurações
```

## 🏗️ Estrutura de Diretórios

```
features/empresa/
├── domain/
│   ├── entity/
│   │   ├── Empresa.java
│   │   ├── TipoEmpresa.java
│   │   ├── EmpresaPermissions.java
│   │   └── EmpresaConfig.java
│   │
│   ├── service/
│   │   └── EmpresaService.java
│   │
│   └── repository/
│       ├── EmpresaRepository.java
│       └── EmpresaConfigRepository.java
│
├── application/
│   ├── service/
│   │   ├── CriarEmpresaService.java
│   │   ├── AtualizarEmpresaService.java
│   │   └── EmpresaServiceImpl.java
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CriarEmpresaRequest.java
│   │   │   ├── AtualizarEmpresaRequest.java
│   │   │   └── EmpresaConfigRequest.java
│   │   │
│   │   └── response/
│   │       ├── EmpresaResponse.java
│   │       ├── EmpresaDetailResponse.java
│   │       └── EmpresaConfigResponse.java
│   │
│   └── validator/
│       └── EmpresaValidator.java
│
├── infrastructure/
│   ├── repository/
│   │   ├── JpaEmpresaRepository.java
│   │   └── JpaEmpresaConfigRepository.java
│   │
│   └── config/
│       └── EmpresaContextHolder.java
│
└── presentation/
    └── controller/
        └── EmpresaController.java
```

## 🔄 Fluxos Principais

### 1️⃣ Criar Empresa
```
1. Controller recebe CriarEmpresaRequest
2. Valida dados (CNPJ único, campos obrigatórios)
3. Se é filial, valida empresa mãe
4. Cria entidade Empresa
5. Cria Endereco associado
6. Persiste no banco
7. Cria configurações padrão
8. Retorna EmpresaResponse
```

### 2️⃣ Atualizar Informações
```
1. Carrega empresa por ID
2. Valida novos dados
3. Atualiza campos permitidos
4. Persiste alterações
5. Invalida cache se existir
6. Retorna confirmação
```

### 3️⃣ Multi-tenancy
```
1. Request chega com contexto de empresa
2. Middleware valida se usuário tem acesso
3. ApplicationService recupera empresa do contexto
4. Queries são filtradas por empresa
5. Resposta inclui apenas dados da empresa
6. Audit registra qual empresa realizou ação
```

## 📡 Endpoints da API

### Básico
```
GET    /api/v1/empresas              # Listar empresas
GET    /api/v1/empresas/{id}         # Obter por ID
POST   /api/v1/empresas              # Criar nova
PUT    /api/v1/empresas/{id}         # Atualizar
DELETE /api/v1/empresas/{id}         # Deletar
```

### Gerenciamento
```
PATCH  /api/v1/empresas/{id}/ativar      # Ativar empresa
PATCH  /api/v1/empresas/{id}/inativar    # Inativar empresa
GET    /api/v1/empresas/{id}/filiais     # Listar filiais
```

### Configuração
```
GET    /api/v1/empresas/{id}/config      # Obter configurações
PUT    /api/v1/empresas/{id}/config      # Atualizar configurações
```

## ✅ Validações

### Ao Criar
- ✓ Nome fantasia não pode estar em branco
- ✓ Razão social não pode estar em branco
- ✓ CNPJ deve ser válido e único
- ✓ Inscrição estadual deve ser válida
- ✓ Se filial, empresa mãe deve existir
- ✓ Email deve ser válido (se fornecido)
- ✓ Telefone deve ser válido (se fornecido)

### Ao Deletar
- ✓ Não pode deletar se tem filiais ativas
- ✓ Não pode deletar se tem usuários associados
- ✓ Não pode deletar se tem produtos/vendas

## 🔐 Multi-tenancy

### Isolamento de Dados
```
// Contexto de empresa por request
@Component
public class EmpresaContextHolder {
    private static final ThreadLocal<Long> empresaId = new ThreadLocal<>();
    
    public static void setEmpresaId(Long id) {
        empresaId.set(id);
    }
    
    public static Long getEmpresaId() {
        return empresaId.get();
    }
}
```

### Filtro Automático
```java
// Spec/Criteria que filtra por empresa automaticamente
public class EmpresaSpec {
    public static Specification<Produto> porEmpresa() {
        return (root, query, cb) -> 
            cb.equal(root.get("empresa"), getEmpresaId());
    }
}
```

## 🧪 Testes

```java
EmpresaEntityTest.java
EmpresaServiceTest.java
CriarEmpresaServiceTest.java
EmpresaControllerTest.java
MultiTenancyTest.java
```

## 🔗 Relacionamentos

```
Empresa (MATRIZ)
    ├── Empresa (FILIAL) [um-para-muitos]
    ├── Endereco (um-para-um ou um-para-muitos)
    ├── Usuario (um-para-muitos)
    ├── Produto (um-para-muitos)
    └── EmpresaConfig (um-para-um)
```

## 🏗️ Configurações por Empresa

Exemplo de configurações personalizáveis:

```json
{
  "empresaId": 1,
  "margem_lucro_padrao": 30.0,
  "tipo_nfe": "NF-E",
  "regime_tributario": "LUCRO_REAL",
  "serie_nf": "1",
  "numero_nf_proximo": 1000,
  "permite_desconto_produto": true,
  "percentual_desconto_maximo": 10.0,
  "permite_venda_negativa": false,
  "dias_prazo_padrao": 30,
  "email_fiscal": "fiscal@empresa.com.br"
}
```

## 🚀 Boas Práticas

1. **Validar CNPJ** com dígitos verificadores
2. **Isolamento rigoroso** em multi-tenancy
3. **Cache de configurações** com invalidação
4. **Audit de mudanças** em dados fiscais
5. **Backup diferenciado** por empresa
6. **Restrição de acesso** entre empresas
7. **Conformidade fiscal** por empresa
8. **Documentação** de estrutura de filiais

## 🔐 Segurança

- ✓ Usuários não podem acessar dados de empresa não autorizada
- ✓ Contexto de empresa validado em cada request
- ✓ Permissões verificadas antes de qualquer operação
- ✓ Dados fiscais com camada adicional de proteção
- ✓ Log de acesso a informações sensíveis

## 📚 Referências Relacionadas

- [FEATURE_ENDERECO.md](FEATURE_ENDERECO.md) - Gerenciamento de endereços
- [FEATURE_USUARIO.md](FEATURE_USUARIO.md) - Usuários por empresa
- [FEATURE_PRODUTO.md](FEATURE_PRODUTO.md) - Produtos por empresa
- [SEGURANCA.md](SEGURANCA.md) - Segurança em multi-tenancy

---

**Última atualização:** Dezembro de 2025
