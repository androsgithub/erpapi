package com.api.erp.v1.main.features.usuario.domain.entity;

public final class UsuarioPermissions {

    private UsuarioPermissions() {
    }

    public static final String PREFIX = "usuario";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String LISTAR = PREFIX + ".listar";
    public static final String LISTAR_ROLES = PREFIX + ".listar_roles";
    public static final String LISTAR_PERMISSOES = PREFIX + ".listar_permissoes";
    public static final String VISUALIZAR_OUTRO_USUARIO = PREFIX + ".view_outro_usuario";
    public static final String DELETAR = PREFIX + ".deletar";
    public static final String ATIVAR = PREFIX + ".ativar";
    public static final String DESATIVAR = PREFIX + ".desativar";
    public static final String APROVAR = PREFIX + ".aprovar";
    public static final String REJEITAR = PREFIX + ".rejeitar";

    public static final String GERENCIAR_PERMISSOES = PREFIX + ".gerenciar.permissoes";
    public static final String ADICIONAR_PERMISSAO = PREFIX + ".adicionar.permissao";
    public static final String REMOVER_PERMISSAO = PREFIX + ".remover.permissao";

    public static final String GERENCIAR_ROLES = PREFIX + ".gerenciar.roles";
    public static final String ADICIONAR_ROLE = PREFIX + ".adicionar.role";
    public static final String REMOVER_ROLE = PREFIX + ".remover.role";
}