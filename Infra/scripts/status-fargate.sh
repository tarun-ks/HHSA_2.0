#!/bin/bash
set -e

cd "$(dirname "$0")/../terraform-fargate"

# Get cluster and service names
CLUSTER_NAME=$(terraform output -raw cluster_name 2>/dev/null)
SERVICE_NAME=$(terraform output -raw service_name 2>/dev/null)
ALB_DNS=$(terraform output -raw alb_dns_name 2>/dev/null)

if [ -z "$CLUSTER_NAME" ]; then
    echo "Error: Could not get cluster name. Is the infrastructure deployed?"
    exit 1
fi

echo "=== ECS Fargate Service Status ==="
echo ""
echo "Cluster: $CLUSTER_NAME"
echo "Service: $SERVICE_NAME"
echo "Keycloak URL: http://$ALB_DNS"
echo ""

# Get service status
echo "Service Status:"
aws ecs describe-services \
    --cluster "$CLUSTER_NAME" \
    --services "$SERVICE_NAME" \
    --query 'services[0].{Status:status,Running:runningCount,Desired:desiredCount,Pending:pendingCount}' \
    --output table

echo ""
echo "Recent Tasks:"
aws ecs list-tasks \
    --cluster "$CLUSTER_NAME" \
    --service-name "$SERVICE_NAME" \
    --query 'taskArns[0]' \
    --output text | xargs -I {} aws ecs describe-tasks \
    --cluster "$CLUSTER_NAME" \
    --tasks {} \
    --query 'tasks[0].{TaskId:taskArn,Status:lastStatus,Health:healthStatus,Started:startedAt}' \
    --output table 2>/dev/null || echo "No tasks found"

echo ""
echo "To view logs:"
echo "  aws logs tail /ecs/keycloak --follow"
