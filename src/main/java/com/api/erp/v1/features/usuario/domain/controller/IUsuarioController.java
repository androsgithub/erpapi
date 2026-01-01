package com.api.erp.v1.features.usuario.domain.controller;


import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.features.usuario.application.dto.request.*;
import com.api.erp.v1.features.usuario.application.dto.response.TokenResponse;
import com.api.erp.v1.features.usuario.application.dto.response.UsuarioPermissoesResponse;
import com.api.erp.v1.features.usuario.application.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Usuários", description = "Gestão de usuários do sistema")
public interface IUsuarioController {

    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token retornado"),
            @ApiResponse(responseCode = "400", description = "Email ou senha inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    TokenResponse login(LoginRequest request);

    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    UsuarioResponse criar(CreateUsuarioRequest request);

    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário específico (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    UsuarioPermissoesResponse buscar(
            @Parameter(description = "ID do usuário") Long id);

    @Operation(summary = "Listar usuários do sistema", description = "Lista todos os usuários (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de usuários")
    List<UsuarioResponse> listar();

    @Operation(summary = "Aprovar usuário pendente", description = "Aprova um usuário que está pendente de aprovação (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário aprovado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não está pendente de aprovação"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    UsuarioPermissoesResponse aprovar(
            @Parameter(description = "ID do usuário") Long id,
            @RequestParam @Parameter(description = "ID do gestor") Long gestorId);

    @Operation(summary = "Rejeitar usuário pendente", description = "Rejeita um usuário que está pendente de aprovação (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário rejeitado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não está pendente de aprovação"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    UsuarioPermissoesResponse rejeitar(
            @Parameter(description = "ID do usuário") Long id,
            @RequestParam @Parameter(description = "ID do gestor") Long gestorId,
            @RequestParam @Parameter(description = "Motivo da rejeição") String motivo);

    @Operation(summary = "Atualizar usuário", description = "Atualiza dados de um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    UsuarioPermissoesResponse atualizar(
            @Parameter(description = "ID do usuário") Long id,
            UpdateUsuarioRequest request);

    @Operation(summary = "Inativar usuário", description = "Inativa um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    void inativar(
            @Parameter(description = "ID do usuário") Long id);

    // NOVOS ENDPOINTS PARA GERENCIAMENTO DE PERMISSÕES

    @Operation(summary = "Adicionar permissões a usuário",
            description = "Adiciona uma ou mais permissões a um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permissões adicionadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou permissão não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para realizar esta ação")
    })
    ResponseEntity<Void> adicionarPermissoes(
            @Parameter(description = "ID do usuário") Long usuarioId,
            AdicionarPermissoesRequest request);

    @Operation(summary = "Remover permissão de usuário",
            description = "Remove uma permissão específica de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permissão removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou permissão não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para realizar esta ação")
    })
    ResponseEntity<Void> removerPermissao(
            @Parameter(description = "ID do usuário") Long usuarioId,
            @Parameter(description = "ID da permissão") Long permissaoId);


    @Operation(summary = "Listar permissões de usuário",
            description = "Lista todas as permissões de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de permissões do usuário"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<List<Permissao>> listarPermissoes(
            @Parameter(description = "ID do usuário") Long usuarioId);

    // NOVOS ENDPOINTS PARA GERENCIAMENTO DE ROLES


    @Operation(summary = "Adicionar roles a usuário",
            description = "Adiciona uma ou mais roles a um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Roles adicionadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou role não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para realizar esta ação")
    })
    ResponseEntity<Void> adicionarRoles(
            @Parameter(description = "ID do usuário") Long usuarioId,
            AdicionarRolesRequest request);

    @Operation(summary = "Remover role de usuário",
            description = "Remove uma role específica de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou role não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para realizar esta ação")
    })
    ResponseEntity<Void> removerRole(
            @Parameter(description = "ID do usuário") Long usuarioId,
            @Parameter(description = "ID da role") Long roleId);

    @Operation(summary = "Listar roles de usuário",
            description = "Lista todas as roles de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles do usuário"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<List<Role>> listarRoles(
            @Parameter(description = "ID do usuário") Long usuarioId);
}
