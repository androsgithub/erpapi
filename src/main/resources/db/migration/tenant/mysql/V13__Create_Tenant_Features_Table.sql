-- V13__Create_Tenant_Features_Table.sql
-- Tabela central para override de beans Spring por tenant
-- Cada linha define: "para este tenant, quando pedir a feature 'featureKey', injete o bean 'beanName'"
CREATE TABLE IF NOT EXISTS
    tb_tnt_features (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        feature_key VARCHAR(100) NOT NULL,
        bean_name VARCHAR(200) NOT NULL,
        description VARCHAR(500),
        active BOOLEAN NOT NULL DEFAULT TRUE,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (tenant_id) REFERENCES tb_tnt (id) ON DELETE CASCADE,
        UNIQUE KEY uk_tnt_features_per_tenant (tenant_id, feature_key)
    );


-- Index para o resolver acessar features ativas rapidamente
CREATE INDEX idx_tnt_features_active ON tb_tnt_features (tenant_id, active);


-- Index genérico por tenant
CREATE INDEX idx_tnt_features_tenant_id ON tb_tnt_features (tenant_id);