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
CREATE TABLE
    IF NOT EXISTS tb_unidade_medida (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        tenant_id BIGINT NOT NULL,
        nome VARCHAR(100) NOT NULL,
        sigla VARCHAR(10) NOT NULL,
        tipo VARCHAR(50),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- ==================================================================================
-- Tabela: tb_role
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_role (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        tenant_id BIGINT NOT NULL,
        nome VARCHAR(100) NOT NULL,
        descricao VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- ==================================================================================
-- Tabela: tb_permissao
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS tb_permissao (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        tenant_id BIGINT NOT NULL,
        nome VARCHAR(100) NOT NULL,
        descricao VARCHAR(255),
        modulo VARCHAR(100),
        acao VARCHAR(50),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
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
-- Criar Índices
-- ==================================================================================
CREATE INDEX idx_cliente_tenant ON tb_cliente (tenant_id);

CREATE INDEX idx_endereco_tenant ON tb_endereco (tenant_id);

CREATE INDEX idx_contato_tenant ON tb_contato (tenant_id);

CREATE INDEX idx_unidade_tenant ON tb_unidade_medida (tenant_id);

CREATE INDEX idx_role_tenant ON tb_role (tenant_id);

CREATE INDEX idx_permissao_tenant ON tb_permissao (tenant_id);

CREATE INDEX idx_produto_tenant ON tb_produto (tenant_id);