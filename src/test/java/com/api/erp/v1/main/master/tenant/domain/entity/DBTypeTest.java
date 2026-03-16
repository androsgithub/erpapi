package com.api.erp.v1.main.master.tenant.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - DBType
 *
 * Valida resolvers fromNome, fromDialeto, fromDriver, isSupported e propriedades.
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("DBType - Testes Unitários")
class DBTypeTest {

    @Test
    @DisplayName("dado_enum_quando_verificarQuantidade_entao_possui16Valores")
    void testGivenEnum_WhenCheckCount_ThenHas16Values() {
        assertThat(DBType.values()).hasSize(16);
    }

    // ===== fromNome =====

    @Test
    @DisplayName("dado_nomeMysql_quando_fromNome_entao_retornaMYSQL")
    void testGivenMysqlName_WhenFromNome_ThenReturnsMYSQL() {
        assertThat(DBType.fromNome("mysql")).isEqualTo(DBType.MYSQL);
    }

    @Test
    @DisplayName("dado_nomePostgresqlCaseInsensitive_quando_fromNome_entao_retornaPOSTGRESQL")
    void testGivenPostgresqlCaseInsensitive_WhenFromNome_ThenReturnsPOSTGRESQL() {
        assertThat(DBType.fromNome("PostgreSQL")).isEqualTo(DBType.POSTGRESQL);
        assertThat(DBType.fromNome("POSTGRESQL")).isEqualTo(DBType.POSTGRESQL);
    }

    @Test
    @DisplayName("dado_nomeH2_quando_fromNome_entao_retornaH2")
    void testGivenH2Name_WhenFromNome_ThenReturnsH2() {
        assertThat(DBType.fromNome("h2")).isEqualTo(DBType.H2);
    }

    @Test
    @DisplayName("dado_nomeComEspacos_quando_fromNome_entao_funciona")
    void testGivenNameWithSpaces_WhenFromNome_ThenWorks() {
        assertThat(DBType.fromNome("  mysql  ")).isEqualTo(DBType.MYSQL);
    }

    @Test
    @DisplayName("dado_nomeNull_quando_fromNome_entao_lancaIllegalArgument")
    void testGivenNullName_WhenFromNome_ThenThrowsIllegalArgument() {
        assertThatThrownBy(() -> DBType.fromNome(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("dado_nomeVazio_quando_fromNome_entao_lancaIllegalArgument")
    void testGivenEmptyName_WhenFromNome_ThenThrowsIllegalArgument() {
        assertThatThrownBy(() -> DBType.fromNome(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("dado_nomeInvalido_quando_fromNome_entao_lancaIllegalArgument")
    void testGivenInvalidName_WhenFromNome_ThenThrowsIllegalArgument() {
        assertThatThrownBy(() -> DBType.fromNome("mongodb"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported database");
    }

    // ===== fromDialeto =====

    @Test
    @DisplayName("dado_dialetoMysql_quando_fromDialeto_entao_retornaMYSQL")
    void testGivenMysqlDialect_WhenFromDialeto_ThenReturnsMYSQL() {
        assertThat(DBType.fromDialeto("org.hibernate.dialect.MySQLDialect")).isEqualTo(DBType.MYSQL);
    }

    @Test
    @DisplayName("dado_dialetoNull_quando_fromDialeto_entao_lancaIllegalArgument")
    void testGivenNullDialect_WhenFromDialeto_ThenThrowsIllegalArgument() {
        assertThatThrownBy(() -> DBType.fromDialeto(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("dado_dialetoInvalido_quando_fromDialeto_entao_lancaIllegalArgument")
    void testGivenInvalidDialect_WhenFromDialeto_ThenThrowsIllegalArgument() {
        assertThatThrownBy(() -> DBType.fromDialeto("org.hibernate.dialect.MongoDialect"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unrecognized dialect");
    }

    // ===== fromDriver =====

    @Test
    @DisplayName("dado_driverH2_quando_fromDriver_entao_retornaH2")
    void testGivenH2Driver_WhenFromDriver_ThenReturnsH2() {
        assertThat(DBType.fromDriver("org.h2.Driver")).isEqualTo(DBType.H2);
    }

    @Test
    @DisplayName("dado_driverPostgresql_quando_fromDriver_entao_retornaPOSTGRESQL")
    void testGivenPostgresqlDriver_WhenFromDriver_ThenReturnsPOSTGRESQL() {
        assertThat(DBType.fromDriver("org.postgresql.Driver")).isEqualTo(DBType.POSTGRESQL);
    }

    @Test
    @DisplayName("dado_driverNull_quando_fromDriver_entao_lancaIllegalArgument")
    void testGivenNullDriver_WhenFromDriver_ThenThrowsIllegalArgument() {
        assertThatThrownBy(() -> DBType.fromDriver(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("dado_driverInvalido_quando_fromDriver_entao_lancaIllegalArgument")
    void testGivenInvalidDriver_WhenFromDriver_ThenThrowsIllegalArgument() {
        assertThatThrownBy(() -> DBType.fromDriver("com.mongodb.Driver"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unrecognized driver");
    }

    // ===== isSupported =====

    @Test
    @DisplayName("dado_driverConhecido_quando_isSupported_entao_retornaTrue")
    void testGivenKnownDriver_WhenIsSupported_ThenReturnsTrue() {
        assertThat(DBType.isSupported("org.h2.Driver")).isTrue();
        assertThat(DBType.isSupported("org.postgresql.Driver")).isTrue();
    }

    @Test
    @DisplayName("dado_driverDesconhecido_quando_isSupported_entao_retornaFalse")
    void testGivenUnknownDriver_WhenIsSupported_ThenReturnsFalse() {
        assertThat(DBType.isSupported("com.mongodb.Driver")).isFalse();
    }

    @Test
    @DisplayName("dado_driverNull_quando_isSupported_entao_retornaFalse")
    void testGivenNullDriver_WhenIsSupported_ThenReturnsFalse() {
        assertThat(DBType.isSupported(null)).isFalse();
        assertThat(DBType.isSupported("")).isFalse();
    }

    // ===== Propriedades =====

    @ParameterizedTest
    @EnumSource(DBType.class)
    @DisplayName("dado_qualquerDBType_quando_acessarPropriedades_entao_naoSaoNulas")
    void testGivenAnyDBType_WhenAccessProperties_ThenNotNull(DBType dbType) {
        assertThat(dbType.getNome()).isNotNull().isNotBlank();
        assertThat(dbType.getDialeto()).isNotNull().isNotBlank();
        assertThat(dbType.getDriver()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("dado_MYSQL_quando_toString_entao_formatoCorreto")
    void testGivenMYSQL_WhenToString_ThenCorrectFormat() {
        assertThat(DBType.MYSQL.toString()).contains("mysql").contains("Dialeto:");
    }
}
