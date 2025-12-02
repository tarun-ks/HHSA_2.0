#!/bin/bash

# Restart Workflow Adapter Service Script
# This script stops and restarts just the workflow-adapter-service

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

LOG_DIR="/tmp/hhsa-services"
SERVICE_NAME="workflow-adapter-service"
SERVICE_DIR="backend/workflow-adapter-service"
PORT="8093"
PROJECT_ROOT="/Users/tarun.o.sharma/Desktop/LLM/hhsa_2.0"

echo -e "${BLUE}🔄 Restarting ${SERVICE_NAME}...${NC}"
echo "======================================"
echo ""

cd "$PROJECT_ROOT"

# Function to check if service is running
check_service() {
    local port=$1
    # Try health endpoint first
    if curl -s -f "http://localhost:${port}/actuator/health" > /dev/null 2>&1; then
        return 0
    fi
    # Fallback: check if port is listening (service might be up but health check failing)
    if lsof -i :${port} > /dev/null 2>&1; then
        # Port is open, service is likely running (even if health check fails)
        return 0
    fi
    return 1
}

# Step 1: Stop the service
echo -e "${YELLOW}📋 Step 1: Stopping ${SERVICE_NAME}...${NC}"
PID_FILE="${LOG_DIR}/${SERVICE_NAME}.pid"

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo -e "${YELLOW}   Stopping process ${PID}...${NC}"
        kill "$PID" 2>/dev/null || true
        sleep 3
        # Force kill if still running
        if ps -p "$PID" > /dev/null 2>&1; then
            echo -e "${YELLOW}   Force killing process ${PID}...${NC}"
            kill -9 "$PID" 2>/dev/null || true
            sleep 1
        fi
        echo -e "${GREEN}✅ Service stopped${NC}"
    else
        echo -e "${YELLOW}⚠️  Process ${PID} not found (may have already stopped)${NC}"
    fi
    rm -f "$PID_FILE"
else
    echo -e "${YELLOW}⚠️  PID file not found. Checking for process on port ${PORT}...${NC}"
    # Try to find and kill process on the port
    LSOF_PID=$(lsof -ti :${PORT} 2>/dev/null || true)
    if [ -n "$LSOF_PID" ]; then
        echo -e "${YELLOW}   Found process ${LSOF_PID} on port ${PORT}, stopping...${NC}"
        kill "$LSOF_PID" 2>/dev/null || true
        sleep 2
        if ps -p "$LSOF_PID" > /dev/null 2>&1; then
            kill -9 "$LSOF_PID" 2>/dev/null || true
        fi
        echo -e "${GREEN}✅ Process stopped${NC}"
    else
        echo -e "${YELLOW}⚠️  No process found on port ${PORT}${NC}"
    fi
fi

# Wait a bit to ensure port is free
sleep 2

# Step 2: Start the service
echo ""
echo -e "${YELLOW}📋 Step 2: Starting ${SERVICE_NAME}...${NC}"

if [ ! -d "$SERVICE_DIR" ]; then
    echo -e "${RED}❌ Service directory not found: ${SERVICE_DIR}${NC}"
    exit 1
fi

cd "$SERVICE_DIR"

# Start service in background
echo -e "${YELLOW}   Starting service on port ${PORT}...${NC}"
mvn spring-boot:run >> "${LOG_DIR}/${SERVICE_NAME}.log" 2>&1 &
PID=$!
echo $PID > "${LOG_DIR}/${SERVICE_NAME}.pid"
echo -e "${GREEN}✅ Service started (PID: ${PID})${NC}"

# Return to project root
cd "$PROJECT_ROOT"

# Step 3: Wait for service to be ready
echo ""
echo -e "${YELLOW}📋 Step 3: Waiting for service to be ready...${NC}"
MAX_ATTEMPTS=60
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if check_service "$PORT"; then
        echo -e "${GREEN}✅ ${SERVICE_NAME} is ready!${NC}"
        echo ""
        echo -e "${BLUE}📊 Service Information:${NC}"
        echo "  • Port: ${PORT}"
        echo "  • PID: ${PID}"
        echo "  • Logs: ${LOG_DIR}/${SERVICE_NAME}.log"
        echo "  • Health: http://localhost:${PORT}/actuator/health"
        echo "  • Swagger: http://localhost:${PORT}/swagger-ui.html"
        echo ""
        exit 0
    fi
    sleep 2
    ATTEMPT=$((ATTEMPT + 1))
    if [ $((ATTEMPT % 10)) -eq 0 ]; then
        echo -e "${YELLOW}   Still waiting... (${ATTEMPT}s)${NC}"
    fi
done

echo -e "${RED}❌ ${SERVICE_NAME} failed to start after ${MAX_ATTEMPTS} attempts${NC}"
echo "Check logs: ${LOG_DIR}/${SERVICE_NAME}.log"
echo ""
echo "View logs: tail -f ${LOG_DIR}/${SERVICE_NAME}.log"
exit 1

