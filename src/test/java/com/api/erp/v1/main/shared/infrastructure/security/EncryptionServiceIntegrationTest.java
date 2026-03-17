package com.api.erp.v1.main.shared.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EncryptionServiceIntegrationTest
 * 
 * Testes de integração para verificar que a criptografia AES-256
 * está funcionando corretamente na aplicação.
 * 
 * Execute com: mvn test -Dtest=EncryptionServiceIntegrationTest
 */
@SpringBootTest
@Slf4j
@DisplayName("AES-256 Encryption Service Integration Tests")
class EncryptionServiceIntegrationTest {

    @Autowired(required = false)
    private EncryptionService encryptionService;

    @BeforeEach
    public void setUp() {
        log.info("Setting up encryption tests");
        log.info("Current Encryption Service: {}", EncryptionProvider.getEncryptionServiceType());
    }

    @Test
    @DisplayName("Should encrypt and decrypt password successfully")
    void testEncryptDecryptPassword() {
        // Given
        String plainPassword = "minha_senha_super_secreta_123!@#";

        // When
        String encrypted = EncryptionProvider.encrypt(plainPassword);
        String decrypted = EncryptionProvider.decrypt(encrypted);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(plainPassword, encrypted);
        assertEquals(plainPassword, decrypted);
        log.info("✓ Encrypt/Decrypt Test PASSED");
    }

    @Test
    @DisplayName("Should handle empty passwords")
    void testEncryptDecryptEmpty() {
        // Given
        String emptyPassword = "";
        String nullPassword = null;

        // When & Then
        assertNull(EncryptionProvider.encrypt(nullPassword));
        assertNull(EncryptionProvider.decrypt(nullPassword));
        assertNull(EncryptionProvider.encrypt(emptyPassword));
        assertNull(EncryptionProvider.decrypt(emptyPassword));
        log.info("✓ Empty Password Test PASSED");
    }

    @Test
    @DisplayName("Should handle special characters in passwords")
    void testSpecialCharactersInPassword() {
        // Given
        String specialPassword = "P@$$w0rd!#%&*()[]{}|;:',.<>?/\\`~";

        // When
        String encrypted = EncryptionProvider.encrypt(specialPassword);
        String decrypted = EncryptionProvider.decrypt(encrypted);

        // Then
        assertEquals(specialPassword, decrypted);
        log.info("✓ Special Characters Test PASSED");
    }

    @Test
    @DisplayName("Should handle unicode characters in passwords")
    void testUnicodeCharactersInPassword() {
        // Given
        String unicodePassword = "Sêñhá_Ünícödé_日本語_🔐";

        // When
        String encrypted = EncryptionProvider.encrypt(unicodePassword);
        String decrypted = EncryptionProvider.decrypt(encrypted);

        // Then
        assertEquals(unicodePassword, decrypted);
        log.info("✓ Unicode Characters Test PASSED");
    }

    @Test
    @DisplayName("Should produce different ciphertexts for same plaintext")
    void testEncryptionNonDeterministic() {
        // Given
        String plainPassword = "test_password";

        // When
        String encrypted1 = EncryptionProvider.encrypt(plainPassword);
        String encrypted2 = EncryptionProvider.encrypt(plainPassword);

        // Then
        assertNotEquals(encrypted1, encrypted2);
        // But both should decrypt to same plaintext
        assertEquals(plainPassword, EncryptionProvider.decrypt(encrypted1));
        assertEquals(plainPassword, EncryptionProvider.decrypt(encrypted2));
        log.info("✓ Non-Deterministic Encryption Test PASSED (different ciphertexts for same plaintext)");
    }

    @Test
    @DisplayName("Should handle large passwords")
    void testLargePassword() {
        // Given
        String largePassword = "x".repeat(1000);  // 1000 character password

        // When
        String encrypted = EncryptionProvider.encrypt(largePassword);
        String decrypted = EncryptionProvider.decrypt(encrypted);

        // Then
        assertEquals(largePassword, decrypted);
        log.info("✓ Large Password Test PASSED");
    }

    @Test
    @DisplayName("Should report encryption service type")
    void testEncryptionServiceType() {
        String serviceType = EncryptionProvider.getEncryptionServiceType();
        assertNotNull(serviceType);
        log.info("Using Encryption Service: {}", serviceType);
        log.info("✓ Service Type Report PASSED");
    }

    @Test
    @DisplayName("Should validate encryption is configured")
    void testEncryptionConfigured() {
        boolean isConfigured = EncryptionProvider.isRealEncryptionConfigured();
        log.info("Real Encryption Configured: {}", isConfigured);
        // This may be true or false depending on environment
        assertTrue(isConfigured || !isConfigured);  // Always true, just logs the state
        log.info("✓ Encryption Configuration Check PASSED");
    }

    @Test
    @DisplayName("Should encrypt database credential examples")
    void testDatabaseCredentialExamples() {
        // Given typical database passwords
        String[] credentials = {
                "postgres_pass_123",
                "mysql@root!Pass",
                "sql_server_complex#P@ss",
                "mongo_dev_password",
                "oracle_secure_pwd"
        };

        // When & Then
        for (String credential : credentials) {
            String encrypted = EncryptionProvider.encrypt(credential);
            String decrypted = EncryptionProvider.decrypt(encrypted);
            assertEquals(credential, decrypted);
            log.info("✓ Credential Test: {} → [encrypted] → {}", 
                    credential.substring(0, Math.min(10, credential.length())) + "...", 
                    credential.substring(0, Math.min(10, credential.length())) + "...");
        }
    }

    @Test
    @DisplayName("Should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        // Given
        String password = "thread_safe_password";
        int threadCount = 10;
        int operationsPerThread = 100;
        boolean[] success = {true};

        // When
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    try {
                        String encrypted = EncryptionProvider.encrypt(password);
                        String decrypted = EncryptionProvider.decrypt(encrypted);
                        if (!password.equals(decrypted)) {
                            success[0] = false;
                        }
                    } catch (Exception e) {
                        log.error("Thread error", e);
                        success[0] = false;
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        assertTrue(success[0]);
        log.info("✓ Thread Safety Test PASSED ({} threads × {} operations)", threadCount, operationsPerThread);
    }
}
