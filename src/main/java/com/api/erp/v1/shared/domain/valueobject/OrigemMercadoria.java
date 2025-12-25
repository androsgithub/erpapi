package com.api.erp.v1.shared.domain.valueobject;

import java.util.Objects;

public final class OrigemMercadoria {

    public static final OrigemMercadoria NACIONAL = new OrigemMercadoria(0, "Nacional, exceto as indicadas nos códigos 3, 4, 5 e 8");
    public static final OrigemMercadoria ESTRANGEIRA_IMPORTACAO_DIRETA = new OrigemMercadoria(1, "Estrangeira - Importação direta, exceto a indicada no código 6");
    public static final OrigemMercadoria ESTRANGEIRA_ADQUIRIDA_MERCADO_INTERNO = new OrigemMercadoria(2, "Estrangeira - Adquirida no mercado interno, exceto a indicada no código 7");
    public static final OrigemMercadoria NACIONAL_CONTEUDO_IMPORTACAO_SUPERIOR_40 = new OrigemMercadoria(3, "Nacional, mercadoria ou bem com Conteúdo de Importação superior a 40% e inferior ou igual a 70%");
    public static final OrigemMercadoria NACIONAL_PROCESSOS_PRODUTIVOS_BASICOS = new OrigemMercadoria(4, "Nacional, cuja produção tenha sido feita em conformidade com os processos produtivos básicos");
    public static final OrigemMercadoria NACIONAL_CONTEUDO_IMPORTACAO_INFERIOR_40 = new OrigemMercadoria(5, "Nacional, mercadoria ou bem com Conteúdo de Importação inferior ou igual a 40%");
    public static final OrigemMercadoria ESTRANGEIRA_IMPORTACAO_DIRETA_SEM_SIMILAR = new OrigemMercadoria(6, "Estrangeira - Importação direta, sem similar nacional, constante em lista CAMEX");
    public static final OrigemMercadoria ESTRANGEIRA_ADQUIRIDA_MERCADO_INTERNO_SEM_SIMILAR = new OrigemMercadoria(7, "Estrangeira - Adquirida no mercado interno, sem similar nacional, constante em lista CAMEX");
    public static final OrigemMercadoria NACIONAL_CONTEUDO_IMPORTACAO_SUPERIOR_70 = new OrigemMercadoria(8, "Nacional, mercadoria ou bem com Conteúdo de Importação superior a 70%");

    private final int codigo;
    private final String descricao;

    private OrigemMercadoria(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static OrigemMercadoria doCodigo(int codigo) {
        return switch (codigo) {
            case 0 -> NACIONAL;
            case 1 -> ESTRANGEIRA_IMPORTACAO_DIRETA;
            case 2 -> ESTRANGEIRA_ADQUIRIDA_MERCADO_INTERNO;
            case 3 -> NACIONAL_CONTEUDO_IMPORTACAO_SUPERIOR_40;
            case 4 -> NACIONAL_PROCESSOS_PRODUTIVOS_BASICOS;
            case 5 -> NACIONAL_CONTEUDO_IMPORTACAO_INFERIOR_40;
            case 6 -> ESTRANGEIRA_IMPORTACAO_DIRETA_SEM_SIMILAR;
            case 7 -> ESTRANGEIRA_ADQUIRIDA_MERCADO_INTERNO_SEM_SIMILAR;
            case 8 -> NACIONAL_CONTEUDO_IMPORTACAO_SUPERIOR_70;
            default -> throw new IllegalArgumentException("Código de origem inválido: " + codigo);
        };
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrigemMercadoria that = (OrigemMercadoria) o;
        return codigo == that.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}