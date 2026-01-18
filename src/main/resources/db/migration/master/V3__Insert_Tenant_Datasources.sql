-- ==================================================================================
-- MIGRATION: V3__Insert_Tenant_Datasources.sql
-- DESCRIÇÃO: Insere configuração de datasources para cada tenant
-- ==================================================================================
-- Datasource para Tenant 1 (HECE) - banco dedicado
INSERT IGNORE INTO tb_tenant_datasource (
    tenant_id,
    host,
    port,
    database_name,
    username,
    password,
    driver_class_name,
    dialect,
    is_active
)
VALUES
    (
        1,
        'localhost',
        3306,
        'ERPAPI_HECE',
        'root',
        '12345',
        'com.mysql.cj.jdbc.Driver',
        'org.hibernate.dialect.MySQL8Dialect',
        TRUE
    );

-- Datasource para Tenant 2 (Tech Solutions) - banco compartilhado
INSERT IGNORE INTO tb_tenant_datasource (
    tenant_id,
    host,
    port,
    database_name,
    username,
    password,
    driver_class_name,
    dialect,
    is_active
)
VALUES
    (
        2,
        'localhost',
        3306,
        'ERPAPI_SHARED',
        'root',
        '12345',
        'com.mysql.cj.jdbc.Driver',
        'org.hibernate.dialect.MySQL8Dialect',
        TRUE
    );

-- Datasource para Tenant 3 (Logística Global) - banco compartilhado
INSERT IGNORE INTO tb_tenant_datasource (
    tenant_id,
    host,
    port,
    database_name,
    username,
    password,
    driver_class_name,
    dialect,
    is_active
)
VALUES
    (
        3,
        'localhost',
        3306,
        'ERPAPI_SHARED',
        'root',
        '12345',
        'com.mysql.cj.jdbc.Driver',
        'org.hibernate.dialect.MySQL8Dialect',
        TRUE
    );

-- Datasource para Tenant 4 (Varejo Express) - banco compartilhado
INSERT IGNORE INTO tb_tenant_datasource (
    tenant_id,
    host,
    port,
    database_name,
    username,
    password,
    driver_class_name,
    dialect,
    is_active
)
VALUES
    (
        4,
        'localhost',
        3306,
        'ERPAPI_SHARED',
        'root',
        '12345',
        'com.mysql.cj.jdbc.Driver',
        'org.hibernate.dialect.MySQL8Dialect',
        TRUE
    );