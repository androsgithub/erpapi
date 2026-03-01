package com.api.erp.v1.main.datasource.routing.cache;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS - DSCache
 * 
 * Cenários cobertos:
 * 1. Adicionar DataSource ao cache
 * 2. Recuperar DataSource do cache
 * 3. Verificar existência no cache
 * 4. Remover DataSource do cache
 * 5. Fechamento de HikariDataSource ao remover
 * 6. Não fechar DataSource que não é HikariDataSource
 * 7. Recuperar todos os DataSources (respeitando unmodifiable)
 * 8. Cache deve ser thread-safe (ConcurrentHashMap)
 * 9. Múltiplas operações sequenciais
 * 10. Comportamento com null
 * 11. Get em cache vazio retorna null
 * 12. Remove em cache vazio retorna null
 * 
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DSCache - Testes Unitários")
class DSCacheTest {

    private DSCache dsCache;

    @Mock
    private DataSource dataSource;

    @Mock
    private HikariDataSource hikariDataSource;

    @BeforeEach
    void setUp() {
        dsCache = new DSCache();
    }

    // ===== Testes de Put =====

    @Test
    @DisplayName("dado_tenantIdEDataSource_quando_put_entao_adicionaAoCache")
    void testGivenTenantIdAndDataSource_WhenPut_ThenAddsToCache() {
        // Arrange
        Long tenantId = 1L;

        // Act
        dsCache.put(tenantId, dataSource);

        // Assert
        assertThat(dsCache.contains(tenantId))
                .as("Cache deve conter o tenant após put")
                .isTrue();
    }

    @Test
    @DisplayName("dado_multiplosDataSources_quando_putVarios_entao_armazenaUm")
    void testGivenMultipleDataSources_WhenPutVarious_ThenStoresMultiple() {
        // Arrange
        Long tenant1 = 1L;
        Long tenant2 = 2L;
        Long tenant3 = 3L;
        DataSource ds1 = mock(DataSource.class);
        DataSource ds2 = mock(DataSource.class);
        DataSource ds3 = mock(DataSource.class);

        // Act
        dsCache.put(tenant1, ds1);
        dsCache.put(tenant2, ds2);
        dsCache.put(tenant3, ds3);

        // Assert
        assertThat(dsCache.contains(tenant1)).isTrue();
        assertThat(dsCache.contains(tenant2)).isTrue();
        assertThat(dsCache.contains(tenant3)).isTrue();
        assertThat(dsCache.get(tenant1)).isEqualTo(ds1);
        assertThat(dsCache.get(tenant2)).isEqualTo(ds2);
        assertThat(dsCache.get(tenant3)).isEqualTo(ds3);
    }

    // ===== Testes de Get =====

    @Test
    @DisplayName("dado_tenantEmCache_quando_get_entao_retornaDataSource")
    void testGivenTenantInCache_WhenGet_ThenReturnsDataSource() {
        // Arrange
        Long tenantId = 1L;
        dsCache.put(tenantId, dataSource);

        // Act
        DataSource retrieved = dsCache.get(tenantId);

        // Assert
        assertThat(retrieved)
                .as("Deve retornar DataSource armazenado em cache")
                .isEqualTo(dataSource);
    }

    @Test
    @DisplayName("dado_tenantNaoEmCache_quando_get_entao_retornaNull")
    void testGivenTenantNotInCache_WhenGet_ThenReturnsNull() {
        // Act
        DataSource retrieved = dsCache.get(999L);

        // Assert
        assertThat(retrieved)
                .as("Deve retornar null quando tenant não está em cache")
                .isNull();
    }

    @Test
    @DisplayName("dado_cacheVazio_quando_get_entao_retornaNull")
    void testGivenEmptyCache_WhenGet_ThenReturnsNull() {
        // Act
        DataSource retrieved = dsCache.get(1L);

        // Assert
        assertThat(retrieved).isNull();
    }

    // ===== Testes de Contains =====

    @Test
    @DisplayName("dado_tenantEmCache_quando_contains_entao_retornaTrue")
    void testGivenTenantInCache_WhenContains_ThenReturnsTrue() {
        // Arrange
        Long tenantId = 1L;
        dsCache.put(tenantId, dataSource);

        // Act
        boolean contains = dsCache.contains(tenantId);

        // Assert
        assertThat(contains)
                .as("Contains deve retornar true para tenant em cache")
                .isTrue();
    }

    @Test
    @DisplayName("dado_tenantNaoEmCache_quando_contains_entao_retornaFalse")
    void testGivenTenantNotInCache_WhenContains_ThenReturnsFalse() {
        // Act
        boolean contains = dsCache.contains(999L);

        // Assert
        assertThat(contains)
                .as("Contains deve retornar false para tenant não em cache")
                .isFalse();
    }

    @Test
    @DisplayName("dado_cacheVazio_quando_contains_entao_retornaFalse")
    void testGivenEmptyCache_WhenContains_ThenReturnsFalse() {
        // Act
        boolean contains = dsCache.contains(1L);

        // Assert
        assertThat(contains).isFalse();
    }

    // ===== Testes de Remove =====

    @Test
    @DisplayName("dado_hikariDataSourceEmCache_quando_remove_entao_fechaERetorna")
    void testGivenHikariDataSourceInCache_WhenRemove_ThenClosesAndReturns() {
        // Arrange
        Long tenantId = 1L;
        dsCache.put(tenantId, hikariDataSource);

        // Act
        DataSource removed = dsCache.remove(tenantId);

        // Assert
        assertThat(removed)
                .as("Remove deve retornar DataSource removido")
                .isEqualTo(hikariDataSource);
        
        // Verificar que foi fechado
        verify(hikariDataSource, times(1)).close();
        
        // Verificar que foi removido do cache
        assertThat(dsCache.contains(tenantId))
                .as("Tenant não deve estar em cache após remove")
                .isFalse();
    }

    @Test
    @DisplayName("dado_dataSourceNaoHikariEmCache_quando_remove_entao_retornaEmNaoFecha")
    void testGivenNonHikariDataSourceInCache_WhenRemove_ThenReturnsAndDoesNotClose() {
        // Arrange
        Long tenantId = 1L;
        dsCache.put(tenantId, dataSource);

        // Act
        DataSource removed = dsCache.remove(tenantId);

        // Assert
        assertThat(removed)
                .isEqualTo(dataSource);
        
        // Não deve chamar close() em DataSource que não é HikariDataSource
        verify(hikariDataSource, never()).close();
        
        // Deve estar removido
        assertThat(dsCache.contains(tenantId)).isFalse();
    }

    @Test
    @DisplayName("dado_tenantNaoEmCache_quando_remove_entao_retornaNull")
    void testGivenTenantNotInCache_WhenRemove_ThenReturnsNull() {
        // Act
        DataSource removed = dsCache.remove(999L);

        // Assert
        assertThat(removed)
                .as("Remove deve retornar null quando tenant não está em cache")
                .isNull();
    }

    @Test
    @DisplayName("dado_cacheVazio_quando_remove_entao_retornaNull")
    void testGivenEmptyCache_WhenRemove_ThenReturnsNull() {
        // Act
        DataSource removed = dsCache.remove(1L);

        // Assert
        assertThat(removed).isNull();
    }

    // ===== Testes de GetAll =====

    @Test
    @DisplayName("dado_cacheMuliplasEntradas_quando_getAll_entao_retornaTodas")
    void testGivenCacheWithMultipleEntries_WhenGetAll_ThenReturnsAll() {
        // Arrange
        Long tenant1 = 1L;
        Long tenant2 = 2L;
        Long tenant3 = 3L;
        DataSource ds1 = mock(DataSource.class);
        DataSource ds2 = mock(DataSource.class);
        DataSource ds3 = mock(DataSource.class);

        dsCache.put(tenant1, ds1);
        dsCache.put(tenant2, ds2);
        dsCache.put(tenant3, ds3);

        // Act
        Map<Long, DataSource> allDataSources = dsCache.getAll();

        // Assert
        assertThat(allDataSources)
                .as("GetAll deve retornar todos os DataSources")
                .hasSize(3)
                .containsEntry(tenant1, ds1)
                .containsEntry(tenant2, ds2)
                .containsEntry(tenant3, ds3);
    }

    @Test
    @DisplayName("dado_cacheVazio_quando_getAll_entao_retornaMapaVazio")
    void testGivenEmptyCache_WhenGetAll_ThenReturnsEmptyMap() {
        // Act
        Map<Long, DataSource> allDataSources = dsCache.getAll();

        // Assert
        assertThat(allDataSources)
                .as("GetAll deve retornar mapa vazio")
                .isEmpty();
    }

    @Test
    @DisplayName("dado_mapRetornadoPorGetAll_quando_modificarMapa_entao_naoAfetaCache")
    void testGivenMapReturnedByGetAll_WhenModifyMap_ThenDoesNotAffectCache() {
        // Arrange
        Long tenant1 = 1L;
        DataSource ds1 = mock(DataSource.class);
        dsCache.put(tenant1, ds1);

        // Act
        Map<Long, DataSource> allDataSources = dsCache.getAll();
        // Tentar adicionar ao mapa retornado
        try {
            allDataSources.put(999L, mock(DataSource.class));
        } catch (UnsupportedOperationException e) {
            // Esperado - mapa é unmodifiable
        }

        // Assert
        assertThat(dsCache.contains(999L))
                .as("Cache não deve ser afetado por modificações no mapa retornado")
                .isFalse();
    }

    // ===== Testes de Thread Safety =====

    @Test
    @DisplayName("dado_cacheEhConcurrentHashMap_quando_usarEmMultiplosThreads_entao_naoLancaExcecao")
    void testGivenCacheIsConcurrentHashMap_WhenUseInMultipleThreads_ThenNoExceptionThrown() throws InterruptedException {
        // Arrange
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                dsCache.put((long) i, mock(DataSource.class));
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 10; i < 20; i++) {
                dsCache.put((long) i, mock(DataSource.class));
            }
        });

        // Act & Assert
        assertThatCode(() -> {
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
        }).doesNotThrowAnyException();

        // Verificar que todas as entradas foram adicionadas
        assertThat(dsCache.getAll()).hasSize(20);
    }

    // ===== Testes de Múltiplas Operações =====

    @Test
    @DisplayName("dado_sequenciaOperacoes_quando_put_get_contains_remove_entao_funcionamCorretos")
    void testGivenSequenceOfOperations_WhenPutGetContainsRemove_ThenFunctionCorrectly() {
        // Arrange
        Long tenantId = 1L;

        // Act & Assert
        // Inicialmente não está em cache
        assertThat(dsCache.contains(tenantId)).isFalse();

        // Adicionar ao cache
        dsCache.put(tenantId, dataSource);
        assertThat(dsCache.contains(tenantId)).isTrue();

        // Recuperar
        assertThat(dsCache.get(tenantId)).isEqualTo(dataSource);

        // Remover
        DataSource removed = dsCache.remove(tenantId);
        assertThat(removed).isEqualTo(dataSource);
        assertThat(dsCache.contains(tenantId)).isFalse();

        // Ao tentar recuperar novamente deve retornar null
        assertThat(dsCache.get(tenantId)).isNull();
    }

    @Test
    @DisplayName("dado_putSobrescreveValorAnterior_quando_putComMesmoTenant_entao_sobrescreve")
    void testGivenPutOverwritesPreviousValue_WhenPutWithSameTenant_ThenOverwrites() {
        // Arrange
        Long tenantId = 1L;
        DataSource ds1 = mock(DataSource.class);
        DataSource ds2 = mock(DataSource.class);

        // Act
        dsCache.put(tenantId, ds1);
        assertThat(dsCache.get(tenantId)).isEqualTo(ds1);

        dsCache.put(tenantId, ds2);
        DataSource retrieved = dsCache.get(tenantId);

        // Assert
        assertThat(retrieved)
                .as("Novo DataSource deve sobrescrever o anterior")
                .isEqualTo(ds2)
                .isNotEqualTo(ds1);
    }

    // ===== Testes com Valores Especiais =====

    @Test
    @DisplayName("dado_tenantIdZero_quando_put_entao_armazenaCorretamente")
    void testGivenTenantIdZero_WhenPut_ThenStoresCorrectly() {
        // Arrange
        Long tenantId = 0L;

        // Act
        dsCache.put(tenantId, dataSource);

        // Assert
        assertThat(dsCache.contains(tenantId)).isTrue();
        assertThat(dsCache.get(tenantId)).isEqualTo(dataSource);
    }

    @Test
    @DisplayName("dado_tenantIdNegativo_quando_put_entao_armazenaCorretamente")
    void testGivenNegativeTenantId_WhenPut_ThenStoresCorrectly() {
        // Arrange
        Long tenantId = -1L;

        // Act
        dsCache.put(tenantId, dataSource);

        // Assert
        assertThat(dsCache.contains(tenantId)).isTrue();
        assertThat(dsCache.get(tenantId)).isEqualTo(dataSource);
    }

    @Test
    @DisplayName("dado_tenantIdMuitoGrande_quando_put_entao_armazenaCorretamente")
    void testGivenVeryLargeTenantId_WhenPut_ThenStoresCorrectly() {
        // Arrange
        Long tenantId = Long.MAX_VALUE;

        // Act
        dsCache.put(tenantId, dataSource);

        // Assert
        assertThat(dsCache.contains(tenantId)).isTrue();
        assertThat(dsCache.get(tenantId)).isEqualTo(dataSource);
    }

    // ===== Testes de Capacidade =====

    @Test
    @DisplayName("dado_muitasEntradasNoCachev_quando_adicionarMaisEntradas_entao_funcionaCorretamente")
    void testGivenManyEntriesInCache_WhenAddMoreEntries_ThenFunctionsCorrectly() {
        // Arrange - adicionar muitas entradas
        int numberOfEntries = 1000;

        // Act
        for (int i = 0; i < numberOfEntries; i++) {
            dsCache.put((long) i, mock(DataSource.class));
        }

        // Assert
        assertThat(dsCache.getAll()).hasSize(numberOfEntries);

        // Verificar alguns valores aleatórios
        assertThat(dsCache.contains(0L)).isTrue();
        assertThat(dsCache.contains(500L)).isTrue();
        assertThat(dsCache.contains((long) (numberOfEntries - 1))).isTrue();
    }
}
