#!/bin/bash

# Service Health Check Script
# Checks status of all services

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🔍 HHSA Services Health Check${NC}"
echo "================================"
echo ""

# Function to check service
check_service() {
    local name=$1
    local port=$2
    local url=$3
    
    if curl -s -f "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ ${name}${NC} - Port ${port} - ${url}"
        return 0
    else
        echo -e "${RED}❌ ${name}${NC} - Port ${port} - Not responding"
        return 1
    fi
}

# Check Camunda Services
echo -e "${YELLOW}Camunda 8 Stack:${NC}"
check_service "Operate" "8081" "http://localhost:8081"
check_service "Tasklist" "8082" "http://localhost:8082"

# Check Zeebe (gRPC - can't use HTTP)
if nc -z localhost 26500 2>/dev/null; then
    echo -e "${GREEN}✅ Zeebe Gateway${NC} - Port 26500 (gRPC) - Running"
else
    echo -e "${RED}❌ Zeebe Gateway${NC} - Port 26500 (gRPC) - Not accessible"
fi

echo ""

# Check Our Services
echo -e "${YELLOW}HHSA Backend Services:${NC}"
check_service "Auth Service" "8091" "http://localhost:8091/api-docs"
check_service "Document Service" "8092" "http://localhost:8092/api-docs"
check_service "Workflow Adapter" "8093" "http://localhost:8093/api-docs"
check_service "Audit Service" "8094" "http://localhost:8094/api-docs"
check_service "Contract Management" "8095" "http://localhost:8095/api-docs"

echo ""
echo "================================"



