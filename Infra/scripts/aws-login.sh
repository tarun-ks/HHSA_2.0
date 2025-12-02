#!/bin/bash
set -e

echo "=== AWS SSO Login ==="
echo ""

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed."
    echo "Please run: brew install awscli"
    exit 1
fi

# Check if config exists
if [ ! -f ~/.aws/config ]; then
    echo "AWS config not found. Running configuration..."
    ./configure-aws-sso.sh
    exit 0
fi

# Determine profile to use
PROFILE=${1:-default}

echo "Logging in with profile: $PROFILE"
echo "Your browser will open for authentication..."
echo ""

# Login
aws sso login --profile $PROFILE

echo ""
echo "✓ Login successful!"
echo ""
echo "To use this profile, run:"
echo "  export AWS_PROFILE=$PROFILE"
echo ""
echo "Or add to your ~/.zshrc:"
echo "  echo 'export AWS_PROFILE=$PROFILE' >> ~/.zshrc"
echo ""
