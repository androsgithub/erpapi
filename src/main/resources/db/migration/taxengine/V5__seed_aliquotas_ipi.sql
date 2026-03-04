-- V5__seed_aliquotas_ipi.sql
-- src/main/resources/db/migration/V5__seed_aliquotas_ipi.sql

-- Dados iniciais de IPI por NCM (exemplos)
INSERT INTO tb_aliquota_ncm (codigo_tributo, ncm, descricao_ncm, aliquota, data_inicio) VALUES
('IPI', '2203.00.00', 'Cervejas de malte', 25.00, '2024-01-01'),
('IPI', '2204.10.10', 'Vinhos espumantes', 20.00, '2024-01-01'),
('IPI', '2208.30.10', 'Uísque', 30.00, '2024-01-01'),
('IPI', '2402.20.00', 'Cigarros contendo tabaco', 300.00, '2024-01-01'),
('IPI', '8703.23.10', 'Automóveis de passageiros até 1000cm³', 7.00, '2024-01-01'),
('IPI', '8703.24.10', 'Automóveis de passageiros 1000-1500cm³', 11.00, '2024-01-01'),
('IPI', '8703.32.10', 'Automóveis de passageiros 1500-3000cm³', 18.00, '2024-01-01'),
('IPI', '8471.30.12', 'Computadores portáteis', 0.00, '2024-01-01'),
('IPI', '8517.12.31', 'Telefones celulares', 15.00, '2024-01-01'),
('IPI', '3304.99.10', 'Perfumes', 35.00, '2024-01-01');