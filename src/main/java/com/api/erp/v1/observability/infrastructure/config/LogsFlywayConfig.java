package com.api.erp.v1.observability.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Configuração de Flyway para o banco de logs (erpapi_logs).
 *
 * Esta configuração garante que as migrações do banco de logs sejam executadas
 * de forma isolada do banco principal e do Flyway padrão do Spring.
 */
@Configuration
public class LogsFlywayConfig {

    /**
     * Configuração e instância do Flyway para o banco de logs.
     * Executa as migrações em classpath:db/migration/logs
     */
    @Bean(initMethod = "migrate")
    public Flyway logsFlywayMigration(
            @Qualifier("logsDataSource") DataSource logsDataSource) {

        return Flyway.configure()
            .dataSource(logsDataSource)
            .locations("classpath:db/migration/logs")
            .baselineOnMigrate(true)
            .outOfOrder(false)
            .validateOnMigrate(false)
            .load();
    }
}
