//package com.api.erp.v1.features.contato.domain.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Repository para a entidade UsuarioContato
// * Gerencia a associação entre Usuários e Contatos
// */
//@Repository
//public interface UsuarioContatoRepository extends JpaRepository<UsuarioContato, Long> {
//
//    /**
//     * Busca todas as associações de um usuário
//     */
//    List<UsuarioContato> findByUsuarioId(Long usuarioId);
//
//    /**
//     * Busca uma associação específica entre usuário e contato
//     */
//    Optional<UsuarioContato> findByUsuarioIdAndContatoId(Long usuarioId, Long contatoId);
//}
