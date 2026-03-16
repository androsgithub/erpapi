package com.api.erp.v1.main.datasource.routing.initialization;

import com.api.erp.v1.main.datasource.routing.core.DataSourceRouter;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS - DataSourceInitializer
 *
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DataSourceInitializer - Testes Unitários")
class DataSourceInitializerTest {

    @Mock
    private DataSourceRouter dataSourceRouter;

    @Mock
    private TenantRepository tenantRepository;

    private DataSourceInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new DataSourceInitializer(dataSourceRouter, tenantRepository);
    }

    @Test
    @DisplayName("dado_tenantsAtivos_quando_initializeTenantDataSources_entao_preLoadCadaUm")
    void testGivenActiveTenants_WhenInitialize_ThenPreloadsEach() {
        Tenant t1 = Tenant.builder().id(1L).name("A").active(true).build();
        Tenant t2 = Tenant.builder().id(2L).name("B").active(true).build();

        when(tenantRepository.findAllByActiveTrue()).thenReturn(Arrays.asList(t1, t2));
        when(dataSourceRouter.getDataSource(anyLong())).thenReturn(mock(DataSource.class));

        initializer.initializeTenantDataSources();

        verify(dataSourceRouter, times(1)).getDataSource(1L);
        verify(dataSourceRouter, times(1)).getDataSource(2L);
    }

    @Test
    @DisplayName("dado_semTenantsAtivos_quando_initializeTenantDataSources_entao_retornaSemErro")
    void testGivenNoActiveTenants_WhenInitialize_ThenReturnsWithoutError() {
        when(tenantRepository.findAllByActiveTrue()).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> initializer.initializeTenantDataSources());

        verify(dataSourceRouter, never()).getDataSource(anyLong());
    }

    @Test
    @DisplayName("dado_tenantFalhaNoPreload_quando_initializeTenantDataSources_entao_continuaComProximo")
    void testGivenTenantFailsPreload_WhenInitialize_ThenContinuesWithNext() {
        Tenant t1 = Tenant.builder().id(1L).name("A").active(true).build();
        Tenant t2 = Tenant.builder().id(2L).name("B").active(true).build();

        when(tenantRepository.findAllByActiveTrue()).thenReturn(Arrays.asList(t1, t2));
        when(dataSourceRouter.getDataSource(1L)).thenThrow(new NotFoundException("Not found"));
        when(dataSourceRouter.getDataSource(2L)).thenReturn(mock(DataSource.class));

        assertDoesNotThrow(() -> initializer.initializeTenantDataSources());

        verify(dataSourceRouter, times(1)).getDataSource(1L);
        verify(dataSourceRouter, times(1)).getDataSource(2L);
    }

    @Test
    @DisplayName("dado_excecaoGeral_quando_initializeTenantDataSources_entao_naoRelanca")
    void testGivenGeneralException_WhenInitialize_ThenDoesNotRethrow() {
        when(tenantRepository.findAllByActiveTrue()).thenThrow(new RuntimeException("DB down"));

        assertDoesNotThrow(() -> initializer.initializeTenantDataSources());
    }

    @Test
    @DisplayName("dado_tenantComExcecaoGenerica_quando_initializeTenantDataSources_entao_incrementaErrorCount")
    void testGivenTenantWithGenericException_WhenInitialize_ThenIncrementsErrorCount() {
        Tenant t1 = Tenant.builder().id(1L).name("A").active(true).build();

        when(tenantRepository.findAllByActiveTrue()).thenReturn(Arrays.asList(t1));
        when(dataSourceRouter.getDataSource(1L)).thenThrow(new RuntimeException("Connection refused"));

        assertDoesNotThrow(() -> initializer.initializeTenantDataSources());

        verify(dataSourceRouter, times(1)).getDataSource(1L);
    }
}
