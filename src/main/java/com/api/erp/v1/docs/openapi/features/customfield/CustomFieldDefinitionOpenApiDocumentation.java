package com.api.erp.v1.docs.openapi.features.customfield;

import com.api.erp.v1.main.features.customfield.application.dto.request.ChangeCustomFieldStatusRequest;
import com.api.erp.v1.main.features.customfield.application.dto.request.CreateCustomFieldRequest;
import com.api.erp.v1.main.features.customfield.application.dto.request.UpdateCustomFieldRequest;
import com.api.erp.v1.main.features.customfield.application.dto.response.CustomFieldResponse;
import com.api.erp.v1.main.features.customfield.domain.controller.ICustomFieldDefinitionController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Interface de documentação OpenAPI para Custom Field Definition.
 */
@Tag(
        name = "Custom Fields",
        description = "Gerenciamento de campos customizáveis por empresa e por entidade do sistema"
)
public interface CustomFieldDefinitionOpenApiDocumentation extends ICustomFieldDefinitionController {

    @Override
    @Operation(
            summary = "Listar campos customizados",
            description = "Retorna todos os campos customizados configurados para uma determinada tabela/entidade do sistema, considerando o tenant da empresa."
    )
    @ApiResponse(responseCode = "200", description = "Lista de campos customizados retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "400", description = "Tabela inválida ou não informada")
    List<CustomFieldResponse> getCustomFields(String table);

    @Override
    @Operation(
            summary = "Criar campo customizado",
            description = "Cria um novo campo customizado para uma entidade específica do sistema (ex: businesspartner, product) vinculado à empresa/tenant."
    )
    @ApiResponse(responseCode = "200", description = "Campo customizado criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou campo já existente")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    CustomFieldResponse create(CreateCustomFieldRequest request);

    @Override
    @Operation(
            summary = "Atualizar campo customizado",
            description = "Atualiza as propriedades de um campo customizado existente, como rótulo, tipo, obrigatoriedade ou metadados."
    )
    @ApiResponse(responseCode = "200", description = "Campo customizado atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Campo não encontrado ou dados inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    CustomFieldResponse update(Long id, UpdateCustomFieldRequest request);

    @Override
    @Operation(
            summary = "Ativar ou desativar campo customizado",
            description = "Altera o status de um campo customizado (ativo/inativo) sem removê-lo do sistema."
    )
    @ApiResponse(responseCode = "200", description = "Status do campo customizado alterado com sucesso")
    @ApiResponse(responseCode = "400", description = "Campo não encontrado ou status inválido")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    CustomFieldResponse changeStatus(Long id, ChangeCustomFieldStatusRequest request);

    @Override
    @Operation(
            summary = "Remover campo customizado",
            description = "Remove permanentemente um campo customizado. Esta operação pode afetar dados já existentes e deve ser utilizada com cautela."
    )
    @ApiResponse(responseCode = "204", description = "Campo customizado removido com sucesso")
    @ApiResponse(responseCode = "400", description = "Campo não encontrado")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    void delete(Long id);
}
