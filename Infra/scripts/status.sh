#!/bin/bash
set -e

cd "$(dirname "$0")/../terraform"

# Get instance IP
INSTANCE_IP=$(terraform output -raw instance_public_ip 2>/dev/null)

if [ -z "$INSTANCE_IP" ]; then
    echo "Error: Could not get instance IP. Is the infrastructure deployed?"
    exit 1
fi

echo "=== Service Status ==="
echo "Instance IP: $INSTANCE_IP"
echo ""

# Check service status
ssh -i ~/.ssh/id_rsa ec2-user@$INSTANCE_IP << 'EOF'
cd /opt/camunda-keycloak
echo "Docker Containers:"
docker-compose ps
echo ""
echo "Service Health:"
echo -n "Keycloak: "
curl -s -o /dev/null -w "%{http_code}" http://localhost:8081 || echo "Not responding"
echo ""
echo -n "Camunda Operate: "
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080 || echo "Not responding"
echo ""
echo -n "Camunda Tasklist: "
curl -s -o /dev/null -w "%{http_code}" http://localhost:8082 || echo "Not responding"
echo ""
EOF
