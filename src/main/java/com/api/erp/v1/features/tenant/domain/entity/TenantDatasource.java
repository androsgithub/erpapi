package com.api.erp.v1.features.tenant.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * TenantDatasource
 * 
 * Entidade que armazena a configuração de datasource (banco de dados) de cada tenant.
 * 
 * Estratégia de Multi-Tenancy:
 * - DATABASE per TENANT: cada tenant pode ter seu próprio banco de dados
 * - ROW-based discrimination: dentro do banco, tenantId discrimina os dados (para matriz e filiais)
 * 
 * Exemplo:
 * - Empresa JAGUAR: banco próprio (jaguar_db) + filiais usam o mesmo banco
 * - Empresa HECE: banco próprio (hece_db)
 * - Filial de JAGUAR em SP: mesmo banco jaguar_db, mas com tenantId diferente
 */
@Entity
@Table(name = "tenant_datasource", indexes = {
        @Index(name = "idx_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_is_active", columnList = "is_active")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDatasource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
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
    @Column(name = "driver_class_name", nullable = false)
    private String driverClassName;

    @Column(name = "dialect", nullable = false)
    private String dialect;

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
        // MySQL: jdbc:mysql://host:port/database
        if (driverClassName.contains("mysql")) {
            return String.format("jdbc:mysql://%s:%d/%s", host, port, databaseName);
        }
        // PostgreSQL: jdbc:postgresql://host:port/database
        if (driverClassName.contains("postgresql")) {
            return String.format("jdbc:postgresql://%s:%d/%s", host, port, databaseName);
        }
        throw new IllegalArgumentException("Driver não suportado: " + driverClassName);
    }

    public enum TestStatus {
        PENDING,    // Aguardando teste
        SUCCESS,    // Testado com sucesso
        FAILED      // Falha na conexão
    }
}
