#!/bin/bash
set -e

echo "=== AWS SSO Configuration ==="
echo ""
echo "This will configure AWS CLI to use your organization's SSO."
echo ""

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed."
    echo "Please install it first:"
    echo "  brew install awscli"
    exit 1
fi

echo "AWS CLI version: $(aws --version)"
echo ""

# Configure AWS SSO
echo "Configuring AWS SSO..."
echo ""

# Create AWS config directory if it doesn't exist
mkdir -p ~/.aws

# Prompt for SSO configuration details
read -p "Enter SSO start URL (press Enter for default): " SSO_START_URL
SSO_START_URL=${SSO_START_URL:-"https://myapps.microsoft.com/signin/113614_574894348494_ACP_AWS_APP/b89459e5-03d1-4c8b-931b-4d9b3bb6e0b3?tenantId=e0793d39-0939-496d-b129-198edd916feb"}

read -p "Enter SSO region [us-east-1]: " SSO_REGION
SSO_REGION=${SSO_REGION:-us-east-1}

read -p "Enter AWS account ID (if known, or press Enter to skip): " ACCOUNT_ID

read -p "Enter role name [default: AdministratorAccess]: " ROLE_NAME
ROLE_NAME=${ROLE_NAME:-AdministratorAccess}

read -p "Enter default region [us-east-1]: " DEFAULT_REGION
DEFAULT_REGION=${DEFAULT_REGION:-us-east-1}

read -p "Enter profile name [default]: " PROFILE_NAME
PROFILE_NAME=${PROFILE_NAME:-default}

# Create AWS config file
cat > ~/.aws/config <<EOF
[profile $PROFILE_NAME]
sso_start_url = $SSO_START_URL
sso_region = $SSO_REGION
sso_account_id = $ACCOUNT_ID
sso_role_name = $ROLE_NAME
region = $DEFAULT_REGION
output = json
EOF

echo ""
echo "✓ AWS config file created at ~/.aws/config"
echo ""
echo "Now logging in via SSO..."
echo "Your browser will open automatically for authentication."
echo ""

# Login via SSO
aws sso login --profile $PROFILE_NAME

echo ""
echo "=== Configuration Complete ==="
echo ""
echo "Your AWS CLI is now configured!"
echo ""
echo "To use this profile, either:"
echo "  1. Set as default: export AWS_PROFILE=$PROFILE_NAME"
echo "  2. Use with commands: aws --profile $PROFILE_NAME <command>"
echo ""
echo "To login again in the future, run:"
echo "  aws sso login --profile $PROFILE_NAME"
echo ""
