-- V1__create_aliquota_tables.sql
-- src/main/resources/db/migration/V1__create_aliquota_tables.sql

-- Tabela de alíquotas por região (UF/Município)
CREATE TABLE tb_aliquota_regiao (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo_tributo VARCHAR(20) NOT NULL,
    uf VARCHAR(2),
    municipio VARCHAR(100),
    aliquota DECIMAL(10,4) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE,
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_tb_aliquota_regiao UNIQUE (codigo_tributo, uf, municipio, data_inicio)
);

CREATE INDEX idx_tb_aliquota_regiao_codigo ON tb_aliquota_regiao(codigo_tributo);
CREATE INDEX idx_tb_aliquota_regiao_uf ON tb_aliquota_regiao(uf);
CREATE INDEX idx_tb_aliquota_regiao_ativo ON tb_aliquota_regiao(ativo);