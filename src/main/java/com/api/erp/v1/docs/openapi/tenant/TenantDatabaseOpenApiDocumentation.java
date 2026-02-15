package com.api.erp.v1.docs.openapi.tenant;

import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.main.tenant.domain.controller.ITenantDatabaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * Interface de documentação OpenAPI para Tenant Database.
 * Esta interface herda de ITenantDatabaseController e adiciona as anotações Swagger.
 */
@Tag(
        name = "Tenant - Database",
        description = "Endpoints responsáveis pela gestão e configurações da empresa (multi-tenant)"
)
public interface TenantDatabaseOpenApiDocumentation extends ITenantDatabaseController {

    @Override
    @Operation(
            summary = "Configurar datasource do tenant",
            description = "Configura um datasource (banco de dados) próprio para o tenant. " +
                    "Cada tenant pode ter seu próprio banco de dados separado. " +
                    "Exemplo: HECE tem banco próprio, JAGUAR tem banco próprio + filiais utilizam o mesmo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DataSource configurado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração de datasource inválida"),
            @ApiResponse(responseCode = "500", description = "Erro ao conectar com o banco de dados")
    })
    ResponseEntity<TenantDatasourceResponse> configurarDatasource(
            TenantDatasourceRequest request
    );

    @Override
    @Operation(
            summary = "Obter configuração de datasource",
            description = "Retorna a configuração de datasource do tenant especificado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuração de datasource retornada"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "DataSource não configurado para este tenant")
    })
    ResponseEntity<TenantDatasourceResponse> obterDatasource();

    @Override
    @Operation(
            summary = "Atualizar configuração de datasource",
            description = "Atualiza a configuração de datasource de um tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DataSource atualizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração de datasource inválida"),
            @ApiResponse(responseCode = "404", description = "DataSource não encontrado")
    })
    ResponseEntity<TenantDatasourceResponse> atualizarDatasource(
            TenantDatasourceRequest request
    );

    @Override
    @Operation(
            summary = "Testar conexão de datasource",
            description = "Valida se a configuração de datasource é válida testando a conexão."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conexão com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração inválida ou falha na conexão")
    })
    ResponseEntity<?> testarConexaoDatasource(
            TenantDatasourceRequest request
    );
}
