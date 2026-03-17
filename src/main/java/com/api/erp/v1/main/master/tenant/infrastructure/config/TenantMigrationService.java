package com.api.erp.v1.main.master.tenant.infrastructure.config;

import com.api.erp.v1.main.master.tenant.domain.entity.DBType;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@Slf4j
public class TenantMigrationService {

    private final TenantRepository tenantRepository;
    private final TenantDatasourceRepository tenantDatasourceRepository;

    public TenantMigrationService(TenantRepository tenantRepository,
                                  TenantDatasourceRepository tenantDatasourceRepository) {
        this.tenantRepository = tenantRepository;
        this.tenantDatasourceRepository = tenantDatasourceRepository;
    }

    private void migrateTenant(String tenantName, TenantDatasource datasource) throws Exception {
        // Creates temporary DataSource for tenant
        HikariDataSource tenantDataSource = createDataSourceForTenant(datasource);

        try {
            // Tenta validar conexão
            validateConnection(tenantDataSource, tenantName);

            // Configura e executa Flyway
            log.info("🔧 Configurando Flyway para tenant: {}", tenantName);

            DBType dbType;
            try (var conn = tenantDataSource.getConnection()) {
                dbType = DBType.fromNome(conn.getMetaData().getDatabaseProductName());
            } catch (Exception e) {
                dbType = DBType.MYSQL;
                log.error("Error getting dbType by driver during migrations! Using DBType.MYSQL as default");
            }

            String flywayLocation = "classpath:db/migration/tenant/" + dbType.getName().toLowerCase();

            Flyway flyway = Flyway.configure()
                    .dataSource(tenantDataSource)
                    .locations(flywayLocation)
                    .baselineOnMigrate(true)
                    .validateOnMigrate(false)
                    .table("tenant_erpapi_migrations_history")  // Tabela padrão do Flyway
                    .load();

            // Tenta repair (remove migrações falhadas se houver)
            try {
                log.debug("Running repair to clean up failed migrations...");
                flyway.repair();
                log.debug("Repair completed");
            } catch (Exception e) {
                log.debug("ℹ️ Repair not necessary or expected error: {}", e.getMessage());
            }

            // Execute migrations
            log.info("📊 Running migrations...");
            var result = flyway.migrate();
            log.info("Migrations executed successfully!");
            log.info("   - Current version: {}", result.success ? "OK" : "ERROR");
            log.info("   - Migrations applied: {}", result.migrationsExecuted);

        } finally {
            // Always close the connection
            if (tenantDataSource != null && !tenantDataSource.isClosed()) {
                log.debug("Closing tenant connection: {}", tenantName);
                tenantDataSource.close();
            }
        }
    }

    public void migrateTenantById(Long tenantId) throws Exception {
        log.info("Running migration for tenant ID: {}", tenantId);

        var tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant == null || !tenant.getActive()) {
            throw new IllegalArgumentException("Tenant not found or inactive: " + tenantId);
        }

        TenantDatasource datasource = tenantDatasourceRepository
                .findByTenantIdAndActiveTrue(tenant.getId()).orElse(null);

        if (datasource == null) {
            throw new IllegalArgumentException("Datasource not configured for tenant: " + tenantId);
        }

        migrateTenant(tenant.getName(), datasource);
    }

    private HikariDataSource createDataSourceForTenant(TenantDatasource datasource) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(datasource.getJdbcUrl());
        config.setUsername(datasource.getUsername());
        config.setPassword(datasource.getPassword());
        config.setMaximumPoolSize(2);  // Pequeno pool já que é temporário
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);  // 10 segundos
        config.setIdleTimeout(30000);  // 30 segundos
        config.setAutoCommit(true);

        log.debug("📦 Creating HikariDataSource for: {}", datasource.getDbType().getName());
        return new HikariDataSource(config);
    }

    private void validateConnection(DataSource dataSource, String tenantName) throws Exception {
        log.debug("Validating database connection: {}", tenantName);

        try (var connection = dataSource.getConnection()) {
            var metaData = connection.getMetaData();
            log.debug("   ✓ Database: {}", metaData.getDatabaseProductName());
            log.debug("   ✓ Version: {}", metaData.getDatabaseProductVersion());
            log.debug("   ✓ Connection active and functional!");
        } catch (Exception e) {
            log.error("❌ Error connecting to database: {}", tenantName, e);
            throw new RuntimeException("Unable to connect to tenant database: " + tenantName, e);
        }
    }

    public static class MigrationReport {
        private int successCount = 0;
        private int failureCount = 0;
        private String criticalError;
        private final java.util.Map<String, String> failures = new java.util.HashMap<>();

        public void addSuccess(String tenantName) {
            successCount++;
        }

        public void addFailure(String tenantName, String error) {
            failureCount++;
            failures.put(tenantName, error);
        }

        public void setCriticalError(String error) {
            this.criticalError = error;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public boolean hasFailures() {
            return failureCount > 0 || criticalError != null;
        }

        public java.util.Map<String, String> getFailures() {
            return failures;
        }

        public String getCriticalError() {
            return criticalError;
        }

        @Override
        public String toString() {
            return "MigrationReport{" +
                    "successCount=" + successCount +
                    ", failureCount=" + failureCount +
                    ", criticalError='" + criticalError + '\'' +
                    ", failures=" + failures +
                    '}';
        }
    }
}
