-- ==================================================================================
-- MIGRATION: V2__Seed_Initial_Data.sql (Tenant)
-- DESCRIÇÃO: Insere dados iniciais para todos os tenants
-- APLICÁVEL: Todos os tenants (executa em cada banco individual)
-- ==================================================================================
-- Seed Unidades de Medida
INSERT IGNORE INTO tb_unidade_medida (
    tenant_id,
    nome,
    sigla,
    tipo,
    created_at,
    updated_at
)
VALUES
    (
        1,
        'Unidade',
        'UN',
        'METRIC',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1,
        'Quilograma',
        'KG',
        'METRIC',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1,
        'Litro',
        'L',
        'METRIC',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Seed Roles
INSERT IGNORE INTO tb_role (
    tenant_id,
    nome,
    descricao,
    created_at,
    updated_at
)
VALUES
    (
        1,
        'ADMIN',
        'Administrador do sistema',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1,
        'USER',
        'Usuário padrão',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Seed Permissões
INSERT IGNORE INTO tb_permissao (
    tenant_id,
    nome,
    descricao,
    modulo,
    acao,
    created_at,
    updated_at
)
VALUES
    (
        1,
        'VISUALIZAR_CLIENTE',
        'Visualizar cliente',
        'CLIENTE',
        'READ',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1,
        'ATUALIZAR_CLIENTE',
        'Atualizar cliente',
        'CLIENTE',
        'UPDATE',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Seed Clientes
INSERT IGNORE INTO tb_cliente (
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
        'Cliente Padrão',
        'ATIVO',
        'PJ',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- =========================
-- Seed Endereços
-- =========================

INSERT IGNORE INTO tb_endereco (
    tenant_id,
    rua,
    numero,
    complemento,
    bairro,
    cidade,
    estado,
    cep,
    tipo,
    principal,
    custom_data,
    created_at,
    updated_at,
    created_by,
    updated_by,
    deleted,
    version
)
VALUES
(
    1,
    'Rua Padrão',
    '123',
    'Apto 101',
    'Bairro Padrão',
    'São Paulo',
    'SP',
    '01234567',
    'RESIDENCIAL',
    TRUE,
    NULL,
    CURRENT_TIMESTAMP(6),
    CURRENT_TIMESTAMP(6),
    1,
    1,
    FALSE,
    0
);


-- Seed Contatos
INSERT IGNORE INTO tb_contato (
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
        'contato@tenant.test',
        'Email padrão',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        1,
        'TELEFONE',
        '(11) 1234-5678',
        'Telefone padrão',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );