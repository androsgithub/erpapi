package com.api.erp.v1.observability.application.mapper.strategy;

import com.api.erp.v1.observability.domain.FlowStatus;

/**
 * Estratégia para mapeamento de erros de banco de dados.
 * 
 * Mapeia DataAccessException, SQLException, PersistenceException,
 * DatabaseException, HibernateException, JpaSystemException,
 * QueryTimeoutException para FlowStatus.ERROR_DATABASE.
 */
public class DatabaseErrorMappingStrategy implements ErrorMappingStrategy {

    @Override
    public boolean canHandle(Throwable exception) {
        if (exception == null) {
            return false;
        }

        String className = exception.getClass().getName();
        return className.contains("DataAccessException") ||
               className.contains("SQLException") ||
               className.contains("PersistenceException") ||
               className.contains("DatabaseException") ||
               className.contains("HibernateException") ||
               className.contains("JpaSystemException") ||
               className.contains("QueryTimeoutException");
    }

    @Override
    public FlowStatus map(Throwable exception) {
        return FlowStatus.ERROR_DATABASE;
    }

    @Override
    public int getPriority() {
        return 85;
    }
}
