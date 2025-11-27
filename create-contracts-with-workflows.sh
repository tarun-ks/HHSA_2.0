#!/bin/bash

# Simple script to create test contracts with workflows
# Uses direct API calls with proper error handling

set -e  # Exit on error

API_BASE="http://localhost:8080/api/v1"
USERNAME="${TEST_USERNAME:-acco_admin}"
PASSWORD="${TEST_PASSWORD:-password123}"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}🔐 Step 1: Authenticating...${NC}"
LOGIN_RESP=$(curl -s -X POST "${API_BASE}/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"${USERNAME}\",\"password\":\"${PASSWORD}\",\"provider\":\"keycloak\"}")

TOKEN=$(echo "$LOGIN_RESP" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('accessToken', ''))" 2>/dev/null)

if [ -z "$TOKEN" ] || [ "$TOKEN" == "None" ] || [ "$TOKEN" == "null" ]; then
    echo -e "${RED}❌ Login failed!${NC}"
    echo "Response: $LOGIN_RESP"
    exit 1
fi

# Trim whitespace
TOKEN=$(echo -n "$TOKEN" | tr -d '\n\r ')
echo -e "${GREEN}✅ Authenticated successfully${NC}\n"

# Get dates
START_DATE=$(date -u +"%Y-%m-%d")
END_DATE=$(date -u -v+1y +"%Y-%m-%d" 2>/dev/null || date -u -d "+1 year" +"%Y-%m-%d" 2>/dev/null || date -u +"%Y-%m-%d")
TIMESTAMP=$(date +%s)

echo -e "${BLUE}📝 Step 2: Creating test contracts...${NC}\n"

# Contract 1: Pending Configuration (Status 59) - Will launch WF302
echo -e "${GREEN}Creating Contract 1: Pending Configuration (Status 59)${NC}"
CN1="WF-TEST-001-${TIMESTAMP}"
PAYLOAD1="{\"contractNumber\":\"${CN1}\",\"contractTitle\":\"Workflow Test Contract 1\",\"contractValue\":500000,\"contractAmount\":500000,\"contractStartDate\":\"${START_DATE}\",\"contractEndDate\":\"${END_DATE}\",\"agencyId\":\"AGENCY001\",\"programId\":\"PROG001\",\"providerId\":\"PROVIDER001\",\"organizationId\":\"ORG001\",\"ePin\":\"EPIN001\"}"

RESP1=$(curl -s -w "\n%{http_code}" -X POST "${API_BASE}/contracts" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d "${PAYLOAD1}")

HTTP_CODE1=$(echo "$RESP1" | tail -1)
BODY1=$(echo "$RESP1" | sed '$d')

if [ "$HTTP_CODE1" == "201" ] || [ "$HTTP_CODE1" == "200" ]; then
    CID1=$(echo "$BODY1" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)
    WK1=$(echo "$BODY1" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('configurationWorkflowInstanceKey', 'None'))" 2>/dev/null)
    echo -e "   ✅ Contract ID: ${CID1}"
    echo -e "   ✅ Workflow Instance: ${WK1}"
else
    echo -e "   ${RED}❌ Failed (HTTP ${HTTP_CODE1})${NC}"
    echo "   Response: ${BODY1:0:200}"
fi
echo ""

# Contract 2: Another Pending Configuration
echo -e "${GREEN}Creating Contract 2: Pending Configuration (Status 59)${NC}"
CN2="WF-TEST-002-${TIMESTAMP}"
PAYLOAD2="{\"contractNumber\":\"${CN2}\",\"contractTitle\":\"Workflow Test Contract 2\",\"contractValue\":750000,\"contractAmount\":750000,\"contractStartDate\":\"${START_DATE}\",\"contractEndDate\":\"${END_DATE}\",\"agencyId\":\"AGENCY002\",\"programId\":\"PROG002\",\"providerId\":\"PROVIDER002\",\"organizationId\":\"ORG002\",\"ePin\":\"EPIN002\"}"

RESP2=$(curl -s -w "\n%{http_code}" -X POST "${API_BASE}/contracts" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d "${PAYLOAD2}")

HTTP_CODE2=$(echo "$RESP2" | tail -1)
BODY2=$(echo "$RESP2" | sed '$d')

if [ "$HTTP_CODE2" == "201" ] || [ "$HTTP_CODE2" == "200" ]; then
    CID2=$(echo "$BODY2" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)
    WK2=$(echo "$BODY2" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('configurationWorkflowInstanceKey', 'None'))" 2>/dev/null)
    echo -e "   ✅ Contract ID: ${CID2}"
    echo -e "   ✅ Workflow Instance: ${WK2}"
else
    echo -e "   ${RED}❌ Failed (HTTP ${HTTP_CODE2})${NC}"
    echo "   Response: ${BODY2:0:200}"
fi
echo ""

# Contract 3: Another one
echo -e "${GREEN}Creating Contract 3: Pending Configuration (Status 59)${NC}"
CN3="WF-TEST-003-${TIMESTAMP}"
PAYLOAD3="{\"contractNumber\":\"${CN3}\",\"contractTitle\":\"Workflow Test Contract 3\",\"contractValue\":1000000,\"contractAmount\":1000000,\"contractStartDate\":\"${START_DATE}\",\"contractEndDate\":\"${END_DATE}\",\"agencyId\":\"AGENCY003\",\"programId\":\"PROG003\",\"providerId\":\"PROVIDER003\",\"organizationId\":\"ORG003\",\"ePin\":\"EPIN003\"}"

RESP3=$(curl -s -w "\n%{http_code}" -X POST "${API_BASE}/contracts" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d "${PAYLOAD3}")

HTTP_CODE3=$(echo "$RESP3" | tail -1)
BODY3=$(echo "$RESP3" | sed '$d')

if [ "$HTTP_CODE3" == "201" ] || [ "$HTTP_CODE3" == "200" ]; then
    CID3=$(echo "$BODY3" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)
    WK3=$(echo "$BODY3" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('configurationWorkflowInstanceKey', 'None'))" 2>/dev/null)
    echo -e "   ✅ Contract ID: ${CID3}"
    echo -e "   ✅ Workflow Instance: ${WK3}"
else
    echo -e "   ${RED}❌ Failed (HTTP ${HTTP_CODE3})${NC}"
    echo "   Response: ${BODY3:0:200}"
fi
echo ""

echo -e "${BLUE}⏳ Step 3: Waiting 5 seconds for workflows to initialize...${NC}"
sleep 5
echo ""

echo -e "${BLUE}📋 Step 4: Checking for workflow tasks...${NC}"
TASKS_RESP=$(curl -s -X GET "${API_BASE}/workflows/tasks/user/${USERNAME}" \
    -H "Authorization: Bearer ${TOKEN}")

TASK_COUNT=$(echo "$TASKS_RESP" | python3 -c "import sys, json; d=json.load(sys.stdin); tasks=d.get('data', []); print(len(tasks))" 2>/dev/null || echo "0")

if [ "$TASK_COUNT" -gt 0 ]; then
    echo -e "${GREEN}✅ Found ${TASK_COUNT} workflow tasks${NC}"
    echo "$TASKS_RESP" | python3 -c "import sys, json; d=json.load(sys.stdin); [print(f'   - Task: {t.get(\"taskName\", \"Unknown\")} (Contract: {t.get(\"contractId\", \"N/A\")})') for t in d.get('data', [])[:10]]" 2>/dev/null
else
    echo -e "${YELLOW}⚠️  No tasks found yet (workflows may still be starting)${NC}"
fi
echo ""

echo -e "${GREEN}✅ Done!${NC}"
echo ""
echo "📊 Summary:"
echo "   Contract 1: ID ${CID1:-N/A}, Workflow: ${WK1:-None}"
echo "   Contract 2: ID ${CID2:-N/A}, Workflow: ${WK2:-None}"
echo "   Contract 3: ID ${CID3:-N/A}, Workflow: ${WK3:-None}"
echo ""
echo "🔗 View contracts: http://localhost:3000/contracts"
echo "🔗 View tasks: http://localhost:3000/workflows/tasks"
