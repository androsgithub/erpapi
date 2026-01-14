package com.api.erp.v1.shared.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Estratégia de migração para schemas de tenants.
 * 
 * Responsável por aplicar migrações Flyway aos schemas individuais dos tenants.
 * Deve ser chamada quando um novo tenant é provisionado ou um schema é criado.
 */
public class FlywayMigrationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(FlywayMigrationStrategy.class);
    private final DataSource dataSource;

    public FlywayMigrationStrategy(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Aplica migrações Flyway para um schema específico de tenant.
     * 
     * @param schemaName Nome do schema do tenant
     * @throws SQLException se houver erro ao acessar o banco de dados
     */
    public void migrateSchema(String schemaName) throws SQLException {
        logger.info("Iniciando migração Flyway para schema: {}", schemaName);

        try {
            // Cria o Flyway configurado para o schema específico
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .validateMigrationNaming(true)
                    .locations("classpath:db/migration/tenant")
                    .baselineOnMigrate(true)
                    .table("_" + schemaName + "_flyway_schema_history")
                    .load();

            // Executa as migrações
            flyway.repair();
            flyway.migrate();
            logger.info("Migração concluída com sucesso para schema: {}", schemaName);

        } catch (Exception e) {
            logger.error("Erro ao migrar schema: {}", schemaName, e);
            throw new SQLException("Erro ao migrar schema: " + schemaName, e);
        }
    }

    /**
     * Verifica o status das migrações para um schema específico.
     * 
     * @param schemaName Nome do schema do tenant
     * @return Informações sobre o status das migrações
     */
    public String getMigrationStatus(String schemaName) {
        logger.info("Verificando status de migração para schema: {}", schemaName);

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .locations("classpath:db/migration/tenant")
                    .table("_" + schemaName + "_flyway_schema_history")
                    .load();

            var info = flyway.info();
            return "Migrations pending: " + info.pending().length + ", Applied: " + info.applied().length;

        } catch (Exception e) {
            logger.error("Erro ao verificar status de migração para schema: {}", schemaName, e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Limpa a história de migrações de um schema específico (apenas para desenvolvimento).
     * ⚠️ CUIDADO: Esta operação é destrutiva e deve ser usada apenas em desenvolvimento!
     * 
     * @param schemaName Nome do schema do tenant
     * @throws SQLException se houver erro ao acessar o banco de dados
     */
    public void cleanSchema(String schemaName) throws SQLException {
        logger.warn("Limpando histórico de migrações para schema: {} - OPERAÇÃO DESTRUTIVA", schemaName);

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .locations("classpath:db/migration/tenant")
                    .table("_" + schemaName + "_flyway_schema_history")
                    .cleanDisabled(false)
                    .load();

            flyway.clean();
            logger.info("Limpeza concluída para schema: {}", schemaName);

        } catch (Exception e) {
            logger.error("Erro ao limpar schema: {}", schemaName, e);
            throw new SQLException("Erro ao limpar schema: " + schemaName, e);
        }
    }
}
