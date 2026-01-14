//package com.api.erp.v1.shared.infrastructure.config.datasource.using_repository;
//
//import jakarta.persistence.EntityManagerFactory;
//import org.hibernate.cfg.AvailableSettings;
//import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
//import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.boot.jpa.autoconfigure.JpaProperties;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.JpaVendorAdapter;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.hibernate.SpringBeanContainer;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableJpaRepositories(basePackages = {
//        // Entitys
//        "com.api.erp.v1.features.tenant.domain.entity",
//        "com.api.erp.v1.features.endereco.domain.entity",
//        "com.api.erp.v1.features.cliente.domain.entity",
//        "com.api.erp.v1.features.contato.domain.entity",
//        "com.api.erp.v1.features.usuario.domain.entity",
//        "com.api.erp.v1.features.permissao.domain.entity",
//        "com.api.erp.v1.features.produto.domain.entity",
//        "com.api.erp.v1.features.camposcustom.domain.entity",
//        "com.api.erp.v1.features.unidademedida.domain.entity",
//        // Repository
//        "com.api.erp.v1.features.endereco.domain.repository",
//        "com.api.erp.v1.features.tenant.domain.repository"},
//        entityManagerFactoryRef = "tenantEntityManagerFactory",
//        transactionManagerRef = "tenantTransactionManager")
//public class TenantPersistenceConfig {
//
//    private final ConfigurableListableBeanFactory beanFactory;
//    private final JpaProperties jpaProperties;
//    private final String entityPackages;
//
//    @Autowired
//    public TenantPersistenceConfig(ConfigurableListableBeanFactory beanFactory, JpaProperties jpaProperties, @Value("${multitenancy.tenant.entityManager.packages}") String entityPackages) {
//        this.beanFactory = beanFactory;
//        this.jpaProperties = jpaProperties;
//        this.entityPackages = entityPackages;
//    }
//
//    @Primary
//    @Bean
//    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(@Qualifier("schemaBasedMultiTenantConnectionProvider") MultiTenantConnectionProvider connectionProvider, @Qualifier("currentTenantIdentifierResolver") CurrentTenantIdentifierResolver tenantResolver) {
//        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
//        emfBean.setPersistenceUnitName("tenant-persistence-unit");
//        emfBean.setPackagesToScan(entityPackages);
//
//        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        emfBean.setJpaVendorAdapter(vendorAdapter);
//
//        Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());
//        properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
//        properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
//        properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(this.beanFactory));
//        properties.remove(AvailableSettings.DEFAULT_SCHEMA);
//        properties.put("hibernate.multiTenancy",
//                "SCHEMA");
//        properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
//        properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
//        emfBean.setJpaPropertyMap(properties);
//
//        emfBean.setPackagesToScan(
//                ApplicationContext.class.getPackageName(),
//                "com.api.erp.v1.features.tenant.domain.entity",
//                "com.api.erp.v1.features.endereco.domain.entity",
//                "com.api.erp.v1.features.cliente.domain.entity",
//                "com.api.erp.v1.features.contato.domain.entity",
//                "com.api.erp.v1.features.usuario.domain.entity",
//                "com.api.erp.v1.features.permissao.domain.entity",
//                "com.api.erp.v1.features.produto.domain.entity",
//                "com.api.erp.v1.features.camposcustom.domain.entity",
//                "com.api.erp.v1.features.unidademedida.domain.entity");
//
//        return emfBean;
//    }
//
//    @Primary
//    @Bean
//    public JpaTransactionManager tenantTransactionManager(@Qualifier("tenantEntityManagerFactory") EntityManagerFactory emf) {
//        JpaTransactionManager tenantTransactionManager = new JpaTransactionManager();
//        tenantTransactionManager.setEntityManagerFactory(emf);
//        return tenantTransactionManager;
//    }
//}
