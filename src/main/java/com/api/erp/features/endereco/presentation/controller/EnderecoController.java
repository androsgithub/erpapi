package com.api.erp.features.endereco.presentation.controller;

import com.api.erp.features.endereco.application.dto.CriarEnderecoRequest;
import com.api.erp.features.endereco.application.dto.EnderecoResponse;
import com.api.erp.features.endereco.application.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/enderecos")
@Tag(name = "Endereços", description = "Gestão de Endereços")
public class EnderecoController {
    
    private final EnderecoService enderecoService;
    
    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }
    
    @PostMapping
    @Operation(summary = "Criar novo endereço", description = "Cria um novo endereço no sistema (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<EnderecoResponse> criar(@RequestBody CriarEnderecoRequest request) {
        EnderecoResponse response = enderecoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar endereço por ID", description = "Busca um endereço específico (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<EnderecoResponse> buscar(
        @PathVariable @Parameter(description = "ID do endereço") String id) {
        EnderecoResponse endereco = enderecoService.buscarPorId(id);
        return ResponseEntity.ok(endereco);
    }
    
    @GetMapping
    @Operation(summary = "Listar todos os endereços", description = "Lista todos os endereços cadastrados (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Lista de endereços")
    public ResponseEntity<List<EnderecoResponse>> listar() {
        List<EnderecoResponse> enderecos = enderecoService.buscarTodos();
        return ResponseEntity.ok(enderecos);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar endereço", description = "Atualiza dados de um endereço (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<EnderecoResponse> atualizar(
        @PathVariable @Parameter(description = "ID do endereço") String id,
        @RequestBody CriarEnderecoRequest request) {
        EnderecoResponse endereco = enderecoService.atualizar(id, request);
        return ResponseEntity.ok(endereco);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar endereço", description = "Remove um endereço do sistema (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Void> deletar(
        @PathVariable @Parameter(description = "ID do endereço") String id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
