#!/bin/bash

# Script to find and complete a workflow task to generate history

API_BASE="http://localhost:8080/api/v1"
USERNAME="${TEST_USERNAME:-acco_admin}"
PASSWORD="${TEST_PASSWORD:-password123}"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}🔐 Authenticating...${NC}"
TOKEN=$(curl -s -X POST "${API_BASE}/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"${USERNAME}\",\"password\":\"${PASSWORD}\",\"provider\":\"keycloak\"}" \
    | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('accessToken', ''))" 2>/dev/null | tr -d '\n\r ')

if [ -z "$TOKEN" ]; then
    echo -e "${RED}❌ Login failed!${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Authenticated${NC}\n"

# Get process instance key from contract
echo -e "${BLUE}📋 Finding workflow instance...${NC}"
PROCESS_INSTANCE_KEY="${1:-4503599627375516}"

if [ -z "$PROCESS_INSTANCE_KEY" ]; then
    echo -e "${YELLOW}Usage: $0 <processInstanceKey>${NC}"
    echo "Getting latest contract workflow instance..."
    PROCESS_INSTANCE_KEY=$(psql -h localhost -U postgres -d eprocurement -t -c "SELECT configuration_workflow_instance_key FROM contracts WHERE deleted = false AND configuration_workflow_instance_key IS NOT NULL ORDER BY id DESC LIMIT 1;" 2>/dev/null | tr -d ' ')
fi

if [ -z "$PROCESS_INSTANCE_KEY" ] || [ "$PROCESS_INSTANCE_KEY" == "" ]; then
    echo -e "${RED}❌ No workflow instance found!${NC}"
    exit 1
fi

echo -e "   Process Instance: ${PROCESS_INSTANCE_KEY}\n"

# Try to get tasks
echo -e "${BLUE}🔍 Looking for tasks...${NC}"
TASKS_RESP=$(curl -s -X GET "${API_BASE}/workflows/process-instances/${PROCESS_INSTANCE_KEY}/tasks" \
    -H "Authorization: Bearer ${TOKEN}")

TASK_COUNT=$(echo "$TASKS_RESP" | python3 -c "import sys, json; d=json.load(sys.stdin); tasks=d.get('data', []); print(len(tasks))" 2>/dev/null || echo "0")

if [ "$TASK_COUNT" -gt 0 ]; then
    echo -e "${GREEN}✅ Found ${TASK_COUNT} task(s)${NC}"
    
    # Get first task
    TASK_KEY=$(echo "$TASKS_RESP" | python3 -c "import sys, json; d=json.load(sys.stdin); tasks=d.get('data', []); print(tasks[0].get('taskKey', '') if tasks else '')" 2>/dev/null)
    TASK_ID=$(echo "$TASKS_RESP" | python3 -c "import sys, json; d=json.load(sys.stdin); tasks=d.get('data', []); print(tasks[0].get('taskId', '') if tasks else '')" 2>/dev/null)
    TASK_NAME=$(echo "$TASKS_RESP" | python3 -c "import sys, json; d=json.load(sys.stdin); tasks=d.get('data', []); print(tasks[0].get('taskType', 'Unknown') if tasks else '')" 2>/dev/null)
    
    echo "   Task Key: ${TASK_KEY}"
    echo "   Task ID: ${TASK_ID}"
    echo "   Task Name: ${TASK_NAME}"
    echo ""
    
    if [ -n "$TASK_KEY" ]; then
        echo -e "${BLUE}✅ Completing task ${TASK_KEY}...${NC}"
        COMPLETE_RESP=$(curl -s -w "\n%{http_code}" -X POST "${API_BASE}/workflows/tasks/${TASK_KEY}/complete" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer ${TOKEN}" \
            -d "{}")
        
        HTTP_CODE=$(echo "$COMPLETE_RESP" | tail -1)
        BODY=$(echo "$COMPLETE_RESP" | sed '$d')
        
        if [ "$HTTP_CODE" == "200" ] || [ "$HTTP_CODE" == "201" ]; then
            echo -e "${GREEN}✅ Task completed successfully!${NC}"
            echo ""
            echo -e "${BLUE}⏳ Waiting 3 seconds for workflow to progress...${NC}"
            sleep 3
            echo ""
            echo -e "${BLUE}📊 Checking activities now...${NC}"
            curl -s -X GET "${API_BASE}/workflows/process-instances/${PROCESS_INSTANCE_KEY}/activities" \
                -H "Authorization: Bearer ${TOKEN}" \
                | python3 -m json.tool 2>/dev/null | head -30
        else
            echo -e "${RED}❌ Failed to complete task (HTTP ${HTTP_CODE})${NC}"
            echo "Response: ${BODY:0:200}"
        fi
    else
        echo -e "${YELLOW}⚠️  Could not extract task key${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  No tasks found for this process instance${NC}"
    echo ""
    echo "This could mean:"
    echo "  1. The workflow hasn't reached a user task yet"
    echo "  2. Tasks are assigned to a different user"
    echo "  3. Operate API is not tracking tasks"
    echo ""
    echo "Response: ${TASKS_RESP:0:300}"
fi

