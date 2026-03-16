package com.api.erp.v1.main.dynamic.features.contact.domain.entity;

/**
 * Enum para representar os tipos de contact suportados
 */
public enum TipoContact {
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

    TipoContact(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Busca um tipo de contact pela sua descrição
     *
     * @param descricao a descrição do tipo
     * @return o tipo encontrado
     * @throws IllegalArgumentException se o tipo não for encontrado
     */
    public static TipoContact porDescricao(String descricao) {
        for (TipoContact tipo : TipoContact.values()) {
            if (tipo.descricao.equalsIgnoreCase(descricao)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Contact type not found: " + descricao);
    }
}
