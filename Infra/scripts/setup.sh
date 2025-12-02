#!/bin/bash
set -e

echo "=== Camunda 8 & Keycloak AWS Setup ==="
echo ""

# Check prerequisites
command -v terraform >/dev/null 2>&1 || { echo "Error: terraform is required but not installed."; exit 1; }
command -v aws >/dev/null 2>&1 || { echo "Error: aws CLI is required but not installed."; exit 1; }

# Check AWS credentials
echo "Checking AWS credentials..."
aws sts get-caller-identity >/dev/null 2>&1 || {
    echo "Error: AWS credentials not configured."
    echo "Please authenticate via your SSO link and configure AWS CLI."
    exit 1
}

echo "✓ AWS credentials configured"
echo ""

# Generate SSH key if it doesn't exist
if [ ! -f ~/.ssh/id_rsa ]; then
    echo "Generating SSH key pair..."
    ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa -N ""
    echo "✓ SSH key generated"
else
    echo "✓ SSH key already exists"
fi
echo ""

# Navigate to terraform directory
cd "$(dirname "$0")/../terraform"

# Initialize Terraform
echo "Initializing Terraform..."
terraform init
echo ""

# Plan deployment
echo "Planning deployment..."
terraform plan -out=tfplan
echo ""

# Confirm deployment
read -p "Do you want to proceed with deployment? (yes/no): " confirm
if [ "$confirm" != "yes" ]; then
    echo "Deployment cancelled."
    exit 0
fi

# Apply deployment
echo "Deploying infrastructure..."
terraform apply tfplan
echo ""

# Get outputs
echo "=== Deployment Complete ==="
echo ""
terraform output -raw access_instructions
echo ""

echo "Note: Services may take 5-10 minutes to fully start."
echo "You can check status by SSHing to the instance and running:"
echo "  docker ps"
echo "  docker-compose logs -f"
