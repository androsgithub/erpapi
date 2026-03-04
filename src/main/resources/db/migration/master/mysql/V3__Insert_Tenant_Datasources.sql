-- ==================================================================================
-- DATASOURCES CONFIGURATION
-- ==================================================================================

-- Datasources para Tenants 1-10 (bancos dedicados)
INSERT IGNORE INTO tb_tenant_datasource (tenant_id, host, port, database_name, username, password, db_type, is_active)
VALUES
(1, 'localhost', 3306, 'ERPAPI_HECE', 'root', '12345', 'MYSQL', TRUE),
(2, 'localhost', 3306, 'ERPAPI_TECHSOLUTIONS', 'root', '12345', 'MYSQL', TRUE),
(3, 'localhost', 3306, 'ERPAPI_LOGISTICA', 'root', '12345', 'MYSQL', TRUE),
(4, 'localhost', 3306, 'ERPAPI_VAREJO', 'root', '12345', 'MYSQL', TRUE),
(5, 'localhost', 3306, 'ERPAPI_FARMASAUDE', 'root', '12345', 'MYSQL', TRUE),
(6, 'localhost', 3306, 'ERPAPI_AUTOPECAS', 'root', '12345', 'MYSQL', TRUE),
(7, 'localhost', 3306, 'ERPAPI_CONSTRUMAX', 'root', '12345', 'MYSQL', TRUE),
(8, 'localhost', 3306, 'ERPAPI_ELETROTECH', 'root', '12345', 'MYSQL', TRUE),
(9, 'localhost', 3306, 'ERPAPI_ALIMENTOSNATURAIS', 'root', '12345', 'MYSQL', TRUE),
(10, 'localhost', 3306, 'ERPAPI_MODASTYLE', 'root', '12345', 'MYSQL', TRUE);

-- Datasources para Tenants 11-50 (banco compartilhado)
INSERT IGNORE INTO tb_tenant_datasource (tenant_id, host, port, database_name, username, password, db_type, is_active)
VALUES
(11, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(12, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(13, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(14, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(15, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(16, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(17, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(18, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(19, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(20, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(21, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(22, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(23, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(24, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(25, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(26, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(27, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(28, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(29, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(30, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(31, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(32, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(33, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(34, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(35, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(36, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(37, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(38, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(39, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(40, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(41, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(42, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(43, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(44, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(45, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(46, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(47, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(48, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(49, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE),
(50, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'MYSQL', TRUE);