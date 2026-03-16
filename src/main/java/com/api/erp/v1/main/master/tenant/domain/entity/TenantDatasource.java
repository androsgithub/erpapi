package com.api.erp.v1.main.master.tenant.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * TenantDatasource
 * <p>
 * Armazena a configuração de datasource (banco de dados) de cada tenant.
 * <p>
 * Estratégia de Multi-Tenancy:
 * - DATABASE per TENANT: cada tenant pode ter seu próprio banco de dados
 * - ROW-based discrimination: dentro do banco, o tenantId discrimina os dados
 * <p>
 * Segurança:
 * - A senha é armazenada criptografada em passwordEncrypted
 * - Use getPassword() para descriptografar e setPassword() para encriptografar
 * - O campo transient password nunca é persistido
 */
@Entity
@Table(name = "tb_tnt_datasource", 
       uniqueConstraints = {@UniqueConstraint(columnNames = "tenant_id")},
       indexes = {
           @Index(name = "idx_tenant_datasource_tenant_id", columnList = "tenant_id"),
           @Index(name = "idx_tenant_datasource_active", columnList = "active")
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

    // ===== Configuração de Conexão JDBC =====
    @Column(name = "driver_class", nullable = false, length = 200)
    private String driverClass;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    /**
     * Senha ENCRIPTADA (nunca armazenar em plaintext no banco)
     * Use getPassword() e setPassword() para lidar com criptografia
     */
    @Column(name = "password_encrypted", nullable = false, length = 500)
    private String passwordEncrypted;

    /**
     * Campo transient: senha em plaintext (apenas em memória, não persistida)
     * Preenchido quando carregado do banco via getPassword()
     * Encriptado quando salvo via setPassword()
     */
    @Transient
    private String password;

    @Column(name = "schema_name", length = 100)
    private String schemaName;

    // ===== Tipo de Banco de Dados =====
    @Column(name = "db_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private DBType dbType;

    // ===== Pool de Conexões =====
    @Column(name = "pool_min")
    @Builder.Default
    private Integer poolMin = 5;

    @Column(name = "pool_max")
    @Builder.Default
    private Integer poolMax = 20;

    @Column(name = "connection_timeout")
    @Builder.Default
    private Integer connectionTimeout = 30000; // ms

    @Column(name = "idle_timeout")
    @Builder.Default
    private Integer idleTimeout = 600000; // ms

    // ===== Status de Teste =====
    @Column(name = "test_status")
    @Enumerated(EnumType.STRING)
    private TestStatus testStatus;

    @Column(name = "tested_at")
    private LocalDateTime testedAt;

    // ===== Status =====
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Encriptografar senha antes de persistir
        if (password != null && !password.isEmpty()) {
            this.passwordEncrypted = encryptPassword(password);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Encriptografar senha se foi modificada
        if (password != null && !password.isEmpty()) {
            this.passwordEncrypted = encryptPassword(password);
        }
    }

    @PostLoad
    protected void onLoad() {
        // Descriptografar senha ao carregar do banco (apenas em memória)
        if (passwordEncrypted != null && !passwordEncrypted.isEmpty()) {
            this.password = decryptPassword(passwordEncrypted);
        }
    }

    /**
     * Obtém a senha descriptografada em memória
     * 
     * @return senha em plaintext (nunca armazenada no banco)
     */
    public String getPassword() {
        if (password == null && passwordEncrypted != null) {
            this.password = decryptPassword(passwordEncrypted);
        }
        return password;
    }

    /**
     * Define a senha em plaintext e a encriptografa antes de persistir
     * 
     * @param plainPassword senha em plaintext
     */
    public void setPassword(String plainPassword) {
        this.password = plainPassword;
        if (plainPassword != null && !plainPassword.isEmpty()) {
            this.passwordEncrypted = encryptPassword(plainPassword);
        }
    }

    /**
     * Encriptografa uma senha em plaintext
     * TODO: Implementar com algoritmo real (ex: AES, bcrypt, etc)
     * Para agora, usa Base64 como placeholder (NÃO usar em produção)
     * 
     * @param plainPassword senha em plaintext
     * @return senha encriptografada
     */
    private String encryptPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            return null;
        }
        // PLACEHOLDER: Usar algoritmo real de criptografia
        // return encryptionService.encrypt(plainPassword);
        return java.util.Base64.getEncoder().encodeToString(plainPassword.getBytes());
    }

    /**
     * Descriptografa uma senha encriptografada
     * TODO: Implementar com algoritmo real (ex: AES, bcrypt, etc)
     * Para agora, usa Base64 como placeholder (NÃO usar em produção)
     * 
     * @param encryptedPassword senha encriptografada
     * @return senha em plaintext
     */
    private String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            return null;
        }
        // PLACEHOLDER: Usar algoritmo real de criptografia
        // return encryptionService.decrypt(encryptedPassword);
        try {
            return new String(java.util.Base64.getDecoder().decode(encryptedPassword));
        } catch (IllegalArgumentException e) {
            // Se não for Base64 válido, retorna como está
            return encryptedPassword;
        }
    }

    /**
     * Retorna a URL JDBC armazenada para este datasource
     */
    public String getJdbcUrl() {
        return this.url;
    }

    /**
     * Status de teste para validação de conexão
     */
    public enum TestStatus {
        PENDING,    // Awaiting test
        SUCCESS,    // Successfully tested
        FAILED      // Connection failed
    }
}

