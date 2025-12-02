#!/bin/bash
set -e

cd "$(dirname "$0")/../terraform"

# Get instance IP
INSTANCE_IP=$(terraform output -raw instance_public_ip 2>/dev/null)

if [ -z "$INSTANCE_IP" ]; then
    echo "Error: Could not get instance IP. Is the infrastructure deployed?"
    exit 1
fi

echo "=== Creating SSH Tunnels ==="
echo "Instance IP: $INSTANCE_IP"
echo ""
echo "This will create tunnels for:"
echo "  - Camunda Operate: http://localhost:8080"
echo "  - Keycloak: http://localhost:8081"
echo "  - Camunda Tasklist: http://localhost:8082"
echo ""
echo "Press Ctrl+C to stop tunnels"
echo ""

# Create SSH tunnels
ssh -i ~/.ssh/id_rsa \
    -L 8080:localhost:8080 \
    -L 8081:localhost:8081 \
    -L 8082:localhost:8082 \
    -N ec2-user@$INSTANCE_IP
