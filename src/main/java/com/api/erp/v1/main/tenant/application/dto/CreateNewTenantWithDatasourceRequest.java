package com.api.erp.v1.main.tenant.application.dto;

import com.api.erp.v1.main.shared.domain.enums.TenantType;
import com.api.erp.v1.main.tenant.domain.entity.DBType;
import com.dros.taxengine.domain.ContribuinteICMS;
import com.dros.taxengine.domain.RegimeTributario;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para criar novo tenant completo com datasource e enfileirar migrações + seeders
 * 
 * Fluxo:
 * 1. Cria novo tenant no master database
 * 2. Configura datasource do tenant
 * 3. Enfileira migração (Flyway)
 * 4. Enfileira MainSeed (permissões, usuário admin, etc) após migrações
 * 
 * @author ERP System
 * @version 1.0
 */
public record CreateNewTenantWithDatasourceRequest(
        
        // ═══════════════════════════════════════════════════════════════════
        // TENANT INFORMATION
        // ═══════════════════════════════════════════════════════════════════
        
        @Schema(
                description = "Nome da empresa/tenant",
                example = "HECE - Distribuidora",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String nome,

        @Schema(
                description = "CNPJ da empresa",
                example = "11222333000181",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String cnpj,

        @Schema(
                description = "Razão social da empresa",
                example = "HECE Distribuidora de Alimentos LTDA",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String razaoSocial,

        @Schema(
                description = "Email da empresa",
                example = "admin@hece.com.br",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String email,

        @Schema(
                description = "Telefone da empresa",
                example = "1133334444",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String telefone,

        @Schema(
                description = "Tipo de tenant/empresa",
                example = "HECE",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        TenantType tenantType,
        
        @Schema(
                description = "Subdomínio do tenant",
                example = "hece",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String tenantSubdomain,
        
        @Schema(
                description = "Contribuinte ICMS",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        ContribuinteICMS contribuinteICMS,
        
        @Schema(
                description = "Regime tributário",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        RegimeTributario regimeTributario,
        
        // ═══════════════════════════════════════════════════════════════════
        // DATASOURCE INFORMATION
        // ═══════════════════════════════════════════════════════════════════
        
        @Schema(
                description = "Host do banco de dados do tenant",
                example = "localhost",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String dbHost,

        @Schema(
                description = "Porta do banco de dados",
                example = "3306",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer dbPort,

        @Schema(
                description = "Nome do banco de dados",
                example = "hece_db",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String dbName,

        @Schema(
                description = "Usuário do banco de dados",
                example = "hece_user",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String dbUsername,

        @Schema(
                description = "Senha do banco de dados",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String dbPassword,

        @Schema(
                description = "Tipo de banco de dados",
                example = "MYSQL",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String dbType
) {
}
