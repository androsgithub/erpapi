package com.api.erp.v1.main.tenant.application.dto;

import com.api.erp.v1.main.shared.domain.enums.TenantType;
import com.dros.taxengine.domain.ContribuinteICMS;
import com.dros.taxengine.domain.RegimeTributario;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para criação/registro de novo tenant (empresa)
 */
public record CriarTenantRequest(

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
        ContribuinteICMS contribuinteICMS,
        RegimeTributario regimeTributario
) {
}
