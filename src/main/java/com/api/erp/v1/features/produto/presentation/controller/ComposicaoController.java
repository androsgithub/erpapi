package com.api.erp.v1.features.produto.presentation.controller;

import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import com.api.erp.v1.features.produto.application.dto.ComposicaoRequestDTO;
import com.api.erp.v1.features.produto.application.dto.ComposicaoResponseDTO;
import com.api.erp.v1.features.produto.application.service.ComposicaoService;
import com.api.erp.v1.features.produto.domain.entity.ComposicaoPermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para Composição de Produto (BOM)
 * 
 * Responsabilidades:
 * - Gerenciar endpoints de composição
 * - Validar requisições HTTP
 * - Retornar respostas formatadas
 * 
 * SRP: Apenas manipulação de requisições/respostas HTTP
 */
@RestController
@RequestMapping("/api/v1/composicoes")
@RequiredArgsConstructor
@Tag(name = "Composição de Produto", description = "Gerenciamento de composições (BOM) de produtos")
public class ComposicaoController {
    
    private final ComposicaoService service;
    
    @PostMapping
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
    @RequiresPermission(ComposicaoPermissions.CRIAR)
    public ResponseEntity<ComposicaoResponseDTO> criar(
            @RequestBody ComposicaoRequestDTO dto) {
        ComposicaoResponseDTO resposta = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar composição",
        description = "Atualiza uma composição existente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Composição atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Composição não encontrada")
    })
    @RequiresPermission(ComposicaoPermissions.ATUALIZAR)
    public ResponseEntity<ComposicaoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody ComposicaoRequestDTO dto) {
        ComposicaoResponseDTO resposta = service.atualizar(id, dto);
        return ResponseEntity.ok(resposta);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Obter composição",
        description = "Retorna os dados de uma composição pelo ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Composição encontrada"),
        @ApiResponse(responseCode = "404", description = "Composição não encontrada")
    })
    @RequiresPermission(ComposicaoPermissions.VISUALIZAR)
    public ResponseEntity<ComposicaoResponseDTO> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obter(id));
    }
    
    @GetMapping("/produto/{produtoFabricadoId}")
    @Operation(
        summary = "Listar composições de um produto",
        description = "Retorna todas as composições (itens da BOM) de um produto fabricável"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de composições"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @RequiresPermission(ComposicaoPermissions.VISUALIZAR)
    public ResponseEntity<List<ComposicaoResponseDTO>> listarComposicoesPor(
            @PathVariable Long produtoFabricadoId) {
        return ResponseEntity.ok(service.listarComposicoesPor(produtoFabricadoId));
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar composição",
        description = "Remove uma composição (item) da BOM"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Composição deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Composição não encontrada")
    })
    @RequiresPermission(ComposicaoPermissions.DELETAR)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/produto/{produtoFabricadoId}/limpar")
    @Operation(
        summary = "Deletar todas as composições",
        description = "Remove todas as composições (BOM completa) de um produto"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Composições deletadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @RequiresPermission(ComposicaoPermissions.DELETAR)
    public ResponseEntity<Void> deletarComposicoesDeProduto(
            @PathVariable Long produtoFabricadoId) {
        service.deletarComposicoesDeProduto(produtoFabricadoId);
        return ResponseEntity.noContent().build();
    }
}
