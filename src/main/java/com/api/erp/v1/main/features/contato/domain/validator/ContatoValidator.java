package com.api.erp.v1.main.features.contato.domain.validator;

import com.api.erp.v1.main.features.contato.domain.entity.TipoContato;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;

/**
 * Validador para a entidade Contato
 * Valida as regras de negócio relacionadas aos contatos
 */
public class ContatoValidator {

    private ContatoValidator() {
    }

    /**
     * Valida um contato
     *
     * @param tipo o tipo do contato
     * @param valor o valor do contato
     * @throws BusinessException se alguma validação falhar
     */
    public static void validar(String tipo, String valor) {
        validarTipo(tipo);
        validarValor(valor);
    }

    /**
     * Valida o tipo do contato
     *
     * @param tipo o tipo a validar
     * @throws BusinessException se o tipo for inválido
     */
    private static void validarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BusinessException("Tipo de contato é obrigatório");
        }

        try {
            Enum.valueOf(TipoContato.class, tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de contato inválido: " + tipo);
        }
    }

    /**
     * Valida o valor do contato
     *
     * @param valor o valor a validar
     * @throws BusinessException se o valor for inválido
     */
    private static void validarValor(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("Valor do contato é obrigatório");
        }

        if (valor.length() > 255) {
            throw new BusinessException("Valor do contato não pode ter mais de 255 caracteres");
        }
    }

    /**
     * Valida a descrição do contato
     *
     * @param descricao a descrição a validar
     * @throws BusinessException se a descrição for inválida
     */
    public static void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > 255) {
            throw new BusinessException("Descrição do contato não pode ter mais de 255 caracteres");
        }
    }
}
