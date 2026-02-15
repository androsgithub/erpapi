package com.api.erp.v1.main.features.contato.domain.entity;

/**
 * Classe com constantes de permissões para a feature Contato
 * Seguindo o padrão de autorização do projeto
 */
public final class ContatoPermissions {

    private ContatoPermissions() {
    }

    public static final String PREFIX = "contatos";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String VISUALIZAR = PREFIX + ".visualizar";
    public static final String DELETAR = PREFIX + ".deletar";
}
