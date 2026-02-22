package com.api.erp.v1.docs.openapi.features.customer;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.application.dto.response.CustomerCompleteResponseDto;
import com.api.erp.v1.main.features.customer.application.dto.response.CustomerSimpleResponseDto;
import com.api.erp.v1.main.features.customer.domain.controller.ICustomerController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface de documentação OpenAPI para Customer.
 */
@Tag(
        name = "Customers",
        description = "Endpoints responsáveis pela gestão de customers da empresa"
)
public interface CustomerOpenApiDocumentation extends ICustomerController {

    @Override
    @Operation(
            summary = "Listar customers",
            description = "Retorna uma lista paginada de customers com informações resumidas. " +
                    "Utilizado principalmente em telas de listagem e pesquisa."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de customers retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para visualizar customers")
    })
    Page<CustomerSimpleResponseDto> listar(
            @Parameter(description = "Número da página (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Quantidade de registros por página", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Campo utilizado para ordenação", example = "nome")
            @RequestParam(defaultValue = "nome") String sortBy
    );

    @Override
    @Operation(
            summary = "Obter customer por ID",
            description = "Retorna todos os dados de um customer específico, incluindo informações completas e relacionamentos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para visualizar customers"),
            @ApiResponse(responseCode = "404", description = "Customer não encontrado")
    })
    CustomerCompleteResponseDto pegar(
            @Parameter(description = "ID do customer", example = "10")
            Long id
    );

    @Override
    @Operation(
            summary = "Excluir customer",
            description = "Remove definitivamente um customer do sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para excluir customers"),
            @ApiResponse(responseCode = "404", description = "Customer não encontrado")
    })
    void deletar(
            @Parameter(description = "ID do customer", example = "10")
            Long id
    );

    @Override
    @Operation(
            summary = "Criar customer",
            description = "Cria um novo customer no sistema com base nos dados informados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para criar customers")
    })
    CustomerCompleteResponseDto criar(
            CreateCustomerDto dto
    );

    @Override
    @Operation(
            summary = "Atualizar customer",
            description = "Atualiza os dados de um customer existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para atualizar customers"),
            @ApiResponse(responseCode = "404", description = "Customer não encontrado")
    })
    CustomerCompleteResponseDto atualizar(
            @Parameter(description = "ID do customer", example = "10")
            @RequestParam Long id,

            CreateCustomerDto dto
    );
}
