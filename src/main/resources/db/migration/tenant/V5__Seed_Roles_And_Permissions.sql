-- ==================================================================================
-- MIGRATION: V12__Seed_Permissoes.sql
-- DESCRIÇÃO: Adiciona todas as permissões do sistema extraídas das classes
--            de Permissions. Essas permissões são usadas para controle de acesso
--            e autorização em toda a aplicação.
-- ==================================================================================
-- ===================== USUÁRIO PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'usuario.criar',
        'Criar Usuário',
        'usuario',
        'CRIAR',
        TRUE
    ),
    (
        'usuario.atualizar',
        'Atualizar Usuário',
        'usuario',
        'EDITAR',
        TRUE
    ),
    (
        'usuario.listar',
        'Listar Usuários',
        'usuario',
        'VISUALIZAR',
        TRUE
    ),
    (
        'usuario.deletar',
        'Deletar Usuário',
        'usuario',
        'EXCLUIR',
        TRUE
    ),
    (
        'usuario.ativar',
        'Ativar Usuário',
        'usuario',
        'EDITAR',
        TRUE
    ),
    (
        'usuario.desativar',
        'Desativar Usuário',
        'usuario',
        'EDITAR',
        TRUE
    ),
    (
        'usuario.aprovar',
        'Aprovar Usuário',
        'usuario',
        'EDITAR',
        TRUE
    ),
    (
        'usuario.rejeitar',
        'Rejeitar Usuário',
        'usuario',
        'EDITAR',
        TRUE
    ),
    (
        'usuario.gerenciar.permissoes',
        'Gerenciar Permissões do Usuário',
        'usuario',
        'EDITAR',
        TRUE
    ),
    (
        'usuario.adicionar.permissao',
        'Adicionar Permissão a Usuário',
        'usuario',
        'EDITAR',
        TRUE
    ),
    (
        'usuario.remover.permissao',
        'Remover Permissão de Usuário',
        'usuario',
        'EDITAR',
        TRUE
    ),
    (
        'usuario.gerenciar.roles',
        'Gerenciar Roles do Usuário',
        'usuario',
        'EDITAR',
        TRUE
    ),
    (
        'usuario.adicionar.role',
        'Adicionar Role a Usuário',
        'usuario',
        'EDITAR',
        TRUE
    ),
    (
        'usuario.remover.role',
        'Remover Role de Usuário',
        'usuario',
        'EDITAR',
        TRUE
    );

-- ===================== EMPRESA PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'empresa.buscar',
        'Buscar Empresa',
        'empresa',
        'VISUALIZAR',
        TRUE
    ),
    (
        'empresa.atualizar',
        'Atualizar Empresa',
        'empresa',
        'EDITAR',
        TRUE
    );

-- ===================== ENDEREÇO PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'enderecos.criar',
        'Criar Endereço',
        'enderecos',
        'CRIAR',
        TRUE
    ),
    (
        'enderecos.atualizar',
        'Atualizar Endereço',
        'enderecos',
        'EDITAR',
        TRUE
    ),
    (
        'enderecos.visualizar',
        'Visualizar Endereço',
        'enderecos',
        'VISUALIZAR',
        TRUE
    ),
    (
        'enderecos.deletar',
        'Deletar Endereço',
        'enderecos',
        'EXCLUIR',
        TRUE
    );

-- ===================== PERMISSÃO PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'permissao.criar',
        'Criar Permissão',
        'permissao',
        'CRIAR',
        TRUE
    ),
    (
        'permissao.atualizar',
        'Atualizar Permissão',
        'permissao',
        'EDITAR',
        TRUE
    ),
    (
        'permissao.visualizar',
        'Visualizar Permissão',
        'permissao',
        'VISUALIZAR',
        TRUE
    ),
    (
        'permissao.deletar',
        'Deletar Permissão',
        'permissao',
        'EXCLUIR',
        TRUE
    );

-- ===================== ROLE PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    ('role.criar', 'Criar Role', 'role', 'CRIAR', TRUE),
    (
        'role.atualizar',
        'Atualizar Role',
        'role',
        'EDITAR',
        TRUE
    ),
    (
        'role.visualizar',
        'Visualizar Role',
        'role',
        'VISUALIZAR',
        TRUE
    ),
    (
        'role.deletar',
        'Deletar Role',
        'role',
        'EXCLUIR',
        TRUE
    ),
    (
        'role.associar',
        'Associar Permissões à Role',
        'role',
        'EDITAR',
        TRUE
    );

-- ===================== PRODUTO PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'produto.criar',
        'Criar Produto',
        'produto',
        'CRIAR',
        TRUE
    ),
    (
        'produto.atualizar',
        'Atualizar Produto',
        'produto',
        'EDITAR',
        TRUE
    ),
    (
        'produto.visualizar',
        'Visualizar Produto',
        'produto',
        'VISUALIZAR',
        TRUE
    ),
    (
        'produto.deletar',
        'Deletar Produto',
        'produto',
        'EXCLUIR',
        TRUE
    ),
    (
        'produto.ativar',
        'Ativar Produto',
        'produto',
        'EDITAR',
        TRUE
    ),
    (
        'produto.desativar',
        'Desativar Produto',
        'produto',
        'EDITAR',
        TRUE
    ),
    (
        'produto.bloquear',
        'Bloquear Produto',
        'produto',
        'EDITAR',
        TRUE
    ),
    (
        'produto.descontinuar',
        'Descontinuar Produto',
        'produto',
        'EDITAR',
        TRUE
    );

-- ===================== COMPOSIÇÃO PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'composicao.criar',
        'Criar Composição',
        'composicao',
        'CRIAR',
        TRUE
    ),
    (
        'composicao.atualizar',
        'Atualizar Composição',
        'composicao',
        'EDITAR',
        TRUE
    ),
    (
        'composicao.visualizar',
        'Visualizar Composição',
        'composicao',
        'VISUALIZAR',
        TRUE
    ),
    (
        'composicao.deletar',
        'Deletar Composição',
        'composicao',
        'EXCLUIR',
        TRUE
    );

-- ===================== LISTA EXPANDIDA PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'lista-expandida.gerar',
        'Gerar Lista Expandida',
        'lista-expandida',
        'OUTRO',
        TRUE
    ),
    (
        'lista-expandida.gerar_compra',
        'Gerar Compra da Lista Expandida',
        'lista-expandida',
        'OUTRO',
        TRUE
    );

-- ===================== CONTATO PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'contatos.criar',
        'Criar Contato',
        'contatos',
        'CRIAR',
        TRUE
    ),
    (
        'contatos.atualizar',
        'Atualizar Contato',
        'contatos',
        'EDITAR',
        TRUE
    ),
    (
        'contatos.visualizar',
        'Visualizar Contato',
        'contatos',
        'VISUALIZAR',
        TRUE
    ),
    (
        'contatos.deletar',
        'Deletar Contato',
        'contatos',
        'EXCLUIR',
        TRUE
    );

-- ===================== CLIENTE PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'cliente.criar',
        'Criar Cliente',
        'cliente',
        'CRIAR',
        TRUE
    ),
    (
        'cliente.atualizar',
        'Atualizar Cliente',
        'cliente',
        'EDITAR',
        TRUE
    ),
    (
        'cliente.visualizar',
        'Visualizar Cliente',
        'cliente',
        'VISUALIZAR',
        TRUE
    ),
    (
        'cliente.deletar',
        'Deletar Cliente',
        'cliente',
        'EXCLUIR',
        TRUE
    );

-- ===================== CAMPOS CUSTOM PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'campos-custom.criar',
        'Criar Campo Customizado',
        'campos-custom',
        'CRIAR',
        TRUE
    ),
    (
        'campos-custom.atualizar',
        'Atualizar Campo Customizado',
        'campos-custom',
        'EDITAR',
        TRUE
    ),
    (
        'campos-custom.visualizar',
        'Visualizar Campo Customizado',
        'campos-custom',
        'VISUALIZAR',
        TRUE
    ),
    (
        'campos-custom.deletar',
        'Deletar Campo Customizado',
        'campos-custom',
        'EXCLUIR',
        TRUE
    );

-- ===================== UNIDADE MEDIDA PERMISSIONS =====================
INSERT INTO
    permissao (codigo, nome, modulo, acao, ativo)
VALUES
    (
        'unidade-medida.criar',
        'Criar Unidade de Medida',
        'unidade-medida',
        'CRIAR',
        TRUE
    ),
    (
        'unidade-medida.atualizar',
        'Atualizar Unidade de Medida',
        'unidade-medida',
        'EDITAR',
        TRUE
    ),
    (
        'unidade-medida.visualizar',
        'Visualizar Unidade de Medida',
        'unidade-medida',
        'VISUALIZAR',
        TRUE
    ),
    (
        'unidade-medida.deletar',
        'Deletar Unidade de Medida',
        'unidade-medida',
        'EXCLUIR',
        TRUE
    ),
    (
        'unidade-medida.ativar',
        'Ativar Unidade de Medida',
        'unidade-medida',
        'EDITAR',
        TRUE
    ),
    (
        'unidade-medida.desativar',
        'Desativar Unidade de Medida',
        'unidade-medida',
        'EDITAR',
        TRUE
    );

-- ==================================================================================
-- ASSOCIATE PERMISSIONS TO ROLES
-- ==================================================================================
-- USUARIO role - basic read permissions
INSERT IGNORE INTO role_permissao (role_id, permissao_id)
SELECT
    1,
    id
FROM
    permissao
WHERE
    codigo IN (
        'usuario.listar',
        'produto.visualizar',
        'enderecos.visualizar',
        'contatos.visualizar',
        'cliente.visualizar',
        'empresa.buscar'
    );

-- GESTOR role - read and write permissions
INSERT IGNORE INTO role_permissao (role_id, permissao_id)
SELECT
    2,
    id
FROM
    permissao
WHERE
    codigo IN (
        'usuario.listar',
        'usuario.atualizar',
        'produto.visualizar',
        'produto.criar',
        'produto.atualizar',
        'produto.ativar',
        'produto.desativar',
        'enderecos.visualizar',
        'enderecos.criar',
        'enderecos.atualizar',
        'contatos.visualizar',
        'contatos.criar',
        'contatos.atualizar',
        'cliente.visualizar',
        'cliente.criar',
        'cliente.atualizar',
        'empresa.buscar',
        'empresa.atualizar',
        'composicao.visualizar',
        'composicao.criar',
        'composicao.atualizar',
        'lista-expandida.gerar',
        'lista-expandida.gerar_compra'
    );

-- ADMIN role - all permissions
INSERT IGNORE INTO role_permissao (role_id, permissao_id)
SELECT
    3,
    id
FROM
    permissao
WHERE
    ativo = TRUE;