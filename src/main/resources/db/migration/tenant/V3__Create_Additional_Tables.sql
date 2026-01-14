-- ==================================================================================
-- MIGRATION: V3__Create_Additional_Tables.sql (Tenant)
-- DESCRIÇÃO: Cria tabelas adicionais essenciais (permissões, roles, etc)
-- APLICÁVEL: Todos os tenants
-- ==================================================================================
-- ==================================================================================
-- Tabela: unidade_medida
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS unidade_medida (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        sigla VARCHAR(10) NOT NULL,
        descricao VARCHAR(100) NOT NULL,
        ativo BOOLEAN NOT NULL DEFAULT TRUE,
        data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        data_atualizacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX idx_sigla (sigla)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: permissao
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS permissao (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        codigo VARCHAR(255) NOT NULL UNIQUE,
        nome VARCHAR(255) NOT NULL,
        modulo VARCHAR(255) NOT NULL,
        acao VARCHAR(50) NOT NULL,
        contexto JSON,
        ativo BOOLEAN NOT NULL DEFAULT TRUE,
        INDEX idx_codigo (codigo),
        INDEX idx_modulo (modulo)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: role
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS role (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        nome VARCHAR(255) NOT NULL UNIQUE,
        ativo BOOLEAN NOT NULL DEFAULT TRUE,
        INDEX idx_nome (nome)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: role_permissao
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS role_permissao (
        role_id BIGINT NOT NULL,
        permissao_id BIGINT NOT NULL,
        PRIMARY KEY (role_id, permissao_id),
        CONSTRAINT fk_role_permissao_role FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE,
        CONSTRAINT fk_role_permissao_permissao FOREIGN KEY (permissao_id) REFERENCES permissao (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: usuarios (atualizada de tb_usuario)
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS usuarios (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        tenant_id VARCHAR(255) NOT NULL,
        nome_completo VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL UNIQUE,
        cpf VARCHAR(11),
        senha_hash VARCHAR(255),
        status VARCHAR(50) NOT NULL DEFAULT 'ATIVO',
        aprovado_por BIGINT,
        data_aprovacao DATETIME,
        data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        data_atualizacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX idx_email (email),
        INDEX idx_cpf (cpf),
        INDEX idx_tenant_id (tenant_id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

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
        CONSTRAINT fk_usuario_permissao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
        INDEX idx_usuario_permissao_usuario (usuario_id),
        INDEX idx_usuario_permissao_ativo (ativo)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: usuario_role
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS usuario_role (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        usuario_permissao_id BIGINT NOT NULL,
        role_id BIGINT NOT NULL,
        CONSTRAINT fk_usuario_role_usuario_permissao FOREIGN KEY (usuario_permissao_id) REFERENCES tb_usuario_permissao (id) ON DELETE CASCADE,
        CONSTRAINT fk_usuario_role_role FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE,
        UNIQUE KEY uk_usuario_role (usuario_permissao_id, role_id),
        INDEX idx_usuario_role_role (role_id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ==================================================================================
-- Tabela: usuario_permissao_direta
-- ==================================================================================
CREATE TABLE
    IF NOT EXISTS usuario_permissao_direta (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        usuario_permissao_id BIGINT NOT NULL,
        permissao_id BIGINT NOT NULL,
        CONSTRAINT fk_usuario_permissao_direta_usuario_permissao FOREIGN KEY (usuario_permissao_id) REFERENCES tb_usuario_permissao (id) ON DELETE CASCADE,
        CONSTRAINT fk_usuario_permissao_direta_permissao FOREIGN KEY (permissao_id) REFERENCES permissao (id) ON DELETE CASCADE,
        UNIQUE KEY uk_usuario_permissao_direta (usuario_permissao_id, permissao_id),
        INDEX idx_usuario_permissao_direta_permissao (permissao_id)
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
    IF NOT EXISTS usuario_contato (
        usuario_id BIGINT NOT NULL,
        contato_id BIGINT NOT NULL,
        PRIMARY KEY (usuario_id, contato_id),
        CONSTRAINT fk_usuario_contato_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
        CONSTRAINT fk_usuario_contato_contato FOREIGN KEY (contato_id) REFERENCES tb_contatos (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;