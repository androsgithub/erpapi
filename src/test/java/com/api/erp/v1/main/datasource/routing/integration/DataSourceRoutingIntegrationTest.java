package com.api.erp.v1.main.datasource.routing.integration;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.datasource.routing.cache.DSCache;
import com.api.erp.v1.main.datasource.routing.core.CustomRoutingDatasource;
import com.api.erp.v1.main.datasource.routing.core.DataSourceRouter;
import com.api.erp.v1.main.datasource.routing.domain.ITenantConfigProvider;
import com.api.erp.v1.main.datasource.routing.infrastructure.HikariDataSourceFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * TESTES DE INTEGRAÇÃO - DataSource Routing (End-to-End)
 *
 * Fluxo completo: TenantContext → DSCache → DataSourceRouter → CustomRoutingDatasource
 * Usa H2 in-memory (sem Docker).
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("DataSource Routing - Testes de Integração")
class DataSourceRoutingIntegrationTest {

    private CustomRoutingDatasource routingDatasource;
    private DataSourceRouter router;
    private DSCache dsCache;
    private HikariDataSource tenant1DS;
    private HikariDataSource tenant2DS;

    @BeforeEach
    void setUp() {
        dsCache = new DSCache();
        ITenantConfigProvider configProvider = mock(ITenantConfigProvider.class);
        HikariDataSourceFactory factory = new HikariDataSourceFactory();

        router = new DataSourceRouter(
                createH2DataSource("master_db"),
                configProvider,
                factory,
                dsCache
        );

        routingDatasource = new CustomRoutingDatasource(router);

        // Criar DataSources H2 reais para cada tenant
        tenant1DS = createH2DataSource("tenant1_int_test");
        tenant2DS = createH2DataSource("tenant2_int_test");

        // Registrar no router
        router.registerDataSource(1L, tenant1DS);
        router.registerDataSource(2L, tenant2DS);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
        if (tenant1DS != null && !tenant1DS.isClosed()) tenant1DS.close();
        if (tenant2DS != null && !tenant2DS.isClosed()) tenant2DS.close();
    }

    @Test
    @DisplayName("dado_tenantContext1_quando_getConnection_entao_conectaAoBancoDoTenant1")
    void testGivenTenantContext1_WhenGetConnection_ThenConnectsToTenant1Database() throws SQLException {
        TenantContext.setTenantId(1L);

        try (Connection conn = routingDatasource.getConnection()) {
            assertThat(conn).isNotNull();
            assertThat(conn.isClosed()).isFalse();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt(1)).isEqualTo(1);
            }
        }
    }

    @Test
    @DisplayName("dado_tenantContext2_quando_getConnection_entao_conectaAoBancoDoTenant2")
    void testGivenTenantContext2_WhenGetConnection_ThenConnectsToTenant2Database() throws SQLException {
        TenantContext.setTenantId(2L);

        try (Connection conn = routingDatasource.getConnection()) {
            assertThat(conn).isNotNull();
            assertThat(conn.isClosed()).isFalse();
        }
    }

    @Test
    @DisplayName("dado_trocaDeTenant_quando_getConnection_entao_isolamentoEntreConexoes")
    void testGivenTenantSwitch_WhenGetConnection_ThenConnectionsAreIsolated() throws SQLException {
        // Criar tabela no tenant1
        TenantContext.setTenantId(1L);
        try (Connection conn = routingDatasource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS test_isolation (id INT)");
            stmt.execute("INSERT INTO test_isolation VALUES (100)");
        }

        // Trocar para tenant2 - tabela não deve existir
        TenantContext.setTenantId(2L);
        try (Connection conn = routingDatasource.getConnection();
             Statement stmt = conn.createStatement()) {
            // Tabela test_isolation não existe no tenant2
            assertThatThrownBy(() -> stmt.executeQuery("SELECT * FROM test_isolation"))
                    .isInstanceOf(SQLException.class);
        }
    }

    @Test
    @DisplayName("dado_tenantContextNulo_quando_getConnection_entao_lancaSQLException")
    void testGivenNullTenantContext_WhenGetConnection_ThenThrowsSQLException() {
        TenantContext.clear();

        assertThatThrownBy(() -> routingDatasource.getConnection())
                .isInstanceOf(SQLException.class);
    }

    @Test
    @DisplayName("dado_2TenantsRegistrados_quando_isDataSourceCached_entao_ambosNoCacheok")
    void testGiven2RegisteredTenants_WhenIsDataSourceCached_ThenBothCached() {
        assertThat(router.isDataSourceCached(1L)).isTrue();
        assertThat(router.isDataSourceCached(2L)).isTrue();
        assertThat(router.isDataSourceCached(999L)).isFalse();
    }

    @Test
    @DisplayName("dado_invalidarERegistrarNovamente_quando_getConnection_entao_novaConexaoFunciona")
    void testGivenInvalidateAndReRegister_WhenGetConnection_ThenNewConnectionWorks() throws SQLException {
        // Invalidar tenant1
        router.invalidateDataSourceCache(1L);
        assertThat(router.isDataSourceCached(1L)).isFalse();

        // Re-registrar com novo DataSource
        HikariDataSource newDS = createH2DataSource("tenant1_new_int");
        router.registerDataSource(1L, newDS);

        TenantContext.setTenantId(1L);
        try (Connection conn = routingDatasource.getConnection()) {
            assertThat(conn).isNotNull();
            assertThat(conn.isClosed()).isFalse();
        }

        newDS.close();
    }

    private HikariDataSource createH2DataSource(String dbName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(2);
        config.setMinimumIdle(1);
        config.setPoolName("test-pool-" + dbName);
        return new HikariDataSource(config);
    }
}
