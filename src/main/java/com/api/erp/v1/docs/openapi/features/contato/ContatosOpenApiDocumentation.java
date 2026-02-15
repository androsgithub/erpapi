package com.api.erp.v1.docs.openapi.features.contato;

import com.api.erp.v1.main.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.main.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.main.features.contato.domain.controller.IContatosController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Interface de documentação OpenAPI para Contatos.
 */
@Tag(name = "Contatos", description = "Gestão de Contatos - Operações Básicas")
public interface ContatosOpenApiDocumentation extends IContatosController {

    @Override
    @Operation(summary = "Criar novo contato", description = "Cria um novo contato no sistema (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contato criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContatoResponse> criar(@RequestBody CreateContatoRequest request);

    @Override
    @Operation(summary = "Buscar contato por ID", description = "Busca um contato específico (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato encontrado"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContatoResponse> buscar(
            @Parameter(description = "ID do contato") Long id);

    @Override
    @Operation(summary = "Listar todos os contatos", description = "Lista todos os contatos cadastrados (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de contatos")
    ResponseEntity<List<ContatoResponse>> listar();

    @Override
    @Operation(summary = "Listar contatos ativos", description = "Lista todos os contatos ativos (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de contatos ativos")
    ResponseEntity<List<ContatoResponse>> listarAtivos();

    @Override
    @Operation(summary = "Listar contatos inativos", description = "Lista todos os contatos inativos (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de contatos inativos")
    ResponseEntity<List<ContatoResponse>> listarInativos();

    @Override
    @Operation(summary = "Listar contatos por tipo", description = "Lista contatos de um tipo específico (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de contatos"),
            @ApiResponse(responseCode = "400", description = "Tipo de contato inválido")
    })
    ResponseEntity<List<ContatoResponse>> listarPorTipo(
            @Parameter(description = "Tipo de contato (TELEFONE, EMAIL, WHATSAPP, etc.)") String tipo);

    @Override
    @Operation(summary = "Buscar contato principal", description = "Busca o contato marcado como principal (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato principal encontrado"),
            @ApiResponse(responseCode = "404", description = "Nenhum contato principal encontrado")
    })
    ResponseEntity<ContatoResponse> buscarPrincipal();

    @Override
    @Operation(summary = "Atualizar contato", description = "Atualiza dados de um contato (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContatoResponse> atualizar(
            @Parameter(description = "ID do contato") Long id,
            @RequestBody CreateContatoRequest request);

    @Override
    @Operation(summary = "Ativar contato", description = "Ativa um contato desativado (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContatoResponse> ativar(
            @Parameter(description = "ID do contato") Long id);

    @Override
    @Operation(summary = "Desativar contato", description = "Desativa um contato (soft delete) (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContatoResponse> desativar(
            @Parameter(description = "ID do contato") Long id);

    @Override
    @Operation(summary = "Deletar contato", description = "Remove um contato do sistema permanentemente (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contato deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Void> deletar(
            @Parameter(description = "ID do contato") Long id);
}
