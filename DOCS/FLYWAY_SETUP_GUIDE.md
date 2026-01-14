# Guia de Configuração do Flyway com Multi-Tenant

## Visão Geral

O Flyway foi adicionado ao projeto para gerenciar migrações de banco de dados em um ambiente multi-tenant. A configuração suporta dois tipos de migrações:

1. **Master Database**: Contém as informações dos tenants
2. **Tenant Schemas**: Cada tenant tem seu próprio schema com suas próprias tabelas

## Estrutura de Diretórios

```
src/main/resources/db/migration/
├── master/              # Migrações do banco master
│   └── V1__Create_Master_Tables.sql
└── tenant/              # Migrações aplicadas a cada schema de tenant
    └── V1__Create_Tenant_Base_Tables.sql
```

## Convenção de Nomenclatura

As migrações seguem a convenção padrão do Flyway:

- **Versionadas**: `V<version>__<description>.sql`
  - `V1__Create_Master_Tables.sql`
  - `V2__Add_User_Table.sql`
  - `V3__Add_Indexes.sql`

- **Callbacks**: `<Event><Description>.sql`
  - `beforeMigrate.sql`
  - `afterMigrate.sql`

## Configuração no Application.properties

```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.out-of-order=false
spring.flyway.schemas=erpapi
spring.flyway.locations=classpath:db/migration/master,classpath:db/migration/tenant
spring.flyway.sql-migration-prefix=V
spring.flyway.sql-migration-separator=__
spring.flyway.sql-migration-suffix=.sql
```

## Classes Principais

### FlywayConfig.java
Configuração Spring que define os beans do Flyway.

- `flywayMaster()`: Configura migrações para o banco master
- `flywayMigrationStrategy()`: Estratégia para migrações de tenants

### FlywayMigrationStrategy.java
Implementação da lógica de migração para schemas de tenants.

**Métodos principais**:
- `migrateSchema(String schemaName)`: Aplica migrações a um schema específico
- `getMigrationStatus(String schemaName)`: Retorna o status das migrações
- `cleanSchema(String schemaName)`: Limpa o histórico de migrações (⚠️ Destrutivo)

### TenantMigrationService.java
Serviço injetável que fornece uma interface para gerenciar migrações.

**Métodos principais**:
- `provisioning(String tenantId, String schemaName)`: Provisiona um novo tenant
- `getStatus(String tenantId, String schemaName)`: Verifica status das migrações
- `cleanTenant(String tenantId, String schemaName)`: Limpa um tenant (⚠️ Destrutivo)

## Como Usar

### 1. Adicionar uma Nova Migração Master

Crie um arquivo em `src/main/resources/db/migration/master/`:

```sql
-- V2__Add_Tenant_Status.sql
ALTER TABLE tenants ADD COLUMN status VARCHAR(50) DEFAULT 'ACTIVE';
CREATE INDEX idx_tenants_status ON tenants(status);
```

### 2. Adicionar uma Nova Migração Tenant

Crie um arquivo em `src/main/resources/db/migration/tenant/`:

```sql
-- V2__Add_Products_Table.sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) UNIQUE,
    price DECIMAL(10, 2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3. Provisionar um Novo Tenant

Injetar `TenantMigrationService` e chamar o método de provisionamento:

```java
@Service
public class TenantService {
    
    private final TenantMigrationService migrationService;
    
    public TenantService(TenantMigrationService migrationService) {
        this.migrationService = migrationService;
    }
    
    public void createTenant(String tenantId, String schemaName) {
        // ... criar tenant no master database ...
        
        // Provisionar schema do tenant
        boolean success = migrationService.provisioning(tenantId, schemaName);
        if (!success) {
            throw new RuntimeException("Falha ao provisionar tenant");
        }
    }
}
```

### 4. Verificar Status das Migrações

```java
String status = migrationService.getStatus(tenantId, schemaName);
System.out.println(status); // Output: Migrations pending: 0, Applied: 2
```

## Fluxo de Inicialização

1. **Startup da Aplicação**
   - Spring inicia o contexto
   - FlywayConfig cria os beans do Flyway
   - Flyway automático executa migrações master
   - FlywayMigrationStrategy fica disponível para migrações dinâmicas

2. **Provisionamento de Novo Tenant**
   - Novo tenant é criado no master database
   - Schema do tenant é criado (via JPA/Hibernate)
   - `TenantMigrationService.provisioning()` é chamado
   - FlywayMigrationStrategy aplica migrações tenant para o novo schema

## Configurações Importantes

### spring.jpa.hibernate.ddl-auto
Alterado de `update` para `validate`:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Isso força o uso do Flyway para controlar as migrações em vez do Hibernate gerar DDL automaticamente.

### Histórico de Migrações

Cada schema tem sua própria tabela de histórico:
- Master: `flyway_schema_history` (criada automáticamente)
- Tenant: `_<schema_name>_flyway_schema_history`

## Boas Práticas

1. **Versionamento Sequencial**: Sempre incremente a versão (V1, V2, V3...)
2. **Descrições Claras**: Use descrições significativas nos nomes dos arquivos
3. **Idempotência**: Migrações podem ser executadas múltiplas vezes com segurança
4. **Reversão**: Flyway não suporta undo automático - planeje com cuidado
5. **Testes**: Teste as migrações em um ambiente de teste antes de produção

## Troubleshooting

### Erro: "Detected failed migration"

Se uma migração falhar, Flyway bloqueia futuras migrações. Para resolver em desenvolvimento:

1. Corrigir o script SQL da migração falhada
2. Usar `TenantMigrationService.cleanTenant()` para limpar o histórico
3. Executar novamente

⚠️ Em produção, essa situação requer intervenção manual!

### Schema não encontrado

Certifique-se de que:
1. O schema foi criado antes de chamar `migrateSchema()`
2. O usuário do banco tem permissão para acessar o schema
3. O nome do schema está correto

## Próximos Passos

1. Integrar o provisionamento de tenant com a criação de schema
2. Adicionar validação de integridade referencial nas migrações
3. Implementar suporte a rollback controlado
4. Criar scripts de teste para as migrações
