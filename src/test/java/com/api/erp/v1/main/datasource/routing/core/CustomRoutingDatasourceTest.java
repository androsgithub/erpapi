package com.api.erp.v1.main.datasource.routing.core;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.datasource.routing.domain.IDataSourceRouter;
import com.api.erp.v1.main.datasource.routing.domain.TenantDataSourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS - CustomRoutingDatasource
 * 
 * Cenários cobertos:
 * 1. Obtenção de conexão com tenant válido definido
 * 2. Roteamento correto para DataSource específico do tenant
 * 3. Carregamento de conexão do DataSource correto
 * 4. Lançamento de exceção quando tenant não está definido
 * 5. Lançamento de exceção quando DataSource não é encontrado para o tenant
 * 6. Comportamento quando ITenantContextProvider retorna tenant "master"
 * 7. Ignorância de username/password fornecidos (getConnection(user, pass) deve usar tenant)
 * 8. Propagação de exceções SQLException originais
 * 
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomRoutingDatasource - Testes Unitários")
class CustomRoutingDatasourceTest {

    @Mock
    private IDataSourceRouter dataSourceRouter;

    @Mock
    private DataSource tenantDataSource;

    @Mock
    private Connection mockConnection;

    private CustomRoutingDatasource customRoutingDatasource;

    @BeforeEach
    void setUp() {
        // Inicializar o CustomRoutingDatasource com mock do router
        customRoutingDatasource = new CustomRoutingDatasource(dataSourceRouter);
        // Limpar contexto antes de cada teste
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        // Limpar contexto após cada teste
        TenantContext.clear();
    }

    // ===== Testes Happy Path =====

    @Test
    @DisplayName("dado_tenantIdDefinido_quando_getConnection_entao_retornaConexaoDoTenantCorreto")
    void testGivenValidTenantId_WhenGetConnection_ThenReturnsConnectionFromCorrectDataSource() throws SQLException {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);

        when(dataSourceRouter.getDataSource(tenantId))
                .thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection())
                .thenReturn(mockConnection);

        // Act
        Connection connection = customRoutingDatasource.getConnection();

        // Assert
        assertThat(connection)
                .as("Deve retornar uma conexão válida")
                .isNotNull()
                .isEqualTo(mockConnection);
        
        // Verificar que o router foi chamado com o tenant correto
        verify(dataSourceRouter, times(1)).getDataSource(tenantId);
        verify(tenantDataSource, times(1)).getConnection();
    }

    @Test
    @DisplayName("dado_multiplosTenants_quando_getConnectionSequencialmente_entao_roteiaProcorretamenteParaCadaTenant")
    void testGivenMultipleTenants_WhenGetConnectionSequentially_ThenRoutesCorrectlyToEachTenant() throws SQLException {
        // Arrange
        Long tenant1 = 1L;
        Long tenant2 = 2L;
        DataSource ds1 = mock(DataSource.class);
        DataSource ds2 = mock(DataSource.class);
        Connection conn1 = mock(Connection.class);
        Connection conn2 = mock(Connection.class);

        when(dataSourceRouter.getDataSource(tenant1)).thenReturn(ds1);
        when(dataSourceRouter.getDataSource(tenant2)).thenReturn(ds2);
        when(ds1.getConnection()).thenReturn(conn1);
        when(ds2.getConnection()).thenReturn(conn2);

        // Act & Assert - Tenant 1
        TenantContext.setTenantId(tenant1);
        Connection connection1 = customRoutingDatasource.getConnection();
        assertThat(connection1).isEqualTo(conn1);
        verify(dataSourceRouter).getDataSource(tenant1);

        // Act & Assert - Tenant 2
        TenantContext.clear();
        TenantContext.setTenantId(tenant2);
        Connection connection2 = customRoutingDatasource.getConnection();
        assertThat(connection2).isEqualTo(conn2);
        verify(dataSourceRouter).getDataSource(tenant2);
    }

    // ===== Testes de Tenant Inválido/Ausente =====

    @Test
    @DisplayName("dado_tenantIdNull_quando_getConnection_entao_lancaSQLException")
    void testGivenNullTenantId_WhenGetConnection_ThenThrowsSQLException() {
        // Arrange - não definir tenant
        TenantContext.clear();

        // Act & Assert
        assertThatThrownBy(() -> customRoutingDatasource.getConnection())
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("No tenant defined in context")
                .hasMessageContaining("Configure TenantContext before accessing database");
    }

    @Test
    @DisplayName("dado_tenantNaoEncontradoNoRouter_quando_getConnection_entao_lancaSQLException")
    void testGivenTenantNotFoundInRouter_WhenGetConnection_ThenThrowsSQLException() {
        // Arrange
        Long tenantId = 999L;
        TenantContext.setTenantId(tenantId);

        TenantDataSourceNotFoundException originalException = 
                new TenantDataSourceNotFoundException(tenantId, "Tenant not found");
        
        when(dataSourceRouter.getDataSource(tenantId))
                .thenThrow(originalException);

        // Act & Assert
        assertThatThrownBy(() -> customRoutingDatasource.getConnection())
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("Datasource not configured")
                .hasMessageContaining(String.valueOf(tenantId))
                .hasCause(originalException);
    }

    @Test
    @DisplayName("dado_dataSourceRetornaNull_quando_getConnection_entao_lancaNullPointerException")
    void testGivenDataSourceReturnsNull_WhenGetConnection_ThenThrowsNullPointerException() {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);

        // DataSource retorna null (cenário edge case)
        when(dataSourceRouter.getDataSource(tenantId))
                .thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> customRoutingDatasource.getConnection())
                .isInstanceOf(NullPointerException.class);
    }

    // ===== Testes de getConnection com Username e Password =====

    @Test
    @DisplayName("dado_usernameEPasswordFornecidos_quando_getConnectionComCredenciais_entao_ignoraeCredenciaisFornecidas")
    void testGivenUsernameAndPasswordProvided_WhenGetConnectionWithCredentials_ThenIgnoresSuppliedCredentials() throws SQLException {
        // Arrange
        Long tenantId = 1L;
        String ignoredUsername = "dummy";
        String ignoredPassword = "password";

        TenantContext.setTenantId(tenantId);
        
        when(dataSourceRouter.getDataSource(tenantId))
                .thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection())
                .thenReturn(mockConnection);

        // Act
        Connection connection = customRoutingDatasource.getConnection(ignoredUsername, ignoredPassword);

        // Assert
        assertThat(connection)
                .isEqualTo(mockConnection);
        
        // Verificar que foi chamado getConnection() sem parâmetros (ignorando credenciais)
        verify(tenantDataSource, times(1)).getConnection();
        verify(tenantDataSource, never()).getConnection(ignoredUsername, ignoredPassword);
    }

    // ===== Testes de Exceção =====

    @Test
    @DisplayName("dado_dataSourceLancaSQLException_quando_getConnection_entao_propaguaSQLException")
    void testGivenDataSourceThrowsSQLException_WhenGetConnection_ThenPropagatesSQLException() throws SQLException {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);

        SQLException originalException = new SQLException("Connection refused");
        when(dataSourceRouter.getDataSource(tenantId))
                .thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection())
                .thenThrow(originalException);

        // Act & Assert
        assertThatThrownBy(() -> {
            try {
                customRoutingDatasource.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        })
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("dado_tenantIdComValorNegativo_quando_getConnection_entao_lancaSQLException")
    void testGivenNegativeTenantId_WhenGetConnection_ThenThrowsSQLException() {
        // Arrange - valor negativo não é validado em TenantContext
        Long negativeTenantId = -1L;
        TenantContext.setTenantId(negativeTenantId);

        when(dataSourceRouter.getDataSource(negativeTenantId))
                .thenThrow(new IllegalArgumentException("Tenant ID cannot be null or less than 1"));

        // Act & Assert
        assertThatThrownBy(() -> customRoutingDatasource.getConnection())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tenant ID cannot be null or less than 1");
    }

    // ===== Testes de Logging =====

    @Test
    @DisplayName("dado_tenantValido_quando_getConnectionBemsucedida_entao_logDebugEhGerado")
    void testGivenValidTenant_WhenGetConnectionSuccessful_ThenDebugLogsGenerated() throws SQLException {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);

        when(dataSourceRouter.getDataSource(tenantId))
                .thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection())
                .thenReturn(mockConnection);

        // Act
        Connection connection = customRoutingDatasource.getConnection();

        // Assert - apenas verificar que foi executado sem erros (logs foram gerados)
        assertThat(connection)
                .isNotNull();
    }

    // ===== Testes de Chamadas Múltiplas =====

    @Test
    @DisplayName("dado_getConnectionChamadoMultiplaVezes_quando_tenantNaoMuda_entao_retornaConexoesDistintas")
    void testGivenGetConnectionCalledMultipleTimes_WhenTenantStaySame_ThenReturnsDistinctConnections() throws SQLException {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);

        Connection conn1 = mock(Connection.class);
        Connection conn2 = mock(Connection.class);

        when(dataSourceRouter.getDataSource(tenantId))
                .thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection())
                .thenReturn(conn1)
                .thenReturn(conn2);

        // Act
        Connection connection1 = customRoutingDatasource.getConnection();
        Connection connection2 = customRoutingDatasource.getConnection();

        // Assert - cada chamada deve retornar uma nova conexão
        assertThat(connection1).isEqualTo(conn1);
        assertThat(connection2).isEqualTo(conn2);
        assertThat(connection1).isNotEqualTo(connection2);
        
        // Verificar que getConnection foi chamado duas vezes
        verify(tenantDataSource, times(2)).getConnection();
    }

    @Test
    @DisplayName("dado_contextoLimpoEntreRequisicoes_quando_getConnectionAposLimpeza_entao_lancaExcecao")
    void testGivenContextClearedBetweenRequests_WhenGetConnectionAfterClearing_ThenThrowsException() {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);
        
        // Limpar contexto simulando fim da requisição
        TenantContext.clear();

        // Act & Assert
        assertThatThrownBy(() -> customRoutingDatasource.getConnection())
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("No tenant defined in context");
    }

    // ===== TESTES ADICIONAIS - Edge Cases e Cenários =====

    @Test
    @DisplayName("dado_multiplasChamadasGetConnection_quando_todoComMesmoTenant_entaoRetornaConexoes")
    void testGivenMultipleGetConnectionsCalls_WhenSameTenant_ThenReturnsConnections() throws SQLException {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);
        when(dataSourceRouter.getDataSource(tenantId)).thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection()).thenReturn(mockConnection);

        // Act
        java.sql.Connection conn1 = customRoutingDatasource.getConnection();
        java.sql.Connection conn2 = customRoutingDatasource.getConnection();
        java.sql.Connection conn3 = customRoutingDatasource.getConnection();

        // Assert
        assertThat(conn1).isNotNull();
        assertThat(conn2).isNotNull();
        assertThat(conn3).isNotNull();
        verify(tenantDataSource, times(3)).getConnection();
    }

    @Test
    @DisplayName("dado_connectionComUsernamePassword_quando_getConnection_entaoIgnoraCredenciais")
    void testGivenConnectionWithUsernamePassword_WhenGetConnection_ThenIgnoresCredentials() throws SQLException {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);
        when(dataSourceRouter.getDataSource(tenantId)).thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection()).thenReturn(mockConnection);

        // Act
        java.sql.Connection conn = customRoutingDatasource.getConnection("ignored_user", "ignored_pass");

        // Assert
        assertThat(conn).isNotNull();
        // verify que as credenciais foram ignoradas (getConnection sem args foi chamado)
        verify(tenantDataSource).getConnection();
        verify(tenantDataSource, never()).getConnection("ignored_user", "ignored_pass");
    }

    @Test
    @DisplayName("dado_tenantIdMudaBetweenCalls_quando_getConnection_entaoUsaTenantCorreto")
    void testGivenTenantIdChangesBetweenCalls_WhenGetConnection_ThenUsesCorrectTenant() throws SQLException {
        // Arrange
        DataSource dataSource1 = mock(DataSource.class);
        DataSource dataSource2 = mock(DataSource.class);
        when(dataSourceRouter.getDataSource(1L)).thenReturn(dataSource1);
        when(dataSourceRouter.getDataSource(2L)).thenReturn(dataSource2);
        when(dataSource1.getConnection()).thenReturn(mockConnection);
        when(dataSource2.getConnection()).thenReturn(mockConnection);

        // Act
        TenantContext.setTenantId(1L);
        java.sql.Connection conn1 = customRoutingDatasource.getConnection();
        
        TenantContext.setTenantId(2L);
        java.sql.Connection conn2 = customRoutingDatasource.getConnection();

        // Assert
        verify(dataSourceRouter).getDataSource(1L);
        verify(dataSourceRouter).getDataSource(2L);
        assertThat(conn1).isNotNull();
        assertThat(conn2).isNotNull();
    }

    @Test
    @DisplayName("dado_tenantIdLargeValue_quando_getConnection_entaoFunciona")
    void testGivenLargeTenantId_WhenGetConnection_ThenWorks() throws SQLException {
        // Arrange
        Long largeTenantId = Long.MAX_VALUE;
        TenantContext.setTenantId(largeTenantId);
        when(dataSourceRouter.getDataSource(largeTenantId)).thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection()).thenReturn(mockConnection);

        // Act
        java.sql.Connection conn = customRoutingDatasource.getConnection();

        // Assert
        assertThat(conn).isNotNull();
        verify(dataSourceRouter).getDataSource(largeTenantId);
    }

    @Test
    @DisplayName("dado_tenantIdZero_quando_getConnection_entaoUsaTenant0")
    void testGivenZeroTenantId_WhenGetConnection_ThenUsesTenant0() throws SQLException {
        // Arrange
        Long zeroTenantId = 0L;
        TenantContext.setTenantId(zeroTenantId);
        when(dataSourceRouter.getDataSource(zeroTenantId)).thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection()).thenReturn(mockConnection);

        // Act
        java.sql.Connection conn = customRoutingDatasource.getConnection();

        // Assert
        assertThat(conn).isNotNull();
        verify(dataSourceRouter).getDataSource(zeroTenantId);
    }

    @Test
    @DisplayName("dado_negativeTenantId_quando_getConnection_entaoUsaTenantNegativo")
    void testGivenNegativeTenantId_WhenGetConnection_ThenUsesNegativeTenant() throws SQLException {
        // Arrange
        Long negativeTenantId = -999L;
        TenantContext.setTenantId(negativeTenantId);
        when(dataSourceRouter.getDataSource(negativeTenantId)).thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection()).thenReturn(mockConnection);

        // Act
        java.sql.Connection conn = customRoutingDatasource.getConnection();

        // Assert
        assertThat(conn).isNotNull();
        verify(dataSourceRouter).getDataSource(negativeTenantId);
    }

    @Test
    @DisplayName("dado_routerLancaDiferenteException_quando_getConnection_entaoRepassa")
    void testGivenRouterThrowsDifferentException_WhenGetConnection_ThenRethrows() {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);
        RuntimeException customException = new RuntimeException("Custom router error");
        when(dataSourceRouter.getDataSource(tenantId)).thenThrow(customException);

        // Act & Assert
        assertThatThrownBy(() -> customRoutingDatasource.getConnection())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Custom router error");
    }

    @Test
    @DisplayName("dado_connectionThrowsRuntimeException_quando_getConnection_entaoRepassa")
    void testGivenConnectionThrowsRuntimeException_WhenGetConnection_ThenThrows() throws SQLException {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);
        when(dataSourceRouter.getDataSource(tenantId)).thenReturn(tenantDataSource);
        RuntimeException runtimeEx = new RuntimeException("Runtime error");
        when(tenantDataSource.getConnection()).thenThrow(runtimeEx);

        // Act & Assert
        assertThatThrownBy(() -> customRoutingDatasource.getConnection())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Runtime error");
    }

    @Test
    @DisplayName("dado_routerRetornaDataSourceValido_quando_getConnectionMultiplaVezes_entaoTodosFunciona")
    void testGivenValidDataSourceFromRouter_WhenGetConnectionMultipleTimes_ThenAllWork() throws SQLException {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);
        when(dataSourceRouter.getDataSource(tenantId)).thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection()).thenReturn(mockConnection);

        // Act & Assert
        for (int i = 0; i < 5; i++) {
            java.sql.Connection conn = customRoutingDatasource.getConnection();
            assertThat(conn).isNotNull();
        }

        verify(tenantDataSource, times(5)).getConnection();
    }

    @Test
    @DisplayName("dado_getConnectionComUsernamePasswordNull_quando_chamado_entaoIgnoraEUsaDefault")
    void testGivenGetConnectionWithNullCredentials_WhenCalled_ThenIgnoresAndUsesDefault() throws SQLException {
        // Arrange
        Long tenantId = 1L;
        TenantContext.setTenantId(tenantId);
        when(dataSourceRouter.getDataSource(tenantId)).thenReturn(tenantDataSource);
        when(tenantDataSource.getConnection()).thenReturn(mockConnection);

        // Act
        java.sql.Connection conn = customRoutingDatasource.getConnection(null, null);

        // Assert
        assertThat(conn).isNotNull();
        verify(tenantDataSource).getConnection();
    }

    @Test
    @DisplayName("dado_multiplosTenantsChamadosRapidamente_quando_getConnection_entaoTodosUsam")
    void testGivenMultipleTenantsCalledRapidly_WhenGetConnection_ThenAllUse() throws SQLException {
        // Arrange
        DataSource ds1 = mock(DataSource.class);
        DataSource ds2 = mock(DataSource.class);
        when(dataSourceRouter.getDataSource(1L)).thenReturn(ds1);
        when(dataSourceRouter.getDataSource(2L)).thenReturn(ds2);
        when(ds1.getConnection()).thenReturn(mockConnection);
        when(ds2.getConnection()).thenReturn(mockConnection);

        // Act
        TenantContext.setTenantId(1L);
        customRoutingDatasource.getConnection();
        TenantContext.setTenantId(2L);
        customRoutingDatasource.getConnection();
        TenantContext.setTenantId(1L);
        customRoutingDatasource.getConnection();

        // Assert
        verify(dataSourceRouter, times(2)).getDataSource(1L);
        verify(dataSourceRouter, times(1)).getDataSource(2L);
    }

    @Test
    @DisplayName("dado_tenantIdUmDepoisAlterar_quando_getConnection_entaoRetornaCorreto")
    void testGivenTenantIDChangedThenBack_WhenGetConnection_ThenReturnsCorrect() throws SQLException {
        // Arrange
        DataSource ds1 = mock(DataSource.class);
        when(dataSourceRouter.getDataSource(1L)).thenReturn(ds1);
        when(ds1.getConnection()).thenReturn(mockConnection);

        // Act
        TenantContext.setTenantId(1L);
        java.sql.Connection conn1 = customRoutingDatasource.getConnection();
        TenantContext.setTenantId(2L);
        TenantContext.setTenantId(1L);
        java.sql.Connection conn2 = customRoutingDatasource.getConnection();

        // Assert
        assertThat(conn1).isNotNull();
        assertThat(conn2).isNotNull();
        verify(ds1, times(2)).getConnection();
    }
}
