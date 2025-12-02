#!/bin/bash
set -e

cd "$(dirname "$0")/../terraform"

# Get instance IP
INSTANCE_IP=$(terraform output -raw instance_public_ip 2>/dev/null)

if [ -z "$INSTANCE_IP" ]; then
    echo "Error: Could not get instance IP. Is the infrastructure deployed?"
    exit 1
fi

echo "=== Connecting to Camunda & Keycloak Server ==="
echo "Instance IP: $INSTANCE_IP"
echo ""

# Check if SSH key exists
if [ ! -f ~/.ssh/id_rsa ]; then
    echo "Error: SSH key not found at ~/.ssh/id_rsa"
    exit 1
fi

# Connect via SSH
ssh -i ~/.ssh/id_rsa ec2-user@$INSTANCE_IP
