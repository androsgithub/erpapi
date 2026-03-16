package com.api.erp.v1.main.master.tenant.domain.entity;

public enum DBType {
    MYSQL("mysql", "org.hibernate.dialect.MySQLDialect", "com.mysql.cj.jdbc.Driver"),
    POSTGRESQL("postgresql", "org.hibernate.dialect.PostgreSQLDialect", "org.postgresql.Driver"),
    ORACLE("oracle", "org.hibernate.dialect.OracleDialect", "oracle.jdbc.OracleDriver"),
    SQL_SERVER("sqlserver", "org.hibernate.dialect.SQLServerDialect", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    H2("h2", "org.hibernate.dialect.H2Dialect", "org.h2.Driver"),
    MARIADB("mariadb", "org.hibernate.dialect.MariaDBDialect", "org.mariadb.jdbc.Driver"),
    DB2("db2", "org.hibernate.dialect.DB2Dialect", "com.ibm.db2.jcc.DB2Driver"),
    SQLITE("sqlite", "org.hibernate.community.dialect.SQLiteDialect", "org.sqlite.JDBC"),
    COCKROACHDB("cockroachdb", "org.hibernate.dialect.CockroachDialect", "org.postgresql.Driver"),
    SYBASE("sybase", "org.hibernate.dialect.SybaseDialect", "com.sybase.jdbc4.jdbc.SybDriver"),
    INFORMIX("informix", "org.hibernate.community.dialect.InformixDialect", "com.informix.jdbc.IfxDriver"),
    FIREBIRD("firebird", "org.hibernate.community.dialect.FirebirdDialect", "org.firebirdsql.jdbc.FBDriver"),
    DERBY("derby", "org.hibernate.community.dialect.DerbyDialect", "org.apache.derby.jdbc.EmbeddedDriver"),
    HSQLDB("hsqldb", "org.hibernate.dialect.HSQLDialect", "org.hsqldb.jdbc.JDBCDriver"),
    VERTICA("vertica", "org.hibernate.dialect.PostgreSQLDialect", "com.vertica.jdbc.Driver"),
    CLICKHOUSE("clickhouse", "org.hibernate.dialect.PostgreSQLDialect", "com.clickhouse.jdbc.ClickHouseDriver");

    private final String nome;
    private final String dialeto;
    private final String driver;

    DBType(String nome, String dialeto, String driver) {
        this.nome = nome;
        this.dialeto = dialeto;
        this.driver = driver;
    }

    public String getNome() {
        return nome;
    }

    public String getDialeto() {
        return dialeto;
    }

    public String getDriver() {
        return driver;
    }

    /**
     * Resolve DBType pelo name do banco (case-insensitive com trim)
     */
    public static DBType fromNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty");
        }
        
        for (DBType banco : values()) {
            if (banco.nome.equalsIgnoreCase(nome.trim())) {
                return banco;
            }
        }
        throw new IllegalArgumentException("Unsupported database: " + nome);
    }

    /**
     * Resolve DBType pelo dialeto Hibernate
     */
    public static DBType fromDialeto(String dialeto) {
        if (dialeto == null || dialeto.trim().isEmpty()) {
            throw new IllegalArgumentException("Dialect cannot be null or empty");
        }
        
        String dialectoLower = dialeto.toLowerCase();
        for (DBType banco : values()) {
            if (banco.dialeto.equalsIgnoreCase(dialeto) || dialectoLower.contains(banco.nome)) {
                return banco;
            }
        }
        throw new IllegalArgumentException("Unrecognized dialect: " + dialeto);
    }

    /**
     * Resolve DBType pelo driver JDBC (case-insensitive com trim)
     */
    public static DBType fromDriver(String driver) {
        if (driver == null || driver.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver cannot be null or empty");
        }
        
        for (DBType banco : values()) {
            if (banco.driver.equalsIgnoreCase(driver.trim())) {
                return banco;
            }
        }
        throw new IllegalArgumentException("Unrecognized driver: " + driver);
    }

    /**
     * Verifica se um driver é suportado
     */
    public static boolean isSupported(String driver) {
        if (driver == null || driver.trim().isEmpty()) {
            return false;
        }
        
        try {
            fromDriver(driver);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.nome + " (Dialeto: " + this.dialeto + ")";
    }
}
