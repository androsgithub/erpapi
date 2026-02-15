package com.api.erp.v1.main.features.contato.domain.controller;

import com.api.erp.v1.main.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.main.features.contato.application.dto.request.AssociarContatosRequest;
import com.api.erp.v1.main.features.contato.application.dto.request.RemoverContatoRequest;
import com.api.erp.v1.main.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.main.features.contato.application.dto.response.UsuarioContatosResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IContatosUsuarioController {

    ResponseEntity<UsuarioContatosResponse> associarContatos(
            @RequestBody AssociarContatosRequest request);

    ResponseEntity<ContatoResponse> adicionarContato(
            Long usuarioId,
            @RequestBody CreateContatoRequest request);

    ResponseEntity<UsuarioContatosResponse> buscarContatosUsuario(
            Long usuarioId);

    ResponseEntity<Void> removerContato(
            @RequestBody RemoverContatoRequest request);

    ResponseEntity<ContatoResponse> marcarComoPrincipal(
            Long usuarioId,
            Long contatoId);

    ResponseEntity<ContatoResponse> desativarContato(
            Long usuarioId,
            Long contatoId);

    ResponseEntity<ContatoResponse> ativarContato(
            Long usuarioId,
            Long contatoId);
}
