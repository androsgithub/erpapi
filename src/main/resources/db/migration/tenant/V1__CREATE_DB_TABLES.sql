
    create table tb_cliente (
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
        limite_credito float(53),
        limite_desconto float(53),
        protestado bit,
        restricao_financeira bit,
        aliquota_icms float(53),
        cnae_principal varchar(255),
        cnpj varchar(14),
        consumidor_final bit,
        cpf varchar(11),
        icms_contribuinte bit,
        inscricao_estadual varchar(255),
        inscricao_municipal varchar(255),
        nome_fantasia varchar(255),
        razao_social varchar(255),
        regime_tributario enum ('LUCRO_ARBITRADO','LUCRO_PRESUMIDO','LUCRO_REAL','MEI','SIMPLES_NACIONAL'),
        rg varchar(9),
        nome varchar(255) not null,
        email_nfe varchar(255),
        email_principal varchar(255),
        enviar_email bit,
        mala_direta bit,
        status enum ('ATIVO','BLOQUEADO','INATIVO') not null,
        tipo enum ('AMBOS','PESSOA_FISICA','PESSOA_JURIDICA') not null,
        primary key (id)
    ) engine=InnoDB;

    create table TB_CLIENTE_CONTATO (
        cliente_id bigint not null,
        contato_id bigint not null
    ) engine=InnoDB;

    create table TB_CLIENTE_ENDERECO (
        cliente_id bigint not null,
        endereco_id bigint not null
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

    create table TB_CUSTOM_DATA (
        id bigint not null auto_increment,
        value longtext,
        custom_field_id bigint,
        entity_id bigint not null,
        custom_data_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table TB_custom_field_definition (
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
        active bit,
        field_key varchar(255),
        field_order integer,
        field_type enum ('BOOLEAN','DATE','EMAIL','MULTI_SELECT','NUMBER','SELECT','TEXT'),
        label varchar(255),
        metadata varchar(255),
        required bit,
        target varchar(255),
        primary key (id)
    ) engine=InnoDB;

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

    create table tb_permissao (
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
        acao enum ('CRIAR','EDITAR','EXCLUIR','OUTRO','VISUALIZAR') not null,
        codigo varchar(255) not null,
        modulo varchar(255) not null,
        nome varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table tb_produto (
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
        cest varchar(7),
        codigo_beneficio_fiscal varchar(10),
        descricao_fiscal varchar(120) not null,
        ncm varchar(8) not null,
        origem_mercadoria integer not null,
        unidade_tributavel_codigo varchar(6) not null,
        unidade_tributavel_descricao varchar(60) not null,
        codigo varchar(50) not null,
        descricao varchar(255) not null,
        descricaoDetalhada TEXT,
        precoCusto DECIMAL(10,2),
        precoVenda DECIMAL(10,2),
        status enum ('ATIVO','BLOQUEADO','DESCONTINUADO','INATIVO') not null,
        tipo enum ('COMPRADO','FABRICAVEL') not null,
        unidade_medida_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table tb_produto_composicao (
        id bigint not null auto_increment,
        dataAtualizacao datetime(6) not null,
        dataCriacao datetime(6) not null,
        observacoes VARCHAR(500),
        quantidadeNecessaria DECIMAL(10,4) not null,
        sequencia integer not null,
        produto_componente_id bigint not null,
        produto_fabricado_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table tb_role (
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
        nome varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table tb_role_permissao (
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
        permissao_id bigint,
        role_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table tb_unidade_medida (
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
        descricao varchar(100) not null,
        sigla varchar(10) not null,
        primary key (id)
    ) engine=InnoDB;

    create table tb_usuario (
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
        approved_at datetime(6),
        aprovado_por bigint,
        cpf varchar(255) not null,
        email varchar(255) not null,
        nome_completo varchar(255) not null,
        senha_hash varchar(255) not null,
        status enum ('ATIVO','BLOQUEADO','INATIVO','PENDENTE_APROVACAO','REJEITADO') not null,
        primary key (id)
    ) engine=InnoDB;

    create table tb_usuario_contato (
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
        contato_id bigint,
        usuario_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table tb_usuario_permissao (
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
        permissao_id bigint,
        usuario_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table tb_usuario_role (
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
        role_id bigint,
        usuario_id bigint,
        primary key (id)
    ) engine=InnoDB;

    alter table TB_CLIENTE_CONTATO
       add constraint UKoyvoyiovpks7mak7a6o9shsjf unique (contato_id);

    alter table TB_CLIENTE_ENDERECO
       add constraint UK3g4mi2jwv887g1adobbi2ty7j unique (endereco_id);

    alter table TB_CUSTOM_DATA
       add constraint UKb7y9ubgkokofmmajuqk9oxkmn unique (custom_data_id);

    alter table tb_permissao
       add constraint UK1uq3cg2rbrpk3ykinkkju2gaw unique (codigo);

    alter table tb_produto
       add constraint UK472kg512c3fntgnpuqi1wk1l2 unique (codigo);

    alter table tb_role
       add constraint UK6e9vttep485ay7pmjccprgdus unique (nome);



    alter table tb_unidade_medida
       add constraint UKqpb3oc1tcjwvcj76341nv6tl3 unique (sigla);

    alter table tb_usuario
       add constraint UK594wib8ansybtilla48x7vdld unique (cpf);

    alter table tb_usuario
       add constraint UKspmnyb4dsul95fjmr5kmdmvub unique (email);

    alter table TB_CLIENTE_CONTATO
       add constraint FKar3q0fwokcc43i1w07alv1u9t
       foreign key (contato_id)
       references tb_contatos (id);

    alter table TB_CLIENTE_CONTATO
       add constraint FKf44b9l0vsi0mdpwwwla1sfx27
       foreign key (cliente_id)
       references tb_cliente (id);

    alter table TB_CLIENTE_ENDERECO
       add constraint FKh4pilbdgv2i62kt67lignqxat
       foreign key (endereco_id)
       references tb_endereco (id);

    alter table TB_CLIENTE_ENDERECO
       add constraint FKe9hd85a57v19b97k79s98yqr8
       foreign key (cliente_id)
       references tb_cliente (id);

    alter table TB_CUSTOM_DATA
       add constraint FKl4dh8wnk2ggj2muhk8ejclagn
       foreign key (custom_field_id)
       references TB_custom_field_definition (id);

    alter table TB_CUSTOM_DATA
       add constraint FKdo0o08go0vcg0t8gty92res9f
       foreign key (custom_data_id)
       references TB_CUSTOM_DATA (id);

    alter table TB_CUSTOM_DATA
       add constraint FKfwqipgg4jncq8uernqmtmuj11
       foreign key (entity_id)
       references tb_produto (id);

    alter table tb_produto
       add constraint FKjm741es56cwngwn3hsr15uqew
       foreign key (unidade_medida_id)
       references tb_unidade_medida (id);

    alter table tb_produto_composicao
       add constraint FKp0ouw2m2gjdkq1ifxqeewbu1d
       foreign key (produto_componente_id)
       references tb_produto (id);

    alter table tb_produto_composicao
       add constraint FKwol2pcy2wvmbq3q7x99dn1lp
       foreign key (produto_fabricado_id)
       references tb_produto (id);

    alter table tb_role_permissao
       add constraint FK1i43spbowu8fpdli5h311a97u
       foreign key (permissao_id)
       references tb_permissao (id);

    alter table tb_role_permissao
       add constraint FKshhi66xugi1k1chvvygj4of5r
       foreign key (role_id)
       references tb_role (id);



    alter table tb_usuario_contato
       add constraint FK3gh0ltuf1syctfafopd94dqlg
       foreign key (contato_id)
       references tb_contatos (id);

    alter table tb_usuario_contato
       add constraint FK128rl735ifi2ei19hvl9gv6gc
       foreign key (usuario_id)
       references tb_usuario (id);

    alter table tb_usuario_permissao
       add constraint FKduh5ix0b4v9ns4ehping4wl68
       foreign key (permissao_id)
       references tb_permissao (id);

    alter table tb_usuario_permissao
       add constraint FKse5klwt0ivx37h6opgx6ipjuw
       foreign key (usuario_id)
       references tb_usuario (id);

    alter table tb_usuario_role
       add constraint FKkix4nwaehqjwnk40e2e36903j
       foreign key (role_id)
       references tb_role (id);

    alter table tb_usuario_role
       add constraint FKj4syki71kai6syrfuly9xfcq
       foreign key (usuario_id)
       references tb_usuario (id);
