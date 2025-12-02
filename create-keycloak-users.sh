#!/bin/bash

# Script to create missing Keycloak users

KEYCLOAK_URL="http://localhost:8090"
REALM="hhsa"

echo "🔐 Creating Keycloak Users..."
echo "=============================="

# Get admin token
ADMIN_TOKEN=$(curl -s -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | python3 -c "import sys, json; print(json.load(sys.stdin).get('access_token', ''))" 2>/dev/null)

if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" == "null" ]; then
    echo "❌ Failed to get admin token"
    exit 1
fi

# Create acco_manager
echo ""
echo "Creating user: acco_manager with role: ACCO_MANAGER"
EXISTING=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/users?username=acco_manager" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" | python3 -c "import sys, json; d=json.load(sys.stdin); print('exists' if d and len(d) > 0 else '')" 2>/dev/null)

if [ "$EXISTING" == "exists" ]; then
    echo "  ℹ️  User already exists, updating..."
    USER_ID=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/users?username=acco_manager" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d[0].get('id', '') if d and len(d) > 0 else '')" 2>/dev/null)
    curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}/reset-password" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" \
      -d '{"type":"password","value":"password123","temporary":false}' > /dev/null
    curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" \
      -d '{"enabled":true}' > /dev/null
else
    curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/users" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" \
      -d '{"username":"acco_manager","enabled":true,"email":"acco_manager@hhsa.local","firstName":"Acco","lastName":"Manager","credentials":[{"type":"password","value":"password123","temporary":false}]}' > /dev/null
    sleep 1
    USER_ID=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/users?username=acco_manager" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d[0].get('id', '') if d and len(d) > 0 else '')" 2>/dev/null)
fi

if [ -n "$USER_ID" ]; then
    ROLE_ID=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/roles/ACCO_MANAGER" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))" 2>/dev/null)
    if [ -n "$ROLE_ID" ]; then
        curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}/role-mappings/realm" \
          -H "Authorization: Bearer ${ADMIN_TOKEN}" \
          -H "Content-Type: application/json" \
          -d "[{\"id\":\"${ROLE_ID}\",\"name\":\"ACCO_MANAGER\"}]" > /dev/null
        echo "  ✅ User created/updated and role assigned"
    fi
fi

# Create acco_staff
echo ""
echo "Creating user: acco_staff with role: ACCO_STAFF"
EXISTING=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/users?username=acco_staff" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" | python3 -c "import sys, json; d=json.load(sys.stdin); print('exists' if d and len(d) > 0 else '')" 2>/dev/null)

if [ "$EXISTING" == "exists" ]; then
    echo "  ℹ️  User already exists, updating..."
    USER_ID=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/users?username=acco_staff" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d[0].get('id', '') if d and len(d) > 0 else '')" 2>/dev/null)
    curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}/reset-password" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" \
      -d '{"type":"password","value":"password123","temporary":false}' > /dev/null
    curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" \
      -d '{"enabled":true}' > /dev/null
else
    curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/users" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      -H "Content-Type: application/json" \
      -d '{"username":"acco_staff","enabled":true,"email":"acco_staff@hhsa.local","firstName":"Acco","lastName":"Staff","credentials":[{"type":"password","value":"password123","temporary":false}]}' > /dev/null
    sleep 1
    USER_ID=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/users?username=acco_staff" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d[0].get('id', '') if d and len(d) > 0 else '')" 2>/dev/null)
fi

if [ -n "$USER_ID" ]; then
    ROLE_ID=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/roles/ACCO_STAFF" \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))" 2>/dev/null)
    if [ -n "$ROLE_ID" ]; then
        curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}/role-mappings/realm" \
          -H "Authorization: Bearer ${ADMIN_TOKEN}" \
          -H "Content-Type: application/json" \
          -d "[{\"id\":\"${ROLE_ID}\",\"name\":\"ACCO_STAFF\"}]" > /dev/null
        echo "  ✅ User created/updated and role assigned"
    fi
fi

echo ""
echo "✅ User creation complete!"
