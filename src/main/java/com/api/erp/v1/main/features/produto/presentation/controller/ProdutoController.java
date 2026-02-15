package com.api.erp.v1.main.features.produto.presentation.controller;

import com.api.erp.v1.main.features.produto.application.dto.ProdutoRequestDTO;
import com.api.erp.v1.main.features.produto.application.dto.ProdutoResponseDTO;
import com.api.erp.v1.main.features.produto.application.mapper.IProdutoMapper;
import com.api.erp.v1.main.features.produto.domain.controller.IProdutoController;
import com.api.erp.v1.docs.openapi.features.produto.ProdutoOpenApiDocumentation;
import com.api.erp.v1.main.features.produto.domain.entity.Produto;
import com.api.erp.v1.main.features.produto.domain.entity.ProdutoPermissions;
import com.api.erp.v1.main.features.produto.domain.entity.TipoProduto;
import com.api.erp.v1.main.features.produto.domain.service.IProdutoService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController implements IProdutoController, ProdutoOpenApiDocumentation {
    @Autowired
    @Qualifier("produtoServiceProxy")
    private IProdutoService service;
    @Autowired
    private IProdutoMapper produtoMapper;

    @PostMapping
    @Operation(summary = "Criar novo produto", description = "Cria um novo produto com validações de domínio")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Produto criado com sucesso"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "404", description = "Unidade de medida não encontrada"), @ApiResponse(responseCode = "409", description = "Código do produto já existe")})
    @RequiresPermission(ProdutoPermissions.CRIAR)
    public ResponseEntity<ProdutoResponseDTO> criar(@RequestBody ProdutoRequestDTO dto) {
        Produto resposta = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoMapper.toResponse(resposta));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "404", description = "Produto não encontrado"), @ApiResponse(responseCode = "409", description = "Código já existe")})
    @RequiresPermission(ProdutoPermissions.ATUALIZAR)
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id, @RequestBody ProdutoRequestDTO dto) {
        Produto produto = service.atualizar(id, dto);
        return ResponseEntity.ok(produtoMapper.toResponse(produto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter produto", description = "Retorna os dados de um produto pelo ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto encontrado"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    @RequiresPermission(ProdutoPermissions.VISUALIZAR)
    public ResponseEntity<ProdutoResponseDTO> obter(@PathVariable Long id) {
        return ResponseEntity.ok(produtoMapper.toResponse(service.obter(id)));
    }

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de produtos")
    @RequiresPermission(ProdutoPermissions.VISUALIZAR)
    public ResponseEntity<Page<ProdutoResponseDTO>> listar(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "descricao") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<ProdutoResponseDTO> response = produtoMapper.toResponsePage(service.listar(pageable));
        return ResponseEntity.ok(response);

    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar produtos por tipo", description = "Retorna produtos filtrados por tipo (COMPRADO ou FABRICAVEL)")
    @ApiResponse(responseCode = "200", description = "Lista de produtos filtrados")
    @RequiresPermission(ProdutoPermissions.VISUALIZAR)
    public ResponseEntity<Page<ProdutoResponseDTO>> listarPorTipo(@PathVariable TipoProduto tipo, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoResponseDTO> response = produtoMapper.toResponsePage(service.listarPorTipo(tipo, pageable));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar produto", description = "Muda o status do produto para ATIVO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto ativado com sucesso"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    @RequiresPermission(ProdutoPermissions.ATIVAR)
    public ResponseEntity<ProdutoResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoMapper.toResponse(service.ativar(id)));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar produto", description = "Muda o status do produto para INATIVO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto desativado com sucesso"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    @RequiresPermission(ProdutoPermissions.DESATIVAR)
    public ResponseEntity<ProdutoResponseDTO> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoMapper.toResponse(service.desativar(id)));
    }

    @PatchMapping("/{id}/bloquear")
    @Operation(summary = "Bloquear produto", description = "Muda o status do produto para BLOQUEADO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto bloqueado com sucesso"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    @RequiresPermission(ProdutoPermissions.BLOQUEAR)
    public ResponseEntity<ProdutoResponseDTO> bloquear(@PathVariable Long id) {
        return ResponseEntity.ok(produtoMapper.toResponse(service.bloquear(id)));
    }

    @PatchMapping("/{id}/descontinuar")
    @Operation(summary = "Descontinuar produto", description = "Muda o status do produto para DESCONTINUADO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Produto descontinuado com sucesso"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    @RequiresPermission(ProdutoPermissions.DESCONTINUAR)
    public ResponseEntity<ProdutoResponseDTO> descontinuar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoMapper.toResponse(service.descontinuar(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar produto", description = "Remove um produto do sistema")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"), @ApiResponse(responseCode = "404", description = "Produto não encontrado")})
    @RequiresPermission(ProdutoPermissions.DELETAR)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
