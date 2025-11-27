#!/bin/bash

# Unified Stop Script - Stops All HHSA Services
# This script stops all our backend services (Camunda should be stopped separately)

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

LOG_DIR="/tmp/hhsa-services"

echo -e "${BLUE}🛑 Stopping HHSA Backend Services...${NC}"
echo "======================================"
echo ""

# Function to stop service
stop_service() {
    local service_name=$1
    local pid_file="${LOG_DIR}/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p "$pid" > /dev/null 2>&1; then
            echo -e "${YELLOW}Stopping ${service_name} (PID: $pid)...${NC}"
            kill "$pid" 2>/dev/null || true
            sleep 2
            # Force kill if still running
            if ps -p "$pid" > /dev/null 2>&1; then
                kill -9 "$pid" 2>/dev/null || true
            fi
            echo -e "${GREEN}✅ ${service_name} stopped${NC}"
            rm -f "$pid_file"
        else
            echo -e "${YELLOW}⚠️  ${service_name} not running (PID file exists but process not found)${NC}"
            rm -f "$pid_file"
        fi
    else
        echo -e "${YELLOW}⚠️  ${service_name} PID file not found${NC}"
    fi
}

# Stop services in reverse order
stop_service "contract-management-service"
stop_service "audit-service"
stop_service "document-service"
stop_service "auth-service"
stop_service "workflow-adapter-service"

# Stop Camunda if we started it
if [ -f "${LOG_DIR}/camunda.pid" ]; then
    CAMUNDA_PID=$(cat "${LOG_DIR}/camunda.pid")
    if ps -p "$CAMUNDA_PID" > /dev/null 2>&1; then
        echo ""
        echo -e "${YELLOW}⚠️  Camunda is running (PID: $CAMUNDA_PID)${NC}"
        echo "Camunda was started by this script. Stop it manually or use your Camunda stop script."
    fi
fi

# Kill any remaining Java processes on our ports
echo ""
echo -e "${YELLOW}Checking for processes on our service ports...${NC}"

for port in 8091 8092 8093 8094 8095; do
    PID=$(lsof -ti :$port 2>/dev/null || true)
    if [ ! -z "$PID" ]; then
        echo -e "${YELLOW}Killing process on port $port (PID: $PID)...${NC}"
        kill "$PID" 2>/dev/null || kill -9 "$PID" 2>/dev/null || true
    fi
done

echo ""
echo "======================================"
echo -e "${GREEN}✅ All HHSA backend services stopped${NC}"
echo ""
echo -e "${YELLOW}Note:${NC} Camunda services (8081, 8082) are still running."
echo "Stop them using your Camunda stop script if needed."
