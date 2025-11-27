#!/bin/bash

# Simple script to create contracts with workflows

API_BASE="http://localhost:8080/api/v1"

echo "🔐 Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "${API_BASE}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"acco_admin","password":"password123","provider":"keycloak"}')

TOKEN=$(echo "$LOGIN_RESPONSE" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('accessToken', ''))" 2>/dev/null)

if [ -z "$TOKEN" ]; then
  echo "❌ Login failed"
  echo "$LOGIN_RESPONSE"
  exit 1
fi

echo "✅ Login successful"
echo ""

START_DATE=$(date -u +"%Y-%m-%d")
END_DATE=$(date -u -v+1y +"%Y-%m-%d" 2>/dev/null || date -u -d "+1 year" +"%Y-%m-%d" 2>/dev/null || date -u +"%Y-%m-%d")
TIMESTAMP=$(date +%s)

echo "📝 Creating Contract 1: Status 59 (Pending Configuration)..."
CONTRACT1_RESPONSE=$(curl -s -X POST "${API_BASE}/contracts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"contractNumber\": \"WF-CONTRACT-001-${TIMESTAMP}\",
    \"contractTitle\": \"Workflow Test Contract 1\",
    \"contractValue\": 500000,
    \"contractAmount\": 500000,
    \"contractStartDate\": \"${START_DATE}\",
    \"contractEndDate\": \"${END_DATE}\",
    \"agencyId\": \"AGENCY001\",
    \"programId\": \"PROG001\",
    \"providerId\": \"PROVIDER001\",
    \"organizationId\": \"ORG001\",
    \"ePin\": \"EPIN001\"
  }")

CONTRACT1_ID=$(echo "$CONTRACT1_RESPONSE" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('id', ''))" 2>/dev/null)
CONTRACT1_WF=$(echo "$CONTRACT1_RESPONSE" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('configurationWorkflowInstanceKey', 'None'))" 2>/dev/null)

if [ -n "$CONTRACT1_ID" ] && [ "$CONTRACT1_ID" != "None" ]; then
  echo "✅ Contract 1 created: ID $CONTRACT1_ID"
  echo "   Workflow Instance: $CONTRACT1_WF"
else
  echo "❌ Failed to create Contract 1"
  echo "$CONTRACT1_RESPONSE"
fi

echo ""
echo "⏳ Waiting 5 seconds for workflow to start..."
sleep 5

echo ""
echo "📋 Checking for workflow tasks..."
TASKS_RESPONSE=$(curl -s -X GET "${API_BASE}/workflows/tasks/user/acco_admin" \
  -H "Authorization: Bearer $TOKEN")

TASK_COUNT=$(echo "$TASKS_RESPONSE" | python3 -c "import sys, json; data=json.load(sys.stdin); print(len(data.get('data', [])))" 2>/dev/null)

if [ "$TASK_COUNT" -gt 0 ]; then
  echo "✅ Found $TASK_COUNT workflow tasks:"
  echo "$TASKS_RESPONSE" | python3 -c "import sys, json; data=json.load(sys.stdin); [print(f\"  - {t.get('taskName')} (Contract ID: {t.get('contractId')}, Task ID: {t.get('taskId')})\") for t in data.get('data', [])[:10]]" 2>/dev/null
else
  echo "⚠️  No tasks found yet (workflow may still be starting)"
fi

echo ""
echo "✅ Done! Check contracts at: http://localhost:3000/contracts"
echo "   Check tasks at: http://localhost:3000/workflows/tasks"

