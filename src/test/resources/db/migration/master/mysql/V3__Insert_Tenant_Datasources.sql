-- ==================================================================================
-- DATASOURCES CONFIGURATION
-- ==================================================================================

-- Datasources para Tenants 1-10 (bancos dedicados)
INSERT IGNORE INTO tb_tenant_datasource (tenant_id, host, port, database_name, username, password, db_type, is_active)
VALUES
(1, 'localhost', 3306, 'ERPAPI_HECE', 'sa', '', 'H2', TRUE),
(2, 'localhost', 3306, 'ERPAPI_TECHSOLUTIONS', 'sa', '', 'H2', TRUE),
(3, 'localhost', 3306, 'ERPAPI_LOGISTICA', 'sa', '', 'H2', TRUE),
(4, 'localhost', 3306, 'ERPAPI_VAREJO', 'sa', '', 'H2', TRUE),
(5, 'localhost', 3306, 'ERPAPI_FARMASAUDE', 'sa', '', 'H2', TRUE),
(6, 'localhost', 3306, 'ERPAPI_AUTOPECAS', 'sa', '', 'H2', TRUE),
(7, 'localhost', 3306, 'ERPAPI_CONSTRUMAX', 'sa', '', 'H2', TRUE),
(8, 'localhost', 3306, 'ERPAPI_ELETROTECH', 'sa', '', 'H2', TRUE),
(9, 'localhost', 3306, 'ERPAPI_ALIMENTOSNATURAIS', 'sa', '', 'H2', TRUE),
(10, 'localhost', 3306, 'ERPAPI_MODASTYLE', 'sa', '', 'H2', TRUE);

-- Datasources para Tenants 11-50 (banco compartilhado)
INSERT IGNORE INTO tb_tenant_datasource (tenant_id, host, port, database_name, username, password, db_type, is_active)
VALUES
(11, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(12, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(13, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(14, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(15, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(16, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(17, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(18, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(19, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(20, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(21, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(22, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(23, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(24, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(25, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(26, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(27, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(28, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(29, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(30, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(31, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(32, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(33, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(34, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(35, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(36, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(37, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(38, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(39, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(40, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(41, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(42, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(43, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(44, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(45, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(46, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(47, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(48, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(49, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE),
(50, 'localhost', 3306, 'ERPAPI_SHARED', 'sa', '', 'H2', TRUE);