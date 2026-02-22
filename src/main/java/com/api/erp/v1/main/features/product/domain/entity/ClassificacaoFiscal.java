package com.api.erp.v1.main.features.product.domain.entity;


import com.api.erp.v1.main.shared.domain.valueobject.*;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Optional;

/**
 * Representa a classificação fiscal de um product conforme regras da NF-e.
 * Contém apenas dados fiscais estáveis, sem informações tributárias dependentes da operação.
 *
 * Esta classe é um Embeddable para ser incorporado na entidade Product.
 * Os Value Objects são serializados para as colunas do banco.
 *
 * A tributação deve ser calculada em outro contexto, considerando:
 * - Esta classificação fiscal
 * - Natureza da operação (CFOP)
 * - UF origem/destino
 * - Regime tributário
 * - Tipo de customer
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassificacaoFiscal {

    @Column(name = "ncm", nullable = false, length = 8)
    private String ncm;

    @Column(name = "descricao_fiscal", nullable = false, length = 120)
    private String descricaoFiscal;

    @Column(name = "origem_mercadoria", nullable = false)
    private Integer origemMercadoria;

    @Column(name = "codigo_beneficio_fiscal", length = 10)
    private String codigoBeneficioFiscal;

    @Column(name = "cest", length = 7)
    private String cest;

    @Column(name = "unidade_tributavel_codigo", nullable = false, length = 6)
    private String unidadeTributavelCodigo;

    @Column(name = "unidade_tributavel_descricao", nullable = false, length = 60)
    private String unidadeTributavelDescricao;

    /**
     * Cria uma nova classificação fiscal
     */
    public static ClassificacaoFiscal criar(
            NCM ncm,
            String descricaoFiscal,
            OrigemMercadoria origem,
            CodigoBeneficioFiscal codigoBeneficio,
            CEST cest,
            UnidadeTributavel unidadeTributavel) {

        validarDescricaoFiscal(descricaoFiscal);

        return new ClassificacaoFiscal(
                ncm.getValor(),
                descricaoFiscal.trim(),
                origem.getCodigo(),
                codigoBeneficio != null ? codigoBeneficio.getValor() : null,
                cest != null ? cest.getValor() : null,
                unidadeTributavel.getCodigo(),
                unidadeTributavel.getDescricao()
        );
    }

    /**
     * Cria classificação fiscal sem benefício e CEST
     */
    public static ClassificacaoFiscal criar(
            NCM ncm,
            String descricaoFiscal,
            OrigemMercadoria origem,
            UnidadeTributavel unidadeTributavel) {

        return criar(ncm, descricaoFiscal, origem, null, null, unidadeTributavel);
    }

    private static void validarDescricaoFiscal(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição fiscal é obrigatória");
        }
        if (descricao.length() > 120) {
            throw new IllegalArgumentException("Descrição fiscal não pode ter mais de 120 caracteres");
        }
    }

    /**
     * Retorna o NCM como Value Object
     */
    public NCM obterNCM() {
        return NCM.de(this.ncm);
    }

    /**
     * Retorna a origem da mercadoria como Value Object
     */
    public OrigemMercadoria obterOrigemMercadoria() {
        return OrigemMercadoria.doCodigo(this.origemMercadoria);
    }

    /**
     * Retorna o código de benefício fiscal se existir
     */
    public Optional<CodigoBeneficioFiscal> obterCodigoBeneficioFiscal() {
        return codigoBeneficioFiscal != null && !codigoBeneficioFiscal.isBlank()
                ? Optional.of(CodigoBeneficioFiscal.de(codigoBeneficioFiscal))
                : Optional.empty();
    }

    /**
     * Retorna o CEST se existir
     */
    public Optional<CEST> obterCEST() {
        return cest != null && !cest.isBlank()
                ? Optional.of(CEST.de(cest))
                : Optional.empty();
    }

    /**
     * Retorna a unidade tributável como Value Object
     */
    public UnidadeTributavel obterUnidadeTributavel() {
        return UnidadeTributavel.de(unidadeTributavelCodigo, unidadeTributavelDescricao);
    }

    /**
     * Atualiza a classificação fiscal
     */
    public ClassificacaoFiscal atualizar(
            NCM ncm,
            String descricaoFiscal,
            OrigemMercadoria origem,
            CodigoBeneficioFiscal codigoBeneficio,
            CEST cest,
            UnidadeTributavel unidadeTributavel) {

        return criar(ncm, descricaoFiscal, origem, codigoBeneficio, cest, unidadeTributavel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassificacaoFiscal that = (ClassificacaoFiscal) o;
        return Objects.equals(ncm, that.ncm) &&
                Objects.equals(origemMercadoria, that.origemMercadoria) &&
                Objects.equals(codigoBeneficioFiscal, that.codigoBeneficioFiscal) &&
                Objects.equals(cest, that.cest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ncm, origemMercadoria, codigoBeneficioFiscal, cest);
    }

    @Override
    public String toString() {
        return "ClassificacaoFiscal{" +
                "ncm='" + ncm + '\'' +
                ", descricaoFiscal='" + descricaoFiscal + '\'' +
                ", origemMercadoria=" + origemMercadoria +
                ", codigoBeneficioFiscal='" + codigoBeneficioFiscal + '\'' +
                ", cest='" + cest + '\'' +
                ", unidadeTributavel='" + unidadeTributavelCodigo + '\'' +
                '}';
    }
}