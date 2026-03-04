package com.api.erp.v1.main.datasource.routing.config;

import com.api.erp.v1.main.datasource.routing.core.CustomRoutingDatasource;
import com.api.erp.v1.main.datasource.routing.domain.IDataSourceRouter;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * INFRASTRUCTURE - Configuration JPA para Tenant DataSource
 * <p>
 * Configura EntityManager e TransactionManager para bancos de dados de tenants.
 * Usa RoutingDS para rotear automaticamente para o banco correto baseado no tenant.
 *
 * @author ERP System
 * @version 1.0
 */
@Configuration
@Slf4j
@EnableJpaRepositories(
        basePackages = {
                "com.api.erp.v1.main.features.address.domain.repository",
                "com.api.erp.v1.main.features.businesspartner.domain.repository",
                "com.api.erp.v1.main.features.contact.domain.repository",
                "com.api.erp.v1.main.features.user.domain.repository",
                "com.api.erp.v1.main.features.permission.domain.repository",
                "com.api.erp.v1.main.features.product.domain.repository",
                "com.api.erp.v1.main.features.customfield.domain.repository",
                "com.api.erp.v1.main.features.measureunit.domain.repository"
        },
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager"
)
public class TenantJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            CustomRoutingDatasource routingDS) {

        log.info("Configurando tenant EntityManagerFactory com roteamento dinâmico");

        // Propriedades específicas para tenant: ddl-auto=none (roteamento dinâmico)
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");

        return builder
                .dataSource(routingDS)
                .packages(
                        "com.api.erp.v1.main.tenant.domain.entity",
                        "com.api.erp.v1.main.features.address.domain.entity",
                        "com.api.erp.v1.main.features.businesspartner.domain.entity",
                        "com.api.erp.v1.main.features.contact.domain.entity",
                        "com.api.erp.v1.main.features.user.domain.entity",
                        "com.api.erp.v1.main.features.permission.domain.entity",
                        "com.api.erp.v1.main.features.product.domain.entity",
                        "com.api.erp.v1.main.features.customfield.domain.entity",
                        "com.api.erp.v1.main.features.measureunit.domain.entity",
                        "com.api.erp.v1.main.shared.infrastructure.persistence.converters"
                )
                .properties(properties)
                .persistenceUnit("tenant")
                .build();
    }

    @Bean
    public PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory emf) {

        log.info("Configurando tenant TransactionManager");
        return new JpaTransactionManager(emf);
    }

    /**
     * Bean de RoutingDS - DataSource dinâmico para tenants
     */
    @Bean
    public CustomRoutingDatasource customRoutingDatasource(
            IDataSourceRouter dataSourceRouter) {

        log.info("Creating CustomRoutingDatasource for automatic routing");
        return new CustomRoutingDatasource(dataSourceRouter);
    }
}
