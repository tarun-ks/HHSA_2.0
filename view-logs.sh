#!/bin/bash

# Log Aggregation Viewer
# Shows all service logs in one terminal with color coding

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

LOG_DIR="/tmp/hhsa-services"

# Function to colorize log lines by service
colorize_log() {
    local line="$1"
    
    # Camunda logs
    if echo "$line" | grep -q "camunda\|Camunda"; then
        echo -e "${BLUE}$line${NC}"
    # Auth service
    elif echo "$line" | grep -q "auth-service\|Auth Service"; then
        echo -e "${GREEN}$line${NC}"
    # Document service
    elif echo "$line" | grep -q "document-service\|Document Service"; then
        echo -e "${CYAN}$line${NC}"
    # Workflow service
    elif echo "$line" | grep -q "workflow\|Workflow"; then
        echo -e "${YELLOW}$line${NC}"
    # Audit service
    elif echo "$line" | grep -q "audit-service\|Audit Service"; then
        echo -e "${RED}$line${NC}"
    # Contract service
    elif echo "$line" | grep -q "contract\|Contract"; then
        echo -e "${GREEN}$line${NC}"
    # Errors
    elif echo "$line" | grep -qi "error\|exception\|failed"; then
        echo -e "${RED}$line${NC}"
    # Success/Started
    elif echo "$line" | grep -qi "started\|ready\|success"; then
        echo -e "${GREEN}$line${NC}"
    else
        echo "$line"
    fi
}

# Check if specific service log requested
if [ "$1" != "" ]; then
    SERVICE_LOG="${LOG_DIR}/$1.log"
    if [ -f "$SERVICE_LOG" ]; then
        echo -e "${YELLOW}📋 Viewing logs for: $1${NC}"
        echo "Press Ctrl+C to exit"
        echo ""
        tail -f "$SERVICE_LOG" | while read line; do
            colorize_log "$line"
        done
    else
        echo -e "${RED}❌ Log file not found: $SERVICE_LOG${NC}"
        echo ""
        echo "Available services:"
        ls -1 "${LOG_DIR}"/*.log 2>/dev/null | sed 's|.*/||' | sed 's|\.log||' | sort
        exit 1
    fi
else
    # Show aggregated logs
    echo -e "${BLUE}📋 HHSA Services - Aggregated Logs${NC}"
    echo "======================================"
    echo ""
    echo -e "${YELLOW}Available log files:${NC}"
    
    if [ ! -d "$LOG_DIR" ]; then
        echo -e "${RED}❌ Log directory not found: $LOG_DIR${NC}"
        echo "Start services first using: ./start-all-services.sh"
        exit 1
    fi
    
    LOG_FILES=$(ls -1t "${LOG_DIR}"/*.log 2>/dev/null | head -10)
    
    if [ -z "$LOG_FILES" ]; then
        echo -e "${RED}❌ No log files found${NC}"
        echo "Start services first using: ./start-all-services.sh"
        exit 1
    fi
    
    echo ""
    echo "Following all service logs (Ctrl+C to exit)..."
    echo ""
    
    # Use multitail if available, otherwise use tail with colorization
    if command -v multitail &> /dev/null; then
        multitail -s 2 \
            -ci green "${LOG_DIR}/auth-service.log" \
            -ci cyan "${LOG_DIR}/document-service.log" \
            -ci yellow "${LOG_DIR}/workflow-adapter-service.log" \
            -ci red "${LOG_DIR}/audit-service.log" \
            -ci green "${LOG_DIR}/contract-management-service.log" \
            -ci blue "${LOG_DIR}/camunda.log" 2>/dev/null || \
        tail -f "${LOG_DIR}"/*.log | while read line; do
            colorize_log "$line"
        done
    else
        # Fallback: tail all logs with basic colorization
        tail -f "${LOG_DIR}"/*.log 2>/dev/null | while read line; do
            colorize_log "$line"
        done
    fi
fi



