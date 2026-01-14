package com.api.erp.v1.features.produto.domain.entity;

public final class ComposicaoPermissions {

    private ComposicaoPermissions() {
    }

    public static final String PREFIX = "composicao";
    public static final String CRIAR = PREFIX + ".criar";
    public static final String ATUALIZAR = PREFIX + ".atualizar";
    public static final String VISUALIZAR = PREFIX + ".visualizar";
    public static final String DELETAR = PREFIX + ".deletar";
}
