package com.api.erp.v1.observability.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class LogsDatabaseConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.logs")
    public DataSourceProperties logsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "logsDataSource")
    public DataSource logsDataSource(DataSourceProperties logsDataSourceProperties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(logsDataSourceProperties.getUrl());
        config.setUsername(logsDataSourceProperties.getUsername());
        config.setPassword(logsDataSourceProperties.getPassword());
        config.setDriverClassName(logsDataSourceProperties.getDriverClassName());
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setPoolName("HikariPool-Logs");
        return new HikariDataSource(config);
    }

    @Bean(name = "logsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean logsEntityManagerFactory(
            @Qualifier("logsDataSource") DataSource logsDataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(logsDataSource);
        em.setPackagesToScan(
                "com.api.erp.v1.observability.domain.entity"
        );
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setPersistenceUnitName("logs");

        return em;
    }

    @Bean(name = "logsTransactionManager")
    public PlatformTransactionManager logsTransactionManager(
           @Qualifier("logsEntityManagerFactory") EntityManagerFactory logsEntityManagerFactory) {
        return new JpaTransactionManager(logsEntityManagerFactory);
    }
}
