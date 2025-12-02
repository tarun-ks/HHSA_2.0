# Deployment Guide: Camunda 8 & Keycloak on AWS

## Prerequisites

1. **AWS Account Access**
   - Authenticate via: https://myapps.microsoft.com/signin/113614_574894348494_ACP_AWS_APP/...
   - Complete browser authentication

2. **Local Tools**
   ```bash
   # Install Terraform
   brew install terraform  # macOS
   
   # Install AWS CLI
   brew install awscli  # macOS
   ```

3. **Configure AWS CLI**
   ```bash
   aws configure
   # Enter your AWS Access Key ID
   # Enter your AWS Secret Access Key
   # Default region: us-east-1
   # Default output format: json
   ```

## Quick Start

### 1. Deploy Infrastructure

```bash
cd Infra/scripts
chmod +x *.sh
./setup.sh
```

This will:
- Create VPC, subnet, and security groups
- Launch EC2 instance (t3.medium)
- Install Docker and Docker Compose
- Deploy Camunda 8 and Keycloak containers
- Configure automatic startup

**Expected time**: 10-15 minutes

### 2. Create SSH Tunnels

Open a new terminal and run:

```bash
cd Infra/scripts
./tunnel.sh
```

Keep this terminal open. The tunnels will forward:
- Port 8080 → Camunda Operate
- Port 8081 → Keycloak
- Port 8082 → Camunda Tasklist

### 3. Access Services

Open your browser:

- **Keycloak Admin Console**: http://localhost:8081
  - Username: `admin`
  - Password: `admin123` (or what you set in variables.tf)

- **Camunda Operate**: http://localhost:8080
  - Default credentials: `demo/demo`

- **Camunda Tasklist**: http://localhost:8082
  - Default credentials: `demo/demo`

## Architecture

```
┌─────────────────────────────────────────────────┐
│                  AWS EC2 Instance               │
│                  (t3.medium)                    │
│                                                 │
│  ┌──────────────┐  ┌──────────────┐           │
│  │  Keycloak    │  │  Camunda 8   │           │
│  │  :8081       │  │  Operate     │           │
│  │              │  │  :8080       │           │
│  └──────────────┘  └──────────────┘           │
│                                                 │
│  ┌──────────────┐  ┌──────────────┐           │
│  │  PostgreSQL  │  │  Camunda     │           │
│  │              │  │  Tasklist    │           │
│  │              │  │  :8082       │           │
│  └──────────────┘  └──────────────┘           │
│                                                 │
│  ┌──────────────┐  ┌──────────────┐           │
│  │  Zeebe       │  │  Elastic-    │           │
│  │  :26500      │  │  search      │           │
│  └──────────────┘  └──────────────┘           │
└─────────────────────────────────────────────────┘
                      │
                      │ SSH Tunnel
                      │
              ┌───────▼────────┐
              │  Your Local    │
              │  Machine       │
              │                │
              │  localhost:    │
              │  8080, 8081,   │
              │  8082          │
              └────────────────┘
```

## Security Features

1. **Network Security**
   - Security group restricts SSH to your IP only
   - Services bound to localhost (not publicly accessible)
   - All access via SSH tunnels

2. **Authentication**
   - SSH key-based authentication (no passwords)
   - Keycloak admin password configurable
   - IAM role with minimal permissions

3. **Data Protection**
   - All data stored in Docker volumes
   - PostgreSQL for Keycloak persistence
   - Elasticsearch for Camunda data

## Cost Breakdown

| Resource | Type | Monthly Cost |
|----------|------|--------------|
| EC2 Instance | t3.medium | ~$30 |
| EBS Storage | 30GB gp3 | ~$2.40 |
| Data Transfer | Minimal | ~$1 |
| **Total** | | **~$33/month** |

### Cost Optimization Tips

1. **Stop instance when not in use**:
   ```bash
   aws ec2 stop-instances --instance-ids $(cd Infra/terraform && terraform output -raw instance_id)
   ```

2. **Use Reserved Instance** (40% savings for 1-year commitment)

3. **Schedule automatic shutdown**:
   ```bash
   # Add to crontab on EC2 instance
   0 18 * * 1-5 /usr/local/bin/docker-compose -f /opt/camunda-keycloak/docker-compose.yml down
   0 8 * * 1-5 /usr/local/bin/docker-compose -f /opt/camunda-keycloak/docker-compose.yml up -d
   ```

## Management Commands

### Check Service Status
```bash
cd Infra/scripts
./status.sh
```

### Connect to EC2 Instance
```bash
cd Infra/scripts
./connect.sh
```

### View Logs
```bash
# After connecting to EC2
cd /opt/camunda-keycloak
docker-compose logs -f
```

### Restart Services
```bash
# After connecting to EC2
cd /opt/camunda-keycloak
docker-compose restart
```

### Destroy Infrastructure
```bash
cd Infra/scripts
./destroy.sh
```

## Troubleshooting

### Services not starting
```bash
# Connect to EC2 and check logs
./connect.sh
cd /opt/camunda-keycloak
docker-compose logs
```

### Cannot connect via SSH
- Verify your IP hasn't changed
- Update security group if needed:
  ```bash
  cd Infra/terraform
  terraform apply -auto-approve
  ```

### Tunnels not working
- Ensure SSH key permissions: `chmod 600 ~/.ssh/id_rsa`
- Check if ports are already in use locally
- Verify EC2 instance is running

### Out of memory
- Increase instance size in `variables.tf`:
  ```hcl
  instance_type = "t3.large"  # 2 vCPU, 8GB RAM
  ```
- Run `terraform apply`

## Integration with Your Application

### Configure Keycloak

1. Access Keycloak: http://localhost:8081
2. Create a new realm for your application
3. Create clients for your services
4. Configure users and roles

### Configure Camunda

1. Access Operate: http://localhost:8080
2. Deploy your BPMN processes
3. Configure connectors if needed
4. Update your application to connect to Zeebe:
   ```
   zeebe.client.broker.gateway-address=localhost:26500
   ```

### Update Your Application

In your `application.properties` or `application.yml`:

```yaml
# Keycloak Configuration
keycloak.auth-server-url=http://localhost:8081
keycloak.realm=your-realm
keycloak.resource=your-client-id

# Camunda Zeebe Configuration
zeebe.client.broker.gateway-address=localhost:26500
zeebe.client.security.plaintext=true
```

## Next Steps

1. Configure Keycloak realm and clients
2. Deploy your BPMN processes to Camunda
3. Integrate authentication with your backend services
4. Set up monitoring and alerting
5. Configure backups for production use

## Support

For issues or questions:
1. Check logs: `docker-compose logs`
2. Verify service status: `./status.sh`
3. Review AWS CloudWatch logs
4. Check Camunda documentation: https://docs.camunda.io
5. Check Keycloak documentation: https://www.keycloak.org/documentation
