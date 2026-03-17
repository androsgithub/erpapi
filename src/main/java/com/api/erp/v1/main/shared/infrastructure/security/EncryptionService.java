package com.api.erp.v1.main.shared.infrastructure.security;

/**
 * Serviço de Encriptação para dados sensíveis
 * 
 * Responsabilidades:
 * - Encriptar dados em plaintext
 * - Descriptografar dados encriptados
 * - Gerenciar chaves de encriptação
 * 
 * Implementações:
 * - AES-256-GCM (padrão)
 * - Jasypt (Spring Boot integration)
 * - HashiCorp Vault (remote key management)
 */
public interface EncryptionService {
    
    /**
     * Encriptografa um valor em plaintext
     * 
     * @param plainValue valor a encriptar
     * @return valor encriptado
     */
    String encrypt(String plainValue);
    
    /**
     * Descriptografa um valor encriptado
     * 
     * @param encryptedValue valor encriptado
     * @return valor em plaintext
     */
    String decrypt(String encryptedValue);
}
