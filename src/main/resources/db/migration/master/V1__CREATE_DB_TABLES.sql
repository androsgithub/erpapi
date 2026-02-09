     create table tb_endereco (
           id bigint not null auto_increment,
           created_at datetime(6) not null default current_timestamp(6),
           created_by bigint,
           deleted bit not null,
           deleted_at datetime(6),
           scope enum ('GLOBAL','GROUP','TENANT'),
           tenant_group_id bigint,
           tenant_id bigint not null,
           updated_at datetime(6),
           updated_by bigint,
           version bigint,
           bairro varchar(100) not null,
           cep varchar(8) not null,
           cidade varchar(100) not null,
           complemento varchar(255),
           estado varchar(2) not null,
           numero varchar(50) not null,
           principal bit not null,
           rua varchar(255) not null,
           tipo enum ('COBRANCA','COMERCIAL','CORRESPONDENCIA','ENTREGA','OUTRO','RESIDENCIAL') not null,
           primary key (id)
       ) engine=InnoDB;

   create table tb_contatos (
        id bigint not null auto_increment,
        created_at datetime(6) not null default current_timestamp(6),
        created_by bigint,
        deleted bit not null,
        deleted_at datetime(6),
        scope enum ('GLOBAL','GROUP','TENANT'),
        tenant_group_id bigint,
        tenant_id bigint not null,
        updated_at datetime(6),
        updated_by bigint,
        version bigint,
        ativo bit not null,
        descricao varchar(255),
        principal bit not null,
        tipo enum ('CELULAR','EMAIL','FACEBOOK','INSTAGRAM','LINKEDIN','OUTRO','TELEFONE','WEBSITE','WHATSAPP') not null,
        valor varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;




 create table tb_tenant (
        id bigint not null auto_increment,
        ativa bit not null,
        cliente_audit_enabled bit not null,
        cliente_cache_enabled bit not null,
        cliente_notification_enabled bit not null,
        cliente_tenant_customization_enabled bit not null,
        cliente_validation_enabled bit not null,
        contato_audit_enabled bit not null,
        contato_cache_enabled bit not null,
        contato_notification_enabled bit not null,
        contato_validation_enabled bit not null,
        endereco_audit_enabled bit not null,
        endereco_cache_enabled bit not null,
        endereco_validation_enabled bit not null,
        tenant_custom_code enum ('DEFAULT','HECE','NOOP'),
        tenant_features_enabled bit not null,
        tenant_subdomain varchar(255),
        tenant_type enum ('DEFAULT','HECE'),
        permissao_audit_enabled bit not null,
        permissao_cache_enabled bit not null,
        permissao_validation_enabled bit not null,
        produto_audit_enabled bit not null,
        produto_cache_enabled bit not null,
        produto_validation_enabled bit not null,
        unidade_medida_cache_enabled bit not null,
        unidade_medida_validation_enabled bit not null,
        usuario_approval_required bit not null,
        usuario_corporate_email_required bit not null,
        cnae_principal varchar(7),
        dados_fiscais_cnpj varchar(14) not null,
        codigo_municipio_ibge varchar(7),
        contribuinte_icms enum ('CONTRIBUINTE','ISENTO','NAO_CONTRIBUINTE') not null,
        dados_fiscais_inscricao_estadual varchar(20),
        inscricao_municipal varchar(20),
        dados_fiscais_nome_fantasia varchar(150),
        dados_fiscais_razao_social varchar(150) not null,
        dados_fiscais_regime_tributario enum ('LUCRO_ARBITRADO','LUCRO_PRESUMIDO','LUCRO_REAL','MEI','SIMPLES_NACIONAL') not null,
        data_atualizacao datetime(6),
        data_criacao datetime(6),
        email varchar(255),
        nome varchar(255),
        telefone varchar(255),
        endereco_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table tb_tenant_datasource (
        id bigint not null auto_increment,
        created_at datetime(6) not null default current_timestamp(6),
        database_name varchar(255) not null,
        db_type enum ('CLICKHOUSE','COCKROACHDB','DB2','DERBY','FIREBIRD','H2','HSQLDB','INFORMIX','MARIADB','MYSQL','ORACLE','POSTGRESQL','SQLITE','SQL_SERVER','SYBASE','VERTICA') not null,
        host varchar(255) not null,
        is_active bit not null,
        password varchar(255) not null,
        port integer not null,
        test_status enum ('FAILED','PENDING','SUCCESS'),
        tested_at datetime(6),
        updated_at datetime(6),
        username varchar(255) not null,
        tenant_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table tb_tenant_domains (
        tenant_id bigint not null,
        allowedEmailDomains varchar(255)
    ) engine=InnoDB;


    create table tb_tenant_group (
            id bigint not null auto_increment,
            active bit not null,
            createdAt datetime(6) not null,
            description varchar(500),
            name varchar(255) not null,
            updatedAt datetime(6) not null,
            primary key (id)
        ) engine=InnoDB;

        create table tb_tenant_group_tenant (
            tenant_group_id bigint not null,
            tenant_id bigint not null,
            primary key (tenant_group_id, tenant_id)
        ) engine=InnoDB;

        alter table tb_tenant_group_tenant
               add constraint FK1x2gy5t2f7nbv8jy89o5xbcus
               foreign key (tenant_id)
               references tb_tenant (id);

            alter table tb_tenant_group_tenant
               add constraint FK7vfgydlg3lora57ra05gut80q
               foreign key (tenant_group_id)
               references tb_tenant_group (id);

     alter table tb_tenant
           add constraint UKdmue91rfs72uisw4ihe9x8eva unique (endereco_id);

        create index idx_tenant_id
           on tb_tenant_datasource (tenant_id);

        create index idx_is_active
           on tb_tenant_datasource (is_active);

        alter table tb_tenant_datasource
           add constraint UKrd1ofabj7y3buvlan8xaaxljt unique (tenant_id);

             alter table tb_tenant
                  add constraint FKs52dqlxqbuj0t38p99258mci9
                  foreign key (endereco_id)
                  references tb_endereco (id);

               alter table tb_tenant_datasource
                  add constraint FK8cjuymohd0b52puoljrjgpch1
                  foreign key (tenant_id)
                  references tb_tenant (id);

               alter table tb_tenant_domains
                  add constraint FK8ahrfsjrp2d2muvc1cdafimiq
                  foreign key (tenant_id)
                  references tb_tenant (id);