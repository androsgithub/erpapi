-- Insert Customer Addresses
INSERT INTO
    TB_CUSTOMER_ADDRESS (customer_id, address_id)
SELECT
    c.id as customer_id,
    a.id as address_id
FROM
    tb_customer c
    CROSS JOIN tb_address a
WHERE
    c.deleted = 0
    AND a.deleted = 0
    AND a.id = c.id
ORDER BY
    c.id,
    a.id