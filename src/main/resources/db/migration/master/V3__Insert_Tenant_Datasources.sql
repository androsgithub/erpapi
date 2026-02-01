-- ==================================================================================
-- DATASOURCES CONFIGURATION
-- ==================================================================================

-- Datasources para Tenants 1-10 (bancos dedicados)
INSERT IGNORE INTO tb_tenant_datasource (tenant_id, host, port, database_name, username, password, driver_class_name, dialect, is_active)
VALUES
(1, 'localhost', 3306, 'ERPAPI_HECE', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(2, 'localhost', 3306, 'ERPAPI_TECHSOLUTIONS', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(3, 'localhost', 3306, 'ERPAPI_LOGISTICA', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(4, 'localhost', 3306, 'ERPAPI_VAREJO', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(5, 'localhost', 3306, 'ERPAPI_FARMASAUDE', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(6, 'localhost', 3306, 'ERPAPI_AUTOPECAS', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(7, 'localhost', 3306, 'ERPAPI_CONSTRUMAX', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(8, 'localhost', 3306, 'ERPAPI_ELETROTECH', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(9, 'localhost', 3306, 'ERPAPI_ALIMENTOSNATURAIS', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(10, 'localhost', 3306, 'ERPAPI_MODASTYLE', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE);

-- Datasources para Tenants 11-50 (banco compartilhado)
INSERT IGNORE INTO tb_tenant_datasource (tenant_id, host, port, database_name, username, password, driver_class_name, dialect, is_active)
VALUES
(11, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(12, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(13, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(14, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(15, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(16, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(17, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(18, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(19, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(20, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(21, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(22, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(23, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(24, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(25, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(26, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(27, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(28, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(29, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(30, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(31, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(32, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(33, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(34, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(35, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(36, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(37, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(38, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(39, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(40, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(41, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(42, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(43, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(44, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(45, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(46, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(47, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(48, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(49, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE),
(50, 'localhost', 3306, 'ERPAPI_SHARED', 'root', '12345', 'com.mysql.cj.jdbc.Driver', 'org.hibernate.dialect.MySQL8Dialect', TRUE);