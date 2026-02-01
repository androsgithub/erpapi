package com.api.erp.v1.config.database;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.flyway.autoconfigure.FlywayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(FlywayProperties.class)
@Primary
public class FlywayConfig {

    private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);

    @Bean(name = "flywayMaster")
    public Flyway flywayMaster(@Qualifier("defaultDataSource") DataSource defaultDataSource,
                               FlywayProperties flywayProperties) {
        logger.info("🚀 Iniciando configuração do Flyway para o banco master");

        Flyway flyway = Flyway.configure()
                .dataSource(defaultDataSource)
                .locations("classpath:db/migration/master")
                .baselineOnMigrate(true)
                .validateOnMigrate(false)
                .table("master_erpapi_migrations_history")
                .load();

        // Repair: remove failed migrations from history
        try {
            logger.info("🔧 Executando repair para limpar migrações falhadas...");
            flyway.repair();
            logger.info("✅ Repair executado com sucesso");
        } catch (Exception e) {
            logger.warn("⚠️ Erro ao executar repair (pode ser normal): {}", e.getMessage());
        }

        // Executa as migrações automaticamente
        try {
            logger.info("📊 Executando migrações do Flyway...");
            var result = flyway.migrate();
            logger.info("✅ Flyway executado com sucesso! Migrações aplicadas: {}", result.migrationsExecuted);
        } catch (Exception e) {
            logger.error("❌ Erro ao executar migrações do Flyway: {}", e.getMessage(), e);
            throw e;
        }

        return flyway;
    }
}
