//package com.api.erp.v1.shared.infrastructure.config.datasource.using_repository;
//
//import jakarta.persistence.EntityManagerFactory;
//import org.hibernate.cfg.AvailableSettings;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.boot.jpa.autoconfigure.JpaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.JpaVendorAdapter;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.hibernate.SpringBeanContainer;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableJpaRepositories(
//        basePackages = {
//                "com.api.erp.v1.features.tenant.domain.entity",
//                "com.api.erp.v1.features.endereco.domain.entity",
//                "com.api.erp.v1.features.cliente.domain.entity",
//                "com.api.erp.v1.features.contato.domain.entity",
//                "com.api.erp.v1.features.usuario.domain.entity",
//                "com.api.erp.v1.features.permissao.domain.entity",
//                "com.api.erp.v1.features.produto.domain.entity",
//                "com.api.erp.v1.features.camposcustom.domain.entity",
//                "com.api.erp.v1.features.unidademedida.domain.entity"
//        },
//        entityManagerFactoryRef = "masterEntityManagerFactory",
//        transactionManagerRef = "masterTransactionManager"
//)
//public class MasterPersistenceConfig {
//    private final ConfigurableListableBeanFactory beanFactory;
//    private final JpaProperties jpaProperties;
//    private final String entityPackages;
//
//    @Autowired
//    public MasterPersistenceConfig(ConfigurableListableBeanFactory beanFactory,
//                                   JpaProperties jpaProperties,
//                                   @Value("${multitenancy.master.entityManager.packages}")
//                                   String entityPackages) {
//        this.beanFactory = beanFactory;
//        this.jpaProperties = jpaProperties;
//        this.entityPackages = entityPackages;
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(DataSource dataSource) {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//
//        em.setPersistenceUnitName("master-persistence-unit");
//        em.setPackagesToScan(entityPackages);
//        em.setDataSource(dataSource);
//
//        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//
//        Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());
//        properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
//        properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
//        properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(this.beanFactory));
//        em.setJpaPropertyMap(properties);
//
//        return em;
//    }
//
//    @Bean
//    public JpaTransactionManager masterTransactionManager(
//            @Qualifier("masterEntityManagerFactory") EntityManagerFactory emf) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(emf);
//        return transactionManager;
//    }
//}
