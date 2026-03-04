DO SLEEP (0.125);

-- Insert BusinessPartner Addresses
INSERT INTO
    TB_BUSINESS_PARTNER_ADDRESS (businesspartner_id, address_id)
SELECT
    c.id as businesspartner_id,
    a.id as address_id
FROM
    tb_business_partner c
    CROSS JOIN tb_address a
WHERE
    c.deleted = 0
    AND a.deleted = 0
    AND a.id = c.id
ORDER BY
    c.id,
    a.id