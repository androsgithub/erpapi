package com.api.erp.v1.main.tenant.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * TenantDatasource
 * <p>
 * Entidade que armazena a configuração de datasource (banco de dados) de cada tenant.
 * <p>
 * Estratégia de Multi-Tenancy:
 * - DATABASE per TENANT: cada tenant pode ter seu próprio banco de dados
 * - ROW-based discrimination: dentro do banco, tenantId discrimina os dados (para matriz e filiais)
 * <p>
 * Exemplo:
 * - Empresa JAGUAR: banco próprio (jaguar_db) + filiais usam o mesmo banco
 * - Empresa HECE: banco próprio (hece_db)
 * - Filial de JAGUAR em SP: mesmo banco jaguar_db, mas com tenantId diferente
 */
@Entity
@Table(name = "tb_tenant_datasource", indexes = {@Index(name = "idx_tenant_id", columnList = "tenant_id"), @Index(name = "idx_is_active", columnList = "is_active")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDatasource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false, unique = true)
    private Tenant tenant;

    // ===== Configuração de Conexão =====
    @Column(name = "host", nullable = false)
    private String host;

    @Column(name = "port", nullable = false)
    private Integer port;

    @Column(name = "database_name", nullable = false)
    private String databaseName;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    // ===== Configuração JDBC =====
    @Enumerated(EnumType.STRING)
    @Column(name = "db_type", nullable = false)
    private DBType dbType;

    // ===== Status =====
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "tested_at")
    private LocalDateTime testedAt;

    @Column(name = "test_status")
    @Enumerated(EnumType.STRING)
    private TestStatus testStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Retorna a URL de conexão JDBC
     */
    public String getJdbcUrl() {
        switch (dbType) {
            case MYSQL:
                return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", host, port, databaseName);

            case MARIADB:
                return String.format("jdbc:mariadb://%s:%d/%s", host, port, databaseName);

            case POSTGRESQL:
                return String.format("jdbc:postgresql://%s:%d/%s", host, port, databaseName);

            case ORACLE:
                // Oracle Thin: jdbc:oracle:thin:@host:port:SID
                // Oracle Service: jdbc:oracle:thin:@host:port/SERVICE
                return String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, databaseName);

            case SQL_SERVER:
                // SQL Server: jdbc:sqlserver://host:port;databaseName=database
                return String.format("jdbc:sqlserver://%s:%d;databaseName=%s", host, port, databaseName);

            case H2:
                // H2 em modo servidor: jdbc:h2:tcp://host:port/database
                // H2 em arquivo: jdbc:h2:file:./data/database
                // H2 em memória: jdbc:h2:mem:database
                return String.format("jdbc:h2:tcp://%s:%d/%s", host, port, databaseName);

            case DB2:
                return String.format("jdbc:db2://%s:%d/%s", host, port, databaseName);

            default:
                throw new IllegalArgumentException("Unsupported database type: " + dbType.getNome());
        }
    }

    public enum TestStatus {
        PENDING,    // Aguardando teste
        SUCCESS,    // Testado com sucesso
        FAILED      // Falha na conexão
    }
}
