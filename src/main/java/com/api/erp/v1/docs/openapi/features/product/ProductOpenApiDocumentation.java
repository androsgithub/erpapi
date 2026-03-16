package com.api.erp.v1.docs.openapi.features.product;

import com.api.erp.v1.main.dynamic.features.product.application.dto.ProductRequestDTO;
import com.api.erp.v1.main.dynamic.features.product.application.dto.ProductResponseDTO;
import com.api.erp.v1.main.dynamic.features.product.domain.controller.IProductController;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.ProductType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface de documentação OpenAPI para Product.
 */
@Tag(name = "Product", description = "Gerenciamento de products do ERP")
public interface ProductOpenApiDocumentation extends IProductController {

    @Override
    @Operation(summary = "Criar novo product", description = "Cria um novo product com validações de domínio")
    @ApiResponse(responseCode = "201", description = "Product criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Unidade de medida não encontrada")

    @ApiResponse(responseCode = "409", description = "Código do product já existe")
    ResponseEntity<ProductResponseDTO> criar(ProductRequestDTO dto);

    @Override
    @Operation(summary = "Atualizar product", description = "Atualiza os dados de um product existente")
    @ApiResponse(responseCode = "200", description = "Product atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Product não encontrado")

    @ApiResponse(responseCode = "409", description = "Código já existe")
    ResponseEntity<ProductResponseDTO> atualizar(Long id, ProductRequestDTO dto);

    @Override
    @Operation(summary = "Obter product", description = "Retorna os dados de um product pelo ID")
    @ApiResponse(responseCode = "200", description = "Product encontrado")

    @ApiResponse(responseCode = "404", description = "Product não encontrado")
    ResponseEntity<ProductResponseDTO> obter(Long id);

    @Override
    @Operation(summary = "Listar products", description = "Retorna todos os products com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de products")
    ResponseEntity<Page<ProductResponseDTO>> listar(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "descricao") String sortBy);

    @Override
    @Operation(summary = "Listar products por tipo", description = "Retorna products filtrados por tipo (COMPRADO ou FABRICAVEL)")
    @ApiResponse(responseCode = "200", description = "Lista de products filtrados")
    ResponseEntity<Page<ProductResponseDTO>> listarPorTipo(ProductType tipo, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size);

    @Override
    @Operation(summary = "Ativar product", description = "Muda o status do product para ATIVO")
    @ApiResponse(responseCode = "200", description = "Product ativado com sucesso")

    @ApiResponse(responseCode = "404", description = "Product não encontrado")
    ResponseEntity<ProductResponseDTO> ativar(Long id);

    @Override
    @Operation(summary = "Desativar product", description = "Muda o status do product para INATIVO")
    @ApiResponse(responseCode = "200", description = "Product desativado com sucesso")

    @ApiResponse(responseCode = "404", description = "Product não encontrado")
    ResponseEntity<ProductResponseDTO> desativar(Long id);

    @Override
    @Operation(summary = "Bloquear product", description = "Muda o status do product para BLOQUEADO")
    @ApiResponse(responseCode = "200", description = "Product bloqueado com sucesso")

    @ApiResponse(responseCode = "404", description = "Product não encontrado")
    ResponseEntity<ProductResponseDTO> bloquear(Long id);

    @Override
    @Operation(summary = "Descontinuar product", description = "Muda o status do product para DESCONTINUADO")
    @ApiResponse(responseCode = "200", description = "Product descontinuado com sucesso")

    @ApiResponse(responseCode = "404", description = "Product não encontrado")
    ResponseEntity<ProductResponseDTO> descontinuar(Long id);

    @Override
    @Operation(summary = "Deletar product", description = "Remove um product do sistema")
    @ApiResponse(responseCode = "204", description = "Product deletado com sucesso")

    @ApiResponse(responseCode = "404", description = "Product não encontrado")
    ResponseEntity<Void> deletar(Long id);
}
