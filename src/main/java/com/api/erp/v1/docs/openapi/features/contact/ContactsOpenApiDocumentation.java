package com.api.erp.v1.docs.openapi.features.contact;

import com.api.erp.v1.main.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.features.contact.domain.controller.IContactsController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Interface de documentação OpenAPI para Contacts.
 */
@Tag(name = "Contacts", description = "Gestão de Contacts - Operações Básicas")
public interface ContactsOpenApiDocumentation extends IContactsController {

    @Override
    @Operation(summary = "Criar novo contact", description = "Cria um novo contact no sistema (requer autenticação)")
    @ApiResponse(responseCode = "201", description = "Contact criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    ResponseEntity<ContactResponse> criar(@RequestBody CreateContactRequest request);

    @Override
    @Operation(summary = "Buscar contact por ID", description = "Busca um contact específico (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Contact encontrado")
    @ApiResponse(responseCode = "404", description = "Contact não encontrado")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    ResponseEntity<ContactResponse> buscar(
            @Parameter(description = "ID do contact") Long id);

    @Override
    @Operation(summary = "Listar todos os contacts", description = "Lista todos os contacts cadastrados (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de contacts")
    ResponseEntity<List<ContactResponse>> listar();

    @Override
    @Operation(summary = "Listar contacts ativos", description = "Lista todos os contacts ativos (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de contacts ativos")
    ResponseEntity<List<ContactResponse>> listarAtivos();

    @Override
    @Operation(summary = "Listar contacts inativos", description = "Lista todos os contacts inativos (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de contacts inativos")
    ResponseEntity<List<ContactResponse>> listarInativos();

    @Override
    @Operation(summary = "Listar contacts por tipo", description = "Lista contacts de um tipo específico (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Lista de contacts")
    @ApiResponse(responseCode = "400", description = "Tipo de contact inválido")
    ResponseEntity<List<ContactResponse>> listarPorTipo(
            @Parameter(description = "Tipo de contact (TELEFONE, EMAIL, WHATSAPP, etc.)") String tipo);

    @Override
    @Operation(summary = "Buscar contact principal", description = "Busca o contact marcado como principal (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Contact principal encontrado")
    @ApiResponse(responseCode = "404", description = "Nenhum contact principal encontrado")
    ResponseEntity<ContactResponse> buscarPrincipal();

    @Override
    @Operation(summary = "Atualizar contact", description = "Atualiza dados de um contact (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Contact atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Contact não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    ResponseEntity<ContactResponse> atualizar(
            @Parameter(description = "ID do contact") Long id,
            @RequestBody CreateContactRequest request);

    @Override
    @Operation(summary = "Ativar contact", description = "Ativa um contact desativado (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Contact ativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Contact não encontrado")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    ResponseEntity<ContactResponse> ativar(
            @Parameter(description = "ID do contact") Long id);

    @Override
    @Operation(summary = "Desativar contact", description = "Desativa um contact (soft delete) (requer autenticação)")
    @ApiResponse(responseCode = "200", description = "Contact desativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Contact não encontrado")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    ResponseEntity<ContactResponse> desativar(
            @Parameter(description = "ID do contact") Long id);

    @Override
    @Operation(summary = "Deletar contact", description = "Remove um contact do sistema permanentemente (requer autenticação)")
    @ApiResponse(responseCode = "204", description = "Contact deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Contact não encontrado")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    ResponseEntity<Void> deletar(
            @Parameter(description = "ID do contact") Long id);
}
