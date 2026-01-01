package com.api.erp.v1.features.contato.domain.entity;

/**
 * Enum para representar os tipos de contato suportados
 */
public enum TipoContato {
    TELEFONE("Telefone"),
    CELULAR("Celular"),
    EMAIL("Email"),
    WHATSAPP("WhatsApp"),
    LINKEDIN("LinkedIn"),
    INSTAGRAM("Instagram"),
    FACEBOOK("Facebook"),
    WEBSITE("Website"),
    OUTRO("Outro");

    private final String descricao;

    TipoContato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Busca um tipo de contato pela sua descrição
     *
     * @param descricao a descrição do tipo
     * @return o tipo encontrado
     * @throws IllegalArgumentException se o tipo não for encontrado
     */
    public static TipoContato porDescricao(String descricao) {
        for (TipoContato tipo : TipoContato.values()) {
            if (tipo.descricao.equalsIgnoreCase(descricao)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de contato não encontrado: " + descricao);
    }
}
