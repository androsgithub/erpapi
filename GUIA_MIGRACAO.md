# 🚀 Guia passo-a-passo para Migração

## ⚠️ PRÉ-MIGRAÇÃO

### 1. Backup
```bash
# Fazer backup do projeto
git checkout -b feature/refactor-architecture
git branch -u origin/feature/refactor-architecture
```

### 2. Análise de Dependências
```bash
# Verificar todas as classes que importam paths críticos
grep -r "com.api.erp.v1.config" --include="*.java"
grep -r "com.api.erp.v1.features" --include="*.java"
grep -r "com.api.erp.v1.shared" --include="*.java"
grep -r "com.api.erp.v1.tenant" --include="*.java"
```

### 3. Garantir que Tests Passam
```bash
mvn clean test
# Tudo DEVE passar antes de começar
```

---

## 📋 FASE 1: Estrutura de Diretórios

### Criar nova estrutura (sem mover ainda)

```bash
cd src/main/java/com/api/erp/v1

# Criar principais

mkdir -p config/{aspects,database,security,swagger,web,aop}
mkdir -p features
mkdir -p tenant/infrastructure/config
mkdir -p tenant/infrastructure/datasource/{manual,dynamic}
mkdir -p tenant/infrastructure/interceptor
mkdir -p shared/core/{domain,infrastructure,presentation}
mkdir -p shared/features
mkdir -p docs/{config,api,technical}
mkdir -p observability
```

### Resultado esperado:
```
v1/
├── config/                  ← Novo
├── features/
├── tenant/
├── shared/                  ← Novo  
├── docs/                    ← Novo
└── observability/           ← Já existe
```

---

## 📋 FASE 2: Mover Config (Baixo Risco)

### Passo 1: Identificar arquivos em config
```bash
find src/main/java/com/api/erp/v1/config -type f -name "*.java"
```

Esta pasta JÁ existe, apenas vamos reorganizar internamente:
```
config/
├── aspects/          ← MOVER daqui para cá
├── database/         ← MOVER
├── security/         ← CRIAR
├── swagger/          ← MOVER
├── web/              ← MOVER
└── aop/              ← CRIAR
```

### Passo 2: Mover classes de aspects

**Ação**: Ir pasta `config/aspects/` e garantir que contains:
```
- AOP principal classes
- LoggingAspect
- TransactionAspect
- etc
```

**Atualizar imports em toda codebase**:
```bash
find src -name "*.java" -type f -exec sed -i 's|com\.api\.erp\.v1\.config\.aspects|com.api.erp.v1.config.aspects|g' {} \;
```

### Passo 3: Testar
```bash
mvn clean compile
# SEM ERROS?
mvn clean test
# TESTS PASSAM?
```

---

## 📋 FASE 3: Mover Features Incrementalmente

### ⚠️ CRÍTICO: Order matters!

Features compartilhadas e sem dependências devem ser movidas primeiro:
1. **unidademedida** (base)
2. **contato** (compartilhado)
3. **endereco** (compartilhado)
4. **permissao** (compartilhado)
5. **cliente** (usa outros)
6. **fornecedor** (usa outros)
7. **produto** (usa outros)
8. **personalizado**
9. **customfield**
10. **usuario** (core)

### Exemplo: Mover UNIDADEMEDIDA

```bash
# 1. Verificar imports de unidademedida
grep -r "com.api.erp.v1.features.unidademedida"

# 2. Se compartilhada, mover para shared/features/, senão ficar em features/
# A decisão: "É usada por múltiplas features?"
#   SIM → shared/features/unidademedida
#   NÃO → features/unidademedida

# 3. Assumindo vai para shared/features/:
# mv src/main/java/com/api/erp/v1/features/unidademedida \
#    src/main/java/com/api/erp/v1/shared/features/unidademedida

# 4. Atualizar todos os imports
find src -name "*.java" -type f -exec sed -i \
  's|com\.api\.erp\.v1\.features\.unidademedida|com.api.erp.v1.shared.features.unidademedida|g' {} \;

# 5. IMPORTANTE: Atualizar FeaturesRepositoriesConfig
# Remover: "com.api.erp.v1.features.unidademedida.domain.repository"
# Adicionar: "com.api.erp.v1.shared.features.unidademedida.domain.repository"

# 6. Testar
mvn clean compile -DskipTests
mvn clean test -Dtest=UnidadeMedidaTest
# OU testar feature inteira
```

### Exemplo: Mover CLIENTE (com customizações)

```bash
# 1. Verificar imports
grep -r "com.api.erp.v1.features.cliente"

# 2. CLIENTE fica em features/ (não é compartilhada)
# Apenas move internamente:
# src/main/java/com/api/erp/v1/features/cliente/
# Estrutura interna já está OK, não muda path pai

# 3. Movimentação interna (exemplo):
mv src/main/java/com/api/erp/v1/features/cliente/config \
   src/main/java/com/api/erp/v1/features/cliente/config
# (Ya está lá, verificar apenas)

# 4. Imports já corretos
# FeaturesRepositoriesConfig já aponta para correto

# 5. Testar
mvn clean test -Dtest=ClienteTest
```

### Template para cada feature:

```bash
#!/bin/bash
FEATURE=$1  # ex: unidademedida
IS_SHARED=$2  # true/false

# Detectar localizacao futura
if [ "$IS_SHARED" = "true" ]; then
  NEW_PATH="src/main/java/com/api/erp/v1/shared/features/$FEATURE"
  OLD_PACKAGE="com.api.erp.v1.features.$FEATURE"
  NEW_PACKAGE="com.api.erp.v1.shared.features.$FEATURE"
else
  NEW_PATH="src/main/java/com/api/erp/v1/features/$FEATURE"
  OLD_PACKAGE=$NEW_PACKAGE # Sem mudança no path
  NEW_PACKAGE=$OLD_PACKAGE
fi

echo "Moving $FEATURE from $OLD_PACKAGE to $NEW_PACKAGE"

# Atualizar imports
find src -name "*.java" -type f -exec sed -i \
  "s/$OLD_PACKAGE/$NEW_PACKAGE/g" {} \;

# Atualizar FeaturesRepositoriesConfig
sed -i "s|$OLD_PACKAGE\.domain\.repository|$NEW_PACKAGE.domain.repository|g" \
  src/main/java/com/api/erp/v1/tenant/infrastructure/config/datasource/manual/FeaturesRepositoriesConfig.java

# Testar
mvn clean compile -DskipTests

echo "✅ $FEATURE migrado com sucesso!"
```

---

## 📋 FASE 4: Mover e Reorganizar Tenant

### Checar estrutura existente:
```bash
ls -la src/main/java/com/api/erp/v1/tenant/infrastructure/config/datasource/
# Deve ter:
# - TenantsMasterRepositoriesConfig.java ⚠️ NÃO MUDANÇA
# - FeaturesRepositoriesConfig.java ⚠️ JÁ ATUALIZADO
# - TenantsMasterDatabaseConfig.java
# - TenantsConfiguration.java
# - TenantsMultitenantDataSourceConfig.java
```

### Subdivisão:
```bash
mkdir -p src/main/java/com/api/erp/v1/tenant/infrastructure/datasource/manual
mkdir -p src/main/java/com/api/erp/v1/tenant/infrastructure/datasource/dynamic
mkdir -p src/main/java/com/api/erp/v1/tenant/infrastructure/interceptor
```

### Mover datasource configs:
```bash
# Apenas se não estiverem no lugar certo. Checkar antes!
# mv TenantsMasterRepositoriesConfig para config/
# mv FeaturesRepositoriesConfig para config/
# mv TenantResolutionStrategy para datasource/manual/
# mv MultiTenantRoutingDataSource para datasource/manual/
# mv TenantContextHolder para datasource/manual/
```

### ⚠️ VALIDAR PATHS:
```java
// TenantsMasterRepositoriesConfig MUST point to:
@EnableJpaRepositories(
    basePackages = {"com.api.erp.v1.tenant.domain.repository"}, // ✅ SEM MUDANÇA
    // ...
)

// FeaturesRepositoriesConfig MUST point to:
@EnableJpaRepositories(
    basePackages = {
        "com.api.erp.v1.features.*.domain.repository",
        "com.api.erp.v1.shared.features.*.domain.repository",  // NOVO
        // ...
    },
    // ...
)
```

---

## 📋 FASE 5: Criar Shared/Core

### Mover código realmente compartilhado:

```bash
mkdir -p src/main/java/com/api/erp/v1/shared/core/{domain,infrastructure,presentation}
```

### Quais arquivos:
```bash
# Domain/Exceptions
find src -name "*Exception.java" -path "*/shared/*" -o -name "*Event.java" -path "*/shared/*"

# Move para shared/core/domain/
# Ex:
mv src/main/.../shared/domain/exceptions/* \
   src/main/java/com/api/erp/v1/shared/core/domain/exceptions/

mv src/main/.../shared/domain/events/* \
   src/main/java/com/api/erp/v1/shared/core/domain/events/
```

### Infrastructure/Persistence:
```bash
# BaseRepository, Specifications, etc
find src -path "*/shared/infrastructure/*" -name "*.java"

# Move para shared/core/infrastructure/
```

### Presentation:
```bash
# APIResponse, BaseController, etc
find src -path "*/shared/presentation/*" -name "*.java"

# Move para shared/core/presentation/
```

---

## 📋 FASE 6: Organizar Observability

### Já existe, apenas garantir:
```bash
ls -la src/main/java/com/api/erp/v1/observability/
# Deve ter: application, config, domain, presentation, strategy

# Se não tiver, criar:
mkdir -p src/main/java/com/api/erp/v1/observability/{application,config,domain,presentation,strategy}
```

---

## ✅ FASE 7: Validação

### Passo 1: Compilação
```bash
mvn clean compile
# SEM ERROS DE COMPILAÇÃO
```

### Passo 2: Testes Unitários
```bash
mvn clean test

# Se houver falhas de imports:
# grep -r "cannot find symbol" target/surefire-reports/
```

### Passo 3: Startup da Aplicação
```bash
# Iniciar aplicação localmente
mvn spring-boot:run

# Verificar logs:
# - @SpringBootApplication scanned correctly
# - @EnableJpaRepositories executado
# - Aspects registered
# - Flyway migrations ran
```

### Passo 4: Test endpoints
```bash
# Testar um endpoint de cada feature:
curl http://localhost:8080/api/v1/clientes

# Resposta esperada: 200 ou 401 (se requer auth), não 404 ou erro de bean
```

### Passo 5: Test database
```bash
# Verificar que Flyway rodou:
# - Check flyway_schema_history table
# - Verificar que TB_TENANT foi criado

# Query:
# SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES;
```

### Passo 6: Test Aspects
```bash
# Verificar que Aspect está interceptando:
# - Look for logging statements
# - @EnableAspectJAutoProxy deve estar ativa

# Log esperado ao fazer request:
# [com.api.erp.v1.config.aspects.LoggingAspect] Starting method execution...
```

### Passo 7: Test Multi-tenant
```bash
# Se tiver teste multi-tenant, rodar:
mvn clean test -Dtest=MultitenantIntegrationTest

# Verificar que TenantContextHolder funciona
# Verificar que MultiTenantRoutingDataSource roteia corretamente
```

---

## 🐛 Troubleshooting

### ❌ Erro: cannot find symbol - class XYZ
```
Causa: Arquivo não foi movido OU import não foi atualizado
Solução: 
1. Encontrar classe: find src -name "XYZ.java"
2. Verificar novo path
3. Atualizar import em quem usa
```

### ❌ Erro: No bean named 'entityManagerFactory'
```
Causa: @EnableJpaRepositories apontando para path errado
Solução:
1. Validar que TenantsMasterRepositoriesConfig existe e está @Configuration
2. Validar que FeaturesRepositoriesConfig existe e está @Configuration
3. Rodar: mvn clean compile -DskipTests
```

### ❌ Erro: Aspect not intercepting
```
Causa: @EnableAspectJAutoProxy não está ativa OU classe não está em path scaneado
Solução:
1. Verificar ErpApplication.java tem @EnableAspectJAutoProxy
2. Verificar Aspect está em com.api.erp.v1.config.aspects.*
3. Verificar classe sendo interceptada tem @Component/@Service
```

### ❌ Erro: Flyway validation failed
```
Causa: Schema mudou OU migration history inconsistente
Solução:
1. Verificar aplicação.yml:
   flyway:
       validate-on-migrate: false  # Temporariamente desativar
2. Rodar: mvn spring-boot:run
3. Re-ativar após checar
```

---

## 📦 Commits Recomendados

```bash
# Commit por fase:

git add src/main/java/com/api/erp/v1/config/
git commit -m "refactor: organize config into subfolders (aspects, database, security, swagger, web)"

git add src/main/java/com/api/erp/v1/features/
git commit -m "refactor: reorganize features - phase 1"

git add src/main/java/com/api/erp/v1/shared/
git commit -m "refactor: create shared core and features structure"

git add src/main/java/com/api/erp/v1/tenant/
git commit -m "refactor: reorganize tenant module (config, datasource, interceptor)"

git add ARQUITETURA_PROPOSTA.md EXEMPLOS_FEATURES.md
git commit -m "docs: add new architecture proposal and examples"

# Final
git commit -m "refactor: complete architecture reorganization (DDD/Clean Architecture)"
git push origin feature/refactor-architecture
```

---

## 🎯 Checklist Final

- ☐ Todos tests passam localmente
- ☐ Aplicação inicia sem erros
- ☐ Endpoints respondem corretamente
- ☐ Aspects estão funcionando
- ☐ Flyway rodou correctamente
- ☐ Multi-tenant funcionando
- ☐ @EnableJpaRepositories escaneia corretamente
- ☐ Swagger/OpenAPI funcionando
- ☐ Observability logs estão sendo gravados
- ☐ Nenhuma regressão em features existentes
- ☐ Documentação atualizada
- ☐ Code review aprovado
- ☐ PR merged para main

---

## 🚀 Próximas Fases (Pós-Migração)

### Fase 8: Otimizações
- Lazy-loading de features
- Module scanning ao invés de @ComponentScan explícito
- Feature toggles

### Fase 9: CI/CD
- Updates no pipeline (se houver)
- Validações de paths no build
- Tests de arquitetura

### Fase 10: Documentação
- README atualizado
- Wiki com padrões
- ADR (Architecture Decision Records)

