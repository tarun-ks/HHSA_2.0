#!/bin/bash

# Keycloak Start Script
# Starts Keycloak container if it exists, or creates a new one
# Includes memory limits and restart policy to prevent OOM kills

CONTAINER_NAME="keycloak-hhsa"
KEYCLOAK_PORT="8090"
IMAGE="quay.io/keycloak/keycloak:latest"

echo "🔐 Starting Keycloak..."
echo "======================"

# Check if Podman machine is running
if ! podman ps > /dev/null 2>&1; then
    echo "⚠️  Podman machine not running. Starting it..."
    podman machine start
    sleep 5
fi

# Check if container exists
if podman ps -a --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    echo "✅ Container '${CONTAINER_NAME}' exists"
    
    # Check if container has memory limits (new configuration)
    MEMORY_LIMIT=$(podman inspect ${CONTAINER_NAME} --format '{{.HostConfig.Memory}}' 2>/dev/null)
    if [ "$MEMORY_LIMIT" = "0" ] || [ -z "$MEMORY_LIMIT" ]; then
        echo "⚠️  Container exists but doesn't have memory limits configured."
        echo "🔄 Removing old container and recreating with new configuration..."
        podman rm -f ${CONTAINER_NAME}
        # Will create new container below
    else
        # Container has memory limits, check if it's running
        if podman ps --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
            echo "✅ Keycloak is already running on port ${KEYCLOAK_PORT}"
            echo "   Access: http://localhost:${KEYCLOAK_PORT}/admin"
            exit 0
        else
            echo "🔄 Starting existing container..."
            podman start ${CONTAINER_NAME}
            
            echo "⏳ Waiting for Keycloak to be ready..."
            for i in {1..60}; do
                if curl -s http://localhost:${KEYCLOAK_PORT}/health/ready > /dev/null 2>&1; then
                    echo "✅ Keycloak is ready!"
                    break
                fi
                if [ $i -eq 60 ]; then
                    echo "⚠️  Keycloak is starting but not ready yet. Check logs: podman logs ${CONTAINER_NAME}"
                    exit 1
                fi
                echo "   Waiting... ($i/60)"
                sleep 3
            done
            
            echo ""
            echo "✅ Keycloak started successfully!"
            echo "   Access: http://localhost:${KEYCLOAK_PORT}/admin"
            echo "   Login: admin / admin"
            exit 0
        fi
    fi
fi

# Create new container (either doesn't exist or was removed above)
echo "📦 Creating new container with memory limits..."
    
podman run -d \
  --name ${CONTAINER_NAME} \
  -p ${KEYCLOAK_PORT}:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  --memory="1.5g" \
  --memory-reservation="512m" \
  --restart=unless-stopped \
  ${IMAGE} \
  start-dev

echo "⏳ Waiting for Keycloak to be ready (this may take 30-60 seconds)..."
for i in {1..60}; do
    if curl -s http://localhost:${KEYCLOAK_PORT}/health/ready > /dev/null 2>&1; then
        echo "✅ Keycloak is ready!"
        break
    fi
    if [ $i -eq 60 ]; then
        echo "⚠️  Keycloak is starting but not ready yet. Check logs: podman logs ${CONTAINER_NAME}"
        exit 1
    fi
    echo "   Waiting... ($i/60)"
    sleep 3
done

echo ""
echo "✅ Keycloak container created and started!"
echo "   Access: http://localhost:${KEYCLOAK_PORT}/admin"
echo "   Login: admin / admin"
echo ""
echo "⚠️  Note: You'll need to run setup-keycloak.sh to configure realm and users"

echo ""
echo "📋 Useful commands:"
echo "   Stop:  podman stop ${CONTAINER_NAME}"
echo "   Logs:  podman logs -f ${CONTAINER_NAME}"
echo "   Remove: podman rm -f ${CONTAINER_NAME}"
