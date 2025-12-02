# Quick Start Guide

## Step 1: Install AWS CLI

```bash
brew install awscli
```

## Step 2: Configure AWS Credentials

Your organization uses Azure AD for AWS access. Run this script:

```bash
cd Infra/scripts
./configure-aws-azure.sh
```

The script will:
- Open your Azure SSO portal in the browser
- Guide you to get AWS credentials
- Help you configure AWS CLI

**Follow the instructions in the browser to:**
1. Sign in with your Microsoft account
2. Click on AWS application
3. Click "Command line or programmatic access"
4. Copy the credentials and paste in terminal

See [AWS_SETUP_GUIDE.md](../AWS_SETUP_GUIDE.md) for detailed instructions.

## Step 3: Deploy Infrastructure

```bash
cd Infra/scripts
./setup.sh
```

This will take about 10-15 minutes.

## Step 4: Create SSH Tunnels

Open a new terminal and run:

```bash
cd Infra/scripts
./tunnel.sh
```

Keep this terminal open!

## Step 5: Access Services

Open your browser:

- **Keycloak**: http://localhost:8081 (admin/admin123)
- **Camunda Operate**: http://localhost:8080 (demo/demo)
- **Camunda Tasklist**: http://localhost:8082 (demo/demo)

## Troubleshooting

### Check if services are running:
```bash
cd Infra/scripts
./status.sh
```

### Connect to the server:
```bash
cd Infra/scripts
./connect.sh
```

### View logs:
```bash
# After connecting
cd /opt/camunda-keycloak
docker-compose logs -f
```

## Cleanup

To destroy all resources:
```bash
cd Infra/scripts
./destroy.sh
```

## Cost

Approximately **$33/month** for a t3.medium instance running 24/7.

To reduce costs, stop the instance when not in use:
```bash
aws ec2 stop-instances --instance-ids $(cd Infra/terraform && terraform output -raw instance_id)
```

To start it again:
```bash
aws ec2 start-instances --instance-ids $(cd Infra/terraform && terraform output -raw instance_id)
```
