package com.api.erp.v1.main.features.contato.infrastructure.service;

import com.api.erp.v1.main.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.main.features.contato.application.dto.request.RemoverContatoRequest;
import com.api.erp.v1.main.features.contato.domain.entity.Contato;
import com.api.erp.v1.main.features.contato.domain.repository.ContatoRepository;
import com.api.erp.v1.main.features.contato.domain.service.IGerenciamentoContatoService;
import com.api.erp.v1.main.features.usuario.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class GerenciamentoContatoService implements IGerenciamentoContatoService {

    @Autowired
    private ContatoRepository contatoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Contato adicionarContato(Long usuarioId, CreateContatoRequest request) {
//        Usuario usuario = usuarioRepository.findById(usuarioId)
//                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
//
//        // Cria o contato
//        Contato contato = Contato.builder()
//                .tipo(TipoContato.valueOf(request.tipo().toUpperCase()))
//                .valor(request.valor())
//                .descricao(request.descricao())
//                .principal(request.principal())
//                .ativo(true)
//                .build();
//
//        // Salva o contato
//        contato = contatoRepository.save(contato);
//
//        // Cria a associação
//        UsuarioContato usuarioContato = UsuarioContato.builder()
//                .usuario(usuario)
//                .contato(contato)
//                .build();
//
//        usuarioContatoRepository.save(usuarioContato);
//
//        return contato;
        return null;
    }

    @Override
    @Transactional
    public void removerContato(RemoverContatoRequest request) {
//        UsuarioContato usuarioContato = usuarioContatoRepository
//                .findByUsuarioIdAndContatoId(request.usuarioId(), request.contatoId())
//                .orElseThrow(() -> new IllegalArgumentException("Associação não encontrada"));
//
//        usuarioContatoRepository.delete(usuarioContato);
    }

    @Override
    @Transactional
    public Contato marcarComoPrincipal(Long usuarioId, Long contatoId) {
//        usuarioRepository.findById(usuarioId)
//                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
//
//        // Desmarca outros contatos como principais
//        List<UsuarioContato> usuarioContatos = usuarioContatoRepository.findByUsuarioId(usuarioId);
//
//        for (UsuarioContato uc : usuarioContatos) {
//            if (uc.getContato().getId().equals(contatoId)) {
//                uc.getContato().setPrincipal(false);
//            } else {
//                uc.getContato().setPrincipal(true);
//            }
//            usuarioContatoRepository.save(uc);
//        }
//
//        return contatoRepository.findById(contatoId)
//                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado"));
        return null;
    }

    @Override
    @Transactional
    public Contato desativarContato(Long usuarioId, Long contatoId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        Contato contato = contatoRepository.findById(contatoId)
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado"));

        contato.setAtivo(false);
        return contatoRepository.save(contato);
    }

    @Override
    @Transactional
    public Contato ativarContato(Long usuarioId, Long contatoId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        Contato contato = contatoRepository.findById(contatoId)
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado"));

        contato.setAtivo(false);
        return contatoRepository.save(contato);
    }
}
