#!/bin/bash
set -e

echo "=== Update Deployment ==="
echo ""
echo "This will:"
echo "  1. Fix Keycloak HTTPS issue"
echo "  2. Add Camunda 8 (Operate, Tasklist, Zeebe)"
echo "  3. Update load balancer routing"
echo ""

# Check AWS credentials
echo "Checking AWS credentials..."
if ! aws sts get-caller-identity &>/dev/null; then
    echo ""
    echo "❌ AWS credentials expired or not configured"
    echo ""
    echo "Please refresh your credentials:"
    echo "1. Go to AWS Console"
    echo "2. Click your AWS application"
    echo "3. Click 'Command line or programmatic access'"
    echo "4. Copy the export commands"
    echo "5. Paste them in this terminal"
    echo ""
    echo "Then run this script again."
    exit 1
fi

echo "✓ AWS credentials valid"
echo ""

cd "$(dirname "$0")/../terraform-fargate"

echo "Applying updates..."
terraform apply -auto-approve

echo ""
echo "=== Update Complete! ==="
echo ""
echo "Services are starting. This will take 10-15 minutes."
echo ""
echo "Access URLs:"
terraform output keycloak_url
terraform output camunda_operate_url
terraform output camunda_tasklist_url
echo ""
echo "View in AWS Console:"
terraform output aws_console_links
echo ""
echo "Check status:"
echo "  aws ecs describe-services --cluster camunda-keycloak-cluster --services keycloak-service camunda-service"
