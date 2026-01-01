package com.api.erp.v1.features.empresa.domain.controller;

import com.api.erp.v1.features.empresa.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "Empresa",
        description = "Endpoints responsáveis pela gestão e configurações da empresa (multi-tenant)"
)
public interface IEmpresaController {

    @Operation(
            summary = "Obter dados da empresa",
            description = "Retorna os dados cadastrais da empresa vinculada ao tenant do usuário autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados da empresa retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Empresa não configurada para o tenant")
    })
    ResponseEntity<EmpresaResponse> obter();


    @Operation(
            summary = "Atualizar dados principais da empresa",
            description = "Atualiza os dados cadastrais principais da empresa, como razão social, CNPJ e configurações gerais."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Empresa não configurada ou dados inválidos")
    })
    ResponseEntity<EmpresaResponse> atualizar(
            @RequestBody EmpresaRequest request
    );


    @Operation(
            summary = "Atualizar configurações de cliente",
            description = "Atualiza as configurações relacionadas ao módulo de clientes da empresa."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurações de cliente atualizadas"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração inválida")
    })
    ResponseEntity<EmpresaResponse> atualizarClienteConfig(
            @RequestBody ClienteConfigRequest request
    );


    @Operation(
            summary = "Atualizar configurações de usuário",
            description = "Atualiza as configurações relacionadas aos usuários da empresa, como permissões padrão e regras internas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurações de usuário atualizadas"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração inválida")
    })
    ResponseEntity<EmpresaResponse> atualizarUsuarioConfig(
            @RequestBody UsuarioConfigRequest request
    );


    @Operation(
            summary = "Atualizar configurações de permissões",
            description = "Atualiza as regras e políticas de permissões da empresa."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurações de permissões atualizadas"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração inválida")
    })
    ResponseEntity<EmpresaResponse> atualizarPermissaoConfig(
            @RequestBody PermissaoConfigRequest request
    );


    @Operation(
            summary = "Atualizar configurações do tenant",
            description = "Atualiza configurações específicas do tenant, como limites, features habilitadas e comportamento do sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurações do tenant atualizadas"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração inválida")
    })
    ResponseEntity<EmpresaResponse> atualizarTenantConfig(
            @RequestBody TenantConfigRequest request
    );


    @Operation(
            summary = "Atualizar endereço da empresa",
            description = "Atualiza os dados de endereço da empresa, utilizados em documentos fiscais e relatórios."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Dados de endereço inválidos")
    })
    ResponseEntity<EmpresaResponse> atualizarEnderecoConfig(
            @RequestBody EnderecoConfigRequest request
    );


    @Operation(
            summary = "Atualizar informações de contato da empresa",
            description = "Atualiza informações de contato da empresa, como telefone, e-mail e canais de comunicação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Dados de contato inválidos")
    })
    ResponseEntity<EmpresaResponse> atualizarContatoConfig(
            @RequestBody ContatoConfigRequest request
    );
}
