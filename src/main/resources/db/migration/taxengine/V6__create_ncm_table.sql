CREATE TABLE tb_ncm (
    ncm              VARCHAR(10)  NOT NULL,
    descricao        TEXT NOT NULL,
    data_inicio      VARCHAR(16)         NOT NULL,
    data_fim         VARCHAR(16)         NOT NULL,
    tipo_ato_ini     VARCHAR(50)  NOT NULL,
    numero_ato_ini   VARCHAR(10)  NOT NULL,
    ano_ato_ini      INTEGER      NOT NULL,
    tipo_ato_fim     VARCHAR(50),
    numero_ato_fim   VARCHAR(10),
    ano_ato_fim      INTEGER,

    CONSTRAINT pk_ncm PRIMARY KEY (ncm, data_inicio)
);