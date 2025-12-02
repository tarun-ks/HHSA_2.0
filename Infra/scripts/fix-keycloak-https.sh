#!/bin/bash
set -e

echo "=== Fix Keycloak HTTPS Issue ==="
echo ""

# Check AWS credentials
if ! aws sts get-caller-identity &>/dev/null; then
    echo "❌ AWS credentials expired"
    echo ""
    echo "Please refresh your credentials:"
    echo "1. Go to AWS Console"
    echo "2. Click 'Command line or programmatic access'"
    echo "3. Copy the export commands"
    echo "4. Paste them here"
    echo ""
    echo "Then run this script again."
    exit 1
fi

echo "✓ AWS credentials valid"
echo ""

cd "$(dirname "$0")/../terraform-fargate"

echo "Applying Keycloak fix..."
echo "This will:"
echo "  - Set proper hostname configuration"
echo "  - Disable HTTPS requirements completely"
echo "  - Configure proxy headers correctly"
echo ""

terraform apply -auto-approve

echo ""
echo "Forcing Keycloak to restart with new configuration..."
aws ecs update-service \
  --cluster camunda-keycloak-cluster \
  --service keycloak-service \
  --force-new-deployment \
  --query 'service.serviceName' \
  --output text

echo ""
echo "✅ Fix applied!"
echo ""
echo "Keycloak is restarting. Wait 3-5 minutes, then try:"
echo "  http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com"
echo ""
echo "Monitor progress:"
echo "  aws logs tail /ecs/keycloak --follow"
