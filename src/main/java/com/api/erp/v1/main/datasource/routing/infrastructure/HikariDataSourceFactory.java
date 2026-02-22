package com.api.erp.v1.main.datasource.routing.infrastructure;

import com.api.erp.v1.main.datasource.routing.domain.TenantDSConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * INFRASTRUCTURE - Factory para criação de DataSources HikariCP
 * 
 * Encapsula toda a lógica de criação e configuração de DataSources.
 * Aplica validações e configurações padrão para todos os pools.
 * 
 * Responsabilidade: Criar DataSources prontos para uso
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@Slf4j
public class HikariDataSourceFactory {

    // Configurações padrão de pool
    private static final int DEFAULT_MAX_POOL_SIZE = 10;
    private static final int DEFAULT_MIN_IDLE = 2;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 30000;
    private static final int DEFAULT_IDLE_TIMEOUT = 600000;
    private static final int DEFAULT_MAX_LIFETIME = 1800000;

    /**
     * Cria um DataSource HikariCP configurado com defaults
     */
    public DataSource createDataSource(TenantDSConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("TenantDataSourceConfig não pode ser null");
        }

        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getDbUrl());
            hikariConfig.setUsername(config.getDbUsername());
            hikariConfig.setPassword(config.getDbPassword());
            hikariConfig.setDriverClassName(config.getDbDriverClassName());
            
            // Aplicar configurações padrão de pool
            hikariConfig.setMaximumPoolSize(DEFAULT_MAX_POOL_SIZE);
            hikariConfig.setMinimumIdle(DEFAULT_MIN_IDLE);
            hikariConfig.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
            hikariConfig.setIdleTimeout(DEFAULT_IDLE_TIMEOUT);
            hikariConfig.setMaxLifetime(DEFAULT_MAX_LIFETIME);
            
            hikariConfig.setPoolName("HikariPool-Tenant-" + config.getTenantId());
            hikariConfig.setAutoCommit(true);

            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            
            log.info("DataSource criado com sucesso para tenant: {}", config.getTenantId());
            return dataSource;
        } catch (Exception e) {
            log.error("Erro ao criar DataSource para tenant: {}", config.getTenantId(), e);
            throw new RuntimeException("Falha ao criar DataSource para tenant: " + config.getTenantId(), e);
        }
    }

    /**
     * Cria um DataSource HikariCP com configurações customizadas
     */
    public DataSource createDataSource(TenantDSConfig config, int maxPoolSize, int minIdle) {
        if (config == null) {
            throw new IllegalArgumentException("TenantDataSourceConfig não pode ser null");
        }

        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getDbUrl());
            hikariConfig.setUsername(config.getDbUsername());
            hikariConfig.setPassword(config.getDbPassword());
            hikariConfig.setDriverClassName(config.getDbDriverClassName());
            
            // Aplicar configurações customizadas
            hikariConfig.setMaximumPoolSize(maxPoolSize <= 0 ? DEFAULT_MAX_POOL_SIZE : maxPoolSize);
            hikariConfig.setMinimumIdle(minIdle < 0 ? DEFAULT_MIN_IDLE : minIdle);
            hikariConfig.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
            hikariConfig.setIdleTimeout(DEFAULT_IDLE_TIMEOUT);
            hikariConfig.setMaxLifetime(DEFAULT_MAX_LIFETIME);
            hikariConfig.setPoolName("HikariPool-Tenant-" + config.getTenantId());
            hikariConfig.setAutoCommit(true);

            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            
            log.info("DataSource criado com sucesso para tenant: {} (poolSize: {})", 
                    config.getTenantId(), maxPoolSize);
            return dataSource;
        } catch (Exception e) {
            log.error("Erro ao criar DataSource para tenant: {}", config.getTenantId(), e);
            throw new RuntimeException("Falha ao criar DataSource para tenant: " + config.getTenantId(), e);
        }
    }
}
