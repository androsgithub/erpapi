package com.api.erp.v1.shared.infrastructure.config.datasource.routing;

import com.api.erp.v1.features.tenant.domain.entity.InternalTenantConfig;
import com.api.erp.v1.features.tenant.domain.entity.Tenant;
import com.api.erp.v1.features.tenant.domain.entity.TenantConfig;
import com.api.erp.v1.features.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.features.tenant.domain.repository.TenantDatasourceRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DataSourceFactory
 * <p>
 * Factory responsável por criar DataSources dinamicamente para cada tenant
 * baseado nas configurações armazenadas no banco master.
 * <p>
 * Usa JDBC direto para buscar configurações no banco master, evitando
 * ciclos de dependência com JPA/EntityManager.
 * <p>
 * Usa HikariCP para gerenciar pools de conexão eficientemente.
 */
@Slf4j
@Component
public class DataSourceFactory {

    private final DataSource defaultDataSource;
    private final TenantDatasourceRepository tenantDatasourceRepository;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private Integer hikariMaxPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:2}")
    private Integer hikariMinIdle;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private Integer hikariConnectionTimeout;

    @Value("${spring.datasource.hikari.idle-timeout:600000}")
    private Integer hikariIdleTimeout;

    /**
     * Construtor com @Lazy para quebrar ciclo de dependência.
     * <p>
     * O defaultDataSource é injetado PRIMEIRO para buscar configurações no banco master via JDBC direto.
     * O repository é lazily initialized para evitar que entityManagerFactory
     * dependa de DataSourceFactory que dependa de entityManagerFactory.
     */
    @Autowired
    public DataSourceFactory(
            @Qualifier("defaultDataSource") DataSource defaultDataSource,
            @Lazy TenantDatasourceRepository tenantDatasourceRepository) {
        this.defaultDataSource = defaultDataSource;
        this.tenantDatasourceRepository = tenantDatasourceRepository;
    }

    /**
     * Cria um novo DataSource para um tenant específico
     *
     * @param tenantId ID único do tenant
     * @return DataSource configurado com HikariCP ou null se não encontrado
     */
    public DataSource createDataSource(Long tenantId) {
        log.info("🔨 [DataSourceFactory] Criando DataSource para tenant: {}", tenantId);

        // Buscar configuração de datasource no banco master usando JDBC direto
        // Isso evita ciclo: MultiTenant -> DataSourceFactory -> Repository -> EntityManager -> MultiTenant
        TenantDatasource tenantDatasource = fetchTenantDatasourceViaJdbc(tenantId);

        if (tenantDatasource == null) {
            log.error("❌ [DataSourceFactory] TenantDatasource NÃO ENCONTRADO para ID: '{}'\n" +
                    "   VERIFIQUE:\n" +
                    "   1) SELECT COUNT(*) FROM tenant_datasource WHERE tenant_id = {} AND is_active = true\n" +
                    "   2) Existe tenant com ID='{}' na tb_tenant?\n" +
                    "   3) Existe linha em tenant_datasource apontando para esse tenant?\n" +
                    "   4) is_active=true?",
                    tenantId, tenantId, tenantId);
            return null;
        }

        log.info("✅ [DataSourceFactory] TenantDatasource ENCONTRADO para ID: '{}'", tenantId);
        return createDataSourceFromConfig(tenantDatasource);
    }

    /**
     * Busca TenantDatasource via JDBC direto (sem JPA) para evitar ciclos
     */
    private TenantDatasource fetchTenantDatasourceViaJdbc(Long tenantId) {
        String sql = "SELECT td.id, td.host, td.port, td.database_name, td.username, td.password, " +
                "       td.driver_class_name, td.tenant_id " +
                "FROM tenant_datasource td " +
                "WHERE td.tenant_id = ? AND td.is_active = true " +
                "LIMIT 1";

        try (Connection conn = defaultDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tenantId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TenantDatasource td = new TenantDatasource();
                    td.setId(rs.getLong("id"));
                    td.setHost(rs.getString("host"));
                    td.setPort(rs.getInt("port"));
                    td.setDatabaseName(rs.getString("database_name"));
                    td.setUsername(rs.getString("username"));
                    td.setPassword(rs.getString("password"));
                    td.setDriverClassName(rs.getString("driver_class_name"));

                    // Cria Tenant minimal com apenas ID
                    Tenant tenant = new Tenant();
                    tenant.setId(rs.getLong("tenant_id"));
                    td.setTenant(tenant);

                    log.debug("✅ TenantDatasource encontrado via JDBC para ID: {}", tenantId);
                    return td;
                }
            }
        } catch (SQLException e) {
            log.error("❌ Erro ao buscar TenantDatasource via JDBC para ID: {}", tenantId, e);
        }

        return null;
    }

    /**
     * Cria DataSource a partir da configuração de TenantDatasource
     */
    public DataSource createDataSourceFromConfig(TenantDatasource config) {
        log.info("Configurando DataSource para tenant ID: {} | DB: {}:{}/{}",
                config.getTenant().getId(),
                config.getHost(),
                config.getPort(),
                config.getDatabaseName());

        HikariConfig hikariConfig = new HikariConfig();

        // Configurações JDBC
        hikariConfig.setJdbcUrl(config.getJdbcUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setDriverClassName(config.getDriverClassName());

        // Pool configuration
        hikariConfig.setMaximumPoolSize(hikariMaxPoolSize);
        hikariConfig.setMinimumIdle(hikariMinIdle);
        hikariConfig.setConnectionTimeout(hikariConnectionTimeout);
        hikariConfig.setIdleTimeout(hikariIdleTimeout);

        // Pool name para identificação
        hikariConfig.setPoolName("HikariPool-Tenant-" + config.getTenant().getId());

        // Auto-commit
        hikariConfig.setAutoCommit(true);

        // Test query para validar conexão
        hikariConfig.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(hikariConfig);
    }
}
