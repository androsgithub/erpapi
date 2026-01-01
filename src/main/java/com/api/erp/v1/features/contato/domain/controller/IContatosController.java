package com.api.erp.v1.features.contato.domain.controller;

import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Contatos", description = "Gestão de Contatos - Operações Básicas")
public interface IContatosController {

    @Operation(summary = "Criar novo contato", description = "Cria um novo contato no sistema (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contato criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ContatoResponse> criar(@RequestBody CreateContatoRequest request);

    @Operation(summary = "Buscar contato por ID", description = "Busca um contato específico (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato encontrado"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ContatoResponse> buscar(
            @Parameter(description = "ID do contato") Long id);


    @Operation(summary = "Listar todos os contatos", description = "Lista todos os contatos cadastrados (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de contatos")
    public ResponseEntity<List<ContatoResponse>> listar();

    @Operation(summary = "Listar contatos ativos", description = "Lista todos os contatos ativos (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de contatos ativos")
    public ResponseEntity<List<ContatoResponse>> listarAtivos();

    @Operation(summary = "Listar contatos inativos", description = "Lista todos os contatos inativos (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de contatos inativos")
    public ResponseEntity<List<ContatoResponse>> listarInativos();

    @Operation(summary = "Listar contatos por tipo", description = "Lista contatos de um tipo específico (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de contatos"),
            @ApiResponse(responseCode = "400", description = "Tipo de contato inválido")
    })
    public ResponseEntity<List<ContatoResponse>> listarPorTipo(
            @Parameter(description = "Tipo de contato (TELEFONE, EMAIL, WHATSAPP, etc.)") String tipo);

    @Operation(summary = "Buscar contato principal", description = "Busca o contato marcado como principal (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato principal encontrado"),
            @ApiResponse(responseCode = "404", description = "Nenhum contato principal encontrado")
    })
    public ResponseEntity<ContatoResponse> buscarPrincipal();

    @Operation(summary = "Atualizar contato", description = "Atualiza dados de um contato (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ContatoResponse> atualizar(
            @Parameter(description = "ID do contato") Long id,
            @RequestBody CreateContatoRequest request);

    @Operation(summary = "Ativar contato", description = "Ativa um contato desativado (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ContatoResponse> ativar(
            @Parameter(description = "ID do contato") Long id);

    @Operation(summary = "Desativar contato", description = "Desativa um contato (soft delete) (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ContatoResponse> desativar(
            @Parameter(description = "ID do contato") Long id);


    @Operation(summary = "Deletar contato", description = "Remove um contato do sistema permanentemente (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contato deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do contato") Long id);
}
