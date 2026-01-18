package com.api.erp.v1.observability.domain;

/**
 * Enumeração de status de fluxo para observabilidade.
 * Valores fixos e estáveis para mapeamento numérico.
 * Sem dependências de Spring ou tecnologia externa.
 */
public enum FlowStatus {
    START(0, "Iniciado"),
    SUCCESS(1, "Sucesso"),
    ERROR_VALIDATION(3, "Erro de Validação"),
    ERROR_CONVERSION(4, "Erro de Conversão"),
    ERROR_THROW(5, "Erro Lançado"),
    ERROR_TIMEOUT(6, "Timeout"),
    ERROR_EXTERNAL(7, "Erro Externo"),
    ERROR_SECURITY(8, "Erro de Segurança"),
    ERROR_DATABASE(9, "Erro de Banco de Dados"),
    ERROR_UNKNOWN(127, "Erro Desconhecido");

    private final int code;
    private final String description;

    FlowStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Obtém o status pela chave numérica.
     * Útil para desserialização.
     */
    public static FlowStatus fromCode(int code) {
        for (FlowStatus status : FlowStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return ERROR_UNKNOWN;
    }

    /**
     * Indica se o status representa um sucesso.
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /**
     * Indica se o status representa um erro.
     */
    public boolean isError() {
        return code >= 3;
    }
}
