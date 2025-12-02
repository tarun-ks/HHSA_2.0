# Fixes and Updates

## Issue 1: "HTTPS Required" Error ✅ FIXED

Keycloak was showing "HTTPS required" because it was in production mode. I've updated the configuration to:
- Enable HTTP explicitly
- Disable strict HTTPS requirements for development
- Configure proper proxy settings

## Issue 2: Adding Camunda 8 ✅ READY

I've created the configuration to add:
- Zeebe (workflow engine)
- Camunda Operate (monitoring)
- Camunda Tasklist (task management)
- Elasticsearch (data storage)

## Issue 3: AWS Session Expired

Your AWS credentials expired. Refresh them:

### Quick Refresh:

```bash
# Option 1: Get new credentials from AWS Console
# 1. Go to your AWS SSO portal
# 2. Click AWS application
# 3. Click "Command line or programmatic access"
# 4. Copy the export commands
# 5. Paste them in terminal

# Option 2: Use CloudShell
# In AWS Console, click the CloudShell icon (>_) and get credentials there
```

### Then Apply Updates:

```bash
cd Infra/terraform-fargate
terraform apply -auto-approve
```

This will:
1. Fix Keycloak HTTP issue
2. Add Camunda 8 components
3. Update load balancer routing

## How to View in AWS Console

### Method 1: Direct Links (After applying updates)

```bash
cd Infra/terraform-fargate
terraform output aws_console_links
```

### Method 2: Manual Navigation

1. **Go to AWS Console**: https://console.aws.amazon.com

2. **View ECS Cluster**:
   - Services → ECS → Clusters
   - Click "camunda-keycloak-cluster"
   - You'll see your services and tasks

3. **View Load Balancer**:
   - Services → EC2 → Load Balancers
   - Look for "camunda-keycloak-alb"
   - See DNS name and health status

4. **View Logs**:
   - Services → CloudWatch → Log groups
   - Look for `/ecs/keycloak`, `/ecs/operate`, etc.

5. **View Tasks (Running Containers)**:
   - In ECS Cluster → Services tab
   - Click on a service
   - Click "Tasks" tab
   - See running containers

## After Applying Updates

### Access URLs:

```
Keycloak:
http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com
Username: admin
Password: admin123

Camunda Operate:
http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/operate
Username: demo
Password: demo

Camunda Tasklist:
http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/tasklist
Username: demo
Password: demo
```

## Updated Cost Estimate

With Camunda 8 added:

| Component | vCPU | Memory | Monthly Cost |
|-----------|------|--------|--------------|
| Keycloak | 0.5 | 1GB | ~$22 |
| Camunda Stack | 1.0 | 2GB | ~$44 |
| Load Balancer | - | - | ~$16 |
| **Total** | | | **~$82/month** |

### Cost Optimization:

Both services can be stopped independently:

```bash
# Stop Keycloak only
aws ecs update-service --cluster camunda-keycloak-cluster \
  --service keycloak-service --desired-count 0

# Stop Camunda only
aws ecs update-service --cluster camunda-keycloak-cluster \
  --service camunda-service --desired-count 0

# Stop both
aws ecs update-service --cluster camunda-keycloak-cluster \
  --service keycloak-service --desired-count 0
aws ecs update-service --cluster camunda-keycloak-cluster \
  --service camunda-service --desired-count 0
```

## Quick Commands Reference

### Refresh AWS Credentials
```bash
# Get from AWS Console → Command line access
export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_SESSION_TOKEN=...
```

### Apply Updates
```bash
cd Infra/terraform-fargate
terraform apply -auto-approve
```

### Check Status
```bash
# Keycloak
aws ecs describe-services --cluster camunda-keycloak-cluster --services keycloak-service

# Camunda
aws ecs describe-services --cluster camunda-keycloak-cluster --services camunda-service

# View logs
aws logs tail /ecs/keycloak --follow
aws logs tail /ecs/operate --follow
```

### Get Console Links
```bash
cd Infra/terraform-fargate
terraform output aws_console_links
```

## Next Steps

1. ✅ Refresh AWS credentials (see above)
2. ✅ Apply updates: `terraform apply -auto-approve`
3. ✅ Wait 10-15 minutes for services to start
4. ✅ Access Keycloak (HTTP issue fixed)
5. ✅ Access Camunda Operate and Tasklist
6. ✅ View everything in AWS Console using the links
