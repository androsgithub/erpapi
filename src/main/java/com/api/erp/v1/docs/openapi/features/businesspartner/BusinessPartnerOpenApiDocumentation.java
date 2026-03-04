package com.api.erp.v1.docs.openapi.features.businesspartner;

import com.api.erp.v1.main.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.features.businesspartner.application.dto.response.BusinessPartnerCompleteResponseDto;
import com.api.erp.v1.main.features.businesspartner.application.dto.response.BusinessPartnerSimpleResponseDto;
import com.api.erp.v1.main.features.businesspartner.domain.controller.IBusinessPartnerController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface de documentação OpenAPI para BusinessPartner.
 */
@Tag(
        name = "BusinessPartners",
        description = "Endpoints responsáveis pela gestão de businesspartners da empresa"
)
public interface BusinessPartnerOpenApiDocumentation extends IBusinessPartnerController {

    @Override
    @Operation(
            summary = "Listar businesspartners",
            description = "Retorna uma lista paginada de businesspartners com informações resumidas. " +
                    "Utilizado principalmente em telas de listagem e pesquisa."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de businesspartners retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para visualizar businesspartners")
    })
    Page<BusinessPartnerSimpleResponseDto> listar(
            @Parameter(description = "Número da página (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Quantidade de registros por página", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Campo utilizado para ordenação", example = "nome")
            @RequestParam(defaultValue = "nome") String sortBy
    );

    @Override
    @Operation(
            summary = "Obter businesspartner por ID",
            description = "Retorna todos os dados de um businesspartner específico, incluindo informações completas e relacionamentos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "BusinessPartner encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para visualizar businesspartners"),
            @ApiResponse(responseCode = "404", description = "BusinessPartner não encontrado")
    })
    BusinessPartnerCompleteResponseDto pegar(
            @Parameter(description = "ID do businesspartner", example = "10")
            Long id
    );

    @Override
    @Operation(
            summary = "Excluir businesspartner",
            description = "Remove definitivamente um businesspartner do sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "BusinessPartner removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para excluir businesspartners"),
            @ApiResponse(responseCode = "404", description = "BusinessPartner não encontrado")
    })
    void deletar(
            @Parameter(description = "ID do businesspartner", example = "10")
            Long id
    );

    @Override
    @Operation(
            summary = "Criar businesspartner",
            description = "Cria um novo businesspartner no sistema com base nos dados informados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "BusinessPartner criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para criar businesspartners")
    })
    BusinessPartnerCompleteResponseDto criar(
            CreateBusinessPartnerDto dto
    );

    @Override
    @Operation(
            summary = "Atualizar businesspartner",
            description = "Atualiza os dados de um businesspartner existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "BusinessPartner atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para atualizar businesspartners"),
            @ApiResponse(responseCode = "404", description = "BusinessPartner não encontrado")
    })
    BusinessPartnerCompleteResponseDto atualizar(
            @Parameter(description = "ID do businesspartner", example = "10")
            @RequestParam Long id,

            CreateBusinessPartnerDto dto
    );
}
