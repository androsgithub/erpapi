package com.api.erp.v1.docs.openapi.features.contato;

import com.api.erp.v1.main.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.main.features.contato.application.dto.request.AssociarContatosRequest;
import com.api.erp.v1.main.features.contato.application.dto.request.RemoverContatoRequest;
import com.api.erp.v1.main.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.main.features.contato.application.dto.response.UsuarioContatosResponse;
import com.api.erp.v1.main.features.contato.domain.controller.IContatosUsuarioController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Interface de documentação OpenAPI para Contatos de Usuario.
 */
@Tag(name = "Contatos do Usuário", description = "Gestão de Contatos - Operações de Usuário")
public interface ContatosUsuarioOpenApiDocumentation extends IContatosUsuarioController {

    @Override
    @Operation(summary = "Associar contatos a usuário",
            description = "Associa múltiplos contatos a um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contatos associados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<UsuarioContatosResponse> associarContatos(
            @RequestBody AssociarContatosRequest request);

    @Override
    @Operation(summary = "Adicionar contato a usuário",
            description = "Adiciona um novo contato a um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contato adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContatoResponse> adicionarContato(
            @Parameter(description = "ID do usuário") Long usuarioId,
            @RequestBody CreateContatoRequest request);

    @Override
    @Operation(summary = "Buscar contatos de usuário",
            description = "Busca todos os contatos associados a um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contatos encontrados"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado ou sem contatos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<UsuarioContatosResponse> buscarContatosUsuario(
            @Parameter(description = "ID do usuário") Long usuarioId);

    @Override
    @Operation(summary = "Remover contato de usuário",
            description = "Remove um contato associado a um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contato removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Void> removerContato(
            @RequestBody RemoverContatoRequest request);

    @Override
    @Operation(summary = "Marcar contato como principal",
            description = "Marca um contato como principal (apenas um contato por usuário) (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato marcado como principal"),
            @ApiResponse(responseCode = "404", description = "Usuário ou contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContatoResponse> marcarComoPrincipal(
            @Parameter(description = "ID do usuário") Long usuarioId,
            @Parameter(description = "ID do contato") Long contatoId);

    @Override
    @Operation(summary = "Desativar contato do usuário",
            description = "Desativa um contato de um usuário (soft delete) (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContatoResponse> desativarContato(
            @Parameter(description = "ID do usuário") Long usuarioId,
            @Parameter(description = "ID do contato") Long contatoId);

    @Override
    @Operation(summary = "Ativar contato do usuário",
            description = "Ativa um contato desativado de um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContatoResponse> ativarContato(
            @Parameter(description = "ID do usuário") Long usuarioId,
            @Parameter(description = "ID do contato") Long contatoId);
}
