package com.api.erp.v1.features.camposcustom.domain.controller;

import com.api.erp.v1.features.camposcustom.application.dto.request.ChangeCustomFieldStatusRequest;
import com.api.erp.v1.features.camposcustom.application.dto.request.CreateCustomFieldRequest;
import com.api.erp.v1.features.camposcustom.application.dto.request.UpdateCustomFieldRequest;
import com.api.erp.v1.features.camposcustom.application.dto.response.CustomFieldResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(
        name = "Custom Fields",
        description = "Gerenciamento de campos customizáveis por empresa e por entidade do sistema"
)
public interface ICustomFieldDefinitionController {

    @Operation(
            summary = "Listar campos customizados",
            description = "Retorna todos os campos customizados configurados para uma determinada tabela/entidade do sistema, considerando o tenant da empresa."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de campos customizados retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Tabela inválida ou não informada")
    })
    List<CustomFieldResponse> getCustomFields(Long tenantId, String table);


    @Operation(
            summary = "Criar campo customizado",
            description = "Cria um novo campo customizado para uma entidade específica do sistema (ex: cliente, produto), vinculado à empresa/tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campo customizado criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campo já existente"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    CustomFieldResponse create(Long tenantId, CreateCustomFieldRequest request);


    @Operation(
            summary = "Atualizar campo customizado",
            description = "Atualiza as propriedades de um campo customizado existente, como rótulo, tipo, obrigatoriedade ou metadados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campo customizado atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campo não encontrado ou dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    CustomFieldResponse update(Long tenantId, Long id, UpdateCustomFieldRequest request);


    @Operation(
            summary = "Ativar ou desativar campo customizado",
            description = "Altera o status de um campo customizado (ativo/inativo), sem removê-lo do sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do campo customizado alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campo não encontrado ou status inválido"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    CustomFieldResponse changeStatus(Long tenantId, Long id, ChangeCustomFieldStatusRequest request);


    @Operation(
            summary = "Remover campo customizado",
            description = "Remove permanentemente um campo customizado. Esta operação pode afetar dados já existentes e deve ser utilizada com cautela."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Campo customizado removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campo não encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    void delete(Long tenantId, Long id);
}
