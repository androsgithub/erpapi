package com.api.erp.features.usuario.presentation.controller;

import com.api.erp.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.features.usuario.application.dto.request.UpdateUsuarioRequest;
import com.api.erp.features.usuario.application.dto.request.LoginRequest;
import com.api.erp.features.usuario.application.dto.response.UsuarioResponse;
import com.api.erp.features.usuario.application.dto.response.TokenResponse;
import com.api.erp.features.usuario.application.service.AuthenticationService;
import com.api.erp.features.usuario.domain.entity.Usuario;
import com.api.erp.features.usuario.domain.service.UsuarioService;
import com.api.erp.features.usuario.infrastructure.factory.UsuarioServiceFactory;
import com.api.erp.features.usuario.infrastructure.decorator.GestorAprovacaoServiceDecorator;
import com.api.erp.shared.domain.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuários", description = "Gestão de usuários do sistema")
public class UsuarioController {
    private final UsuarioServiceFactory serviceFactory;
    private final AuthenticationService authenticationService;
    
    public UsuarioController(UsuarioServiceFactory serviceFactory, AuthenticationService authenticationService) {
        this.serviceFactory = serviceFactory;
        this.authenticationService = authenticationService;
    }
    
    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token retornado"),
        @ApiResponse(responseCode = "400", description = "Email ou senha inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public TokenResponse login(@RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check da API", description = "Endpoint público para verificar saúde da API")
    @ApiResponse(responseCode = "200", description = "API está operacional")
    public String health() {
        return "API está operacional!";
    }
    
    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public UsuarioResponse criar(@RequestBody CreateUsuarioRequest request) {
        UsuarioService service = serviceFactory.create();
        Usuario usuario = service.criar(request);
        return toResponse(usuario);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário específico (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public UsuarioResponse buscar(
        @PathVariable @Parameter(description = "ID do usuário") UUID id) {
        UsuarioService service = serviceFactory.create();
        Usuario usuario = service.buscarPorId(id);
        return toResponse(usuario);
    }
    
    @GetMapping
    @Operation(summary = "Listar usuários do sistema", description = "Lista todos os usuários (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Lista de usuários")
    public List<UsuarioResponse> listar() {
        UsuarioService service = serviceFactory.create();
        return service.listarTodos().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @PatchMapping("/{id}/aprovar")
    @Operation(summary = "Aprovar usuário pendente", description = "Aprova um usuário que está pendente de aprovação (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário aprovado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Usuário não está pendente de aprovação"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public UsuarioResponse aprovar(
        @PathVariable @Parameter(description = "ID do usuário") UUID id, 
        @RequestParam @Parameter(description = "ID do gestor") UUID gestorId) {
        UsuarioService service = serviceFactory.create();
        Usuario usuario = service.aprovar(id, gestorId);
        return toResponse(usuario);
    }
    @PatchMapping("/{id}/rejeitar")
    @Operation(summary = "Rejeitar usuário pendente", description = "Rejeita um usuário que está pendente de aprovação (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário rejeitado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Usuário não está pendente de aprovação"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public UsuarioResponse rejeitar(
        @PathVariable @Parameter(description = "ID do usuário") UUID id, 
        @RequestParam @Parameter(description = "ID do gestor") UUID gestorId, 
        @RequestParam @Parameter(description = "Motivo da rejeição") String motivo) {
        UsuarioService service = serviceFactory.create();
        Usuario usuario = service.rejeitar(id, gestorId, motivo);
        return toResponse(usuario);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza dados de um usuário (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public UsuarioResponse atualizar(
        @PathVariable @Parameter(description = "ID do usuário") UUID id, 
        @RequestBody UpdateUsuarioRequest request) {
        UsuarioService service = serviceFactory.create();
        Usuario usuario = service.atualizar(id, request);
        return toResponse(usuario);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar usuário", description = "Inativa um usuário (requer autenticação)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário inativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public void inativar(
        @PathVariable @Parameter(description = "ID do usuário") UUID id) {
        UsuarioService service = serviceFactory.create();
        service.inativar(id);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNomeCompleto(usuario.getNomeCompleto());
        response.setEmail(usuario.getEmail().getValor());
        response.setCpf(usuario.getCpf().getFormatado());
        response.setStatus(usuario.getStatus());
        response.setDataCriacao(usuario.getDataCriacao());
        return response;
    }
}