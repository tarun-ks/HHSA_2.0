#!/bin/bash

# Keycloak Stop Script

CONTAINER_NAME="keycloak-hhsa"

echo "🛑 Stopping Keycloak..."
echo "======================"

if podman ps --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    podman stop ${CONTAINER_NAME}
    echo "✅ Keycloak stopped"
else
    echo "ℹ️  Keycloak is not running"
fi

