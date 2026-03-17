package com.api.erp.v1.main.master.tenant.application.dto.request.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * UpdateTenantDatasourceWithPasswordRequest
 * 
 * DTO para atualizar configuração de datasource com validação de senha.
 * 
 * Fluxo de segurança:
 * 1. Cliente envia currentPassword (senha armazenada no banco)
 * 2. Servidor descriptografa a senha armazenada
 * 3. Compara currentPassword com a senha descriptografada
 * 4. Se forem iguais, atualiza para newPassword
 * 5. Se forem diferentes, rejeita a atualização com erro
 * 
 * Propriedades:
 * - currentPassword: Senha atual (para verificação de segurança)
 * - newPassword: Nova senha (opcional, se vazio mantém a atual)
 * - host: Host do banco de dados
 * - port: Porta do banco de dados
 * - databaseName: Nome do banco de dados
 * - username: Usuário de autenticação
 * - dbType: Tipo de banco (MySQL, PostgreSQL, Oracle, etc)
 * - runMigrations: Se true, enfileira migrações após atualizar (padrão: false)
 */
@Schema(
        title = "Update Tenant Datasource with Password Verification",
        description = "Requires verifying current password before updating datasource configuration"
)
public record UpdateTenantDatasourceWithPasswordRequest(
        
        @NotBlank(message = "Current password is required for verification")
        @Schema(
                description = "Current password for verification (must match stored password)",
                example = "current_secure_password_123"
        )
        String currentPassword,

        @Schema(
                description = "New password (optional, leave empty to keep current)",
                example = "new_secure_password_456",
                nullable = true
        )
        String newPassword,

        @NotBlank(message = "Host is required")
        @Schema(
                description = "Database host address",
                example = "db.example.com"
        )
        String host,

        @NotNull(message = "Port is required")
        @Schema(
                description = "Database port",
                example = "5432"
        )
        Integer port,

        @NotBlank(message = "Database name is required")
        @Schema(
                description = "Database name / schema",
                example = "tenant_db_prod"
        )
        String databaseName,

        @NotBlank(message = "Username is required")
        @Schema(
                description = "Database user for authentication",
                example = "db_user"
        )
        String username,

        @NotBlank(message = "Database type is required")
        @Schema(
                description = "Type of database (MYSQL, POSTGRESQL, ORACLE, etc)",
                example = "POSTGRESQL"
        )
        String dbType,

        @Schema(
                description = "Whether to enqueue database migrations after update",
                example = "false",
                nullable = true
        )
        Boolean runMigrations
) {
    
    /**
     * Construtor alternativo sem runMigrations (padrão = false)
     */
    public UpdateTenantDatasourceWithPasswordRequest(
            String currentPassword,
            String newPassword,
            String host,
            Integer port,
            String databaseName,
            String username,
            String dbType) {
        this(currentPassword, newPassword, host, port, databaseName, username, dbType, false);
    }

    /**
     * Construtor sem newPassword (usa currentPassword como a senha final)
     */
    public UpdateTenantDatasourceWithPasswordRequest(
            String currentPassword,
            String host,
            Integer port,
            String databaseName,
            String username,
            String dbType,
            Boolean runMigrations) {
        this(currentPassword, null, host, port, databaseName, username, dbType, runMigrations);
    }

    /**
     * Retorna a senha a ser usada na atualização.
     * Se newPassword foi informado e não vazio, usa newPassword.
     * Caso contrário usa currentPassword (mantém a senha atual).
     */
    public String getPasswordToUpdate() {
        if (newPassword != null && !newPassword.isEmpty()) {
            return newPassword;
        }
        return currentPassword;
    }
}
