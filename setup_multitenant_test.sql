-- ==================================================================================
-- SCRIPT: Setup Multi-Tenant Testing
-- DESCRIÇÃO: Insere 3 tenants de teste com suas configurações de datasource
--
-- Cenário:
-- - Tenant 1 (id=1, slug="tenant1"): banco próprio (tenant1_db)
-- - Tenant 2 (id=2, slug="tenant2"): banco compartilhado (shared_db)
-- - Tenant 3 (id=3, slug="tenant3"): banco compartilhado (shared_db), discriminado por tenantId
-- ==================================================================================
USE erpapi;

-- ==================================================================================
-- PASSO 1: Limpar dados anteriores (opcional, comentado)
-- ==================================================================================
-- DELETE FROM tenant_datasource;
-- DELETE FROM tb_tenant WHERE nome IN ('Tenant Test 1', 'Tenant Test 2', 'Tenant Test 3');
-- ==================================================================================
-- PASSO 2: Inserir TENANT 1 (banco próprio)
-- ==================================================================================
INSERT INTO
    tb_tenant (
        nome,
        email,
        telefone,
        endereco_id,
        ativa,
        data_criacao,
        data_atualizacao,
        dados_fiscais_cnpj,
        dados_fiscais_razao_social,
        dados_fiscais_nome_fantasia,
        dados_fiscais_inscricao_estadual,
        inscricao_municipal,
        dados_fiscais_regime_tributario,
        contribuinte_icms,
        cnae_principal,
        codigo_municipio_ibge,
        usuario_approval_required,
        usuario_corporate_email_required,
        cliente_validation_enabled,
        cliente_audit_enabled,
        cliente_cache_enabled,
        cliente_notification_enabled,
        cliente_tenant_customization_enabled,
        cliente_is_default_pf,
        cliente_is_default_pj,
        cliente_cpf_required,
        cliente_cnpj_required,
        contato_validation_enabled,
        contato_audit_enabled,
        contato_cache_enabled,
        contato_format_validation_enabled,
        contato_notification_enabled,
        contato_aprovacao_descricao,
        endereco_validation_enabled,
        endereco_audit_enabled,
        endereco_cache_enabled,
        endereco_is_required,
        endereco_estado_obrigatorio,
        endereco_cidade_obrigatoria,
        permissao_validation_enabled,
        permissao_cache_enabled,
        permissao_audit_enabled,
        permissao_default_policy,
        unidade_medida_validation_enabled,
        unidade_medida_cache_enabled,
        unidade_medida_system,
        produto_validation_enabled,
        produto_audit_enabled,
        produto_cache_enabled,
        produto_requires_image,
        tenant_type,
        tenant_slug,
        tenant_subdomain,
        tenant_custom_code,
        tenant_features_enabled
    )
VALUES
    (
        'Tenant Test 1', -- nome
        'admin@tenant1.test', -- email
        '(11) 1111-1111', -- telefone
        NULL, -- endereco_id
        TRUE, -- ativa
        CURRENT_TIMESTAMP, -- data_criacao
        CURRENT_TIMESTAMP, -- data_atualizacao
        '11111111000001', -- dados_fiscais_cnpj
        'Tenant Test 1 Ltda', -- dados_fiscais_razao_social
        'Tenant 1', -- dados_fiscais_nome_fantasia
        '111111111.111.111', -- dados_fiscais_inscricao_estadual
        '111111', -- inscricao_municipal
        'SIMPLES_NACIONAL', -- dados_fiscais_regime_tributario
        'CONTRIBUINTE', -- contribuinte_icms
        '4789101', -- cnae_principal
        '3550308', -- codigo_municipio_ibge
        FALSE, -- usuario_approval_required
        FALSE, -- usuario_corporate_email_required
        FALSE, -- cliente_validation_enabled
        FALSE, -- cliente_audit_enabled
        FALSE, -- cliente_cache_enabled
        FALSE, -- cliente_notification_enabled
        FALSE, -- cliente_tenant_customization_enabled
        FALSE, -- cliente_is_default_pf
        TRUE, -- cliente_is_default_pj
        FALSE, -- cliente_cpf_required
        TRUE, -- cliente_cnpj_required
        FALSE, -- contato_validation_enabled
        FALSE, -- contato_audit_enabled
        FALSE, -- contato_cache_enabled
        FALSE, -- contato_format_validation_enabled
        FALSE, -- contato_notification_enabled
        NULL, -- contato_aprovacao_descricao
        FALSE, -- endereco_validation_enabled
        FALSE, -- endereco_audit_enabled
        FALSE, -- endereco_cache_enabled
        TRUE, -- endereco_is_required
        TRUE, -- endereco_estado_obrigatorio
        TRUE, -- endereco_cidade_obrigatoria
        FALSE, -- permissao_validation_enabled
        FALSE, -- permissao_cache_enabled
        FALSE, -- permissao_audit_enabled
        'ROLE_BASED', -- permissao_default_policy
        FALSE, -- unidade_medida_validation_enabled
        FALSE, -- unidade_medida_cache_enabled
        'METRIC', -- unidade_medida_system
        FALSE, -- produto_validation_enabled
        FALSE, -- produto_audit_enabled
        FALSE, -- produto_cache_enabled
        FALSE, -- produto_requires_image
        'DEFAULT', -- tenant_type
        'tenant1', -- tenant_slug
        'tenant1', -- tenant_subdomain
        NULL, -- tenant_custom_code
        TRUE -- tenant_features_enabled
    );

-- ==================================================================================
-- PASSO 3: Inserir TENANT 2 (banco compartilhado - shared_db)
-- ==================================================================================
INSERT INTO
    tb_tenant (
        nome,
        email,
        telefone,
        endereco_id,
        ativa,
        data_criacao,
        data_atualizacao,
        dados_fiscais_cnpj,
        dados_fiscais_razao_social,
        dados_fiscais_nome_fantasia,
        dados_fiscais_inscricao_estadual,
        inscricao_municipal,
        dados_fiscais_regime_tributario,
        contribuinte_icms,
        cnae_principal,
        codigo_municipio_ibge,
        usuario_approval_required,
        usuario_corporate_email_required,
        cliente_validation_enabled,
        cliente_audit_enabled,
        cliente_cache_enabled,
        cliente_notification_enabled,
        cliente_tenant_customization_enabled,
        cliente_is_default_pf,
        cliente_is_default_pj,
        cliente_cpf_required,
        cliente_cnpj_required,
        contato_validation_enabled,
        contato_audit_enabled,
        contato_cache_enabled,
        contato_format_validation_enabled,
        contato_notification_enabled,
        contato_aprovacao_descricao,
        endereco_validation_enabled,
        endereco_audit_enabled,
        endereco_cache_enabled,
        endereco_is_required,
        endereco_estado_obrigatorio,
        endereco_cidade_obrigatoria,
        permissao_validation_enabled,
        permissao_cache_enabled,
        permissao_audit_enabled,
        permissao_default_policy,
        unidade_medida_validation_enabled,
        unidade_medida_cache_enabled,
        unidade_medida_system,
        produto_validation_enabled,
        produto_audit_enabled,
        produto_cache_enabled,
        produto_requires_image,
        tenant_type,
        tenant_slug,
        tenant_subdomain,
        tenant_custom_code,
        tenant_features_enabled
    )
VALUES
    (
        'Tenant Test 2', -- nome
        'admin@tenant2.test', -- email
        '(11) 2222-2222', -- telefone
        NULL, -- endereco_id
        TRUE, -- ativa
        CURRENT_TIMESTAMP, -- data_criacao
        CURRENT_TIMESTAMP, -- data_atualizacao
        '22222222000002', -- dados_fiscais_cnpj
        'Tenant Test 2 Ltda', -- dados_fiscais_razao_social
        'Tenant 2', -- dados_fiscais_nome_fantasia
        '222222222.222.222', -- dados_fiscais_inscricao_estadual
        '222222', -- inscricao_municipal
        'SIMPLES_NACIONAL', -- dados_fiscais_regime_tributario
        'CONTRIBUINTE', -- contribuinte_icms
        '4789101', -- cnae_principal
        '3550308', -- codigo_municipio_ibge
        FALSE, -- usuario_approval_required
        FALSE, -- usuario_corporate_email_required
        FALSE, -- cliente_validation_enabled
        FALSE, -- cliente_audit_enabled
        FALSE, -- cliente_cache_enabled
        FALSE, -- cliente_notification_enabled
        FALSE, -- cliente_tenant_customization_enabled
        FALSE, -- cliente_is_default_pf
        TRUE, -- cliente_is_default_pj
        FALSE, -- cliente_cpf_required
        TRUE, -- cliente_cnpj_required
        FALSE, -- contato_validation_enabled
        FALSE, -- contato_audit_enabled
        FALSE, -- contato_cache_enabled
        FALSE, -- contato_format_validation_enabled
        FALSE, -- contato_notification_enabled
        NULL, -- contato_aprovacao_descricao
        FALSE, -- endereco_validation_enabled
        FALSE, -- endereco_audit_enabled
        FALSE, -- endereco_cache_enabled
        TRUE, -- endereco_is_required
        TRUE, -- endereco_estado_obrigatorio
        TRUE, -- endereco_cidade_obrigatoria
        FALSE, -- permissao_validation_enabled
        FALSE, -- permissao_cache_enabled
        FALSE, -- permissao_audit_enabled
        'ROLE_BASED', -- permissao_default_policy
        FALSE, -- unidade_medida_validation_enabled
        FALSE, -- unidade_medida_cache_enabled
        'METRIC', -- unidade_medida_system
        FALSE, -- produto_validation_enabled
        FALSE, -- produto_audit_enabled
        FALSE, -- produto_cache_enabled
        FALSE, -- produto_requires_image
        'DEFAULT', -- tenant_type
        'tenant2', -- tenant_slug
        'tenant2', -- tenant_subdomain
        NULL, -- tenant_custom_code
        TRUE -- tenant_features_enabled
    );

-- ==================================================================================
-- PASSO 4: Inserir TENANT 3 (banco compartilhado - shared_db, discriminado por tenantId)
-- ==================================================================================
INSERT INTO
    tb_tenant (
        nome,
        email,
        telefone,
        endereco_id,
        ativa,
        data_criacao,
        data_atualizacao,
        dados_fiscais_cnpj,
        dados_fiscais_razao_social,
        dados_fiscais_nome_fantasia,
        dados_fiscais_inscricao_estadual,
        inscricao_municipal,
        dados_fiscais_regime_tributario,
        contribuinte_icms,
        cnae_principal,
        codigo_municipio_ibge,
        usuario_approval_required,
        usuario_corporate_email_required,
        cliente_validation_enabled,
        cliente_audit_enabled,
        cliente_cache_enabled,
        cliente_notification_enabled,
        cliente_tenant_customization_enabled,
        cliente_is_default_pf,
        cliente_is_default_pj,
        cliente_cpf_required,
        cliente_cnpj_required,
        contato_validation_enabled,
        contato_audit_enabled,
        contato_cache_enabled,
        contato_format_validation_enabled,
        contato_notification_enabled,
        contato_aprovacao_descricao,
        endereco_validation_enabled,
        endereco_audit_enabled,
        endereco_cache_enabled,
        endereco_is_required,
        endereco_estado_obrigatorio,
        endereco_cidade_obrigatoria,
        permissao_validation_enabled,
        permissao_cache_enabled,
        permissao_audit_enabled,
        permissao_default_policy,
        unidade_medida_validation_enabled,
        unidade_medida_cache_enabled,
        unidade_medida_system,
        produto_validation_enabled,
        produto_audit_enabled,
        produto_cache_enabled,
        produto_requires_image,
        tenant_type,
        tenant_slug,
        tenant_subdomain,
        tenant_custom_code,
        tenant_features_enabled
    )
VALUES
    (
        'Tenant Test 3', -- nome
        'admin@tenant3.test', -- email
        '(11) 3333-3333', -- telefone
        NULL, -- endereco_id
        TRUE, -- ativa
        CURRENT_TIMESTAMP, -- data_criacao
        CURRENT_TIMESTAMP, -- data_atualizacao
        '33333333000003', -- dados_fiscais_cnpj
        'Tenant Test 3 Ltda', -- dados_fiscais_razao_social
        'Tenant 3', -- dados_fiscais_nome_fantasia
        '333333333.333.333', -- dados_fiscais_inscricao_estadual
        '333333', -- inscricao_municipal
        'SIMPLES_NACIONAL', -- dados_fiscais_regime_tributario
        'CONTRIBUINTE', -- contribuinte_icms
        '4789101', -- cnae_principal
        '3550308', -- codigo_municipio_ibge
        FALSE, -- usuario_approval_required
        FALSE, -- usuario_corporate_email_required
        FALSE, -- cliente_validation_enabled
        FALSE, -- cliente_audit_enabled
        FALSE, -- cliente_cache_enabled
        FALSE, -- cliente_notification_enabled
        FALSE, -- cliente_tenant_customization_enabled
        FALSE, -- cliente_is_default_pf
        TRUE, -- cliente_is_default_pj
        FALSE, -- cliente_cpf_required
        TRUE, -- cliente_cnpj_required
        FALSE, -- contato_validation_enabled
        FALSE, -- contato_audit_enabled
        FALSE, -- contato_cache_enabled
        FALSE, -- contato_format_validation_enabled
        FALSE, -- contato_notification_enabled
        NULL, -- contato_aprovacao_descricao
        FALSE, -- endereco_validation_enabled
        FALSE, -- endereco_audit_enabled
        FALSE, -- endereco_cache_enabled
        TRUE, -- endereco_is_required
        TRUE, -- endereco_estado_obrigatorio
        TRUE, -- endereco_cidade_obrigatoria
        FALSE, -- permissao_validation_enabled
        FALSE, -- permissao_cache_enabled
        FALSE, -- permissao_audit_enabled
        'ROLE_BASED', -- permissao_default_policy
        FALSE, -- unidade_medida_validation_enabled
        FALSE, -- unidade_medida_cache_enabled
        'METRIC', -- unidade_medida_system
        FALSE, -- produto_validation_enabled
        FALSE, -- produto_audit_enabled
        FALSE, -- produto_cache_enabled
        FALSE, -- produto_requires_image
        'DEFAULT', -- tenant_type
        'tenant3', -- tenant_slug
        'tenant3', -- tenant_subdomain
        NULL, -- tenant_custom_code
        TRUE -- tenant_features_enabled
    );

-- ==================================================================================
-- PASSO 5: Inserir configurações de DATASOURCE
-- ==================================================================================
-- TENANT 1: banco próprio (tenant1_db)
INSERT INTO
    tenant_datasource (
        tenant_id,
        host,
        port,
        database_name,
        username,
        password,
        driver_class_name,
        dialect,
        is_active,
        created_at,
        updated_at
    )
VALUES
    (
        1, -- tenant_id para Tenant Test 1
        'localhost',
        3306,
        'tenant1_db',
        'root',
        '12345',
        'com.mysql.cj.jdbc.Driver',
        'org.hibernate.dialect.MySQL8Dialect',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- TENANT 2: banco compartilhado (shared_db)
INSERT INTO
    tenant_datasource (
        tenant_id,
        host,
        port,
        database_name,
        username,
        password,
        driver_class_name,
        dialect,
        is_active,
        created_at,
        updated_at
    )
VALUES
    (
        2, -- tenant_id para Tenant Test 2
        'localhost',
        3306,
        'shared_db',
        'root',
        '12345',
        'com.mysql.cj.jdbc.Driver',
        'org.hibernate.dialect.MySQL8Dialect',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- TENANT 3: banco compartilhado (shared_db) - discriminado por tenantId
INSERT INTO
    tenant_datasource (
        tenant_id,
        host,
        port,
        database_name,
        username,
        password,
        driver_class_name,
        dialect,
        is_active,
        created_at,
        updated_at
    )
VALUES
    (
        3, -- tenant_id para Tenant Test 3
        'localhost',
        3306,
        'shared_db',
        'root',
        '12345',
        'com.mysql.cj.jdbc.Driver',
        'org.hibernate.dialect.MySQL8Dialect',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- ==================================================================================
-- PASSO 6: Verificar os dados inseridos
-- ==================================================================================
SELECT
    '=== TENANTS CRIADOS ===' AS '';

SELECT
    id,
    nome,
    tenant_slug
FROM
    tb_tenant
WHERE
    nome LIKE 'Tenant Test%'
ORDER BY
    id;

SELECT
    '' AS '';

SELECT
    '=== DATASOURCES CONFIGURADOS ===' AS '';

SELECT
    td.id,
    t.nome,
    t.tenant_slug,
    td.host,
    td.port,
    td.database_name
FROM
    tenant_datasource td
    JOIN tb_tenant t ON td.tenant_id = t.id
WHERE
    t.nome LIKE 'Tenant Test%'
ORDER BY
    td.tenant_id;