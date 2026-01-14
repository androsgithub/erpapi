# ERP API - Database Schema Documentation

## Visão Geral

Este documento descreve o esquema completo do banco de dados para o ERP API, incluindo todas as tabelas, colunas, relacionamentos e constraints.

**Padrões de Nomenclatura:**
- Tabelas: `snake_case` (ex: `tb_cliente`, `tb_produto`)
- Colunas: `snake_case` (ex: `nome_completo`, `data_criacao`)
- Foreign Keys: `fk_origem_referencia` (ex: `fk_cliente_endereco_cliente`)
- Unique Keys: `uk_descricao` (ex: `uk_cliente_endereco`)
- Índices: `idx_campo` (ex: `idx_tenant_id`)

---

## 1. Tabelas Base

### 1.1 `unidade_medida`
**Descrição:** Unidades de medida utilizadas em produtos.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `sigla` | VARCHAR(10) | NOT NULL, UNIQUE | Código da unidade (ex: KG, LT, UN) |
| `descricao` | VARCHAR(100) | NOT NULL | Descrição da unidade |
| `ativo` | BOOLEAN | NOT NULL, DEFAULT TRUE | Status da unidade |
| `data_criacao` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de criação |
| `data_atualizacao` | DATETIME | NOT NULL, AUTO_UPDATE | Data de atualização |

**Índices:**
- `idx_sigla` em `sigla`

---

## 2. Tabelas de Segurança e Permissões

### 2.1 `permissao`
**Descrição:** Define as permissões do sistema.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `codigo` | VARCHAR(255) | NOT NULL, UNIQUE | Código único da permissão |
| `nome` | VARCHAR(255) | NOT NULL | Nome da permissão |
| `modulo` | VARCHAR(255) | NOT NULL | Módulo ao qual pertence |
| `acao` | VARCHAR(50) | NOT NULL | Tipo de ação (CREATE, READ, UPDATE, DELETE, etc) |
| `contexto` | JSON | - | Contexto adicional da permissão |
| `ativo` | BOOLEAN | NOT NULL, DEFAULT TRUE | Status da permissão |

**Índices:**
- `idx_codigo` em `codigo`
- `idx_modulo` em `modulo`

---

### 2.2 `role`
**Descrição:** Define as roles (papéis) do sistema.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `nome` | VARCHAR(255) | NOT NULL, UNIQUE | Nome único da role |

**Índices:**
- `idx_nome` em `nome`

**Relacionamentos:**
- JoinTable: `role_permissao` (N:N com `permissao`)

---

### 2.3 `role_permissao`
**Descrição:** Relacionamento N:N entre roles e permissões.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `role_id` | BIGINT | FK, NOT NULL | Referência a `role.id` |
| `permissao_id` | BIGINT | FK, NOT NULL | Referência a `permissao.id` |

**Primary Key:** (`role_id`, `permissao_id`)

---

## 3. Tabelas de Usuários

### 3.1 `usuarios`
**Descrição:** Armazena dados dos usuários do sistema.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `tenant_id` | VARCHAR(255) | NOT NULL | Identificador do tenant |
| `nome_completo` | VARCHAR(255) | NOT NULL | Nome completo do usuário |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | Email único |
| `cpf` | VARCHAR(11) | NOT NULL, UNIQUE | CPF único |
| `senha_hash` | VARCHAR(255) | NOT NULL | Hash da senha |
| `status` | VARCHAR(50) | NOT NULL, DEFAULT 'ATIVO' | Status (ATIVO, INATIVO, BLOQUEADO) |
| `aprovado_por` | BIGINT | NOT NULL | ID do usuário aprovador |
| `data_aprovacao` | DATETIME | NOT NULL | Data de aprovação |
| `data_criacao` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de criação |
| `data_atualizacao` | DATETIME | NOT NULL, AUTO_UPDATE | Data de atualização |

**Índices:**
- `idx_email` em `email`
- `idx_cpf` em `cpf`
- `idx_tenant_id` em `tenant_id`

**Relacionamentos:**
- JoinTable: `usuario_permissao` (N:N com `permissao`)
- JoinTable: `usuario_contato` (N:N com `tb_contatos`)

---

### 3.2 `usuario_permissao`
**Descrição:** Relacionamento N:N entre usuários e permissões.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `usuario_id` | BIGINT | FK, NOT NULL | Referência a `usuarios.id` |
| `permissao_id` | BIGINT | FK, NOT NULL | Referência a `permissao.id` |

**Primary Key:** (`usuario_id`, `permissao_id`)

---

## 4. Tabelas de Endereço

### 4.1 `tb_endereco`
**Descrição:** Armazena endereços do sistema (com suporte a soft delete).

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `tenant_id` | BIGINT | NOT NULL | Identificador do tenant |
| `rua` | VARCHAR(255) | NOT NULL | Nome da rua |
| `numero` | VARCHAR(50) | NOT NULL | Número da residência |
| `complemento` | VARCHAR(255) | - | Complemento do endereço |
| `bairro` | VARCHAR(100) | NOT NULL | Nome do bairro |
| `cidade` | VARCHAR(100) | NOT NULL | Nome da cidade |
| `estado` | VARCHAR(2) | NOT NULL | Sigla do estado (UF) |
| `cep` | VARCHAR(8) | NOT NULL | CEP (sem hífen) |
| `tipo` | VARCHAR(50) | NOT NULL | Tipo (RESIDENCIAL, COMERCIAL, ENTREGA, etc) |
| `principal` | BOOLEAN | NOT NULL, DEFAULT FALSE | Endereço principal |
| `custom_data` | JSON | - | Dados customizados |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de criação |
| `updated_at` | DATETIME | NOT NULL, AUTO_UPDATE | Data de atualização |
| `created_by` | BIGINT | - | ID do criador |
| `updated_by` | BIGINT | - | ID do atualizador |
| `deleted` | BOOLEAN | NOT NULL, DEFAULT FALSE | Flag de soft delete |
| `deleted_at` | DATETIME | - | Data de deleção lógica |
| `version` | BIGINT | DEFAULT 0 | Versão para controle de concorrência |

**Indices:**
- `idx_tenant_id` em `tenant_id`
- `idx_cidade` em `cidade`
- `idx_estado` em `estado`
- `idx_deleted` em `deleted`

**Unique Keys:**
- `uk_cep_numero` em (`cep`, `numero`)

---

## 5. Tabelas de Contato

### 5.1 `tb_contatos`
**Descrição:** Armazena informações de contato (telefone, email, etc).

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `tenant_id` | BIGINT | NOT NULL | Identificador do tenant |
| `tipo` | VARCHAR(50) | NOT NULL | Tipo (EMAIL, TELEFONE, CELULAR, WHATSAPP) |
| `valor` | VARCHAR(255) | NOT NULL | Valor do contato |
| `descricao` | VARCHAR(255) | - | Descrição adicional |
| `principal` | BOOLEAN | NOT NULL, DEFAULT FALSE | Contato principal |
| `ativo` | BOOLEAN | NOT NULL, DEFAULT TRUE | Status ativo |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de criação |
| `updated_at` | DATETIME | NOT NULL, AUTO_UPDATE | Data de atualização |
| `created_by` | BIGINT | - | ID do criador |
| `updated_by` | BIGINT | - | ID do atualizador |
| `deleted` | BOOLEAN | NOT NULL, DEFAULT FALSE | Flag de soft delete |
| `deleted_at` | DATETIME | - | Data de deleção lógica |
| `version` | BIGINT | DEFAULT 0 | Versão para controle de concorrência |

**Índices:**
- `idx_tenant_id` em `tenant_id`
- `idx_tipo` em `tipo`
- `idx_deleted` em `deleted`

---

### 5.2 `tb_cliente_contato`
**Descrição:** Relacionamento entre clientes e contatos.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `cliente_id` | BIGINT | FK, NOT NULL | Referência a `tb_cliente.id` |
| `contato_id` | BIGINT | FK, NOT NULL | Referência a `tb_contatos.id` |
| `data_criacao` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de criação |
| `data_atualizacao` | DATETIME | NOT NULL, AUTO_UPDATE | Data de atualização |

**Unique Key:** `uk_cliente_contato` em (`cliente_id`, `contato_id`)

---

### 5.3 `usuario_contato`
**Descrição:** Relacionamento N:N entre usuários e contatos.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `usuario_id` | BIGINT | FK, NOT NULL | Referência a `usuarios.id` |
| `contato_id` | BIGINT | FK, NOT NULL | Referência a `tb_contatos.id` |

**Primary Key:** (`usuario_id`, `contato_id`)

---

## 6. Tabelas de Cliente

### 6.1 `tb_cliente`
**Descrição:** Armazena dados dos clientes (com embeddings de dados complementares).

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `tenant_id` | BIGINT | NOT NULL | Identificador do tenant |
| `nome` | VARCHAR(255) | NOT NULL | Nome do cliente |
| `status` | VARCHAR(50) | NOT NULL, DEFAULT 'ATIVO' | Status (ATIVO, INATIVO, BLOQUEADO) |
| `tipo` | VARCHAR(50) | NOT NULL | Tipo (PESSOA_FISICA, PESSOA_JURIDICA, AMBOS) |

**Dados Fiscais (Embedded):**
| `razao_social` | VARCHAR(255) | - | Razão social |
| `nome_fantasia` | VARCHAR(255) | - | Nome fantasia |
| `cnpj` | VARCHAR(14) | - | CNPJ |
| `cpf` | VARCHAR(11) | - | CPF |
| `rg` | VARCHAR(9) | - | RG |
| `inscricao_estadual` | VARCHAR(255) | - | Inscrição estadual |
| `inscricao_municipal` | VARCHAR(255) | - | Inscrição municipal |
| `regime_tributario` | VARCHAR(50) | - | Regime tributário |
| `icms_contribuinte` | BOOLEAN | - | É contribuinte de ICMS |
| `aliquota_icms` | DECIMAL(5,2) | - | Alíquota de ICMS |
| `cnae_principal` | VARCHAR(255) | - | CNAE principal |
| `consumidor_final` | BOOLEAN | - | É consumidor final |

**Dados Financeiros (Embedded):**
| `limite_credito` | DECIMAL(15,2) | - | Limite de crédito |
| `limite_desconto` | DECIMAL(5,2) | - | Limite de desconto |
| `restricao_financeira` | BOOLEAN | - | Tem restrição financeira |
| `protestado` | BOOLEAN | - | Está protestado |

**Preferências (Embedded):**
| `email_principal` | VARCHAR(255) | - | Email principal |
| `email_nfe` | VARCHAR(255) | - | Email para NF-e |
| `enviar_email` | BOOLEAN | - | Enviar emails |
| `mala_direta` | BOOLEAN | - | Recebe mala direta |

**Auditoria:**
| `custom_data` | JSON | - | Dados customizados |
| `created_at` | DATETIME | NOT NULL | Data de criação |
| `updated_at` | DATETIME | NOT NULL | Data de atualização |
| `created_by` | BIGINT | - | ID do criador |
| `updated_by` | BIGINT | - | ID do atualizador |
| `deleted` | BOOLEAN | NOT NULL, DEFAULT FALSE | Flag de soft delete |
| `deleted_at` | DATETIME | - | Data de deleção lógica |
| `version` | BIGINT | DEFAULT 0 | Versão para controle de concorrência |

**Índices:**
- `idx_tenant_id` em `tenant_id`
- `idx_nome` em `nome`
- `idx_status` em `status`
- `idx_cpf` em `cpf`
- `idx_cnpj` em `cnpj`
- `idx_deleted` em `deleted`

---

### 6.2 `tb_cliente_endereco`
**Descrição:** Relacionamento entre clientes e endereços (com auditoria completa).

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `cliente_id` | BIGINT | FK, NOT NULL | Referência a `tb_cliente.id` |
| `endereco_id` | BIGINT | FK, NOT NULL | Referência a `tb_endereco.id` |
| `principal` | BOOLEAN | NOT NULL, DEFAULT FALSE | Endereço principal |
| `created_at` | DATETIME | NOT NULL | Data de criação |
| `updated_at` | DATETIME | NOT NULL | Data de atualização |
| `created_by` | BIGINT | - | ID do criador |
| `updated_by` | BIGINT | - | ID do atualizador |
| `deleted` | BOOLEAN | NOT NULL, DEFAULT FALSE | Flag de soft delete |
| `deleted_at` | DATETIME | - | Data de deleção lógica |
| `version` | BIGINT | DEFAULT 0 | Versão para controle de concorrência |

**Unique Key:** `uk_cliente_endereco` em (`cliente_id`, `endereco_id`)

---

## 7. Tabelas de Produto

### 7.1 `tb_produto`
**Descrição:** Armazena dados dos produtos.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `tenant_id` | BIGINT | NOT NULL | Identificador do tenant |
| `codigo` | VARCHAR(50) | NOT NULL, UNIQUE | Código único do produto |
| `descricao` | VARCHAR(255) | NOT NULL | Descrição do produto |
| `status` | VARCHAR(50) | NOT NULL, DEFAULT 'ATIVO' | Status |
| `tipo` | VARCHAR(50) | NOT NULL | Tipo (COMPRADO, FABRICAVEL) |
| `unidade_medida_id` | BIGINT | FK, NOT NULL | Referência a `unidade_medida.id` |
| `ncm` | VARCHAR(8) | - | NCM fiscal |
| `peso` | DECIMAL(10,3) | - | Peso em kg |
| `custom_data` | JSON | - | Dados customizados |
| `created_at` | DATETIME | NOT NULL | Data de criação |
| `updated_at` | DATETIME | NOT NULL | Data de atualização |
| `created_by` | BIGINT | - | ID do criador |
| `updated_by` | BIGINT | - | ID do atualizador |
| `deleted` | BOOLEAN | NOT NULL, DEFAULT FALSE | Flag de soft delete |
| `deleted_at` | DATETIME | - | Data de deleção lógica |
| `version` | BIGINT | DEFAULT 0 | Versão para controle de concorrência |

**Índices:**
- `idx_tenant_id` em `tenant_id`
- `idx_status` em `status`
- `idx_tipo` em `tipo`
- `idx_unidade_medida_id` em `unidade_medida_id`
- `idx_deleted` em `deleted`

**Unique Key:** `uk_codigo_tenant` em (`codigo`, `tenant_id`)

---

### 7.2 `tb_produto_composicao`
**Descrição:** Relacionamento para composição de produtos.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `produto_pai_id` | BIGINT | FK, NOT NULL | Produto pai |
| `produto_filho_id` | BIGINT | FK, NOT NULL | Produto filho |
| `quantidade` | DECIMAL(10,3) | NOT NULL | Quantidade na composição |
| `sequencia` | INT | NOT NULL | Ordem de sequência |
| `created_at` | DATETIME | NOT NULL | Data de criação |
| `deleted` | BOOLEAN | NOT NULL, DEFAULT FALSE | Flag de soft delete |

**Unique Key:** `uk_composicao` em (`produto_pai_id`, `produto_filho_id`)

---

## 8. Tabelas de Campos Customizados

### 8.1 `tb_custom_field_definition`
**Descrição:** Define campos customizados por tenant e por tabela.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `tenant_id` | BIGINT | NOT NULL | Identificador do tenant |
| `table_name` | VARCHAR(255) | NOT NULL | Nome da tabela |
| `field_key` | VARCHAR(255) | NOT NULL | Chave do campo |
| `label` | VARCHAR(255) | NOT NULL | Rótulo para exibição |
| `field_type` | VARCHAR(50) | NOT NULL | Tipo de campo |
| `required` | BOOLEAN | NOT NULL, DEFAULT FALSE | Campo obrigatório |
| `field_order` | INT | NOT NULL | Ordem de exibição |
| `metadata` | JSON | - | Metadados adicionais |
| `active` | BOOLEAN | NOT NULL, DEFAULT TRUE | Campo ativo |
| `created_at` | DATETIME | NOT NULL | Data de criação |
| `updated_at` | DATETIME | NOT NULL | Data de atualização |
| `created_by` | BIGINT | - | ID do criador |
| `updated_by` | BIGINT | - | ID do atualizador |
| `deleted` | BOOLEAN | NOT NULL, DEFAULT FALSE | Flag de soft delete |
| `deleted_at` | DATETIME | - | Data de deleção lógica |
| `version` | BIGINT | DEFAULT 0 | Versão para controle de concorrência |

**Índices:**
- `idx_tenant_id` em `tenant_id`
- `idx_table_name` em `table_name`
- `idx_deleted` em `deleted`

**Unique Key:** `uk_custom_field` em (`table_name`, `field_key`, `tenant_id`)

---

## 9. Tabelas de Tenant

### 9.1 `tb_tenant`
**Descrição:** Armazena dados dos tenants do sistema.

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `nome` | VARCHAR(255) | NOT NULL, UNIQUE | Nome único do tenant |
| `email` | VARCHAR(255) | - | Email do tenant |
| `telefone` | VARCHAR(20) | - | Telefone do tenant |
| `endereco_id` | BIGINT | - | Endereço do tenant |

**Config (Embedded):**
| `config_tipo_acesso` | VARCHAR(50) | - | Tipo de acesso |
| `config_status_ativo` | BOOLEAN | DEFAULT TRUE | Status ativo |

**Dados Fiscais (Embedded):**
| `dados_fiscais_razao_social` | VARCHAR(255) | - | Razão social |
| `dados_fiscais_nome_fantasia` | VARCHAR(255) | - | Nome fantasia |
| `dados_fiscais_cnpj` | VARCHAR(14) | - | CNPJ |
| `dados_fiscais_inscricao_estadual` | VARCHAR(255) | - | Inscrição estadual |
| `dados_fiscais_regime_tributario` | VARCHAR(50) | - | Regime tributário |

**Auditoria:**
| `ativa` | BOOLEAN | NOT NULL, DEFAULT TRUE | Tenant ativo |
| `data_criacao` | DATETIME | NOT NULL | Data de criação |
| `data_atualizacao` | DATETIME | NOT NULL | Data de atualização |

**Índices:**
- `idx_ativa` em `ativa`

**Unique Key:** `uk_tenant_nome` em `nome`

---

## Diagrama de Relacionamentos

```
                           ┌─────────────────┐
                           │  unidade_medida │
                           └────────┬────────┘
                                    │
                                    │ 1:N
                                    │
                         ┌──────────▼──────────┐
                         │    tb_produto      │
                         └──────┬─────────────┘
                                │
                          ┌─────┴─────┐
                          │           │
                      (N:N)│           │(composição)
                          │           │
                ┌─────────▼──────┐    └─────────┐
                │ tb_cliente     │    tb_produto_
                │                │    composicao
                └────┬────┬──────┘
                     │    │
              ┌──────┘    │
              │           │
        (1:N) │      (1:N)│
              │           │
              │     ┌─────▼─────────┐
              │     │tb_cliente_    │
              │     │endereco       │
              │     └───────────────┘
              │
        ┌─────▼────────┐
        │ tb_contatos  │
        └──────────────┘

        ┌───────────────┐
        │   permissao   │
        └───┬───────┬───┘
            │       │
       (N:N)│       │(N:N)
            │       │
    ┌───────▼──┐   ┌▼──────────┐
    │   role   │   │ usuarios  │
    │    &     │   │    &      │
    │   role_  │   │ usuario_  │
    │ permissao│   │permissao  │
    └──────────┘   └───────────┘
```

---

## Considerações de Segurança

1. **Soft Delete:** Todas as tabelas principais usam soft delete (`deleted` flag)
2. **Auditoria:** Campos de `created_at`, `updated_at`, `created_by`, `updated_by`
3. **Concorrência:** Campo `version` para controle otimista
4. **Multi-tenancy:** Campo `tenant_id` em tabelas relevantes
5. **Constraints:** Foreign keys com `ON DELETE CASCADE` para integridade referencial

---

## Padrões de Design

### Embedded Objects
- `ClienteDadosFiscais` (em `tb_cliente`)
- `ClienteDadosFinanceiros` (em `tb_cliente`)
- `ClientePreferencias` (em `tb_cliente`)
- `ContextoPermissao` (em `permissao`)
- `TenantConfig` (em `tb_tenant`)
- `TenantDadosFiscais` (em `tb_tenant`)

### Value Objects
- `Email` (em `usuarios`, `tb_tenant`)
- `Telefone` (em `tb_tenant`)
- `CEP` (em `tb_endereco`)
- `CPF` (em `usuarios`, `tb_cliente`)
- `CNPJ` (em `tb_cliente`)
- `RG` (em `tb_cliente`)

---

## Migrations

- **V1__DATABASEINITIALIZER.sql:** Criação da estrutura completa do banco

---

**Data da Última Atualização:** 2026-01-06
**Versão do Schema:** 1.0.0

