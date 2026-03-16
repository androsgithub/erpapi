-- V14__Migrate_Tenant_Features_From_Hardcoded.sql
-- Popula tb_tnt_features com os overrides que antes estavam hardcoded ou em properties
-- NOTA: Esta migration depende dos dados existentes em tb_tnt
-- Ajuste os INSERT de acordo com seus tenants reais e beans específicos
-- Exemplo 1: Tenant "Hece" (ID = 1)
-- Se o tenant Hece tem beans customizados, insira aqui:
INSERT INTO
    tb_tnt_features (tenant_id, feature_key, bean_name, description, active)
SELECT
    1,
    'userService',
    'heceUserService',
    'Migrado de hardcoded em UserServiceProxy para tenant Hece',
    TRUE
WHERE
    EXISTS (
        SELECT
            1
        FROM
            tb_tnt
        WHERE
            id = 1
    )
    AND NOT EXISTS (
        SELECT
            1
        FROM
            tb_tnt_features
        WHERE
            tenant_id = 1
            AND feature_key = 'userService'
    );


INSERT INTO
    tb_tnt_features (tenant_id, feature_key, bean_name, description, active)
SELECT
    1,
    'userValidator',
    'heceUserValidator',
    'Migrado de hardcoded em UserValidatorProxy para tenant Hece',
    TRUE
WHERE
    EXISTS (
        SELECT
            1
        FROM
            tb_tnt
        WHERE
            id = 1
    )
    AND NOT EXISTS (
        SELECT
            1
        FROM
            tb_tnt_features
        WHERE
            tenant_id = 1
            AND feature_key = 'userValidator'
    );


INSERT INTO
    tb_tnt_features (tenant_id, feature_key, bean_name, description, active)
SELECT
    1,
    'productService',
    'heceProductService',
    'Migrado de hardcoded em ProductServiceProxy para tenant Hece',
    TRUE
WHERE
    EXISTS (
        SELECT
            1
        FROM
            tb_tnt
        WHERE
            id = 1
    )
    AND NOT EXISTS (
        SELECT
            1
        FROM
            tb_tnt_features
        WHERE
            tenant_id = 1
            AND feature_key = 'productService'
    );


-- Exemplo 2: Tenant "Acme" (ID = 2) - descomente e ajuste conforme necessário
-- INSERT INTO tb_tnt_features (tenant_id, feature_key, bean_name, description, active)
-- SELECT 2, 'userService', 'acmeUserService', 'Tenant Acme - User Service customizado', true
-- WHERE EXISTS (SELECT 1 FROM tb_tnt WHERE id = 2)
-- AND NOT EXISTS (SELECT 1 FROM tb_tnt_features WHERE tenant_id = 2 AND feature_key = 'userService');
-- Seu TODO aqui:
-- 1. Conecte ao banco de dados e execute: SELECT id, name FROM tb_tnt;
-- 2. Para cada tenant com beans customizados, adicione um INSERT neste arquivo
-- 3. Padrão de name de bean: {TENANT_PREFIX}{FEATURE_NAME}
--    Ex: tenant Hece → heceUserService, heceProductService
-- 4. Deixe comentado ou remova os exemplos acima se não forem aplicáveis
-- Exemplo de como descobrir que beans devem ser migrados:
-- - Procure por padrões no código antigo: "userService" + tenantType.name()
-- - Cada bean deve ser uma classe Spring anotada com @Component, @Service, etc.
-- - E implementar a mesma interface: UserService impl IUserService, etc.
-- DICA: Se não houver overrides por enquanto, deixe esta migration vazia.
-- Você pode adicioná-las posteriormente conforme os beans customizados forem criados.