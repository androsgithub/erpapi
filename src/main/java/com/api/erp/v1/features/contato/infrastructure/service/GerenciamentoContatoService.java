package com.api.erp.v1.features.contato.infrastructure.service;

import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.application.dto.request.AssociarContatosRequest;
import com.api.erp.v1.features.contato.application.dto.request.RemoverContatoRequest;
import com.api.erp.v1.features.contato.application.mapper.UsuarioContatoMapper;
import com.api.erp.v1.features.contato.domain.entity.Contato;
import com.api.erp.v1.features.contato.domain.entity.TipoContato;
import com.api.erp.v1.features.contato.domain.entity.UsuarioContato;
import com.api.erp.v1.features.contato.domain.repository.ContatoRepository;
import com.api.erp.v1.features.contato.domain.repository.UsuarioContatoRepository;
import com.api.erp.v1.features.contato.domain.service.IGerenciamentoContatoService;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
public class GerenciamentoContatoService implements IGerenciamentoContatoService {

    private final UsuarioContatoRepository usuarioContatoRepository;
    private final ContatoRepository contatoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public UsuarioContato associarContatos(AssociarContatosRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + request.usuarioId()));

        UsuarioContato ultimoUsuarioContato = null;

        // Cria os contatos primeiro
        for (CreateContatoRequest contatoRequest : request.contatos()) {
            Contato contato = Contato.builder()
                    .tipo(TipoContato.valueOf(contatoRequest.tipo().toUpperCase()))
                    .valor(contatoRequest.valor())
                    .descricao(contatoRequest.descricao())
                    .principal(contatoRequest.principal())
                    .ativo(true)
                    .build();

            // Salva o contato
            contato = contatoRepository.save(contato);

            // Cria a associação usuario-contato
            UsuarioContato usuarioContato = UsuarioContato.builder()
                    .usuario(usuario)
                    .contato(contato)
                    .build();

            ultimoUsuarioContato = usuarioContatoRepository.save(usuarioContato);
        }

        // Retorna a última associação criada
        return ultimoUsuarioContato;
    }

    @Override
    @Transactional
    public Contato adicionarContato(Long usuarioId, CreateContatoRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        // Cria o contato
        Contato contato = Contato.builder()
                .tipo(TipoContato.valueOf(request.tipo().toUpperCase()))
                .valor(request.valor())
                .descricao(request.descricao())
                .principal(request.principal())
                .ativo(true)
                .build();

        // Salva o contato
        contato = contatoRepository.save(contato);

        // Cria a associação
        UsuarioContato usuarioContato = UsuarioContato.builder()
                .usuario(usuario)
                .contato(contato)
                .build();

        usuarioContatoRepository.save(usuarioContato);

        return contato;
    }

    @Override
    @Transactional
    public void removerContato(RemoverContatoRequest request) {
        UsuarioContato usuarioContato = usuarioContatoRepository
                .findByUsuarioIdAndContatoId(request.usuarioId(), request.contatoId())
                .orElseThrow(() -> new IllegalArgumentException("Associação não encontrada"));

        usuarioContatoRepository.delete(usuarioContato);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioContato buscarContatosUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        List<UsuarioContato> associacoes = usuarioContatoRepository.findByUsuarioId(usuarioId);

        if (associacoes.isEmpty()) {
            throw new IllegalArgumentException("Usuário não possui contatos cadastrados");
        }

        // Retorna a primeira associação (contém a primeira relação)
        return associacoes.get(0);
    }

    @Override
    @Transactional
    public Contato marcarComoPrincipal(Long usuarioId, Long contatoId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        // Desmarca outros contatos como principais
        List<UsuarioContato> usuarioContatos = usuarioContatoRepository.findByUsuarioId(usuarioId);

        for (UsuarioContato uc : usuarioContatos) {
            if (uc.getContato().getId().equals(contatoId)) {
                uc.getContato().marcarComoPrincipal();
            } else {
                uc.getContato().desmarcarComoPrincipal();
            }
            usuarioContatoRepository.save(uc);
        }

        return contatoRepository.findById(contatoId)
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado"));
    }

    @Override
    @Transactional
    public Contato desativarContato(Long usuarioId, Long contatoId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        Contato contato = contatoRepository.findById(contatoId)
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado"));

        contato.desativar();
        return contatoRepository.save(contato);
    }

    @Override
    @Transactional
    public Contato ativarContato(Long usuarioId, Long contatoId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        Contato contato = contatoRepository.findById(contatoId)
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado"));

        contato.ativar();
        return contatoRepository.save(contato);
    }
}
