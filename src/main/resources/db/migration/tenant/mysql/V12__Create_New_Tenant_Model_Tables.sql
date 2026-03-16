-- V12__Create_New_Tenant_Model_Tables.sql
-- Refactoring do modelo de Tenant para arquitetura escalável com override de beans por tenant
-- 1. Tabela de Planos de Assinatura
CREATE TABLE IF NOT EXISTS
    tb_tnt_plan (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        NAME VARCHAR(100) NOT NULL,
        description VARCHAR(500),
        monthly_price DECIMAL(10, 2) NOT NULL,
        max_users INT NOT NULL,
        max_branches INT NOT NULL,
        active BOOLEAN NOT NULL DEFAULT TRUE,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );


CREATE INDEX idx_tnt_plan_active ON tb_tnt_plan (active);


-- 2. Refactor da tabela principal de Tenant
CREATE TABLE IF NOT EXISTS
    tb_tnt (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        NAME VARCHAR(200) NOT NULL,
        email VARCHAR(150) NOT NULL,
        phone VARCHAR(20),
        plan_id BIGINT NOT NULL REFERENCES tb_tnt_plan (id),
        active BOOLEAN NOT NULL DEFAULT TRUE,
        trial BOOLEAN NOT NULL DEFAULT FALSE,
        trial_expires_at TIMESTAMP NULL,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (plan_id) REFERENCES tb_tnt_plan (id)
    );


CREATE INDEX idx_tnt_active ON tb_tnt (active);


CREATE INDEX idx_tnt_plan_id ON tb_tnt (plan_id);


-- 3. Configurações gerais do Tenant
CREATE TABLE IF NOT EXISTS
    tb_tnt_config (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL UNIQUE,
        slug VARCHAR(100) NOT NULL,
        app_name VARCHAR(150) NOT NULL,
        app_description VARCHAR(500),
        support_email VARCHAR(150),
        website_url VARCHAR(500),
        timezone VARCHAR(50) NOT NULL DEFAULT 'America/Sao_Paulo',
        locale VARCHAR(10) NOT NULL DEFAULT 'pt_BR',
        currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
        date_format VARCHAR(20) NOT NULL DEFAULT 'dd/MM/yyyy',
        -- Módulos
        module_fiscal BOOLEAN NOT NULL DEFAULT TRUE,
        module_hr BOOLEAN NOT NULL DEFAULT FALSE,
        module_stock BOOLEAN NOT NULL DEFAULT FALSE,
        module_financial BOOLEAN NOT NULL DEFAULT FALSE,
        module_crm BOOLEAN NOT NULL DEFAULT FALSE,
        module_timesheet BOOLEAN NOT NULL DEFAULT FALSE,
        module_ecommerce BOOLEAN NOT NULL DEFAULT FALSE,
        -- Funcionalidades
        multi_branch BOOLEAN NOT NULL DEFAULT FALSE,
        allow_api_access BOOLEAN NOT NULL DEFAULT TRUE,
        white_label BOOLEAN NOT NULL DEFAULT FALSE,
        -- Notificações
        notify_email BOOLEAN NOT NULL DEFAULT TRUE,
        notify_sms BOOLEAN NOT NULL DEFAULT FALSE,
        notify_push BOOLEAN NOT NULL DEFAULT FALSE,
        notify_webhook BOOLEAN NOT NULL DEFAULT FALSE,
        -- Segurança e Autenticação
        two_factor_auth BOOLEAN NOT NULL DEFAULT FALSE,
        sso_enabled BOOLEAN NOT NULL DEFAULT FALSE,
        allow_social_login BOOLEAN NOT NULL DEFAULT FALSE,
        -- Status
        maintenance_mode BOOLEAN NOT NULL DEFAULT FALSE,
        onboarding_done BOOLEAN NOT NULL DEFAULT FALSE,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (tenant_id) REFERENCES tb_tnt (id) ON DELETE CASCADE
    );


CREATE INDEX idx_tnt_config_tenant_id ON tb_tnt_config (tenant_id);


-- 4. Identidade Visual do Tenant
CREATE TABLE IF NOT EXISTS
    tb_tnt_visual (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL UNIQUE,
        theme VARCHAR(50) DEFAULT 'light',
        border_radius VARCHAR(20) DEFAULT '4px',
        sidebar_collapsed BOOLEAN NOT NULL DEFAULT FALSE,
        -- Cores
        color_primary VARCHAR(7),
        color_secondary VARCHAR(7),
        color_accent VARCHAR(7),
        color_background VARCHAR(7),
        color_text VARCHAR(7),
        color_danger VARCHAR(7),
        color_success VARCHAR(7),
        color_warning VARCHAR(7),
        -- Assets
        logo_url VARCHAR(500),
        logo_large_url VARCHAR(500),
        favicon_url VARCHAR(500),
        email_logo_url VARCHAR(500),
        background_image_url VARCHAR(500),
        -- Tipografia
        font_family VARCHAR(100),
        font_cdn_url VARCHAR(500),
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (tenant_id) REFERENCES tb_tnt (id) ON DELETE CASCADE
    );


CREATE INDEX idx_tnt_visual_tenant_id ON tb_tnt_visual (tenant_id);


-- 5. Datasource do Tenant
CREATE TABLE IF NOT EXISTS
    tb_tnt_datasource (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL UNIQUE,
        driver_class VARCHAR(200) NOT NULL,
        url VARCHAR(500) NOT NULL,
        username VARCHAR(100) NOT NULL,
        password_encrypted VARCHAR(500) NOT NULL,
        schema_name VARCHAR(100),
        db_type VARCHAR(30) NOT NULL,
        pool_min INT DEFAULT 5,
        pool_max INT DEFAULT 20,
        connection_timeout INT DEFAULT 30000,
        idle_timeout INT DEFAULT 600000,
        active BOOLEAN NOT NULL DEFAULT TRUE,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (tenant_id) REFERENCES tb_tnt (id) ON DELETE CASCADE
    );


CREATE INDEX idx_tnt_datasource_tenant_id ON tb_tnt_datasource (tenant_id);


CREATE INDEX idx_tnt_datasource_active ON tb_tnt_datasource (active);


-- 6. Domínios Permitidos
CREATE TABLE IF NOT EXISTS
    tb_tnt_allow_domains (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL,
        DOMAIN VARCHAR(255) NOT NULL,
        TYPE VARCHAR(20) NOT NULL,
        active BOOLEAN NOT NULL DEFAULT TRUE,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (tenant_id) REFERENCES tb_tnt (id) ON DELETE CASCADE,
        CONSTRAINT chk_tnt_domain_type CHECK (
            TYPE IN ('EMAIL', 'CORS', 'SSO')
        ),
        UNIQUE KEY uk_tnt_allow_domains (tenant_id, DOMAIN)
    );


CREATE INDEX idx_tnt_allow_domains_tenant_id ON tb_tnt_allow_domains (tenant_id);


-- 7. Dados Fiscais
CREATE TABLE IF NOT EXISTS
    tb_tnt_fiscal (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL UNIQUE,
        cnpj VARCHAR(14) NOT NULL UNIQUE,
        legal_name VARCHAR(150) NOT NULL,
        trade_name VARCHAR(150),
        state_registration VARCHAR(20),
        city_registration VARCHAR(20),
        tax_regime VARCHAR(50),
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (tenant_id) REFERENCES tb_tnt (id) ON DELETE CASCADE
    );


CREATE INDEX idx_tnt_fiscal_tenant_id ON tb_tnt_fiscal (tenant_id);


CREATE INDEX idx_tnt_fiscal_cnpj ON tb_tnt_fiscal (cnpj);


-- 8. Política de Segurança
CREATE TABLE IF NOT EXISTS
    tb_tnt_security (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tenant_id BIGINT NOT NULL UNIQUE,
        -- Política de Senha
        min_password_length INT DEFAULT 8,
        require_uppercase BOOLEAN NOT NULL DEFAULT TRUE,
        require_number BOOLEAN NOT NULL DEFAULT TRUE,
        require_special BOOLEAN NOT NULL DEFAULT FALSE,
        password_expiration_days INT DEFAULT 90,
        -- Política de Lockout
        max_login_attempts INT DEFAULT 5,
        lockout_duration_min INT DEFAULT 15,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (tenant_id) REFERENCES tb_tnt (id) ON DELETE CASCADE
    );


CREATE INDEX idx_tnt_security_tenant_id ON tb_tnt_security (tenant_id);