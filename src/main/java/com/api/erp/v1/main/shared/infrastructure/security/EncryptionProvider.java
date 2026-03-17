package com.api.erp.v1.main.shared.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * EncryptionProvider
 * 
 * Fornece acesso estático e thread-safe ao EncryptionService para uso em entidades JPA
 * que não podem ter dependências via constructor injection ou @Autowired.
 * 
 * Uso:
 * - EncryptionProvider.encrypt(plainPassword)
 * - EncryptionProvider.decrypt(encryptedPassword)
 * 
 * Se nenhum EncryptionService estiver registrado no Spring, usa fallback Base64
 * (desenvolvimento/teste apenas).
 */
@Component
@Slf4j
public class EncryptionProvider {

    private static ApplicationContext applicationContext;
    private static EncryptionService encryptionService;

    /**
     * Inicializa o provider com o ApplicationContext do Spring
     * Chamado automaticamente pelo Spring quando o componente é criado
     */
    public EncryptionProvider(ApplicationContext ctx) {
        applicationContext = ctx;
        initializeEncryptionService();
    }

    /**
     * Inicializa o serviço de criptografia
     * 1. Tenta carregar AES256EncryptionService
     * 2. Se falhar, tenta carregar qualquer EncryptionService registrado
     * 3. Se falhar, usa fallback Base64 com aviso
     */
    private static void initializeEncryptionService() {
        try {
            // Tentar obter AES256EncryptionService (preferido)
            encryptionService = applicationContext.getBean(AES256EncryptionService.class);
            log.info("[ENCRYPTION] AES256EncryptionService inicializado com sucesso");
        } catch (NoSuchBeanDefinitionException e) {
            try {
                // Fallback: qualquer EncryptionService registrado
                encryptionService = applicationContext.getBean(EncryptionService.class);
                log.info("[ENCRYPTION] EncryptionService registrado inicializado: {}", 
                        encryptionService.getClass().getSimpleName());
            } catch (NoSuchBeanDefinitionException e2) {
                // Último recurso: nenhum serviço de criptografia disponível
                log.warn("[ENCRYPTION] Nenhum EncryptionService disponível. " +
                        "Usando fallback Base64. ⚠️ NÃO usar em produção!");
                encryptionService = null;
            }
        }
    }

    /**
     * Encripta um valor plaintext usando o EncryptionService disponível
     * 
     * @param plainValue valor em plaintext
     * @return valor encriptado
     */
    public static String encrypt(String plainValue) {
        if (plainValue == null || plainValue.isEmpty()) {
            return null;
        }

        if (encryptionService != null) {
            try {
                return encryptionService.encrypt(plainValue);
            } catch (Exception e) {
                log.error("[ENCRYPTION] Erro ao encriptar valor", e);
                // Fallback para Base64 em caso de erro
                return fallbackEncrypt(plainValue);
            }
        } else {
            // Usar fallback Base64
            return fallbackEncrypt(plainValue);
        }
    }

    /**
     * Descriptografa um valor encriptado usando o EncryptionService disponível
     * 
     * @param encryptedValue valor encriptado
     * @return valor descriptografado (plaintext)
     */
    public static String decrypt(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            return null;
        }

        if (encryptionService != null) {
            try {
                return encryptionService.decrypt(encryptedValue);
            } catch (Exception e) {
                log.error("[ENCRYPTION] Erro ao descriptografar valor", e);
                // Fallback para Base64 em caso de erro
                return fallbackDecrypt(encryptedValue);
            }
        } else {
            // Usar fallback Base64
            return fallbackDecrypt(encryptedValue);
        }
    }

    /**
     * Encriptação com fallback Base64 (desenvolvimento/teste)
     */
    private static String fallbackEncrypt(String plainValue) {
        try {
            return java.util.Base64.getEncoder().encodeToString(plainValue.getBytes());
        } catch (Exception e) {
            log.error("[ENCRYPTION] Erro no fallback de encriptação", e);
            return plainValue;
        }
    }

    /**
     * Descriptografia com fallback Base64 (desenvolvimento/teste)
     */
    private static String fallbackDecrypt(String encryptedValue) {
        try {
            return new String(java.util.Base64.getDecoder().decode(encryptedValue));
        } catch (IllegalArgumentException e) {
            // Se não for Base64 válido, retorna como está
            return encryptedValue;
        } catch (Exception e) {
            log.error("[ENCRYPTION] Erro no fallback de descriptografia", e);
            return encryptedValue;
        }
    }

    /**
     * Retorna o tipo de serviço de criptografia em uso
     */
    public static String getEncryptionServiceType() {
        if (encryptionService == null) {
            return "Base64 (Fallback - Development Only)";
        }
        return encryptionService.getClass().getSimpleName();
    }

    /**
     * Retorna se um serviço de criptografia real está configurado
     * (não é Base64 fallback)
     */
    public static boolean isRealEncryptionConfigured() {
        return encryptionService != null;
    }
}
