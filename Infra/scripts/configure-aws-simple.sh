#!/bin/bash
set -e

echo "=== Simple AWS SSO Configuration ==="
echo ""

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed."
    echo "Please run: brew install awscli"
    exit 1
fi

# Create AWS config directory
mkdir -p ~/.aws

# Create a simple SSO config
cat > ~/.aws/config <<'EOF'
[default]
region = us-east-1
output = json

[profile camunda-keycloak]
sso_session = my-sso
sso_account_id = YOUR_ACCOUNT_ID
sso_role_name = AdministratorAccess
region = us-east-1
output = json

[sso-session my-sso]
sso_start_url = https://myapps.microsoft.com/signin/113614_574894348494_ACP_AWS_APP/b89459e5-03d1-4c8b-931b-4d9b3bb6e0b3?tenantId=e0793d39-0939-496d-b129-198edd916feb
sso_region = us-east-1
sso_registration_scopes = sso:account:access
EOF

echo "✓ AWS config created"
echo ""
echo "IMPORTANT: You need to update ~/.aws/config with your AWS Account ID"
echo ""
echo "Steps:"
echo "1. Open your SSO URL in browser to find your Account ID"
echo "2. Edit ~/.aws/config and replace YOUR_ACCOUNT_ID with your actual account ID"
echo "3. Run: aws sso login --profile camunda-keycloak"
echo ""
echo "Opening your SSO URL now..."
sleep 2
open "https://myapps.microsoft.com/signin/113614_574894348494_ACP_AWS_APP/b89459e5-03d1-4c8b-931b-4d9b3bb6e0b3?tenantId=e0793d39-0939-496d-b129-198edd916feb"
