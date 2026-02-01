-- ==================================================================================
-- MIGRATION: V1__Create_Base_Tables.sql (Tenant)
-- DESCRIÇÃO: Cria estrutura base de tabelas para todos os tenants
-- APLICÁVEL: Todos os tenants (executa em cada banco individual)
-- ==================================================================================
-- ==================================================================================
-- Tabela: tb_cliente
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_cliente (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        tenant_id BIGINT NOT NULL,
        nome VARCHAR(255) NOT NULL,
        status VARCHAR(50) NOT NULL DEFAULT 'ATIVO',
        tipo VARCHAR(50) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- ==================================================================================
-- Tabela: tb_endereco
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_endereco (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        rua VARCHAR(255) NOT NULL,
        numero VARCHAR(50) NOT NULL,
        complemento VARCHAR(255),
        bairro VARCHAR(100) NOT NULL,
        cidade VARCHAR(100) NOT NULL,
        estado VARCHAR(2) NOT NULL,
        cep VARCHAR(8) NOT NULL,
        tipo VARCHAR(50) NOT NULL,
        principal BOOLEAN NOT NULL DEFAULT FALSE,
        custom_data JSON,
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        created_by BIGINT,
        updated_by BIGINT,
        deleted BOOLEAN NOT NULL DEFAULT FALSE,
        deleted_at DATETIME,
        version BIGINT DEFAULT 0,
        UNIQUE KEY uk_cep_numero (cep, numero),
        INDEX idx_tenant_id (tenant_id),
        INDEX idx_cidade (cidade),
        INDEX idx_estado (estado),
        INDEX idx_deleted (deleted)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
-- ==================================================================================
-- Tabela: tb_contato
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_contato (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        tenant_id BIGINT NOT NULL,
        tipo VARCHAR(50) NOT NULL,
        valor VARCHAR(255) NOT NULL,
        descricao VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- ==================================================================================
-- Tabela: tb_unidade_medida
-- ==================================================================================
CREATE TABLE tb_unidade_medida (
    -- ===== Identificação =====
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,

    -- ===== Campos de negócio =====
    sigla VARCHAR(10) NOT NULL,
    descricao VARCHAR(100) NOT NULL,

    -- ===== Infra / Auditoria =====
    custom_data JSON,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    created_by BIGINT,
    updated_by BIGINT,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,

    version BIGINT NOT NULL DEFAULT 0,

    -- ===== Constraints =====
    CONSTRAINT uk_tb_unidade_medida_sigla_tenant UNIQUE (sigla, tenant_id)
);


-- ==================================================================================
-- Tabela: tb_role
-- ==================================================================================
CREATE TABLE tb_role (
    -- ===== Identificação =====
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,

    -- ===== Campos de negócio =====
    nome VARCHAR(255) NOT NULL,

    -- ===== Infra / Auditoria =====
    custom_data JSON,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    created_by BIGINT,
    updated_by BIGINT,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,

    version BIGINT NOT NULL DEFAULT 0,

    -- ===== Constraints =====
    CONSTRAINT uk_tb_role_nome_tenant UNIQUE (nome, tenant_id)
);

-- ==================================================================================
-- Tabela: tb_permissao
-- ==================================================================================
CREATE TABLE tb_permissao (
    -- ===== Identificação =====
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,

    -- ===== Campos de negócio =====
    codigo VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    modulo VARCHAR(255) NOT NULL,
    acao VARCHAR(50) NOT NULL,

    -- ===== Infra / Auditoria =====
    custom_data JSON,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    created_by BIGINT,
    updated_by BIGINT,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,

    version BIGINT NOT NULL DEFAULT 0,

    -- ===== Constraints =====
    CONSTRAINT uk_tb_permissao_codigo UNIQUE (codigo,tenant_id)
);


-- ==================================================================================
-- Tabela: tb_produto
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_produto (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        tenant_id BIGINT NOT NULL,
        nome VARCHAR(255) NOT NULL,
        sku VARCHAR(100),
        descricao TEXT,
        preco DECIMAL(10, 2),
        status VARCHAR(50) DEFAULT 'ATIVO',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- ==================================================================================
-- Tabela: tb_cliente_dados_fiscais
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_cliente_dados_fiscais (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        tenant_id BIGINT NOT NULL,
        cliente_id BIGINT,
        cnpj VARCHAR(20),
        cpf VARCHAR(20),
        razao_social VARCHAR(255),
        nome_fantasia VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- ==================================================================================
-- Tabela: tb_cliente_dados_financeiros
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_cliente_dados_financeiros (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        tenant_id BIGINT NOT NULL,
        cliente_id BIGINT,
        limite_credito DECIMAL(15, 2),
        dias_pagamento INT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- ==================================================================================
-- Tabela: tb_cliente_endereco
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_cliente_endereco (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        tenant_id BIGINT NOT NULL,
        cliente_id BIGINT,
        endereco_id BIGINT,
        tipo VARCHAR(50),
        principal BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- ==================================================================================
-- Tabela: tb_cliente_contato
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_cliente_contato (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        tenant_id BIGINT NOT NULL,
        cliente_id BIGINT,
        contato_id BIGINT,
        principal BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );


-- ==================================================================================
-- Tabela: tb_role_permissao
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_role_permissao (
        role_id BIGINT NOT NULL,
        permissao_id BIGINT NOT NULL,
        PRIMARY KEY (role_id, permissao_id),
        CONSTRAINT fk_tb_role_permissao_role FOREIGN KEY (role_id) REFERENCES tb_role (id) ON DELETE CASCADE,
        CONSTRAINT fk_tb_role_permissao_permissao FOREIGN KEY (permissao_id) REFERENCES tb_permissao (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: tb_usuario
-- ==================================================================================
CREATE TABLE IF NOT EXISTS tb_usuario (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    cpf VARCHAR(11),
    senha_hash VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'ATIVO',
    aprovado_por BIGINT,
    approved_at DATETIME,
    custom_data JSON,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME,
    version BIGINT DEFAULT 0,
    UNIQUE KEY uk_email_tenant (email, tenant_id),
    INDEX idx_email (email),
    INDEX idx_cpf (cpf),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_deleted (deleted),
    INDEX idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
CREATE INDEX idx_usuario_tenant_status ON tb_usuario (tenant_id, status);
CREATE INDEX idx_usuario_tenant_deleted ON tb_usuario (tenant_id, deleted);

-- ==================================================================================
-- Tabela: tb_usuario_permissao
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_usuario_permissao (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        usuario_id BIGINT NOT NULL,
        data_inicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        data_fim DATETIME,
        ativo BOOLEAN NOT NULL DEFAULT TRUE,
        CONSTRAINT fk_usuario_permissao_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario (id) ON DELETE CASCADE,
        INDEX idx_usuario_permissao_usuario (usuario_id),
        INDEX idx_usuario_permissao_ativo (ativo)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: tb_usuario_role
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_usuario_role (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        usuario_permissao_id BIGINT NOT NULL,
        role_id BIGINT NOT NULL,
        CONSTRAINT fk_tb_usuario_role_usuario_permissao FOREIGN KEY (usuario_permissao_id) REFERENCES tb_usuario_permissao (id) ON DELETE CASCADE,
        CONSTRAINT fk_tb_usuario_role_role FOREIGN KEY (role_id) REFERENCES tb_role (id) ON DELETE CASCADE,
        UNIQUE KEY uk_tb_usuario_role (usuario_permissao_id, role_id),
        INDEX idx_tb_usuario_role_role (role_id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: tb_usuario_permissao_direta
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_usuario_permissao_direta (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        usuario_permissao_id BIGINT NOT NULL,
        permissao_id BIGINT NOT NULL,
        CONSTRAINT fk_tb_usuario_permissao_direta_usuario_permissao FOREIGN KEY (usuario_permissao_id) REFERENCES tb_usuario_permissao (id) ON DELETE CASCADE,
        CONSTRAINT fk_tb_usuario_permissao_direta_permissao FOREIGN KEY (permissao_id) REFERENCES tb_permissao (id) ON DELETE CASCADE,
        UNIQUE KEY uk_tb_usuario_permissao_direta (usuario_permissao_id, permissao_id),
        INDEX idx_tb_usuario_permissao_direta_permissao (permissao_id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: tb_contatos (atualizada de tb_contato)
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_contatos (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        tipo VARCHAR(50) NOT NULL,
        valor VARCHAR(255) NOT NULL,
        descricao VARCHAR(255),
        principal BOOLEAN NOT NULL DEFAULT FALSE,
        ativo BOOLEAN NOT NULL DEFAULT TRUE,
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        created_by BIGINT,
        updated_by BIGINT,
        deleted BOOLEAN NOT NULL DEFAULT FALSE,
        deleted_at DATETIME,
        version BIGINT DEFAULT 0,
        INDEX idx_tenant_id (tenant_id),
        INDEX idx_tipo (tipo),
        INDEX idx_deleted (deleted)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: usuario_contato
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_usuario_contato (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        usuario_id BIGINT NOT NULL,
        contato_id BIGINT NOT NULL,
        CONSTRAINT fk_usuario_contato_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario (id) ON DELETE CASCADE,
        CONSTRAINT fk_usuario_contato_contato FOREIGN KEY (contato_id) REFERENCES tb_contatos (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Criar Índices
-- ==================================================================================
CREATE INDEX idx_cliente_tenant ON tb_cliente (tenant_id);

CREATE INDEX idx_endereco_tenant ON tb_endereco (tenant_id);

CREATE INDEX idx_contato_tenant ON tb_contato (tenant_id);

CREATE INDEX idx_unidade_tenant ON tb_unidade_medida (tenant_id);

CREATE INDEX idx_role_tenant ON tb_role (tenant_id);

CREATE INDEX idx_permissao_tenant ON tb_permissao (tenant_id);

CREATE INDEX idx_produto_tenant ON tb_produto (tenant_id);

-- Multi-tenant (filtro principal)
CREATE INDEX idx_tb_permissao_tenant
    ON tb_permissao (tenant_id);

-- Tenant + soft delete (caso comum)
CREATE INDEX idx_tb_permissao_tenant_deleted
    ON tb_permissao (tenant_id, deleted);

-- Busca por código (além do unique)
CREATE INDEX idx_tb_permissao_codigo
    ON tb_permissao (codigo);
