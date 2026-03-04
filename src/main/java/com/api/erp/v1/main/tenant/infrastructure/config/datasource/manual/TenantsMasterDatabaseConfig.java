//package com.api.erp.v1.main.tenant.infrastructure.config.datasource.manual;
//
//import jakarta.persistence.EntityManagerFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//
///**
// * ⚠️ DEPRECATED - Use MasterJpaConfig em com.api.erp.v1.main.datasource.routing.config
// *
// * Esta configuração foi substituída pela arquitetura consolidada e limpa.
// * Migre para: com.api.erp.v1.main.datasource.routing.config.MasterJpaConfig
// *
// * Configuration específica de DataSource, EntityManager e TransactionManager
// * para o banco master (erpapi) - TB_TENANT.
// *
// * Esta classe garante que TB_TENANT SEMPRE usa o defaultDataSource (master database),
// * independentemente do contexto de tenant da requisição.
// *
// * Estratégia:
// * - Cria masterEntityManagerFactory específico para TB_TENANT
// * - Cria masterTransactionManager específico para TB_TENANT
// * - TenantsMasterRepositoriesConfig usa esses beans para TenantRepository
// *
// * Diferença de TenantsConfiguration:
// * - TenantsConfiguration: cria entityManagerFactory principal (com multiTenantRoutingDataSource)
// * - TenantsMasterDatabaseConfig: cria masterEntityManagerFactory (com defaultDataSource)
// *
// * Padrão baseado em LogsDatabaseConfig.
// */
//@Deprecated(since = "2.0", forRemoval = true)
//@Configuration
//public class TenantsMasterDatabaseConfig {
//
//    /**
//     * EntityManagerFactory específico para o master database (TB_TENANT).
//     * Usa defaultDataSource diretamente, nunca multiTenantRoutingDataSource.
//     */
//    @Bean(name = "masterEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(
//            @Qualifier("defaultDataSource") DataSource defaultDataSource) {
//
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(defaultDataSource);
//        em.setPackagesToScan(
//                "com.api.erp.v1.main.tenant.domain.entity",
//                "com.api.erp.v1.main.features.address.domain.entity",
//                "com.api.erp.v1.main.features.businesspartner.domain.entity",
//                "com.api.erp.v1.main.features.contact.domain.entity",
//                "com.api.erp.v1.main.features.user.domain.entity",
//                "com.api.erp.v1.main.features.permission.domain.entity",
//                "com.api.erp.v1.main.features.product.domain.entity",
//                "com.api.erp.v1.main.features.customfield.domain.entity",
//                "com.api.erp.v1.main.features.measureunit.domain.entity",
//                "com.api.erp.v1.main.shared.infrastructure.persistence.converters"
//        );
//        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//        em.setPersistenceUnitName("master");
//
//        return em;
//    }
//
//    /**
//     * TransactionManager específico para o master database (TB_TENANT).
//     * Garante transações isoladas do contexto multi-tenant.
//     */
//    @Bean(name = "masterTransactionManager")
//    public PlatformTransactionManager masterTransactionManager(
//            @Qualifier("masterEntityManagerFactory") EntityManagerFactory masterEntityManagerFactory) {
//        return new JpaTransactionManager(masterEntityManagerFactory);
//    }
//}
