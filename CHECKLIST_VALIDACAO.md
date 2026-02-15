# ✅ CHECKLIST DE VALIDAÇÃO - Migração de Arquitetura

## 📋 PRÉ-MIGRAÇÃO

### Preparação do Ambiente
- [ ] Branch criado: `feature/refactor-architecture`
- [ ] Todos os testes passam no estado atual
- [ ] Backup/Snapshot do projeto feito
- [ ] Equipe entende a proposta (reviews feitos)
- [ ] Scripts de migração preparados
- [ ] 1 dev designado como responsável

### Análise de Dependências
- [ ] Mapeadas todas as importações críticas de `config/`
- [ ] Mapeadas todas as importações de `features/`
- [ ] Mapeadas todas as importações de `shared/`
- [ ] Documentadas customizações por tenant
- [ ] Identificadas todas as classes com imports circulares (se houver)

### Validação de Configurações
- [ ] ErpApplication.java revisada (sem mudanças esperadas)
- [ ] TenantsMasterRepositoriesConfig revisado (path preservado)
- [ ] FeaturesRepositoriesConfig revisado (lista de basePackages mapeada)
- [ ] application.yml Flyway locations verificadas
- [ ] Aspects identificados e localizados

---

## 📁 FASE 1: Criar Estrutura (Dia 1)

### Criar diretórios principais
- [ ] `config/aspects/` criado
- [ ] `config/database/` criado
- [ ] `config/security/` criado
- [ ] `config/swagger/` criado
- [ ] `config/web/` criado
- [ ] `config/aop/` criado
- [ ] `tenant/infrastructure/config/` criado
- [ ] `tenant/infrastructure/datasource/{manual,dynamic}/` criado
- [ ] `tenant/infrastructure/interceptor/` criado
- [ ] `shared/core/{domain,infrastructure,presentation}/` criado
- [ ] `shared/features/` criado
- [ ] `docs/{config,api,technical}/` criado

### Validação da estrutura
```bash
find src/main/java/com/api/erp/v1 -type d -maxdepth 3 | wc -l
# Deve ter aumentado significativamente
```
- [ ] Número de diretórios aumentou conforme esperado
- [ ] Nenhum erro ao compilar (ainda sem mover classes)

---

## 📋 FASE 2: Mover CONFIG (Dia 1-2)

### Organizar config/aspects
- [ ] Identificadas todas as classes em config/aspects/
- [ ] LoggingAspect.java → config/aspects/
- [ ] TransactionAspect.java → config/aspects/
- [ ] PerformanceAspect.java → config/aspects/ (se existir)
- [ ] SecurityAspect.java → config/aspects/ (se existir)
- [ ] Todos @Component/@Configuration preservados

### Organizar config/database
- [ ] DataSourceConfig.java verificado
- [ ] JpaConfig.java verificado
- [ ] HibernateConfig.java → config/database/ (se existir)
- [ ] Nenhuma mudança de funcionalidade

### Organizar config/security
- [ ] SecurityConfig.java → config/security/
- [ ] JwtTokenProvider.java → config/security/ (se existir)
- [ ] AuthenticationFilter.java → config/security/
- [ ] UserDetailsServiceImpl.java → config/security/

### Organizar config/swagger
- [ ] SwaggerConfig.java verificado e movido
- [ ] OpenAPIConfig.java → config/swagger/ (se existir)
- [ ] Endpoints /swagger-ui continuam funcionando

### Organizar config/web
- [ ] WebConfig.java → config/web/
- [ ] CorsConfig.java → config/web/
- [ ] ExceptionHandler.java → config/web/
- [ ] GlobalExceptionHandler.java → config/web/ (se existir)

### Validação Phase 2
```bash
mvn clean compile -DskipTests
mvn clean test
# Todos testes DEVEM passar
```
- [ ] Compilação sem erros
- [ ] Tests unitários passam
- [ ] Nenhuma regressão reportada

---

## 📋 FASE 3: Mover FEATURES (Dias 2-5)

### ORDER de migração (menos dependências primeiro)

#### Feature 1: UNIDADEMEDIDA
- [ ] Identificar: é compartilhada? → SIM, mover para `shared/features/`
- [ ] Mover: `features/unidademedida/` → `shared/features/unidademedida/`
- [ ] Atualizar imports em todos os .java que usam
- [ ] Atualizar FeaturesRepositoriesConfig (remover antigo, adicionar novo path)
- [ ] Test: `mvn clean compile -DskipTests && mvn test -Dtest=UnidadeMedidaTest`
- [ ] Validar: Nenhum erro, testes passam

#### Feature 2: CONTATO
- [ ] Identificar: é compartilhada? → SIM, mover para `shared/features/`
- [ ] Mover estrutura interna (application, domain, infrastructure, presentation)
- [ ] Atualizar imports
- [ ] Atualizar FeaturesRepositoriesConfig
- [ ] Test: `mvn clean test -Dtest=ContatoTest`
- [ ] Validar: ✅

#### Feature 3: ENDERECO
- [ ] Identificar: é compartilhada? → SIM, mover para `shared/features/`
- [ ] Mover estrutura
- [ ] Atualizar imports (usado por cliente, fornecedor)
- [ ] Atualizar FeaturesRepositoriesConfig
- [ ] Test: `mvn clean test`
- [ ] Validar: ✅

#### Feature 4: PERMISSAO
- [ ] Identificar: é compartilhada? → SIM, mover para `shared/features/`
- [ ] Mover estrutura
- [ ] Atualizar imports
- [ ] Atualizar FeaturesRepositoriesConfig
- [ ] Test: `mvn clean test`
- [ ] Validar: ✅

#### Feature 5: CLIENTE
- [ ] Identificar: é compartilhada? → NÃO, fica em `features/cliente`
- [ ] Reorganizar internamente (config, application, domain, infrastructure, presentation)
- [ ] Mover tenants/hece/ para correto local
- [ ] Atualizar imports se necessário
- [ ] FeaturesRepositoriesConfig: sem mudança (já aponta correto)
- [ ] Test: `mvn clean test -Dtest=ClienteTest`
- [ ] Validar: ✅

#### Feature 6: FORNECEDOR
- [ ] Identificar: é compartilhada? → NÃO
- [ ] Reorganizar internamente
- [ ] Que usa: Endereco (da shared), Contato (da shared)
- [ ] Atualizar imports
- [ ] Test: `mvn clean test`
- [ ] Validar: ✅

#### Feature 7: PRODUTO
- [ ] Identificar: é compartilhada? → NÃO
- [ ] Reorganizar internamente
- [ ] Pode usar: UnidadeMedida (shared)
- [ ] Atualizar imports
- [ ] Test: `mvn clean test`
- [ ] Validar: ✅

#### Feature 8: USUARIO
- [ ] Identificar: é compartilhada? → NÃO (ou parcialmente)
- [ ] Reorganizar internamente
- [ ] Pode usar: Permissao, Contato, Endereco (shared)
- [ ] Atualizar imports
- [ ] Test: `mvn clean test`
- [ ] Validar: ✅

#### Feature 9: CUSTOMFIELD
- [ ] Identificar: é compartilhada? → NÃO
- [ ] Reorganizar internamente
- [ ] Atualizar imports
- [ ] Test: `mvn clean test`
- [ ] Validar: ✅

#### Feature 10: PERSONALIZADO
- [ ] Identificar: é compartilhada? → NÃO
- [ ] Reorganizar internamente
- [ ] Atualizar imports
- [ ] Test: `mvn clean test`
- [ ] Validar: ✅

### Validação Phase 3
```bash
mvn clean test
# Todos testes DEVEM passar
```
- [ ] 100% de tests passam
- [ ] Nenhuma feature quebrada
- [ ] Endpoints continuam funcionando
- [ ] Swagger/OpenAPI mostrando todas as features

---

## 📋 FASE 4: Reorganizar SHARED/CORE (Dia 5-6)

### Mover para shared/core/domain
- [ ] Todas as exceptions para `shared/core/domain/exceptions/`
  - [ ] BusinessException.java
  - [ ] NotFoundException.java
  - [ ] ValidationException.java
  - [ ] [outras exceptions...]
- [ ] Domain Events para `shared/core/domain/events/`
  - [ ] DomainEvent.java
  - [ ] EventPublisher.java
  - [ ] [eventos...]
- [ ] Value Objects para `shared/core/domain/value-objects/`
  - [ ] Money.java
  - [ ] Email.java
  - [ ] CPF.java
  - [ ] [outros...]
- [ ] Base classes para `shared/core/domain/base/`
  - [ ] BaseEntity.java
  - [ ] BaseRepository.java
  - [ ] BaseDomainService.java

### Mover para shared/core/infrastructure
- [ ] BaseRepositoryImpl → `shared/core/infrastructure/persistence/`
- [ ] SpecificationBase → `shared/core/infrastructure/persistence/`
- [ ] UserContext → `shared/core/infrastructure/context/`
- [ ] ApplicationEventPublisher → `shared/core/infrastructure/event/`
- [ ] Utils → `shared/core/infrastructure/utils/`

### Mover para shared/core/presentation
- [ ] APIResponse → `shared/core/presentation/response/`
- [ ] ErrorResponse → `shared/core/presentation/response/`
- [ ] PaginatedResponse → `shared/core/presentation/response/`
- [ ] BaseController → `shared/core/presentation/controller/`

### Atualizar todos os imports
```bash
find src -name "*.java" -type f -exec grep -l "shared.domain.exceptions" {} \;
# Para CADA arquivo, atualizar para novo path
```
- [ ] Verificado todos os imports
- [ ] Nenhum import quebrado

### Validação Phase 4
```bash
mvn clean compile -DskipTests
mvn clean test
```
- [ ] Compilação sucesso
- [ ] Tests passam
- [ ] Exceptions continuam sendo lançadas corretamente

---

## 📋 FASE 5: Reorganizar TENANT (Dia 6-7)

### Validar configurações críticas
- [ ] TenantsMasterRepositoriesConfig path preservado
  ```java
  basePackages = {"com.api.erp.v1.tenant.domain.repository"}  // ✅ SEM MUDANÇA
  ```
- [ ] FeaturesRepositoriesConfig atualizado com todos os paths
  ```java
  basePackages = {
      "com.api.erp.v1.features.cliente.domain.repository",
      "com.api.erp.v1.shared.features.endereco.domain.repository",  // ✅ NOVO
      // ... todos os outros
  }
  ```

### Reorganizar directories (se necessário)
- [ ] Datasource configs em `infrastructure/config/`
- [ ] Datasource routines em `infrastructure/datasource/{manual,dynamic}/`
- [ ] Interceptors em `infrastructure/interceptor/`

### Nenhuma lógica deve ser alterada
- [ ] TenantContextHolder funciona igual
- [ ] MultiTenantRoutingDataSource roteia corretamente
- [ ] Master database acessa TB_TENANT
- [ ] Tenant databases acessam features

### Validação Phase 5
```bash
mvn spring-boot:run &
# Aguardar startup
# Verificar:
# - Datasources criados
# - Tenant detection funcionando
# - MultiTenantRoutingDataSource ativo
```
- [ ] Aplicação inicia sem erros
- [ ] Logs mostram DataSources inicializados
- [ ] TenantContextHolder funciona

---

## 📋 FASE 6: Criar DOCS (Dia 7)

### Criar estrutura
- [ ] `docs/config/` com OpenAPIConfiguration
- [ ] `docs/api/` com documentação de cada feature
- [ ] `docs/technical/` com diagrama arquitetura

### Documentação
- [ ] Swagger/OpenAPI funcionando
- [ ] /swagger-ui.html acessível
- [ ] Todas as features listadas
- [ ] Nenhuma entidade/controller faltando

### Validação
- [ ] `curl http://localhost:8080/v3/api-docs` retorna JSON válido
- [ ] `http://localhost:8080/swagger-ui.html` carrega

---

## 📋 FASE 7: VALIDAÇÃO COMPLETA (Dia 7-8)

### Compilação
- [ ] `mvn clean compile` sucesso
- [ ] Nenhuma warning importante
- [ ] Bytecode gerado sem problemas

### Tests Unitários
```bash
mvn clean test
```
- [ ] 100% dos testes passam
- [ ] Cobertura não diminuiu
- [ ] Nenhuma flakiness novamente

### Startup da Aplicação
```bash
mvn spring-boot:run
```
- [ ] Aplicação inicia sem erros
- [ ] Todos os beans criados
- [ ] Aspects registrados
- [ ] DataSources inicializados
- [ ] Flyway rodeu

### Validar Aspects
```bash
# Fazer uma requisição e verificar logs
curl http://localhost:8080/api/v1/clientes

# Verificar nos logs:
# [com.api.erp.v1.config.aspects.LoggingAspect] Starting method execution...
```
- [ ] Logging Aspect interceptando
- [ ] Transaction Aspect funcionando
- [ ] Performance Aspect (se houver) funcionando
- [ ] Security Aspect funcionando

### Validar Banco de Dados
```sql
SELECT * FROM flyway_schema_history;
```
- [ ] Todas as migrations executadas
- [ ] Nenhuma falha de migração
- [ ] TB_TENANT existe e tem dados

### Validar Multi-Tenancy
```bash
# Se houver testes multitenant
mvn test -Dtest=MultiTenantIntegrationTest
```
- [ ] TenantContextHolder resolve tenant
- [ ] Request routing funciona
- [ ] Tenant 1 acessa seu banco
- [ ] Tenant 2 acessa seu banco
- [ ] Master database separado

### Validar Swagger
```
http://localhost:8080/swagger-ui.html
```
- [ ] Todas as features aparecem
- [ ] Endpoints estão corretos
- [ ] DTOs mostram corretamente
- [ ] Filter by tag funciona

### Testar Cada Feature
- [ ] Cliente: GET/POST/PUT endpoints funcionam
- [ ] Produto: endpoints funcionam
- [ ] Fornecedor: endpoints funcionam
- [ ] Usuario: endpoints funcionam
- [ ] [validar cada uma...]

### Validar Flyway
- [ ] Migrations continuam rodando
- [ ] Nenhuma migration quebrada
- [ ] Schema continua intacto
- [ ] Dados não foram perdidos

### Performance
- [ ] Startup time não aumentou significativamente
- [ ] Respostas continuam com latência similar
- [ ] Memory footprint similar

---

## 🔄 FASE 8: Code Review & Cleanup (Dia 8-9)

### Code Review
- [ ] Todos os PRs revisados por 2+ pessoas
- [ ] Nenhuma dependency invertida
- [ ] Nenhuma circular dependency
- [ ] Imports organizados (@Autowired antes de fields)
- [ ] Nenhuma classe grande (respeitando Single Responsibility)

### Cleanup
- [ ] Remover commented-out code
- [ ] Remover imports não usados
```bash
mvn enforce:enforce -Dorg.codehaus.mojo.enforcer.ignoreMissingDeps=true
```
- [ ] Aplicar formatter
```bash
mvn spotless:apply
```
- [ ] Atualizar IDE indexes
  - [ ] IntelliJ: File → Invalidate Caches
  - [ ] VS Code: Reload Window

### Documentation
- [ ] README.md atualizado com nova arquitetura
- [ ] Toda documentação no projeto atualizada
- [ ] Links internos funcionando
- [ ] ADRs criados (Architecture Decision Records)

---

## ✅ FASE 9: MERGE & DEPLOY (Dia 9-10)

### Preparação para Merge
- [ ] Branch atualizado com main
- [ ] Nenhum conflito
- [ ] Última validação de tests
- [ ] Performance checked

### Merge
- [ ] PR mergido após aprovação
- [ ] Branch deletado
- [ ] Tag criada: `v{version}-refactor-architecture`

### Pós-Deployu
- [ ] Produção verificada
- [ ] Logs não mostram erros
- [ ] Alertas de performance normais
- [ ] Usuários não reportando problemas

---

## 📊 SUMMARY FINAL

### Métricas de Sucesso
- [ ] 0 commits "revert"
- [ ] 100% de tests passando
- [ ] 0 production issues relacionadas
- [ ] Tempo de startup: +/- 5% do original
- [ ] Time aderiu aos novos padrões

### Documentação Entregue
- [ ] RESUMO_EXECUTIVO.md ✅
- [ ] ARQUITETURA_PROPOSTA.md ✅
- [ ] EXEMPLOS_FEATURES.md ✅
- [ ] GUIA_MIGRACAO.md ✅
- [ ] ESTRUTURA_VISUAL.md ✅
- [ ] README.md atualizado ✅
- [ ] ADRs criados ✅

### Treinamento da Equipe
- [ ] Todos entendem nova estrutura
- [ ] Padrões de feature documentados
- [ ] Template disponível para novos features
- [ ] Code reviews aplicando novos padrões

### Próximas Ações
- [ ] Criar issue template para novas features
- [ ] Adicionar validação no CI/CD (arquitetura)
- [ ] Configurar pre-commit hooks
- [ ] Planejar otimizações (lazy loading, module scanning)

---

## 🎯 Assinatura de Conclusão

```
Data de Conclusão: _______________
Responsável: _______________
Aprovado por: _______________
Observations: _____________________________________
```

