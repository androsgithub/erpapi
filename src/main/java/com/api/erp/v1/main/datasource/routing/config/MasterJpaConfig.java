package com.api.erp.v1.main.datasource.routing.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * INFRASTRUCTURE - Configuração JPA para Master DataSource
 * 
 * Configura EntityManager e TransactionManager para o banco Master centralizado.
 * Banco Master armazena configurações globais como tenants e usuários administrativos.
 * 
 * DataSource: Master (configurado em MasterDataSourceConfiguration)
 * 
 * @author ERP System
 * @version 1.0
 */
@Configuration
@Slf4j
@EnableJpaRepositories(
        basePackages = {
                "com.api.erp.v1.main.tenant.domain.repository"
        },
        entityManagerFactoryRef = "masterEntityManagerFactory",
        transactionManagerRef = "masterTransactionManager"
)
@Primary
public class MasterJpaConfig {

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("masterDataSource") DataSource dataSource) {

        log.info("Configurando master EntityManagerFactory");
        
        // Propriedades específicas para master (ddl-auto: update para criar tabelas/colunas)
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        
        return builder
                .dataSource(dataSource)
                .packages(
                        "com.api.erp.v1.main.tenant.domain.entity",
                        "com.api.erp.v1.main.features.address.domain.entity",
                        "com.api.erp.v1.main.features.contact.domain.entity",
                        "com.api.erp.v1.main.shared.infrastructure.persistence.converters"
                )
                .properties(properties)
                .persistenceUnit("master")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager masterTransactionManager(
            @Qualifier("masterEntityManagerFactory") EntityManagerFactory emf) {
        
        log.info("Configurando master TransactionManager");
        return new JpaTransactionManager(emf);
    }
}
