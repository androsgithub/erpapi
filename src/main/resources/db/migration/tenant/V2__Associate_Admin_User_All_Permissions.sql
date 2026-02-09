INSERT INTO tb_usuario_role
(
    created_at,
    created_by,
    deleted,
    scope,
    tenant_group_id,
    tenant_id,
    version,
    usuario_id,
    role_id
)
SELECT
    NOW(6),
    1,
    b'0',
    'TENANT',
    1,
    1,
    0,
    1,
    3
WHERE NOT EXISTS (
    SELECT 1
    FROM tb_usuario_role ur
    WHERE ur.usuario_id = 1
      AND ur.role_id = 3
      AND ur.tenant_id = 1
      AND ur.deleted = b'0'
);
