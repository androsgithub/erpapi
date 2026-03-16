//package com.api.erp.v1.migration;
//
//import org.flywaydb.core.Flyway;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Classe base para testes de migração de banco de dados.
// *
// * CARACTERÍSTICAS:
// * - Usa H2 em memória para testes rápidos e isolados
// * - Executa Flyway automaticamente antes de cada teste
// * - Oferece utilitários para validar estrutura de BD
// * - Transação automática para isolamento entre testes
// * - Limpeza automática após cada teste
// *
// * BOAS PRÁTICAS:
// * - Cada teste é isolado (rola back automaticamente)
// * - Flyway executa antes de cada teste (reseta estado)
// * - Validações são feitas via JdbcTemplate (simples e direto)
// * - Suporta H2 em MODE=MYSQL para compatibilidade
// *
// * @author Your Team
// * @version 1.0
// */
//@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.yml")
//@Transactional // Cada teste rola back automaticamente
//@DisplayName("Abstract Migration Test Base")
//public abstract class AbstractMigrationTest {
//
//    @Autowired
//    protected JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    protected DataSource dataSource;
//
//    @Autowired(required = false)
//    protected Flyway flyway;
//
//    /**
//     * Executa antes de cada teste.
//     * - Reseta Flyway (limpa e aplica todas as migrações)
//     * - Garante estado conhecido do BD
//     */
//    @BeforeEach
//    void setupMigrations() {
//        if (flyway != null) {
//            // Clean e Migrate para resetar o estado
//            flyway.clean();
//            flyway.migrate();
//        }
//    }
//
//    // ============= UTILITÁRIOS DE VALIDAÇÃO =============
//
//    /**
//     * Verifica se uma tabela existe no banco de dados.
//     *
//     * @param tableName name da tabela
//     * @return true se existe, false caso contrário
//     */
//    protected boolean tableExists(String tableName) {
//        try (Connection conn = dataSource.getConnection()) {
//            DatabaseMetaData metaData = conn.getMetaData();
//            try (ResultSet tables = metaData.getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
//                return tables.next();
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Erro ao verificar existência da tabela: " + tableName, e);
//        }
//    }
//
//    /**
//     * Retorna informações de todas as colunas de uma tabela.
//     *
//     * ColumnInfo contém:
//     * - name: name da coluna
//     * - type: tipo (VARCHAR, BIGINT, etc)
//     * - nullable: se aceita NULL
//     * - isAutoIncrement: se é auto increment
//     *
//     * @param tableName name da tabela
//     * @return lista de informações sobre colunas
//     */
//    protected List<ColumnInfo> getTableColumns(String tableName) {
//        List<ColumnInfo> columns = new ArrayList<>();
//
//        try (Connection conn = dataSource.getConnection()) {
//            DatabaseMetaData metaData = conn.getMetaData();
//
//            try (ResultSet colMetaData = metaData.getColumns(null, null, tableName.toUpperCase(), null)) {
//                while (colMetaData.next()) {
//                    ColumnInfo column = ColumnInfo.builder()
//                            .name(colMetaData.getString("COLUMN_NAME"))
//                            .type(colMetaData.getString("TYPE_NAME"))
//                            .size(colMetaData.getInt("COLUMN_SIZE"))
//                            .nullable(colMetaData.getInt("NULLABLE") == DatabaseMetaData.columnNullable)
//                            .isAutoIncrement("YES".equals(colMetaData.getString("IS_AUTOINCREMENT")))
//                            .defaultValue(colMetaData.getString("COLUMN_DEF"))
//                            .build();
//
//                    columns.add(column);
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Erro ao obter colunas da tabela: " + tableName, e);
//        }
//
//        return columns;
//    }
//
//    /**
//     * Verifica se uma coluna existe em uma tabela.
//     *
//     * @param tableName name da tabela
//     * @param columnName name da coluna
//     * @return true se existe, false caso contrário
//     */
//    protected boolean columnExists(String tableName, String columnName) {
//        return getTableColumns(tableName).stream()
//                .anyMatch(col -> col.getName().equalsIgnoreCase(columnName));
//    }
//
//    /**
//     * Obtém informação sobre uma coluna específica.
//     *
//     * @param tableName name da tabela
//     * @param columnName name da coluna
//     * @return ColumnInfo da coluna, ou null se não existe
//     */
//    protected ColumnInfo getColumnInfo(String tableName, String columnName) {
//        return getTableColumns(tableName).stream()
//                .filter(col -> col.getName().equalsIgnoreCase(columnName))
//                .findFirst()
//                .orElse(null);
//    }
//
//    /**
//     * Retorna todas as constraints UNIQUE de uma tabela.
//     *
//     * @param tableName name da tabela
//     * @return mapa com name da constraint e colunas envolvidas
//     */
//    protected Map<String, List<String>> getUniqueConstraints(String tableName) {
//        Map<String, List<String>> constraints = new HashMap<>();
//
//        try (Connection conn = dataSource.getConnection()) {
//            DatabaseMetaData metaData = conn.getMetaData();
//
//            try (ResultSet indexMetaData = metaData.getIndexInfo(null, null, tableName.toUpperCase(), true, false)) {
//                while (indexMetaData.next()) {
//                    String indexName = indexMetaData.getString("INDEX_NAME");
//                    String columnName = indexMetaData.getString("COLUMN_NAME");
//
//                    constraints.computeIfAbsent(indexName, k -> new ArrayList<>()).add(columnName);
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Erro ao obter constraints UNIQUE da tabela: " + tableName, e);
//        }
//
//        return constraints;
//    }
//
//    /**
//     * Obtém informações sobre chaves primárias de uma tabela.
//     *
//     * @param tableName name da tabela
//     * @return lista com nomes das colunas da PK
//     */
//    protected List<String> getPrimaryKeyColumns(String tableName) {
//        List<String> pkColumns = new ArrayList<>();
//
//        try (Connection conn = dataSource.getConnection()) {
//            DatabaseMetaData metaData = conn.getMetaData();
//
//            try (ResultSet pkMetaData = metaData.getPrimaryKeys(null, null, tableName.toUpperCase())) {
//                while (pkMetaData.next()) {
//                    pkColumns.add(pkMetaData.getString("COLUMN_NAME"));
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Erro ao obter PK da tabela: " + tableName, e);
//        }
//
//        return pkColumns;
//    }
//
//    /**
//     * Obtém informações sobre chaves estrangeiras que apontam PARA esta tabela.
//     *
//     * @param tableName name da tabela
//     * @return mapa com informações das FKs
//     */
//    protected List<ForeignKeyInfo> getForeignKeys(String tableName) {
//        List<ForeignKeyInfo> foreignKeys = new ArrayList<>();
//
//        try (Connection conn = dataSource.getConnection()) {
//            DatabaseMetaData metaData = conn.getMetaData();
//
//            try (ResultSet fkMetaData = metaData.getImportedKeys(null, null, tableName.toUpperCase())) {
//                while (fkMetaData.next()) {
//                    ForeignKeyInfo fk = ForeignKeyInfo.builder()
//                            .fkName(fkMetaData.getString("FK_NAME"))
//                            .fkTableName(fkMetaData.getString("FKTABLE_NAME"))
//                            .fkColumnName(fkMetaData.getString("FKCOLUMN_NAME"))
//                            .pkTableName(fkMetaData.getString("PKTABLE_NAME"))
//                            .pkColumnName(fkMetaData.getString("PKCOLUMN_NAME"))
//                            .deleteRule(fkMetaData.getInt("DELETE_RULE"))
//                            .build();
//
//                    foreignKeys.add(fk);
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Erro ao obter FKs da tabela: " + tableName, e);
//        }
//
//        return foreignKeys;
//    }
//
//    /**
//     * Obtém informações sobre índices de uma tabela.
//     *
//     * @param tableName name da tabela
//     * @return mapa com índices (chave = name do índice, valor = colunas)
//     * @throws SQLException Se erro ao acessar metadados
//     */
//    protected Map<String, IndexInfo> getTableIndexes(String tableName) throws SQLException {
//        Map<String, IndexInfo> indexes = new HashMap<>();
//
//        try (Connection conn = dataSource.getConnection()) {
//            DatabaseMetaData metaData = conn.getMetaData();
//
//            try (ResultSet indexMetaData = metaData.getIndexInfo(null, null, tableName.toUpperCase(), false, false)) {
//                while (indexMetaData.next()) {
//                    String indexName = indexMetaData.getString("INDEX_NAME");
//
//                    if (indexName != null) {
//                        indexes.computeIfAbsent(indexName, k -> {
//                            try {
//                                return IndexInfo.builder()
//                                        .name(indexName)
//                                        .unique(indexMetaData.getBoolean("NON_UNIQUE"))
//                                        .build();
//                            } catch (SQLException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }).getColumns().add(indexMetaData.getString("COLUMN_NAME"));
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Erro ao obter índices da tabela: " + tableName, e);
//        }
//
//        return indexes;
//    }
//
//    /**
//     * Executa uma query COUNT na tabela e retorna o número de registros.
//     *
//     * @param tableName name da tabela
//     * @return quantidade de registros
//     */
//    protected long countTableRecords(String tableName) {
//        try {
//            Integer count = jdbcTemplate.queryForObject(
//                    "SELECT COUNT(*) FROM " + tableName,
//                    Integer.class
//            );
//            return count != null ? count : 0L;
//        } catch (Exception e) {
//            return 0L;
//        }
//    }
//
//    /**
//     * Verifica se existe pelo menos um registro na tabela.
//     *
//     * @param tableName name da tabela
//     * @return true se tem registros, false caso contrário
//     */
//    protected boolean hasRecords(String tableName) {
//        return countTableRecords(tableName) > 0;
//    }
//
//    /**
//     * Executa uma query SELECT e retorna um Map com os resultados.
//     * Útil para validar dados de seed.
//     *
//     * @param sql query SQL
//     * @return lista com resultados como Maps
//     */
//    protected List<Map<String, Object>> executeQuery(String sql) {
//        return jdbcTemplate.queryForList(sql);
//    }
//
//    /**
//     * Executa uma query UPDATE/INSERT/DELETE.
//     *
//     * @param sql query SQL
//     * @return número de linhas afetadas
//     */
//    protected int executeUpdate(String sql) {
//        return jdbcTemplate.update(sql);
//    }
//
//    /**
//     * Executa uma query UPDATE/INSERT/DELETE com parâmetros.
//     *
//     * @param sql query SQL com placeholders ?
//     * @param params valores dos parâmetros
//     * @return número de linhas afetadas
//     */
//    protected int executeUpdate(String sql, Object... params) {
//        return jdbcTemplate.update(sql, params);
//    }
//
//    // ============= INNER CLASSES =============
//
//    @lombok.Data
//    @lombok.Builder
//    public static class ColumnInfo {
//        private String name;
//        private String type;
//        private int size;
//        private boolean nullable;
//        private boolean isAutoIncrement;
//        private String defaultValue;
//    }
//
//    @lombok.Data
//    @lombok.Builder
//    public static class ForeignKeyInfo {
//        private String fkName;
//        private String fkTableName;
//        private String fkColumnName;
//        private String pkTableName;
//        private String pkColumnName;
//        private int deleteRule;
//    }
//
//    @lombok.Data
//    @lombok.Builder
//    public static class IndexInfo {
//        private String name;
//        private boolean unique;
//        private List<String> columns = new ArrayList<>();
//    }
//}
