package com.api.erp.v1.features.unidademedida.domain.controller;

import com.api.erp.v1.features.unidademedida.application.dto.request.UnidadeMedidaRequestDTO;
import com.api.erp.v1.features.unidademedida.application.dto.response.UnidadeMedidaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Unidade de Medida", description = "Gerenciamento de unidades de medida")
public interface IUnidadeMedidaController {

    @Operation(
            summary = "Criar nova unidade de medida",
            description = "Cria uma nova unidade de medida com validações de domínio"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Unidade criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Sigla já existe")
    })
    public ResponseEntity<UnidadeMedidaResponseDTO> criar(UnidadeMedidaRequestDTO dto);


    @Operation(
            summary = "Atualizar unidade de medida",
            description = "Atualiza os dados de uma unidade de medida existente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "409", description = "Sigla já existe")
    })
    ResponseEntity<UnidadeMedidaResponseDTO> atualizar(Long id, UnidadeMedidaRequestDTO dto);

    @Operation(
            summary = "Obter unidade de medida",
            description = "Retorna os dados de uma unidade de medida pelo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    ResponseEntity<UnidadeMedidaResponseDTO> obter(Long id);

    @Operation(
            summary = "Listar unidades de medida",
            description = "Retorna todas as unidades de medida com paginação"
    )
    @ApiResponse(responseCode = "200", description = "Lista de unidades")
    ResponseEntity<Page<UnidadeMedidaResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "descricao") String sortBy);

    @Operation(
            summary = "Ativar unidade de medida",
            description = "Ativa uma unidade de medida desativada"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade ativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    ResponseEntity<UnidadeMedidaResponseDTO> ativar(Long id);

    @Operation(
            summary = "Desativar unidade de medida",
            description = "Desativa uma unidade de medida ativa"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade desativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    ResponseEntity<UnidadeMedidaResponseDTO> desativar(Long id);

    @Operation(
            summary = "Deletar unidade de medida",
            description = "Remove uma unidade de medida do sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Unidade deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    ResponseEntity<Void> deletar(Long id);
}
