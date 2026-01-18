package com.api.erp.v1.shared.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando validações de negócio falham.
 * 
 * Implementa DDD ao representar falhas de validação com contexto de domínio.
 * Usa Strategy para permitir diferentes tipos de validação.
 * Segue Clean Code com responsabilidade única.
 */
public class ValidationException extends RuntimeException {
    private final String field;
    private final HttpStatus status;
    private final String code;

    /**
     * Construtor básico com campo e mensagem.
     * Usa BAD_REQUEST como status padrão.
     */
    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
        this.status = HttpStatus.BAD_REQUEST;
        this.code = "VALIDATION_ERROR";
    }

    /**
     * Construtor completo com controle de status HTTP.
     */
    public ValidationException(String field, String message, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
        this.code = "VALIDATION_ERROR";
    }

    /**
     * Construtor com código de erro customizado para melhor identificação.
     */
    public ValidationException(String field, String message, String code, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
        this.code = code;
    }

    public String getField() {
        return field;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
