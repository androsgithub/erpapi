package com.api.erp.v1.main.datasource.routing.domain;

import javax.sql.DataSource;

/**
 * DOMAIN - Interface para DataSource com suporte a roteamento
 * 
 * Marca DataSource que suporta roteamento dinâmico de conexões.
 * 
 * @author ERP System
 * @version 1.0
 */
public interface IRoutingDataSource extends DataSource {
    // Marker interface
}
