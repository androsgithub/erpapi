package com.api.erp.v1.shared.infrastructure.config.datasource.manual;

import com.api.erp.v1.shared.infrastructure.config.datasource.TenantContext;
import com.api.erp.v1.shared.infrastructure.config.datasource.routing.DataSourceFactory;
import com.api.erp.v1.shared.infrastructure.config.datasource.routing.MultiTenantRoutingDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Map;

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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            MultiTenantRoutingDataSource multiTenantRoutingDataSource) {
        final var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        // Usa MultiTenantRoutingDataSource para rotear requisições dinamicamente por tenant
        entityManagerFactoryBean.setDataSource(multiTenantRoutingDataSource);
        entityManagerFactoryBean.setPackagesToScan(
                ApplicationContext.class.getPackageName(),
                "com.api.erp.v1.features.tenant.domain.entity",
                "com.api.erp.v1.features.endereco.domain.entity",
                "com.api.erp.v1.features.cliente.domain.entity",
                "com.api.erp.v1.features.contato.domain.entity",
                "com.api.erp.v1.features.usuario.domain.entity",
                "com.api.erp.v1.shared.infrastructure.persistence.converters",
                "com.api.erp.v1.features.permissao.domain.entity",
                "com.api.erp.v1.features.produto.domain.entity",
                "com.api.erp.v1.features.camposcustom.domain.entity",
                "com.api.erp.v1.features.unidademedida.domain.entity");
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        entityManagerFactoryBean.setJpaPropertyMap(Map.ofEntries(
                Map.entry("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
        ));
        return entityManagerFactoryBean;
    }
}
