-- V4__seed_aliquotas_iss.sql
-- src/main/resources/db/migration/V4__seed_aliquotas_iss.sql

-- Dados iniciais de ISS por município (capitais)
INSERT INTO tb_aliquota_regiao (codigo_tributo, uf, municipio, aliquota, data_inicio) VALUES
('ISS', 'SP', 'São Paulo', 5.00, '2024-01-01'),
('ISS', 'RJ', 'Rio de Janeiro', 5.00, '2024-01-01'),
('ISS', 'MG', 'Belo Horizonte', 5.00, '2024-01-01'),
('ISS', 'RS', 'Porto Alegre', 5.00, '2024-01-01'),
('ISS', 'BA', 'Salvador', 5.00, '2024-01-01'),
('ISS', 'PR', 'Curitiba', 5.00, '2024-01-01'),
('ISS', 'PE', 'Recife', 5.00, '2024-01-01'),
('ISS', 'CE', 'Fortaleza', 5.00, '2024-01-01'),
('ISS', 'DF', 'Brasília', 5.00, '2024-01-01'),
('ISS', 'AM', 'Manaus', 5.00, '2024-01-01');