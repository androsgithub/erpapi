package com.api.erp.v1.main.datasource.routing.integration;

import com.api.erp.v1.main.datasource.routing.domain.TenantDSConfig;
import com.api.erp.v1.main.datasource.routing.infrastructure.HikariDataSourceFactory;
import com.api.erp.v1.main.master.tenant.domain.entity.DBType;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES DE INTEGRAÇÃO - HikariDataSourceFactory
 *
 * Cria DataSources reais com H2 e valida pool config.
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("HikariDataSourceFactory - Testes de Integração")
class HikariDataSourceFactoryIntegrationTest {

    private HikariDataSourceFactory factory;
    private List<HikariDataSource> createdDataSources;

    @BeforeEach
    void setUp() {
        factory = new HikariDataSourceFactory();
        createdDataSources = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        for (HikariDataSource ds : createdDataSources) {
            if (ds != null && !ds.isClosed()) {
                ds.close();
            }
        }
    }

    @Test
    @DisplayName("dado_configH2Valido_quando_createDataSource_entao_retornaHikariDataSourceFuncional")
    void testGivenValidH2Config_WhenCreateDataSource_ThenReturnsFunctionalHikariDataSource() throws Exception {
        TenantDSConfig config = new TenantDSConfig(
                1L,
                "jdbc:h2:mem:factory_int_test1;DB_CLOSE_DELAY=-1",
                "sa",
                "",
                DBType.H2
        );

        HikariDataSource ds = (HikariDataSource) factory.createDataSource(config);
        createdDataSources.add(ds);

        assertThat(ds).isNotNull();
        assertThat(ds).isInstanceOf(HikariDataSource.class);

        // Obter conexão real e executar query
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("dado_configH2_quando_createDataSource_entao_poolNameContemTenantId")
    void testGivenH2Config_WhenCreateDataSource_ThenPoolNameContainsTenantId() {
        TenantDSConfig config = new TenantDSConfig(
                42L,
                "jdbc:h2:mem:factory_int_test2;DB_CLOSE_DELAY=-1",
                "sa",
                "",
                DBType.H2
        );

        HikariDataSource ds = (HikariDataSource) factory.createDataSource(config);
        createdDataSources.add(ds);

        assertThat(ds.getPoolName()).contains("42");
    }

    @Test
    @DisplayName("dado_configComPoolCustom_quando_createDataSourceComPool_entao_validaTamanhos")
    void testGivenConfigWithCustomPool_WhenCreateDataSourceWithPool_ThenValidatesSizes() {
        TenantDSConfig config = new TenantDSConfig(
                3L,
                "jdbc:h2:mem:factory_int_test3;DB_CLOSE_DELAY=-1",
                "sa",
                "",
                DBType.H2
        );

        HikariDataSource ds = (HikariDataSource) factory.createDataSource(config, 5, 1);
        createdDataSources.add(ds);

        assertThat(ds.getMaximumPoolSize()).isEqualTo(5);
        assertThat(ds.getMinimumIdle()).isEqualTo(1);
    }

    @Test
    @DisplayName("dado_multiplosCriarDataSource_quando_criarVarios_entao_todosIndependentes")
    void testGivenMultipleCreations_WhenCreateMultiple_ThenAllIndependent() throws Exception {
        TenantDSConfig c1 = new TenantDSConfig(1L, "jdbc:h2:mem:factory_multi1;DB_CLOSE_DELAY=-1", "sa", "", DBType.H2);
        TenantDSConfig c2 = new TenantDSConfig(2L, "jdbc:h2:mem:factory_multi2;DB_CLOSE_DELAY=-1", "sa", "", DBType.H2);

        HikariDataSource ds1 = (HikariDataSource) factory.createDataSource(c1);
        HikariDataSource ds2 = (HikariDataSource) factory.createDataSource(c2);
        createdDataSources.add(ds1);
        createdDataSources.add(ds2);

        assertThat(ds1).isNotSameAs(ds2);

        // Criar tabela no ds1, não deve existir no ds2
        try (Connection conn = ds1.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE test_factory (id INT)");
        }

        try (Connection conn = ds2.getConnection(); Statement stmt = conn.createStatement()) {
            assertThatThrownBy(() -> stmt.executeQuery("SELECT * FROM test_factory"))
                    .isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("dado_dataSourceCriado_quando_close_entao_isClosed")
    void testGivenCreatedDataSource_WhenClose_ThenIsClosed() {
        TenantDSConfig config = new TenantDSConfig(
                99L,
                "jdbc:h2:mem:factory_close_test;DB_CLOSE_DELAY=-1",
                "sa",
                "",
                DBType.H2
        );

        HikariDataSource ds = (HikariDataSource) factory.createDataSource(config);
        assertThat(ds.isClosed()).isFalse();

        ds.close();
        assertThat(ds.isClosed()).isTrue();
    }
}
