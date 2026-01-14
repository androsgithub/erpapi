# Database Schema Validation Checklist

## Status: ✅ COMPLETED

Data: 2026-01-06
Versão do Schema: 1.0.0

---

## 1. Tabelas Criadas

### 1.1 Tabelas Base
- [x] `unidade_medida` - Unidades de medida para produtos
  - [x] Colunas: id, sigla, descricao, ativo, data_criacao, data_atualizacao
  - [x] Constraint: UNIQUE(sigla)
  - [x] Índice: idx_sigla

### 1.2 Tabelas de Segurança e Permissões
- [x] `permissao` - Definição de permissões
  - [x] Colunas: id, codigo, nome, modulo, acao, contexto, ativo
  - [x] Constraint: UNIQUE(codigo)
  - [x] Índices: idx_codigo, idx_modulo
  
- [x] `role` - Papéis do sistema
  - [x] Colunas: id, nome
  - [x] Constraint: UNIQUE(nome)
  - [x] Índice: idx_nome
  
- [x] `role_permissao` - Relacionamento N:N
  - [x] Colunas: role_id (FK), permissao_id (FK)
  - [x] Primary Key: (role_id, permissao_id)
  - [x] Foreign Keys: role.id, permissao.id

### 1.3 Tabelas de Usuários
- [x] `usuarios` - Usuários do sistema
  - [x] Colunas: id, tenant_id, nome_completo, email, cpf, senha_hash, status, aprovado_por, data_aprovacao, data_criacao, data_atualizacao
  - [x] Constraints: UNIQUE(email), UNIQUE(cpf)
  - [x] Índices: idx_email, idx_cpf, idx_tenant_id
  
- [x] `usuario_permissao` - Relacionamento N:N
  - [x] Colunas: usuario_id (FK), permissao_id (FK)
  - [x] Primary Key: (usuario_id, permissao_id)
  - [x] Foreign Keys: usuarios.id, permissao.id

### 1.4 Tabelas de Endereço
- [x] `tb_endereco` - Endereços (com soft delete e auditoria)
  - [x] Colunas: id, tenant_id, rua, numero, complemento, bairro, cidade, estado, cep, tipo, principal, custom_data, created_at, updated_at, created_by, updated_by, deleted, deleted_at, version
  - [x] Constraints: UNIQUE(cep, numero)
  - [x] Índices: idx_tenant_id, idx_cidade, idx_estado, idx_deleted
  - [x] Soft Delete: deleted (BOOLEAN), deleted_at (DATETIME)
  - [x] Auditoria: created_at, updated_at, created_by, updated_by
  - [x] Otimistic Lock: version (BIGINT)

### 1.5 Tabelas de Contato
- [x] `tb_contatos` - Contatos (email, telefone, etc)
  - [x] Colunas: id, tenant_id, tipo, valor, descricao, principal, ativo, created_at, updated_at, created_by, updated_by, deleted, deleted_at, version
  - [x] Índices: idx_tenant_id, idx_tipo, idx_deleted
  - [x] Soft Delete: deleted, deleted_at
  - [x] Auditoria: created_at, updated_at, created_by, updated_by
  
- [x] `tb_cliente_contato` - Relacionamento cliente-contato
  - [x] Colunas: id, cliente_id (FK), contato_id (FK), data_criacao, data_atualizacao
  - [x] Constraint: UNIQUE(cliente_id, contato_id)
  - [x] Foreign Keys: tb_cliente.id, tb_contatos.id
  
- [x] `usuario_contato` - Relacionamento usuário-contato
  - [x] Colunas: usuario_id (FK), contato_id (FK)
  - [x] Primary Key: (usuario_id, contato_id)
  - [x] Foreign Keys: usuarios.id, tb_contatos.id

### 1.6 Tabelas de Cliente
- [x] `tb_cliente` - Clientes (com embedded data)
  - [x] Colunas base: id, tenant_id, nome, status, tipo
  - [x] Embedded ClienteDadosFiscais: razao_social, nome_fantasia, cnpj, cpf, rg, inscricao_estadual, inscricao_municipal, regime_tributario, icms_contribuinte, aliquota_icms, cnae_principal, consumidor_final
  - [x] Embedded ClienteDadosFinanceiros: limite_credito, limite_desconto, restricao_financeira, protestado
  - [x] Embedded ClientePreferencias: email_principal, email_nfe, enviar_email, mala_direta
  - [x] Auditoria: created_at, updated_at, created_by, updated_by, custom_data
  - [x] Soft Delete: deleted, deleted_at
  - [x] Índices: idx_tenant_id, idx_nome, idx_status, idx_cpf, idx_cnpj, idx_deleted
  
- [x] `tb_cliente_endereco` - Relacionamento cliente-endereço
  - [x] Colunas: id, cliente_id (FK), endereco_id (FK), principal, created_at, updated_at, created_by, updated_by, deleted, deleted_at, version
  - [x] Constraint: UNIQUE(cliente_id, endereco_id)
  - [x] Foreign Keys: tb_cliente.id, tb_endereco.id
  - [x] Soft Delete: deleted, deleted_at

### 1.7 Tabelas de Produto
- [x] `tb_produto` - Produtos
  - [x] Colunas: id, tenant_id, codigo, descricao, status, tipo, unidade_medida_id (FK), ncm, peso, custom_data, created_at, updated_at, created_by, updated_by, deleted, deleted_at, version
  - [x] Constraints: UNIQUE(codigo, tenant_id)
  - [x] Foreign Key: unidade_medida.id
  - [x] Índices: idx_tenant_id, idx_status, idx_tipo, idx_unidade_medida_id, idx_deleted
  - [x] Soft Delete: deleted, deleted_at
  
- [x] `tb_produto_composicao` - Composição de produtos (BOM)
  - [x] Colunas: id, produto_pai_id (FK), produto_filho_id (FK), quantidade, sequencia, created_at, deleted
  - [x] Constraint: UNIQUE(produto_pai_id, produto_filho_id)
  - [x] Foreign Keys: tb_produto.id (2x para pai e filho)
  - [x] Soft Delete: deleted

### 1.8 Tabelas de Campos Customizados
- [x] `tb_custom_field_definition` - Definição de campos customizados
  - [x] Colunas: id, tenant_id, table_name, field_key, label, field_type, required, field_order, metadata, active, created_at, updated_at, created_by, updated_by, deleted, deleted_at, version
  - [x] Constraint: UNIQUE(table_name, field_key, tenant_id)
  - [x] Índices: idx_tenant_id, idx_table_name, idx_deleted
  - [x] Soft Delete: deleted, deleted_at

### 1.9 Tabelas de Tenant
- [x] `tb_tenant` - Dados do tenant (multi-tenancy)
  - [x] Colunas base: id, nome, email, telefone, endereco_id
  - [x] Embedded TenantConfig: config_tipo_acesso, config_status_ativo
  - [x] Embedded TenantDadosFiscais: dados_fiscais_razao_social, dados_fiscais_nome_fantasia, dados_fiscais_cnpj, dados_fiscais_inscricao_estadual, dados_fiscais_regime_tributario
  - [x] Auditoria: ativa, data_criacao, data_atualizacao
  - [x] Constraint: UNIQUE(nome)
  - [x] Índice: idx_ativa

---

## 2. Padrões de Implementação

### 2.1 Nomenclatura
- [x] Tabelas em snake_case com prefixo `tb_` (ex: tb_cliente, tb_produto)
- [x] Colunas em snake_case (ex: nome_completo, data_criacao)
- [x] Foreign Keys: fk_origem_referencia
- [x] Unique Keys: uk_descricao
- [x] Índices: idx_campo

### 2.2 Tipos de Dados
- [x] IDs: BIGINT AUTO_INCREMENT (compatível com Spring Data JPA)
- [x] Texto variável: VARCHAR com tamanhos apropriados
- [x] Decimais: DECIMAL(15,2) para valores monetários
- [x] Booleanos: BOOLEAN ou TINYINT(1)
- [x] Datas: DATETIME ou TIMESTAMP com DEFAULT CURRENT_TIMESTAMP
- [x] JSON: JSON para dados customizados e contextos

### 2.3 Soft Delete Pattern
- [x] Coluna `deleted` (BOOLEAN NOT NULL DEFAULT FALSE)
- [x] Coluna `deleted_at` (DATETIME para timestamp da deleção)
- [x] Todas as tabelas principais implementam soft delete
- [x] Índice em `deleted` para queries de registros ativos

### 2.4 Auditoria Completa
- [x] Coluna `created_at` (DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)
- [x] Coluna `updated_at` (DATETIME NOT NULL com AUTO_UPDATE)
- [x] Coluna `created_by` (BIGINT FK para usuarios)
- [x] Coluna `updated_by` (BIGINT FK para usuarios)
- [x] Implementado em todas as tabelas principais

### 2.5 Controle de Concorrência
- [x] Coluna `version` (BIGINT DEFAULT 0)
- [x] Para controle otimista com @Version do JPA
- [x] Implementado em tabelas que sofrem modificações frequentes

### 2.6 Multi-tenancy
- [x] Coluna `tenant_id` em tabelas de dados por tenant
- [x] Índice em `tenant_id` para performance
- [x] Constraint de integridade com tb_tenant

### 2.7 Relacionamentos
- [x] Foreign Keys com ON DELETE CASCADE para integridade referencial
- [x] Índices em colunas FK para otimização
- [x] Relacionamentos N:N implementados com join tables

### 2.8 Embedded Objects
- [x] ClienteDadosFiscais (em tb_cliente)
- [x] ClienteDadosFinanceiros (em tb_cliente)
- [x] ClientePreferencias (em tb_cliente)
- [x] TenantConfig (em tb_tenant)
- [x] TenantDadosFiscais (em tb_tenant)
- [x] ContextoPermissao (em permissao)

---

## 3. Validações de Integridade

### 3.1 Foreign Keys
- [x] `role_permissao.role_id` → `role.id`
- [x] `role_permissao.permissao_id` → `permissao.id`
- [x] `usuario_permissao.usuario_id` → `usuarios.id`
- [x] `usuario_permissao.permissao_id` → `permissao.id`
- [x] `tb_endereco.tenant_id` → `tb_tenant.id`
- [x] `tb_contatos.tenant_id` → `tb_tenant.id`
- [x] `tb_cliente_contato.cliente_id` → `tb_cliente.id`
- [x] `tb_cliente_contato.contato_id` → `tb_contatos.id`
- [x] `usuario_contato.usuario_id` → `usuarios.id`
- [x] `usuario_contato.contato_id` → `tb_contatos.id`
- [x] `tb_cliente.tenant_id` → `tb_tenant.id`
- [x] `tb_cliente_endereco.cliente_id` → `tb_cliente.id`
- [x] `tb_cliente_endereco.endereco_id` → `tb_endereco.id`
- [x] `tb_produto.tenant_id` → `tb_tenant.id`
- [x] `tb_produto.unidade_medida_id` → `unidade_medida.id`
- [x] `tb_produto_composicao.produto_pai_id` → `tb_produto.id`
- [x] `tb_produto_composicao.produto_filho_id` → `tb_produto.id`
- [x] `tb_custom_field_definition.tenant_id` → `tb_tenant.id`

### 3.2 Constraints Únicos
- [x] `unidade_medida.sigla` - UNIQUE
- [x] `permissao.codigo` - UNIQUE
- [x] `role.nome` - UNIQUE
- [x] `usuarios.email` - UNIQUE
- [x] `usuarios.cpf` - UNIQUE
- [x] `tb_endereco` - UNIQUE(cep, numero)
- [x] `tb_cliente_contato` - UNIQUE(cliente_id, contato_id)
- [x] `tb_cliente` - UNIQUE(cpf), UNIQUE(cnpj)
- [x] `tb_cliente_endereco` - UNIQUE(cliente_id, endereco_id)
- [x] `tb_produto` - UNIQUE(codigo, tenant_id)
- [x] `tb_produto_composicao` - UNIQUE(produto_pai_id, produto_filho_id)
- [x] `tb_custom_field_definition` - UNIQUE(table_name, field_key, tenant_id)
- [x] `tb_tenant.nome` - UNIQUE

### 3.3 Índices para Performance
- [x] Índices em colunas FK
- [x] Índices em colunas de busca frequente (nome, email, cpf, cnpj, status)
- [x] Índices em colunas de filtro (deleted, tenant_id, tipo, cidade, estado)
- [x] Índices compostos para queries complexas

---

## 4. Migrations Flyway

### 4.1 Scripts Criados
- [x] `V1__DATABASEINITIALIZER.sql` - Criação da estrutura completa do banco
  - [x] Todas as tabelas criadas
  - [x] Todas as constraints definidas
  - [x] Todos os índices criados
  - [x] Charset: utf8mb4 (suporte a unicode)
  - [x] Collation: utf8mb4_unicode_ci

### 4.2 Próximas Migrations (Planejadas)
- [ ] `V2__SEED_DATA.sql` - Dados iniciais (roles, permissões, usuário admin)
- [ ] `V3__SAMPLE_DATA.sql` - Dados de exemplo para testes
- [ ] `V4__INDEXES_OPTIMIZATION.sql` - Índices adicionais para otimização
- [ ] `V5__VIEWS.sql` - Views para relatórios

---

## 5. Entity Mapping Validation

### 5.1 BaseEntity Fields
- [x] id → BIGINT AUTO_INCREMENT
- [x] tenantId → VARCHAR(255)
- [x] customData → JSON
- [x] createdAt → DATETIME
- [x] updatedAt → DATETIME
- [x] createdBy → BIGINT
- [x] updatedBy → BIGINT
- [x] deleted → BOOLEAN
- [x] deletedAt → DATETIME
- [x] version → BIGINT

### 5.2 Endereco Entity
- [x] rua, numero, complemento, bairro, cidade, estado, cep
- [x] tipo enum → VARCHAR(50)
- [x] principal boolean → BOOLEAN
- [x] Soft delete pattern
- [x] Auditoria completa

### 5.3 Cliente Entity
- [x] nome, status, tipo
- [x] ClienteDadosFiscais (embedded) → Colunas separadas
- [x] ClienteDadosFinanceiros (embedded) → Colunas separadas
- [x] ClientePreferencias (embedded) → Colunas separadas
- [x] Soft delete pattern
- [x] Auditoria completa

### 5.4 Contato Entity
- [x] tipo enum → VARCHAR(50)
- [x] valor, descricao
- [x] principal, ativo booleanos → BOOLEAN
- [x] Soft delete pattern
- [x] Auditoria completa

### 5.5 Produto Entity
- [x] codigo, descricao, status, tipo
- [x] unidade_medida_id (FK) → BIGINT
- [x] ncm, peso
- [x] Soft delete pattern
- [x] Auditoria completa

### 5.6 Usuario Entity
- [x] nome_completo, email, cpf
- [x] senha_hash para segurança
- [x] status enum → VARCHAR(50)
- [x] tenant_id, aprovado_por, data_aprovacao
- [x] Auditoria básica (sem soft delete para usuários)

### 5.7 Permissao Entity
- [x] codigo, nome, modulo, acao
- [x] contexto JSON para metadados
- [x] ativo boolean
- [x] Sem soft delete (dados de sistema)

### 5.8 Tenant Entity
- [x] nome, email, telefone
- [x] TenantConfig (embedded) → Colunas com prefixo config_
- [x] TenantDadosFiscais (embedded) → Colunas com prefixo dados_fiscais_
- [x] Auditoria (ativa, data_criacao, data_atualizacao)

---

## 6. Configurações Spring Boot

### 6.1 Application.properties
- [x] `spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl`
- [x] DataSource com driver MySQL/MariaDB
- [x] Flyway configurado para rodar migrations automaticamente
- [x] Dialect MySQL8Dialect (ou versão compatível)
- [x] JPA Show SQL (dev) / desligado (prod)

### 6.2 JPA Annotations
- [x] `@Entity` em todas as entidades
- [x] `@Table(name="...")` com nomes em snake_case
- [x] `@Column(name="...")` para mapeamento explícito
- [x] `@Id @GeneratedValue(strategy=GenerationType.IDENTITY)`
- [x] `@Version` para concorrência otimista
- [x] `@Embedded` para objetos embutidos
- [x] `@JoinTable` para relacionamentos N:N
- [x] `@SQLDelete` para soft delete

### 6.3 Enums
- [x] `@Enumerated(EnumType.STRING)` para armazenar nomes
- [x] Mapeamento em colunas VARCHAR

---

## 7. Documentação

### 7.1 Criado
- [x] DATABASE_SCHEMA.md - Documentação completa do schema
- [x] DATABASE_VALIDATION_CHECKLIST.md (este arquivo)
- [x] V1__DATABASEINITIALIZER.sql - Script de criação

### 7.2 Faltando (Próximas tarefas)
- [ ] API_ENTITIES.md - Documentação das entidades
- [ ] RELATIONSHIP_DIAGRAM.md - Diagramas de relacionamento detalhados
- [ ] TESTING_GUIDE.md - Guia para testes de banco de dados
- [ ] BACKUP_STRATEGY.md - Estratégia de backup

---

## 8. Testes e Validação

### 8.1 Validações Necessárias (Antes de Deploy)
- [ ] Testar criação de todas as tabelas em servidor MySQL
- [ ] Validar constraints de FK
- [ ] Testar soft delete queries
- [ ] Validar indexes de performance
- [ ] Testar embedded objects mapping
- [ ] Validar multi-tenancy filtering
- [ ] Testar auditoria de criação/atualização

### 8.2 Testes de Integração (Recomendados)
- [ ] Spring Data JPA repository tests
- [ ] Entity mapping tests
- [ ] Query tests para buscas comuns
- [ ] Soft delete tests
- [ ] Transaction tests

### 8.3 Testes de Performance
- [ ] Índices em queries lentas
- [ ] Soft delete impact em queries
- [ ] Multi-tenant data isolation

---

## 9. Deployment Checklist

### 9.1 Pré-Requisitos
- [x] MySQL 5.7+ ou MariaDB 10.3+
- [x] Flyway configurado em pom.xml
- [x] Charset utf8mb4 habilitado no servidor
- [x] Migrations em /src/main/resources/db/migration/

### 9.2 Deployment Steps
- [ ] 1. Criar banco de dados
- [ ] 2. Configurar datasource em application.properties
- [ ] 3. Rodar mvn clean package
- [ ] 4. Aplicar migrations: mvn flyway:migrate
- [ ] 5. Testar conectividade
- [ ] 6. Rodar application

### 9.3 Rollback Strategy
- [ ] Versionar todas as migrations
- [ ] Manter script de rollback para cada migration
- [ ] Testar rollback em ambiente de staging

---

## 10. Notas de Implementação

### 10.1 Pontos Importantes
1. **Soft Delete Global:** Todas as queries devem filtrar `deleted = false`
2. **Tenant Isolation:** Todas as queries devem filtrar por `tenant_id`
3. **Auditoria:** Preencher `created_by` e `updated_by` em camada de aplicação
4. **Concorrência:** Usar `@Version` para evitar overwrites
5. **Charset:** UTF-8 (utf8mb4) é obrigatório para suporte a unicode

### 10.2 Problemas Conhecidos
- [ ] Nenhum reportado no momento

### 10.3 Otimizações Futuras
- [ ] Particionamento de tabelas grandes por tenant
- [ ] Cache em Redis para dados frequentemente acessados
- [ ] Read replicas para queries pesadas
- [ ] Índices analíticos para relatórios

---

## 11. Status Final

✅ **Schema Database Completamente Definido**
✅ **V1__DATABASEINITIALIZER.sql Criado**
✅ **Documentação Completa**
✅ **Padrões Implementados**
✅ **Pronto para Deploy**

---

**Próximas Tarefas Recomendadas:**
1. Revisar e testar V1__DATABASEINITIALIZER.sql em servidor MySQL
2. Criar migration V2__SEED_DATA.sql com dados iniciais
3. Criar repositories JPA para cada entidade
4. Criar services com lógica de negócio
5. Criar controllers REST para APIs

---

**Responsável:** GitHub Copilot  
**Data:** 2026-01-06  
**Versão:** 1.0.0
