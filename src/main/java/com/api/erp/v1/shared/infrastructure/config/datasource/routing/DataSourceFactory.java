package com.api.erp.v1.shared.infrastructure.config.datasource.routing;

import com.api.erp.v1.features.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.features.tenant.domain.repository.TenantDatasourceRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * DataSourceFactory
 * 
 * Factory responsável por criar DataSources dinamicamente para cada tenant
 * baseado nas configurações armazenadas no banco master.
 * 
 * Usa HikariCP para gerenciar pools de conexão eficientemente.
 */
@Slf4j
@Component
public class DataSourceFactory {

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
     * O repository é lazily initialized para evitar que entityManagerFactory
     * dependa de DataSourceFactory que depende de entityManagerFactory.
     */
    @Autowired
    public DataSourceFactory(@Lazy TenantDatasourceRepository tenantDatasourceRepository) {
        this.tenantDatasourceRepository = tenantDatasourceRepository;
    }

    /**
     * Cria um novo DataSource para um tenant específico
     * 
     * @param tenantSlug slug único do tenant
     * @return DataSource configurado com HikariCP ou null se não encontrado
     */
    public DataSource createDataSource(String tenantSlug) {
        log.debug("Criando DataSource para tenant: {}", tenantSlug);
        
        // Buscar configuração de datasource no banco master
        TenantDatasource tenantDatasource = tenantDatasourceRepository
                .findByTenant_SlugAndIsActive(tenantSlug, true)
                .orElse(null);

        if (tenantDatasource == null) {
            log.error("❌ TenantDatasource NÃO ENCONTRADO para slug: '{}' - Verifica: 1) existe tenant com nome='{}' na tb_tenant? 2) existe linha em tenant_datasource apontando para esse tenant? 3) is_active=true?", 
                tenantSlug, tenantSlug);
            return null;
        }

        log.info("✅ TenantDatasource ENCONTRADO para slug: '{}' - Tenant ID: {}", tenantSlug, tenantDatasource.getTenant().getId());
        return createDataSourceFromConfig(tenantDatasource);
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
