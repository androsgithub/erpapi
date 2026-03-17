package com.api.erp.v1.docs.openapi.tenant;

import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantRequest;
import com.api.erp.v1.main.master.tenant.application.dto.request.update.UnifiedTenantConfigRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantSummaryResponse;
import com.api.erp.v1.main.master.tenant.domain.controller.ITenantController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Interface de documentação OpenAPI para Tenant.
 * Esta interface herda de ITenantController e adiciona as anotações Swagger.
 * A interface original ITenantController permanece limpa apenas com as assinaturas dos métodos.
 */
@Tag(
        name = "Tenant",
        description = "Endpoints responsáveis pela gestão e configurações da tenant (multi-tenant)"
)
public interface TenantOpenApiDocumentation extends ITenantController {

    @Override
    @Operation(
            summary = "Obter dados da tenant",
            description = "Retorna os dados cadastrais da tenant vinculada ao tenant do usuário autenticado."
    )
    @ApiResponse(responseCode = "200", description = "Dados da tenant retornados com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "400", description = "Tenant não configurada para o tenant")
    ResponseEntity<TenantSummaryResponse> obter();

    @Override
    @Operation(
            summary = "Listar todas as tenants",
            description = "Retorna a lista de todas as tenants cadastradas no sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista de tenants retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    ResponseEntity<List<TenantSummaryResponse>> listar();

    @Override
    @Operation(
            summary = "Atualizar dados principais da tenant",
            description = "Atualiza os dados cadastrais principais da tenant, como razão social, CNPJ e configurações gerais."
    )
    @ApiResponse(responseCode = "200", description = "Tenant atualizada com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "400", description = "Tenant não configurada ou dados inválidos")
    ResponseEntity<TenantSummaryResponse> atualizar(
            @PathVariable Long id,
            @RequestBody TenantRequest request
    );

    @Override
    @Operation(
            summary = "Atualizar configurações unificadas da tenant (PATCH)",
            description = """
                    Atualiza uma ou múltiplas configurações da tenant em uma única requisição.
                    
                    Substitui os 6 endpoints antigos:
                    - PUT /config/businesspartner
                    - PUT /config/user
                    - PUT /config/permission
                    - PUT /config/tenant
                    - PUT /config/address
                    - PUT /config/contact
                    
                    Envie apenas os campos que deseja atualizar (todos são opcionais).
                    Exemplo:
                    {
                      "businesspartnerValidationEnabled": true,
                      "userApprovalRequired": false,
                      "permissionCacheEnabled": true
                    }
                    """
    )
    @ApiResponse(responseCode = "200", description = "Configurações atualizadas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "400", description = "Configuração inválida")
    ResponseEntity<TenantSummaryResponse> atualizarConfigUnificada(
            @PathVariable Long id,
            @RequestBody UnifiedTenantConfigRequest request
    );
}
