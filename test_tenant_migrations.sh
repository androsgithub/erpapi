#!/bin/bash

# ==================================================================================
# TESTE RÁPIDO: Flyway Tenant Migrations
# ==================================================================================
# Este script testa se as migrações de tenant estão funcionando corretamente
# via Flyway quando a aplicação inicia.
#
# Pré-requisitos:
# - MySQL rodando
# - Master database (erpapi) com table tenant_datasource configurada
# - Tenants ativos em tb_tenant
# - Aplicação Spring Boot buildada
#
# Execução:
# bash test_tenant_migrations.sh
# ==================================================================================

set -e

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║   TESTE: Flyway Tenant Migrations                              ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ==================================================================================
# PARTE 1: Verificar Pré-requisitos
# ==================================================================================
echo -e "${BLUE}[1] Verificando Pré-requisitos...${NC}"
echo ""

# Verificar MySQL
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}❌ MySQL não encontrado. Instale MySQL.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ MySQL disponível${NC}"

# Verificar Master Database
if ! mysql -u root -p12345 -e "USE erpapi;" &> /dev/null; then
    echo -e "${RED}❌ Master database (erpapi) não encontrado ou credenciais inválidas.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Master database (erpapi) acessível${NC}"

# Verificar tabela tenant_datasource
if ! mysql -u root -p12345 erpapi -e "SELECT COUNT(*) FROM tenant_datasource;" &> /dev/null; then
    echo -e "${RED}❌ Tabela tenant_datasource não existe.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Tabela tenant_datasource existe${NC}"

# ==================================================================================
# PARTE 2: Verificar Tenants Configurados
# ==================================================================================
echo ""
echo -e "${BLUE}[2] Verificando Tenants Configurados...${NC}"
echo ""

TENANT_COUNT=$(mysql -u root -p12345 erpapi -N -e "SELECT COUNT(*) FROM tb_tenant WHERE ativa = 1;")
echo -e "${GREEN}✓ Encontrados $TENANT_COUNT tenants ativos${NC}"

# Mostrar detalhes dos tenants
echo ""
echo "Tenants e seus Datasources:"
echo "─────────────────────────────────────────────────"
mysql -u root -p12345 erpapi -e "
SELECT 
    CONCAT('ID: ', t.id, ' | Nome: ', t.nome) as 'Tenant',
    CONCAT(td.host, ':', td.port, '/', td.database_name) as 'Database'
FROM tb_tenant t
LEFT JOIN tenant_datasource td ON t.id = td.tenant_id
WHERE t.ativa = 1
ORDER BY t.id;
" | grep -v Tenant

echo "─────────────────────────────────────────────────"

# ==================================================================================
# PARTE 3: Verificar Migrações Antes de Iniciar Aplicação
# ==================================================================================
echo ""
echo -e "${BLUE}[3] Estado das Migrações ANTES de iniciar aplicação...${NC}"
echo ""

echo "📊 Master Database (erpapi):"
MASTER_MIGRATIONS=$(mysql -u root -p12345 erpapi -N -e "SELECT COUNT(*) FROM flyway_schema_history;" 2>/dev/null || echo "0")
echo "   Migrações executadas: $MASTER_MIGRATIONS"

echo ""
echo "📊 Tenant Databases:"

# Verificar cada banco de tenant
TENANT_DBS=$(mysql -u root -p12345 erpapi -N -e "SELECT DISTINCT database_name FROM tenant_datasource;")

for DB in $TENANT_DBS; do
    if mysql -u root -p12345 -e "USE $DB;" &> /dev/null; then
        MIGRATIONS=$(mysql -u root -p12345 "$DB" -N -e "SELECT COUNT(*) FROM flyway_schema_history;" 2>/dev/null || echo "0")
        echo "   $DB: $MIGRATIONS migrações"
    fi
done

# ==================================================================================
# PARTE 4: Instruções para Iniciar a Aplicação
# ==================================================================================
echo ""
echo -e "${YELLOW}[4] PRÓXIMO PASSO: Iniciar a Aplicação${NC}"
echo ""
echo "Execute o comando abaixo para iniciar a aplicação:"
echo ""
echo "  mvn spring-boot:run"
echo ""
echo "A aplicação irá:"
echo "  1. Executar migrações do master (FlywayConfig)"
echo "  2. Aguardar initialização completa"
echo "  3. ApplicationStartupListener dispara"
echo "  4. TenantMigrationService.migrateAllTenants()"
echo "  5. Para cada tenant:"
echo "     - Busca datasource configurado"
echo "     - Cria HikariDataSource"
echo "     - Executa Flyway (db/migration/tenant/)"
echo "     - Fecha conexão"
echo ""

# ==================================================================================
# PARTE 5: Instruções Pós-Startup
# ==================================================================================
echo -e "${YELLOW}[5] DEPOIS DE INICIAR: Verificar Resultados${NC}"
echo ""
echo "Aguarde a aplicação iniciar completamente e então execute:"
echo ""
echo "  bash verify_tenant_migrations.sh"
echo ""
echo "Este script verificará se as migrações foram executadas com sucesso"
echo "em TODOS os bancos de tenant."
echo ""

# ==================================================================================
# PARTE 6: Limpeza (Opcional)
# ==================================================================================
echo -e "${YELLOW}[6] (OPCIONAL) Resetar Migrações para Testar Novamente${NC}"
echo ""
echo "Se precisar resetar as migrações para testar novamente:"
echo ""
echo "  # Remove histórico de migrações (Master)"
echo "  mysql -u root -p12345 erpapi -e 'DELETE FROM flyway_schema_history;'"
echo ""
echo "  # Remove histórico de migrações (Tenants)"
echo "  mysql -u root -p12345 tenant1_db -e 'DELETE FROM flyway_schema_history;'"
echo "  mysql -u root -p12345 shared_db -e 'DELETE FROM flyway_schema_history;'"
echo ""
echo "  # Depois reinicie a aplicação"
echo ""

echo ""
echo -e "${GREEN}✅ Verificação de Pré-requisitos Concluída!${NC}"
echo ""
