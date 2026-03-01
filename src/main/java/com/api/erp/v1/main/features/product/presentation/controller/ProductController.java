package com.api.erp.v1.main.features.product.presentation.controller;

import com.api.erp.v1.main.features.product.application.dto.ProductRequestDTO;
import com.api.erp.v1.main.features.product.application.dto.ProductResponseDTO;
import com.api.erp.v1.main.features.product.application.mapper.IProductMapper;
import com.api.erp.v1.main.features.product.domain.controller.IProductController;
import com.api.erp.v1.docs.openapi.features.product.ProductOpenApiDocumentation;
import com.api.erp.v1.main.features.product.domain.entity.Product;
import com.api.erp.v1.main.features.product.domain.entity.ProductPermissions;
import com.api.erp.v1.main.features.product.domain.entity.ProductType;
import com.api.erp.v1.main.features.product.domain.service.IProductService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
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
@RequestMapping("/api/v1/products")
public class ProductController implements IProductController, ProductOpenApiDocumentation {
    @Autowired
    @Qualifier("productServiceProxy")
    private IProductService service;
    @Autowired
    private IProductMapper productMapper;

    @PostMapping
    @Operation(summary = "Criar novo product", description = "Cria um novo product com validações de domínio")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Product criado com sucesso"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "404", description = "Unidade de medida não encontrada"), @ApiResponse(responseCode = "409", description = "Código do product já existe")})
    @RequiresXTenantId
    @RequiresPermission(ProductPermissions.CRIAR)
    public ResponseEntity<ProductResponseDTO> criar(@RequestBody ProductRequestDTO dto) {
        Product resposta = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toResponse(resposta));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar product", description = "Atualiza os dados de um product existente")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Product atualizado com sucesso"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "404", description = "Product não encontrado"), @ApiResponse(responseCode = "409", description = "Código já existe")})
    @RequiresXTenantId
    @RequiresPermission(ProductPermissions.ATUALIZAR)
    public ResponseEntity<ProductResponseDTO> atualizar(@PathVariable Long id, @RequestBody ProductRequestDTO dto) {
        Product product = service.atualizar(id, dto);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter product", description = "Retorna os dados de um product pelo ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Product encontrado"), @ApiResponse(responseCode = "404", description = "Product não encontrado")})
    @RequiresXTenantId
    @RequiresPermission(ProductPermissions.VISUALIZAR)
    public ResponseEntity<ProductResponseDTO> obter(@PathVariable Long id) {
        return ResponseEntity.ok(productMapper.toResponse(service.obter(id)));
    }

    @GetMapping
    @Operation(summary = "Listar products", description = "Retorna todos os products com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de products")
    @RequiresXTenantId
    @RequiresPermission(ProductPermissions.VISUALIZAR)
    public ResponseEntity<Page<ProductResponseDTO>> listar(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "descricao") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<ProductResponseDTO> response = productMapper.toResponsePage(service.listar(pageable));
        return ResponseEntity.ok(response);

    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar products por tipo", description = "Retorna products filtrados por tipo (COMPRADO ou FABRICAVEL)")
    @ApiResponse(responseCode = "200", description = "Lista de products filtrados")
    @RequiresXTenantId
    @RequiresPermission(ProductPermissions.VISUALIZAR)
    public ResponseEntity<Page<ProductResponseDTO>> listarPorTipo(@PathVariable ProductType type, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDTO> response = productMapper.toResponsePage(service.listarPorTipo(type, pageable));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar product", description = "Muda o status do product para ATIVO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Product ativado com sucesso"), @ApiResponse(responseCode = "404", description = "Product não encontrado")})
    @RequiresXTenantId
    @RequiresPermission(ProductPermissions.ATIVAR)
    public ResponseEntity<ProductResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(productMapper.toResponse(service.ativar(id)));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar product", description = "Muda o status do product para INATIVO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Product desativado com sucesso"), @ApiResponse(responseCode = "404", description = "Product não encontrado")})
    @RequiresXTenantId
    @RequiresPermission(ProductPermissions.DESATIVAR)
    public ResponseEntity<ProductResponseDTO> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(productMapper.toResponse(service.desativar(id)));
    }

    @PatchMapping("/{id}/bloquear")
    @Operation(summary = "Bloquear product", description = "Muda o status do product para BLOQUEADO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Product bloqueado com sucesso"), @ApiResponse(responseCode = "404", description = "Product não encontrado")})
    @RequiresXTenantId
    @RequiresPermission(ProductPermissions.BLOQUEAR)
    public ResponseEntity<ProductResponseDTO> bloquear(@PathVariable Long id) {
        return ResponseEntity.ok(productMapper.toResponse(service.bloquear(id)));
    }

    @PatchMapping("/{id}/descontinuar")
    @Operation(summary = "Descontinuar product", description = "Muda o status do product para DESCONTINUADO")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Product descontinuado com sucesso"), @ApiResponse(responseCode = "404", description = "Product não encontrado")})
    @RequiresXTenantId
    @RequiresPermission(ProductPermissions.DESCONTINUAR)
    public ResponseEntity<ProductResponseDTO> descontinuar(@PathVariable Long id) {
        return ResponseEntity.ok(productMapper.toResponse(service.descontinuar(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar product", description = "Remove um product do sistema")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Product deletado com sucesso"), @ApiResponse(responseCode = "404", description = "Product não encontrado")})
    @RequiresXTenantId
    @RequiresPermission(ProductPermissions.DELETAR)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
