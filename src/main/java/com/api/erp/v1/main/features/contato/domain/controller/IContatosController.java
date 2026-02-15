package com.api.erp.v1.main.features.contato.domain.controller;

import com.api.erp.v1.main.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.main.features.contato.application.dto.response.ContatoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IContatosController {

    public ResponseEntity<ContatoResponse> criar(@RequestBody CreateContatoRequest request);

    public ResponseEntity<ContatoResponse> buscar(Long id);

    public ResponseEntity<List<ContatoResponse>> listar();

    public ResponseEntity<List<ContatoResponse>> listarAtivos();

    public ResponseEntity<List<ContatoResponse>> listarInativos();

    public ResponseEntity<List<ContatoResponse>> listarPorTipo(String tipo);

    public ResponseEntity<ContatoResponse> buscarPrincipal();

    public ResponseEntity<ContatoResponse> atualizar(Long id, @RequestBody CreateContatoRequest request);

    public ResponseEntity<ContatoResponse> ativar(Long id);

    public ResponseEntity<ContatoResponse> desativar(Long id);

    public ResponseEntity<Void> deletar(Long id);
}
