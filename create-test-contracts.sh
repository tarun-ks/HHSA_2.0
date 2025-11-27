#!/bin/bash

# Script to create test contracts with different workflow statuses
# Uses existing API endpoints
# Automatically authenticates using test user credentials

API_BASE="http://localhost:8080/api/v1"
TOKEN="${1:-}"

# Test user credentials (can be overridden via environment variables)
USERNAME="${TEST_USERNAME:-acco_admin}"
PASSWORD="${TEST_PASSWORD:-password123}"
PROVIDER="${TEST_PROVIDER:-keycloak}"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Function to login and get token
login() {
    echo -e "${BLUE}Authenticating with API Gateway...${NC}"
    
    local login_response=$(curl -s -X POST "${API_BASE}/auth/login" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"${USERNAME}\",
            \"password\": \"${PASSWORD}\",
            \"provider\": \"${PROVIDER}\"
        }")
    
    # Extract access token from response and trim whitespace
    local access_token=$(echo "$login_response" | python3 -c "import sys, json; data=json.load(sys.stdin); token=data.get('data', {}).get('accessToken', ''); print(token.strip() if token else '')" 2>/dev/null)
    
    # Remove any trailing newlines or carriage returns
    access_token=$(echo -n "$access_token" | tr -d '\n\r')
    
    if [ -z "$access_token" ] || [ "$access_token" == "None" ] || [ "$access_token" == "null" ]; then
        echo -e "${RED}❌ Authentication failed!${NC}"
        echo "Response: $login_response"
        echo ""
        echo "Available test users:"
        echo "  - acco_admin / password123 (ACCO_ADMIN_STAFF)"
        echo "  - acco_manager / password123 (ACCO_MANAGER)"
        echo "  - acco_staff / password123 (ACCO_STAFF)"
        echo "  - finance_manager / password123 (FINANCE_MANAGER)"
        echo "  - finance_staff / password123 (FINANCE_STAFF)"
        echo "  - program_manager / password123 (PROGRAM_MANAGER)"
        echo ""
        echo "Usage: TEST_USERNAME=username TEST_PASSWORD=password ./create-test-contracts.sh"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Authentication successful!${NC}" >&2
    echo "$access_token"
}

# Get token if not provided
if [ -z "$TOKEN" ]; then
    TOKEN=$(login)
    if [ -z "$TOKEN" ]; then
        exit 1
    fi
    # Remove any trailing newlines or carriage returns
    TOKEN=$(echo -n "$TOKEN" | tr -d '\n\r')
    echo ""
fi

# Debug: Verify token is set (first 30 chars only)
if [ -z "$TOKEN" ]; then
    echo -e "${RED}❌ ERROR: Token is empty!${NC}"
    exit 1
fi

echo -e "${BLUE}Creating test contracts with different workflow statuses...${NC}\n"

# Function to make API call
api_call() {
    local method=$1
    local endpoint=$2
    local data=$3
    
    local temp_file=$(mktemp)
    local http_code=$(curl -s -w "%{http_code}" -o "$temp_file" -X "$method" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d "$data" \
        "${API_BASE}${endpoint}")
    
    local body=$(cat "$temp_file")
    rm -f "$temp_file"
    
    # Only show error if it's actually an error
    if [ "$http_code" != "200" ] && [ "$http_code" != "201" ]; then
        echo -e "${RED}API call failed with HTTP $http_code${NC}" >&2
        if [ -n "$body" ] && [ ${#body} -gt 0 ]; then
            # Try to extract error message from JSON response
            local error_msg=$(echo "$body" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('message', data.get('error', {}).get('description', 'Unknown error')))" 2>/dev/null || echo "$body")
            echo -e "${RED}Error: $error_msg${NC}" >&2
        fi
    fi
    
    echo "$body"
}

# Get current date and future dates
START_DATE=$(date -u +"%Y-%m-%d")
END_DATE=$(date -u -v+1y +"%Y-%m-%d" 2>/dev/null || date -u -d "+1 year" +"%Y-%m-%d" 2>/dev/null || date -u +"%Y-%m-%d")

# Generate unique suffix for contract numbers
TIMESTAMP=$(date +%s)
SUFFIX="${TIMESTAMP: -6}"  # Last 6 digits of timestamp

echo -e "${GREEN}1. Creating Contract 1: Status 59 (Pending Configuration)${NC}"
CONTRACT1=$(api_call POST "/contracts" "{\"contractNumber\": \"TEST-CONTRACT-001-${SUFFIX}\", \"contractTitle\": \"Test Contract - Pending Configuration\", \"contractValue\": 500000.00, \"contractAmount\": 500000.00, \"contractStartDate\": \"${START_DATE}\", \"contractEndDate\": \"${END_DATE}\", \"agencyId\": \"AGENCY001\", \"programId\": \"PROG001\", \"providerId\": \"PROVIDER001\", \"organizationId\": \"ORG001\", \"ePin\": \"EPIN001\"}")

CONTRACT1_ID=$(echo "$CONTRACT1" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('id', ''))" 2>/dev/null || echo "$CONTRACT1" | grep -oE '"id"\s*:\s*[0-9]+' | head -1 | grep -oE '[0-9]+')
if [ -z "$CONTRACT1_ID" ]; then
    echo -e "${RED}   ⚠️  Failed to create contract. Response: ${CONTRACT1:0:200}...${NC}"
else
    echo "   Created Contract ID: $CONTRACT1_ID"
fi
echo "   Status: 59 (Pending Configuration) - Workflow WF302 should be launched automatically"
echo ""

echo -e "${GREEN}2. Creating Contract 2: Status 59, then configuring to Status 60${NC}"
CONTRACT2=$(api_call POST "/contracts" "{\"contractNumber\": \"TEST-CONTRACT-002-${SUFFIX}\", \"contractTitle\": \"Test Contract - Pending COF\", \"contractValue\": 750000.00, \"contractAmount\": 750000.00, \"contractStartDate\": \"${START_DATE}\", \"contractEndDate\": \"${END_DATE}\", \"agencyId\": \"AGENCY002\", \"programId\": \"PROG002\", \"providerId\": \"PROVIDER002\", \"organizationId\": \"ORG002\", \"ePin\": \"EPIN002\"}")

CONTRACT2_ID=$(echo "$CONTRACT2" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('id', ''))" 2>/dev/null || echo "$CONTRACT2" | grep -oE '"id"\s*:\s*[0-9]+' | head -1 | grep -oE '[0-9]+')
echo "   Created Contract ID: $CONTRACT2_ID"

echo "   Configuring contract with COA allocations..."
CONFIG_RESPONSE=$(api_call POST "/contracts/${CONTRACT2_ID}/configure" "{\"contractId\": ${CONTRACT2_ID}, \"coaAllocations\": [{\"uobc\": \"UOBC001\", \"subOc\": \"SUBOC001\", \"rc\": \"RC001\", \"amount\": 375000.00, \"fiscalYearAmounts\": {\"FY12\": 187500.00, \"FY13\": 187500.00}}, {\"uobc\": \"UOBC002\", \"subOc\": \"SUBOC002\", \"rc\": \"RC002\", \"amount\": 375000.00, \"fiscalYearAmounts\": {\"FY12\": 187500.00, \"FY13\": 187500.00}}]}")
echo "   Configuration completed. Status should be 60 (Pending COF)"
echo ""

echo -e "${GREEN}3. Creating Contract 3: Status 61 (Pending Registration)${NC}"
CONTRACT3=$(api_call POST "/contracts" "{\"contractNumber\": \"TEST-CONTRACT-003-${SUFFIX}\", \"contractTitle\": \"Test Contract - Pending Registration\", \"contractValue\": 1000000.00, \"contractAmount\": 1000000.00, \"contractStartDate\": \"${START_DATE}\", \"contractEndDate\": \"${END_DATE}\", \"agencyId\": \"AGENCY003\", \"programId\": \"PROG003\", \"providerId\": \"PROVIDER003\", \"organizationId\": \"ORG003\", \"ePin\": \"EPIN003\"}")

CONTRACT3_ID=$(echo "$CONTRACT3" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('id', ''))" 2>/dev/null || echo "$CONTRACT3" | grep -oE '"id"\s*:\s*[0-9]+' | head -1 | grep -oE '[0-9]+')
echo "   Created Contract ID: $CONTRACT3_ID"

api_call PATCH "/contracts/${CONTRACT3_ID}/status" "{\"statusId\": 61}" > /dev/null
echo "   Status updated to 61 (Pending Registration)"
echo ""

echo -e "${GREEN}4. Creating Contract 4: Status 62 (Registered)${NC}"
CONTRACT4=$(api_call POST "/contracts" "{\"contractNumber\": \"TEST-CONTRACT-004-${SUFFIX}\", \"contractTitle\": \"Test Contract - Registered\", \"contractValue\": 2500000.00, \"contractAmount\": 2500000.00, \"contractStartDate\": \"${START_DATE}\", \"contractEndDate\": \"${END_DATE}\", \"agencyId\": \"AGENCY004\", \"programId\": \"PROG004\", \"providerId\": \"PROVIDER004\", \"organizationId\": \"ORG004\", \"ePin\": \"EPIN004\"}")

CONTRACT4_ID=$(echo "$CONTRACT4" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('id', ''))" 2>/dev/null || echo "$CONTRACT4" | grep -oE '"id"\s*:\s*[0-9]+' | head -1 | grep -oE '[0-9]+')
echo "   Created Contract ID: $CONTRACT4_ID"

api_call PATCH "/contracts/${CONTRACT4_ID}/status" "{\"statusId\": 62}" > /dev/null
echo "   Status updated to 62 (Registered)"
echo ""

echo -e "${GREEN}5. Creating Contract 5: Status 67 (Suspended)${NC}"
CONTRACT5=$(api_call POST "/contracts" "{\"contractNumber\": \"TEST-CONTRACT-005-${SUFFIX}\", \"contractTitle\": \"Test Contract - Suspended\", \"contractValue\": 300000.00, \"contractAmount\": 300000.00, \"contractStartDate\": \"${START_DATE}\", \"contractEndDate\": \"${END_DATE}\", \"agencyId\": \"AGENCY005\", \"programId\": \"PROG005\", \"providerId\": \"PROVIDER005\", \"organizationId\": \"ORG005\", \"ePin\": \"EPIN005\"}")

CONTRACT5_ID=$(echo "$CONTRACT5" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('id', ''))" 2>/dev/null || echo "$CONTRACT5" | grep -oE '"id"\s*:\s*[0-9]+' | head -1 | grep -oE '[0-9]+')
echo "   Created Contract ID: $CONTRACT5_ID"

api_call PATCH "/contracts/${CONTRACT5_ID}/status" "{\"statusId\": 67}" > /dev/null
echo "   Status updated to 67 (Suspended)"
echo ""

echo -e "${BLUE}Summary:${NC}"
echo "  Contract 1 (ID: $CONTRACT1_ID) - Status 59: Pending Configuration"
echo "  Contract 2 (ID: $CONTRACT2_ID) - Status 60: Pending COF (with COA configuration)"
echo "  Contract 3 (ID: $CONTRACT3_ID) - Status 61: Pending Registration"
echo "  Contract 4 (ID: $CONTRACT4_ID) - Status 62: Registered"
echo "  Contract 5 (ID: $CONTRACT5_ID) - Status 67: Suspended"
echo ""
echo -e "${YELLOW}Note: Contracts 1 and 2 should have workflow instances (WF302) if Camunda is running.${NC}"
echo ""
echo "View contracts at: http://localhost:3000/contracts"

