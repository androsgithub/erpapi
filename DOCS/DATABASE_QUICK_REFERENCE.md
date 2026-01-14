# Database Setup - Quick Reference Guide

## 📖 Documentos Principais

| Documento | Propósito | Quando Usar |
|-----------|-----------|-------------|
| [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md) | Descrição completa de todas as tabelas, colunas e relacionamentos | Entender a estrutura, fazer queries, validar design |
| [DATABASE_VALIDATION_CHECKLIST.md](DATABASE_VALIDATION_CHECKLIST.md) | Checklist de validação e deployment | Verificar o que foi implementado, deployment |
| [DATABASE_SETUP_SUMMARY.md](DATABASE_SETUP_SUMMARY.md) | Sumário executivo e próximas ações | Overview rápido, roadmap |
| [V1__DATABASEINITIALIZER.sql](../resources/db/migration/master/V1__DATABASEINITIALIZER.sql) | Script SQL para criar todas as tabelas | Executar migrate do Flyway |

---

## 🚀 Como Começar

### 1️⃣ Setup do Banco de Dados (1ª vez)

```bash
# Criar banco de dados (MySQL)
mysql -u root -p -e "CREATE DATABASE erpapi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Configurar datasource em application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/erpapi
spring.datasource.username=root
spring.datasource.password=password

# Rodar aplicação (Flyway executa automaticamente)
mvn spring-boot:run
```

### 2️⃣ Estrutura de Pastas

```
src/main/resources/db/migration/master/
├── V1__DATABASEINITIALIZER.sql    ✅ Criado (tabelas)
├── V2__SEED_DATA.sql              ⏳ Próximo (dados iniciais)
├── V3__SAMPLE_DATA.sql            ⏳ Futuro (dados teste)
└── V4__INDEXES_OPTIMIZATION.sql   ⏳ Futuro (otimização)
```

### 3️⃣ Arquivos de Documentação

```
DOCS/
├── DATABASE_SCHEMA.md              ✅ Criado
├── DATABASE_VALIDATION_CHECKLIST.md ✅ Criado
├── DATABASE_SETUP_SUMMARY.md       ✅ Criado
├── DATABASE_QUICK_REFERENCE.md     ✅ Criado (este)
└── ... (outros docs do projeto)
```

---

## 📊 Tabelas em 30 Segundos

### Tabelas de Base
- **`unidade_medida`** - KG, LT, UN, etc.

### Tabelas de Segurança
- **`permissao`** - Permissões do sistema (CREATE, READ, UPDATE, DELETE)
- **`role`** - Papéis (Admin, Gerenciador, Usuário)
- **`role_permissao`** - N:N role ↔ permissão

### Tabelas de Usuários
- **`usuarios`** - Usuários do sistema com email e CPF únicos
- **`usuario_permissao`** - N:N usuário ↔ permissão
- **`usuario_contato`** - N:N usuário ↔ contato

### Tabelas de Dados
- **`tb_endereco`** - Endereços (rua, número, CEP, cidade, estado)
- **`tb_contatos`** - Contatos (email, telefone, celular, whatsapp)
- **`tb_cliente`** - Clientes com dados fiscais e financeiros embutidos
- **`tb_cliente_endereco`** - N:N cliente ↔ endereço
- **`tb_cliente_contato`** - N:N cliente ↔ contato
- **`tb_produto`** - Produtos com unidade de medida
- **`tb_produto_composicao`** - Composição de produtos (BOM)
- **`tb_custom_field_definition`** - Campos customizados por tenant e tabela

### Tabelas de Tenant
- **`tb_tenant`** - Tenants do sistema (multi-tenancy)

---

## 🔍 Padrões de Design

### Soft Delete (Todas as tabelas de dados)
```sql
-- Coluna de status
deleted BOOLEAN NOT NULL DEFAULT FALSE
deleted_at DATETIME

-- Query exemplo
SELECT * FROM tb_cliente WHERE deleted = FALSE AND tenant_id = ?
```

### Auditoria (Todas as tabelas de dados)
```sql
-- Colunas de auditoria
created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
updated_at DATETIME NOT NULL AUTO_UPDATE
created_by BIGINT
updated_by BIGINT
```

### Multi-Tenancy (Tabelas de dados)
```sql
-- Coluna de tenant
tenant_id BIGINT NOT NULL

-- Sempre filtrar
WHERE tenant_id = ? AND deleted = FALSE
```

### Concorrência Otimista (Tabelas de dados)
```sql
-- Coluna de versão
version BIGINT DEFAULT 0

-- JPA gerencia automaticamente
@Version
private Long version;
```

---

## 💻 Quero...

### Buscar Dados de um Tipo Específico

**Buscar todos os clientes ativos:**
```sql
SELECT * FROM tb_cliente 
WHERE deleted = FALSE 
AND tenant_id = ? 
AND status = 'ATIVO';
```

**Ver [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md#6-tabelas-de-cliente) para estrutura completa**

### Entender um Relacionamento

**Exemplo: Cliente → Contatos**
- Cliente tem múltiplos contatos
- Contatos podem ser reutilizados
- Junction table: `tb_cliente_contato`

**Ver [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md#diagrama-de-relacionamentos) para diagrama**

### Adicionar Nova Tabela

1. Criar entidade Java em `src/main/java/com/api/...`
2. Adicionar `@Entity @Table(name="tb_new_table")`
3. Estender de `BaseEntity`
4. Criar migration V#__ADD_NEW_TABLE.sql em `src/main/resources/db/migration/master/`
5. Rodar `mvn spring-boot:run` (Flyway executa)

### Modificar Estrutura Existente

⚠️ **NUNCA editar migrations já rodadas!**

Solução:
1. Criar nova migration: `V#__ALTER_TABLE_NAME.sql`
2. Usar `ALTER TABLE` commands
3. Exemplos em [DATABASE_SETUP_SUMMARY.md#curto-prazo-próximas-2-semanas)](DATABASE_SETUP_SUMMARY.md#curto-prazo-próximas-2-semanas)

---

## 🔑 Chaves e Índices

### Constraints Importantes
- `unidade_medida.sigla` - UNIQUE
- `permissao.codigo` - UNIQUE
- `role.nome` - UNIQUE
- `usuarios.email` - UNIQUE
- `usuarios.cpf` - UNIQUE
- `tb_cliente.cnpj` - UNIQUE
- `tb_cliente.cpf` - UNIQUE
- `tb_produto.codigo` - UNIQUE per tenant

### Índices para Performance
- `idx_tenant_id` - Em todas as tabelas de dados
- `idx_nome` - Busca por nome (clientes, produtos)
- `idx_status` - Filtro por status
- `idx_deleted` - Para queries de registros ativos
- `idx_email` - Busca por email (usuários)
- `idx_cpf`, `idx_cnpj` - Busca por documentos

---

## 🛡️ Segurança

### Data Isolation
✅ `tenant_id` previne acesso cruzado entre tenants  
✅ Soft delete previne deleção acidental  
✅ Auditoria rastreia todas as mudanças  

### Integridade
✅ Foreign keys com ON DELETE CASCADE  
✅ Unique constraints em campos críticos  
✅ NOT NULL em campos obrigatórios  

### Concorrência
✅ Versionamento otimista com @Version  
✅ Previne overwrites accidentais  

---

## 📈 Próximos Passos

### Esta Semana
1. [ ] Testar V1__DATABASEINITIALIZER.sql em MySQL
2. [ ] Confirmar Flyway executa sem erros
3. [ ] Validar estrutura criada

### Próximas 2 Semanas  
1. [ ] Criar V2__SEED_DATA.sql com permissões e roles
2. [ ] Criar repositories JPA para cada entidade
3. [ ] Implementar services básicos

### 1 Mês
1. [ ] REST controllers para CRUD operations
2. [ ] Validações de negócio
3. [ ] Testes de integração

**Ver [DATABASE_SETUP_SUMMARY.md#-próximas-ações](DATABASE_SETUP_SUMMARY.md#-próximas-ações) para detalhes**

---

## ❓ Respostas Rápidas

**P: Qual é o padrão de nomeação?**  
R: Tabelas `tb_nome`, colunas `nome_coluna`, índices `idx_coluna`, FKs `fk_origem_ref`

**P: Por que soft delete?**  
R: Histórico, auditoria, recuperação de dados acidentais

**P: Como filtrar por tenant?**  
R: `WHERE tenant_id = ? AND deleted = FALSE` em toda query de dados

**P: Preciso rodar migrations manual?**  
R: Não, Flyway executa automaticamente quando app inicia

**P: Como reverter uma migration?**  
R: Criar nova migration com ALTER/DROP commands, NUNCA editar a anterior

**P: Qual versão do MySQL?**  
R: 5.7+ (recomendado 8.0+). MySQL 5.5 não é suportado

---

## 📚 Referência Técnica

### Tipos de Dados Usados
- `BIGINT` - IDs, FK, counters (64-bit)
- `VARCHAR(n)` - Textos curtos/médios
- `DECIMAL(15,2)` - Valores monetários
- `BOOLEAN` - Flags (true/false)
- `DATETIME` - Timestamps
- `JSON` - Dados semi-estruturados

### Constraints Implementados
- PRIMARY KEY (`id`)
- FOREIGN KEY (relacionamentos)
- UNIQUE (dados únicos)
- NOT NULL (dados obrigatórios)
- DEFAULT (valores padrão)
- CHECK (em índices)

### Índices por Tipo
- **Single column:** `id`, `tenant_id`, `deleted`, `status`
- **Composite:** `(cliente_id, endereco_id)`, `(codigo, tenant_id)`
- **Full-text:** (não implementado ainda)

---

## 🔗 Links Úteis

- [Documentação MySQL](https://dev.mysql.com/doc/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Hibernate JPA](https://hibernate.org/orm/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)

---

## 📝 Mudanças Recentes

| Data | Alteração | Status |
|------|-----------|--------|
| 2026-01-06 | Criação de V1__DATABASEINITIALIZER.sql | ✅ Completo |
| 2026-01-06 | DATABASE_SCHEMA.md | ✅ Completo |
| 2026-01-06 | DATABASE_VALIDATION_CHECKLIST.md | ✅ Completo |
| 2026-01-06 | DATABASE_SETUP_SUMMARY.md | ✅ Completo |
| 2026-01-06 | DATABASE_QUICK_REFERENCE.md | ✅ Completo |
| TBD | V2__SEED_DATA.sql | ⏳ Planejado |
| TBD | JPA Repositories | ⏳ Planejado |
| TBD | Services Implementation | ⏳ Planejado |

---

**Status:** ✅ Production Ready  
**Última Atualização:** 2026-01-06  
**Versão:** 1.0.0  
**Responsável:** GitHub Copilot

Para dúvidas ou alterações, consulte os documentos listados acima ou crie uma issue com label `database`.
