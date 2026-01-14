-- ==================================================================================
-- MIGRATION: V1__Master_Database_Init.sql
-- DESCRIÇÃO: Cria estrutura MÍNIMA da master database
-- APENAS tabelas de configuração de tenants
-- ==================================================================================
-- ==================================================================================
-- Tabela: tb_tenant
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_tenant (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        nome VARCHAR(255) NOT NULL,
        email VARCHAR(255),
        telefone VARCHAR(20),
        endereco_id BIGINT,
        ativa BOOLEAN DEFAULT TRUE,
        data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        -- Configuração Geral de Tenant
        config_tipo_acesso VARCHAR(50),
        config_status_ativo BOOLEAN DEFAULT TRUE,
        -- Dados Fiscais
        dados_fiscais_cnpj VARCHAR(20),
        dados_fiscais_razao_social VARCHAR(255),
        dados_fiscais_nome_fantasia VARCHAR(255),
        dados_fiscais_inscricao_estadual VARCHAR(50),
        inscricao_municipal VARCHAR(20),
        dados_fiscais_regime_tributario VARCHAR(50),
        contribuinte_icms VARCHAR(20),
        cnae_principal VARCHAR(7),
        codigo_municipio_ibge VARCHAR(7),
        -- Configurações de Tenant
        usuario_approval_required BOOLEAN DEFAULT FALSE,
        usuario_corporate_email_required BOOLEAN DEFAULT FALSE,
        cliente_validation_enabled BOOLEAN DEFAULT FALSE,
        cliente_audit_enabled BOOLEAN DEFAULT FALSE,
        cliente_cache_enabled BOOLEAN DEFAULT FALSE,
        cliente_notification_enabled BOOLEAN DEFAULT FALSE,
        cliente_tenant_customization_enabled BOOLEAN DEFAULT FALSE,
        cliente_is_default_pf BOOLEAN DEFAULT FALSE,
        cliente_is_default_pj BOOLEAN DEFAULT FALSE,
        cliente_cpf_required BOOLEAN DEFAULT FALSE,
        cliente_cnpj_required BOOLEAN DEFAULT FALSE,
        contato_validation_enabled BOOLEAN DEFAULT FALSE,
        contato_audit_enabled BOOLEAN DEFAULT FALSE,
        contato_cache_enabled BOOLEAN DEFAULT FALSE,
        contato_format_validation_enabled BOOLEAN DEFAULT FALSE,
        contato_notification_enabled BOOLEAN DEFAULT FALSE,
        contato_aprovacao_descricao VARCHAR(500),
        endereco_validation_enabled BOOLEAN DEFAULT FALSE,
        endereco_audit_enabled BOOLEAN DEFAULT FALSE,
        endereco_cache_enabled BOOLEAN DEFAULT FALSE,
        endereco_is_required BOOLEAN DEFAULT TRUE,
        endereco_estado_obrigatorio BOOLEAN DEFAULT TRUE,
        endereco_cidade_obrigatoria BOOLEAN DEFAULT TRUE,
        -- Configurações de Permissão
        permissao_audit_enabled BOOLEAN DEFAULT FALSE,
        permissao_validation_enabled BOOLEAN DEFAULT FALSE,
        permissao_cache_enabled BOOLEAN DEFAULT FALSE,
        permissao_notification_enabled BOOLEAN DEFAULT FALSE,
        permissao_default_policy VARCHAR(100) DEFAULT 'ROLE_BASED',
        -- Configurações de Role
        role_audit_enabled BOOLEAN DEFAULT FALSE,
        role_validation_enabled BOOLEAN DEFAULT FALSE,
        role_cache_enabled BOOLEAN DEFAULT FALSE,
        role_notification_enabled BOOLEAN DEFAULT FALSE,
        role_manager_required BOOLEAN DEFAULT FALSE,
        -- Configurações de Usuário
        usuario_audit_enabled BOOLEAN DEFAULT FALSE,
        usuario_validation_enabled BOOLEAN DEFAULT FALSE,
        usuario_cache_enabled BOOLEAN DEFAULT FALSE,
        -- Configurações de Produto
        produto_audit_enabled BOOLEAN DEFAULT FALSE,
        produto_validation_enabled BOOLEAN DEFAULT FALSE,
        produto_cache_enabled BOOLEAN DEFAULT FALSE,
        produto_notification_enabled BOOLEAN DEFAULT FALSE,
        produto_requires_image BOOLEAN DEFAULT FALSE,
        -- Configurações Gerais
        unidade_medida_validation_enabled BOOLEAN DEFAULT FALSE,
        unidade_medida_cache_enabled BOOLEAN DEFAULT FALSE,
        unidade_medida_system VARCHAR(100) DEFAULT 'METRIC',
        tenant_custom_code VARCHAR(100),
        tenant_slug VARCHAR(100),
        tenant_subdomain VARCHAR(100),
        tenant_type VARCHAR(100),
        tenant_features_enabled BOOLEAN DEFAULT FALSE,
        INDEX idx_tb_tenant_nome (nome),
        INDEX idx_tb_tenant_ativa (ativa),
        UNIQUE INDEX idx_tb_tenant_cnpj (dados_fiscais_cnpj)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: tenant_datasource
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tenant_datasource (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL UNIQUE,
        -- Configurações de conexão
        host VARCHAR(255) NOT NULL,
        port INT NOT NULL DEFAULT 3306,
        database_name VARCHAR(255) NOT NULL,
        username VARCHAR(255) NOT NULL,
        password VARCHAR(255) NOT NULL,
        -- Configuração JDBC
        driver_class_name VARCHAR(255) NOT NULL DEFAULT 'com.mysql.cj.jdbc.Driver',
        dialect VARCHAR(255) NOT NULL DEFAULT 'org.hibernate.dialect.MySQL8Dialect',
        -- Status e testes
        is_active BOOLEAN NOT NULL DEFAULT TRUE,
        tested_at TIMESTAMP NULL,
        test_status VARCHAR(50),
        -- Auditoria
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        -- Índices e constraints
        KEY idx_tenant_id (tenant_id),
        KEY idx_is_active (is_active),
        UNIQUE INDEX idx_tenant_id_active (tenant_id, is_active),
        CONSTRAINT fk_tenant_datasource_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant (id) ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: _prisma_migrations (Controle de Flyway para tenants)
-- ==================================================================================
--    CREATE TABLE
--        IF NOT EXISTS _prisma_migrations (
--            id VARCHAR(36) PRIMARY KEY,
--            checksum VARCHAR(64) NOT NULL,
--            finished_at TIMESTAMP NULL,
--            migration_name VARCHAR(255) NOT NULL,
--            logs TEXT,
--            rolled_back_at TIMESTAMP NULL,
--            started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--            applied_steps_count INT NOT NULL DEFAULT 0
--        ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE tb_endereco (
    -- =========================
    -- ID
    -- =========================
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),

    -- =========================
    -- MULTI-TENANT
    -- =========================
    tenant_id BIGINT NOT NULL,

    -- =========================
    -- DADOS DO ENDEREÇO
    -- =========================
    rua VARCHAR(255) NOT NULL,
    numero VARCHAR(50) NOT NULL,
    complemento VARCHAR(255),
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado CHAR(2) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,

    -- =========================
    -- CUSTOM DATA
    -- =========================
    custom_data JSON,

    -- =========================
    -- AUDITORIA - TEMPO
    -- =========================
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    -- =========================
    -- AUDITORIA - USUÁRIO
    -- =========================
    created_by BIGINT,
    updated_by BIGINT,

    -- =========================
    -- SOFT DELETE
    -- =========================
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME(6),

    -- =========================
    -- CONCORRÊNCIA
    -- =========================
    version BIGINT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE INDEX idx_tb_endereco_tenant
    ON tb_endereco (tenant_id);

CREATE INDEX idx_tb_endereco_deleted
    ON tb_endereco (deleted);

CREATE INDEX idx_tb_endereco_principal
    ON tb_endereco (principal);

CREATE INDEX idx_tb_endereco_cep
    ON tb_endereco (cep);