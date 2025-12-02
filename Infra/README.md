# AWS Infrastructure Setup for Camunda 8 and Keycloak

## Overview
This setup deploys Camunda 8 and Keycloak on a single AWS EC2 instance to minimize costs while maintaining secure access from your local machine.

## Architecture
- **Single EC2 Instance**: t3.medium (2 vCPU, 4GB RAM) - ~$30/month
- **Docker Compose**: Running both services in containers
- **Security**: SSH tunneling for secure local access
- **Storage**: 30GB EBS volume

## Cost Optimization
- Single instance for both services
- On-demand pricing (can switch to Reserved Instance for 40% savings)
- Automatic shutdown scripts for non-business hours (optional)

## Prerequisites
1. AWS CLI configured with your credentials
2. SSH key pair for EC2 access
3. Docker and Docker Compose knowledge

## Deployment Steps

### 1. Configure AWS Credentials
```bash
# Authenticate via your Microsoft SSO link
# Then configure AWS CLI
aws configure
```

### 2. Deploy Infrastructure
```bash
cd Infra
terraform init
terraform plan
terraform apply
```

### 3. Access Services Locally
```bash
# SSH tunnel for Camunda (port 8080)
ssh -i ~/.ssh/your-key.pem -L 8080:localhost:8080 ec2-user@<EC2_PUBLIC_IP>

# SSH tunnel for Keycloak (port 8081)
ssh -i ~/.ssh/your-key.pem -L 8081:localhost:8081 ec2-user@<EC2_PUBLIC_IP>
```

### 4. Access URLs
- Camunda: http://localhost:8080
- Keycloak: http://localhost:8081

## Security Features
- Security Group with restricted access (only your IP)
- SSH key-based authentication
- Services bound to localhost (not exposed publicly)
- SSH tunneling for secure access
- IAM roles with minimal permissions

## Maintenance
- Logs: `docker-compose logs -f`
- Restart: `docker-compose restart`
- Stop: `docker-compose down`
- Start: `docker-compose up -d`
