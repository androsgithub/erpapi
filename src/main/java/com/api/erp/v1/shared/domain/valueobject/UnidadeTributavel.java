package com.api.erp.v1.shared.domain.valueobject;

import java.util.Objects;

public final class UnidadeTributavel {

    public static final UnidadeTributavel UNIDADE = new UnidadeTributavel("UN", "Unidade");
    public static final UnidadeTributavel QUILOGRAMA = new UnidadeTributavel("KG", "Quilograma");
    public static final UnidadeTributavel METRO = new UnidadeTributavel("M", "Metro");
    public static final UnidadeTributavel LITRO = new UnidadeTributavel("L", "Litro");
    public static final UnidadeTributavel CAIXA = new UnidadeTributavel("CX", "Caixa");
    public static final UnidadeTributavel DUZIA = new UnidadeTributavel("DZ", "Dúzia");
    public static final UnidadeTributavel PACOTE = new UnidadeTributavel("PC", "Pacote");

    private final String codigo;
    private final String descricao;

    private UnidadeTributavel(String codigo, String descricao) {
        this.codigo = validarCodigo(codigo);
        this.descricao = validarDescricao(descricao);
    }

    public static UnidadeTributavel de(String codigo, String descricao) {
        return new UnidadeTributavel(codigo, descricao);
    }

    private String validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código da unidade tributável não pode ser vazio");
        }
        String codigoLimpo = codigo.trim().toUpperCase();
        if (codigoLimpo.length() > 6) {
            throw new IllegalArgumentException("Código da unidade tributável não pode ter mais de 6 caracteres");
        }
        return codigoLimpo;
    }

    private String validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição da unidade tributável não pode ser vazia");
        }
        return descricao.trim();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnidadeTributavel that = (UnidadeTributavel) o;
        return Objects.equals(codigo, that.codigo);
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