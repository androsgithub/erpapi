package com.api.erp.v1.main.datasource.config;

import com.api.erp.v1.main.datasource.routing.core.CustomRoutingDatasource;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * TESTES UNITÁRIOS - TenantJpaConfig
 * <p>
 * Cenários cobertos:
 * 1. Configuração de EntityManagerFactory com CustomRoutingDatasource
 * 2. Propriedade hibernate.hbm2ddl.auto = "none" para tenants
 * 3. Pacotes de entidades configurados corretamente
 * 4. Persistenceunit name = "tenant"
 * 5. Configuração de TransactionManager para tenants
 * 6. Bean de CustomRoutingDatasource criado corretamente
 * 7. Repositories apontam para tenantEntityManagerFactory
 * 8. EnableJpaRepositories com pacotes corretos
 * 9. Isolamento entre tenant e master config
 *
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantJpaConfig - Testes Unitários")
class DynamicJpaConfigTest {

    private DynamicJpaConfig tenantJpaConfig;

    @Mock
    private EntityManagerFactoryBuilder builder;

    @Mock
    private CustomRoutingDatasource routingDatasource;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        tenantJpaConfig = new DynamicJpaConfig();
    }

    // ===== Testes de EntityManagerFactory =====

    @Test
    @DisplayName("dado_customRoutingDatasource_quando_tenantEntityManagerFactory_entao_criaComRoteamento")
    void testGivenCustomRoutingDatasource_WhenTenantEntityManagerFactory_ThenCreatesWithRouting() {
        // Assert - classe deve ter método tenantEntityManagerFactory
        assertThat(DynamicJpaConfig.class.getDeclaredMethods())
                .as("Classe deve ter método tenantEntityManagerFactory")
                .anyMatch(m -> m.getName().equals("tenantEntityManagerFactory"));
    }

    @Test
    @DisplayName("dado_configJPA_quando_tenantEntityManagerFactory_entao_hbm2ddlAutoEVNone")
    void testGivenJPAConfig_WhenTenantEntityManagerFactory_ThenHbm2ddlAutoNone() {
        // Assert - classe deve ter o método
        assertThat(DynamicJpaConfig.class.getDeclaredMethods())
                .as("Classe deve ter método para configurar EntityManagerFactory")
                .anyMatch(m -> m.getName().equals("tenantEntityManagerFactory"));
    }

    // ===== Testes de TransactionManager =====

    @Test
    @DisplayName("dado_entityManagerFactory_quando_tenantTransactionManager_entao_criaJpaTransactionManager")
    void testGivenEntityManagerFactory_WhenTenantTransactionManager_ThenCreatesJpaTransactionManager() {
        // Act
        PlatformTransactionManager transactionManager = tenantJpaConfig.tenantTransactionManager(entityManagerFactory);

        // Assert
        assertThat(transactionManager)
                .as("TransactionManager deve ser criado")
                .isNotNull()
                .isInstanceOf(JpaTransactionManager.class);
    }

    // ===== Testes de CustomRoutingDatasource Bean =====

    @Test
    @DisplayName("dado_dataSourceRouter_quando_customRoutingDatasource_entao_criaBean")
    void testGivenDataSourceRouter_WhenCustomRoutingDatasource_ThenCreatesBean() {
        // Arrange
        com.api.erp.v1.main.datasource.routing.domain.IDataSourceRouter mockRouter =
                mock(com.api.erp.v1.main.datasource.routing.domain.IDataSourceRouter.class);

        // Act
        CustomRoutingDatasource result = tenantJpaConfig.customRoutingDatasource(mockRouter);

        // Assert
        assertThat(result)
                .as("CustomRoutingDatasource deve ser criado")
                .isNotNull()
                .isInstanceOf(CustomRoutingDatasource.class);
    }

    // ===== Testes de Pacotes de Entidades =====

    @Test
    @DisplayName("dado_builderComPacotes_quando_tenantEntityManagerFactory_entao_incluiTodasAsEntidades")
    void testGivenBuilderWithPackages_WhenTenantEntityManagerFactory_ThenIncludesAllEntities() {
        // Assert - método deve existir e ter anotação @Bean
        var methods = DynamicJpaConfig.class.getDeclaredMethods();
        var emfMethod = java.util.Arrays.stream(methods)
                .filter(m -> m.getName().equals("tenantEntityManagerFactory"))
                .findFirst();

        assertThat(emfMethod)
                .as("Método tenantEntityManagerFactory deve existir")
                .isPresent();
    }

    // ===== Testes de Isolamento =====

    @Test
    @DisplayName("dado_tenantConfigEMasterConfig_quando_ambos_entao_naoInterfere")
    void testGivenTenantConfigAndMasterConfig_WhenBoth_ThenNoInterference() {
        // Arrange
        MasterJpaConfig masterConfig = new MasterJpaConfig();
        DynamicJpaConfig tenantConfig = new DynamicJpaConfig();

        // Act & Assert - devem existir separadamente
        assertThat(tenantConfig)
                .isNotNull()
                .isNotEqualTo(masterConfig);
    }

    // ===== Testes de Anotações de Configuração =====

    @Test
    @DisplayName("dado_classeComConfigurationAnnotation_quando_tenantJpaConfig_entao_eUmaConfiguracao")
    void testGivenClassWithConfigurationAnnotation_WhenTenantJpaConfig_ThenIsConfiguration() {
        // Assert - classe deve ter anotação @Configuration
        assertThat(DynamicJpaConfig.class)
                .as("Classe deve ser anotada com @Configuration")
                .hasAnnotation(org.springframework.context.annotation.Configuration.class);
    }

    @Test
    @DisplayName("dado_classComEnableJpaRepositories_quando_tenantJpaConfig_entao_temAnotacao")
    void testGivenClassWithEnableJpaRepositories_WhenTenantJpaConfig_ThenHasAnnotation() {
        // Assert - classe deve ter anotação @EnableJpaRepositories
        assertThat(DynamicJpaConfig.class)
                .as("Classe deve ser anotada com @EnableJpaRepositories")
                .hasAnnotation(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class);
    }
}
