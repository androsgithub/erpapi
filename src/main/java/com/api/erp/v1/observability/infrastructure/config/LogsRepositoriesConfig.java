package com.api.erp.v1.observability.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuração isolada de JPA Repositories para o banco de logs (erpapi_logs).
 * 
 * Esta classe é separada de LogsDatabaseConfig para evitar conflitos com outros
 * @EnableJpaRepositories na aplicação (ex: em TenantsConfiguration).
 * 
 * Configuração:
 * - basePackages: com.api.erp.v1.observability.domain.repository (contém FlowEventRepository)
 * - entityManagerFactoryRef: logsEntityManagerFactory (criado em LogsDatabaseConfig)
 * - transactionManagerRef: logsTransactionManager (criado em LogsDatabaseConfig)
 * 
 * Benefícios:
 * ✅ Evita múltiplos @EnableJpaRepositories na mesma aplicação
 * ✅ Separa responsabilidades (DataSource vs Repositories)
 * ✅ Garante que FlowEventRepository use o EntityManager correto
 * ✅ Sem conflitos com repositórios de tenants e features
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.api.erp.v1.observability.domain.repository",
        entityManagerFactoryRef = "logsEntityManagerFactory",
        transactionManagerRef = "logsTransactionManager"
)
public class LogsRepositoriesConfig {
    // Apenas configuração de Repositories para logs
}
