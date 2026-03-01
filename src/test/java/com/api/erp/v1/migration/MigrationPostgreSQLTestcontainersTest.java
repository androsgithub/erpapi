//package com.api.erp.v1.migration;
//
//import org.flywaydb.core.Flyway;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import javax.sql.DataSource;
//
//import java.sql.SQLException;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Testes de Migração com PostgreSQL em Container (Testcontainers).
// *
// * QUANDO USAR:
// * - Quando você quer testar contra um banco REAL (não H2)
// * - Para validar compatibilidade com PostgreSQL/MySQL
// * - Quando a migração usa features específicas do BD
// * - Para testes de integração mais robustos
// *
// * COMO FUNCIONA:
// * - @Testcontainers cria container PostgreSQL automaticamente
// * - DynamicPropertySource injeta URL na application.properties
// * - Cada teste roda em transação (rollback automático)
// * - Container é compartilhado entre testes (mais rápido)
// *
// * VANTAGENS:
// * - Testa contra BD real (PostgreSQL)
// * - Não depende de H2 e compatibilidade MODE=MYSQL
// * - Flyway executa idêntico à produção
// * - Suporta features avançadas (jsonb, arrays, etc)
// *
// * DESVANTAGENS:
// * - Mais lento que H2 (precisa de container)
// * - Requer Docker disponível
// * - Usa mais recursos
// *
// * RECOMENDAÇÃO:
// * - Use este teste para testes críticos / integração
// * - Use H2 para testes rápidos de desenvolvimento
// * - Configure CI/CD para rodar ambos
// *
// * @author Your Team
// * @version 1.0
// */
//@SpringBootTest
//@Testcontainers
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
//@Transactional
//@DisplayName("Migração com PostgreSQL Real (Testcontainers)")
//class MigrationPostgreSQLTestcontainersTest extends AbstractMigrationTest {
//
//    // Container PostgreSQL compartilhado entre testes
//    @Container
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
//            .withDatabaseName("erp_test_db")
//            .withUsername("erp_user")
//            .withPassword("erp_password")
//            .withReuse(true);  // Reusa container entre teste runs (mais rápido)
//
//    /**
//     * Injeta propriedades de conexão dinamicamente.
//     * Spring usa isso ao invés de application.properties
//     */
//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
//        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
//    }
//
//    // ============= TESTES =============
//
//    @Test
//    @Disabled
//    @DisplayName("Deve conectar ao container PostgreSQL com sucesso")
//    void shouldConnectToPostgresContainer() {
//        assertTrue(postgres.isRunning(), "Container PostgreSQL deve estar rodando");
//        assertNotNull(jdbcTemplate, "JdbcTemplate deve estar injetado");
//    }
//
//    @Test
//    @Disabled
//    @DisplayName("Deve criar tabelas em PostgreSQL com sucesso")
//    void shouldCreateTablesInPostgres() {
//        assertTrue(tableExists("tb_address"), "Tabela tb_address deve existir");
//        assertTrue(tableExists("tb_contacts"), "Tabela tb_contacts deve existir");
//        assertTrue(tableExists("tb_tenant"), "Tabela tb_tenant deve existir");
//    }
//
//    @Test
//    @Disabled
//    @DisplayName("Deve validar tipos de dados PostgreSQL específicos")
//    void shouldValidatePostgresSQLSpecificTypes() {
//        ColumnInfo idColumn = getColumnInfo("tb_address", "id");
//
//        assertNotNull(idColumn, "Coluna 'id' deve existir");
//        assertEquals("bigint", idColumn.getType(), "Em PostgreSQL, BIGINT é 'bigint'");
//    }
//
//    @Test
//    @Disabled
//    @DisplayName("Deve validar sequência auto-increment no PostgreSQL")
//    void shouldValidatePostgresSequence() {
//        ColumnInfo idColumn = getColumnInfo("tb_address", "id");
//
//        assertNotNull(idColumn, "Coluna 'id' deve existir");
//        assertTrue(idColumn.isAutoIncrement(), "Coluna 'id' deve usar sequência");
//    }
//
//    @Test
//    @Disabled
//    @DisplayName("Deve permitir INSERT e respeitar constraints PostgreSQL")
//    void shouldRespectPostgresConstraints() {
//        // INSERT válido deve funcionar
//        int inserted = executeUpdate(
//                "INSERT INTO tb_address (tenant_id, rua, numero, bairro, cidade, estado, cep, tipo, principal, deleted) " +
//                        "VALUES (1, 'Rua Teste', '123', 'Centro', 'São Paulo', 'SP', '01234567', 'RESIDENTIAL', false, false)"
//        );
//
//        assertEquals(1, inserted, "Deve inserir exatamente 1 registro");
//
//        // Validar que o registro foi inserido
//        long count = countTableRecords("tb_address");
//        assertTrue(count > 0, "Deve ter registros em tb_address");
//    }
//
//    @Test
//    @Disabled
//    @DisplayName("Deve validar enum em PostgreSQL")
//    void shouldValidatePostgresEnum() {
//        ColumnInfo tipoColumn = getColumnInfo("tb_address", "tipo");
//
//        assertNotNull(tipoColumn, "Coluna 'tipo' deve existir");
//        // Em PostgreSQL real, ENUM aparece como 'USER-DEFINED'
//        // ou pode ser visto como 'character varying' dependendo da versão
//        assertTrue(
//                tipoColumn.getType().contains("enum") ||
//                        tipoColumn.getType().contains("character varying"),
//                "Coluna 'tipo' deve ser tipo enum"
//        );
//    }
//
//    @Test
//    @Disabled
//    @DisplayName("Deve validar foreign keys em PostgreSQL")
//    void shouldValidatePostgresForeignKeys() {
//        var foreignKeys = getForeignKeys("tb_address");
//
//        assertTrue(
//                foreignKeys.stream().anyMatch(fk -> fk.getPkTableName().equalsIgnoreCase("tb_tenant")),
//                "Deve havr FK para tb_tenant"
//        );
//    }
//
//    @Test
//    @Disabled
//    @DisplayName("Container retem dados entre testes (mas cada um usa transação)")
//    void verifyTransactionIsolation() {
//        // Cada @Test está em @Transactional, então rollback automático
//        // Mas você pode validar que os dados de seed persistem
//
//        long tenantCount = countTableRecords("tb_tenant");
//        assertTrue(tenantCount > 0, "Deve ter tenants (dados de seed de V2)");
//    }
//
//    // ============= TESTES DE PERFORMANCE =============
//
//    @Test
//    @Disabled
//    @DisplayName("Índices devem melhomar performance de queries")
//    void shouldHaveIndexesForPerformance() {
//        // Validar que índices comuns foram criados
//        Map<String, IndexInfo> indexes = null;
//        try {
//            indexes = getTableIndexes("tb_address");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Tipicamente, deve haver índices em FKs
//        assertFalse(indexes.isEmpty(), "Tabela deve ter índices");
//    }
//}
