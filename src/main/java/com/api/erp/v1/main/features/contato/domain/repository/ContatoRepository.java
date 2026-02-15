package com.api.erp.v1.main.features.contato.domain.repository;

import com.api.erp.v1.main.features.contato.domain.entity.Contato;
import com.api.erp.v1.main.features.contato.domain.entity.TipoContato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Contato
 * Define as operações de persistência e consulta
 */
@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {

    /**
     * Busca contatos ativos por tipo
     *
     * @param tipo o tipo de contato
     * @return lista de contatos ativos do tipo especificado
     */
    List<Contato> findByTipoAndAtivoTrue(TipoContato tipo);

    /**
     * Busca contatos ativos
     *
     * @return lista de contatos ativos
     */
    List<Contato> findByAtivoTrue();

    /**
     * Busca contatos inativos
     *
     * @return lista de contatos inativos
     */
    List<Contato> findByAtivoFalse();

    /**
     * Busca o contato principal
     *
     * @return o contato marcado como principal
     */
    @Query("SELECT c FROM Contato c WHERE c.principal = true AND c.ativo = true LIMIT 1")
    Optional<Contato> findPrincipal();

    /**
     * Verifica se existe um contato principal
     *
     * @return true se existe um contato principal
     */
    @Query("SELECT COUNT(c) > 0 FROM Contato c WHERE c.principal = true AND c.ativo = true")
    boolean existePrincipalAtivo();

    /**
     * Busca contatos por valor (para validar duplicatas)
     *
     * @param valor o valor do contato
     * @return lista de contatos com esse valor
     */
    List<Contato> findByValor(@Param("valor") String valor);

    /**
     * Busca contatos por valor e tipo (para validar duplicatas)
     *
     * @param valor o valor do contato
     * @param tipo o tipo do contato
     * @return lista de contatos com esse valor e tipo
     */
    List<Contato> findByValorAndTipo(@Param("valor") String valor, @Param("tipo") TipoContato tipo);
}
