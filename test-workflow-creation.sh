#!/bin/bash

# Simple script to create a contract and check for workflow

TOKEN=$(curl -s -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"acco_admin","password":"password123","provider":"keycloak"}' \
  | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('accessToken', ''))")

TIMESTAMP=$(date +%s)
CONTRACT_NUMBER="WF-TEST-${TIMESTAMP}"

echo "Creating contract: ${CONTRACT_NUMBER}"

RESPONSE=$(curl -s -X POST "http://localhost:8080/api/v1/contracts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"contractNumber\": \"${CONTRACT_NUMBER}\",
    \"contractTitle\": \"Workflow Test Contract\",
    \"contractValue\": 100000,
    \"contractAmount\": 100000,
    \"contractStartDate\": \"2025-01-01\",
    \"contractEndDate\": \"2026-01-01\",
    \"agencyId\": \"AGENCY001\",
    \"programId\": \"PROG001\",
    \"providerId\": \"PROVIDER001\",
    \"organizationId\": \"ORG001\",
    \"ePin\": \"EPIN001\"
  }")

CONTRACT_ID=$(echo "$RESPONSE" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('id', ''))" 2>/dev/null)
WF_KEY=$(echo "$RESPONSE" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('configurationWorkflowInstanceKey', 'None'))" 2>/dev/null)

if [ -n "$CONTRACT_ID" ] && [ "$CONTRACT_ID" != "None" ]; then
  echo "✅ Contract created: ID $CONTRACT_ID"
  echo "   Workflow Instance Key: $WF_KEY"
  
  if [ "$WF_KEY" != "None" ] && [ -n "$WF_KEY" ]; then
    echo "✅ Workflow launched successfully!"
  else
    echo "⚠️  No workflow instance key (may take a few seconds)"
  fi
  
  echo ""
  echo "Waiting 5 seconds for workflow to start..."
  sleep 5
  
  echo "Checking for tasks..."
  TASKS=$(curl -s -X GET "http://localhost:8080/api/v1/workflows/tasks/user/acco_admin" \
    -H "Authorization: Bearer $TOKEN")
  
  TASK_COUNT=$(echo "$TASKS" | python3 -c "import sys, json; data=json.load(sys.stdin); print(len(data.get('data', [])))" 2>/dev/null)
  
  if [ "$TASK_COUNT" -gt 0 ]; then
    echo "✅ Found $TASK_COUNT workflow tasks!"
    echo "$TASKS" | python3 -c "import sys, json; data=json.load(sys.stdin); [print(f\"  - {t.get('taskName')} (Contract ID: {t.get('contractId')}, Task ID: {t.get('taskId')})\") for t in data.get('data', [])[:5]]" 2>/dev/null
  else
    echo "⚠️  No tasks found yet (workflow may still be starting)"
  fi
else
  echo "❌ Failed to create contract"
  echo "Response: $RESPONSE"
fi

