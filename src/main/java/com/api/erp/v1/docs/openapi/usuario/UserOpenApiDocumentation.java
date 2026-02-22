package com.api.erp.v1.docs.openapi.user;

import com.api.erp.v1.main.features.user.application.dto.request.*;
import com.api.erp.v1.main.features.user.application.dto.response.TokenResponse;
import com.api.erp.v1.main.features.user.application.dto.response.UserPermissionsResponse;
import com.api.erp.v1.main.features.user.application.dto.response.UserResponse;
import com.api.erp.v1.main.features.permission.domain.entity.Permission;
import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.user.domain.controller.IUserController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Interface de documentação OpenAPI para User.
 * Esta interface herda de IUserController e adiciona as anotações Swagger.
 */
@Tag(
        name = "User",
        description = "Endpoints responsáveis pela gestão de usuários e autenticação"
)
public interface UserOpenApiDocumentation extends IUserController {

    @Override
    @Operation(
            summary = "Realizar login",
            description = "Autentica um usuário e retorna um token de acesso"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    TokenResponse login(LoginRequest request);

    @Override
    @Operation(
            summary = "Criar novo usuário",
            description = "Cria um novo usuário no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Usuário já existe")
    })
    UserResponse criar(CreateUserRequest request);

    @Override
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os detalhes de um usuário específico com suas permissões"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    UserPermissionsResponse buscar(Long id);

    @Override
    @Operation(
            summary = "Listar todos os usuários",
            description = "Retorna uma lista de todos os usuários do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada")
    })
    List<UserResponse> listar();

    @Override
    @Operation(
            summary = "Aprovar usuário",
            description = "Aprova um usuário pendente de aprovação por um gestor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário aprovado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Usuário não pode ser aprovado")
    })
    UserPermissionsResponse aprovar(
            Long id,
            @RequestParam Long gestorId);

    @Override
    @Operation(
            summary = "Rejeitar usuário",
            description = "Rejeita um usuário pendente de aprovação com um motivo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário rejeitado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Usuário não pode ser rejeitado")
    })
    UserPermissionsResponse rejeitar(
            Long id,
            @RequestParam Long gestorId,
            @RequestParam String motivo);

    @Override
    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza os dados de um usuário existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    UserPermissionsResponse atualizar(
            Long id,
            UpdateUserRequest request);

    @Override
    @Operation(
            summary = "Inativar usuário",
            description = "Inativa um usuário do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    void inativar(Long id);

    @Override
    @Operation(
            summary = "Adicionar permissões ao usuário",
            description = "Associa uma ou mais permissões a um usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissões adicionadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou permissão não encontrado")
    })
    ResponseEntity<Void> adicionarPermissions(
            Long userId,
            AdicionarPermissionsRequest request);

    @Override
    @Operation(
            summary = "Remover permissão do usuário",
            description = "Remove uma permissão específica de um usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissão removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou permissão não encontrado")
    })
    ResponseEntity<Void> removerPermission(
            Long userId,
            Long permissionId);

    @Override
    @Operation(
            summary = "Listar permissões do usuário",
            description = "Retorna todas as permissões associadas a um usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissões listadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<List<Permission>> listarPermissions(
            Long userId);

    @Override
    @Operation(
            summary = "Adicionar roles ao usuário",
            description = "Associa uma ou mais roles a um usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles adicionadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou role não encontrado")
    })
    ResponseEntity<Void> adicionarRoles(
            Long userId,
            AdicionarRolesRequest request);

    @Override
    @Operation(
            summary = "Remover role do usuário",
            description = "Remove uma role específica de um usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou role não encontrado")
    })
    ResponseEntity<Void> removerRole(
            Long userId,
            Long roleId);

    @Override
    @Operation(
            summary = "Listar roles do usuário",
            description = "Retorna todas as roles associadas a um usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles listadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<List<Role>> listarRoles(
            Long userId);

    @Override
    @Operation(
            summary = "Obter dados atualizados do usuário",
            description = "Retorna os dados atualizados do usuário autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados obtidos com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    UserPermissionsResponse obterDadosAtualizados();
}
