#!/bin/bash
set -e

echo "=== AWS Configuration for Azure AD SSO ==="
echo ""

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed."
    echo "Please run: brew install awscli"
    exit 1
fi

echo "Your organization uses Azure AD for AWS access."
echo ""
echo "Step 1: Authenticate via browser"
echo "Opening your SSO portal..."
echo ""

# Open the SSO URL
open "https://myapps.microsoft.com/signin/113614_574894348494_ACP_AWS_APP/b89459e5-03d1-4c8b-931b-4d9b3bb6e0b3?tenantId=e0793d39-0939-496d-b129-198edd916feb"

echo "✓ Browser opened"
echo ""
echo "Step 2: Get your AWS credentials"
echo ""
echo "After logging in:"
echo "1. Click on the AWS application"
echo "2. You'll see your AWS accounts"
echo "3. Click on 'Command line or programmatic access'"
echo "4. Copy the credentials shown"
echo ""
echo "Step 3: Configure AWS CLI"
echo ""
echo "You have two options:"
echo ""
echo "Option A - Temporary credentials (expires in hours):"
echo "  Copy and paste the export commands from the portal"
echo "  Example:"
echo "    export AWS_ACCESS_KEY_ID=ASIA..."
echo "    export AWS_SECRET_ACCESS_KEY=..."
echo "    export AWS_SESSION_TOKEN=..."
echo ""
echo "Option B - Configure credentials file:"
echo "  1. Copy the credentials from the portal"
echo "  2. Run: nano ~/.aws/credentials"
echo "  3. Paste the credentials under [default]"
echo ""
echo "Press Enter when you've completed the authentication..."
read

# Create basic config if it doesn't exist
mkdir -p ~/.aws
if [ ! -f ~/.aws/config ]; then
    cat > ~/.aws/config <<EOF
[default]
region = us-east-1
output = json
EOF
    echo "✓ Created ~/.aws/config"
fi

echo ""
echo "Testing AWS connection..."
if aws sts get-caller-identity &>/dev/null; then
    echo "✓ AWS CLI configured successfully!"
    echo ""
    aws sts get-caller-identity
    echo ""
    echo "You're ready to deploy!"
else
    echo "⚠ AWS credentials not detected yet."
    echo ""
    echo "Please set your credentials using one of these methods:"
    echo ""
    echo "Method 1 - Environment variables (temporary):"
    echo "  export AWS_ACCESS_KEY_ID=your_access_key"
    echo "  export AWS_SECRET_ACCESS_KEY=your_secret_key"
    echo "  export AWS_SESSION_TOKEN=your_session_token"
    echo ""
    echo "Method 2 - Credentials file (persistent):"
    echo "  aws configure"
    echo "  # Then enter your Access Key ID and Secret Access Key"
    echo ""
fi
