# 🌐 CLOUD.md - Documentação Arquitetural do ERPAPI

## 📌 Propósito deste Documento

Este é um documento de referência arquitetural para agentes de IA. Fornece uma visão completa de como o sistema funciona, sua estrutura, fluxos de dados e padrões utilizados. **Não contém código Java**, focando apenas em conceitos e arquitetura.

---

## 🏢 Visão Geral do ERPAPI

**ERPAPI** é uma aplicação Enterprise Resource Planning (ERP) desenvolvida em **Spring Boot 4.0.1** com **Java 21**, projetada para oferecer uma plataforma escalável de gestão empresarial com suporte completo a **múltiplos tenants** (multi-tenancy).

### Características Principais:
- ✅ Arquitetura multi-tenant com 3 estratégias diferentes
- ✅ Segregação de dados por tenant, grupo de tenants e escopo global
- ✅ Suporte a múltiplos bancos de dados e schemas
- ✅ Sistema de features extensível e modular
- ✅ Padrões de design avançados (Strategy, Decorator, Proxy)
- ✅ Observabilidade com integração SonarQube
- ✅ Spring Batch para processamento assíncrono de migrações
- ✅ Flyway para controle de versão de banco de dados

---

## 🏗️ Arquitetura Multi-Tenant

### 1. Estratégias de Multi-Tenancy Suportadas

O ERPAPI suporta 3 estratégias principais de isolamento de dados:

#### 🔹 **Estratégia 1: Segmentação por Coluna (Row-Level Isolation)**
Todos os tenants compartilham o mesmo banco de dados e tabelas, com segregação via colunas específicas:

- **Tenant ID**: Identifica o específico tenant proprietário do registro
- **Tenant Group IDs**: Agrupa múltiplos tenants para compartilhamento de registros específicos
- **Scope**: Define se um registro é:
  - **Tenant**: Pertence a um único tenant
  - **Global**: Disponível para todos os tenants do banco

*Benefício*: Sincronização de dados compartilhados em tempo real
*Limitação*: Requer filtros rigorosos em todas as queries

#### 🔹 **Estratégia 2: Segmentação por Schema**
Cada tenant (ou grupo de tenants) possui um schema separado dentro do mesmo banco de dados.

*Benefício*: Isolamento lógico completo, schemas independentes
*Limitação*: Maior complexidade operacional

#### 🔹 **Estratégia 3: Bancos Separados**
Cada tenant possui um banco de dados completamente separado.

*Benefício*: Isolamento máximo, compliance de dados, escalabilidade horizontal
*Limitação*: Maior consumo de recursos, sincronização complexa para dados compartilhados

### 2. Modelo de Dados Multi-Tenant

```
┌────────────────────────────────────────────────────┐
│                    BANCO DE DADOS                  │
├────────────────────────────────────────────────────┤
│                                                    │
│  ┌──────────────────────────────────────────────┐  │
│  │          TABELA COMPARTILHADA                │  │
│  ├──────────────────────────────────────────────┤  │
│  │ ID  │ TenantId │ TenantGroupIds │ Scope │... │  │
│  ├─────┼──────────┼────────────────┼───────┼────┤  │
│  │ 1   │ Tenant-A │ NULL           │ TENANT│... │  │
│  │ 2   │ Tenant-B │ NULL           │ TENANT│... │  │
│  │ 3   │ NULL     │ Group-1,2,3    │ GROUP │... │  │
│  │ 4   │ NULL     │ NULL           │GLOBAL │... │  │
│  └──────────────────────────────────────────────┘  │
│                                                    │
└────────────────────────────────────────────────────┘

  Tenant-A vê: Registros 1 (TENANT-A) + 3 (GROUP) + 4 (GLOBAL)
  Tenant-B vê: Registros 2 (TENANT-B) + 3 (GROUP) + 4 (GLOBAL)
  Tenant-C vê: Registros 3 (GROUP) + 4 (GLOBAL)
```

---

## 🔄 Fluxos e Serviços

### 1. Padrão de Fluxos Genéricos vs Especializados

O sistema utiliza um padrão em camadas onde cada tenant pode utilizar um fluxo padrão ou um fluxo personalizado:

#### 🟢 **Fluxo Genérico (Padrão)**
Implementação base que funciona para qualquer tenant:
- Segue as regras e processos padrão do sistema
- Facilita manutenção centralizada
- Exemplo: `UserService` para gerenciamento padrão de usuários

#### 🔵 **Fluxo com Gerenciamento**
Versão estendida com processos adicionais:
- Adiciona workflows como aprovações, notificações
- Mantém compatibilidade com a versão genérica
- Exemplo: `UserServiceWithManagement` para usuários com fluxos de aprovação

#### 🟣 **Fluxo Especializado por Tenant**
Implementação customizada para um tenant específico:
- Lógica única e proprietária
- Uso de Custom Fields e Custom Data para personalizações
- Exemplo: `UserServiceEmpresa` para fluxos específicos da empresa

### 2. Resolução de Serviços via Proxy

O sistema utiliza **proxies de roteamento** que:

1. Identificam qual tenant iniciou a requisição
2. Consultam um registro de mapeamento tenant → uma implementação de serviço
3. Roteia a chamada para o serviço correto
4. Aplicam decorators (logs, métricas) de forma transparente

```
Requisição (Tenant-A)
    ↓
┌─────────────────────────┐
│  Proxy Interceptor      │
│  (Tenant Resolver)      │
└────────────┬────────────┘
             ↓
    Thread Local: Tenant-A
             ↓
┌─────────────────────────┐
│  Service Registry       │
│  Tenant-A → Service X   │
│  Tenant-B → Service X   │
│  Tenant-C → Service Y   │
└────────────┬────────────┘
             ↓
    Resolve → Service Y
             ↓
    Apply Decorators (Logs, Metrics)
             ↓
    Execute Service Y.execute()
             ↓
    Response
```

### 3. Decorators e Cross-Cutting Concerns

Decorators são adicionados aos serviços para implementar preocupações transversais:

- **Logging Decorator**: Registra entrada/saída de operações
- **Metrics Decorator**: Coleta métricas de desempenho (latência, throughput)
- **Audit Decorator**: Rastreia alterações para compliance
- **Cache Decorator**: Melhora desempengo com cache

Exemplo de composição:
```
UserService
    ↓ (decorado com)
├─ LoggingDecorator
├─ MetricsDecorator
├─ AuditDecorator
└─ CacheDecorator
```

---

## 📦 Arquitetura de Features

O sistema é organizado em **features modularizadas**, cada uma responsável por um domínio específico:

### 1. Features Principais (Core)

#### 🏷️ **Feature: Produtos**
Gerenciamento completo do catálogo de produtos:
- Cadastro de produtos com informações detalhadas
- Informações fiscais (NCM, ICMS, IPI, ISS)
- Classificação e categorização
- Composição de produtos
- Integração com TaxEngine para cálculos de impostos
- Essencial para: Estoque, Vendas, Compras, Contabilidade

#### 👥 **Feature: BusinessPartner**
Cadastro centralizado de entidades de negócio (clientes, fornecedores, parceiros comerciais e outros):
- Informações básicas e fiscais (CPF/CNPJ, Razão Social)
- Atividades econômicas (CNAEs)
- Tipos de parceria (cliente, fornecedor, outro)
- Vinculação com endereços e contatos
- Suporte a atributos customizados via Custom Fields
- Essencial para: Vendas, Compras, Contabilidade, Gestão de Relacionamento

#### 💰 **Feature: TaxEngine**
Sistema especializado para cálculos de impostos e alíquotas:
- Tabelas de alíquotas por produto e operação
- Cálculos de ICMS, IPI, ISS
- Integração com dados de NCM
- Aplicação automática de impostos em transações
- Essencial para: Vendas, Compras, Nota Fiscal

### 2. Features Compartilhadas (Shared)

Estas features servem como "sudomínios" auxiliares, reutilizadas por outras features:

#### 📍 **Feature: Endereço**
Gerenciamento centralizado de endereços:
- Entidade separada e reutilizável
- Suporta múltiplos tipos: residencial, comercial, entrega
- Integração com geografias (UF, Município)
- Utilizada por: BusinessPartner, Filiais, Pontos de Entrega

#### 📞 **Feature: Contato**
Gerenciamento centralizado de contatos:
- Telefone, E-mail, Redes Sociais
- Tipos de contato (pessoal, comercial, emergência)
- Associação com BusinessPartner e outras entidades
- Utilizada por: BusinessPartner e demais features

#### 🎯 **Feature: Custom Field / Custom Data**
Sistema de personalizações:
- **Custom Field**: Define novos campos dinamicamente
- **Custom Data**: Armazena valores desses campos
- Permite cada tenant adicionar campos específicos sem modificar o schema
- Exemplo: Um tenant pode querer "Matrícula Interna" em BusinessPartner ou campos específicos por tipo de negócio

### 3. Features Obrigatórias (Não-Opcionais)

Funcionalidades essenciais que todo sistema ERP necessita:

- **Movimento de Estoque**: Entrada, saída, ajustes
- **Financeiro**: Contas a Pagar, Contas a Receber, Caixa
- **Nota Fiscal**: Geração e validação de documentos fiscais

### 4. Features Planejadas (Roadmap)

Funcionalidades futuras a serem implementadas:

- 📦 **Compras**: Pedidos, recebimento, nota fiscal de entrada
- 🛒 **Vendas**: Pedidos, faturamento, nota fiscal de saída
- 🔧 **Serviços**: Ordens de serviço, cronogramas
- 📥 **Entradas**: Movimentação de entrada de mercadorias
- 💳 **Contas a Pagar**: Gestão de passivos
- 💸 **Contas a Receber**: Gestão de recebíveis
- 📊 **Contábil**: Integração com sistemas contábeis externos

---

## 🔌 Padrões de Design Implementados

### 1. Strategy Pattern
Define uma família de algoritmos e permite que um seja selecionado em tempo de execução.

**Uso no ERPAPI:**
- Diferentes estratégias de cálculo de impostos (ICMS vs IPI vs ISS)
- Diferentes estratégias de precificação por tenant
- Diferentes validações por tipo de entidade

### 2. Decorator Pattern
Adiciona responsabilidades a objetos dinamicamente sem alterar suas estruturas.

**Uso no ERPAPI:**
- Logging em torno de operações de serviço
- Métricas de desempenho
- Auditoria de alterações
- Caching de resultados

Permite composição de múltiplos comportamentos sem criar subclasses explodidas.

### 3. Proxy Pattern
Fornece um substituto ou espaço reservado para outro objeto para controlar o acesso.

**Uso no ERPAPI:**
- Roteamento baseado em tenant antes de executar um serviço
- Interceptação de requisições para resolução de contexto
- Aplicação automática de decorators
- Controle de acesso por permissões

---

## 🔄 Fluxo de Ciclo de Vida do Tenant

### Fase 1: Inicialização da Aplicação
1. Spring Boot inicia
2. Master DataSource é criado (banco central)
3. Migrações Flyway do Master são executadas sincronamente
4. Spring Boot fica pronto (ApplicationReadyEvent)

### Fase 2: Carregamento de Tenants Existentes
1. `ApplicationStartupListener` intercepta o evento de ApplicationReady
2. `TenantMigrationStartupWorker` inicia
3. Todos os tenants ativos são carregados do banco master
4. Para cada tenant: um evento é criado e enfileirado
5. Um registro de rastreamento é criado para cada tenant

### Fase 3: Processamento de Fila de Migrações
1. `TenantMigrationQueueConsumer` inicia em thread background
2. Aguarda eventos na fila (BlockingQueue)
3. Para cada evento:
   - Resolve o DataSource do tenant
   - Executa migrações Flyway
   - Executa scripts de seed (dados iniciais)
   - Atualiza o status para COMPLETED
4. Se houver erro: tenta 3 vezes (retry)
5. Se falhar definitivamente: marca como FAILED

### Fase 4: Novo Tenant (Runtime)
1. Requisição POST para criar novo tenant
2. `TenantService` valida e cria o registro
3. Publica `TenantCreatedEvent`
4. `TenantCreationMigrationListener` intercepta o evento
5. Novo evento é enfileirado automaticamente
6. Consumer processa de forma assíncrona
7. Resposta retorna imediatamente (sem bloquear)

---

## 🗄️ Gerenciamento de Banco de Dados e Migrações

### 1. Estratégia de Versionamento com Flyway

**Flyway** é utilizado para controlar versões de schema:

- **Migrações Versionadas** (`V1__*, V2__*, V3__*`): Executadas em ordem, nunca são revertidas
- **Migrações Repetíveis** (`R__*`): Executadas sempre que mudam, úteis para views e procedures

**Estrutura de Migrações:**
```
src/main/resources/db/migration/
├── master/              # Banco central (Master)
│   └── mysql/
│       ├── V1__CREATE_DB_TABLES.sql
│       ├── V2__Insert_Initial_Tenants.sql
│       └── V3__Insert_Tenant_Datasources.sql
├── tenant/              # Para cada tenant
│   └── mysql/
│       ├── V1__CREATE_DB_TABLES.sql
│       ├── V2__Insert_Measure_Units.sql
│       ├── V3__Insert_Addresses.sql
│       └── ... mais V4, V5, etc
├── taxengine/           # Módulo de impostos
│   └── mysql/
│       ├── V1__create_aliquota_tables.sql
│       └── V2__seed_aliquotas_icms.sql
└── observability/       # Logs e eventos
    └── mysql/
        └── V1__Create_Flow_Events_Table.sql
```

### 2. Suporte a Múltiplos Bancos de Dados

O sistema suporta múltiplos SGBDs através de schemas separados por banco:

- ✅ MySQL
- ✅ PostgreSQL
- ✅ SQL Server
- ✅ Oracle
- ✅ H2 (testes)
- ✅ SQLite
- ✅ Firebird
- ✅ MariaDB
- ✅ DB2
- ✅ Derby
- ✅ HSQLDB
- ✅ Informix
- ✅ Sybase
- ✅ Vertica
- ✅ CockroachDB
- ✅ ClickHouse

Cada banco tem seus próprios scripts SQL otimizados para suas características.

### 3. Seed de Dados

Após as migrações Flyway, scripts de seed populam dados iniciais:

- Unidades de Medida padrão
- Endereços iniciais
- Contatos padrão
- Composições de produtos
- Alíquotas de impostos

---

## 📊 Observabilidade e Monitoramento

### 1. Integração com SonarQube

- **Análise estática de código**: Detecção de bugs, vulnerabilidades e code smells
- **Métricas de qualidade**: Cobertura de testes, duplicação de código
- **Histórico de evolução**: Acompanhamento de melhoria contínua

**Configuração:**
```
URL: http://localhost:9000
Project Key: ERPAPI
```

### 2. Logging Estruturado

- Logs detalhados com contexto de tenant
- Rastreamento de fluxos de migração
- Eventos de sucesso e falha registrados
- Métricas de duração e items processados

### 3. Auditoria e Eventos de Flow

- Tabela dedicada para eventos de fluxo
- Rastreamento completo de execuções
- Suporte para extração de relatórios futuros
- Histórico de reprocessamentos

---

## 🔐 Segurança e Controle de Acesso

### 1. Spring Security Integration

- Autenticação via credenciais (username/password)
- Suporte a JWT para APIs stateless
- Controle de acesso baseado em Roles

### 2. Sistema de Permissões

- Permissões granulares atribuíveis a roles
- Validação por endpoint
- Suporte a permissões customizadas

### 3. Isolamento de Dados por Tenant

- Contexto de tenant propagado em ThreadLocal
- Filtros automáticos em queries (row-level security)
- Segregação garantida mesmo com bugs no código

---

## 🚀 Tecnologias Principais

| Componente | Tecnologia | Versão |
|-----------|-----------|--------|
| Framework | Spring Boot | 4.0.1 |
| Linguagem | Java | 21 |
| Build | Maven | 3.6+ |
| ORM | Hibernate/JPA | - |
| Segurança | Spring Security | - |
| Processamento Batch | Spring Batch | - |
| Migrações DB | Flyway | - |
| Documentação API | SpringDoc OpenAPI | 2.3.0 |
| JWT | JJWT | 0.11.5 |
| Lombok | Lombok | - |
| Análise Código | SonarQube | - |

---

## 📋 Checklist de Recursos Implementados

### ✅ Núcleo Multi-Tenant
- [x] Suporte para segregação por coluna (tenantId, tenantGroupIds, scope)
- [x] Roteamento dinâmico de serviços por tenant
- [x] Carregamento automático de DataSources
- [x] Fila unificada de processamento

### ✅ Features Implementadas
- [x] Gestão de Usuários
- [x] Gestão de Produtos
- [x] Gestão de BusinessPartner (Clientes, Fornecedores e outros)
- [x] Gestão de Endereços
- [x] Gestão de Contatos
- [x] TaxEngine (Cálculos de Impostos)
- [x] Custom Fields e Custom Data
- [x] Sistema de Permissões

### ⏳ Features em Desenvolvimento / Planejadas
- [ ] Compras
- [ ] Vendas
- [ ] Serviços
- [ ] Movimento de Estoque (Entradas/Saídas)
- [ ] Contas a Pagar
- [ ] Contas a Receber
- [ ] Integração Contábil

### 🔄 Infraestrutura e Operacional
- [x] Spring Batch para migrações batch
- [x] Flyway para versionamento de schema
- [x] Fila de processamento com retry automático
- [x] Logging estruturado e rastreável
- [x] Suporte a múltiplos bancos de dados
- [ ] Melhorias em observability (em progresso)

---

## 🛣️ Roadmap Futuro

### Curto Prazo (1-2 sprints)
- [ ] Completar features de Compras e Vendas
- [ ] Implementar movimento de estoque com rastreabilidade
- [ ] Melhorar relatórios de observability

### Médio Prazo (2-4 sprints)
- [ ] Integração com sistemas contábeis
- [ ] Dashboard de analytics
- [ ] Melhorias na performance com caching distribuído

### Longo Prazo (4+ sprints)
- [ ] Machine Learning para previsões de demanda
- [ ] Integração EDI com parceiros
- [ ] Conformidade com padrões internacionais (ISO, Sarbanes-Oxley)

---

## 💡 Notas Técnicas para Agentes de IA

### Ao Implementar Novas Features:
1. Seguir a estrutura de pacotes: `v1/main/features/{feature}`
2. Implementar interface padrão de serviço
3. Adicionar suporte a multi-tenant desde o início
4. Criar decorators para logging/métricas
5. Registrar novos serviços no proxy resolver

### Ao Modificar Migrações:
1. Criar nova versão `V{N}__description.sql`
2. Nunca modificar migrations já executadas
3. Testar contra múltiplos bancos de dados
4. Incluir scripts seed quando necessário

### Ao Trabalhar com Dados Compartilhados:
1. Sempre considerar TenantGroupIds
2. Implementar filtros row-level corretos
3. Documentar comportamento esperado por Scope
4. Testar isolamento entre tenants

---

## 📚 Dependências e Versões

Veja `pom.xml` para lista completa de dependências. Principais:
- Spring Boot 4.0.1 com Spring Framework 6.x
- Java 21+ (compilação e runtime)
- Jakarta EE (Java 21+)
- PostgreSQL, MySQL e outros drivers de banco
