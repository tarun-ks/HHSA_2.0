#!/bin/bash

# Script to redeploy BPMN workflows to Camunda 8
# This is useful after reinstalling Camunda or when workflows need to be redeployed

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

WORKFLOW_SERVICE_URL="http://localhost:8093"
BPMN_DIR="backend/workflow-adapter-service/src/main/resources/bpmn"

echo -e "${GREEN}🔄 Redeploying Workflows to Camunda 8...${NC}"
echo "=========================================="
echo ""

# Check if workflow-adapter-service is running
echo -e "${YELLOW}Checking if workflow-adapter-service is running...${NC}"
if ! curl -s "${WORKFLOW_SERVICE_URL}/api-docs" > /dev/null 2>&1; then
    echo -e "${RED}❌ Workflow adapter service is not running on ${WORKFLOW_SERVICE_URL}${NC}"
    echo -e "${YELLOW}Please start the service first:${NC}"
    echo "  ./start-all-services.sh"
    exit 1
fi
echo -e "${GREEN}✅ Workflow adapter service is running${NC}"
echo ""

# Method 1: Restart the service (auto-deploys on startup)
echo -e "${YELLOW}Option 1: Restart workflow-adapter-service (recommended)${NC}"
echo "This will automatically deploy workflows on startup."
echo ""
read -p "Restart workflow-adapter-service? (y/n) " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Stopping workflow-adapter-service...${NC}"
    
    # Find and kill the process
    PID=$(lsof -ti:8093 2>/dev/null || echo "")
    if [ -n "$PID" ]; then
        kill $PID 2>/dev/null || true
        sleep 2
        echo -e "${GREEN}✅ Service stopped${NC}"
    else
        echo -e "${YELLOW}⚠️  No process found on port 8093${NC}"
    fi
    
    echo -e "${YELLOW}Starting workflow-adapter-service...${NC}"
    cd backend/workflow-adapter-service
    mvn spring-boot:run > /tmp/workflow-adapter-redeploy.log 2>&1 &
    NEW_PID=$!
    echo $NEW_PID > /tmp/workflow-adapter-service.pid
    
    echo -e "${YELLOW}Waiting for service to start...${NC}"
    for i in {1..30}; do
        if curl -s "${WORKFLOW_SERVICE_URL}/api-docs" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Service started successfully (PID: $NEW_PID)${NC}"
            echo ""
            echo -e "${YELLOW}Checking deployment logs...${NC}"
            sleep 3
            tail -20 /tmp/workflow-adapter-redeploy.log | grep -i "deploy\|process\|BPMN" || echo "No deployment logs found yet"
            echo ""
            echo -e "${GREEN}✅ Workflows should be deployed!${NC}"
            echo -e "${YELLOW}Check Camunda Operate: http://localhost:8081${NC}"
            exit 0
        fi
        sleep 1
    done
    
    echo -e "${RED}❌ Service failed to start${NC}"
    echo "Check logs: /tmp/workflow-adapter-redeploy.log"
    exit 1
fi

# Method 2: Manual deployment via API
echo ""
echo -e "${YELLOW}Option 2: Manual deployment via API${NC}"
echo "Deploying workflows using REST API..."
echo ""

# Check if BPMN files exist
if [ ! -f "${BPMN_DIR}/WF302_ContractConfiguration.bpmn" ]; then
    echo -e "${RED}❌ BPMN file not found: ${BPMN_DIR}/WF302_ContractConfiguration.bpmn${NC}"
    exit 1
fi

if [ ! -f "${BPMN_DIR}/WF303_ContractCOF.bpmn" ]; then
    echo -e "${RED}❌ BPMN file not found: ${BPMN_DIR}/WF303_ContractCOF.bpmn${NC}"
    exit 1
fi

# Deploy WF302
echo -e "${YELLOW}Deploying WF302: Contract Configuration...${NC}"
BPMN_XML=$(cat "${BPMN_DIR}/WF302_ContractConfiguration.bpmn")
RESPONSE=$(curl -s -X POST "${WORKFLOW_SERVICE_URL}/api/v1/workflows/deploy" \
    -H "Content-Type: application/json" \
    -d "$BPMN_XML")

if echo "$RESPONSE" | grep -q '"success":true'; then
    echo -e "${GREEN}✅ WF302 deployed successfully${NC}"
else
    echo -e "${RED}❌ Failed to deploy WF302${NC}"
    echo "Response: $RESPONSE"
fi

echo ""

# Deploy WF303
echo -e "${YELLOW}Deploying WF303: Contract COF...${NC}"
BPMN_XML=$(cat "${BPMN_DIR}/WF303_ContractCOF.bpmn")
RESPONSE=$(curl -s -X POST "${WORKFLOW_SERVICE_URL}/api/v1/workflows/deploy" \
    -H "Content-Type: application/json" \
    -d "$BPMN_XML")

if echo "$RESPONSE" | grep -q '"success":true'; then
    echo -e "${GREEN}✅ WF303 deployed successfully${NC}"
else
    echo -e "${RED}❌ Failed to deploy WF303${NC}"
    echo "Response: $RESPONSE"
fi

echo ""
echo -e "${GREEN}✅ Workflow deployment complete!${NC}"
echo -e "${YELLOW}Verify in Camunda Operate: http://localhost:8081${NC}"
echo "  Navigate to: Processes → Look for 'ContractConfiguration' and 'ContractCOF'"



