INSERT IGNORE INTO tb_usuario_permissao (usuario_id, data_inicio, data_fim, ativo)
VALUES
    (1, CURRENT_TIMESTAMP, NULL, TRUE);

INSERT IGNORE INTO tb_usuario_role (usuario_permissao_id, role_id)
SELECT
    up.id as usuario_permissao_id,
    3 as role_id
FROM
    tb_usuario_permissao up
WHERE
    up.usuario_id = 1
    AND up.ativo = TRUE
    AND up.data_fim IS NULL
    AND NOT EXISTS (
        SELECT
            1
        FROM
            tb_usuario_role ur
        WHERE
            ur.usuario_permissao_id = up.id
            AND ur.role_id = 3
    );