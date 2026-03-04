-- Insert Custom Data
-- Categoria Cliente para Distribuidora Brasil
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    'PREMIUM',
    cfd.id,
    c.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_business_partner c
WHERE
    cfd.field_key = 'categoria_cliente'
    AND c.nome = 'DISTRIBUIDORA BRASIL'
LIMIT
    1;

-- Categoria Cliente para Loja Central
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    'PADRÃO',
    cfd.id,
    c.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_business_partner c
WHERE
    cfd.field_key = 'categoria_cliente'
    AND c.nome = 'LOJA CENTRAL'
LIMIT
    1;

-- Categoria Cliente para João da Silva
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    'BAIXO_VOLUME',
    cfd.id,
    c.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_business_partner c
WHERE
    cfd.field_key = 'categoria_cliente'
    AND c.nome = 'João da Silva'
LIMIT
    1;

-- Cliente ativo em marketing - Distribuidora Brasil
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    '1',
    cfd.id,
    c.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_business_partner c
WHERE
    cfd.field_key = 'cliente_ativo_marketing'
    AND c.nome = 'DISTRIBUIDORA BRASIL'
LIMIT
    1;

-- Cliente ativo em marketing - Forbecedor ABC
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    '0',
    cfd.id,
    c.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_business_partner c
WHERE
    cfd.field_key = 'cliente_ativo_marketing'
    AND c.nome = 'FORNECEDOR ABC'
LIMIT
    1;

-- Observações internas - Loja Central
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    'Cliente reclamou sobre prazo de entrega em Janeiro/2025',
    cfd.id,
    c.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_business_partner c
WHERE
    cfd.field_key = 'observacoes_internas'
    AND c.nome = 'LOJA CENTRAL'
LIMIT
    1;

-- Produto Promocional - Televisor
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    '1',
    cfd.id,
    p.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_product p
WHERE
    cfd.field_key = 'produto_promocional'
    AND p.codigo = 'PROD001'
LIMIT
    1;

-- Produto Promocional - Notebook
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    '1',
    cfd.id,
    p.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_product p
WHERE
    cfd.field_key = 'produto_promocional'
    AND p.codigo = 'PROD002'
LIMIT
    1;

-- Margem Lucro Meta - Monitor
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    '35',
    cfd.id,
    p.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_product p
WHERE
    cfd.field_key = 'margem_lucro_meta'
    AND p.codigo = 'PROD005'
LIMIT
    1;

-- Margem Lucro Meta - Café
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    '50',
    cfd.id,
    p.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_product p
WHERE
    cfd.field_key = 'margem_lucro_meta'
    AND p.codigo = 'PROD006'
LIMIT
    1;

-- Fornecedor Preferido - Notebook
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    'FORNECEDOR_A',
    cfd.id,
    p.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_product p
WHERE
    cfd.field_key = 'fornecedor_preferido'
    AND p.codigo = 'PROD002'
LIMIT
    1;

-- Data Validade Relevante - Leite
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    '1',
    cfd.id,
    p.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_product p
WHERE
    cfd.field_key = 'data_validade_relevante'
    AND p.codigo = 'PROD007'
LIMIT
    1;

-- Data Validade Relevante - Suco
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    '1',
    cfd.id,
    p.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_product p
WHERE
    cfd.field_key = 'data_validade_relevante'
    AND p.codigo = 'PROD009'
LIMIT
    1;

-- Certificações - Monitor
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    'ISO9001|FCC|CE',
    cfd.id,
    p.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_product p
WHERE
    cfd.field_key = 'certificacoes_produto'
    AND p.codigo = 'PROD005'
LIMIT
    1;

-- Certificações - Notebook
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    'FCC|CE',
    cfd.id,
    p.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_product p
WHERE
    cfd.field_key = 'certificacoes_produto'
    AND p.codigo = 'PROD002'
LIMIT
    1;

-- Volume Anual Estimado - Distribuidora
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    '50000',
    cfd.id,
    c.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_business_partner c
WHERE
    cfd.field_key = 'volume_anual_estimado'
    AND c.nome = 'DISTRIBUIDORA BRASIL'
LIMIT
    1;

-- Volume Anual Estimado - Loja Central
INSERT INTO
    TB_CUSTOM_DATA (value, custom_field_id, entity_id, custom_data_id)
SELECT
    '15000',
    cfd.id,
    c.id,
    NULL
FROM
    TB_custom_field_definition cfd,
    tb_business_partner c
WHERE
    cfd.field_key = 'volume_anual_estimado'
    AND c.nome = 'LOJA CENTRAL'
LIMIT
    1;