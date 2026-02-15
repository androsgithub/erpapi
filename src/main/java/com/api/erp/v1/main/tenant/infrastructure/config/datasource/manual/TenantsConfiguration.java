package com.api.erp.v1.main.tenant.infrastructure.config.datasource.manual;

import com.api.erp.v1.main.tenant.infrastructure.config.datasource.routing.DataSourceFactory;
import com.api.erp.v1.main.tenant.infrastructure.config.datasource.routing.MultiTenantRoutingDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Configuração manual de DataSource para o banco master (erpapi).
 *
 * Esta classe configura:
 * - defaultDataSource: DataSource para o banco master
 * - multiTenantRoutingDataSource: Roteia requisições entre bancos de tenants
 * - entityManagerFactory: EntityManager para o banco master
 * - transactionManager: Gerenciador de transações
 *
 * IMPORTANTE: @EnableJpaRepositories foi removido e movido para TenantsMasterRepositoriesConfig
 * para evitar conflitos com outros @EnableJpaRepositories (ex: em LogsRepositoriesConfig).
 *
 * Estratégia de TB_TENANT:
 * - TB_TENANT está na master database
 * - TenantRepository é gerenciado por TenantsMasterRepositoriesConfig
 * - TB_TENANT é acessada sempre pelo master database, nunca pelo tenant routing
 * - Garante consistência na leitura de configurações de tenants
 */
@Configuration
public class TenantsConfiguration {
    @Bean(name = "defaultDataSource")
    public DataSource defaultDataSource(Environment environment) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environment.getRequiredProperty("spring.datasource.url"));
        config.setUsername(environment.getRequiredProperty("spring.datasource.username"));
        config.setPassword(environment.getRequiredProperty("spring.datasource.password"));
        config.setDriverClassName(environment.getRequiredProperty("spring.datasource.driver-class-name"));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setPoolName("HikariPool-Master");
        return new HikariDataSource(config);
    }

    @Bean
    @Primary
    @Lazy
    public MultiTenantRoutingDataSource multiTenantRoutingDataSource(
            DataSourceFactory dataSourceFactory,
            @Qualifier("defaultDataSource") DataSource defaultDataSource) {
        return new MultiTenantRoutingDataSource(dataSourceFactory, defaultDataSource);
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            MultiTenantRoutingDataSource multiTenantRoutingDataSource) {
        final var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        // Usa MultiTenantRoutingDataSource para rotear requisições dinamicamente por tenant
        entityManagerFactoryBean.setDataSource(multiTenantRoutingDataSource);
        entityManagerFactoryBean.setPackagesToScan(
                ApplicationContext.class.getPackageName(),
                "com.api.erp.v1.main.features.endereco.domain.entity",
                "com.api.erp.v1.main.features.cliente.domain.entity",
                "com.api.erp.v1.main.features.contato.domain.entity",
                "com.api.erp.v1.main.features.usuario.domain.entity",
                "com.api.erp.v1.main.shared.infrastructure.persistence.converters",
                "com.api.erp.v1.main.features.permissao.domain.entity",
                "com.api.erp.v1.main.features.produto.domain.entity",
                "com.api.erp.v1.main.features.customfield.domain.entity",
                "com.api.erp.v1.main.features.unidademedida.domain.entity",
                "com.api.erp.v1.main.tenant.domain.entity");
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        entityManagerFactoryBean.setJpaPropertyMap(Map.ofEntries(
                Map.entry("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
        ));
        return entityManagerFactoryBean;
    }

    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
