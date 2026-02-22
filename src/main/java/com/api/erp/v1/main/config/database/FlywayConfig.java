package com.api.erp.v1.main.config.database;

import com.api.erp.v1.main.tenant.domain.entity.DBType;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.flyway.autoconfigure.FlywayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(FlywayProperties.class)
@Primary
@Slf4j
public class FlywayConfig {

    @Bean(name = "flywayMaster")
    public Flyway flywayMaster(@Qualifier("masterDataSource") DataSource defaultDataSource,
                               FlywayProperties flywayProperties) {
        log.info("🚀 Iniciando configuração do Flyway para o banco master");

        DBType dbType;
        try {
            dbType = DBType.fromDriver(defaultDataSource.getConnection().getMetaData().getDriverName());
        } catch (Exception e) {
            dbType = DBType.MYSQL;
            log.error("Erro ao pegar o dbType pelo driver ao rodar as migrações! Utilizando DBType.MYSQL por padrão");
        }

        String flywayLocation = "classpath:db/migration/master/" + dbType.getNome().toLowerCase();


        Flyway flyway = Flyway.configure()
                .dataSource(defaultDataSource)
                .locations(flywayLocation)
                .baselineOnMigrate(true)
                .validateOnMigrate(false)
                .table("master_erpapi_migrations_history")
                .load();

        // Repair: remove failed migrations from history
        try {
            log.info("🔧 Executando repair para limpar migrações falhadas...");
            flyway.repair();
            log.info("✅ Repair executado com sucesso");
        } catch (Exception e) {
            log.warn("⚠️ Erro ao executar repair (pode ser normal): {}", e.getMessage());
        }

        // Executa as migrações automaticamente
        try {
            log.info("📊 Executando migrações do Flyway...");
            var result = flyway.migrate();
            log.info("✅ Flyway executado com sucesso! Migrações aplicadas: {}", result.migrationsExecuted);
        } catch (Exception e) {
            log.error("❌ Erro ao executar migrações do Flyway: {}", e.getMessage(), e);
            throw e;
        }

        return flyway;
    }
}
