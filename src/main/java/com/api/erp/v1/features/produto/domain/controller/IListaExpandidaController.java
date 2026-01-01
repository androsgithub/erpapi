package com.api.erp.v1.features.produto.domain.controller;

import com.api.erp.v1.features.produto.application.dto.ListaExpandidaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Tag(name = "Lista Expandida de Produção", description = "Cálculo de listas expandidas e composições aninhadas")
public interface IListaExpandidaController {
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
    public ResponseEntity<ListaExpandidaResponseDTO> gerarListaExpandida(
            @PathVariable Long produtoId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade);


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
    public ResponseEntity<ListaExpandidaResponseDTO> gerarListaCompras(
            @PathVariable Long produtoId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade);
}
