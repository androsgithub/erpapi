#!/usr/bin/env python3
"""
Script para gerar JWT tokens para testes de Multi-Tenant
Tokens incluem as claims: email, usuarioId, tenantId, tenantSlug
"""

import json
from datetime import datetime, timedelta

import jwt

# Deve ser igual ao valor de jwt.secret em application.properties
SECRET = "your-secret-key-change-this-in-production-environment-very-important"
ALGORITHM = "HS512"

def generate_token(email, usuario_id, tenant_id, tenant_slug):
    """
    Gera um JWT token com as claims necessárias para o sistema
    
    Args:
        email: Email do usuário
        usuario_id: ID do usuário no sistema
        tenant_id: ID do tenant (discriminador de dados)
        tenant_slug: Slug do tenant (roteador de banco de dados)
    
    Returns:
        JWT token assinado
    """
    now = datetime.utcnow()
    payload = {
        "sub": email,                              # Subject (email do usuário)
        "usuarioId": usuario_id,                  # Claim customizada
        "tenantId": tenant_id,                    # Claim customizada (row-based discrimination)
        "tenantSlug": tenant_slug,                # Claim customizada (database routing)
        "iat": now,                               # Issued at
        "exp": now + timedelta(hours=24)         # Expiration
    }
    
    token = jwt.encode(payload, SECRET, algorithm=ALGORITHM)
    return token

def main():
    """Gera tokens para os 3 tenants de teste"""
    
    print("=" * 80)
    print("GERADOR DE JWT TOKENS PARA TESTES DE MULTI-TENANT")
    print("=" * 80)
    print()
    
    # Configurações dos tenants de teste
    tenants = [
        {
            "name": "Tenant 1",
            "id": 2,
            "slug": "tenant1",
            "email": "test@tenant1.test",
            "usuario_id": "1"
        },
        {
            "name": "Tenant 2",
            "id": 3,
            "slug": "tenant2",
            "email": "test@tenant2.test",
            "usuario_id": "1"
        },
        {
            "name": "Tenant 3",
            "id": 4,
            "slug": "tenant3",
            "email": "test@tenant3.test",
            "usuario_id": "1"
        }
    ]
    
    tokens = {}
    
    for tenant in tenants:
        print(f"📌 Gerando token para {tenant['name']}")
        print(f"   - tenant_id: {tenant['id']}")
        print(f"   - tenant_slug: {tenant['slug']}")
        print(f"   - email: {tenant['email']}")
        print()
        
        token = generate_token(
            email=tenant['email'],
            usuario_id=tenant['usuario_id'],
            tenant_id=str(tenant['id']),
            tenant_slug=tenant['slug']
        )
        
        tokens[tenant['slug']] = token
        
        print(f"✅ Token gerado:")
        print(f"{token}")
        print()
        print("-" * 80)
        print()
    
    # Exibir exemplos de uso
    print()
    print("=" * 80)
    print("EXEMPLOS DE USO COM CURL")
    print("=" * 80)
    print()
    
    for tenant in tenants:
        slug = tenant['slug']
        token = tokens[slug]
        
        print(f"📌 Para {tenant['name']}:")
        print()
        print("Comando curl:")
        print(f"""curl -X GET "http://localhost:8080/api/v1/clientes" \\
    -H "Authorization: Bearer {token}" \\
    -H "X-Tenant-Id: {tenant['id']}" \\
    -H "X-Tenant-Slug: {slug}" \\
    -H "Content-Type: application/json" | jq .""")
        print()
        print("-" * 80)
        print()
    
    # Salvar tokens em arquivo JSON para referência
    with open("tokens.json", "w") as f:
        tokens_data = {}
        for tenant in tenants:
            tokens_data[tenant['slug']] = {
                "token": tokens[tenant['slug']],
                "tenant_id": tenant['id'],
                "email": tenant['email']
            }
        json.dump(tokens_data, f, indent=2)
    
    print("✅ Tokens salvos em 'tokens.json'")
    print()

if __name__ == "__main__":
    main()
