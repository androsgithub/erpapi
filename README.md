# ERP API

Sistema de ERP (Enterprise Resource Planning) desenvolvido em **Spring Boot 4.0.1** com **Java 17**.

## 📋 Visão Geral

Aplicação completa de ERP com suporte a múltiplas funcionalidades de gestão empresarial, incluindo gerenciamento de usuários, produtos, empresas, endereços, permissões e unidades de medida.

## 🚀 Tecnologias

- **Framework**: Spring Boot 4.0.1
- **Linguagem**: Java 17
- **Build**: Maven
- **Segurança**: Spring Security
- **Banco de Dados**: JPA/Hibernate
- **WebSocket**: Suporte para comunicação em tempo real

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/api/erp/
│   │   ├── ErpApplication.java         # Classe principal da aplicação
│   │   └── v1/
│   │       ├── features/
│   │       │   ├── empresa/            # Gestão de empresas
│   │       │   ├── endereco/           # Gestão de endereços
│   │       │   ├── permissao/          # Gestão de permissões
│   │       │   ├── produto/            # Gestão de produtos
│   │       │   ├── unidademedida/      # Gestão de unidades de medida
│   │       │   └── usuario/            # Gestão de usuários
│   │       └── shared/
│   │           ├── domain/             # Entidades e conceitos compartilhados
│   │           ├── exception/          # Exceções customizadas
│   │           ├── infrastructure/     # Configurações de infraestrutura
│   │           └── websocket/          # Configurações WebSocket
│   └── resources/
│       ├── application.properties      # Configurações da aplicação
│       ├── static/                     # Arquivos estáticos (HTML, CSS, JS)
│       └── templates/                  # Templates dinâmicos
│
└── test/
    └── java/com/api/erp/               # Testes automatizados
```

## 🔧 Requisitos

- Java 17+
- Maven 3.6+
- Git

## 🏃 Como Executar

### 1. Clone o repositório
```bash
git clone https://github.com/androsgithub/erpapi.git
cd erpapi
```

### 2. Build do projeto
```bash
./mvnw clean install
```

### 3. Execute a aplicação
```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## 📚 Módulos Principais

### Empresa
Gestão completa de informações de empresas vinculadas ao ERP.

### Usuário
Gerenciamento de usuários com suporte a controle de acesso baseado em roles e permissões.

### Produto
Catálogo completo de produtos com categorização e gestão de estoque.

### Unidade de Medida
Sistema de unidades de medida para produtos e operações.

### Endereço
Gestão centralizada de endereços para empresas, filiais e clientes.

### Permissão
Sistema granular de permissões e controle de acesso.

## 🔐 Segurança

A aplicação utiliza **Spring Security** para autenticação e autorização, garantindo que apenas usuários autorizados possam acessar recursos específicos.

## 📖 Documentação

Toda a documentação do projeto está disponível na pasta `DOCS/`:

- **CHECKLIST_COMPLETO.md** - Checklist de funcionalidades
- **CLASSIFICACAO_FISCAL_AJUSTE.md** - Informações de classificação fiscal
- **HELP.md** - Guia de ajuda
- **IMPLEMENTACAO_FINALIZADA.md** - Funcionalidades implementadas
- **LEIA-ME-PRODUTOS.md** - Documentação específica de produtos
- **PRODUTO_DIAGRAMA_ARQUITETURA.md** - Diagrama da arquitetura
- **PRODUTO_FEATURE_DOCUMENTACAO.md** - Documentação de features
- **PRODUTO_GUIA_RAPIDO.md** - Guia rápido
- **PRODUTO_INDICE_CLASSES.md** - Índice de classes
- **PRODUTO_RESUMO_EXECUTIVO.md** - Resumo executivo
- **TESTE_PRATICO_MULTIPLOS_COMPONENTES.md** - Testes práticos
- **VALIDACAO_MULTIPLOS_COMPONENTES.md** - Validações
- **README_OLD.md** - README anterior (histórico)

## ✅ Testes

Execute os testes automatizados com:
```bash
./mvnw test
```

## 📊 Relatórios Maven

Para gerar relatórios do Maven:
```bash
./mvnw site
```

## 👨‍💻 Contribuições

Contribuições são bem-vindas! Por favor, abra uma issue ou pull request para sugerir melhorias.

## 📄 Licença

Este projeto é licenciado sob a MIT License.

## 📞 Suporte

Para dúvidas e suporte, consulte a documentação na pasta `DOCS/` ou abra uma issue no repositório.

---

**Último atualizado:** Dezembro de 2025
