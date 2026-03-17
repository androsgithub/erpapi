package com.api.erp.v1.docs.openapi.tenant;

import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.application.dto.response.UpdateDatasourceResponse;
import com.api.erp.v1.main.master.tenant.domain.controller.ITenantDatabaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * Interface de documentação OpenAPI para Tenant Database.
 * Esta interface herda de ITenantDatabaseController e adiciona as anotações Swagger.
 */
@Tag(
        name = "Tenant - Database",
        description = "Endpoints responsáveis pela gestão de datasources e configurações de bancos de dados (multi-tenant)"
)
public interface TenantDatabaseOpenApiDocumentation extends ITenantDatabaseController {

    @Override
    @Operation(
            summary = "Obter configuração de datasource",
            description = "Retorna a configuração de datasource do tenant especificado."
    )
    @ApiResponse(responseCode = "200", description = "Configuração de datasource retornada")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "404", description = "DataSource não configurado para este tenant")
    ResponseEntity<TenantDatasourceResponse> obterDatasource();

    @Override
    @Operation(
            summary = "Atualizar configuração de datasource",
            description = "Atualiza a configuração de datasource de um tenant. " +
                    "Opcionalmente enfileira migrações se runMigrations=true.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "runMigrations",
                            description = "Se true, enfileira migrações após atualizar. Padrão: false",
                            schema = @Schema(type = "boolean", example = "false")
                    )
            }
    )
    @ApiResponse(responseCode = "200", description = "DataSource atualizado (runMigrations=false)")
    @ApiResponse(responseCode = "202", description = "DataSource atualizado e migrações enfileiradas (runMigrations=true)")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "400", description = "Configuração inválida ou falha ao enfileirar")
    @ApiResponse(responseCode = "500", description = "Erro interno ao enfileirar migrações")
    ResponseEntity<UpdateDatasourceResponse> atualizarDatasource(
            TenantDatasourceRequest request
    );

    @Override
    @Operation(
            summary = "Validar configuração de datasource",
            description = "Testa se a configuração de datasource é válida, verificando a conectividade. " +
                    "NÃO cria ou modifica nada no banco, apenas valida."
    )
    @ApiResponse(responseCode = "200", description = "Datasource válido e conectável")
    @ApiResponse(responseCode = "400", description = "Datasource inválido ou falha na conexão")
    ResponseEntity<?> validarDatasource(
            TenantDatasourceRequest request
    );

    @Override
    @Operation(
            summary = "Configurar datasource e enfileirar migrações",
            description = "Configura novo datasource e enfileira automaticamente as migrações Flyway + seeders. " +
                    "Executa 3 fases: config → teste conexão → enfileira."
    )
    @ApiResponse(responseCode = "202", description = "Datasource configurado e migrações enfileiradas")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "400", description = "Configuração inválida ou falha na conexão")
    @ApiResponse(responseCode = "500", description = "Erro ao enfileirar migrações")
    ResponseEntity<?> configurarDatasourceEEnfileirarMigracao(
            TenantDatasourceRequest request
    );

    @Override
    @Operation(
            summary = "Enfileirar migrações manualmente",
            description = "Enfileira novo evento de migração para um datasource já existente e ativo. " +
                    "Não modifica configurações, apenas cria uma tarefa de migração."
    )
    @ApiResponse(responseCode = "202", description = "Migração enfileirada com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "400", description = "Datasource ativo não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro ao enfileirar migração")
    ResponseEntity<?> enqueueMigration();
}
