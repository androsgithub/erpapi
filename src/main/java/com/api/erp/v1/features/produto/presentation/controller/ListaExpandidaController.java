package com.api.erp.v1.features.produto.presentation.controller;

import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import com.api.erp.v1.features.produto.application.dto.ListaExpandidaResponseDTO;
import com.api.erp.v1.features.produto.application.service.ListaExpandidaService;
import com.api.erp.v1.features.produto.domain.entity.ListaExpandidaPermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller para Lista Expandida de Produção
 * 
 * Responsabilidades:
 * - Fornecer endpoints para cálculos de BOM expandida
 * - Orquestrar requisições para o serviço de aplicação
 * - Retornar respostas formatadas
 * 
 * SRP: Apenas manipulação de requisições/respostas HTTP
 */
@RestController
@RequestMapping("/api/v1/lista-expandida")
@RequiredArgsConstructor
@Tag(name = "Lista Expandida de Produção", description = "Cálculo de listas expandidas e composições aninhadas")
public class ListaExpandidaController {
    
    private final ListaExpandidaService service;
    
    @GetMapping("/produto/{produtoId}")
    @Operation(
        summary = "Gerar lista expandida de produção",
        description = """
            Gera a lista expandida de produção para um produto fabricável.
            
            Considera:
            - Composições aninhadas (produto fabricável que usa outro fabricável)
            - Quantidades acumuladas corretamente
            - Evita duplicidades, somando quantidades do mesmo produto
            
            Exemplo:
            Produto A → Produto B (2x) → Produto C (3x)
            Resultado: Produto C = 6x
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista expandida gerada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado ou não é fabricável")
    })
    @RequiresPermission(ListaExpandidaPermissions.GERAR)
    public ResponseEntity<ListaExpandidaResponseDTO> gerarListaExpandida(
            @PathVariable Long produtoId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade) {
        ListaExpandidaResponseDTO resposta = service.gerarListaExpandida(produtoId, quantidade);
        return ResponseEntity.ok(resposta);
    }
    
    @GetMapping("/compras/produto/{produtoId}")
    @Operation(
        summary = "Gerar lista de compras",
        description = """
            Gera a lista de compras (apenas produtos comprados) para um produto fabricável.
            
            Útil para:
            - Identificar materiais a comprar
            - Gerar lista de requisição para fornecedores
            - Planejar compras
            
            Exemplo:
            Produto A (fabricável) → inclui Produtos B, C, D (comprados)
            Resultado: Lista apenas de B, C, D com quantidades acumuladas
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de compras gerada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado ou não é fabricável")
    })
    @RequiresPermission(ListaExpandidaPermissions.GERAR_COMPRA)
    public ResponseEntity<ListaExpandidaResponseDTO> gerarListaCompras(
            @PathVariable Long produtoId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade) {
        ListaExpandidaResponseDTO resposta = service.gerarListaCompras(produtoId, quantidade);
        return ResponseEntity.ok(resposta);
    }
}
