INSERT INTO
    `tb_usuario` (
        `id`,
        `created_at`,
        `updated_at`,
        `email`,
        `nome_completo`,
        `senha_hash`,
        `status`,
        `tenant_id`,
        `cpf`,
        `aprovado_por`,
        `approved_at`
    )
VALUES
    (
        1,
        now(),
        now(),
        'admin@empresa.com',
        'Administrador do Sistema',
        '$2a$10$bcBn0eI2zxV7Rn..f5sT2O204pgvJf9AVdhEnXnTouNe/1MErPtFm',
        'ATIVO',
        '1',
        '11144477735',
        1,
        now()
    );