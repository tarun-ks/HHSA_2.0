#!/bin/bash
set -e

echo "=== Destroying Camunda & Keycloak Infrastructure ==="
echo ""
echo "WARNING: This will destroy all resources and data!"
echo ""

read -p "Are you sure you want to destroy the infrastructure? (yes/no): " confirm
if [ "$confirm" != "yes" ]; then
    echo "Destruction cancelled."
    exit 0
fi

cd "$(dirname "$0")/../terraform"

echo "Destroying infrastructure..."
terraform destroy

echo ""
echo "Infrastructure destroyed successfully."
