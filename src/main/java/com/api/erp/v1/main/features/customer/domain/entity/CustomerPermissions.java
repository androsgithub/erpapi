package com.api.erp.v1.main.features.customer.domain.entity;

/**
 * Classe com constantes de permissões para a feature Customer
 * Seguindo o padrão de autorização do projeto
 */
public final class CustomerPermissions {

    private CustomerPermissions() {
    }

    public static final String PREFIX = "customer";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String VISUALIZAR = PREFIX + ".visualizar";
    public static final String DELETAR = PREFIX + ".deletar";
}
