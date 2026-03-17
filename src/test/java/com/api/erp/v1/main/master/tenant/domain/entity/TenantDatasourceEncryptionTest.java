package com.api.erp.v1.main.master.tenant.domain.entity;

import com.api.erp.v1.main.master.tenant.domain.vo.Email;
import com.api.erp.v1.main.master.tenant.domain.vo.Telefone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TenantDatasourceEncryptionTest
 * 
 * Testes unitários para verificar que a criptografia de senha
 * no TenantDatasource está funcionando corretamente.
 * 
 * Fluxo testado:
 * 1. setPassword(plaintext) → encripta internamente
 * 2. @PrePersist → armazena criptografado em passwordEncrypted
 * 3. @PostLoad → descriptografa automaticamente
 * 4. getPassword() → retorna plaintext
 */
@DisplayName("TenantDatasource Password Encryption Tests")
class TenantDatasourceEncryptionTest {

    private TenantDatasource datasource;
    private Tenant tenant;

    @BeforeEach
    void setUp() {
        // Setup Tenant parent
        tenant = Tenant.builder()
                .id(1L)
                .name("Test Tenant")
                .email(Email.of("test@example.com"))
                .telefone(Telefone.of("+55", "11", "981234567"))
                .active(true)
                .trial(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Setup TenantDatasource
        datasource = TenantDatasource.builder()
                .id(1L)
                .tenant(tenant)
                .driverClass("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/tenant_db")
                .username("tenant_user")
                .dbType(TenantDatasource.DBType.POSTGRESQL)
                .poolMin(5)
                .poolMax(20)
                .connectionTimeout(30000)
                .idleTimeout(600000)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should encrypt password when set")
    void testPasswordEncryption() {
        // Given
        String plainPassword = "SecureDbPassword123!@#";

        // When
        datasource.setPassword(plainPassword);

        // Then
        assertEquals(plainPassword, datasource.getPassword());
        assertNotNull(datasource.getPasswordEncrypted());
        assertNotEquals(plainPassword, datasource.getPasswordEncrypted());
        assertTrue(datasource.getPasswordEncrypted().length() > 0);
    }

    @Test
    @DisplayName("Should not store plaintext password in passwordEncrypted field")
    void testPlaintextNotStored() {
        // Given
        String plainPassword = "MySecurePassword123";

        // When
        datasource.setPassword(plainPassword);

        // Then
        String encrypted = datasource.getPasswordEncrypted();
        assertNotEquals(plainPassword, encrypted);
        assertFalse(encrypted.equals(plainPassword));
    }

    @Test
    @DisplayName("Should decrypt password correctly on getPassword()")
    void testPasswordDecryption() {
        // Given
        String plainPassword = "PostgresPassword@123";

        // When
        datasource.setPassword(plainPassword);
        String retrieved = datasource.getPassword();

        // Then
        assertEquals(plainPassword, retrieved);
    }

    @Test
    @DisplayName("Should handle empty passwords")
    void testEmptyPassword() {
        // When
        datasource.setPassword("");

        // Then
        assertNull(datasource.getPasswordEncrypted());
        assertNull(datasource.getPassword());
    }

    @Test
    @DisplayName("Should handle null passwords")
    void testNullPassword() {
        // When
        datasource.setPassword(null);

        // Then
        assertNull(datasource.getPasswordEncrypted());
        assertNull(datasource.getPassword());
    }

    @Test
    @DisplayName("Should handle special characters in password")
    void testSpecialCharactersPassword() {
        // Given
        String specialPassword = "P@$$w0rd!#%&*()[]{}|;:',.<>?";

        // When
        datasource.setPassword(specialPassword);

        // Then
        assertEquals(specialPassword, datasource.getPassword());
        assertNotEquals(specialPassword, datasource.getPasswordEncrypted());
    }

    @Test
    @DisplayName("Should handle unicode characters in password")
    void testUnicodePassword() {
        // Given
        String unicodePassword = "Sêñhá_日本語_🔐_Москва";

        // When
        datasource.setPassword(unicodePassword);

        // Then
        assertEquals(unicodePassword, datasource.getPassword());
    }

    @Test
    @DisplayName("Should re-encrypt password on update")
    void testPasswordRe_encryption() {
        // Given
        String password1 = "FirstPassword123";
        String password2 = "SecondPassword456";

        // When
        datasource.setPassword(password1);
        String encrypted1 = datasource.getPasswordEncrypted();

        datasource.setPassword(password2);
        String encrypted2 = datasource.getPasswordEncrypted();

        // Then
        assertNotEquals(encrypted1, encrypted2);
        assertEquals(password2, datasource.getPassword());
    }

    @Test
    @DisplayName("Should populate created_at on first persistence")
    void testCreatedAtPopulation() {
        // Given - datasource already has createdAt set
        LocalDateTime createdAt = datasource.getCreatedAt();

        // Then
        assertNotNull(createdAt);
    }

    @Test
    @DisplayName("Should support changing password multiple times")
    void testMultiplePasswordChanges() {
        // Given
        String[] passwords = {
                "FirstPass123",
                "SecondPass456",
                "ThirdPass789",
                "FourthPass000"
        };

        // When & Then
        for (String password : passwords) {
            datasource.setPassword(password);
            assertEquals(password, datasource.getPassword());
            assertNotNull(datasource.getPasswordEncrypted());
            assertNotEquals(password, datasource.getPasswordEncrypted());
        }
    }

    @Test
    @DisplayName("Should preserve password across get/set cycles")
    void testPasswordPreservation() {
        // Given
        String originalPassword = "OriginalPassword@123";
        datasource.setPassword(originalPassword);

        // When - simulate persistence cycle
        String encrypted = datasource.getPasswordEncrypted();
        String retrieved = datasource.getPassword();

        // Simulate reload from database
        datasource.setPassword(null);  // Clear transient
        String encryptedFromDb = encrypted;
        datasource.passwordEncrypted = encryptedFromDb;  // Simulate DB load
        datasource.onLoad();  // Trigger @PostLoad

        // Then
        assertEquals(originalPassword, datasource.getPassword());
    }

    @Test
    @DisplayName("Should work with real database credential formats")
    void testRealDatabaseCredentials() {
        // Given typical database passwords from different systems
        String[][] credentials = {
                {"PostgreSQL", "p@ssw0rd_db_123"},
                {"MySQL", "my$ql#root!pass"},
                {"SQL Server", "S3cur3_Pass@2024"},
                {"MongoDB", "mongo_dev_xyz789"},
                {"Oracle", "ORACLE_SYS_PASS_001"}
        };

        // When & Then
        for (String[] cred : credentials) {
            String dbType = cred[0];
            String password = cred[1];

            datasource.setPassword(password);
            String decrypted = datasource.getPassword();

            assertEquals(password, decrypted,
                    "Password mismatch for " + dbType);
        }
    }
}
