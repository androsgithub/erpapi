package com.api.erp.v1.docs.openapi.features.permission;

import com.api.erp.v1.main.features.permission.application.dto.request.AssociarPermissionRequest;
import com.api.erp.v1.main.features.permission.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.main.features.permission.application.dto.response.RoleResponse;
import com.api.erp.v1.main.features.permission.domain.controller.IRoleController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Interface de documentação OpenAPI para Role.
 */
@Tag(
        name = "Roles",
        description = "Gestão de roles (papéis) e associação de permissões no sistema"
)
public interface RoleOpenApiDocumentation extends IRoleController {

    @Override
    @Operation(
            summary = "Criar nova role",
            description = "Cria uma nova role no sistema. " +
                    "As roles agrupam permissões e facilitam o controle de acesso."
    )
    @ApiResponse(responseCode = "201", description = "Role criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou role já existente")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Sem permissão para criar roles")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    ResponseEntity<RoleResponse> createRole(
            @RequestBody CreateRoleRequest request
    );

    @Override
    @Operation(
            summary = "Listar todas as roles",
            description = "Retorna a lista de todas as roles cadastradas no sistema"
    )
    @ApiResponse(responseCode = "200", description = "Lista de roles retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Sem permissão para listar roles")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    ResponseEntity<List<RoleResponse>> getAllRoles();

    @Override
    @Operation(
            summary = "Associar permissão a role",
            description = "Associa uma ou mais permissões a uma role existente"
    )
    @ApiResponse(responseCode = "204", description = "Permissão(ões) associada(s) com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Role ou permissão não encontrada")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Sem permissão para associar permissões")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    ResponseEntity<Void> associarPermission(
            @RequestBody AssociarPermissionRequest request
    );
}
