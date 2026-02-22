package com.api.erp.v1.main.features.contact.domain.entity;

/**
 * Classe com constantes de permissões para a feature Contact
 * Seguindo o padrão de autorização do projeto
 */
public final class ContactPermissions {

    private ContactPermissions() {
    }

    public static final String PREFIX = "contacts";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String VISUALIZAR = PREFIX + ".visualizar";
    public static final String DELETAR = PREFIX + ".deletar";
}
