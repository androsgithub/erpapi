#!/bin/bash

# ==================================================================================
# SCRIPT: Teste Multi-Tenant com Curl
# DESCRIÇÃO: Valida que o sistema está roteando corretamente entre tenants
# ==================================================================================

# Configurações
API_URL="http://localhost:8080"
JWT_SECRET="your-secret-key-change-this-in-production-environment-very-important"

# Função para gerar JWT token (usando jq + base64)
# Token inclui: email, usuarioId, tenantId, tenantSlug
generate_jwt() {
    local email=$1
    local usuario_id=$2
    local tenant_id=$3
    local tenant_slug=$4
    
    # Header
    local header='{
        "alg": "HS512",
        "typ": "JWT"
    }'
    
    # Payload
    local payload='{
        "sub": "'$email'",
        "usuarioId": "'$usuario_id'",
        "tenantId": "'$tenant_id'",
        "tenantSlug": "'$tenant_slug'",
        "iat": '$(date +%s)',
        "exp": '$(( $(date +%s) + 86400 ))',
        "jti": "test-token"
    }'
    
    # Encode header
    local header_b64=$(echo -n "$header" | base64 | tr -d '=' | tr '+/' '-_')
    
    # Encode payload
    local payload_b64=$(echo -n "$payload" | base64 | tr -d '=' | tr '+/' '-_')
    
    # Note: A assinatura real requer HMAC-SHA512 que é complexo em bash
    # Para simplificar, você pode usar um token JWT válido gerado externamente
    # Ou comentar o teste com autenticação
    
    echo "Header: $header_b64"
    echo "Payload: $payload_b64"
}

echo "=========================================================================="
echo "TESTE MULTI-TENANT COM CURL"
echo "=========================================================================="
echo ""

# ==================================================================================
# TESTE 1: Verificar se a aplicação está rodando
# ==================================================================================
echo "📌 TESTE 1: Verificar se a aplicação está rodando"
echo "Comando: curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/swagger-ui.html"
status=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/swagger-ui.html)
if [ "$status" = "200" ] || [ "$status" = "301" ]; then
    echo "✅ Aplicação está rodando (status: $status)"
else
    echo "❌ Aplicação não está rodando (status: $status)"
    echo "Inicie a aplicação com: mvn spring-boot:run"
    exit 1
fi
echo ""

# ==================================================================================
# TESTE 2: Testar roteamento SEM autenticação (endpoints públicos)
# ==================================================================================
echo "=========================================================================="
echo "📌 TESTE 2: Listar Clientes sem Headers de Tenant"
echo "=========================================================================="
echo ""

echo "Teste 2a: GET /api/v1/clientes SEM tenant headers"
curl -v http://localhost:8080/api/v1/clientes 2>&1 | grep -A 5 "< HTTP"
echo ""
echo ""

# ==================================================================================
# TESTE 3: Testar com Headers de Tenant (sem autenticação JWT)
# ==================================================================================
echo "=========================================================================="
echo "📌 TESTE 3: Acessar com Headers X-Tenant-Id e X-Tenant-Slug"
echo "=========================================================================="
echo ""

echo "Teste 3a: GET /api/v1/clientes com X-Tenant-Id=2 e X-Tenant-Slug=tenant1"
echo "Esperado: Erro de autenticação (sem JWT token)"
echo ""
curl -s -X GET "http://localhost:8080/api/v1/clientes" \
    -H "X-Tenant-Id: 2" \
    -H "X-Tenant-Slug: tenant1" \
    -H "Content-Type: application/json" | jq . 2>/dev/null || echo "Resposta não é JSON"
echo ""
echo ""

# ==================================================================================
# NOTA IMPORTANTE
# ==================================================================================
echo "=========================================================================="
echo "⚠️  PRÓXIMOS PASSOS"
echo "=========================================================================="
echo ""
echo "Para testar com autenticação JWT, você precisa:"
echo ""
echo "1. Gerar um JWT token com as claims corretas:"
echo "   - email: um email válido"
echo "   - usuarioId: um ID de usuário existente"
echo "   - tenantId: o ID do tenant (2, 3 ou 4)"
echo "   - tenantSlug: o slug do tenant (tenant1, tenant2 ou tenant3)"
echo ""
echo "2. Opção A: Usar jwt.io"
echo "   - Ir para https://jwt.io"
echo "   - Header: { 'alg': 'HS512', 'typ': 'JWT' }"
echo "   - Payload: { 'sub': 'test@tenant1.test', 'usuarioId': '1', 'tenantId': '2', 'tenantSlug': 'tenant1' }"
echo "   - Secret: $JWT_SECRET"
echo ""
echo "3. Opção B: Script Python para gerar JWT"
echo "   Salve como generate_jwt.py:"
echo ""
cat > /tmp/generate_jwt_example.py << 'PYEOF'
#!/usr/bin/env python3
import jwt
import json
from datetime import datetime, timedelta

secret = "your-secret-key-change-this-in-production-environment-very-important"
algorithm = "HS512"

# Payload para Tenant 1 (id=2, slug=tenant1)
payload = {
    "sub": "test@tenant1.test",
    "usuarioId": "1",
    "tenantId": "2",
    "tenantSlug": "tenant1",
    "iat": datetime.utcnow(),
    "exp": datetime.utcnow() + timedelta(hours=24)
}

token = jwt.encode(payload, secret, algorithm=algorithm)
print(f"Token for Tenant 1:\n{token}\n")

# Payload para Tenant 2 (id=3, slug=tenant2)
payload["tenantId"] = "3"
payload["tenantSlug"] = "tenant2"
payload["sub"] = "test@tenant2.test"
token = jwt.encode(payload, secret, algorithm=algorithm)
print(f"Token for Tenant 2:\n{token}\n")

# Payload para Tenant 3 (id=4, slug=tenant3)
payload["tenantId"] = "4"
payload["tenantSlug"] = "tenant3"
payload["sub"] = "test@tenant3.test"
token = jwt.encode(payload, secret, algorithm=algorithm)
print(f"Token for Tenant 3:\n{token}\n")
PYEOF

echo "   python3 generate_jwt_example.py"
echo ""
echo "4. Usar o token para fazer requisições:"
echo ""
echo "   curl -X GET 'http://localhost:8080/api/v1/clientes' \\"
echo "       -H 'Authorization: Bearer <TOKEN>' \\"
echo "       -H 'X-Tenant-Id: 2' \\"
echo "       -H 'X-Tenant-Slug: tenant1'"
echo ""
echo "=========================================================================="
echo ""
