package com.api.erp.v1.features.produto.domain.controller;

import com.api.erp.v1.features.produto.application.dto.ProdutoRequestDTO;
import com.api.erp.v1.features.produto.application.dto.ProdutoResponseDTO;
import com.api.erp.v1.features.produto.domain.entity.TipoProduto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Produto", description = "Gerenciamento de produtos do ERP")
public interface IProdutoController {
    @Operation(summary = "Criar novo produto", description = "Cria um novo produto com validações de domínio")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Produto criado com sucesso"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "404", description = "Unidade de medida não encontrada"), @ApiResponse(responseCode = "409", description = "Código do produto já existe")})
    ResponseEntity<ProdutoResponseDTO> criar(ProdutoRequestDTO dto);

    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "404", description = "Produto não encontrado"), @ApiResponse(responseCode = "409", description = "Código já existe")})
    ResponseEntity<ProdutoResponseDTO> atualizar(Long id, ProdutoRequestDTO dto);

    @Operation(summary = "Obter produto", description = "Retorna os dados de um produto pelo ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto encontrado"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    ResponseEntity<ProdutoResponseDTO> obter(Long id);

    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de produtos")
    ResponseEntity<Page<ProdutoResponseDTO>> listar(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "descricao") String sortBy);

    @Operation(summary = "Listar produtos por tipo", description = "Retorna produtos filtrados por tipo (COMPRADO ou FABRICAVEL)")
    @ApiResponse(responseCode = "200", description = "Lista de produtos filtrados")
    ResponseEntity<Page<ProdutoResponseDTO>> listarPorTipo(TipoProduto tipo, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Ativar produto", description = "Muda o status do produto para ATIVO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto ativado com sucesso"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    ResponseEntity<ProdutoResponseDTO> ativar(Long id);

    @Operation(summary = "Desativar produto", description = "Muda o status do produto para INATIVO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto desativado com sucesso"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    ResponseEntity<ProdutoResponseDTO> desativar(Long id);

    @Operation(summary = "Bloquear produto", description = "Muda o status do produto para BLOQUEADO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto bloqueado com sucesso"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    ResponseEntity<ProdutoResponseDTO> bloquear(Long id);

    @Operation(summary = "Descontinuar produto", description = "Muda o status do produto para DESCONTINUADO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto descontinuado com sucesso"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    ResponseEntity<ProdutoResponseDTO> descontinuar(Long id);

    @Operation(summary = "Deletar produto", description = "Remove um produto do sistema")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    ResponseEntity<Void> deletar(Long id);
}
