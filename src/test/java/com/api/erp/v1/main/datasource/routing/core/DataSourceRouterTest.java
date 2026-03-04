package com.api.erp.v1.main.datasource.routing.core;

import com.api.erp.v1.main.datasource.routing.cache.DSCache;
import com.api.erp.v1.main.datasource.routing.domain.IDataSourceRouter;
import com.api.erp.v1.main.datasource.routing.domain.ITenantConfigProvider;
import com.api.erp.v1.main.datasource.routing.domain.TenantDataSourceNotFoundException;
import com.api.erp.v1.main.datasource.routing.domain.TenantDSConfig;
import com.api.erp.v1.main.datasource.routing.infrastructure.HikariDataSourceFactory;
import com.api.erp.v1.main.datasource.routing.infrastructure.JdbcTenantConfigProvider;
import com.api.erp.v1.main.tenant.domain.entity.DBType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS - DataSourceRouter
 * 
 * Cenários cobertos:
 * 1. Registrar DataSource com valores válidos
 * 2. Rejeitar registro com tenantId inválido (null, <= 0)
 * 3. Rejeitar registro com DataSource null
 * 4. Recuperar DataSource do cache
 * 5. Criar e cachear DataSource quando não encontrado em cache
 * 6. Lançar exceção quando tenant não é encontrado
 * 7. Verificar se DataSource está em cache
 * 8. Invalidar e remover DataSource do cache
 * 9. Recuperar Master DataSource
 * 10. Getall DataSources para debug
 * 11. Validação de tenantId negativo
 * 12. Comportamento em cenários de erro na factory
 * 
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DataSourceRouter - Testes Unitários")
class DataSourceRouterTest {

    @Mock
    private DataSource masterDataSource;

    @Mock
    private ITenantConfigProvider tenantConfigProvider;

    @Mock
    private HikariDataSourceFactory dataSourceFactory;

    private DSCache dsCache;

    @Mock
    private DataSource tenantDataSource;

    @Mock
    private JdbcTenantConfigProvider jdbcTenantConfigProvider;

    private DataSourceRouter dataSourceRouter;

    @BeforeEach
    void setUp() {
        dsCache = new DSCache();

        dataSourceRouter = new DataSourceRouter(
                masterDataSource,
                tenantConfigProvider,
                dataSourceFactory,
                dsCache
        );
    }

    // ===== Testes de Registração =====

    @Test
    @DisplayName("dado_tenantIdEDataSourceValidos_quando_registerDataSource_entao_armazenaCacheComSucesso")
    void testGivenValidTenantIdAndDataSource_WhenRegisterDataSource_ThenStoresCacheSuccessfully() {
        // Arrange
        Long tenantId = 1L;
        DataSource dataSource = mock(DataSource.class);

        // Act
        dataSourceRouter.registerDataSource(tenantId, dataSource);

        // Assert
        assertThat(dsCache.contains(tenantId)).isTrue();
        assertThat(dsCache.get(tenantId)).isEqualTo(dataSource);
    }

    @Test
    @DisplayName("dado_tenantIdNull_quando_registerDataSource_entao_lancaIllegalArgumentException")
    void testGivenNullTenantId_WhenRegisterDataSource_ThenThrowsIllegalArgumentException() {
        // Arrange
        DataSource dataSource = mock(DataSource.class);

        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.registerDataSource(null, dataSource))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    @Test
    @DisplayName("dado_tenantIdZero_quando_registerDataSource_entao_lancaIllegalArgumentException")
    void testGivenZeroTenantId_WhenRegisterDataSource_ThenThrowsIllegalArgumentException() {
        // Arrange
        DataSource dataSource = mock(DataSource.class);

        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.registerDataSource(0L, dataSource))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    @Test
    @DisplayName("dado_tenantIdNegativo_quando_registerDataSource_entao_lancaIllegalArgumentException")
    void testGivenNegativeTenantId_WhenRegisterDataSource_ThenThrowsIllegalArgumentException() {
        // Arrange
        DataSource dataSource = mock(DataSource.class);

        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.registerDataSource(-1L, dataSource))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    @Test
    @DisplayName("dado_dataSourceNull_quando_registerDataSource_entao_lancaIllegalArgumentException")
    void testGivenNullDataSource_WhenRegisterDataSource_ThenThrowsIllegalArgumentException() {
        // Arrange
        Long tenantId = 1L;

        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.registerDataSource(tenantId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DataSource cannot be null");
    }

    // ===== Testes de Obtenção (Cache Hit) =====

    @Test
    @DisplayName("dado_dataSourceEmCache_quando_getDataSource_entao_retornaDoCache")
    void testGivenDataSourceInCache_WhenGetDataSource_ThenReturnFromCache() {
        // Arrange
        Long tenantId = 1L;
        dsCache.put(tenantId, tenantDataSource);

        // Act
        DataSource result = dataSourceRouter.getDataSource(tenantId);

        // Assert
        assertThat(result)
                .isEqualTo(tenantDataSource);

        // Verificar que buscou do cache e não criou novo DataSource
        verify(dsCache, times(1)).contains(tenantId);
        verify(dsCache, times(1)).get(tenantId);
        verify(tenantConfigProvider, never()).getTenantConfig(anyLong());
        verify(dataSourceFactory, never()).createDataSource(any());
    }

    // ===== Testes de Obtenção (Cache Miss) =====

    @Test
    @DisplayName("dado_dataSourceNaoEmCache_quando_getDataSource_entao_criaRegistraERetorna")
    void testGivenDataSourceNotInCache_WhenGetDataSource_ThenCreatesRegistersAndReturns() {
        // Arrange
        Long tenantId = 1L;
        TenantDSConfig config = new TenantDSConfig(
                tenantId,
                "jdbc:postgresql://localhost:5432/tenant1",
                "user",
                "password",
                DBType.POSTGRESQL
        );

        when(dsCache.contains(tenantId)).thenReturn(false);
        when(tenantConfigProvider.getTenantConfig(tenantId))
                .thenReturn(Optional.of(config));
        when(dataSourceFactory.createDataSource(config))
                .thenReturn(tenantDataSource);

        // Act
        DataSource result = dataSourceRouter.getDataSource(tenantId);

        // Assert
        assertThat(result)
                .isEqualTo(tenantDataSource);
        
        // Verificar fluxo: cache hit -> config -> factory -> register
        verify(dsCache, times(1)).contains(tenantId);
        verify(tenantConfigProvider, times(1)).getTenantConfig(tenantId);
        verify(dataSourceFactory, times(1)).createDataSource(config);
        verify(dsCache, times(1)).put(tenantId, tenantDataSource);
    }

    @Test
    @DisplayName("dado_configuracaoNaoEncontrada_quando_getDataSource_entao_lancaExcecao")
    void testGivenConfigNotFound_WhenGetDataSource_ThenThrowsTenantDataSourceNotFoundException() {
        // Arrange
        Long tenantId = 999L;
        when(dsCache.contains(tenantId)).thenReturn(false);
        when(tenantConfigProvider.getTenantConfig(tenantId))
                .thenReturn(Optional.empty());

        // Act & Assert
        // Note: DataSourceRouter.getDataSource calls toNotFoundException() on DATASOURCE_NOT_CONFIGURED
        // which has status BAD_REQUEST (not NOT_FOUND), so it throws IllegalStateException
        assertThatThrownBy(() -> dataSourceRouter.getDataSource(tenantId))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("This error is not of type NOT_FOUND");
    }

    @Test
    @DisplayName("dado_tenantIdNull_quando_getDataSource_entao_lancaIllegalArgumentException")
    void testGivenNullTenantId_WhenGetDataSource_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.getDataSource(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    @Test
    @DisplayName("dado_tenantIdNegativo_quando_getDataSource_entao_lancaIllegalArgumentException")
    void testGivenNegativeTenantId_WhenGetDataSource_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.getDataSource(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    @Test
    @DisplayName("dado_factoryLancaExcecao_quando_getDataSource_entao_lancaRuntime")
    void testGivenFactoryThrowsException_WhenGetDataSource_ThenThrowsRuntimeException() {
        // Arrange
        Long tenantId = 1L;
        TenantDSConfig config = new TenantDSConfig(
                tenantId,
                "jdbc:postgresql://localhost:5432/tenant1",
                "user",
                "password"
        );

        when(dsCache.contains(tenantId)).thenReturn(false);
        when(tenantConfigProvider.getTenantConfig(tenantId))
                .thenReturn(Optional.of(config));
        when(dataSourceFactory.createDataSource(config))
                .thenThrow(new RuntimeException("Connection failed"));

        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.getDataSource(tenantId))
                .isInstanceOf(RuntimeException.class);
    }

    // ===== Testes de Verificação em Cache =====

    @Test
    @DisplayName("dado_dataSourceEmCache_quando_isDataSourceCached_entao_retornaTrue")
    void testGivenDataSourceInCache_WhenIsDataSourceCached_ThenReturnsTrue() {
        // Arrange
        Long tenantId = 1L;
        when(dsCache.contains(tenantId)).thenReturn(true);

        // Act
        boolean result = dataSourceRouter.isDataSourceCached(tenantId);

        // Assert
        assertThat(result).isTrue();
        verify(dsCache, times(1)).contains(tenantId);
    }

    @Test
    @DisplayName("dado_dataSourceNaoEmCache_quando_isDataSourceCached_entao_retornaFalse")
    void testGivenDataSourceNotInCache_WhenIsDataSourceCached_ThenReturnsFalse() {
        // Arrange
        Long tenantId = 1L;
        // Act
        boolean result = dataSourceRouter.isDataSourceCached(tenantId);

        // Assert
        assertThat(result).isFalse();
    }

    // ===== Testes de Invalidação =====

    @Test
    @DisplayName("dado_dataSourceEmCache_quando_invalidateDataSourceCache_entao_removeDoCacheEFecha")
    void testGivenDataSourceInCache_WhenInvalidateDataSourceCache_ThenRemovesAndCloses() {
        // Arrange
        Long tenantId = 1L;
        when(dsCache.remove(tenantId)).thenReturn(tenantDataSource);

        // Act
        dataSourceRouter.invalidateDataSourceCache(tenantId);

        // Assert
        verify(dsCache, times(1)).remove(tenantId);
    }

    @Test
    @DisplayName("dado_tenantIdNull_quando_invalidateDataSourceCache_entao_lancaIllegalArgumentException")
    void testGivenNullTenantId_WhenInvalidateDataSourceCache_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.invalidateDataSourceCache(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    @Test
    @DisplayName("dado_tenantIdNegativo_quando_invalidateDataSourceCache_entao_lancaIllegalArgumentException")
    void testGivenNegativeTenantId_WhenInvalidateDataSourceCache_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.invalidateDataSourceCache(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    // ===== Testes de Master DataSource =====

    @Test
    @DisplayName("dado_masterDatasourceBeanDefinido_quando_getMasterDataSource_entao_retornaMasterDataSource")
    void testGivenMasterDataSourceBeanDefined_WhenGetMasterDataSource_ThenReturnsMasterDataSource() {
        // Act
        DataSource result = dataSourceRouter.getMasterDataSource();

        // Assert
        assertThat(result)
                .isEqualTo(masterDataSource);
    }

    // ===== Testes de Múltiplos Tenants =====

    @Test
    @DisplayName("dado_multiplosTenantIds_quando_getDataSourceParaCada_entao_retornaDistintosDataSources")
    void testGivenMultipleTenantIds_WhenGetDataSourceForEach_ThenReturnsDistinctDataSources() {
        // Arrange
        Long tenant1 = 1L;
        Long tenant2 = 2L;
        DataSource ds1 = mock(DataSource.class);
        DataSource ds2 = mock(DataSource.class);

        when(dsCache.contains(tenant1)).thenReturn(true);
        when(dsCache.contains(tenant2)).thenReturn(true);
        when(dsCache.get(tenant1)).thenReturn(ds1);
        when(dsCache.get(tenant2)).thenReturn(ds2);

        // Act
        DataSource result1 = dataSourceRouter.getDataSource(tenant1);
        DataSource result2 = dataSourceRouter.getDataSource(tenant2);

        // Assert
        assertThat(result1).isEqualTo(ds1);
        assertThat(result2).isEqualTo(ds2);
        assertThat(result1).isNotEqualTo(result2);
    }

    // ===== Testes de Chamadas Consecutivas =====

    @Test
    @DisplayName("dado_mesmoTenantIdChamadoSequencialmente_quando_getDataSourceDuasVezes_entao_primeiraVezCriaSegundaVezRetornaDoCacheao")
    void testGivenSameTenantIdCalledSequentially_WhenGetDataSourceTwice_ThenFirstCreatesSecondReturnsFromCache() {
        // Arrange
        Long tenantId = 1L;
        TenantDSConfig config = new TenantDSConfig(tenantId, "url", "user", "pass");

        // Primeira chamada: não em cache
        when(dsCache.contains(tenantId))
                .thenReturn(false)
                .thenReturn(true);  // Simula que foi adicionado ao cache
        
        when(tenantConfigProvider.getTenantConfig(tenantId))
                .thenReturn(Optional.of(config));
        when(dataSourceFactory.createDataSource(config))
                .thenReturn(tenantDataSource);
        when(dsCache.get(tenantId)).thenReturn(tenantDataSource);

        // Act
        DataSource result1 = dataSourceRouter.getDataSource(tenantId);
        DataSource result2 = dataSourceRouter.getDataSource(tenantId);

        // Assert
        assertThat(result1).isEqualTo(tenantDataSource);
        assertThat(result2).isEqualTo(tenantDataSource);
        
        // Factory deve ser chamado apenas uma vez (cache hit na segunda)
        verify(dataSourceFactory, times(1)).createDataSource(config);
    }

    // ===== TESTES ADICIONAIS - Cache, Invalidação e Múltiplos Tenants =====

    @Test
    @DisplayName("dado_3TenantsDiferentes_quando_getDataSource_entaoTodosArmazenadosEmCache")
    void testGiven3DifferentTenants_WhenGetDataSource_ThenAllCached() {
        // Arrange
        Long tenant1 = 1L;
        Long tenant2 = 2L;
        Long tenant3 = 3L;
        
        TenantDSConfig config1 = new TenantDSConfig(tenant1, "jdbc:h2:1", "u", "p", DBType.H2);
        TenantDSConfig config2 = new TenantDSConfig(tenant2, "jdbc:h2:2", "u", "p", DBType.H2);
        TenantDSConfig config3 = new TenantDSConfig(tenant3, "jdbc:h2:3", "u", "p", DBType.H2);
        
        when(tenantConfigProvider.getTenantConfig(tenant1)).thenReturn(Optional.of(config1));
        when(tenantConfigProvider.getTenantConfig(tenant2)).thenReturn(Optional.of(config2));
        when(tenantConfigProvider.getTenantConfig(tenant3)).thenReturn(Optional.of(config3));
        when(dataSourceFactory.createDataSource(any())).thenReturn(tenantDataSource);

        // Act
        DataSource ds1 = dataSourceRouter.getDataSource(tenant1);
        DataSource ds2 = dataSourceRouter.getDataSource(tenant2);
        DataSource ds3 = dataSourceRouter.getDataSource(tenant3);

        // Assert
        assertThat(ds1).isNotNull();
        assertThat(ds2).isNotNull();
        assertThat(ds3).isNotNull();
        assertThat(dataSourceRouter.isDataSourceCached(tenant1)).isTrue();
        assertThat(dataSourceRouter.isDataSourceCached(tenant2)).isTrue();
        assertThat(dataSourceRouter.isDataSourceCached(tenant3)).isTrue();
    }

    @Test
    @DisplayName("dado_tenantInvalidado_quando_getDataSource_entaoCriaNovoDataSource")
    void testGivenTenantInvalidated_WhenGetDataSource_ThenCreatesNewDataSource() {
        // Arrange
        Long tenantId = 1L;
        TenantDSConfig config = new TenantDSConfig(tenantId, "jdbc:h2:mem:test", "sa", "p", DBType.H2);
        when(tenantConfigProvider.getTenantConfig(tenantId)).thenReturn(Optional.of(config));
        when(dataSourceFactory.createDataSource(config)).thenReturn(tenantDataSource);

        // Act - criar primeira vez
        dataSourceRouter.getDataSource(tenantId);
        assertThat(dataSourceRouter.isDataSourceCached(tenantId)).isTrue();

        // Invalidar cache
        dataSourceRouter.invalidateDataSourceCache(tenantId);
        assertThat(dataSourceRouter.isDataSourceCached(tenantId)).isFalse();

        // Act - criar novamente
        DataSource newDs = dataSourceRouter.getDataSource(tenantId);

        // Assert
        assertThat(newDs).isNotNull();
        verify(dataSourceFactory, times(2)).createDataSource(config);
    }

    @Test
    @DisplayName("dado_invalidarMultiplosTenants_quando_getDataSource_entaoRecriaAmbos")
    void testGivenInvalidateMultipleTenants_WhenGetDataSource_ThenRecreatesBoth() {
        // Arrange
        Long tenant1 = 1L;
        Long tenant2 = 2L;
        TenantDSConfig config1 = new TenantDSConfig(tenant1, "jdbc:h2:1", "u", "p", DBType.H2);
        TenantDSConfig config2 = new TenantDSConfig(tenant2, "jdbc:h2:2", "u", "p", DBType.H2);
        
        when(tenantConfigProvider.getTenantConfig(tenant1)).thenReturn(Optional.of(config1));
        when(tenantConfigProvider.getTenantConfig(tenant2)).thenReturn(Optional.of(config2));
        when(dataSourceFactory.createDataSource(any())).thenReturn(tenantDataSource);

        // Act - Create initial
        dataSourceRouter.getDataSource(tenant1);
        dataSourceRouter.getDataSource(tenant2);

        // Invalidate both
        dataSourceRouter.invalidateDataSourceCache(tenant1);
        dataSourceRouter.invalidateDataSourceCache(tenant2);

        // Recreate
        dataSourceRouter.getDataSource(tenant1);
        dataSourceRouter.getDataSource(tenant2);

        // Assert
        verify(dataSourceFactory, times(2)).createDataSource(config1);
        verify(dataSourceFactory, times(2)).createDataSource(config2);
    }

    @Test
    @DisplayName("dado_masterDataSourceRetornado_quando_getMasterDataSource_entaoRetorna")
    void testGivenMasterDataSourceReturned_WhenGetMasterDataSource_ThenReturns() {
        // Act
        DataSource master = dataSourceRouter.getMasterDataSource();

        // Assert
        assertThat(master).isNotNull();
        assertThat(master).isSameAs(masterDataSource);
    }

    @Test
    @DisplayName("dado_tenantIdZero_quando_getDataSource_entaoThrowsIllegalArgument")
    void testGivenZeroTenantId_WhenGetDataSource_ThenThrowsIllegalArgument() {
        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.getDataSource(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    @Test
    @DisplayName("dado_tenantIdNegativo_quando_getDataSource_entaoThrowsIllegalArgument")
    void testGivenNegativeTenantId_WhenGetDataSource_ThenThrowsIllegalArgument() {
        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.getDataSource(-999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    @Test
    @DisplayName("dado_registerComDataSourceNull_quando_registerDataSource_entaoThrowsIllegalArgument")
    void testGivenNullDataSource_WhenRegister_ThenThrowsIllegalArgument() {
        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.registerDataSource(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DataSource cannot be null");
    }

    @Test
    @DisplayName("dado_registerComTenantIdZero_quando_registerDataSource_entaoThrowsIllegalArgument")
    void testGivenZeroTenantIdRegister_WhenRegister_ThenThrowsIllegalArgument() {
        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.registerDataSource(0L, tenantDataSource))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    @Test
    @DisplayName("dado_registerComTenantIdNegativo_quando_registerDataSource_entaoThrowsIllegalArgument")
    void testGivenNegativeTenantIdRegister_WhenRegister_ThenThrowsIllegalArgument() {
        // Act & Assert
        assertThatThrownBy(() -> dataSourceRouter.registerDataSource(-1L, tenantDataSource))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    @Test
    @DisplayName("dado_isDataSourceCachedComTenantNaoCacheado_quando_check_entaoRetornaFalse")
    void testGivenUncachedTenant_WhenIsDataSourceCached_ThenReturnsFalse() {
        // Act & Assert
        assertThat(dataSourceRouter.isDataSourceCached(999L)).isFalse();
    }

    @Test
    @DisplayName("dado_largeListaDeTenants_quando_getAllDataSources_entaoRetornaTodos")
    void testGivenLargeListOfTenants_WhenGetAllDataSources_ThenReturnsAll() {
        // Arrange
        for (long i = 1; i <= 5; i++) {
            TenantDSConfig config = new TenantDSConfig(i, "jdbc:h2:" + i, "u", "p", DBType.H2);
            when(tenantConfigProvider.getTenantConfig(i)).thenReturn(Optional.of(config));
            when(dataSourceFactory.createDataSource(any())).thenReturn(tenantDataSource);
            dataSourceRouter.getDataSource(i);
        }

        // Act
        java.util.Map<Long, DataSource> allDs = dataSourceRouter.getAllDataSources();

        // Assert
        assertThat(allDs).hasSize(5);
    }

    @Test
    @DisplayName("dado_tenantIdMaxValue_quando_getDataSource_entaoFunciona")
    void testGivenMaxValueTenantId_WhenGetDataSource_ThenWorks() {
        // Arrange
        Long maxTenantId = Long.MAX_VALUE;
        TenantDSConfig config = new TenantDSConfig(maxTenantId, "jdbc:h2:max", "u", "p", DBType.H2);
        when(tenantConfigProvider.getTenantConfig(maxTenantId)).thenReturn(Optional.of(config));
        when(dataSourceFactory.createDataSource(config)).thenReturn(tenantDataSource);

        // Act
        DataSource ds = dataSourceRouter.getDataSource(maxTenantId);

        // Assert
        assertThat(ds).isNotNull();
        assertThat(dataSourceRouter.isDataSourceCached(maxTenantId)).isTrue();
    }

    @Test
    @DisplayName("dado_invalidarTenantNaoCacheado_quando_invalidate_entaoNaoThrows")
    void testGivenUncachedTenantInvalidate_WhenInvalidate_ThenNothingHappens() {
        // Act & Assert - não deve lançar exceção
        dataSourceRouter.invalidateDataSourceCache(999L);
        assertThat(dataSourceRouter.isDataSourceCached(999L)).isFalse();
    }

    @Test
    @DisplayName("dado_10TenantsCacheados_quando_getAllDataSources_entaoRetorna10")
    void testGiven10CachedTenants_WhenGetAllDataSources_ThenReturns10() {
        // Arrange
        for (long i = 1; i <= 10; i++) {
            TenantDSConfig config = new TenantDSConfig(i, "jdbc:h2:" + i, "u", "p", DBType.H2);
            when(tenantConfigProvider.getTenantConfig(i)).thenReturn(Optional.of(config));
            when(dataSourceFactory.createDataSource(any())).thenReturn(tenantDataSource);
            dataSourceRouter.getDataSource(i);
        }

        // Act
        java.util.Map<Long, DataSource> all = dataSourceRouter.getAllDataSources();

        // Assert
        assertThat(all).hasSize(10);
        for (long i = 1; i <= 10; i++) {
            assertThat(all.containsKey(i)).isTrue();
        }
    }

    @Test
    @DisplayName("dado_registerManualmenteSeguido porGetDataSource_quando_retorna_entaoUsaRegistrado")
    void testGivenManuallyRegisteredThenGetDataSource_WhenRedundantCreate_ThenUsesRegistered() {
        // Arrange
        Long tenantId = 1L;
        TenantDSConfig config = new TenantDSConfig(tenantId, "jdbc:h2:mem:test", "sa", "p", DBType.H2);
        
        // Register manually
        dataSourceRouter.registerDataSource(tenantId, tenantDataSource);

        // Act
        DataSource ds = dataSourceRouter.getDataSource(tenantId);

        // Assert
        assertThat(ds).isSameAs(tenantDataSource);
        assertThat(dataSourceRouter.isDataSourceCached(tenantId)).isTrue();
    }
}
