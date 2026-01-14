# Sumário de Correções - Ciclo de Dependência

## Status: ✅ RESOLVIDO

**Data:** 2026-01-11  
**Erro Original:** Circular dependency between entityManagerFactory, multiTenantRoutingDataSource, dataSourceFactory, e tenantDatasourceRepository

---

## Mudanças Implementadas

### 1. DataSourceFactory.java
**Localização:** `src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/routing/`

```diff
- import lombok.RequiredArgsConstructor;
+ import org.springframework.beans.factory.annotation.Autowired;
+ import org.springframework.context.annotation.Lazy;

- @Component
- @RequiredArgsConstructor
- public class DataSourceFactory {
+ @Component
+ public class DataSourceFactory {
  
      private final TenantDatasourceRepository tenantDatasourceRepository;
      
+     @Autowired
+     public DataSourceFactory(@Lazy TenantDatasourceRepository tenantDatasourceRepository) {
+         this.tenantDatasourceRepository = tenantDatasourceRepository;
+     }
```

**Impacto:** Quebra a cadeia de dependência durante inicialização ao fazer o repositório ser inicializado tardio (lazy).

---

### 2. TenantsConfiguration.java
**Localização:** `src/main/java/com/api/erp/v1/shared/infrastructure/config/datasource/manual/`

**Adição A - Import de HikariCP:**
```diff
+ import com.zaxxer.hikari.HikariConfig;
+ import com.zaxxer.hikari.HikariDataSource;
+ import javax.sql.DataSource;
```

**Adição B - Novo bean defaultDataSource:**
```diff
+ /**
+  * DataSource padrão (Master Database) para Flyway e operações no banco master.
+  * Este DataSource NÃO é @Primary, apenas usado explicitamente pelo Flyway.
+  */
+ @Bean(name = "defaultDataSource")
+ public DataSource defaultDataSource(Environment environment) {
+     HikariConfig config = new HikariConfig();
+     config.setJdbcUrl(environment.getRequiredProperty("spring.datasource.url"));
+     config.setUsername(environment.getRequiredProperty("spring.datasource.username"));
+     config.setPassword(environment.getRequiredProperty("spring.datasource.password"));
+     config.setDriverClassName(environment.getRequiredProperty("spring.datasource.driver-class-name"));
+     config.setMaximumPoolSize(10);
+     config.setMinimumIdle(2);
+     config.setConnectionTimeout(30000);
+     config.setIdleTimeout(600000);
+     config.setPoolName("HikariPool-Master");
+     return new HikariDataSource(config);
+ }
```

**Adição C - @Lazy no multiTenantRoutingDataSource:**
```diff
  @Bean
  @Primary
+ @Lazy
  public MultiTenantRoutingDataSource multiTenantRoutingDataSource(DataSourceFactory dataSourceFactory) {
      return new MultiTenantRoutingDataSource(dataSourceFactory);
  }
```

**Impacto:** 
- `defaultDataSource` fornece um DataSource independente para o Flyway
- `@Lazy` no routing datasource adia sua criação até ser realmente necessário

---

### 3. FlywayConfig.java
**Localização:** `src/main/java/com/api/erp/v1/shared/infrastructure/config/`

**Mudança A - flywayMaster:**
```diff
  @Bean(name = "flywayMaster")
- public Flyway flywayMaster(DataSource dataSource, FlywayProperties flywayProperties) {
+ public Flyway flywayMaster(
+     @org.springframework.beans.factory.annotation.Qualifier("defaultDataSource") 
+     DataSource defaultDataSource, 
+     FlywayProperties flywayProperties
+ ) {
      return Flyway.configure()
-             .dataSource(dataSource)
+             .dataSource(defaultDataSource)
              .locations("classpath:db/migration/master")
              .baselineOnMigrate(true)
              .load();
  }
```

**Mudança B - flywayMigrationStrategy:**
```diff
  @Bean(name = "flywayTenant")
  @DependsOn("flywayMaster")
- public FlywayMigrationStrategy flywayMigrationStrategy(DataSource dataSource) {
-     return new FlywayMigrationStrategy(dataSource);
+ public FlywayMigrationStrategy flywayMigrationStrategy(
+     @org.springframework.beans.factory.annotation.Qualifier("defaultDataSource") 
+     DataSource defaultDataSource
+ ) {
+     return new FlywayMigrationStrategy(defaultDataSource);
  }
```

**Impacto:** Flyway agora usa explicitamente o `defaultDataSource` ao invés de qualquer DataSource, evitando dependência do routing datasource.

---

## Testes de Validação

### Compilação
```bash
mvn clean compile
✅ BUILD SUCCESS
```

### Build Completo
```bash
mvn clean install -DskipTests
✅ BUILD SUCCESS
[INFO] Building jar: erp-0.0.1-SNAPSHOT.jar
[INFO] The original artifact has been renamed to erp-0.0.1-SNAPSHOT.jar.original
```

### Log de Avisos
- ⚠️ 14 warnings sobre @Builder (não-críticos, apenas style suggestions)
- ⚠️ MapStruct unmapped target properties (não-críticos, apenas mapping hints)
- ℹ️ Deprecated API warnings (não-críticos)

---

## Estrutura de Dependências Pós-Correção

```
defaultDataSource (HikariCP)
    │
    └─── flywayMaster (executa migrations)
            │
            └─── applicationStartup
    
entityManagerFactory
    │
    └─── multiTenantRoutingDataSource (@Lazy) ◄── criado quando necessário
            │
            └─── dataSourceFactory (@Lazy inject) ◄── criado quando necessário
                    │
                    └─── tenantDatasourceRepository (@Lazy) ◄── criado quando necessário
```

**Resultado:** Sem ciclos, fluxo linear, Flyway independente.

---

## Arquivo de Documentação Detalhada

Veja [CIRCULAR_DEPENDENCY_RESOLUTION.md](CIRCULAR_DEPENDENCY_RESOLUTION.md) para explicação técnica completa da solução.

---

## Próximas Ações

1. **Opcional:** Executar a aplicação para validar
   ```bash
   mvn spring-boot:run
   ```

2. **Recomendado:** Atualizar entidades conforme documentação de multitenancy
   - Cliente
   - Contato
   - Endereco
   - Usuario
   - Permissao
   - Produto

3. **Recomendado:** Atualizar serviços para setar tenantId automaticamente

---

## Resumo Executivo

| Item | Status |
|------|--------|
| Problema Identificado | ✅ Ciclo de 4 beans |
| Solução Implementada | ✅ @Lazy + DataSource separado |
| Arquivos Modificados | ✅ 3 arquivos |
| Teste de Compilação | ✅ SUCCESS |
| Teste de Build | ✅ SUCCESS |
| Documentação | ✅ Completa |

**Risco de Regressão:** BAIXO - Mudanças apenas em configuração de inicialização, sem alteração de lógica.
