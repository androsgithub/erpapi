package com.api.erp.v1.main.datasource.routing.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * INFRASTRUCTURE - Configuração de DataSource Master
 * 
 * Configuração centralizada para o banco de dados Master.
 * O Master armazena:
 * - Configurações de todos os tenants
 * - Usuários administrativos
 * - Informações compartilhadas entre todos os tenants
 * 
 * Thread-safe: HikariCP gerencia pool de conexões.
 * Primary: Este é o DataSource padrão da aplicação.
 * 
 * @author ERP System
 * @version 1.0
 */
@Configuration
@Slf4j
public class MasterDataSourceConfiguration {

    @Value("${spring.datasource.master.url:${spring.datasource.url}}")
    private String masterUrl;

    @Value("${spring.datasource.master.username:${spring.datasource.username}}")
    private String masterUsername;

    @Value("${spring.datasource.master.password:${spring.datasource.password}}")
    private String masterPassword;

    @Value("${spring.datasource.master.driver-class-name:${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}}")
    private String driverClassName;

    /**
     * Cria o Bean do Master DataSource
     */
    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() {
        validateConfiguration();

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(masterUrl);
            config.setUsername(masterUsername);
            config.setPassword(masterPassword);
            config.setDriverClassName(driverClassName);
            
            // Configurações de pool
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            config.setPoolName("HikariPool-Master");
            config.setAutoCommit(true);

            HikariDataSource dataSource = new HikariDataSource(config);
            log.info("Master DataSource criado com sucesso");
            return dataSource;
        } catch (Exception e) {
            log.error("Erro ao criar Master DataSource", e);
            throw new RuntimeException("Falha ao criar Master DataSource", e);
        }
    }

    private void validateConfiguration() {
        if (masterUrl == null || masterUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("spring.datasource.master.url não pode estar vazia");
        }
        if (masterUsername == null || masterUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("spring.datasource.master.username não pode estar vazia");
        }
        if (masterPassword == null || masterPassword.trim().isEmpty()) {
            log.warn("spring.datasource.master.password não foi configurada");
        }
    }
}
