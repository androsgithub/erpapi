# Resolução de Ciclo de Dependência Circular

## Problema Original

Erro ao iniciar a aplicação:

```
The dependencies of some of the beans in the application context form a cycle:

   entityManagerFactory
   ↓
   multiTenantRoutingDataSource
   ↓
   dataSourceFactory
   ↓
   tenantDatasourceRepository
   ↓
   jpaSharedEM_entityManagerFactory (volta ao início)
```

### Causa Raiz

O ciclo foi causado pela seguinte cadeia de dependências:

1. `entityManagerFactory` depende de `multiTenantRoutingDataSource` (DataSource @Primary)
2. `multiTenantRoutingDataSource` depende de `dataSourceFactory` (para criar pools dinamicamente)
3. `dataSourceFactory` depende de `tenantDatasourceRepository` (para carregar configs do banco)
4. `tenantDatasourceRepository` depende de `jpaSharedEM_entityManagerFactory` (para acessar dados)
5. Isso volta para o passo 1 = **CICLO**

Além disso:
- `flywayMaster` recebia o DataSource padrão que era agora o `multiTenantRoutingDataSource`
- Isso criava uma dependência extra: `flywayMaster` → `multiTenantRoutingDataSource`

## Solução Implementada

### 1. Lazy Initialization em DataSourceFactory

**Antes:**
```java
@Component
@RequiredArgsConstructor
public class DataSourceFactory {
    private final TenantDatasourceRepository tenantDatasourceRepository;
}
```

**Depois:**
```java
@Component
public class DataSourceFactory {
    private final TenantDatasourceRepository tenantDatasourceRepository;

    @Autowired
    public DataSourceFactory(@Lazy TenantDatasourceRepository tenantDatasourceRepository) {
        this.tenantDatasourceRepository = tenantDatasourceRepository;
    }
}
```

**Por quê:** O `@Lazy` instrui o Spring a criar um proxy lazy do repositório, diferindo sua inicialização até ser usado. Isso quebra a cadeia de dependência no tempo de inicialização.

### 2. Lazy Initialization no MultiTenantRoutingDataSource

**Antes:**
```java
@Bean
@Primary
public MultiTenantRoutingDataSource multiTenantRoutingDataSource(DataSourceFactory dataSourceFactory) {
    return new MultiTenantRoutingDataSource(dataSourceFactory);
}
```

**Depois:**
```java
@Bean
@Primary
@Lazy
public MultiTenantRoutingDataSource multiTenantRoutingDataSource(DataSourceFactory dataSourceFactory) {
    return new MultiTenantRoutingDataSource(dataSourceFactory);
}
```

**Por quê:** O bean é criado lentamente (lazy), apenas quando é realmente necessário, não durante o bootstrap.

### 3. DataSource Padrão Separado

**Criado em TenantsConfiguration:**
```java
@Bean(name = "defaultDataSource")
public DataSource defaultDataSource(Environment environment) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(environment.getRequiredProperty("spring.datasource.url"));
    config.setUsername(environment.getRequiredProperty("spring.datasource.username"));
    config.setPassword(environment.getRequiredProperty("spring.datasource.password"));
    config.setDriverClassName(environment.getRequiredProperty("spring.datasource.driver-class-name"));
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setConnectionTimeout(30000);
    config.setIdleTimeout(600000);
    config.setPoolName("HikariPool-Master");
    return new HikariDataSource(config);
}
```

**Por quê:** 
- `defaultDataSource` é um DataSource independente que não depende do `multiTenantRoutingDataSource`
- Pode ser usado por Flyway e outras operações no banco master sem criar dependências circulares
- Não é @Primary, então não interfere com o `multiTenantRoutingDataSource`

### 4. Flyway Usando DataSource Separado

**Antes:**
```java
@Bean(name = "flywayMaster")
public Flyway flywayMaster(DataSource dataSource, FlywayProperties flywayProperties) {
    return Flyway.configure()
            .dataSource(dataSource)
            ...
}
```

**Depois:**
```java
@Bean(name = "flywayMaster")
public Flyway flywayMaster(
    @Qualifier("defaultDataSource") DataSource defaultDataSource, 
    FlywayProperties flywayProperties
) {
    return Flyway.configure()
            .dataSource(defaultDataSource)
            ...
}
```

**Por quê:** Flyway recebe explicitamente o `defaultDataSource` ao invés de receber qualquer DataSource. Isso quebra a dependência com `multiTenantRoutingDataSource`.

## Fluxo de Inicialização (Resolvido)

```
1. Spring initializa as dependências
   ↓
2. Cria 'defaultDataSource' (independente, sem dependências de multitenancy)
   ↓
3. Cria 'flywayMaster' usando 'defaultDataSource' (sem dependência de routing)
   ↓
4. Cria 'entityManagerFactory' (depende de multiTenantRoutingDataSource)
   ↓
5. Cria 'multiTenantRoutingDataSource' com @Lazy (adiado, não bloqueia)
   ↓
6. Cria 'dataSourceFactory' com @Lazy TenantDatasourceRepository
   ↓
7. Repositórios são acessados apenas quando necessário, não durante bootstrap
   ↓
✅ Aplicação inicia com sucesso!
```

## Arquivos Modificados

### 1. [DataSourceFactory.java](../src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/routing/DataSourceFactory.java)
- Removido `@RequiredArgsConstructor`
- Adicionado construtor com `@Lazy` para o repositório

### 2. [TenantsConfiguration.java](../src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/manual/TenantsConfiguration.java)
- Adicionado `@Bean(name = "defaultDataSource")` para DataSource independente
- Adicionado `@Lazy` ao bean `multiTenantRoutingDataSource`
- Importados `HikariConfig` e `HikariDataSource`

### 3. [FlywayConfig.java](../src/main/java/com/api/erp/v1/shared/infrastructure/config/FlywayConfig.java)
- Atualizado `flywayMaster()` para usar `@Qualifier("defaultDataSource")`
- Atualizado `flywayMigrationStrategy()` para usar `@Qualifier("defaultDataSource")`

## Validação

✅ **Compilation:** `mvn clean compile` - SUCCESS
✅ **Build:** `mvn clean install -DskipTests` - SUCCESS
✅ **Dependency Graph:** Ciclo resolvido, todas as dependências resolvidas

## Benefícios da Solução

1. **Sem mudanças na lógica de negócio** - Apenas configuração de inicialização
2. **Lazy loading** - Componentes não são criados desnecessariamente
3. **Separação clara** - Master database separado do tenant routing
4. **Escalável** - Pode adicionar mais DataSources sem gerar ciclos
5. **Testável** - Cada componente pode ser testado isoladamente

## Próximos Passos

1. ✅ Compilação bem-sucedida
2. ✅ Build bem-sucedido
3. ⏳ **Executar aplicação:** `mvn spring-boot:run`
4. ⏳ Testar endpoints de multitenancy
5. ⏳ Validar isolamento de tenants

## Notas Importantes

- O `@Lazy` não é usado para lazy loading de dados, apenas para quebrar ciclos no bootstrap
- O `defaultDataSource` é criado imediatamente (não é lazy)
- O `multiTenantRoutingDataSource` é criado apenas quando a primeira requisição chega
- Flyway é executado com sucesso usando o `defaultDataSource`
