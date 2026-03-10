package com.api.erp.v1.docs.openapi.features.product;

import com.api.erp.v1.main.features.product.application.dto.ListaExpandidaResponseDTO;
import com.api.erp.v1.main.features.product.domain.controller.IListaExpandidaController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Interface de documentação OpenAPI para Lista Expandida.
 */
@Tag(name = "Lista Expandida de Produção", description = "Cálculo de listas expandidas e composições aninhadas")
public interface ListaExpandidaOpenApiDocumentation extends IListaExpandidaController {

    @Override
    @Operation(
            summary = "Gerar lista expandida de produção",
            description = """
                    Gera a lista expandida de produção para um product fabricável.
                    
                    Considera:
                    - Composições aninhadas (product fabricável que usa outro fabricável)
                    - Quantidades acumuladas corretamente
                    - Evita duplicidades, somando quantidades do mesmo product
                    
                    Exemplo:
                    Product A → Product B (2x) → Product C (3x)
                    Resultado: Product C = 6x
                    """
    )
    @ApiResponse(responseCode = "200", description = "Lista expandida gerada com sucesso")
    @ApiResponse(responseCode = "400", description = "Quantidade inválida")
    @ApiResponse(responseCode = "404", description = "Product não encontrado ou não é fabricável")
    ResponseEntity<ListaExpandidaResponseDTO> gerarListaExpandida(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade);

    @Override
    @Operation(
            summary = "Gerar lista de compras",
            description = """
                    Gera a lista de compras (apenas products comprados) para um product fabricável.
                    
                    Útil para:
                    - Identificar materiais a comprar
                    - Gerar lista de requisição para fornecedores
                    - Planejar compras
                    
                    Exemplo:
                    Product A (fabricável) → inclui Products B, C, D (comprados)
                    Resultado: Lista apenas de B, C, D com quantidades acumuladas
                    """
    )
    @ApiResponse(responseCode = "200", description = "Lista de compras gerada com sucesso")
    @ApiResponse(responseCode = "400", description = "Quantidade inválida")
    @ApiResponse(responseCode = "404", description = "Product não encontrado ou não é fabricável")
    ResponseEntity<ListaExpandidaResponseDTO> gerarListaCompras(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") BigDecimal quantidade);
}
