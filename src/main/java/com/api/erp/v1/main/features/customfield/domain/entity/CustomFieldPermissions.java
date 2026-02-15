package com.api.erp.v1.main.features.customfield.domain.entity;

/**
 * Classe com constantes de permissões para a feature CustomFieldPermissions
 * Seguindo o padrão de autorização do projeto
 */
public final class CustomFieldPermissions {

    private CustomFieldPermissions() {
    }

    public static final String PREFIX = "campos-custom";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String VISUALIZAR = PREFIX + ".visualizar";
    public static final String DELETAR = PREFIX + ".deletar";
}
