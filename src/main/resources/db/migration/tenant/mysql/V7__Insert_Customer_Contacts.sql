DO SLEEP (0.125);

-- Insert BusinessPartner Contacts
INSERT INTO
    TB_BUSINESS_PARTNER_CONTACT (businesspartner_id, contact_id)
VALUES
    -- Distribuidora Brasil - multiple contacts
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'DISTRIBUIDORA BRASIL'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'TELEFONE'
                AND principal = 1
            LIMIT
                1
        )
    ),
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'DISTRIBUIDORA BRASIL'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'CELULAR'
                AND principal = 1
            LIMIT
                1
        )
    ),
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'DISTRIBUIDORA BRASIL'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'EMAIL'
                AND principal = 1
            LIMIT
                1
        )
    ),
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'DISTRIBUIDORA BRASIL'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'WHATSAPP'
            LIMIT
                1
        )
    ),
    -- Loja Central - multiple contacts
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'LOJA CENTRAL'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'TELEFONE'
            ORDER BY
                id
            LIMIT
                1
            OFFSET
                1
        )
    ),
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'LOJA CENTRAL'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'EMAIL'
            ORDER BY
                id
            LIMIT
                1
            OFFSET
                1
        )
    ),
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'LOJA CENTRAL'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'INSTAGRAM'
            LIMIT
                1
        )
    ),
    -- Fornecedor ABC - multiple contacts
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'FORNECEDOR ABC'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'TELEFONE'
            ORDER BY
                id
            LIMIT
                1
            OFFSET
                2
        )
    ),
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'FORNECEDOR ABC'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'EMAIL'
            ORDER BY
                id
            LIMIT
                1
            OFFSET
                2
        )
    ),
    -- João da Silva
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'João da Silva'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'CELULAR'
            ORDER BY
                id
            LIMIT
                1
            OFFSET
                1
        )
    ),
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'João da Silva'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'EMAIL'
            ORDER BY
                id
            LIMIT
                1
            OFFSET
                3
        )
    ),
    -- Maria dos Santos
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'Maria dos Santos'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'TELEFONE'
            ORDER BY
                id
            LIMIT
                1
            OFFSET
                3
        )
    ),
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'Maria dos Santos'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'EMAIL'
            ORDER BY
                id
            LIMIT
                1
            OFFSET
                4
        )
    ),
    -- Suprimentos XYZ
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'SUPRIMENTOS XYZ'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'TELEFONE'
            ORDER BY
                id
            LIMIT
                1
            OFFSET
                4
        )
    ),
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'SUPRIMENTOS XYZ'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'FACEBOOK'
            LIMIT
                1
        )
    ),
    -- Pedro Oliveira
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'Pedro Oliveira'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'CELULAR'
            ORDER BY
                id
            LIMIT
                1
            OFFSET
                2
        )
    ),
    (
        (
            SELECT
                id
            FROM
                tb_business_partner
            WHERE
                nome = 'Pedro Oliveira'
            LIMIT
                1
        ),
        (
            SELECT
                id
            FROM
                tb_contacts
            WHERE
                tipo = 'LINKEDIN'
            LIMIT
                1
        )
    );