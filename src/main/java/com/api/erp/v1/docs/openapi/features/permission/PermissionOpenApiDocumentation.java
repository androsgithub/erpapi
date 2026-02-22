package com.api.erp.v1.docs.openapi.features.permission;

import com.api.erp.v1.main.features.permission.application.dto.request.CreatePermissionRequest;
import com.api.erp.v1.main.features.permission.application.dto.response.PermissionResponse;
import com.api.erp.v1.main.features.permission.domain.controller.IPermissionController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Interface de documentação OpenAPI para Permissão.
 */
@Tag(
        name = "Permissões",
        description = "Gestão de permissões do sistema (controle de acesso e autorizações)"
)
public interface PermissionOpenApiDocumentation extends IPermissionController {

    @Override
    @Operation(
            summary = "Criar nova permissão",
            description = "Cria uma nova permissão no sistema. " +
                    "As permissões são utilizadas para controle de acesso e autorização."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissão criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou permissão já existente"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para criar permissões"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<PermissionResponse> createPermission(
            @RequestBody CreatePermissionRequest request
    );

    @Override
    @Operation(
            summary = "Listar todas as permissões",
            description = "Retorna a lista de todas as permissões cadastradas no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de permissões retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para listar permissões"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<List<PermissionResponse>> getAllPermissions();
}
