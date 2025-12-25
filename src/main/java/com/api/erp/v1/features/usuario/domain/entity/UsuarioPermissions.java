package com.api.erp.v1.features.usuario.domain.entity;

public final class UsuarioPermissions {

    private UsuarioPermissions() {
    }

    public static final String PREFIX = "usuario";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String LISTAR = PREFIX + ".listar";
    public static final String DELETAR = PREFIX + ".deletar";
    public static final String ATIVAR = PREFIX + ".ativar";
    public static final String DESATIVAR = PREFIX + ".desativar";
    public static final String APROVAR = PREFIX + ".aprovar";
    public static final String REJEITAR = PREFIX + ".rejeitar";
}