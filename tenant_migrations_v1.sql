-- ==================================================================================
-- MIGRATION: V1__DATABASEINITIALIZER.sql para TENANT1_DB
-- DESCRIÇÃO: Criar estrutura básica de tabelas para o tenant
-- ==================================================================================
-- Tabela de Clientes (inherited from master structure)
CREATE TABLE
    IF NOT EXISTS tb_cliente (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        nome VARCHAR(255) NOT NULL,
        status VARCHAR(50) NOT NULL,
        tipo VARCHAR(50) NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted BOOLEAN DEFAULT FALSE,
        deleted_at TIMESTAMP NULL,
        INDEX idx_tenant_id (tenant_id),
        INDEX idx_status (status),
        INDEX idx_deleted (deleted)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Endereços
CREATE TABLE
    IF NOT EXISTS tb_endereco (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        logradouro VARCHAR(255),
        numero VARCHAR(20),
        complemento VARCHAR(255),
        bairro VARCHAR(100),
        cidade VARCHAR(100),
        estado VARCHAR(2),
        cep VARCHAR(10),
        pais VARCHAR(100),
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted BOOLEAN DEFAULT FALSE,
        deleted_at TIMESTAMP NULL,
        INDEX idx_tenant_id (tenant_id),
        INDEX idx_cep (cep)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Contatos
CREATE TABLE
    IF NOT EXISTS tb_contato (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        tipo VARCHAR(50),
        valor VARCHAR(255),
        descricao VARCHAR(255),
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted BOOLEAN DEFAULT FALSE,
        deleted_at TIMESTAMP NULL,
        INDEX idx_tenant_id (tenant_id),
        INDEX idx_tipo (tipo)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Unidades de Medida
CREATE TABLE
    IF NOT EXISTS tb_unidade_medida (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        nome VARCHAR(100) NOT NULL,
        sigla VARCHAR(10) NOT NULL,
        tipo VARCHAR(50),
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted BOOLEAN DEFAULT FALSE,
        deleted_at TIMESTAMP NULL,
        INDEX idx_tenant_id (tenant_id),
        UNIQUE KEY uk_sigla_tenant (sigla, tenant_id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Permissões
CREATE TABLE
    IF NOT EXISTS tb_permissao (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        nome VARCHAR(100) NOT NULL,
        descricao VARCHAR(255),
        modulo VARCHAR(100),
        acao VARCHAR(100),
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX idx_tenant_id (tenant_id),
        INDEX idx_modulo (modulo)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Roles
CREATE TABLE
    IF NOT EXISTS tb_role (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        nome VARCHAR(100) NOT NULL,
        descricao VARCHAR(255),
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX idx_tenant_id (tenant_id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Usuários
CREATE TABLE
    IF NOT EXISTS tb_usuario (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        email VARCHAR(255) NOT NULL,
        senha VARCHAR(255) NOT NULL,
        nome VARCHAR(255),
        ativo BOOLEAN DEFAULT TRUE,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted BOOLEAN DEFAULT FALSE,
        deleted_at TIMESTAMP NULL,
        INDEX idx_tenant_id (tenant_id),
        INDEX idx_email (email),
        UNIQUE KEY uk_email_tenant (email, tenant_id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Dados Fiscais do Cliente
CREATE TABLE
    IF NOT EXISTS tb_cliente_dados_fiscais (
        cliente_id BIGINT PRIMARY KEY,
        cnpj VARCHAR(20),
        cpf VARCHAR(15),
        razao_social VARCHAR(255),
        nome_fantasia VARCHAR(255),
        inscricao_estadual VARCHAR(20),
        inscricao_municipal VARCHAR(20),
        regime_tributario VARCHAR(50),
        FOREIGN KEY (cliente_id) REFERENCES tb_cliente (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Dados Financeiros do Cliente
CREATE TABLE
    IF NOT EXISTS tb_cliente_dados_financeiros (
        cliente_id BIGINT PRIMARY KEY,
        limite_credito DECIMAL(15, 2),
        dias_pagamento INT,
        desconto_padrao DECIMAL(5, 2),
        FOREIGN KEY (cliente_id) REFERENCES tb_cliente (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Endereços do Cliente
CREATE TABLE
    IF NOT EXISTS tb_cliente_endereco (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        cliente_id BIGINT NOT NULL,
        endereco_id BIGINT NOT NULL,
        tipo VARCHAR(50),
        is_principal BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (cliente_id) REFERENCES tb_cliente (id) ON DELETE CASCADE,
        FOREIGN KEY (endereco_id) REFERENCES tb_endereco (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Contatos do Cliente
CREATE TABLE
    IF NOT EXISTS tb_cliente_contato (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        cliente_id BIGINT NOT NULL,
        contato_id BIGINT NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (cliente_id) REFERENCES tb_cliente (id) ON DELETE CASCADE,
        FOREIGN KEY (contato_id) REFERENCES tb_contato (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabela de Produtos
CREATE TABLE
    IF NOT EXISTS tb_produto (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        nome VARCHAR(255) NOT NULL,
        descricao TEXT,
        sku VARCHAR(100),
        preco_unitario DECIMAL(15, 2),
        unidade_medida_id BIGINT,
        ativo BOOLEAN DEFAULT TRUE,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted BOOLEAN DEFAULT FALSE,
        deleted_at TIMESTAMP NULL,
        INDEX idx_tenant_id (tenant_id),
        INDEX idx_sku (sku),
        FOREIGN KEY (unidade_medida_id) REFERENCES tb_unidade_medida (id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Criar índice para otimizar queries multi-tenant
CREATE INDEX idx_all_tables_tenant_id ON tb_cliente (tenant_id);

CREATE INDEX idx_all_tables_tenant_id_endereco ON tb_endereco (tenant_id);

CREATE INDEX idx_all_tables_tenant_id_contato ON tb_contato (tenant_id);

CREATE INDEX idx_all_tables_tenant_id_produto ON tb_produto (tenant_id);

-- Inserir dados de teste
INSERT INTO
    tb_unidade_medida (tenant_id, nome, sigla, tipo)
VALUES
    (1, 'Unidade', 'UN', 'METRIC'),
    (1, 'Quilograma', 'KG', 'METRIC'),
    (1, 'Litro', 'L', 'METRIC'),
    (1, 'Metro', 'M', 'METRIC'),
    (1, 'Caixa', 'CX', 'METRIC');

INSERT INTO
    tb_role (tenant_id, nome, descricao)
VALUES
    (1, 'ADMIN', 'Administrador do sistema'),
    (1, 'USER', 'Usuário padrão'),
    (1, 'VENDEDOR', 'Vendedor');

INSERT INTO
    tb_permissao (tenant_id, nome, descricao, modulo, acao)
VALUES
    (
        1,
        'CRIAR_CLIENTE',
        'Criar novo cliente',
        'CLIENTE',
        'CREATE'
    ),
    (
        1,
        'VISUALIZAR_CLIENTE',
        'Visualizar cliente',
        'CLIENTE',
        'READ'
    ),
    (
        1,
        'ATUALIZAR_CLIENTE',
        'Atualizar cliente',
        'CLIENTE',
        'UPDATE'
    ),
    (
        1,
        'DELETAR_CLIENTE',
        'Deletar cliente',
        'CLIENTE',
        'DELETE'
    ),
    (
        1,
        'VISUALIZAR_RELATORIO',
        'Visualizar relatório',
        'RELATORIO',
        'READ'
    );

-- Inserir clientes de teste
INSERT INTO
    tb_cliente (
        tenant_id,
        nome,
        status,
        tipo,
        created_at,
        updated_at
    )
VALUES
    (
        1,
        'Cliente A do Tenant 1',
        'ATIVO',
        'PJ',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1,
        'Cliente B do Tenant 1',
        'ATIVO',
        'PJ',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Inserir endereços de teste
INSERT INTO
    tb_endereco (
        tenant_id,
        logradouro,
        numero,
        bairro,
        cidade,
        estado,
        cep,
        pais,
        created_at,
        updated_at
    )
VALUES
    (
        1,
        'Rua A',
        '123',
        'Bairro A',
        'São Paulo',
        'SP',
        '01234-567',
        'Brasil',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1,
        'Rua B',
        '456',
        'Bairro B',
        'São Paulo',
        'SP',
        '01234-890',
        'Brasil',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Inserir contatos de teste
INSERT INTO
    tb_contato (
        tenant_id,
        tipo,
        valor,
        descricao,
        created_at,
        updated_at
    )
VALUES
    (
        1,
        'EMAIL',
        'clientea@tenant1.test',
        'Email principal',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1,
        'TELEFONE',
        '(11) 1111-1111',
        'Telefone principal',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1,
        'EMAIL',
        'clienteb@tenant1.test',
        'Email principal',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1,
        'TELEFONE',
        '(11) 2222-2222',
        'Telefone principal',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Status da migração
SELECT
    '✅ Tabelas criadas com sucesso em tenant1_db' AS status;

SELECT
    COUNT(*) as total_clientes
FROM
    tb_cliente;

SELECT
    COUNT(*) as total_enderecos
FROM
    tb_endereco;

SELECT
    COUNT(*) as total_contatos
FROM
    tb_contato;

SELECT
    COUNT(*) as total_unidades
FROM
    tb_unidade_medida;