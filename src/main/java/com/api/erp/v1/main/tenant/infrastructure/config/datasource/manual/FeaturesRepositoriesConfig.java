//package com.api.erp.v1.main.tenant.infrastructure.config.datasource.manual;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//
///**
// * ⚠️ DEPRECATED - Use TenantRepositoriesConfig em com.api.erp.v1.main.datasource.routing.config
// *
// * Esta configuração foi substituída pela arquitetura consolidada e limpa.
// * Migre para: com.api.erp.v1.main.datasource.routing.config.TenantRepositoriesConfig
// *
// * Configuração de JPA Repositories para os repositories multi-tenant (features).
// *
// * Esta classe é separada de TenantsConfiguration para evitar conflitos com outros
// * @EnableJpaRepositories na aplicação (ex: em LogsRepositoriesConfig e TenantsMasterRepositoriesConfig).
// *
// * Configuração:
// * - basePackages: Todos os repositories de features (address, customer, contact, user, etc)
// * - entityManagerFactoryRef: entityManagerFactory (criado em TenantsConfiguration)
// * - transactionManagerRef: transactionManager (criado em TenantsConfiguration)
// *
// * Estratégia de Multi-Tenancy:
// * Os repositories de features usam o MultiTenantRoutingDataSource, que roteia
// * automaticamente para o banco de dados do tenant baseado no contexto da requisição.
// *
// * Benefícios:
// * ✅ Evita múltiplos @EnableJpaRepositories na mesma aplicação
// * ✅ Separa responsabilidades (DataSource vs Repositories)
// * ✅ Garante que features repositories usem o MultiTenantRoutingDataSource
// * ✅ Sem conflitos com repositórios de master (TenantsMasterRepositoriesConfig) e logs (LogsRepositoriesConfig)
// */
//@Deprecated(since = "2.0", forRemoval = true)
//@Configuration
//@EnableJpaRepositories(
//        basePackages = {
//                "com.api.erp.v1.main.features.address.domain.repository",
//                "com.api.erp.v1.main.features.customer.domain.repository",
//                "com.api.erp.v1.main.features.contact.domain.repository",
//                "com.api.erp.v1.main.features.user.domain.repository",
//                "com.api.erp.v1.main.features.permission.domain.repository",
//                "com.api.erp.v1.main.features.product.domain.repository",
//                "com.api.erp.v1.main.features.customfield.domain.repository",
//                "com.api.erp.v1.main.features.measureunit.domain.repository"
//        },
//        entityManagerFactoryRef = "entityManagerFactory",
//        transactionManagerRef = "transactionManager"
//)
//public class FeaturesRepositoriesConfig {
//    // Configuração de Repositories para features multi-tenant
//}
