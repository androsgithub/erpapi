package com.api.erp.v1.main.features.contact.domain.validator;

import com.api.erp.v1.main.features.contact.domain.entity.TipoContact;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;

/**
 * Validator for the Contact entity
 * Validates business rules related to contacts
 */
public class ContactValidator {

    private ContactValidator() {
    }

    /**
     * Validates a contact
     *
     * @param tipo the contact type
     * @param valor the contact value
     * @throws BusinessException if any validation fails
     */
    public static void validar(String tipo, String valor) {
        validarTipo(tipo);
        validarValor(valor);
    }

    /**
     * Validates the contact type
     *
     * @param tipo the type to validate
     * @throws BusinessException if the type is invalid
     */
    private static void validarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BusinessException("Contact type is required");
        }

        try {
            Enum.valueOf(TipoContact.class, tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid contact type: " + tipo);
        }
    }

    /**
     * Validates the contact value
     *
     * @param valor the value to validate
     * @throws BusinessException if the value is invalid
     */
    private static void validarValor(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("Contact value is required");
        }

        if (valor.length() > 255) {
            throw new BusinessException("Contact value cannot exceed 255 characters");
        }
    }

    /**
     * Validates the contact description
     *
     * @param descricao the description to validate
     * @throws BusinessException if the description is invalid
     */
    public static void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > 255) {
            throw new BusinessException("Contact description cannot exceed 255 characters");
        }
    }
}
