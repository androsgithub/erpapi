

-- Insert Custom Field Definitions

INSERT INTO TB_custom_field_definition (created_at, created_by, deleted, deleted_at, SCOPE, tenant_group_id, tenant_id, updated_at, updated_by, VERSION, active, field_key, field_order, field_type, label, metadata, required, target)
VALUES -- BusinessPartner Custom Fields
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', '1', 'categoria_cliente', 1, 'SELECT', 'Categoria do Cliente', 'PREMIUM,PADRÃO,BAIXO_VOLUME', 0, 'BUSINESSPARTNER'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', '1', 'data_cadastro_anterior', 2, 'DATE', 'Data Cadastro Sistema Anterior', NULL, 0, 'BUSINESSPARTNER'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 1, 'observacoes_internas', 3, 'TEXT', 'Observações Internas', NULL, 0, 'BUSINESSPARTNER'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 1, 'cliente_ativo_marketing', 4, 'BOOLEAN', 'Ativo em Marketing', NULL, 0, 'BUSINESSPARTNER'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 1, 'data_ultima_compra', 5, 'DATE', 'Data Última Compra', NULL, 0, 'BUSINESSPARTNER'),
 -- Product Custom Fields
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 1, 'fornecedor_preferido', 6, 'SELECT', 'Fornecedor Preferido', 'FORNECEDOR_A,FORNECEDOR_B,FORNECEDOR_C', 0, 'PRODUCT'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 1, 'margem_lucro_meta', 7, 'NUMBER', 'Margem de Lucro Meta (%)', NULL, 0, 'PRODUCT'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 1, 'produto_promocional', 8, 'BOOLEAN', 'Produto Promocional', NULL, 0, 'PRODUCT'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 1, 'data_validade_relevante', 9, 'BOOLEAN', 'Data Validade Relevante', NULL, 0, 'PRODUCT'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 1, 'certificacoes_produto', 10, 'MULTI_SELECT', 'Certificações', 'ISO9001,ISO14001,INMETRO,FCC,CE', 0, 'PRODUCT'),
 -- General Custom Fields
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 1, 'ambiente_uso', 11, 'SELECT', 'Ambiente de Uso', 'INTERNO,EXTERNO,AMBOS', 0, 'GENERAL'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 1, 'prioridade_processamento', 12, 'SELECT', 'Prioridade de Processamento', 'BAIXA,MEDIA,ALTA,CRITICA', 0, 'GENERAL'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 0, 'email_contato_secundario', 13, 'EMAIL', 'Email de Contato Secundário', NULL, 0, 'BUSINESSPARTNER'),
 (CURRENT_TIMESTAMP, 1, b'0', NULL, 'TENANT', NULL, 1, NULL, NULL, '0', 0, 'volume_anual_estimado', 14, 'NUMBER', 'Volume Anual Estimado (KG)', NULL, 0, 'BUSINESSPARTNER');