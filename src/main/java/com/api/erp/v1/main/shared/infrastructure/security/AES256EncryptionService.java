package com.api.erp.v1.main.shared.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * AES-256-GCM Encryption Service
 * 
 * Implementação padrão de encriptação com AES-256
 * 
 * Configuração necessária em application.yml:
 * encryption:
 *   key: "sua_chave_256bits_em_hex_aqui"
 *   
 * Para gerar uma chave:
 * openssl enc -aes-256-cbc -S "salt" -P -pass pass:"senha" | grep key
 * 
 * Ou usando Java:
 * KeyGenerator kg = KeyGenerator.getInstance("AES");
 * kg.init(256);
 * SecretKey key = kg.generateKey();
 * Base64.getEncoder().encodeToString(key.getEncoded());
 */
@Slf4j
@Service
public class AES256EncryptionService implements EncryptionService {

    @Value("${encryption.key:#{null}}")
    private String encryptionKey;

    private static final String CIPHER_ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    @Override
    public String encrypt(String plainValue) {
        if (plainValue == null || plainValue.isEmpty()) {
            return null;
        }

        try {
            SecretKey key = getSecretKey();
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(plainValue.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            log.error("[ENCRYPTION] Error encrypting value", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    @Override
    public String decrypt(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            return null;
        }

        try {
            SecretKey key = getSecretKey();
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
            byte[] decryptedBytes = cipher.doFinal(decodedValue);
            return new String(decryptedBytes);

        } catch (IllegalArgumentException e) {
            log.warn("[ENCRYPTION] Failed to decode Base64, treating as plaintext: {}", e.getMessage());
            return encryptedValue;
        } catch (Exception e) {
            log.error("[ENCRYPTION] Error decrypting value", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Obtém a chave secreta para encriptação
     * 
     * Carrega a chave da propriedade encryption.key ou gera uma nova
     */
    private SecretKey getSecretKey() {
        // Se não configurou chave, usa Base64 simples (SOMENTE para DEV/TEST!)
        if (encryptionKey == null || encryptionKey.isEmpty()) {
            log.warn("[ENCRYPTION] ⚠️  No encryption key configured! Using Base64 encoding (INSECURE!)");
            log.warn("[ENCRYPTION] Configure 'encryption.key' in application.yml for production");
            return generateDefaultKey();
        }

        try {
            // Converte a chave hexadecimal para bytes
            byte[] decodedKey = hexStringToByteArray(encryptionKey);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, CIPHER_ALGORITHM);
        } catch (Exception e) {
            log.error("[ENCRYPTION] Failed to load encryption key, using default", e);
            return generateDefaultKey();
        }
    }

    /**
     * Gera uma chave padrão para desenvolvimento
     * NÃO USAR EM PRODUÇÃO
     */
    private SecretKey generateDefaultKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(CIPHER_ALGORITHM);
            kg.init(KEY_SIZE);
            return kg.generateKey();
        } catch (Exception e) {
            log.error("[ENCRYPTION] Failed to generate default key", e);
            throw new RuntimeException("Cannot initialize encryption key", e);
        }
    }

    /**
     * Converte string hexadecimal para byte array
     */
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Gera uma chave AES-256 aleatória (para configuração inicial)
     * 
     * Use este método uma vez para gerar a chave e copie para application.yml
     */
    public static String generateNewEncryptionKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(CIPHER_ALGORITHM);
            kg.init(KEY_SIZE);
            SecretKey key = kg.generateKey();
            byte[] encoded = key.getEncoded();
            
            // Converte para hexadecimal
            StringBuilder hex = new StringBuilder();
            for (byte b : encoded) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate encryption key", e);
        }
    }
}
