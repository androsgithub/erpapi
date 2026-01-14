package com.api.erp.v1.shared.infrastructure.config;

import com.api.erp.v1.features.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.features.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.features.tenant.domain.repository.TenantRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

/**
 * Serviço responsável por executar migrações Flyway em bancos de dados de tenants.
 * 
 * Estratégia: DATABASE per TENANT
 * - Cada tenant tem seu próprio banco de dados (não schema)
 * - As credenciais estão armazenadas em tenant_datasource na tabela master
 * - As migrações estão em classpath:db/migration/tenant/
 * 
 * Este serviço:
 * 1. Busca todos os tenants ativos no banco master
 * 2. Para cada tenant, obtém a configuração de datasource
 * 3. Cria um HikariDataSource temporário com essas credenciais
 * 4. Executa Flyway para aplicar as migrações do tenant
 * 5. Log detalhado de sucesso/falha
 * 
 * Uso:
 * - Na inicialização da aplicação (via @PostConstruct ou listener de startup)
 * - Quando um novo tenant é provisionado
 * - Via endpoint REST para migração sob demanda
 */
@Service
public class TenantMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(TenantMigrationService.class);
    
    private final TenantRepository tenantRepository;
    private final TenantDatasourceRepository tenantDatasourceRepository;

    public TenantMigrationService(TenantRepository tenantRepository, 
                                   TenantDatasourceRepository tenantDatasourceRepository) {
        this.tenantRepository = tenantRepository;
        this.tenantDatasourceRepository = tenantDatasourceRepository;
    }

    /**
     * Executa migrações em TODOS os bancos de tenants ativos.
     * 
     * Fluxo:
     * 1. Busca tenants ativos no master
     * 2. Para cada tenant, busca sua configuração de datasource
     * 3. Cria DataSource temporário com credenciais do tenant
     * 4. Executa Flyway para aplicar migrations/tenant
     * 5. Fecha a conexão
     * 
     * @return Relatório com resultado de cada migração
     */
    @Transactional(readOnly = true)
    public MigrationReport migrateAllTenants() {
        logger.info("========================================");
        logger.info("🚀 Iniciando migrações de TODOS os tenants");
        logger.info("========================================");
        
        MigrationReport report = new MigrationReport();
        
        try {
            // Busca tenants ativos
            List<?> tenants = tenantRepository.findAllByAtivaTrue();
            logger.info("📊 Encontrados {} tenants ativos", tenants.size());
            
            if (tenants.isEmpty()) {
                logger.warn("⚠️ Nenhum tenant ativo encontrado!");
                return report;
            }
            
            // Para cada tenant, executa migração
            tenants.forEach(tenantObj -> {
                try {
                    var tenant = (com.api.erp.v1.features.tenant.domain.entity.Tenant) tenantObj;
                    logger.info("");
                    logger.info("─────────────────────────────────────────");
                    logger.info("🔄 Processando Tenant: {} (ID: {})", tenant.getNome(), tenant.getId());
                    logger.info("─────────────────────────────────────────");
                    
                    // Busca datasource do tenant
                    TenantDatasource datasource = tenantDatasourceRepository
                            .findByTenantIdAndStatus(tenant.getId(), true);
                    
                    if (datasource == null) {
                        logger.warn("⚠️ Tenant {} não tem datasource configurado!", tenant.getNome());
                        report.addFailure(tenant.getNome(), "Datasource não configurado");
                        return;
                    }
                    
                    logger.info("📍 Banco de dados: {}@{}:{}/{}",
                            datasource.getUsername(),
                            datasource.getHost(),
                            datasource.getPort(),
                            datasource.getDatabaseName());
                    
                    // Executa migração para este tenant
                    migrateTenant(tenant.getNome(), tenant.getId(), datasource);
                    report.addSuccess(tenant.getNome());
                    logger.info("✅ Migração concluída com sucesso para: {}", tenant.getNome());
                    
                } catch (Exception e) {
                    var tenant = (com.api.erp.v1.features.tenant.domain.entity.Tenant) tenantObj;
                    logger.error("❌ Erro ao migrar tenant: {}", tenant.getNome(), e);
                    report.addFailure(tenant.getNome(), e.getMessage());
                }
            });
            
        } catch (Exception e) {
            logger.error("❌ Erro crítico ao processar migrações de tenants", e);
            report.setCriticalError(e.getMessage());
        }
        
        logger.info("");
        logger.info("========================================");
        logger.info("📋 RESUMO DE MIGRAÇÕES");
        logger.info("========================================");
        logger.info("✅ Sucesso: {}", report.getSuccessCount());
        logger.info("❌ Falha: {}", report.getFailureCount());
        
        if (report.hasFailures()) {
            logger.warn("⚠️ Algumas migrações falharam:");
            report.getFailures().forEach((tenant, error) -> {
                logger.warn("  - {}: {}", tenant, error);
            });
        }
        
        return report;
    }

    /**
     * Executa migração para um tenant específico.
     * 
     * @param tenantName Nome do tenant
     * @param tenantId ID do tenant
     * @param datasource Configuração de datasource do tenant
     * @throws Exception se houver erro na migração
     */
    private void migrateTenant(String tenantName, Long tenantId, TenantDatasource datasource) throws Exception {
        // Cria DataSource temporário para o tenant
        HikariDataSource tenantDataSource = createDataSourceForTenant(datasource);
        
        try {
            // Tenta validar conexão
            validateConnection(tenantDataSource, tenantName);
            
            // Configura e executa Flyway
            logger.info("🔧 Configurando Flyway para tenant: {}", tenantName);
            
            Flyway flyway = Flyway.configure()
                    .dataSource(tenantDataSource)
                    .locations("classpath:db/migration/tenant")
                    .baselineOnMigrate(true)
                    .validateOnMigrate(false)
                    .table("flyway_schema_history")  // Tabela padrão do Flyway
                    .load();
            
            // Tenta repair (remove migrações falhadas se houver)
            try {
                logger.debug("🔧 Executando repair para limpar migrações falhadas...");
                flyway.repair();
                logger.debug("✅ Repair concluído");
            } catch (Exception e) {
                logger.debug("ℹ️ Repair não necessário ou erro esperado: {}", e.getMessage());
            }
            
            // Executa as migrações
            logger.info("📊 Executando migrações...");
            var result = flyway.migrate();
            logger.info("✅ Migrações executadas com sucesso!");
            logger.info("   - Versão atual: {}", result.success ? "OK" : "ERRO");
            logger.info("   - Migrações aplicadas: {}", result.migrationsExecuted);
            
        } finally {
            // Sempre fecha a conexão
            if (tenantDataSource != null && !tenantDataSource.isClosed()) {
                logger.debug("🔌 Fechando conexão do tenant: {}", tenantName);
                tenantDataSource.close();
            }
        }
    }

    /**
     * Executa migração para um tenant específico (sobrecarga para slug).
     * Útil quando temos apenas o slug do tenant.
     * 
     * @param tenantSlug Slug único do tenant
     * @throws Exception se houver erro na migração
     */
    @Transactional(readOnly = true)
    public void migrateTenantBySlug(String tenantSlug) throws Exception {
        logger.info("🔄 Executando migração para tenant slug: {}", tenantSlug);
        
        var tenant = tenantRepository.findBySlug(tenantSlug);
        if (tenant == null || !tenant.isAtiva()) {
            throw new IllegalArgumentException("Tenant não encontrado ou inativo: " + tenantSlug);
        }
        
        TenantDatasource datasource = tenantDatasourceRepository
                .findByTenantIdAndStatus(tenant.getId(), true);
        
        if (datasource == null) {
            throw new IllegalArgumentException("Datasource não configurado para tenant: " + tenantSlug);
        }
        
        migrateTenant(tenant.getNome(), tenant.getId(), datasource);
    }

    /**
     * Cria um HikariDataSource com as credenciais do tenant.
     * 
     * @param datasource Configuração com host, port, database, usuario, senha
     * @return HikariDataSource configurado
     */
    private HikariDataSource createDataSourceForTenant(TenantDatasource datasource) {
        HikariConfig config = new HikariConfig();
        
        // Constrói JDBC URL: jdbc:mysql://host:port/database
        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                datasource.getHost(),
                datasource.getPort(),
                datasource.getDatabaseName()
        );
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(datasource.getUsername());
        config.setPassword(datasource.getPassword());
        config.setMaximumPoolSize(2);  // Pequeno pool já que é temporário
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);  // 10 segundos
        config.setIdleTimeout(30000);  // 30 segundos
        config.setAutoCommit(true);
        
        logger.debug("📦 Criando HikariDataSource para: {}", datasource.getDatabaseName());
        return new HikariDataSource(config);
    }

    /**
     * Valida a conexão com o banco de dados do tenant.
     * 
     * @param dataSource DataSource do tenant
     * @param tenantName Nome do tenant (para logging)
     * @throws Exception se não conseguir conectar
     */
    private void validateConnection(DataSource dataSource, String tenantName) throws Exception {
        logger.debug("🔗 Validando conexão com banco: {}", tenantName);
        
        try (var connection = dataSource.getConnection()) {
            var metaData = connection.getMetaData();
            logger.debug("   ✓ Banco de dados: {}", metaData.getDatabaseProductName());
            logger.debug("   ✓ Versão: {}", metaData.getDatabaseProductVersion());
            logger.debug("   ✓ Conexão ativa e funcional!");
        } catch (Exception e) {
            logger.error("❌ Erro ao conectar ao banco: {}", tenantName, e);
            throw new RuntimeException("Não foi possível conectar ao banco do tenant: " + tenantName, e);
        }
    }

    /**
     * Classe interna para armazenar o relatório de migrações.
     */
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
