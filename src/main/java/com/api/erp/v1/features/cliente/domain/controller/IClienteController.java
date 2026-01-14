package com.api.erp.v1.features.cliente.domain.controller;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.application.dto.response.ClienteCompleteResponseDto;
import com.api.erp.v1.features.cliente.application.dto.response.ClienteSimpleResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(
        name = "Clientes",
        description = "Endpoints responsáveis pela gestão de clientes da empresa"
)
public interface IClienteController {

    @Operation(
            summary = "Listar clientes",
            description = "Retorna uma lista paginada de clientes com informações resumidas. " +
                    "Utilizado principalmente em telas de listagem e pesquisa."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para visualizar clientes")
    })
    Page<ClienteSimpleResponseDto> listar(
            @Parameter(description = "Número da página (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Quantidade de registros por página", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Campo utilizado para ordenação", example = "nome")
            @RequestParam(defaultValue = "nome") String sortBy
    );


    @Operation(
            summary = "Obter cliente por ID",
            description = "Retorna todos os dados de um cliente específico, incluindo informações completas e relacionamentos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para visualizar clientes"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    ClienteCompleteResponseDto pegar(
            @Parameter(description = "ID do cliente", example = "10")
            Long id
    );


    @Operation(
            summary = "Excluir cliente",
            description = "Remove definitivamente um cliente do sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para excluir clientes"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    void deletar(
            @Parameter(description = "ID do cliente", example = "10")
            Long id
    );


    @Operation(
            summary = "Criar cliente",
            description = "Cria um novo cliente no sistema com base nos dados informados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para criar clientes")
    })
    ClienteCompleteResponseDto criar(
            CreateClienteDto dto
    );


    @Operation(
            summary = "Atualizar cliente",
            description = "Atualiza os dados de um cliente existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para atualizar clientes"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    ClienteCompleteResponseDto atualizar(
            @Parameter(description = "ID do cliente", example = "10")
            @RequestParam Long id,

            CreateClienteDto dto
    );
}
