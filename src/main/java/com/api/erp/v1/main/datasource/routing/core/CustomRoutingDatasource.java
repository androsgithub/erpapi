package com.api.erp.v1.main.datasource.routing.core;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.datasource.routing.domain.IDataSourceRouter;
import com.api.erp.v1.main.datasource.routing.domain.IRoutingDataSource;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * INFRASTRUCTURE - Implementação de IRoutingDataSource
 * 
 * DataSource dinâmico que roteia conexões para o tenant apropriado.
 * Estende AbstractDataSource do Spring Framework.
 * 
 * Fluxo:
 * 1. Gets o tenant do contexto atual (ITenantContextProvider)
 * 2. Se tenant == "master", usa masterDataSource diretamente
 * 3. Caso contrário, roteia para o DataSource correto (IDataSourceRouter)
 * 4. Returns uma Connection para o banco do tenant
 * 
 * Durante startup/Hibernate initialization, quando não há tenant definido,
 * o TenantContextProvider retorna "master" (default), permitindo que Hibernate
 * inicialize sem erros.
 * 
 * @author ERP System
 * @version 1.0
 */
@Slf4j
public class CustomRoutingDatasource extends AbstractDataSource implements IRoutingDataSource {
    private final IDataSourceRouter dataSourceRouter;

    public CustomRoutingDatasource(
            IDataSourceRouter dataSourceRouter) {
        this.dataSourceRouter = dataSourceRouter;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Long tenantId = TenantContext.getTenantId();
        
        if (tenantId == null) {
            throw new SQLException("No tenant defined in context. Configure TenantContext before accessing database.");
        }

        try {
            log.debug("Getting connection for tenant: {}", tenantId);
            DataSource tenantDataSource = dataSourceRouter.getDataSource(tenantId);
            Connection connection = tenantDataSource.getConnection();
            
            log.debug("Connection obtained successfully for tenant: {}", tenantId);
            return connection;
        } catch (com.api.erp.v1.main.datasource.routing.domain.TenantDataSourceNotFoundException e) {
            String errorMsg = TenantErrorMessage.DATASOURCE_NOT_CONFIGURED.getMessage() +
                String.format(" Verify if tenant configuration exists in tb_tenant_datasource and is marked as active. Tenant: %d", tenantId);
            log.error("Tenant not found or DataSource not available: {}", tenantId);
            log.debug("Details: {}", e.getMessage());
            throw new SQLException(errorMsg, e);
        } catch (SQLException e) {
            log.error("Error getting connection for tenant: {}", tenantId, e);
            throw e;
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        log.debug("Ignoring supplied username/password, using tenant credentials");
        return getConnection();
    }
}
