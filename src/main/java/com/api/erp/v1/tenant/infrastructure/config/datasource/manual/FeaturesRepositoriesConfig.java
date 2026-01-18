package com.api.erp.v1.tenant.infrastructure.config.datasource.manual;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuração de JPA Repositories para os repositories multi-tenant (features).
 *
 * Esta classe é separada de TenantsConfiguration para evitar conflitos com outros
 * @EnableJpaRepositories na aplicação (ex: em LogsRepositoriesConfig e TenantsMasterRepositoriesConfig).
 *
 * Configuração:
 * - basePackages: Todos os repositories de features (endereco, cliente, contato, usuario, etc)
 * - entityManagerFactoryRef: entityManagerFactory (criado em TenantsConfiguration)
 * - transactionManagerRef: transactionManager (criado em TenantsConfiguration)
 *
 * Estratégia de Multi-Tenancy:
 * Os repositories de features usam o MultiTenantRoutingDataSource, que roteia
 * automaticamente para o banco de dados do tenant baseado no contexto da requisição.
 *
 * Benefícios:
 * ✅ Evita múltiplos @EnableJpaRepositories na mesma aplicação
 * ✅ Separa responsabilidades (DataSource vs Repositories)
 * ✅ Garante que features repositories usem o MultiTenantRoutingDataSource
 * ✅ Sem conflitos com repositórios de master (TenantsMasterRepositoriesConfig) e logs (LogsRepositoriesConfig)
 */
@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.api.erp.v1.features.endereco.domain.repository",
                "com.api.erp.v1.features.cliente.domain.repository",
                "com.api.erp.v1.features.contato.domain.repository",
                "com.api.erp.v1.features.usuario.domain.repository",
                "com.api.erp.v1.features.permissao.domain.repository",
                "com.api.erp.v1.features.produto.domain.repository",
                "com.api.erp.v1.features.camposcustom.domain.repository",
                "com.api.erp.v1.features.unidademedida.domain.repository"
        },
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class FeaturesRepositoriesConfig {
    // Configuração de Repositories para features multi-tenant
}
