package com.api.erp.features.unidademedida.presentation.controller;

import com.api.erp.features.unidademedida.application.dto.UnidadeMedidaRequestDTO;
import com.api.erp.features.unidademedida.application.dto.UnidadeMedidaResponseDTO;
import com.api.erp.features.unidademedida.application.service.UnidadeMedidaService;
import io.swagger.v3.oas.annotations.Operation;
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
 * Controller para UnidadeMedida
 * <p>
 * Responsabilidades:
 * - Receber requisições HTTP
 * - Validar entrada
 * - Chamar serviço apropriado
 * - Retornar resposta formatada
 * <p>
 * SRP: Apenas manipulação de requisições/respostas
 */
@RestController
@RequestMapping("/api/v1/unidades-medida")
@RequiredArgsConstructor
@Tag(name = "Unidade de Medida", description = "Gerenciamento de unidades de medida")
public class UnidadeMedidaController {

    private final UnidadeMedidaService service;

    @PostMapping
    @Operation(
            summary = "Criar nova unidade de medida",
            description = "Cria uma nova unidade de medida com validações de domínio"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Unidade criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Sigla já existe")
    })
    public ResponseEntity<UnidadeMedidaResponseDTO> criar(
            @RequestBody UnidadeMedidaRequestDTO dto) {
        UnidadeMedidaResponseDTO resposta = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar unidade de medida",
            description = "Atualiza os dados de uma unidade de medida existente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "409", description = "Sigla já existe")
    })
    public ResponseEntity<UnidadeMedidaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody UnidadeMedidaRequestDTO dto) {
        UnidadeMedidaResponseDTO resposta = service.atualizar(id, dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obter unidade de medida",
            description = "Retorna os dados de uma unidade de medida pelo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    public ResponseEntity<UnidadeMedidaResponseDTO> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @GetMapping
    @Operation(
            summary = "Listar unidades de medida",
            description = "Retorna todas as unidades de medida com paginação"
    )
    @ApiResponse(responseCode = "200", description = "Lista de unidades")
    public ResponseEntity<Page<UnidadeMedidaResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "descricao") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(service.listar(pageable));
    }

    @PatchMapping("/{id}/ativar")
    @Operation(
            summary = "Ativar unidade de medida",
            description = "Ativa uma unidade de medida desativada"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade ativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    public ResponseEntity<UnidadeMedidaResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(
            summary = "Desativar unidade de medida",
            description = "Desativa uma unidade de medida ativa"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade desativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    public ResponseEntity<UnidadeMedidaResponseDTO> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.desativar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar unidade de medida",
            description = "Remove uma unidade de medida do sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Unidade deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
