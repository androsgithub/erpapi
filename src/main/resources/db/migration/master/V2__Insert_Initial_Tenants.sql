-- ==================================================================================
-- MIGRATION: V2__Insert_Initial_Tenants.sql
-- DESCRIÇÃO: Insere tenants de teste iniciais na master database
-- ==================================================================================
-- Tenant 1: HECE Distribuidora
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    tenant_slug
)
VALUES
    (
        1,
        'HECE Distribuidora',
        'contato@hece.test',
        '(11) 9999-8888',
        TRUE,
        '81901548000104',
        'HECE Distribuidora LTDA',
        'HECE',
        'LUCRO_PRESUMIDO',
        'ERPAPI_HECE'
    );

-- Tenant 2: Tech Solutions
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    tenant_slug
)
VALUES
    (
        2,
        'Tech Solutions',
        'contato@techsolutions.test',
        '(11) 8888-7777',
        TRUE,
        '17380471000175',
        'Tech Solutions Brazil LTDA',
        'Tech Solutions',
        'SIMPLES_NACIONAL',
        'ERPAPI_SHARED'
    );

-- Tenant 3: Logística Global
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    tenant_slug
)
VALUES
    (
        3,
        'Logística Global',
        'contato@logistica.test',
        '(11) 7777-6666',
        TRUE,
        '80297991000155',
        'Logística Global Express LTDA',
        'Logística Global',
        'LUCRO_PRESUMIDO',
        'ERPAPI_SHARED'
    );

-- Tenant 4: Varejo Express
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    tenant_slug
)
VALUES
    (
        4,
        'Varejo Express',
        'contato@varejo.test',
        '(11) 6666-5555',
        TRUE,
        '58071930000116',
        'Varejo Express Distribution LTDA',
        'Varejo Express',
        'SIMPLES_NACIONAL',
        'ERPAPI_SHARED'
    );