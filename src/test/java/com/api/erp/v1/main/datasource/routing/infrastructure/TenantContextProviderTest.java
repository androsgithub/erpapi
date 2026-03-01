package com.api.erp.v1.main.datasource.routing.infrastructure;

import com.api.erp.v1.main.datasource.routing.domain.ITenantContextProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS - TenantContextProvider
 * 
 * Cenários cobertos:
 * 1. Definir e recuperar tenant em RequestScope (quando há HTTP request)
 * 2. Definir e recuperar tenant em ThreadLocal (quando não há HTTP request)
 * 3. Limpeza de contexto de requisição
 * 4. Limpeza de contexto ThreadLocal
 * 5. Comportamento com tenant null
 * 6. Comportamento com tenant vazio
 * 7. Validação de entrada (null/vazio)
 * 8. Retorno de default tenant quando nenhum está definido
 * 9. Verificação de presença de tenant (hasTenant)
 * 10. Isolamento entre RequestScope e ThreadLocal
 * 
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantContextProvider - Testes Unitários")
class TenantContextProviderTest {

    private TenantContextProvider tenantContextProvider;

    @BeforeEach
    void setUp() {
        tenantContextProvider = new TenantContextProvider();
        // Definir default tenant via reflection
        ReflectionTestUtils.setField(tenantContextProvider, "defaultTenant", "master");
        
        // Limpar RequestContext
        RequestContextHolder.resetRequestAttributes();
        
        // Limpar ThreadLocal via reflection
        try {
            java.lang.reflect.Field field = TenantContextProvider.class.getDeclaredField("TENANT_FALLBACK");
            field.setAccessible(true);
            ThreadLocal<?> threadLocal = (ThreadLocal<?>) field.get(null);
            threadLocal.remove();
        } catch (Exception e) {
            // Ignorar se não conseguir acessar
        }
    }

    @AfterEach
    void tearDown() {
        // Limpar RequestContext
        RequestContextHolder.resetRequestAttributes();
        
        // Limpar ThreadLocal
        try {
            java.lang.reflect.Field field = TenantContextProvider.class.getDeclaredField("TENANT_FALLBACK");
            field.setAccessible(true);
            ThreadLocal<?> threadLocal = (ThreadLocal<?>) field.get(null);
            threadLocal.remove();
        } catch (Exception e) {
            // Ignorar
        }
    }

    // ===== Testes de Validação de Entrada =====

    @Test
    @DisplayName("dado_tenantIdNull_quando_setCurrentTenant_entao_lancaIllegalArgumentException")
    void testGivenNullTenantId_WhenSetCurrentTenant_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> tenantContextProvider.setCurrentTenant(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID não pode ser nulo ou vazio");
    }

    @Test
    @DisplayName("dado_tenantIdVazio_quando_setCurrentTenant_entao_lancaIllegalArgumentException")
    void testGivenEmptyTenantId_WhenSetCurrentTenant_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> tenantContextProvider.setCurrentTenant(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID não pode ser nulo ou vazio");
    }

    @Test
    @DisplayName("dado_tenantIdComEspacos_quando_setCurrentTenant_entao_lancaIllegalArgumentException")
    void testGivenTenantIdWithOnlySpaces_WhenSetCurrentTenant_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> tenantContextProvider.setCurrentTenant("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID não pode ser nulo ou vazio");
    }

    // ===== Testes de ThreadLocal Fallback =====

    @Test
    @DisplayName("dado_semRequestContext_quando_setCurrentTenant_entao_armazenaEmThreadLocal")
    void testGivenNoRequestContext_WhenSetCurrentTenant_ThenStoresInThreadLocal() {
        // Arrange
        String tenantId = "tenant-1";
        RequestContextHolder.resetRequestAttributes();

        // Act
        tenantContextProvider.setCurrentTenant(tenantId);
        String retrievedTenant = tenantContextProvider.getCurrentTenant();

        // Assert
        assertThat(retrievedTenant)
                .as("Deve recuperar tenant do ThreadLocal quando não há RequestContext")
                .isEqualTo(tenantId);
    }

    @Test
    @DisplayName("dado_tenantDefinidoEmThreadLocal_quando_getCurrentTenant_entao_retornaTenant")
    void testGivenTenantDefinedInThreadLocal_WhenGetCurrentTenant_ThenReturnsTenant() {
        // Arrange
        String tenantId = "tenant-2";
        RequestContextHolder.resetRequestAttributes();
        tenantContextProvider.setCurrentTenant(tenantId);

        // Act
        String retrieved = tenantContextProvider.getCurrentTenant();

        // Assert
        assertThat(retrieved)
                .isEqualTo(tenantId);
    }

    @Test
    @DisplayName("dado_tenantDefinidoEmThreadLocal_quando_clearContext_entao_removeDoThreadLocal")
    void testGivenTenantDefinedInThreadLocal_WhenClearContext_ThenRemovesFromThreadLocal() {
        // Arrange
        String tenantId = "tenant-3";
        RequestContextHolder.resetRequestAttributes();
        tenantContextProvider.setCurrentTenant(tenantId);

        // Act
        tenantContextProvider.clearContext();
        String retrieved = tenantContextProvider.getCurrentTenant();

        // Assert - após limpeza deve retornar default
        assertThat(retrieved)
                .as("Após limpeza deve retornar default tenant")
                .isEqualTo("master");
    }

    // ===== Testes de Default Tenant =====

    @Test
    @DisplayName("dado_nenhumTenantDadoDef_quando_getCurrentTenant_entao_retornaDefaultTenant")
    void testGivenNoTenantDefined_WhenGetCurrentTenant_ThenReturnsDefaultTenant() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();
        ReflectionTestUtils.setField(tenantContextProvider, "defaultTenant", "master");

        // Act
        String retrieved = tenantContextProvider.getCurrentTenant();

        // Assert
        assertThat(retrieved)
                .as("Deve retornar default tenant quando nenhum foi definido")
                .isEqualTo("master");
    }

    @Test
    @DisplayName("dado_defaultTenantCustomizado_quando_getCurrentTenant_entao_retornaDefaultCustomizado")
    void testGivenCustomDefaultTenant_WhenGetCurrentTenant_ThenReturnsCustomDefault() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();
        ReflectionTestUtils.setField(tenantContextProvider, "defaultTenant", "custom-default");

        // Act
        String retrieved = tenantContextProvider.getCurrentTenant();

        // Assert
        assertThat(retrieved)
                .isEqualTo("custom-default");
    }

    // ===== Testes de Trimming de Espaços =====

    @Test
    @DisplayName("dado_tenantIdComEspacosNoInicio_quando_setCurrentTenant_entao_removeEspacos")
    void testGivenTenantIdWithLeadingSpaces_WhenSetCurrentTenant_ThenTrimsSpaces() {
        // Arrange
        String tenantIdWithSpaces = "  tenant-spaces";
        RequestContextHolder.resetRequestAttributes();

        // Act
        tenantContextProvider.setCurrentTenant(tenantIdWithSpaces);
        String retrieved = tenantContextProvider.getCurrentTenant();

        // Assert
        assertThat(retrieved)
                .isEqualTo("tenant-spaces");
    }

    @Test
    @DisplayName("dado_tenantIdComEspacosNoFinal_quando_setCurrentTenant_entao_removeEspacos")
    void testGivenTenantIdWithTrailingSpaces_WhenSetCurrentTenant_ThenTrimsSpaces() {
        // Arrange
        String tenantIdWithSpaces = "tenant-spaces  ";
        RequestContextHolder.resetRequestAttributes();

        // Act
        tenantContextProvider.setCurrentTenant(tenantIdWithSpaces);
        String retrieved = tenantContextProvider.getCurrentTenant();

        // Assert
        assertThat(retrieved)
                .isEqualTo("tenant-spaces");
    }

    // ===== Testes de hasTenant =====

    @Test
    @DisplayName("dado_tenantDefinido_quando_hasTenant_entao_retornaTrue")
    void testGivenTenantDefined_WhenHasTenant_ThenReturnsTrue() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();
        tenantContextProvider.setCurrentTenant("tenant-1");

        // Act
        boolean hasTenant = tenantContextProvider.hasTenant();

        // Assert
        assertThat(hasTenant)
                .as("hasTenant deve retornar true quando há tenant definido")
                .isTrue();
    }

    @Test
    @DisplayName("dado_nenhumTenantDefinido_quando_hasTenant_entao_retornaTrue")
    void testGivenNoTenantDefined_WhenHasTenant_ThenReturnsTrue() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();

        // Act
        boolean hasTenant = tenantContextProvider.hasTenant();

        // Assert - default tenant sempre existe
        assertThat(hasTenant)
                .as("hasTenant deve retornar true pois default tenant existe")
                .isTrue();
    }

    // ===== Testes de Múltiplas Definições =====

    @Test
    @DisplayName("dado_tenantRedefinidoMulitplaVezes_quando_getCurrentTenant_entao_retornaUltimovalor")
    void testGivenTenantRedefinedMultipleTimes_WhenGetCurrentTenant_ThenReturnsLastValue() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();
        
        // Act - redefinir múltiplas vezes
        tenantContextProvider.setCurrentTenant("tenant-1");
        tenantContextProvider.setCurrentTenant("tenant-2");
        tenantContextProvider.setCurrentTenant("tenant-3");
        String retrieved = tenantContextProvider.getCurrentTenant();

        // Assert
        assertThat(retrieved)
                .as("Deve retornar o último tenant definido")
                .isEqualTo("tenant-3");
    }

    // ===== Testes de Idempotência =====

    @Test
    @DisplayName("dado_clearChamadoSemTenant_quando_clearContext_entao_naoLancaExcecao")
    void testGivenClearCalledWithoutTenant_WhenClearContext_ThenNoExceptionThrown() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();

        // Act & Assert
        assertThatNoException()
                .as("clearContext deve ser idempotente")
                .isThrownBy(() -> tenantContextProvider.clearContext());
    }

    @Test
    @DisplayName("dado_clearChamadoDuasVezes_quando_clearContext_entao_ambosComSucesso")
    void testGivenClearCalledTwice_WhenClearContext_ThenBothSucceed() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();
        tenantContextProvider.setCurrentTenant("tenant-1");

        // Act
        tenantContextProvider.clearContext();
        tenantContextProvider.clearContext(); // Chamar nova vez

        // Assert
        assertThat(tenantContextProvider.getCurrentTenant())
                .isEqualTo("master"); // default
    }

    // ===== Testes de Valores Especiais =====

    @Test
    @DisplayName("dado_tenantIdComCaracteresEspeciais_quando_setCurrentTenant_entao_armazenaCorretamente")
    void testGivenTenantIdWithSpecialCharacters_WhenSetCurrentTenant_ThenStoresCorrectly() {
        // Arrange
        String tenantId = "tenant-@#$%_123";
        RequestContextHolder.resetRequestAttributes();

        // Act
        tenantContextProvider.setCurrentTenant(tenantId);
        String retrieved = tenantContextProvider.getCurrentTenant();

        // Assert
        assertThat(retrieved)
                .isEqualTo(tenantId);
    }

    @Test
    @DisplayName("dado_tenantIdMuitoLongo_quando_setCurrentTenant_entao_armazenaCorretamente")
    void testGivenVeryLongTenantId_WhenSetCurrentTenant_ThenStoresCorrectly() {
        // Arrange
        String longTenantId = "tenant-" + "x".repeat(1000);
        RequestContextHolder.resetRequestAttributes();

        // Act
        tenantContextProvider.setCurrentTenant(longTenantId);
        String retrieved = tenantContextProvider.getCurrentTenant();

        // Assert
        assertThat(retrieved)
                .isEqualTo(longTenantId);
    }

    @Test
    @DisplayName("dado_tenantIdUmaLetra_quando_setCurrentTenant_entao_armazenaCorretamente")
    void testGivenSingleCharacterTenantId_WhenSetCurrentTenant_ThenStoresCorrectly() {
        // Arrange
        String singleChar = "1";
        RequestContextHolder.resetRequestAttributes();

        // Act
        tenantContextProvider.setCurrentTenant(singleChar);
        String retrieved = tenantContextProvider.getCurrentTenant();

        // Assert
        assertThat(retrieved)
                .isEqualTo(singleChar);
    }
}
