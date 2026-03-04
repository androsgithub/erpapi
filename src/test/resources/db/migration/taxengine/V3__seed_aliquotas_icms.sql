-- V3__seed_aliquotas_icms.sql
-- src/main/resources/db/migration/V3__seed_aliquotas_icms.sql

-- Dados iniciais de ICMS por UF (alíquotas internas)
INSERT INTO tb_aliquota_regiao (codigo_tributo, uf, municipio, aliquota, data_inicio) VALUES
('ICMS', 'AC', NULL, 17.00, '2024-01-01'),
('ICMS', 'AL', NULL, 18.00, '2024-01-01'),
('ICMS', 'AP', NULL, 18.00, '2024-01-01'),
('ICMS', 'AM', NULL, 18.00, '2024-01-01'),
('ICMS', 'BA', NULL, 18.00, '2024-01-01'),
('ICMS', 'CE', NULL, 18.00, '2024-01-01'),
('ICMS', 'DF', NULL, 18.00, '2024-01-01'),
('ICMS', 'ES', NULL, 17.00, '2024-01-01'),
('ICMS', 'GO', NULL, 17.00, '2024-01-01'),
('ICMS', 'MA', NULL, 18.00, '2024-01-01'),
('ICMS', 'MT', NULL, 17.00, '2024-01-01'),
('ICMS', 'MS', NULL, 17.00, '2024-01-01'),
('ICMS', 'MG', NULL, 18.00, '2024-01-01'),
('ICMS', 'PA', NULL, 17.00, '2024-01-01'),
('ICMS', 'PB', NULL, 18.00, '2024-01-01'),
('ICMS', 'PR', NULL, 18.00, '2024-01-01'),
('ICMS', 'PE', NULL, 18.00, '2024-01-01'),
('ICMS', 'PI', NULL, 18.00, '2024-01-01'),
('ICMS', 'RJ', NULL, 18.00, '2024-01-01'),
('ICMS', 'RN', NULL, 18.00, '2024-01-01'),
('ICMS', 'RS', NULL, 17.00, '2024-01-01'),
('ICMS', 'RO', NULL, 17.50, '2024-01-01'),
('ICMS', 'RR', NULL, 17.00, '2024-01-01'),
('ICMS', 'SC', NULL, 17.00, '2024-01-01'),
('ICMS', 'SP', NULL, 18.00, '2024-01-01'),
('ICMS', 'SE', NULL, 18.00, '2024-01-01'),
('ICMS', 'TO', NULL, 18.00, '2024-01-01');