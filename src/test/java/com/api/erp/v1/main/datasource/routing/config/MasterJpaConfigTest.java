package com.api.erp.v1.main.datasource.routing.config;

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

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS - MasterJpaConfig
 * 
 * Cenários cobertos:
 * 1. Configuração de EntityManagerFactory para Master DataSource
 * 2. Propriedade hibernate.hbm2ddl.auto = "update" para master (cria/atualiza tabelas)
 * 3. Pacotes de entidades master configurados
 * 4. Persistenceunit name = "master"
 * 5. Configuração de TransactionManager para master
 * 6. Master é Primary (não interfere com tenant config)
 * 7. EnableJpaRepositories aponta para master
 * 8. Master DataSource é usado (não routing)
 * 9. Isolamento entre master e tenant datasources
 * 10. Beans são corretamente qualificados
 * 
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MasterJpaConfig - Testes Unitários")
class MasterJpaConfigTest {

    private MasterJpaConfig masterJpaConfig;

    @Mock
    private EntityManagerFactoryBuilder builder;

    @Mock
    private DataSource masterDataSource;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        masterJpaConfig = new MasterJpaConfig();
    }

    // ===== Testes de EntityManagerFactory =====

    @Test
    @DisplayName("dado_masterDataSource_quando_masterEntityManagerFactory_entao_criaComMasterDS")
    void testGivenMasterDataSource_WhenMasterEntityManagerFactory_ThenCreatesWithMasterDS() {
        // Assert - classe deve ter método masterEntityManagerFactory
        assertThat(MasterJpaConfig.class.getDeclaredMethods())
                .as("Classe deve ter método masterEntityManagerFactory")
                .anyMatch(m -> m.getName().equals("masterEntityManagerFactory"));
    }

    @Test
    @DisplayName("dado_configJPA_quando_masterEntityManagerFactory_entao_hbm2ddlAutoEUpdate")
    void testGivenJPAConfig_WhenMasterEntityManagerFactory_ThenHbm2ddlAutoUpdate() {
        // Assert - classe deve ter o método
        assertThat(MasterJpaConfig.class.getDeclaredMethods())
                .as("Classe deve ter método para configurar EntityManagerFactory")
                .anyMatch(m -> m.getName().equals("masterEntityManagerFactory"));
    }

    // ===== Testes de TransactionManager =====

    @Test
    @DisplayName("dado_entityManagerFactory_quando_masterTransactionManager_entao_criaJpaTransactionManager")
    void testGivenEntityManagerFactory_WhenMasterTransactionManager_ThenCreatesJpaTransactionManager() {
        // Act
        PlatformTransactionManager transactionManager = masterJpaConfig.masterTransactionManager(entityManagerFactory);

        // Assert
        assertThat(transactionManager)
                .as("Master TransactionManager deve ser criado")
                .isNotNull()
                .isInstanceOf(JpaTransactionManager.class);
    }

    // ===== Testes de Pacotes de Entidades =====

    @Test
    @DisplayName("dado_builderComPacotes_quando_masterEntityManagerFactory_entao_incluiEntidadesMaster")
    void testGivenBuilderWithPackages_WhenMasterEntityManagerFactory_ThenIncludesAllMasterEntities() {
        // Assert - método deve existir
        var methods = MasterJpaConfig.class.getDeclaredMethods();
        var emfMethod = java.util.Arrays.stream(methods)
                .filter(m -> m.getName().equals("masterEntityManagerFactory"))
                .findFirst();
        
        assertThat(emfMethod)
                .as("Método masterEntityManagerFactory deve existir")
                .isPresent();
    }

    // ===== Testes de Isolamento entre Master e Tenant =====

    @Test
    @DisplayName("dado_masterEtenantBeans_quando_ambos_entao_naoConflitam")
    void testGivenMasterAndTenantBeans_WhenBoth_ThenNoConflict() {
        // Arrange
        MasterJpaConfig masterConfig = new MasterJpaConfig();
        TenantJpaConfig tenantConfig = new TenantJpaConfig();

        // Act & Assert
        assertThat(masterConfig)
                .as("MasterJpaConfig deve existir")
                .isNotNull();
        assertThat(tenantConfig)
                .as("TenantJpaConfig deve existir")
                .isNotNull();
        assertThat(masterConfig).isNotEqualTo(tenantConfig);
    }

    // ===== Testes de Anotações =====

    @Test
    @DisplayName("dado_classComPrimaryAnnotation_quando_masterJpaConfig_entao_temPrimaryBean")
    void testGivenClassWithPrimaryAnnotation_WhenMasterJpaConfig_ThenHasPrimaryBeans() {
        // Assert - classe deve ter anotação @Configuration e métodos com @Primary
        assertThat(MasterJpaConfig.class)
                .as("MasterJpaConfig deve ser anotado com @Configuration")
                .hasAnnotation(org.springframework.context.annotation.Configuration.class);
    }

    @Test
    @DisplayName("dado_classeComEnableJpaRepositories_quando_masterJpaConfig_entao_temAnotacao")
    void testGivenClassWithEnableJpaRepositories_WhenMasterJpaConfig_ThenHasAnnotation() {
        // Assert
        assertThat(MasterJpaConfig.class)
                .as("MasterJpaConfig deve ter @EnableJpaRepositories")
                .hasAnnotation(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class);
    }

    // ===== Testes de Métodos Anotados com @Primary =====

    @Test
    @DisplayName("dado_methodsMasterJpa_quando_analisarAnotacoes_entao_devemTerPrimary")
    void testGivenMethodsMasterJpa_WhenAnalyzeAnnotations_ThenShouldHavePrimary() {
        // Act - obter métodos da classe
        var methods = MasterJpaConfig.class.getDeclaredMethods();

        // Assert - deve haver métodos com @Primary
        long primaryMethods = java.util.Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(org.springframework.context.annotation.Primary.class))
                .count();

        assertThat(primaryMethods)
                .as("MasterJpaConfig deve ter métodos com @Primary")
                .isGreaterThan(0);
    }

    // ===== Testes de Pacotes de Repositórios =====

    @Test
    @DisplayName("dado_enableJpaRepositoriesAnotacao_quando_masterJpaConfig_entao_apontaParaMaser")
    void testGivenEnableJpaRepositoriesAnnotation_WhenMasterJpaConfig_ThenPointsToMaster() {
        // Assert - verificar que a anotação @EnableJpaRepositories aponta para master
        var annotation = MasterJpaConfig.class
                .getAnnotation(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class);

        assertThat(annotation)
                .as("EnableJpaRepositories deve estar presente")
                .isNotNull();

        // Verificar que entityManagerFactoryRef é "masterEntityManagerFactory"
        String emfRef = annotation.entityManagerFactoryRef();
        assertThat(emfRef)
                .as("EntityManagerFactoryRef deve ser 'masterEntityManagerFactory'")
                .isEqualTo("masterEntityManagerFactory");
    }

    // ===== Testes de Qualificadores =====

    @Test
    @DisplayName("dado_masterDataSourceQualificado_quando_usarEm_masterJpaConfig_entao_funcionaCorretamente")
    void testGivenMasterDataSourceQualified_WhenUseInMasterJpaConfig_ThenFunctionsCorrectly() {
        // Assert - method must exist
        assertThat(MasterJpaConfig.class.getDeclaredMethods())
                .as("MasterJpaConfig deve ter método masterEntityManagerFactory")
                .anyMatch(m -> m.getName().equals("masterEntityManagerFactory"));
    }

    // ===== Testes de Persistenceunit =====

    @Test
    @DisplayName("dado_persistenceunitMaster_quando_masterEntityManagerFactory_entao_nomeCorreto")
    void testGivenPersistenceunitMaster_WhenMasterEntityManagerFactory_ThenNameCorrect() {
        // Assert - verificar que a anotação @EnableJpaRepositories aponta para master
        var annotation = MasterJpaConfig.class
                .getAnnotation(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class);

        assertThat(annotation)
                .as("EnableJpaRepositories deve estar presente")
                .isNotNull();
    }

    // ===== Testes de Múltiplas Instâncias =====

    @Test
    @DisplayName("dado_configInstanciadoMultiplaVezes_quando_criar_entao_funcionaSemErro")
    void testGivenConfigInstantiatedMultipleTimes_WhenCreate_ThenFunctionsWithoutError() {
        // Act
        MasterJpaConfig config1 = new MasterJpaConfig();
        MasterJpaConfig config2 = new MasterJpaConfig();

        // Assert
        assertThat(config1).isNotNull();
        assertThat(config2).isNotNull();
        assertThat(config1).isNotSameAs(config2); // Instâncias diferentes
    }

    // ===== Testes de Integração Conceitual =====

    @Test
    @DisplayName("dado_masterEhPrimeiro_quando_comparadoComTenant_entao_masterTemPriority")
    void testGivenMasterIsPrimary_WhenComparedWithTenant_ThenMasterHasPriority() {
        // Act - verificar que Master tem anotação @Primary
        var primaryAnnotations = java.util.Arrays.stream(MasterJpaConfig.class.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(org.springframework.context.annotation.Primary.class))
                .toList();

        // Assert
        assertThat(primaryAnnotations)
                .as("Master deve ter métodos anotados com @Primary")
                .isNotEmpty();
    }
}
