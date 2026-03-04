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
    public Flyway flywayMaster(@Qualifier("masterDataSource") DataSource masterDataSource,
                               FlywayProperties flywayProperties) {
        log.info("🚀 Starting Flyway configuration for master database");

        DBType dbType;
        try {
            dbType = DBType.fromDriver(masterDataSource.getConnection().getMetaData().getDriverName());
        } catch (Exception e) {
            dbType = DBType.MYSQL;
            log.error("Error getting dbType from driver while running migrations! Using DBType.MYSQL as default");
        }

        String flywayLocation = "classpath:db/migration/master/" + dbType.getNome().toLowerCase();


        Flyway flyway = Flyway.configure()
                .dataSource(masterDataSource)
                .locations(flywayLocation)
                .baselineOnMigrate(true)
                .validateOnMigrate(false)
                .table("master_erpapi_migrations_history")
                .load();

        // Repair: remove failed migrations from history
        try {
            log.info("🔧 Running repair to clean failed migrations...");
            flyway.repair();
            log.info("✅ Repair executed successfully");
        } catch (Exception e) {
            log.warn("⚠️ Erro ao executar repair (pode ser normal): {}", e.getMessage());
        }

        // Executes migrations automatically
        try {
            log.info("📊 Running Flyway migrations...");
            var result = flyway.migrate();
            log.info("✅ Flyway executed successfully! Migrations applied: {}", result.migrationsExecuted);
        } catch (Exception e) {
            log.error("❌ Error executing Flyway migrations: {}", e.getMessage(), e);
            throw e;
        }

        return flyway;
    }
}
