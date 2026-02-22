//package com.api.erp.v1.main.tenant.infrastructure.config.datasource.manual;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//
///**
// * ⚠️ DEPRECATED - Use MasterRepositoriesConfig em com.api.erp.v1.main.datasource.routing.config
// *
// * Esta configuração foi substituída pela arquitetura consolidada e limpa.
// * Migre para: com.api.erp.v1.main.datasource.routing.config.MasterRepositoriesConfig
// *
// * Configuração isolada de JPA Repositories para o banco master (erpapi - TB_TENANT).
// * <p>
// * Esta classe é separada de TenantsConfiguration para evitar conflitos com outros
// *
// * @EnableJpaRepositories na aplicação (ex: em LogsRepositoriesConfig).
// * <p>
// * Configuração:
// * - basePackages: com.api.erp.v1.tenant.domain.repository (contém TenantRepository)
// * - entityManagerFactoryRef: masterEntityManagerFactory (criado em TenantsMasterDatabaseConfig)
// * - transactionManagerRef: masterTransactionManager (criado em TenantsMasterDatabaseConfig)
// * <p>
// * Estratégia:
// * Os dados de TB_TENANT (Tenant) sempre vêm do masterDataSource (defaultDataSource),
// * independentemente de qual tenant/datasource esteja ativo no contexto da requisição.
// * Isso é essencial porque TB_TENANT é uma tabela de CONFIGURAÇÃO central que mapeia
// * tenants para seus respectivos bancos de dados.
// * <p>
// * Padrão baseado em LogsDatabaseConfig:
// * - LogsDatabaseConfig → logsEntityManagerFactory + LogsRepositoriesConfig
// * - TenantsMasterDatabaseConfig → masterEntityManagerFactory + TenantsMasterRepositoriesConfig
// * <p>
// * Benefícios:
// * ✅ Garante que TB_TENANT **SEMPRE** vem do defaultDataSource (master)
// * ✅ EntityManagerFactory específico isolado do routing de tenants
// * ✅ Evita múltiplos @EnableJpaRepositories na mesma aplicação
// * ✅ Separa responsabilidades (DataSource vs Repositories)
// * ✅ Sem conflitos com repositórios de logs e features
// */
//@Deprecated(since = "2.0", forRemoval = true)
//@Configuration
//@EnableJpaRepositories(
//        basePackages = {
//                "com.api.erp.v1.main.tenant.domain.repository"
//        },
//        entityManagerFactoryRef = "masterEntityManagerFactory",
//        transactionManagerRef = "masterTransactionManager"
//)
//public class TenantsMasterRepositoriesConfig {
//    // Apenas configuração de Repositories para master database (TB_TENANT)
//    // Usa masterEntityManagerFactory que só acessa defaultDataSource
//}
