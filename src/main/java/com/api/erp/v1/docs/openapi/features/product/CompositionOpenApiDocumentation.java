package com.api.erp.v1.docs.openapi.features.product;

import com.api.erp.v1.main.dynamic.features.product.application.dto.CompositionRequestDTO;
import com.api.erp.v1.main.dynamic.features.product.application.dto.CompositionResponseDTO;
import com.api.erp.v1.main.dynamic.features.product.domain.controller.ICompositionController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface de documentação OpenAPI para Composição.
 */
@Tag(name = "Composição de Product", description = "Gerenciamento de composições (BOM) de products")
public interface CompositionOpenApiDocumentation extends ICompositionController {

    @Override
    @Operation(
            summary = "Criar composição",
            description = "Cria uma nova composição (BOM) entre products com validações de domínio"
    )
    @ApiResponse(responseCode = "201", description = "Composição criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Product não encontrado")
    @ApiResponse(responseCode = "409", description = "Composição circular ou já existe")
    ResponseEntity<CompositionResponseDTO> criar(
            CompositionRequestDTO dto);

    @Override
    @Operation(
            summary = "Atualizar composição",
            description = "Atualiza uma composição existente"
    )
    @ApiResponse(responseCode = "200", description = "Composição atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Composição não encontrada")
    ResponseEntity<CompositionResponseDTO> atualizar(
            Long id,
            CompositionRequestDTO dto);

    @Override
    @Operation(
            summary = "Obter composição",
            description = "Retorna os dados de uma composição pelo ID"
    )
    @ApiResponse(responseCode = "200", description = "Composição encontrada")
    @ApiResponse(responseCode = "404", description = "Composição não encontrada")
    ResponseEntity<CompositionResponseDTO> obter(Long id);

    @Override
    @Operation(
            summary = "Listar composições de um product",
            description = "Retorna todas as composições (itens da BOM) de um product fabricável"
    )
    @ApiResponse(responseCode = "200", description = "Lista de composições")
    @ApiResponse(responseCode = "404", description = "Product não encontrado")
    ResponseEntity<List<CompositionResponseDTO>> listarComposicoesPor(
            Long productFabricadoId);

    @Override
    @Operation(
            summary = "Deletar composição",
            description = "Remove uma composição (item) da BOM"
    )
    @ApiResponse(responseCode = "204", description = "Composição deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Composição não encontrada")
    ResponseEntity<Void> deletar(Long id);

    @Override
    @Operation(
            summary = "Deletar todas as composições",
            description = "Remove todas as composições (BOM completa) de um product"
    )
    @ApiResponse(responseCode = "204", description = "Composições deletadas com sucesso")
    @ApiResponse(responseCode = "404", description = "Product não encontrado")
    ResponseEntity<Void> deletarComposicoesDeProduct(
            Long productFabricadoId);
}
