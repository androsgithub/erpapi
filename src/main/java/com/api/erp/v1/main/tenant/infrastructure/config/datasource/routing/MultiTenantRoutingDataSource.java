//package com.api.erp.v1.main.tenant.infrastructure.config.datasource.routing;
//
//import com.api.erp.v1.main.tenant.infrastructure.config.datasource.TenantContext;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.jdbc.datasource.AbstractDataSource;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * ⚠️ DEPRECATED - Use CustomRoutingDatasource em com.api.erp.v1.main.datasource.routing
// *
// * Esta classe foi substituída pela arquitetura consolidada e limpa.
// * Migre para: com.api.erp.v1.main.datasource.routing.core.CustomRoutingDatasource
// *
// * MultiTenantRoutingDataSource
// *
// * DataSource que roteia conexões para o banco correto baseado no tenantId.
// *
// * Fluxo:
// * 1. Requisição chega com tenantId no header ou JWT
// * 2. TenantContext.setTenantId(id)
// * 3. MultiTenantRoutingDataSource obtém tenantId do contexto
// * 4. Busca datasource correspondente no cache
// * 5. Returns conexão para o banco do tenant
// */
//@Deprecated(since = "2.0", forRemoval = true)
//@Slf4j
//public class MultiTenantRoutingDataSource extends AbstractDataSource {
//
//    private final Map<Long, DataSource> dataSources = new HashMap<>();
//    private final DataSourceFactory dataSourceFactory;
//    private final DataSource defaultDataSource;
//
//    public MultiTenantRoutingDataSource(DataSourceFactory dataSourceFactory, DataSource defaultDataSource) {
//        this.dataSourceFactory = dataSourceFactory;
//        this.defaultDataSource = defaultDataSource;
//    }
//
//    /**
//     * Logs um novo datasource no cache
//     */
//    public void addDataSource(Long tenantId, DataSource dataSource) {
//        log.info("Logsndo datasource para tenant: {}", tenantId);
//        dataSources.put(tenantId, dataSource);
//    }
//
//    /**
//     * Gets conexão para o banco do tenant
//     *
//     * Se TenantContext estiver vazio (inicialização da app), usa defaultDataSource (master)
//     */
//    @Override
//    public Connection getConnection() throws SQLException {
//        Long tenantId = TenantContext.getTenantId();
//        log.info("🔌 [MultiTenantRoutingDataSource.getConnection] Tentando obter conexão. TenantContext.tenantId = {}", tenantId);
//
//        // Fallback para defaultDataSource quando TenantContext está vazio
//        // Isso acontece durante a inicialização da aplicação (startup/migrations)
//        if (tenantId == null) {
//            log.warn("⚠️ [MultiTenantRoutingDataSource] TenantId NÃO DEFINIDO em TenantContext! Usando defaultDataSource (banco MASTER) como fallback");
//            return defaultDataSource.getConnection();
//        }
//
//        log.debug("🔍 [MultiTenantRoutingDataSource] Buscando DataSource em cache para tenant ID: '{}'", tenantId);
//        DataSource dataSource = dataSources.get(tenantId);
//
//        if (dataSource == null) {
//            log.info("📊 [MultiTenantRoutingDataSource] DataSource NÃO está em cache para tenant '{}'. Criando novo...", tenantId);
//            dataSource = dataSourceFactory.createDataSource(tenantId);
//
//            if (dataSource == null) {
//                log.error("❌ [MultiTenantRoutingDataSource] ERRO CRÍTICO: DataSource não pode ser criado para tenant ID: '{}'\n" +
//                        "   VERIFIQUE:\n" +
//                        "   1) SELECT COUNT(*) FROM tb_tenant_datasource WHERE tenant_id = {} AND is_active = true\n" +
//                        "   2) Existe tenant com ID='{}' em tb_tenant?\n" +
//                        "   3) Existe linha em tb_tenant_datasource apontando para esse tenant?\n" +
//                        "   4) is_active=true?",
//                        tenantId, tenantId, tenantId);
//                throw new SQLException("Banco de dados não configurado para tenant: " + tenantId);
//            }
//
//            // Cache do datasource para próximas requisições
//            log.info("✅ [MultiTenantRoutingDataSource] DataSource criado e cacheado para tenant: '{}'", tenantId);
//            addDataSource(tenantId, dataSource);
//        } else {
//            log.debug("✅ [MultiTenantRoutingDataSource] DataSource já estava em cache para tenant ID: '{}'", tenantId);
//        }
//
//        log.info("🔗 [MultiTenantRoutingDataSource] Obtendo conexão do DataSource | tenant ID: '{}' | banco específico (NÃO master!)", tenantId);
//        return dataSource.getConnection();
//    }
//
//    /**
//     * Gets conexão com username e password
//     */
//    @Override
//    public Connection getConnection(String username, String password) throws SQLException {
//        return getConnection();
//    }
//
//    /**
//     * Returns lista de datasources registrados (para DEBUG)
//     */
//    public Map<Long, DataSource> getAllDataSources() {
//        return new HashMap<>(dataSources);
//    }
//
//    /**
//     * Limpa cache de datasources
//     */
//    public void clearCache() {
//        log.info("Limpando cache de datasources");
//        dataSources.clear();
//    }
//}
