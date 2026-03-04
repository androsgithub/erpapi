package com.api.erp.v1.docs.openapi.features.contact;

import com.api.erp.v1.main.features.contact.application.dto.CreateContactRequest;
import com.api.erp.v1.main.features.contact.application.dto.request.AssociarContactsRequest;
import com.api.erp.v1.main.features.contact.application.dto.request.RemoverContactRequest;
import com.api.erp.v1.main.features.contact.application.dto.response.ContactResponse;
import com.api.erp.v1.main.features.contact.application.dto.response.UserContactsResponse;
import com.api.erp.v1.main.features.contact.domain.controller.IContactsUserController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Interface de documentação OpenAPI para Contacts de User.
 */
@Tag(name = "Contacts do Usuário", description = "Gestão de Contacts - Operações de Usuário")
public interface ContactsUserOpenApiDocumentation extends IContactsUserController {

    @Override
    @Operation(summary = "Associar contacts a usuário",
            description = "Associa múltiplos contacts a um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contacts associados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<UserContactsResponse> associarContacts(
            @RequestBody AssociarContactsRequest request);

    @Override
    @Operation(summary = "Adicionar contact a usuário",
            description = "Adiciona um novo contact a um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contact adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContactResponse> adicionarContact(
            @Parameter(description = "ID do usuário") Long userId,
            @RequestBody CreateContactRequest request);

    @Override
    @Operation(summary = "Buscar contacts de usuário",
            description = "Busca todos os contacts associados a um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contacts encontrados"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado ou sem contacts"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<UserContactsResponse> buscarContactsUser(
            @Parameter(description = "ID do usuário") Long userId);

    @Override
    @Operation(summary = "Remover contact de usuário",
            description = "Remove um contact associado a um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contact removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou contact não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Void> removerContact(
            @RequestBody RemoverContactRequest request);

    @Override
    @Operation(summary = "Marcar contact como principal",
            description = "Marca um contact como principal (apenas um contact por usuário) (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact marcado como principal"),
            @ApiResponse(responseCode = "404", description = "Usuário ou contact não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContactResponse> marcarComoPrincipal(
            @Parameter(description = "ID do usuário") Long userId,
            @Parameter(description = "ID do contact") Long contactId);

    @Override
    @Operation(summary = "Desativar contact do usuário",
            description = "Desativa um contact de um usuário (soft delete) (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou contact não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContactResponse> desativarContact(
            @Parameter(description = "ID do usuário") Long userId,
            @Parameter(description = "ID do contact") Long contactId);

    @Override
    @Operation(summary = "Ativar contact do usuário",
            description = "Ativa um contact desativado de um usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou contact não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<ContactResponse> ativarContact(
            @Parameter(description = "ID do usuário") Long userId,
            @Parameter(description = "ID do contact") Long contactId);
}
