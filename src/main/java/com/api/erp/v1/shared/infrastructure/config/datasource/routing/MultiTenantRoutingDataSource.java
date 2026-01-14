package com.api.erp.v1.shared.infrastructure.config.datasource.routing;

import com.api.erp.v1.shared.infrastructure.config.datasource.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * MultiTenantRoutingDataSource
 * 
 * DataSource que roteia conexões para o banco correto baseado no tenantSlug.
 * 
 * Fluxo:
 * 1. Requisição chega com tenantSlug no header ou JWT
 * 2. TenantContext.setTenantSlug(slug)
 * 3. MultiTenantRoutingDataSource obtém tenantSlug do contexto
 * 4. Busca datasource correspondente no cache
 * 5. Retorna conexão para o banco do tenant
 */
@Slf4j
public class MultiTenantRoutingDataSource extends AbstractDataSource {

    private final Map<String, DataSource> dataSources = new HashMap<>();
    private final DataSourceFactory dataSourceFactory;

    public MultiTenantRoutingDataSource(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * Registra um novo datasource no cache
     */
    public void addDataSource(String tenantSlug, DataSource dataSource) {
        log.info("Registrando datasource para tenant: {}", tenantSlug);
        dataSources.put(tenantSlug, dataSource);
    }

    /**
     * Remove datasource do cache (ex: quando tenant é deletado)
     */
    public void removeDataSource(String tenantSlug) {
        log.info("Removendo datasource para tenant: {}", tenantSlug);
        dataSources.remove(tenantSlug);
    }

    /**
     * Obtém conexão para o banco do tenant
     */
    @Override
    public Connection getConnection() throws SQLException {
        String tenantSlug = TenantContext.getTenantSlug();
        
        if (tenantSlug == null || tenantSlug.isEmpty()) {
            log.error("❌ TenantSlug não definido no TenantContext!");
            throw new SQLException("TenantSlug não definido no contexto");
        }

        log.debug("🔍 Buscando DataSource em cache para tenant slug: '{}'", tenantSlug);
        DataSource dataSource = dataSources.get(tenantSlug);
        
        if (dataSource == null) {
            log.info("📊 DataSource NÃO está em cache para slug '{}'. Tentando criar/obter do banco de dados...", tenantSlug);
            dataSource = dataSourceFactory.createDataSource(tenantSlug);
            
            if (dataSource == null) {
                log.error("❌ ERRO: DataSource não pode ser criado para tenant slug: '{}' - Verifique se existe registro em tenant_datasource!", tenantSlug);
                throw new SQLException("Banco de dados não configurado para tenant: " + tenantSlug);
            }
            
            // Cache do datasource para próximas requisições
            log.info("✅ Datasource criado e armazenado em cache para tenant: '{}'", tenantSlug);
            addDataSource(tenantSlug, dataSource);
        } else {
            log.debug("✅ DataSource já estava em cache para slug: '{}'", tenantSlug);
        }

        log.info("🔗 Obtendo conexão do DataSource para tenant slug: '{}'", tenantSlug);
        return dataSource.getConnection();
    }

    /**
     * Obtém conexão com username e password
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    /**
     * Retorna lista de datasources registrados (para DEBUG)
     */
    public Map<String, DataSource> getAllDataSources() {
        return new HashMap<>(dataSources);
    }

    /**
     * Limpa cache de datasources
     */
    public void clearCache() {
        log.info("Limpando cache de datasources");
        dataSources.clear();
    }
}
