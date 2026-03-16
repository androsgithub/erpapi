package com.api.erp.v1.main.datasource.routing.domain;

import com.api.erp.v1.main.master.tenant.domain.entity.DBType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - TenantDSConfig (Value Object)
 * 
 * Cenários cobertos:
 * 1. Criação com parâmetros válidos e DBType enum
 * 2. Criação com parâmetros válidos e DBType como string
 * 3. Criação com default DBType (PostgreSQL)
 * 4. Validação de campos nulos
 * 5. Validação de campos vazios
 * 6. Validação de DBType null
 * 7. Validação de DBType inválido (string)
 * 8. Imutabilidade (getters apenas)
 * 9. Igualdade entre objetos (equals/hashCode)
 * 10. ToString correto
 * 11. Getters retornam valores corretos
 * 12. DBType conversão e getDriver/Dialect
 * 
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantDSConfig - Testes Unitários")
class TenantDSConfigTest {

    // ===== Testes de Criação com DBType Enum =====

    @Test
    @DisplayName("dado_parametrosValidos_quando_criarComDBTypeEnum_entao_criaCorretamente")
    void testGivenValidParameters_WhenCreateWithDBTypeEnum_ThenCreatesSuccessfully() {
        // Arrange
        Long tenantId = 1L;
        String dbUrl = "jdbc:postgresql://localhost:5432/db";
        String dbUsername = "user";
        String dbPassword = "pass";
        DBType dbType = DBType.POSTGRESQL;

        // Act
        TenantDSConfig config = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbType);

        // Assert
        assertThat(config)
                .as("Deve criar config com sucesso")
                .isNotNull();
        assertThat(config.getTenantId()).isEqualTo(tenantId);
        assertThat(config.getDbUrl()).isEqualTo(dbUrl);
        assertThat(config.getDbUsername()).isEqualTo(dbUsername);
        assertThat(config.getDbPassword()).isEqualTo(dbPassword);
        assertThat(config.getDbType()).isEqualTo(dbType);
    }

    // ===== Testes de Criação com DBType String =====

    @Test
    @DisplayName("dado_parametrosComDBTypeString_quando_criarComStringDBType_entao_converteParaEnum")
    void testGivenParametersWithDBTypeString_WhenCreateWithDBTypeString_ThenConvertsToEnum() {
        // Arrange
        String tenantId = "1";
        String dbUrl = "jdbc:postgresql://localhost:5432/db";
        String dbUsername = "user";
        String dbPassword = "pass";
        String dbTypeString = "POSTGRESQL";

        // Act
        TenantDSConfig config = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbTypeString);

        // Assert
        assertThat(config.getTenantId()).isEqualTo(1L);
        assertThat(config.getDbType()).isEqualTo(DBType.POSTGRESQL);
    }

    @Test
    @DisplayName("dado_dbTypeStringInvalido_quando_criarComStringDBType_entao_lancaIllegalArgumentException")
    void testGivenInvalidDBTypeString_WhenCreateWithDBTypeString_ThenThrowsIllegalArgumentException() {
        // Arrange
        String tenantId = "1";
        String dbUrl = "jdbc:test://localhost/db";
        String dbUsername = "user";
        String dbPassword = "pass";
        String invalidDbTypeString = "INVALID_DB_TYPE";

        // Act & Assert
        assertThatThrownBy(() -> new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, invalidDbTypeString))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de banco de dados não suportado")
                .hasMessageContaining(invalidDbTypeString);
    }

    // ===== Testes de Criação com Default DBType =====

    @Test
    @DisplayName("dado_parametrosSemDBType_quando_criar_entao_usaDefaultPostgreSQL")
    void testGivenParametersWithoutDBType_WhenCreate_ThenUsesDefaultPostgreSQL() {
        // Arrange
        Long tenantId = 1L;
        String dbUrl = "jdbc:postgresql://localhost/db";
        String dbUsername = "user";
        String dbPassword = "pass";

        // Act
        TenantDSConfig config = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword);

        // Assert
        assertThat(config.getDbType())
                .as("Deve usar PostgreSQL como default")
                .isEqualTo(DBType.POSTGRESQL);
    }

    // ===== Testes de Validação - tenantId =====

    @Test
    @DisplayName("dado_tenantIdNull_quando_criar_entao_lancaIllegalArgumentException")
    void testGivenNullTenantId_WhenCreate_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> new TenantDSConfig(
                null,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                DBType.POSTGRESQL
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tenantId não pode ser nulo ou vazio");
    }

    @Test
    @DisplayName("dado_tenantIdStringNull_quando_criarComString_entao_lancaIllegalArgumentException")
    void testGivenNullTenantIdString_WhenCreateWithString_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> new TenantDSConfig(
                null,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                "POSTGRESQL"
        ))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ===== Testes de Validação - dbUrl =====

    @Test
    @DisplayName("dado_dbUrlNull_quando_criar_entao_lancaIllegalArgumentException")
    void testGivenNullDbUrl_WhenCreate_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> new TenantDSConfig(
                1L,
                null,
                "user",
                "pass",
                DBType.POSTGRESQL
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dbUrl não pode ser nulo ou vazio");
    }

    @Test
    @DisplayName("dado_dbUrlVazio_quando_criar_entao_lancaIllegalArgumentException")
    void testGivenEmptyDbUrl_WhenCreate_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> new TenantDSConfig(
                1L,
                "",
                "user",
                "pass",
                DBType.POSTGRESQL
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dbUrl não pode ser nulo ou vazio");
    }

    // ===== Testes de Validação - dbUsername =====

    @Test
    @DisplayName("dado_dbUsernameNull_quando_criar_entao_lancaIllegalArgumentException")
    void testGivenNullDbUsername_WhenCreate_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                null,
                "pass",
                DBType.POSTGRESQL
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dbUsername não pode ser nulo ou vazio");
    }

    // ===== Testes de Validação - dbPassword =====

    @Test
    @DisplayName("dado_dbPasswordNull_quando_criar_entao_lancaIllegalArgumentException")
    void testGivenNullDbPassword_WhenCreate_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                null,
                DBType.POSTGRESQL
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dbPassword não pode ser nulo ou vazio");
    }

    // ===== Testes de Validação - DBType =====

    @Test
    @DisplayName("dado_dbTypeNull_quando_criar_entao_lancaIllegalArgumentException")
    void testGivenNullDBType_WhenCreate_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                (DBType) null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DBType não pode ser null");
    }

    @Test
    @DisplayName("dado_dbTypeStringNull_quando_criarComString_entao_lancaIllegalArgumentException")
    void testGivenNullDBTypeString_WhenCreateWithString_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> new TenantDSConfig(
                "1",
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dbTypeString não pode ser nulo ou vazio");
    }

    // ===== Testes de Getters =====

    @Test
    @DisplayName("dado_configCriada_quando_getChamados_entao_retornamValoresCorretos")
    void testGivenConfigCreated_WhenGettersCalled_ThenReturnCorrectValues() {
        // Arrange
        Long expectedTenantId = 1L;
        String expectedUrl = "jdbc:postgresql://localhost:5432/testdb";
        String expectedUsername = "postgres";
        String expectedPassword = "secret";
        DBType expectedDbType = DBType.POSTGRESQL;

        TenantDSConfig config = new TenantDSConfig(
                expectedTenantId,
                expectedUrl,
                expectedUsername,
                expectedPassword,
                expectedDbType
        );

        // Act & Assert
        assertThat(config.getTenantId()).isEqualTo(expectedTenantId);
        assertThat(config.getDbUrl()).isEqualTo(expectedUrl);
        assertThat(config.getDbUsername()).isEqualTo(expectedUsername);
        assertThat(config.getDbPassword()).isEqualTo(expectedPassword);
        assertThat(config.getDbType()).isEqualTo(expectedDbType);
    }

    // ===== Testes de DBType - Driver Class Name =====

    @Test
    @DisplayName("dado_dbTypePostgreSQL_quando_getDbDriverClassName_entao_retornaPostgresDriver")
    void testGivenDBTypePostgreSQL_WhenGetDbDriverClassName_ThenReturnsPostgresDriver() {
        // Arrange
        TenantDSConfig config = new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                DBType.POSTGRESQL
        );

        // Act
        String driverClassName = config.getDbDriverClassName();

        // Assert
        assertThat(driverClassName)
                .as("Deve retornar driver PostgreSQL")
                .contains("postgresql");
    }

    @Test
    @DisplayName("dado_dbTypeMySQL_quando_getDbDriverClassName_entao_retornaMySQLDriver")
    void testGivenDBTypeMySQL_WhenGetDbDriverClassName_ThenReturnsMySQLDriver() {
        // Arrange
        TenantDSConfig config = new TenantDSConfig(
                1L,
                "jdbc:mysql://localhost/db",
                "user",
                "pass",
                DBType.MYSQL
        );

        // Act
        String driverClassName = config.getDbDriverClassName();

        // Assert
        assertThat(driverClassName)
                .as("Deve retornar driver MySQL")
                .contains("mysql");
    }

    // ===== Testes de DBType - Dialect =====

    @Test
    @DisplayName("dado_dbTypePostgreSQL_quando_getDbDialect_entao_retornaHibernatePostgresDialect")
    void testGivenDBTypePostgreSQL_WhenGetDbDialect_ThenReturnsHibernateDialect() {
        // Arrange
        TenantDSConfig config = new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                DBType.POSTGRESQL
        );

        // Act
        String dialect = config.getDbDialect();

        // Assert
        assertThat(dialect)
                .as("Deve retornar dialect Hibernate para PostgreSQL")
                .isNotNull()
                .isNotEmpty();
    }

    // ===== Testes de Igualdade =====

    @Test
    @DisplayName("dado_duasConfigsComMesmosValores_quando_equals_entao_saoIguais")
    void testGivenTwoConfigsWithSameValues_WhenEquals_ThenAreEqual() {
        // Arrange
        TenantDSConfig config1 = new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                DBType.POSTGRESQL
        );
        TenantDSConfig config2 = new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                DBType.POSTGRESQL
        );

        // Act & Assert
        assertThat(config1)
                .as("Configs com mesmos valores devem ser iguais")
                .isEqualTo(config2);
    }

    @Test
    @DisplayName("dado_duasConfigsComValoresDiferentes_quando_equals_entao_naoSaoIguais")
    void testGivenTwoConfigsWithDifferentValues_WhenEquals_ThenAreNotEqual() {
        // Arrange
        TenantDSConfig config1 = new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                DBType.POSTGRESQL
        );
        TenantDSConfig config2 = new TenantDSConfig(
                2L,  // Tenant ID diferente
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                DBType.POSTGRESQL
        );

        // Act & Assert
        assertThat(config1)
                .as("Configs com tenant IDs diferentes não devem ser iguais")
                .isNotEqualTo(config2);
    }

    // ===== Testes de HashCode =====

    @Test
    @DisplayName("dado_duasConfigsIguais_quando_hashCode_entao_temMesmoHashCode")
    void testGivenTwoEqualConfigs_WhenHashCode_ThenHaveSameHashCode() {
        // Arrange
        TenantDSConfig config1 = new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                DBType.POSTGRESQL
        );
        TenantDSConfig config2 = new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                DBType.POSTGRESQL
        );

        // Act & Assert
        assertThat(config1.hashCode())
                .as("HashCode de configs iguais deve ser o mesmo")
                .isEqualTo(config2.hashCode());
    }

    // ===== Testes de ToString =====

    @Test
    @DisplayName("dado_configValida_quando_toString_entao_contemInformacoesPrincipais")
    void testGivenValidConfig_WhenToString_ThenContainsMainInformation() {
        // Arrange
        TenantDSConfig config = new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                "pass",
                DBType.POSTGRESQL
        );

        // Act
        String stringRepresentation = config.toString();

        // Assert
        assertThat(stringRepresentation)
                .as("ToString deve conter informações principais")
                .contains("TenantDataSourceConfig")
                .contains("tenantId")
                .contains("1")
                .contains("dbUrl")
                .contains("dbType");
    }

    // ===== Testes com Valores Especiais =====

    @Test
    @DisplayName("dado_urlComCaracteresEspeciais_quando_criar_entao_armazenaCorretamente")
    void testGivenUrlWithSpecialCharacters_WhenCreate_ThenStoresCorrectly() {
        // Arrange
        String urlComEspeciais = "jdbc:postgresql://localhost:5432/db?ssl=true&sslmode=require";

        // Act & Assert
        assertThatCode(() -> {
            TenantDSConfig config = new TenantDSConfig(
                    1L,
                    urlComEspeciais,
                    "user",
                    "pass",
                    DBType.POSTGRESQL
            );
            assertThat(config.getDbUrl()).isEqualTo(urlComEspeciais);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("dado_senhaVazia_quando_criar_entao_lancaExcecao")
    void testGivenEmptyPassword_WhenCreate_ThenStoresCorrectly() {
        // Senha vazia (ou apenas espaços) não é permitida
        String password = ""; // valor vazio

        // Act & Assert
        assertThatThrownBy(() -> new TenantDSConfig(
                1L,
                "jdbc:postgresql://localhost/db",
                "user",
                password,
                DBType.POSTGRESQL
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dbPassword não pode ser nulo ou vazio");
    }

    // ========== Testes de Resiliência e Edge Cases ==========

    @Test
    void testMultipleInstancesCreatedConcurrently() {
        // Arrange
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future<TenantDSConfig>> futures = new ArrayList<>();

        // Act
        for (int i = 0; i < 10; i++) {
            final int index = i;
            futures.add(executorService.submit(() -> 
                new TenantDSConfig(
                    (long) index, 
                    "jdbc:postgresql://localhost:5432/db" + index,
                    "user",
                    "pass",
                    DBType.POSTGRESQL
                )
            ));
        }

        // Assert
        List<TenantDSConfig> configs = new ArrayList<>();
        for (Future<TenantDSConfig> future : futures) {
            try {
                configs.add(future.get());
                assertThat(future.get()).isNotNull();
            } catch (Exception e) {
                Assertions.fail("Não deve falhar durante criação concorrente: " + e.getMessage());
            }
        }

        assertThat(configs).hasSize(10);
        executorService.shutdown();
    }

    @Test
    void testImmutabilityAfterCreation() {
        // Arrange
        Long tenantId = 1L;
        String dbUrl = "jdbc:postgresql://localhost:5432/db";
        String dbUsername = "user";
        String dbPassword = "pass";
        DBType dbType = DBType.POSTGRESQL;

        TenantDSConfig config = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbType);

        // Act - Tentar modificar (não deveria ser possível)
        String originalUrl = config.getDbUrl();

        // Assert - Verificar que getter retorna o mesmo
        assertThat(config.getDbUrl()).isEqualTo(originalUrl);
        assertThat(config.getDbUrl()).isEqualTo(dbUrl);
    }

    @Test
    void testEqualityWithSameValues() {
        // Arrange
        Long tenantId = 1L;
        String dbUrl = "jdbc:postgresql://localhost:5432/db";
        String dbUsername = "user";
        String dbPassword = "pass";
        DBType dbType = DBType.POSTGRESQL;

        TenantDSConfig config1 = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbType);
        TenantDSConfig config2 = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbType);

        // Assert
        assertThat(config1).isEqualTo(config2);
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    }

    @Test
    void testInequalityWithDifferentValues() {
        // Arrange
        Long tenantId1 = 1L;
        Long tenantId2 = 2L;
        String dbUrl = "jdbc:postgresql://localhost:5432/db";
        String dbUsername = "user";
        String dbPassword = "pass";
        DBType dbType = DBType.POSTGRESQL;

        TenantDSConfig config1 = new TenantDSConfig(tenantId1, dbUrl, dbUsername, dbPassword, dbType);
        TenantDSConfig config2 = new TenantDSConfig(tenantId2, dbUrl, dbUsername, dbPassword, dbType);

        // Assert
        assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    void testStringReprintationIsValid() {
        // Arrange
        Long tenantId = 1L;
        String dbUrl = "jdbc:postgresql://localhost:5432/db";
        String dbUsername = "user";
        String dbPassword = "pass";
        DBType dbType = DBType.POSTGRESQL;

        TenantDSConfig config = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbType);

        // Act
        String toString = config.toString();

        // Assert
        assertThat(toString).isNotNull();
        assertThat(toString).isNotEmpty();
        assertThat(toString).contains("DataSourceConfig");
    }

    @Test
    void testGettersReturnCorrectValues() {
        // Arrange
        Long tenantId = 42L;
        String dbUrl = "jdbc:mysql://localhost:3306/testdb";
        String dbUsername = "testuser";
        String dbPassword = "testpass";
        DBType dbType = DBType.MYSQL;

        // Act
        TenantDSConfig config = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbType);

        // Assert
        assertThat(config.getTenantId()).isEqualTo(42L);
        assertThat(config.getDbUrl()).isEqualTo("jdbc:mysql://localhost:3306/testdb");
        assertThat(config.getDbUsername()).isEqualTo("testuser");
        assertThat(config.getDbPassword()).isEqualTo("testpass");
        assertThat(config.getDbType()).isEqualTo(DBType.MYSQL);
    }

    @Test
    void testSameInstanceCreatedMultipleTimes() {
        // Arrange
        Long tenantId = 1L;
        String dbUrl = "jdbc:postgresql://localhost:5432/db";
        String dbUsername = "user";
        String dbPassword = "pass";
        DBType dbType = DBType.POSTGRESQL;

        // Act
        TenantDSConfig config1 = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbType);
        TenantDSConfig config2 = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbType);
        TenantDSConfig config3 = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbType);

        // Assert
        assertThat(config1).isEqualTo(config2).isEqualTo(config3);
    }

    @Test
    void testDBTypeEnumConversion() {
        // Arrange
        Long tenantId = 1L;
        String dbUrl = "jdbc:h2:mem:test";
        String dbUsername = "user";
        String dbPassword = "pass";

        // Act & Assert - Verificar conversão para diferentes DBTypes
        for (DBType dbType : DBType.values()) {
            TenantDSConfig config = new TenantDSConfig(tenantId, dbUrl, dbUsername, dbPassword, dbType);
            assertThat(config.getDbType()).isEqualTo(dbType);
            assertThat(config.getDbType()).isNotNull();
        }
    }

    @Test
    void testNullTenantIdRejected() {
        // Act & Assert
        assertThatThrownBy(() ->
            new TenantDSConfig(null, "jdbc:postgresql://localhost:5432/db", "user", "pass", DBType.POSTGRESQL)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tenantId não pode ser nulo");
    }

    @Test
    void testValueObjectBehavior() {
        // Arrange - dois objetos com mesmos valores
        TenantDSConfig config1 = new TenantDSConfig(
            1L, "jdbc:postgresql://localhost:5432/db", "user", "pass", DBType.POSTGRESQL
        );
        TenantDSConfig config2 = new TenantDSConfig(
            1L, "jdbc:postgresql://localhost:5432/db", "user", "pass", DBType.POSTGRESQL
        );

        // Assert - Value objects devem ser iguais por valor
        assertThat(config1).isEqualTo(config2);
        assertThat(config1).hasSameHashCodeAs(config2);
        assertThat(config1.toString()).isEqualTo(config2.toString());
    }

    @Test
    void testLargeNumberOfConcurrentInstances() throws InterruptedException, java.util.concurrent.ExecutionException {
        // Arrange
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int numberOfInstances = 100;
        List<Future<TenantDSConfig>> futures = new ArrayList<>();

        // Act
        for (int i = 0; i < numberOfInstances; i++) {
            final int index = i;
            futures.add(executorService.submit(() ->
                new TenantDSConfig(
                    (long) index % 10,
                    "jdbc:postgresql://localhost:5432/db" + (index % 5),
                    "user" + index,
                    "pass" + index,
                    DBType.values()[index % DBType.values().length]
                )
            ));
        }

        // Assert
        List<TenantDSConfig> configs = new ArrayList<>();
        for (Future<TenantDSConfig> future : futures) {
            configs.add(future.get());
        }

        assertThat(configs).hasSize(numberOfInstances);
        executorService.shutdown();
        Assertions.assertTrue(
            executorService.awaitTermination(10, TimeUnit.SECONDS),
            "Execução concorrente deve terminar em tempo adequado"
        );
    }
}
