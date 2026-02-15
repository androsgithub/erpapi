package com.api.erp.v1.docs.openapi.features.produto;

import com.api.erp.v1.main.features.produto.application.dto.ComposicaoRequestDTO;
import com.api.erp.v1.main.features.produto.application.dto.ComposicaoResponseDTO;
import com.api.erp.v1.main.features.produto.domain.controller.IComposicaoController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface de documentação OpenAPI para Composição.
 */
@Tag(name = "Composição de Produto", description = "Gerenciamento de composições (BOM) de produtos")
public interface ComposicaoOpenApiDocumentation extends IComposicaoController {

    @Override
    @Operation(
            summary = "Criar composição",
            description = "Cria uma nova composição (BOM) entre produtos com validações de domínio"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Composição criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Composição circular ou já existe")
    })
    ResponseEntity<ComposicaoResponseDTO> criar(
            ComposicaoRequestDTO dto);

    @Override
    @Operation(
            summary = "Atualizar composição",
            description = "Atualiza uma composição existente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Composição atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Composição não encontrada")
    })
    ResponseEntity<ComposicaoResponseDTO> atualizar(
            Long id,
            ComposicaoRequestDTO dto);

    @Override
    @Operation(
            summary = "Obter composição",
            description = "Retorna os dados de uma composição pelo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Composição encontrada"),
            @ApiResponse(responseCode = "404", description = "Composição não encontrada")
    })
    ResponseEntity<ComposicaoResponseDTO> obter(Long id);

    @Override
    @Operation(
            summary = "Listar composições de um produto",
            description = "Retorna todas as composições (itens da BOM) de um produto fabricável"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de composições"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    ResponseEntity<List<ComposicaoResponseDTO>> listarComposicoesPor(
            Long produtoFabricadoId);

    @Override
    @Operation(
            summary = "Deletar composição",
            description = "Remove uma composição (item) da BOM"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Composição deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Composição não encontrada")
    })
    ResponseEntity<Void> deletar(Long id);

    @Override
    @Operation(
            summary = "Deletar todas as composições",
            description = "Remove todas as composições (BOM completa) de um produto"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Composições deletadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    ResponseEntity<Void> deletarComposicoesDeProduto(
            Long produtoFabricadoId);
}
