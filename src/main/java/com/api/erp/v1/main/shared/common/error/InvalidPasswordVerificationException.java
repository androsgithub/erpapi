package com.api.erp.v1.main.shared.common.error;

/**
 * InvalidPasswordVerificationException
 * 
 * Exceção lançada quando a tentativa de verificação de senha falha.
 * 
 * Uso: Quando o usuário tenta atualizar um datasource mas fornece
 * uma senha atual (currentPassword) que não corresponde à senha 
 * armazenada no banco de dados.
 * 
 * Resposta HTTP: 400 Bad Request
 * Mensagem: "Current password is invalid. Please verify and try again."
 */
public class InvalidPasswordVerificationException extends RuntimeException {

    public InvalidPasswordVerificationException() {
        super("Current password is invalid. Please verify and try again.");
    }

    public InvalidPasswordVerificationException(String message) {
        super(message);
    }

    public InvalidPasswordVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
