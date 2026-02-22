package com.api.erp.v1.main.features.contact.domain.repository;

import com.api.erp.v1.main.features.contact.domain.entity.Contact;
import com.api.erp.v1.main.features.contact.domain.entity.TipoContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Contact
 * Define as operações de persistência e consulta
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
     * Busca contacts ativos por tipo
     *
     * @param tipo o tipo de contact
     * @return lista de contacts ativos do tipo especificado
     */
    List<Contact> findByTipoAndAtivoTrue(TipoContact tipo);

    /**
     * Busca contacts ativos
     *
     * @return lista de contacts ativos
     */
    List<Contact> findByAtivoTrue();

    /**
     * Busca contacts inativos
     *
     * @return lista de contacts inativos
     */
    List<Contact> findByAtivoFalse();

    /**
     * Busca o contact principal
     *
     * @return o contact marcado como principal
     */
    @Query("SELECT c FROM Contact c WHERE c.principal = true AND c.ativo = true LIMIT 1")
    Optional<Contact> findPrincipal();

    /**
     * Verifica se existe um contact principal
     *
     * @return true se existe um contact principal
     */
    @Query("SELECT COUNT(c) > 0 FROM Contact c WHERE c.principal = true AND c.ativo = true")
    boolean existePrincipalAtivo();

    /**
     * Busca contacts por valor (para validar duplicatas)
     *
     * @param valor o valor do contact
     * @return lista de contacts com esse valor
     */
    List<Contact> findByValor(@Param("valor") String valor);

    /**
     * Busca contacts por valor e tipo (para validar duplicatas)
     *
     * @param valor o valor do contact
     * @param tipo o tipo do contact
     * @return lista de contacts com esse valor e tipo
     */
    List<Contact> findByValorAndTipo(@Param("valor") String valor, @Param("tipo") TipoContact tipo);
}
