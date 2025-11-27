#!/bin/bash

# Unified Startup Script - Starts Camunda + All HHSA Services
# This script starts everything needed for the application

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Starting HHSA Application Stack...${NC}"
echo "=========================================="
echo ""

# Configuration
CAMUNDA_START_SCRIPT="/Users/tarun.o.sharma/Documents/start-all.sh"
PROJECT_ROOT="/Users/tarun.o.sharma/Desktop/LLM/hhsa_2.0"
LOG_DIR="/tmp/hhsa-services"
LOG_FILE="${LOG_DIR}/all-services.log"

# Keycloak Client Secret (required for auth-service)
# If not set, use the default from Keycloak setup
export KEYCLOAK_CLIENT_SECRET=${KEYCLOAK_CLIENT_SECRET:-9EuwJlytkTGsdQ2X542dp2hGTmoYENHP}

# Create log directory
mkdir -p "$LOG_DIR"

# Function to log with timestamp
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# Function to check if service is running
check_service() {
    local port=$1
    local service_name=$2
    
    if curl -s -f "http://localhost:${port}/api-docs" > /dev/null 2>&1 || \
       curl -s -f "http://localhost:${port}/actuator/health" > /dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Step 1: Check Prerequisites
echo -e "${YELLOW}📋 Checking Prerequisites...${NC}"
echo ""

# Check PostgreSQL
if ! pg_isready -h localhost -p 5432 -U postgres > /dev/null 2>&1; then
    echo -e "${RED}❌ PostgreSQL is not running. Please start PostgreSQL first.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ PostgreSQL is running${NC}"
log "PostgreSQL check: OK"

# Check if Camunda is already running
CAMUNDA_RUNNING=false
if check_service 8081 "Camunda Operate"; then
    echo -e "${GREEN}✅ Camunda is already running${NC}"
    log "Camunda: Already running"
    CAMUNDA_RUNNING=true
else
    echo -e "${YELLOW}⚠️  Camunda is not running${NC}"
    log "Camunda: Not running"
fi

# Check Zeebe Gateway (gRPC, not HTTP)
# Try multiple methods to check if port is open
ZEEBE_RUNNING=false
if (nc -z localhost 26500 2>/dev/null) || \
   (lsof -i :26500 >/dev/null 2>&1) || \
   (timeout 1 bash -c "</dev/tcp/localhost/26500" 2>/dev/null); then
    echo -e "${GREEN}✅ Zeebe Gateway is running${NC}"
    log "Zeebe Gateway: Running"
    ZEEBE_RUNNING=true
else
    echo -e "${YELLOW}⚠️  Zeebe Gateway (26500) not accessible${NC}"
    echo -e "${YELLOW}   Note: This is a warning. If Camunda is running, Zeebe may still work.${NC}"
    echo -e "${YELLOW}   The workflow-adapter-service will attempt to connect on startup.${NC}"
    log "Zeebe Gateway: Not accessible (warning only)"
fi

echo ""

# Step 2: Start Camunda (if not running)
if [ "$CAMUNDA_RUNNING" = false ]; then
    echo -e "${YELLOW}🔄 Starting Camunda 8 (optional - you can start manually)...${NC}"
    echo ""
    
    if [ -f "$CAMUNDA_START_SCRIPT" ]; then
        log "Starting Camunda via: $CAMUNDA_START_SCRIPT"
        cd /Users/tarun.o.sharma/Documents
        bash "$CAMUNDA_START_SCRIPT" >> "${LOG_DIR}/camunda.log" 2>&1 &
        CAMUNDA_PID=$!
        echo $CAMUNDA_PID > "${LOG_DIR}/camunda.pid"
        log "Camunda started (PID: $CAMUNDA_PID)"
        
        # Wait for Camunda to start (non-blocking - continue even if it takes time)
        echo -e "${YELLOW}⏳ Waiting for Camunda to start (this may take 1-2 minutes)...${NC}"
        echo -e "${YELLOW}   Note: If it takes too long, you can start Camunda manually${NC}"
        MAX_WAIT=60
        WAIT_COUNT=0
        
        while [ $WAIT_COUNT -lt $MAX_WAIT ]; do
            if check_service 8081 "Camunda Operate"; then
                echo -e "${GREEN}✅ Camunda is ready!${NC}"
                log "Camunda: Ready"
                break
            fi
            sleep 2
            WAIT_COUNT=$((WAIT_COUNT + 2))
            if [ $((WAIT_COUNT % 20)) -eq 0 ]; then
                echo -e "${YELLOW}   Still waiting... (${WAIT_COUNT}s)${NC}"
            fi
        done
        
        if [ $WAIT_COUNT -ge $MAX_WAIT ]; then
            echo -e "${YELLOW}⚠️  Camunda is still starting (takes 1-2 minutes)${NC}"
            echo -e "${YELLOW}   Continuing with other services...${NC}"
            echo -e "${YELLOW}   You can start Camunda manually: cd /Users/tarun.o.sharma/Documents && ./start-all.sh${NC}"
            log "Camunda: Still starting (non-blocking)"
        fi
    else
        echo -e "${YELLOW}⚠️  Camunda start script not found: $CAMUNDA_START_SCRIPT${NC}"
        echo -e "${YELLOW}   Please start Camunda manually: cd /Users/tarun.o.sharma/Documents && ./start-all.sh${NC}"
        log "Camunda: Start script not found (non-blocking)"
    fi
    echo ""
fi

# Step 3: Start Our Backend Services
echo -e "${YELLOW}🔄 Starting HHSA Backend Services...${NC}"
echo ""

cd "$PROJECT_ROOT"

# Function to start a service
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    # Check if already running
    if check_service "$port" "$service_name"; then
        echo -e "${GREEN}✅ ${service_name} is already running on port ${port}${NC}"
        log "${service_name}: Already running"
        return 0
    fi
    
    echo -e "${YELLOW}Starting ${service_name} on port ${port}...${NC}"
    log "Starting ${service_name} on port ${port}"
    
    # Use absolute path to service directory
    local full_service_dir="${PROJECT_ROOT}/${service_dir}"
    
    if [ ! -d "$full_service_dir" ]; then
        echo -e "${RED}❌ Service directory not found: $full_service_dir${NC}"
        log "${service_name}: Directory not found"
        return 1
    fi
    
    cd "$full_service_dir"
    
    # Start service in background
    mvn spring-boot:run >> "${LOG_DIR}/${service_name}.log" 2>&1 &
    local pid=$!
    echo $pid > "${LOG_DIR}/${service_name}.pid"
    log "${service_name}: Started (PID: $pid)"
    
    # Return to project root
    cd "$PROJECT_ROOT"
    
    # Wait for service to be ready
    local max_attempts=60
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if check_service "$port" "$service_name"; then
            echo -e "${GREEN}✅ ${service_name} is ready (PID: $pid)${NC}"
            log "${service_name}: Ready"
            return 0
        fi
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo -e "${RED}❌ ${service_name} failed to start after ${max_attempts} attempts${NC}"
    echo "Check logs: ${LOG_DIR}/${service_name}.log"
    log "${service_name}: Failed to start"
    return 1
}

# Start services in order
# API Gateway must be started first (frontend entry point)
start_service "api-gateway-service" "backend/api-gateway-service" "8080"
sleep 5

start_service "workflow-adapter-service" "backend/workflow-adapter-service" "8093"
sleep 3

start_service "auth-service" "backend/auth-service" "8091"
sleep 3

start_service "document-service" "backend/document-service" "8092"
sleep 3

start_service "audit-service" "backend/audit-service" "8094"
sleep 3

start_service "contract-management-service" "backend/contract-management-service" "8095"
sleep 5

echo ""
echo "=========================================="
echo -e "${GREEN}🎉 All services started!${NC}"
echo ""

# Display service status
echo -e "${BLUE}📊 Service Status:${NC}"
echo ""

# Camunda Services
echo -e "${YELLOW}Camunda 8 Stack:${NC}"
echo "  • Operate:    http://localhost:8081"
echo "  • Tasklist:   http://localhost:8082"
echo "  • Zeebe:      localhost:26500 (gRPC)"
echo ""

# Our Services
echo -e "${YELLOW}HHSA Backend Services:${NC}"
echo "  • API Gateway:           http://localhost:8080 (Frontend Entry Point)"
echo "  • Auth Service:          http://localhost:8091"
echo "  • Document Service:      http://localhost:8092"
echo "  • Workflow Adapter:      http://localhost:8093"
echo "  • Audit Service:         http://localhost:8094"
echo "  • Contract Management:   http://localhost:8095"
echo ""

# Swagger UI
echo -e "${YELLOW}Swagger UI:${NC}"
echo "  • Auth:                  http://localhost:8091/swagger-ui.html"
echo "  • Document:              http://localhost:8092/swagger-ui.html"
echo "  • Workflow:              http://localhost:8093/swagger-ui.html"
echo "  • Audit:                http://localhost:8094/swagger-ui.html"
echo "  • Contract:              http://localhost:8095/swagger-ui.html"
echo ""

# Log information
echo -e "${YELLOW}📋 Logs:${NC}"
echo "  • All logs:              ${LOG_DIR}/all-services.log"
echo "  • Individual logs:       ${LOG_DIR}/<service-name>.log"
echo "  • View all logs:         ./view-logs.sh"
echo "  • View specific service: tail -f ${LOG_DIR}/<service-name>.log"
echo ""

# Management commands
echo -e "${YELLOW}🔧 Management:${NC}"
echo "  • Stop all services:     ./stop-all-services.sh"
echo "  • Check service status:  ./check-services.sh"
echo ""

log "All services started successfully"
