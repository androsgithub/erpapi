package com.api.erp.v1.docs.openapi.features.endereco;

import com.api.erp.v1.main.features.endereco.application.dto.request.CreateEnderecoRequest;
import com.api.erp.v1.main.features.endereco.application.dto.response.EnderecoResponse;
import com.api.erp.v1.main.features.endereco.domain.controller.IEnderecoController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface de documentação OpenAPI para Endereco.
 */
@Tag(name = "Endereços", description = "Gestão de Endereços")
public interface EnderecoOpenApiDocumentation extends IEnderecoController {

    @Override
    @Operation(summary = "Criar novo endereço", description = "Cria um novo endereço no sistema (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<EnderecoResponse> criar(CreateEnderecoRequest request);

    @Override
    @Operation(summary = "Buscar endereço por ID", description = "Busca um endereço específico (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<EnderecoResponse> buscar(
            Long id);

    @Override
    @Operation(summary = "Listar todos os endereços", description = "Lista todos os endereços cadastrados (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de endereços")
    ResponseEntity<List<EnderecoResponse>> listar();

    @Override
    @Operation(summary = "Atualizar endereço", description = "Atualiza dados de um endereço (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<EnderecoResponse> atualizar(
            Long id,
            CreateEnderecoRequest request);

    @Override
    @Operation(summary = "Deletar endereço", description = "Remove um endereço do sistema (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Void> deletar(
            Long id);
}
