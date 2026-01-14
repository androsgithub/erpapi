package com.api.erp.v1.features.contato.infrastructure.service;

import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.domain.entity.Contato;
import com.api.erp.v1.features.contato.domain.entity.TipoContato;
import com.api.erp.v1.features.contato.domain.repository.ContatoRepository;
import com.api.erp.v1.features.contato.domain.service.IContatoService;
import com.api.erp.v1.features.contato.domain.validator.ContatoValidator;
import com.api.erp.v1.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContatoService implements IContatoService {

    @Autowired
    private ContatoRepository contatoRepository;

    @Transactional
    public Contato criar(CreateContatoRequest request) {
        // Validar dados
        ContatoValidator.validar(request.tipo(), request.valor());
        ContatoValidator.validarDescricao(request.descricao());

        // Se o contato é marcado como principal, remover principal dos outros
        if (request.principal()) {
            removerPrincipalExistente();
        }

        // Criar novo contato
        TipoContato tipo = TipoContato.valueOf(request.tipo().toUpperCase());
        Contato contato = new Contato(tipo, request.valor());

        if (request.descricao() != null && !request.descricao().isBlank()) {
            contato.setDescricao(request.descricao());
        }

        contato.setPrincipal(request.principal());
        contato.setCustomData(request.customData());

        return contatoRepository.save(contato);
    }


    @Transactional(readOnly = true)
    public Contato buscarPorId(Long id) {
        return contatoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contato não encontrado com id: " + id));
    }


    @Transactional(readOnly = true)
    public List<Contato> buscarTodos() {
        return contatoRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<Contato> buscarAtivos() {
        return contatoRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Contato> buscarInativos() {
        return contatoRepository.findByAtivoFalse();
    }

    @Transactional(readOnly = true)
    public List<Contato> buscarPorTipo(String tipo) {
        TipoContato tipoContato = TipoContato.valueOf(tipo.toUpperCase());
        return contatoRepository.findByTipoAndAtivoTrue(tipoContato);
    }

    @Transactional(readOnly = true)
    public Contato buscarPrincipal() {
        return contatoRepository.findPrincipal()
                .orElseThrow(() -> new NotFoundException("Nenhum contato principal encontrado"));
    }

    @Transactional
    public Contato atualizar(Long id, CreateContatoRequest request) {
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contato não encontrado com id: " + id));

        // Validar dados
        ContatoValidator.validar(request.tipo(), request.valor());
        ContatoValidator.validarDescricao(request.descricao());

        // Se está marcando como principal, remover principal dos outros
        if (request.principal() && !contato.isPrincipal()) {
            removerPrincipalExistente();
        }

        // Atualizar campos
        TipoContato tipo = TipoContato.valueOf(request.tipo().toUpperCase());
        contato.setTipo(tipo);
        contato.setValor(request.valor());
        contato.setDescricao(request.descricao());
        contato.setPrincipal(request.principal());
        contato.setCustomData(request.customData());

        return contatoRepository.save(contato);
    }

    @Transactional
    public Contato ativar(Long id) {
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contato não encontrado com id: " + id));

        contato.setAtivo(true);
        return contatoRepository.save(contato);
    }

    @Transactional
    public Contato desativar(Long id) {
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contato não encontrado com id: " + id));
        if (contato.isPrincipal()) {
            contato.setPrincipal(false);
        }

        contato.setAtivo(false);
        return contatoRepository.save(contato);
    }

    @Transactional
    public void deletar(Long id) {
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contato não encontrado com id: " + id));

        contatoRepository.deleteById(id);
    }

    @Transactional
    private void removerPrincipalExistente() {
        var principal = contatoRepository.findPrincipal();
        if (principal.isPresent()) {
            principal.get().setPrincipal(false);
            contatoRepository.save(principal.get());
        }
    }
}
