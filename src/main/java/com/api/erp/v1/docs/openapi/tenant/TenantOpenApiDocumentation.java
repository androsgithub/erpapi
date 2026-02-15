package com.api.erp.v1.docs.openapi.tenant;

import com.api.erp.v1.main.tenant.application.dto.*;
import com.api.erp.v1.main.tenant.domain.controller.ITenantController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Interface de documentação OpenAPI para Tenant.
 * Esta interface herda de ITenantController e adiciona as anotações Swagger.
 * A interface original ITenantController permanece limpa apenas com as assinaturas dos métodos.
 */
@Tag(
        name = "Tenant",
        description = "Endpoints responsáveis pela gestão e configurações da tenant (multi-tenant)"
)
public interface TenantOpenApiDocumentation extends ITenantController {

    @Override
    @Operation(
            summary = "Obter dados da tenant",
            description = "Retorna os dados cadastrais da tenant vinculada ao tenant do usuário autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados da tenant retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Tenant não configurada para o tenant")
    })
    ResponseEntity<TenantResponse> obter();

    @Override
    @Operation(
            summary = "Criar/Registrar novo tenant (tenant)",
            description = "Registra uma nova tenant (tenant) no sistema com seus dados básicos e opcionalmente com um datasource próprio."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tenant criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Tenant com CNPJ já existente")
    })
    ResponseEntity<TenantResponse> criar(
            @RequestBody CriarTenantRequest request
    );

    @Override
    @Operation(
            summary = "Listar todas as tenants",
            description = "Retorna a lista de todas as tenants cadastradas no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tenants retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    ResponseEntity<List<TenantResponse>> listar();

    @Override
    @Operation(
            summary = "Atualizar dados principais da tenant",
            description = "Atualiza os dados cadastrais principais da tenant, como razão social, CNPJ e configurações gerais."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenant atualizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Tenant não configurada ou dados inválidos")
    })
    ResponseEntity<TenantResponse> atualizar(
            @RequestBody TenantRequest request
    );

    @Override
    @Operation(
            summary = "Atualizar configurações de cliente",
            description = "Atualiza as configurações relacionadas ao módulo de clientes da tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurações de cliente atualizadas"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração inválida")
    })
    ResponseEntity<TenantResponse> atualizarClienteConfig(
            @RequestBody ClienteConfigRequest request
    );

    @Override
    @Operation(
            summary = "Atualizar configurações de usuário",
            description = "Atualiza as configurações relacionadas aos usuários da tenant, como permissões padrão e regras internas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurações de usuário atualizadas"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração inválida")
    })
    ResponseEntity<TenantResponse> atualizarUsuarioConfig(
            @RequestBody UsuarioConfigRequest request
    );

    @Override
    @Operation(
            summary = "Atualizar configurações de permissões",
            description = "Atualiza as regras e políticas de permissões da tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurações de permissões atualizadas"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração inválida")
    })
    ResponseEntity<TenantResponse> atualizarPermissaoConfig(
            @RequestBody PermissaoConfigRequest request
    );

    @Override
    @Operation(
            summary = "Atualizar configurações do tenant",
            description = "Atualiza configurações específicas do tenant, como limites, features habilitadas e comportamento do sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurações do tenant atualizadas"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Configuração inválida")
    })
    ResponseEntity<TenantResponse> atualizarTenantConfig(
            @RequestBody InternalTenantConfigRequest request
    );

    @Override
    @Operation(
            summary = "Atualizar endereço da tenant",
            description = "Atualiza os dados de endereço da tenant, utilizados em documentos fiscais e relatórios."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Dados de endereço inválidos")
    })
    ResponseEntity<TenantResponse> atualizarEnderecoConfig(
            @RequestBody EnderecoConfigRequest request
    );

    @Override
    @Operation(
            summary = "Atualizar informações de contato da tenant",
            description = "Atualiza informações de contato da tenant, como telefone, e-mail e canais de comunicação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Dados de contato inválidos")
    })
    ResponseEntity<TenantResponse> atualizarContatoConfig(
            @RequestBody ContatoConfigRequest request
    );
}
