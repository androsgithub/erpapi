create table tb_address (
                            id bigint not null auto_increment,
                            created_at datetime(6),
                            created_by bigint,
                            deleted bit,
                            deleted_at datetime(6),
                            updated_at datetime(6),
                            updated_by bigint,
                            version bigint,
                            scope enum ('GLOBAL','GROUP','TENANT'),
                            tenant_group_id bigint,
                            tenant_id bigint not null,
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

create table tb_contacts (
                             id bigint not null auto_increment,
                             created_at datetime(6),
                             created_by bigint,
                             deleted bit,
                             deleted_at datetime(6),
                             updated_at datetime(6),
                             updated_by bigint,
                             version bigint,
                             scope enum ('GLOBAL','GROUP','TENANT'),
                             tenant_group_id bigint,
                             tenant_id bigint not null,
                             ativo bit not null,
                             descricao varchar(255),
                             principal bit not null,
                             tipo enum ('CELULAR','EMAIL','FACEBOOK','INSTAGRAM','LINKEDIN','OUTRO','TELEFONE','WEBSITE','WHATSAPP') not null,
                             valor varchar(255) not null,
                             primary key (id)
) engine=InnoDB;

create table tb_permission (
                               id bigint not null auto_increment,
                               created_at datetime(6),
                               created_by bigint,
                               deleted bit,
                               deleted_at datetime(6),
                               updated_at datetime(6),
                               updated_by bigint,
                               version bigint,
                               scope enum ('GLOBAL','GROUP','TENANT'),
                               tenant_group_id bigint,
                               tenant_id bigint not null,
                               acao enum ('CRIAR','EDITAR','EXCLUIR','OUTRO','VISUALIZAR') not null,
                               codigo varchar(255) not null,
                               modulo varchar(255) not null,
                               nome varchar(255) not null,
                               primary key (id)
) engine=InnoDB;

create table tb_role (
                         id bigint not null auto_increment,
                         created_at datetime(6),
                         created_by bigint,
                         deleted bit,
                         deleted_at datetime(6),
                         updated_at datetime(6),
                         updated_by bigint,
                         version bigint,
                         scope enum ('GLOBAL','GROUP','TENANT'),
                         tenant_group_id bigint,
                         tenant_id bigint not null,
                         nome varchar(255) not null,
                         primary key (id)
) engine=InnoDB;

create table tb_role_permission (
                                    role_id bigint not null,
                                    permission_id bigint not null,
                                    primary key (role_id, permission_id)
) engine=InnoDB;

create table tb_tenant_group (
                                 id bigint not null auto_increment,
                                 active bit not null,
                                 created_at datetime(6) not null,
                                 description varchar(500),
                                 name varchar(255) not null,
                                 updated_at datetime(6) not null,
                                 primary key (id)
) engine=InnoDB;

create table tb_tnt (
                        id bigint not null auto_increment,
                        active bit not null,
                        created_at datetime(6) not null,
                        email varchar(150) not null,
                        name varchar(200) not null,
                        phone varchar(20),
                        trial bit not null,
                        trial_expires_at datetime(6),
                        updated_at datetime(6),
                        plan_id bigint not null,
                        primary key (id)
) engine=InnoDB;

create table tb_tnt_allow_domains (
                                      id bigint not null auto_increment,
                                      active bit not null,
                                      created_at datetime(6) not null,
                                      domain varchar(255) not null,
                                      type enum ('CORS','EMAIL','SSO') not null,
                                      tenant_id bigint not null,
                                      primary key (id)
) engine=InnoDB;

create table tb_tnt_config (
                               id bigint not null auto_increment,
                               allow_api_access bit not null,
                               app_description varchar(500),
                               app_name varchar(150) not null,
                               created_at datetime(6) not null,
                               maintenance_mode bit not null,
                               multi_branch bit not null,
                               onboarding_done bit not null,
                               slug varchar(100) not null,
                               support_email varchar(150),
                               updated_at datetime(6),
                               white_label bit not null,
                               tenant_id bigint not null,
                               primary key (id)
) engine=InnoDB;

create table tb_tnt_datasource (
                                   id bigint not null auto_increment,
                                   active bit not null,
                                   connection_timeout integer,
                                   created_at datetime(6) not null,
                                   db_type enum ('CLICKHOUSE','COCKROACHDB','DB2','DERBY','FIREBIRD','H2','HSQLDB','INFORMIX','MARIADB','MYSQL','ORACLE','POSTGRESQL','SQLITE','SQL_SERVER','SYBASE','VERTICA') not null,
                                   driver_class varchar(200) not null,
                                   idle_timeout integer,
                                   password_encrypted varchar(500) not null,
                                   pool_max integer,
                                   pool_min integer,
                                   schema_name varchar(100),
                                   test_status enum ('FAILED','PENDING','SUCCESS'),
                                   tested_at datetime(6),
                                   updated_at datetime(6),
                                   url varchar(500) not null,
                                   username varchar(100) not null,
                                   tenant_id bigint not null,
                                   primary key (id)
) engine=InnoDB;

create table tb_tnt_features (
                                 id bigint not null auto_increment,
                                 active bit not null,
                                 bean_name varchar(200) not null,
                                 created_at datetime(6) not null,
                                 description varchar(500),
                                 feature_key varchar(100) not null,
                                 updated_at datetime(6),
                                 tenant_id bigint not null,
                                 primary key (id)
) engine=InnoDB;

create table tb_tnt_fiscal (
                               id bigint not null auto_increment,
                               city_registration varchar(20),
                               cnpj varchar(14) not null,
                               created_at datetime(6) not null,
                               legal_name varchar(150) not null,
                               state_registration varchar(20),
                               tax_regime varchar(50),
                               trade_name varchar(150),
                               updated_at datetime(6),
                               tenant_id bigint not null,
                               primary key (id)
) engine=InnoDB;

create table tb_tnt_group_tnt (
                                  tnt_group_id bigint not null,
                                  tnt_id bigint not null,
                                  primary key (tnt_group_id, tnt_id)
) engine=InnoDB;

create table tb_tnt_plan (
                             id bigint not null auto_increment,
                             active bit not null,
                             created_at datetime(6) not null,
                             description varchar(500),
                             max_users integer not null,
                             monthly_price decimal(10,2) not null,
                             name varchar(100) not null,
                             updated_at datetime(6),
                             primary key (id)
) engine=InnoDB;

create table tb_tnt_security (
                                 id bigint not null auto_increment,
                                 created_at datetime(6) not null,
                                 lockout_duration_min integer,
                                 max_login_attempts integer,
                                 min_password_length integer,
                                 password_expiration_days integer,
                                 require_number bit not null,
                                 require_special bit not null,
                                 require_uppercase bit not null,
                                 updated_at datetime(6),
                                 tenant_id bigint not null,
                                 primary key (id)
) engine=InnoDB;

create table tb_tnt_visual (
                               id bigint not null auto_increment,
                               background_image_url varchar(500),
                               border_radius varchar(20),
                               color_accent varchar(7),
                               color_background varchar(7),
                               color_danger varchar(7),
                               color_primary varchar(7),
                               color_secondary varchar(7),
                               color_success varchar(7),
                               color_text varchar(7),
                               color_warning varchar(7),
                               created_at datetime(6) not null,
                               email_logo_url varchar(500),
                               favicon_url varchar(500),
                               font_cdn_url varchar(500),
                               font_family varchar(100),
                               logo_large_url varchar(500),
                               logo_url varchar(500),
                               sidebar_collapsed bit not null,
                               theme varchar(50),
                               updated_at datetime(6),
                               tenant_id bigint not null,
                               primary key (id)
) engine=InnoDB;

create table tb_user (
                         id bigint not null auto_increment,
                         created_at datetime(6),
                         created_by bigint,
                         deleted bit,
                         deleted_at datetime(6),
                         updated_at datetime(6),
                         updated_by bigint,
                         version bigint,
                         approved_at datetime(6),
                         approved_by bigint,
                         cpf varchar(255) not null,
                         email varchar(255) not null,
                         name varchar(255) not null,
                         password_hash varchar(255) not null,
                         status enum ('APPROVE_PENDING','BLOCKED','DISABLED','ENABLED','REJECTED') not null,
                         primary key (id)
) engine=InnoDB;

create table tb_user_contact (
                                 user_id bigint not null,
                                 contact_id bigint not null,
                                 primary key (user_id, contact_id)
) engine=InnoDB;

create table tb_user_permission (
                                    user_id bigint not null,
                                    permission_id bigint not null,
                                    primary key (user_id, permission_id)
) engine=InnoDB;

create table tb_user_role (
                              user_id bigint not null,
                              role_id bigint not null,
                              primary key (user_id, role_id)
) engine=InnoDB;

create table tb_user_tnt (
                             user_id bigint not null,
                             tnt_id bigint not null,
                             primary key (user_id, tnt_id)
) engine=InnoDB;

alter table tb_permission
    add constraint UKqvmybc9gau6vod2h7wl1wragk unique (codigo);

alter table tb_role
    add constraint UK6e9vttep485ay7pmjccprgdus unique (nome);

alter table tb_role_permission
    add constraint UKg7v1tr7uo3kf265hexplc4xvv unique (permission_id);

create index idx_tenant_group_active
    on tb_tenant_group (active);

create index idx_tenant_allow_domains_tenant_id
    on tb_tnt_allow_domains (tenant_id);

alter table tb_tnt_allow_domains
    add constraint UKrlsg28h08nkoucgftb1twraud unique (tenant_id, domain);

alter table tb_tnt_config
    add constraint UKfxya4ikqain54so79fuw32kpw unique (tenant_id);

create index idx_tenant_datasource_tenant_id
    on tb_tnt_datasource (tenant_id);

create index idx_tenant_datasource_active
    on tb_tnt_datasource (active);

alter table tb_tnt_datasource
    add constraint UK9vveyte2pqlbx954qjafvboih unique (tenant_id);

create index idx_tenant_features_tenant_id
    on tb_tnt_features (tenant_id);

create index idx_tenant_features_active
    on tb_tnt_features (active);

alter table tb_tnt_features
    add constraint UK1epclajmqmj3lgij5i49iisaf unique (tenant_id, feature_key);

alter table tb_tnt_fiscal
    add constraint UK1go9a7woa7yytkl6bbhyvqimu unique (tenant_id);

alter table tb_tnt_fiscal
    add constraint UKlt2esgyl2jgllnbkrwanakh3e unique (cnpj);

alter table tb_tnt_security
    add constraint UKaaxb6cvs7w8e8hx8nn5l3p8a3 unique (tenant_id);

alter table tb_tnt_visual
    add constraint UKb90n2c9p2cj0bfnvaqf1i6yoc unique (tenant_id);

alter table tb_user
    add constraint UK869sa3rebuf3nm0d4jwxdtouk unique (cpf);

alter table tb_user
    add constraint UK4vih17mube9j7cqyjlfbcrk4m unique (email);

alter table tb_user_contact
    add constraint UK6eaoe63exxesfelbotobxwv7f unique (contact_id);

alter table tb_user_permission
    add constraint UK44w3gaqxmtfvn2xslng1a5pi0 unique (permission_id);

alter table tb_user_role
    add constraint UK1txpcisco2l99tl5qqshr6ptp unique (role_id);

alter table tb_user_tnt
    add constraint UKiv97fe2su39qd9ww0vj96ti92 unique (tnt_id);

alter table tb_role_permission
    add constraint FKn1fuq85qvjb8i00n532hji0k5
        foreign key (permission_id)
            references tb_permission (id);

alter table tb_role_permission
    add constraint FKopy61ig7uc3g8pc5xky4c9sqs
        foreign key (role_id)
            references tb_role (id);

alter table tb_tnt
    add constraint FKiuoqtmr51chsebyoiel7fi4gj
        foreign key (plan_id)
            references tb_tnt_plan (id);

alter table tb_tnt_allow_domains
    add constraint FKh2hfst1laichn7b9pabxppf61
        foreign key (tenant_id)
            references tb_tnt (id);

alter table tb_tnt_config
    add constraint FK24412v0f2abulgcklvl88ntq0
        foreign key (tenant_id)
            references tb_tnt (id);

alter table tb_tnt_datasource
    add constraint FKolft6u9cdqtltk5od8fi3wdpj
        foreign key (tenant_id)
            references tb_tnt (id);

alter table tb_tnt_features
    add constraint FKn6ox8ro48o16taqlmpf4p94jb
        foreign key (tenant_id)
            references tb_tnt (id);

alter table tb_tnt_fiscal
    add constraint FKkcwpsdr9biiccuycqbyueipbp
        foreign key (tenant_id)
            references tb_tnt (id);

alter table tb_tnt_group_tnt
    add constraint FKra6xv7gfkqdkjab0fdgpq7qhr
        foreign key (tnt_id)
            references tb_tnt (id);

alter table tb_tnt_group_tnt
    add constraint FKfv5aagmkdpldxs9omake8ixw6
        foreign key (tnt_group_id)
            references tb_tenant_group (id);

alter table tb_tnt_security
    add constraint FKkfhfagflmqeauxfjwlsc45g8s
        foreign key (tenant_id)
            references tb_tnt (id);

alter table tb_tnt_visual
    add constraint FKs5y36ad5xptjh6l247q87a2x8
        foreign key (tenant_id)
            references tb_tnt (id);

alter table tb_user_contact
    add constraint FKa67d7o0dt7qdhby08x6erv3bn
        foreign key (contact_id)
            references tb_contacts (id);

alter table tb_user_contact
    add constraint FKlhgj4r45vl0ioa06q7dgam6n0
        foreign key (user_id)
            references tb_user (id);

alter table tb_user_permission
    add constraint FKjhgg2p2m7j547g6wl1dur8sga
        foreign key (permission_id)
            references tb_permission (id);

alter table tb_user_permission
    add constraint FKooh8t1qmj769ttjnsqtieriqb
        foreign key (user_id)
            references tb_user (id);

alter table tb_user_role
    add constraint FKea2ootw6b6bb0xt3ptl28bymv
        foreign key (role_id)
            references tb_role (id);

alter table tb_user_role
    add constraint FK7vn3h53d0tqdimm8cp45gc0kl
        foreign key (user_id)
            references tb_user (id);

alter table tb_user_tnt
    add constraint FK5d22ddkook556efnctjj2f58k
        foreign key (tnt_id)
            references tb_tnt (id);

alter table tb_user_tnt
    add constraint FKqy6u5x9j7trpatc0w97nlfaxk
        foreign key (user_id)
            references tb_user (id);