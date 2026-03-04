package com.api.erp.v1.docs.openapi.features.measureunit;

import com.api.erp.v1.main.features.measureunit.application.dto.request.MeasureUnitRequestDTO;
import com.api.erp.v1.main.features.measureunit.application.dto.response.MeasureUnitResponseDTO;
import com.api.erp.v1.main.features.measureunit.domain.controller.IMeasureUnitController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface de documentação OpenAPI para Unidade de Medida.
 */
@Tag(name = "Unidade de Medida", description = "Gerenciamento de unidades de medida")
public interface MeasureUnitOpenApiDocumentation extends IMeasureUnitController {

    @Override
    @Operation(
            summary = "Criar nova unidade de medida",
            description = "Cria uma nova unidade de medida com validações de domínio"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Unidade criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Sigla já existe")
    })
    ResponseEntity<MeasureUnitResponseDTO> criar(MeasureUnitRequestDTO dto);

    @Override
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
    ResponseEntity<MeasureUnitResponseDTO> atualizar(Long id, MeasureUnitRequestDTO dto);

    @Override
    @Operation(
            summary = "Obter unidade de medida",
            description = "Retorna os dados de uma unidade de medida pelo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    ResponseEntity<MeasureUnitResponseDTO> obter(Long id);

    @Override
    @Operation(
            summary = "Listar unidades de medida",
            description = "Retorna todas as unidades de medida com paginação"
    )
    @ApiResponse(responseCode = "200", description = "Lista de unidades")
    ResponseEntity<Page<MeasureUnitResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "descricao") String sortBy);

    @Override
    @Operation(
            summary = "Ativar unidade de medida",
            description = "Ativa uma unidade de medida desativada"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade ativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    ResponseEntity<MeasureUnitResponseDTO> ativar(Long id);

    @Override
    @Operation(
            summary = "Desativar unidade de medida",
            description = "Desativa uma unidade de medida ativa"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade desativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    ResponseEntity<MeasureUnitResponseDTO> desativar(Long id);

    @Override
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
