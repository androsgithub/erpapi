

create table
   tb_business_partner (
      id bigint not null auto_increment,
      created_at datetime (6) not null default current_timestamp(6),
      created_by bigint,
      deleted bit not null,
      deleted_at datetime (6),
      scope enum ('GLOBAL', 'GROUP', 'TENANT'),
      tenant_group_id bigint,
      tenant_id bigint not null,
      updated_at datetime (6),
      updated_by bigint,
      version bigint,
      limite_credito float (53),
      limite_desconto float (53),
      protestado bit,
      restricao_financeira bit,
      aliquota_icms float (53),
      cnae_principal varchar(255),
      cnpj varchar(14),
      consumidor_final bit,
      cpf varchar(11),
      icms_contribuinte bit,
      inscricao_estadual varchar(255),
      inscricao_municipal varchar(255),
      nome_fantasia varchar(255),
      razao_social varchar(255),
      regime_tributario enum (
         'LUCRO_ARBITRADO',
         'LUCRO_PRESUMIDO',
         'LUCRO_REAL',
         'MEI',
         'SIMPLES_NACIONAL'
      ),
      rg varchar(9),
      nome varchar(255) not null,
      email_nfe varchar(255),
      email_principal varchar(255),
      enviar_email bit,
      mala_direta bit,
      status enum ('ATIVO', 'BLOQUEADO', 'INATIVO') not null,
      tipo enum ('AMBOS', 'PESSOA_FISICA', 'PESSOA_JURIDICA') not null,
      primary key (id)
   ) engine = InnoDB;

create table
   TB_BUSINESS_PARTNER_CONTACT (
      businesspartner_id bigint not null,
      contact_id bigint not null
   ) engine = InnoDB;

create table
   TB_BUSINESS_PARTNER_ADDRESS (
      businesspartner_id bigint not null,
      address_id bigint not null
   ) engine = InnoDB;

create table
   tb_contacts (
      id bigint not null auto_increment,
      created_at datetime (6) not null default current_timestamp(6),
      created_by bigint,
      deleted bit not null,
      deleted_at datetime (6),
      scope enum ('GLOBAL', 'GROUP', 'TENANT'),
      tenant_group_id bigint,
      tenant_id bigint not null,
      updated_at datetime (6),
      updated_by bigint,
      version bigint,
      ativo bit not null,
      descricao varchar(255),
      principal bit not null,
      tipo enum (
         'CELULAR',
         'EMAIL',
         'FACEBOOK',
         'INSTAGRAM',
         'LINKEDIN',
         'OUTRO',
         'TELEFONE',
         'WEBSITE',
         'WHATSAPP'
      ) not null,
      valor varchar(255) not null,
      primary key (id)
   ) engine = InnoDB;

create table
   TB_CUSTOM_DATA (
      id bigint not null auto_increment,
      value longtext,
      custom_field_id bigint,
      entity_id bigint not null,
      custom_data_id bigint,
      primary key (id)
   ) engine = InnoDB;

create table
   TB_custom_field_definition (
      id bigint not null auto_increment,
      created_at datetime (6) not null default current_timestamp(6),
      created_by bigint,
      deleted bit not null,
      deleted_at datetime (6),
      scope enum ('GLOBAL', 'GROUP', 'TENANT'),
      tenant_group_id bigint,
      tenant_id bigint not null,
      updated_at datetime (6),
      updated_by bigint,
      version bigint,
      active bit,
      field_key varchar(255),
      field_order integer,
      field_type enum (
         'BOOLEAN',
         'DATE',
         'EMAIL',
         'MULTI_SELECT',
         'NUMBER',
         'SELECT',
         'TEXT'
      ),
      label varchar(255),
      metadata varchar(255),
      required bit,
      target varchar(255),
      primary key (id)
   ) engine = InnoDB;

create table
   tb_address (
      id bigint not null auto_increment,
      created_at datetime (6) not null default current_timestamp(6),
      created_by bigint,
      deleted bit not null,
      deleted_at datetime (6),
      scope enum ('GLOBAL', 'GROUP', 'TENANT'),
      tenant_group_id bigint,
      tenant_id bigint not null,
      updated_at datetime (6),
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
      tipo enum (
         'COBRANCA',
         'COMERCIAL',
         'CORRESPONDENCIA',
         'ENTREGA',
         'OUTRO',
         'RESIDENCIAL'
      ) not null,
      primary key (id)
   ) engine = InnoDB;



create table
   tb_product (
      id bigint not null auto_increment,
      created_at datetime (6) not null default current_timestamp(6),
      created_by bigint,
      deleted bit not null,
      deleted_at datetime (6),
      scope enum ('GLOBAL', 'GROUP', 'TENANT'),
      tenant_group_id bigint,
      tenant_id bigint not null,
      updated_at datetime (6),
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
      precoCusto DECIMAL(10, 2),
      precoVenda DECIMAL(10, 2),
      status enum ('ATIVO', 'BLOQUEADO', 'DESCONTINUADO', 'INATIVO') not null,
      type enum ('COMPRADO', 'FABRICAVEL') not null,
      measure_unit_id bigint not null,
      primary key (id)
   ) engine = InnoDB;

create table
   tb_product_composition (
      id bigint not null auto_increment,
      dataAtualizacao datetime (6) not null,
      dataCriacao datetime (6) not null,
      observacoes VARCHAR(500),
      quantidadeNecessaria DECIMAL(10, 4) not null,
      sequencia integer not null,
      product_componente_id bigint not null,
      product_fabricado_id bigint not null,
      primary key (id)
   ) engine = InnoDB;

create table
   tb_measure_unit (
      id bigint not null auto_increment,
      created_at datetime (6) not null default current_timestamp(6),
      created_by bigint,
      deleted bit not null,
      deleted_at datetime (6),
      scope enum ('GLOBAL', 'GROUP', 'TENANT'),
      tenant_group_id bigint,
      tenant_id bigint not null,
      updated_at datetime (6),
      updated_by bigint,
      version bigint,
      descricao varchar(100) not null,
      sigla varchar(10) not null,
      primary key (id)
   ) engine = InnoDB;

alter table TB_BUSINESS_PARTNER_CONTACT add constraint UKoyvoyiovpks7mak7a6o9shsjf unique (contact_id);

alter table TB_BUSINESS_PARTNER_ADDRESS add constraint UK3g4mi2jwv887g1adobbi2ty7j unique (address_id);

alter table tb_product add constraint UK472kg512c3fntgnpuqi1wk1l2 unique (codigo);

alter table tb_measure_unit add constraint UKqpb3oc1tcjwvcj76341nv6tl3 unique (sigla);


alter table TB_BUSINESS_PARTNER_CONTACT add constraint FKar3q0fwokcc43i1w07alv1u9t foreign key (contact_id) references tb_contacts (id);

alter table TB_BUSINESS_PARTNER_CONTACT add constraint FKf44b9l0vsi0mdpwwwla1sfx27 foreign key (businesspartner_id) references tb_business_partner (id);

alter table TB_BUSINESS_PARTNER_ADDRESS add constraint FKh4pilbdgv2i62kt67lignqxat foreign key (address_id) references tb_address (id);

alter table TB_BUSINESS_PARTNER_ADDRESS add constraint FKe9hd85a57v19b97k79s98yqr8 foreign key (businesspartner_id) references tb_business_partner (id);

alter table TB_CUSTOM_DATA add constraint FKl4dh8wnk2ggj2muhk8ejclagn foreign key (custom_field_id) references TB_custom_field_definition (id);

alter table TB_CUSTOM_DATA add constraint FKdo0o08go0vcg0t8gty92res9f foreign key (custom_data_id) references TB_CUSTOM_DATA (id);

alter table TB_CUSTOM_DATA add constraint FKfwqipgg4jncq8uernqmtmuj11 foreign key (entity_id) references tb_product (id);

alter table tb_product add constraint FKjm741es56cwngwn3hsr15uqew foreign key (measure_unit_id) references tb_measure_unit (id);

alter table tb_product_composition add constraint FKp0ouw2m2gjdkq1ifxqeewbu1d foreign key (product_componente_id) references tb_product (id);

alter table tb_product_composition add constraint FKwol2pcy2wvmbq3q7x99dn1lp foreign key (product_fabricado_id) references tb_product (id);


