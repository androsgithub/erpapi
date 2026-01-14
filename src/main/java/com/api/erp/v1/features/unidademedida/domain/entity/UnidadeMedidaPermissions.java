package com.api.erp.v1.features.unidademedida.domain.entity;

public final class UnidadeMedidaPermissions {

    private UnidadeMedidaPermissions() {
    }

    public static final String PREFIX = "unidade-medida";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String VISUALIZAR = PREFIX + ".visualizar";
    public static final String DELETAR = PREFIX + ".deletar";
    public static final String ATIVAR = PREFIX + ".ativar";
    public static final String DESATIVAR = PREFIX + ".desativar";
}