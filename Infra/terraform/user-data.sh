#!/bin/bash
set -e

# Update system
yum update -y

# Install Docker
yum install -y docker
systemctl start docker
systemctl enable docker
usermod -aG docker ec2-user

# Install Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Create application directory
mkdir -p /opt/camunda-keycloak
cd /opt/camunda-keycloak

# Create docker-compose.yml
cat > docker-compose.yml <<'EOF'
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: postgres
    environment:
      POSTGRES_DB: keycloak
      POSTGRES_USER: keycloak
      POSTGRES_PASSWORD: keycloak
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - camunda-network
    restart: unless-stopped

  keycloak:
    image: quay.io/keycloak/keycloak:23.0
    container_name: keycloak
    environment:
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: keycloak
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: ${keycloak_admin_password}
      KC_HTTP_PORT: 8081
      KC_HOSTNAME_STRICT: false
      KC_PROXY: edge
    command:
      - start-dev
      - --http-port=8081
    ports:
      - "127.0.0.1:8081:8081"
    depends_on:
      - postgres
    networks:
      - camunda-network
    restart: unless-stopped

  zeebe:
    image: camunda/zeebe:8.4.0
    container_name: zeebe
    environment:
      ZEEBE_BROKER_GATEWAY_SECURITY_ENABLED: false
      ZEEBE_BROKER_NETWORK_HOST: 0.0.0.0
    ports:
      - "127.0.0.1:26500:26500"
    volumes:
      - zeebe_data:/usr/local/zeebe/data
    networks:
      - camunda-network
    restart: unless-stopped

  operate:
    image: camunda/operate:8.4.0
    container_name: operate
    environment:
      CAMUNDA_OPERATE_ZEEBE_GATEWAYADDRESS: zeebe:26500
      CAMUNDA_OPERATE_ELASTICSEARCH_URL: http://elasticsearch:9200
      CAMUNDA_OPERATE_ZEEBEELASTICSEARCH_URL: http://elasticsearch:9200
      SERVER_PORT: 8080
    ports:
      - "127.0.0.1:8080:8080"
    depends_on:
      - zeebe
      - elasticsearch
    networks:
      - camunda-network
    restart: unless-stopped

  tasklist:
    image: camunda/tasklist:8.4.0
    container_name: tasklist
    environment:
      CAMUNDA_TASKLIST_ZEEBE_GATEWAYADDRESS: zeebe:26500
      CAMUNDA_TASKLIST_ELASTICSEARCH_URL: http://elasticsearch:9200
      CAMUNDA_TASKLIST_ZEEBEELASTICSEARCH_URL: http://elasticsearch:9200
      SERVER_PORT: 8082
    ports:
      - "127.0.0.1:8082:8082"
    depends_on:
      - zeebe
      - elasticsearch
    networks:
      - camunda-network
    restart: unless-stopped

  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.9.0
    container_name: elasticsearch
    environment:
      discovery.type: single-node
      xpack.security.enabled: false
      ES_JAVA_OPTS: "-Xms512m -Xmx512m"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
    networks:
      - camunda-network
    restart: unless-stopped

networks:
  camunda-network:
    driver: bridge

volumes:
  postgres_data:
  zeebe_data:
  elasticsearch_data:
EOF

# Set permissions
chown -R ec2-user:ec2-user /opt/camunda-keycloak

# Start services
docker-compose up -d

# Create a systemd service for auto-start
cat > /etc/systemd/system/camunda-keycloak.service <<'EOF'
[Unit]
Description=Camunda and Keycloak Services
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/opt/camunda-keycloak
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
User=root

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable camunda-keycloak.service

echo "Installation complete! Services are starting..."
