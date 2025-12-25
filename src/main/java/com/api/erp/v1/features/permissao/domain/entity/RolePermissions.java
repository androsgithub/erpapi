package com.api.erp.v1.features.permissao.domain.entity;

public final class RolePermissions {

    private RolePermissions() {
    }

    public static final String PREFIX = "role";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String VISUALIZAR = PREFIX + ".visualizar";
    public static final String DELETAR = PREFIX + ".deletar";
    public static final String ASSOCIAR = PREFIX + ".associar";
}
