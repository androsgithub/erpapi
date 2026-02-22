package com.api.erp.v1.main.features.user.domain.entity;

public final class UserPermissions {

    private UserPermissions() {
    }

    public static final String PREFIX = "user";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String LISTAR = PREFIX + ".listar";
    public static final String LISTAR_ROLES = PREFIX + ".listar_roles";
    public static final String LISTAR_PERMISSIONS = PREFIX + ".listar_permissions";
    public static final String VISUALIZAR_OUTRO_USER = PREFIX + ".view_outro_user";
    public static final String DELETAR = PREFIX + ".deletar";
    public static final String ATIVAR = PREFIX + ".ativar";
    public static final String DESATIVAR = PREFIX + ".desativar";
    public static final String APROVAR = PREFIX + ".aprovar";
    public static final String REJEITAR = PREFIX + ".rejeitar";

    public static final String GERENCIAR_PERMISSIONS = PREFIX + ".gerenciar.permissions";
    public static final String ADICIONAR_PERMISSION = PREFIX + ".adicionar.permission";
    public static final String REMOVER_PERMISSION = PREFIX + ".remover.permission";

    public static final String GERENCIAR_ROLES = PREFIX + ".gerenciar.roles";
    public static final String ADICIONAR_ROLE = PREFIX + ".adicionar.role";
    public static final String REMOVER_ROLE = PREFIX + ".remover.role";
}