#!/bin/bash

# ==================================================================================
# VERIFICAÇÃO: Flyway Tenant Migrations - Pós Startup
# ==================================================================================
# Este script verifica se as migrações de tenant foram executadas com sucesso
# após a inicialização da aplicação.
#
# Execução (APÓS aplicação ter iniciado):
# bash verify_tenant_migrations.sh
# ==================================================================================

set -e

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║   VERIFICAÇÃO: Flyway Tenant Migrations                        ║"
echo "║   (Execute APÓS iniciar a aplicação)                           ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

FAILED=0
PASSED=0

# ==================================================================================
# PARTE 1: Verificar Master Database
# ==================================================================================
echo -e "${BLUE}[1] Verificando Master Database (erpapi)${NC}"
echo ""

MASTER_MIGRATIONS=$(mysql -u root -p12345 erpapi -N -e "SELECT COUNT(*) FROM flyway_schema_history;")
echo "Migrações do Master: $MASTER_MIGRATIONS"

if [ "$MASTER_MIGRATIONS" -gt 0 ]; then
    echo -e "${GREEN}✓ Master foi migrado com sucesso${NC}"
    ((PASSED++))
    
    echo ""
    echo "Histórico de migrações do Master:"
    mysql -u root -p12345 erpapi -e "
    SELECT version, description, installed_on 
    FROM flyway_schema_history 
    ORDER BY version;
    "
else
    echo -e "${RED}✗ Master não foi migrado!${NC}"
    ((FAILED++))
fi

# ==================================================================================
# PARTE 2: Verificar Tenants
# ==================================================================================
echo ""
echo -e "${BLUE}[2] Verificando Tenant Databases${NC}"
echo ""

TENANT_DBS=$(mysql -u root -p12345 erpapi -N -e "SELECT DISTINCT database_name FROM tenant_datasource;")

TOTAL_TENANTS=0
MIGRATED_TENANTS=0

for DB in $TENANT_DBS; do
    ((TOTAL_TENANTS++))
    
    if mysql -u root -p12345 -e "USE $DB;" &> /dev/null; then
        echo "Database: $DB"
        
        # Verificar se tabela existe
        if mysql -u root -p12345 "$DB" -e "SELECT COUNT(*) FROM flyway_schema_history;" &> /dev/null; then
            MIGRATIONS=$(mysql -u root -p12345 "$DB" -N -e "SELECT COUNT(*) FROM flyway_schema_history;")
            
            if [ "$MIGRATIONS" -gt 0 ]; then
                echo -e "${GREEN}  ✓ $MIGRATIONS migrações executadas${NC}"
                ((MIGRATED_TENANTS++))
                ((PASSED++))
                
                # Mostrar migrações
                echo "  Migrações:"
                mysql -u root -p12345 "$DB" -e "
                SELECT CONCAT('    - ', version, ': ', description) as 'Migration'
                FROM flyway_schema_history 
                ORDER BY version;
                " | tail -n +2
                
            else
                echo -e "${RED}  ✗ Nenhuma migração executada${NC}"
                ((FAILED++))
            fi
        else
            echo -e "${RED}  ✗ Tabela flyway_schema_history não existe${NC}"
            ((FAILED++))
        fi
    else
        echo -e "${RED}Database $DB não acessível${NC}"
        ((FAILED++))
    fi
    
    echo ""
done

# ==================================================================================
# PARTE 3: Verificar Tabelas Criadas
# ==================================================================================
echo -e "${BLUE}[3] Verificando Tabelas Criadas nos Tenants${NC}"
echo ""

for DB in $TENANT_DBS; do
    echo "Database: $DB"
    
    if mysql -u root -p12345 -e "USE $DB;" &> /dev/null; then
        
        # Listar tabelas criadas
        TABLES=$(mysql -u root -p12345 "$DB" -N -e "
        SELECT GROUP_CONCAT(table_name SEPARATOR ', ')
        FROM information_schema.tables 
        WHERE table_schema = '$DB' 
        AND table_name NOT IN ('flyway_schema_history')
        ORDER BY table_name;
        ")
        
        if [ -n "$TABLES" ] && [ "$TABLES" != "NULL" ]; then
            TABLE_COUNT=$(echo "$TABLES" | tr ',' '\n' | wc -l)
            echo -e "${GREEN}  ✓ $TABLE_COUNT tabelas criadas:${NC}"
            echo "$TABLES" | tr ',' '\n' | while read -r table; do
                echo "    - $table"
            done
        else
            echo -e "${RED}  ✗ Nenhuma tabela criada${NC}"
        fi
    fi
    
    echo ""
done

# ==================================================================================
# PARTE 4: Verificar Dados Inseridos
# ==================================================================================
echo -e "${BLUE}[4] Verificando Dados Inseridos${NC}"
echo ""

for DB in $TENANT_DBS; do
    echo "Database: $DB"
    
    if mysql -u root -p12345 -e "USE $DB;" &> /dev/null; then
        
        # Contar registros em tabelas principais
        CLIENTE_COUNT=$(mysql -u root -p12345 "$DB" -N -e "SELECT COUNT(*) FROM tb_cliente;" 2>/dev/null || echo "0")
        ENDERECO_COUNT=$(mysql -u root -p12345 "$DB" -N -e "SELECT COUNT(*) FROM tb_endereco;" 2>/dev/null || echo "0")
        USUARIO_COUNT=$(mysql -u root -p12345 "$DB" -N -e "SELECT COUNT(*) FROM tb_usuario;" 2>/dev/null || echo "0")
        ROLE_COUNT=$(mysql -u root -p12345 "$DB" -N -e "SELECT COUNT(*) FROM tb_role;" 2>/dev/null || echo "0")
        
        echo "  Registros:"
        echo "    - tb_cliente: $CLIENTE_COUNT"
        echo "    - tb_endereco: $ENDERECO_COUNT"
        echo "    - tb_usuario: $USUARIO_COUNT"
        echo "    - tb_role: $ROLE_COUNT"
        
        if [ "$CLIENTE_COUNT" -gt 0 ] || [ "$USUARIO_COUNT" -gt 0 ]; then
            echo -e "${GREEN}  ✓ Dados inseridos com sucesso${NC}"
            ((PASSED++))
        else
            echo -e "${YELLOW}  ⚠ Sem dados de teste${NC}"
        fi
    fi
    
    echo ""
done

# ==================================================================================
# PARTE 5: Resumo
# ==================================================================================
echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                        RESUMO                                  ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

echo "Master Database: $MASTER_MIGRATIONS migrações"
echo "Tenants Migrados: $MIGRATED_TENANTS / $TOTAL_TENANTS"
echo ""

if [ "$FAILED" -eq 0 ]; then
    echo -e "${GREEN}✅ TODOS os testes passaram!${NC}"
    echo ""
    echo "O sistema está pronto para uso com múltiplos tenants."
    echo "Cada tenant tem seu próprio banco de dados com as tabelas criadas."
    echo ""
else
    echo -e "${RED}❌ $FAILED testes falharam${NC}"
    echo ""
    echo "Verifique os logs da aplicação para mais detalhes:"
    echo "  - Verifique se ApplicationStartupListener foi executado"
    echo "  - Verifique se TenantMigrationService conectou aos bancos"
    echo "  - Verifique credenciais em tenant_datasource"
    echo ""
fi

# ==================================================================================
# PARTE 6: Próximas Ações
# ==================================================================================
echo -e "${BLUE}[5] Próximas Ações${NC}"
echo ""

if [ "$FAILED" -eq 0 ]; then
    echo "✅ Testar API com curl:"
    echo ""
    echo "  # Gerar JWT Token:"
    echo "  # (Veja tokens.json gerado anteriormente)"
    echo ""
    echo "  # Testar endpoint autenticado:"
    echo "  curl -H 'Authorization: Bearer {TOKEN}' \\"
    echo "       -H 'X-Tenant-Id: 1' \\"
    echo "       -H 'X-Tenant-Slug: tenant-hece' \\"
    echo "       http://localhost:8080/api/v1/tenants/1"
    echo ""
    echo "✅ Testar isolamento de dados:"
    echo ""
    echo "  # Cada tenant deve ver apenas seus dados"
    echo "  # Veja detalhes na documentação MULTITENANT_TESTING_REPORT.md"
    echo ""
else
    echo "❌ Executar migrações novamente:"
    echo ""
    echo "  # Resetar histórico de migrações"
    echo "  mysql -u root -p12345 erpapi -e 'DELETE FROM flyway_schema_history;'"
    echo "  mysql -u root -p12345 tenant1_db -e 'DELETE FROM flyway_schema_history;'"
    echo "  mysql -u root -p12345 shared_db -e 'DELETE FROM flyway_schema_history;'"
    echo ""
    echo "  # Reiniciar a aplicação"
    echo "  mvn spring-boot:run"
    echo ""
fi

echo ""
