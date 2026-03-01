-- V2__create_tb_aliquota_ncm_table.sql
-- src/main/resources/db/migration/V2__create_tb_aliquota_ncm_table.sql

-- Tabela de alíquotas por NCM (IPI, IS)
CREATE TABLE tb_aliquota_ncm (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo_tributo VARCHAR(20) NOT NULL,
    ncm VARCHAR(10) NOT NULL,
    descricao_ncm VARCHAR(500),
    aliquota DECIMAL(10,4) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE,
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_tb_aliquota_ncm UNIQUE (codigo_tributo, ncm, data_inicio)
);

CREATE INDEX idx_tb_aliquota_ncm_codigo ON tb_aliquota_ncm(codigo_tributo);
CREATE INDEX idx_tb_aliquota_ncm_ncm ON tb_aliquota_ncm(ncm);
CREATE INDEX idx_tb_aliquota_ncm_ativo ON tb_aliquota_ncm(ativo);