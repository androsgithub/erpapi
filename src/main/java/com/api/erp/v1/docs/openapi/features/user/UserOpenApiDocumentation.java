package com.api.erp.v1.docs.openapi.features.user;

import com.api.erp.v1.main.features.permission.domain.entity.Permission;
import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.user.application.dto.request.*;
import com.api.erp.v1.main.features.user.application.dto.response.TokenResponse;
import com.api.erp.v1.main.features.user.application.dto.response.UserPermissionsResponse;
import com.api.erp.v1.main.features.user.application.dto.response.UserResponse;
import com.api.erp.v1.main.features.user.domain.controller.IUserController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Interface de documentação OpenAPI para User.
 */
@Tag(name = "Usuários", description = "Gestão de usuários do sistema")
public interface UserOpenApiDocumentation extends IUserController {

    @Override
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token retornado"),
            @ApiResponse(responseCode = "400", description = "Email ou senha inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    TokenResponse login(LoginRequest request);

    @Override
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    UserResponse criar(CreateUserRequest request);

    @Override
    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário específico (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    UserPermissionsResponse buscar(
            @Parameter(description = "ID do usuário") Long id);

    @Override
    @Operation(summary = "Listar usuários do sistema", description = "Lista todos os usuários (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de usuários")
    List<UserResponse> listar();

    @Override
    @Operation(summary = "Aprovar usuário pendente", description = "Aprova um usuário que está pendente de aprovação (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário aprovado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não está pendente de aprovação"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    UserPermissionsResponse aprovar(
            @Parameter(description = "ID do usuário") Long id,
            @RequestParam @Parameter(description = "ID do gestor") Long gestorId);

    @Override
    @Operation(summary = "Rejeitar usuário pendente", description = "Rejeita um usuário que está pendente de aprovação (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário rejeitado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não está pendente de aprovação"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    UserPermissionsResponse rejeitar(
            @Parameter(description = "ID do usuário") Long id,
            @RequestParam @Parameter(description = "ID do gestor") Long gestorId,
            @RequestParam @Parameter(description = "Motivo da rejeição") String motivo);

    @Override
    @Operation(summary = "Atualizar usuário", description = "Atualiza dados de um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    UserPermissionsResponse atualizar(
            @Parameter(description = "ID do usuário") Long id,
            UpdateUserRequest request);

    @Override
    @Operation(summary = "Inativar usuário", description = "Inativa um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    void inativar(
            @Parameter(description = "ID do usuário") Long id);

    @Override
    @Operation(summary = "Adicionar permissões a usuário",
            description = "Adiciona uma ou mais permissões a um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permissões adicionadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou permissão não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para realizar esta ação")
    })
    ResponseEntity<Void> adicionarPermissions(
            @Parameter(description = "ID do usuário") Long userId,
            AdicionarPermissionsRequest request);

    @Override
    @Operation(summary = "Remover permissão de usuário",
            description = "Remove uma permissão específica de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permissão removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou permissão não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para realizar esta ação")
    })
    ResponseEntity<Void> removerPermission(
            @Parameter(description = "ID do usuário") Long userId,
            @Parameter(description = "ID da permissão") Long permissionId);

    @Override
    @Operation(summary = "Listar permissões de usuário",
            description = "Lista todas as permissões de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de permissões do usuário"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<List<Permission>> listarPermissions(
            @Parameter(description = "ID do usuário") Long userId);

    @Override
    @Operation(summary = "Adicionar roles a usuário",
            description = "Adiciona uma ou mais roles a um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Roles adicionadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou role não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para realizar esta ação")
    })
    ResponseEntity<Void> adicionarRoles(
            @Parameter(description = "ID do usuário") Long userId,
            AdicionarRolesRequest request);

    @Override
    @Operation(summary = "Remover role de usuário",
            description = "Remove uma role específica de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou role não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para realizar esta ação")
    })
    ResponseEntity<Void> removerRole(
            @Parameter(description = "ID do usuário") Long userId,
            @Parameter(description = "ID da role") Long roleId);

    @Override
    @Operation(summary = "Listar roles de usuário",
            description = "Lista todas as roles de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles do usuário"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<List<Role>> listarRoles(
            @Parameter(description = "ID do usuário") Long userId);

    @Override
    @Operation(summary = "Informaçoes do user atual",
            description = "Pega informações do user atual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do user"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    UserPermissionsResponse obterDadosAtualizados();
}
