#!/bin/bash
set -e

echo "=== Camunda 8 & Keycloak on AWS Fargate ==="
echo ""
echo "This deployment uses ECS Fargate (serverless containers)"
echo "Estimated cost: ~$25-30/month"
echo ""

# Check prerequisites
command -v terraform >/dev/null 2>&1 || { echo "Error: terraform is required but not installed."; exit 1; }
command -v aws >/dev/null 2>&1 || { echo "Error: aws CLI is required but not installed."; exit 1; }

# Check AWS credentials
echo "Checking AWS credentials..."
aws sts get-caller-identity >/dev/null 2>&1 || {
    echo "Error: AWS credentials not configured."
    exit 1
}

echo "✓ AWS credentials configured"
echo ""

# Navigate to terraform directory
cd "$(dirname "$0")/../terraform-fargate"

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

echo "=== Deployment Complete ==="
echo ""
terraform output -raw access_instructions
echo ""

echo "Note: Services may take 5-10 minutes to fully start."
echo "You can check status with:"
echo "  cd Infra/scripts"
echo "  ./status-fargate.sh"
