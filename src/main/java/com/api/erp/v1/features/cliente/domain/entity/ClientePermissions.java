package com.api.erp.v1.features.cliente.domain.entity;

/**
 * Classe com constantes de permissões para a feature Cliente
 * Seguindo o padrão de autorização do projeto
 */
public final class ClientePermissions {

    private ClientePermissions() {
    }

    public static final String PREFIX = "cliente";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String VISUALIZAR = PREFIX + ".visualizar";
    public static final String DELETAR = PREFIX + ".deletar";
}
