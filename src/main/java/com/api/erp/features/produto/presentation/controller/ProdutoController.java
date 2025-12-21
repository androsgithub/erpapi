package com.api.erp.features.produto.presentation.controller;

import com.api.erp.features.produto.application.dto.ProdutoRequestDTO;
import com.api.erp.features.produto.application.dto.ProdutoResponseDTO;
import com.api.erp.features.produto.application.service.ProdutoService;
import com.api.erp.features.produto.domain.entity.TipoProduto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para Produto
 * 
 * Responsabilidades:
 * - Receber requisições HTTP
 * - Validar entrada
 * - Chamar serviço apropriado
 * - Retornar resposta formatada
 * 
 * SRP: Apenas manipulação de requisições/respostas HTTP
 */
@RestController
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
@Tag(name = "Produto", description = "Gerenciamento de produtos do ERP")
public class ProdutoController {
    
    private final ProdutoService service;
    
    @PostMapping
    @Operation(
        summary = "Criar novo produto",
        description = "Cria um novo produto com validações de domínio"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Unidade de medida não encontrada"),
        @ApiResponse(responseCode = "409", description = "Código do produto já existe")
    })
    public ResponseEntity<ProdutoResponseDTO> criar(
            @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO resposta = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar produto",
        description = "Atualiza os dados de um produto existente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "409", description = "Código já existe")
    })
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO resposta = service.atualizar(id, dto);
        return ResponseEntity.ok(resposta);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Obter produto",
        description = "Retorna os dados de um produto pelo ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obter(id));
    }
    
    @GetMapping
    @Operation(
        summary = "Listar produtos",
        description = "Retorna todos os produtos com paginação"
    )
    @ApiResponse(responseCode = "200", description = "Lista de produtos")
    public ResponseEntity<Page<ProdutoResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "descricao") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(service.listar(pageable));
    }
    
    @GetMapping("/tipo/{tipo}")
    @Operation(
        summary = "Listar produtos por tipo",
        description = "Retorna produtos filtrados por tipo (COMPRADO ou FABRICAVEL)"
    )
    @ApiResponse(responseCode = "200", description = "Lista de produtos filtrados")
    public ResponseEntity<Page<ProdutoResponseDTO>> listarPorTipo(
            @PathVariable TipoProduto tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.listarPorTipo(tipo, pageable));
    }
    
    @PatchMapping("/{id}/ativar")
    @Operation(
        summary = "Ativar produto",
        description = "Muda o status do produto para ATIVO"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto ativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.ativar(id));
    }
    
    @PatchMapping("/{id}/desativar")
    @Operation(
        summary = "Desativar produto",
        description = "Muda o status do produto para INATIVO"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.desativar(id));
    }
    
    @PatchMapping("/{id}/bloquear")
    @Operation(
        summary = "Bloquear produto",
        description = "Muda o status do produto para BLOQUEADO"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto bloqueado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> bloquear(@PathVariable Long id) {
        return ResponseEntity.ok(service.bloquear(id));
    }
    
    @PatchMapping("/{id}/descontinuar")
    @Operation(
        summary = "Descontinuar produto",
        description = "Muda o status do produto para DESCONTINUADO"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto descontinuado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> descontinuar(@PathVariable Long id) {
        return ResponseEntity.ok(service.descontinuar(id));
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar produto",
        description = "Remove um produto do sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
