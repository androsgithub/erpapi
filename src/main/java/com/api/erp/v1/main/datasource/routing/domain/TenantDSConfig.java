package com.api.erp.v1.main.datasource.routing.domain;

import com.api.erp.v1.main.master.tenant.domain.entity.DBType;

/**
 * DOMAIN - Value Object para Configuration de DataSource de Tenant
 *
 * Encapsula as informações necessárias para criar uma conexão de banco de dados
 * para um tenant específico. Objetos imutáveis que representam valores do domínio.
 *
 * Suporta múltiplos tipos de banco de dados através do enum DBType.
 *
 * @author ERP System
 * @version 1.0
 */
public class TenantDSConfig {

    private final Long tenantId;
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final DBType dbType;

    /**
     * Construtor com DBType enum
     */
    public TenantDSConfig(
            Long tenantId,
            String dbUrl,
            String dbUsername,
            String dbPassword,
            DBType dbType) {

        this.tenantId = validateNotBlank(tenantId, "tenantId");
        this.dbUrl = validateNotBlank(dbUrl, "dbUrl");
        this.dbUsername = validateNotBlank(dbUsername, "dbUsername");
        this.dbPassword = dbPassword;

        if (dbType == null) {
            throw new IllegalArgumentException("DBType cannot be null");
        }

        this.dbType = dbType;
    }

    /**
     * Construtor com DBType como string (útil para dados do banco)
     */
    public TenantDSConfig(
            String tenantId,
            String dbUrl,
            String dbUsername,
            String dbPassword,
            String dbTypeString) {

        this.tenantId = Long.valueOf(validateNotBlank(tenantId, "tenantId"));
        this.dbUrl = validateNotBlank(dbUrl, "dbUrl");
        this.dbUsername = validateNotBlank(dbUsername, "dbUsername");
        this.dbPassword = dbPassword;

        if (dbTypeString == null || dbTypeString.trim().isEmpty()) {
            throw new IllegalArgumentException("dbTypeString cannot be null or empty");
        }

        try {
            this.dbType = DBType.fromNome(dbTypeString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Unsupported database type: " + dbTypeString, e);
        }
    }

    /**
     * Construtor com default (PostgreSQL)
     */
    public TenantDSConfig(
            Long tenantId,
            String dbUrl,
            String dbUsername,
            String dbPassword) {
        this(tenantId, dbUrl, dbUsername, dbPassword, DBType.POSTGRESQL);
    }

    private static String validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " não pode ser nulo ou vazio");
        }
        return value;
    }

    private static Long validateNotBlank(Long value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " não pode ser nulo ou vazio");
        }
        return value;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public DBType getDbType() {
        return dbType;
    }

    public String getDbDriverClassName() {
        return dbType.getDriver();
    }

    public String getDbDialect() {
        return dbType.getDialect();
    }

    @Override
    public String toString() {
        return "TenantDataSourceConfig{" +
                "tenantId='" + tenantId + '\'' +
                ", dbUrl='" + dbUrl + '\'' +
                ", dbType=" + dbType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TenantDSConfig that = (TenantDSConfig) o;

        if (!tenantId.equals(that.tenantId)) return false;
        if (!dbUrl.equals(that.dbUrl)) return false;
        if (!dbUsername.equals(that.dbUsername)) return false;
        if (!dbPassword.equals(that.dbPassword)) return false;
        return dbType == that.dbType;
    }

    @Override
    public int hashCode() {
        int result = tenantId.hashCode();
        result = 31 * result + dbUrl.hashCode();
        result = 31 * result + dbUsername.hashCode();
        result = 31 * result + dbPassword.hashCode();
        result = 31 * result + dbType.hashCode();
        return result;
    }
}
