#!/bin/bash

# Framework-based Contract Creation Script
# Creates a contract via API and automatically launches workflow (WF302)
# Reusable for any contract type - no hardcoding

set -e

# Configuration (can be overridden via environment variables)
API_BASE="${API_BASE:-http://localhost:8080/api/v1}"
USERNAME="${USERNAME:-acco_admin}"
PASSWORD="${PASSWORD:-password123}"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print usage
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Creates a contract via API and launches workflow (WF302)"
    echo ""
    echo "Options:"
    echo "  -u, --username USERNAME    Username for authentication (default: acco_admin)"
    echo "  -p, --password PASSWORD    Password for authentication"
    echo "  -n, --number NUMBER        Contract number (default: auto-generated)"
    echo "  -t, --title TITLE          Contract title (default: auto-generated)"
    echo "  -v, --value VALUE          Contract value (default: 1000000)"
    echo "  -s, --start-date DATE      Start date YYYY-MM-DD (default: today)"
    echo "  -e, --end-date DATE        End date YYYY-MM-DD (default: +1 year)"
    echo "  -a, --agency-id ID         Agency ID (default: AGENCY001)"
    echo "  -g, --program-id ID        Program ID (default: PROG001)"
    echo "  -r, --provider-id ID       Provider ID (default: PROVIDER001)"
    echo "  -o, --org-id ID            Organization ID (default: ORG001)"
    echo "  -i, --epin EPIN            E-PIN (default: auto-generated)"
    echo "  -h, --help                 Show this help message"
    echo ""
    echo "Environment Variables:"
    echo "  API_BASE                   API base URL (default: http://localhost:8080/api/v1)"
    echo "  USERNAME                   Default username"
    echo "  PASSWORD                   Default password"
    echo ""
    echo "Examples:"
    echo "  $0                                    # Create contract with defaults"
    echo "  $0 -n CONTRACT-001 -t 'Test Contract' # Create with specific number and title"
    echo "  $0 -u acco_staff -v 500000            # Create as acco_staff with value 500000"
    exit 1
}

# Parse command line arguments
CONTRACT_NUMBER=""
CONTRACT_TITLE=""
CONTRACT_VALUE="1000000"
START_DATE=""
END_DATE=""
AGENCY_ID="AGENCY001"
PROGRAM_ID="PROG001"
PROVIDER_ID="PROVIDER001"
ORG_ID="ORG001"
EPIN=""

while [[ $# -gt 0 ]]; do
    case $1 in
        -u|--username)
            USERNAME="$2"
            shift 2
            ;;
        -p|--password)
            PASSWORD="$2"
            shift 2
            ;;
        -n|--number)
            CONTRACT_NUMBER="$2"
            shift 2
            ;;
        -t|--title)
            CONTRACT_TITLE="$2"
            shift 2
            ;;
        -v|--value)
            CONTRACT_VALUE="$2"
            shift 2
            ;;
        -s|--start-date)
            START_DATE="$2"
            shift 2
            ;;
        -e|--end-date)
            END_DATE="$2"
            shift 2
            ;;
        -a|--agency-id)
            AGENCY_ID="$2"
            shift 2
            ;;
        -g|--program-id)
            PROGRAM_ID="$2"
            shift 2
            ;;
        -r|--provider-id)
            PROVIDER_ID="$2"
            shift 2
            ;;
        -o|--org-id)
            ORG_ID="$2"
            shift 2
            ;;
        -i|--epin)
            EPIN="$2"
            shift 2
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            usage
            ;;
    esac
done

# Generate defaults if not provided
TIMESTAMP=$(date +%s)
if [ -z "$CONTRACT_NUMBER" ]; then
    CONTRACT_NUMBER="CONTRACT-${TIMESTAMP}"
fi
if [ -z "$CONTRACT_TITLE" ]; then
    CONTRACT_TITLE="Contract Created via Script - ${TIMESTAMP}"
fi
if [ -z "$START_DATE" ]; then
    START_DATE=$(date -u +"%Y-%m-%d")
fi
if [ -z "$END_DATE" ]; then
    # Try different date commands for different OS
    END_DATE=$(date -u -v+1y +"%Y-%m-%d" 2>/dev/null || date -u -d "+1 year" +"%Y-%m-%d" 2>/dev/null || date -u +"%Y-%m-%d")
fi
if [ -z "$EPIN" ]; then
    EPIN="EPIN-${TIMESTAMP}"
fi

echo -e "${BLUE}🔄 Creating Contract via API${NC}"
echo "=================================="
echo ""
echo -e "${YELLOW}Configuration:${NC}"
echo "  API Base: ${API_BASE}"
echo "  Username: ${USERNAME}"
echo "  Contract Number: ${CONTRACT_NUMBER}"
echo "  Contract Title: ${CONTRACT_TITLE}"
echo "  Contract Value: ${CONTRACT_VALUE}"
echo "  Start Date: ${START_DATE}"
echo "  End Date: ${END_DATE}"
echo "  Agency ID: ${AGENCY_ID}"
echo "  Provider ID: ${PROVIDER_ID}"
echo "  Organization ID: ${ORG_ID}"
echo "  E-PIN: ${EPIN}"
echo ""

# Step 1: Login
echo -e "${BLUE}Step 1: Authenticating...${NC}"
LOGIN_RESP=$(curl -s -X POST "${API_BASE}/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"${USERNAME}\",\"password\":\"${PASSWORD}\",\"provider\":\"keycloak\"}")

TOKEN=$(echo "$LOGIN_RESP" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('accessToken', ''))" 2>/dev/null | tr -d '\n\r ')
USER_ID=$(echo "$LOGIN_RESP" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('user', {}).get('id', ''))" 2>/dev/null | tr -d '\n\r ')

if [ -z "$TOKEN" ] || [ "$TOKEN" == "None" ] || [ "$TOKEN" == "" ]; then
    echo -e "${RED}❌ Authentication failed!${NC}"
    echo "Response: ${LOGIN_RESP:0:200}"
    exit 1
fi
echo -e "${GREEN}✅ Authenticated as ${USERNAME} (User ID: ${USER_ID})${NC}"
echo ""

# Step 2: Create Contract
echo -e "${BLUE}Step 2: Creating contract...${NC}"
PAYLOAD=$(cat <<EOF
{
  "contractNumber": "${CONTRACT_NUMBER}",
  "contractTitle": "${CONTRACT_TITLE}",
  "contractValue": ${CONTRACT_VALUE},
  "contractAmount": ${CONTRACT_VALUE},
  "contractStartDate": "${START_DATE}",
  "contractEndDate": "${END_DATE}",
  "agencyId": "${AGENCY_ID}",
  "programId": "${PROGRAM_ID}",
  "providerId": "${PROVIDER_ID}",
  "organizationId": "${ORG_ID}",
  "ePin": "${EPIN}"
}
EOF
)

CREATE_RESP=$(curl -s -w "\n%{http_code}" -X POST "${API_BASE}/contracts" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "X-User-Id: ${USER_ID}" \
    -d "${PAYLOAD}")

HTTP_CODE=$(echo "$CREATE_RESP" | tail -1)
BODY=$(echo "$CREATE_RESP" | sed '$d')

if [ "$HTTP_CODE" == "201" ] || [ "$HTTP_CODE" == "200" ]; then
    CONTRACT_ID=$(echo "$BODY" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)
    WORKFLOW_INSTANCE_KEY=$(echo "$BODY" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('configurationWorkflowInstanceKey', ''))" 2>/dev/null)
    
    echo -e "${GREEN}✅ Contract created successfully!${NC}"
    echo ""
    echo -e "${GREEN}📋 Contract Details:${NC}"
    echo "  Contract ID: ${CONTRACT_ID}"
    echo "  Contract Number: ${CONTRACT_NUMBER}"
    echo "  Contract Title: ${CONTRACT_TITLE}"
    echo "  Contract Value: \$${CONTRACT_VALUE}"
    echo "  Workflow Instance Key: ${WORKFLOW_INSTANCE_KEY}"
    echo ""
    echo -e "${GREEN}🔗 View Contract:${NC}"
    echo "  Frontend: http://localhost:3000/contracts/${CONTRACT_ID}"
    echo "  API: ${API_BASE}/contracts/${CONTRACT_ID}"
    echo ""
    if [ -n "$WORKFLOW_INSTANCE_KEY" ] && [ "$WORKFLOW_INSTANCE_KEY" != "None" ] && [ "$WORKFLOW_INSTANCE_KEY" != "" ]; then
        echo -e "${GREEN}✅ Workflow (WF302) launched automatically!${NC}"
        echo "  View in Operate: http://localhost:8081/operate/2/process-instances/${WORKFLOW_INSTANCE_KEY}"
    else
        echo -e "${YELLOW}⚠️  Workflow instance key not returned (workflow may still be launching)${NC}"
    fi
    echo ""
    exit 0
else
    echo -e "${RED}❌ Failed to create contract (HTTP ${HTTP_CODE})${NC}"
    echo "Response: ${BODY:0:500}"
    exit 1
fi



