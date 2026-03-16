package com.api.erp.v1.main.datasource.routing.integration;

import com.api.erp.v1.main.datasource.routing.domain.TenantDSConfig;
import com.api.erp.v1.main.datasource.routing.infrastructure.JdbcTenantConfigProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES DE INTEGRAÇÃO - JdbcTenantConfigProvider
 *
 * Usa H2 in-memory simulando a tabela tb_tenant_datasource.
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("JdbcTenantConfigProvider - Testes de Integração")
class JdbcTenantConfigProviderIntegrationTest {

    private HikariDataSource masterDS;
    private JdbcTenantConfigProvider provider;

    @BeforeEach
    void setUp() throws Exception {
        masterDS = createMasterDataSource();
        createTenantDatasourceTable(masterDS);
        provider = new JdbcTenantConfigProvider(masterDS);
    }

    @AfterEach
    void tearDown() {
        if (masterDS != null && !masterDS.isClosed()) masterDS.close();
    }

    @Test
    @DisplayName("dado_tenantExistente_quando_getTenantConfig_entao_retornaConfigCorreto")
    void testGivenExistingTenant_WhenGetTenantConfig_ThenReturnsCorrectConfig() {
        Optional<TenantDSConfig> config = provider.getTenantConfig(1L);

        assertThat(config).isPresent();
        assertThat(config.get().getTenantId()).isEqualTo(1L);
        assertThat(config.get().getDbUrl()).isNotNull().contains("jdbc:");
        assertThat(config.get().getDbUsername()).isEqualTo("user1");
        assertThat(config.get().getDbPassword()).isEqualTo("pass1");
    }

    @Test
    @DisplayName("dado_tenantInexistente_quando_getTenantConfig_entao_lancaExcecao")
    void testGivenNonExistentTenant_WhenGetTenantConfig_ThenThrowsException() {
        assertThatThrownBy(() -> provider.getTenantConfig(999L))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("dado_tenantExistente_quando_tenantExists_entao_retornaTrue")
    void testGivenExistingTenant_WhenTenantExists_ThenReturnsTrue() {
        assertThat(provider.tenantExists("1")).isTrue();
    }

    @Test
    @DisplayName("dado_tenantInexistente_quando_tenantExists_entao_retornaFalse")
    void testGivenNonExistentTenant_WhenTenantExists_ThenReturnsFalse() {
        assertThat(provider.tenantExists("999")).isFalse();
    }

    private HikariDataSource createMasterDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:master_jdbc_int_test;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(2);
        config.setPoolName("master-jdbc-int-test");
        return new HikariDataSource(config);
    }

    private void createTenantDatasourceTable(DataSource ds) throws Exception {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tb_tenant_datasource (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    tenant_id BIGINT NOT NULL,
                    host VARCHAR(255) NOT NULL DEFAULT 'localhost',
                    port INT NOT NULL DEFAULT 5432,
                    database_name VARCHAR(255) NOT NULL,
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    db_type VARCHAR(50) NOT NULL DEFAULT 'POSTGRESQL',
                    is_active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);
            stmt.execute("""
                INSERT INTO tb_tenant_datasource (tenant_id, host, port, database_name, username, password, db_type, is_active) 
                VALUES (1, 'localhost', 5432, 'tenant1_db', 'user1', 'pass1', 'POSTGRESQL', true)
            """);
            stmt.execute("""
                INSERT INTO tb_tenant_datasource (tenant_id, host, port, database_name, username, password, db_type, is_active) 
                VALUES (2, 'localhost', 3306, 'tenant2_db', 'user2', 'pass2', 'MYSQL', true)
            """);
        }
    }
}
