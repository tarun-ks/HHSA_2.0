#!/bin/bash

# Keycloak Setup Script
# This script configures Keycloak realm, client, and creates test users

KEYCLOAK_URL="http://localhost:8090"
ADMIN_USER="admin"
ADMIN_PASS="admin"
REALM="hhsa"
CLIENT_ID="hhsa-backend"

echo "🔐 Setting up Keycloak for HHSA..."
echo "===================================="

# Wait for Keycloak to be ready
echo "⏳ Waiting for Keycloak to be ready..."
for i in {1..60}; do
    if curl -s "${KEYCLOAK_URL}/health/ready" > /dev/null 2>&1; then
        echo "✅ Keycloak is ready!"
        break
    fi
    if [ $i -eq 60 ]; then
        echo "❌ Keycloak did not become ready. Please check the container."
        exit 1
    fi
    echo "   Waiting... ($i/60)"
    sleep 3
done

# Get admin token
echo ""
echo "🔑 Getting admin access token..."
ADMIN_TOKEN=$(curl -s -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=${ADMIN_USER}" \
  -d "password=${ADMIN_PASS}" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r '.access_token')

if [ "$ADMIN_TOKEN" == "null" ] || [ -z "$ADMIN_TOKEN" ]; then
    echo "❌ Failed to get admin token. Check admin credentials."
    exit 1
fi
echo "✅ Admin token obtained"

# Create realm
echo ""
echo "📋 Creating realm '${REALM}'..."
REALM_EXISTS=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" | jq -r '.id // empty')

if [ -z "$REALM_EXISTS" ]; then
    curl -s -X POST "${KEYCLOAK_URL}/admin/realms" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" \
      -d "{\"realm\":\"${REALM}\",\"enabled\":true}" > /dev/null
    echo "✅ Realm '${REALM}' created"
else
    echo "ℹ️  Realm '${REALM}' already exists"
fi

# Get realm token
echo ""
echo "🔑 Getting realm admin token..."
REALM_TOKEN=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=${ADMIN_USER}" \
  -d "password=${ADMIN_PASS}" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r '.access_token')

# Create client
echo ""
echo "📋 Creating client '${CLIENT_ID}'..."
CLIENT_EXISTS=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/clients?clientId=${CLIENT_ID}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" | jq -r '.[0].id // empty')

if [ -z "$CLIENT_EXISTS" ]; then
    CLIENT_RESPONSE=$(curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/clients" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" \
      -d "{
        \"clientId\": \"${CLIENT_ID}\",
        \"enabled\": true,
        \"clientAuthenticatorType\": \"client-secret\",
        \"secret\": \"change-me-temporary\",
        \"redirectUris\": [\"http://localhost:3000/*\", \"http://localhost:3001/*\"],
        \"webOrigins\": [\"http://localhost:3000\", \"http://localhost:3001\"],
        \"standardFlowEnabled\": true,
        \"directAccessGrantsEnabled\": true,
        \"serviceAccountsEnabled\": true
      }")
    
    # Get client UUID and update to confidential
    CLIENT_UUID=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/clients?clientId=${CLIENT_ID}" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" | jq -r '.[0].id')
    
    # Get client secret
    CLIENT_SECRET=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/clients/${CLIENT_UUID}/client-secret" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" | jq -r '.value')
    
    echo "✅ Client '${CLIENT_ID}' created"
    echo "   Client Secret: ${CLIENT_SECRET}"
    echo "   ⚠️  Set this in environment: export KEYCLOAK_CLIENT_SECRET=${CLIENT_SECRET}"
else
    echo "ℹ️  Client '${CLIENT_ID}' already exists"
    CLIENT_UUID=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/clients?clientId=${CLIENT_ID}" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" | jq -r '.[0].id')
    CLIENT_SECRET=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/clients/${CLIENT_UUID}/client-secret" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" | jq -r '.value')
    echo "   Client Secret: ${CLIENT_SECRET}"
fi

# Create roles
echo ""
echo "📋 Creating required roles..."
ROLES=("ACCO_STAFF" "ACCO_MANAGER" "ACCO_ADMIN_STAFF" "PROGRAM_MANAGER" "FINANCE_STAFF" "FINANCE_MANAGER" "PROVIDER_MANAGER" "PROVIDER_STAFF")

for ROLE in "${ROLES[@]}"; do
    ROLE_EXISTS=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/roles/${ROLE}" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" 2>/dev/null | jq -r '.name // empty')
    
    if [ -z "$ROLE_EXISTS" ]; then
        curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/roles" \
          -H "Authorization: Bearer ${ADMIN_TOKEN}" \
          -H "Content-Type: application/json" \
          -d "{\"name\":\"${ROLE}\"}" > /dev/null
        echo "   ✅ Role '${ROLE}' created"
    else
        echo "   ℹ️  Role '${ROLE}' already exists"
    fi
done

echo ""
echo "✅ Keycloak setup complete!"
echo ""
echo "📝 Next steps:"
echo "   1. Set client secret: export KEYCLOAK_CLIENT_SECRET=${CLIENT_SECRET}"
echo "   2. Restart auth-service"
echo "   3. Use API to create users: POST http://localhost:8091/api/v1/admin/users/create-test-users"
echo ""
echo "🔗 Access URLs:"
echo "   Admin Console: http://localhost:8090/admin"
echo "   Account Console: http://localhost:8090/realms/${REALM}/account"

