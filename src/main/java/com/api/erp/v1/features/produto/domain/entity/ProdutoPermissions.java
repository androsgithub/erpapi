package com.api.erp.v1.features.produto.domain.entity;

public final class ProdutoPermissions {

    private ProdutoPermissions() {
    }

    public static final String PREFIX = "produto";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String VISUALIZAR = PREFIX + ".visualizar";
    public static final String DELETAR = PREFIX + ".deletar";
    public static final String ATIVAR = PREFIX + ".ativar";
    public static final String DESATIVAR = PREFIX + ".desativar";
    public static final String BLOQUEAR = PREFIX + ".bloquear";
    public static final String DESCONTINUAR = PREFIX + ".descontinuar";
}
