 -- Insert Product Compositions
 -- Kit Limpeza Completo (PROD019) is composed of:
 INSERT INTO
     tb_product_composition (
         dataAtualizacao,
         dataCriacao,
         observacoes,
         quantidadeNecessaria,
         sequencia,
         product_componente_id,
         product_fabricado_id
     )
 SELECT
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     'Quantidade necessária no kit',
     1.0,
     1,
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD011'
         LIMIT
             1
     ),
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD019'
         LIMIT
             1
     )
 WHERE
     EXISTS (
         SELECT
             1
         FROM
             tb_product
         WHERE
             codigo IN ('PROD011', 'PROD019')
     );
 -- Kit Limpeza: Sabão em Pó
 INSERT INTO
     tb_product_composition (
         dataAtualizacao,
         dataCriacao,
         observacoes,
         quantidadeNecessaria,
         sequencia,
         product_componente_id,
         product_fabricado_id
     )
 SELECT
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     'Quantidade necessária no kit',
     1.0,
     2,
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD012'
         LIMIT
             1
     ),
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD019'
         LIMIT
             1
     )
 WHERE
     EXISTS (
         SELECT
             1
         FROM
             tb_product
         WHERE
             codigo IN ('PROD012', 'PROD019')
     );
 -- Kit Limpeza: Papel Higiênico
 INSERT INTO
     tb_product_composition (
         dataAtualizacao,
         dataCriacao,
         observacoes,
         quantidadeNecessaria,
         sequencia,
         product_componente_id,
         product_fabricado_id
     )
 SELECT
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     'Quantidade necessária no kit',
     1.0,
     3,
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD013'
         LIMIT
             1
     ),
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD019'
         LIMIT
             1
     )
 WHERE
     EXISTS (
         SELECT
             1
         FROM
             tb_product
         WHERE
             codigo IN ('PROD013', 'PROD019')
     );
 -- Kit Higiene Pessoal (PROD020) is composed of:
 -- Kit Higiene: Shampoo Neutro
 INSERT INTO
     tb_product_composition (
         dataAtualizacao,
         dataCriacao,
         observacoes,
         quantidadeNecessaria,
         sequencia,
         product_componente_id,
         product_fabricado_id
     )
 SELECT
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     'Quantidade necessária no kit',
     2.0,
     1,
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD014'
         LIMIT
             1
     ),
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD020'
         LIMIT
             1
     )
 WHERE
     EXISTS (
         SELECT
             1
         FROM
             tb_product
         WHERE
             codigo IN ('PROD014', 'PROD020')
     );
 -- Kit Higiene: Sabonete Glicerina
 INSERT INTO
     tb_product_composition (
         dataAtualizacao,
         dataCriacao,
         observacoes,
         quantidadeNecessaria,
         sequencia,
         product_componente_id,
         product_fabricado_id
     )
 SELECT
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     'Quantidade necessária no kit',
     3.0,
     2,
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD015'
         LIMIT
             1
     ),
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD020'
         LIMIT
             1
     )
 WHERE
     EXISTS (
         SELECT
             1
         FROM
             tb_product
         WHERE
             codigo IN ('PROD015', 'PROD020')
     );
 -- Kit Higiene: Café Solúvel
 INSERT INTO
     tb_product_composition (
         dataAtualizacao,
         dataCriacao,
         observacoes,
         quantidadeNecessaria,
         sequencia,
         product_componente_id,
         product_fabricado_id
     )
 SELECT
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     'Quantidade necessária no kit',
     1.0,
     3,
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD006'
         LIMIT
             1
     ),
     (
         SELECT
             id
         FROM
             tb_product
         WHERE
             codigo = 'PROD020'
         LIMIT
             1
     )
 WHERE
     EXISTS (
         SELECT
             1
         FROM
             tb_product
         WHERE
             codigo IN ('PROD006', 'PROD020')
     );