# AWS ECS Fargate Deployment Guide

## Overview

This deployment uses **AWS ECS Fargate** - a serverless container platform that doesn't require EC2 instances. Perfect for bypassing EC2 restrictions!

## Architecture

```
┌─────────────────────────────────────────────┐
│         Application Load Balancer           │
│              (Your IP Only)                 │
│         http://[alb-dns-name]              │
└──────────────┬──────────────────────────────┘
               │
               │ Routes traffic
               │
┌──────────────▼──────────────────────────────┐
│         ECS Fargate Service                 │
│                                             │
│  ┌──────────────────────────────────────┐  │
│  │  Keycloak Container                  │  │
│  │  - 0.5 vCPU, 1GB RAM                │  │
│  │  - Port 8081                        │  │
│  │  - Auto-scaling ready               │  │
│  └──────────────────────────────────────┘  │
│                                             │
└─────────────────────────────────────────────┘
```

## Cost Breakdown (Low Cost Configuration)

| Resource | Specification | Monthly Cost |
|----------|--------------|--------------|
| Fargate vCPU | 0.5 vCPU × 730 hours | ~$18 |
| Fargate Memory | 1GB × 730 hours | ~$4 |
| Application Load Balancer | 1 ALB | ~$16 |
| Data Transfer | Minimal | ~$1 |
| **Total** | | **~$39/month** |

### Cost Optimization Tips

1. **Stop when not in use**:
   ```bash
   # Scale to 0 (stop)
   aws ecs update-service --cluster camunda-keycloak-cluster \
     --service keycloak-service --desired-count 0
   
   # Scale to 1 (start)
   aws ecs update-service --cluster camunda-keycloak-cluster \
     --service keycloak-service --desired-count 1
   ```

2. **Use Fargate Spot** (50-70% savings):
   - Modify task definition to use FARGATE_SPOT capacity provider
   - Good for dev/test environments

3. **Reduce resources**:
   - Current: 0.5 vCPU, 1GB RAM
   - Minimum: 0.25 vCPU, 512MB RAM (~$10/month savings)

## Quick Start

### 1. Deploy Infrastructure

```bash
cd Infra/scripts
./deploy-fargate.sh
```

This will take about 10-15 minutes.

### 2. Access Keycloak

After deployment completes, you'll get a URL like:
```
http://camunda-keycloak-alb-123456789.us-east-1.elb.amazonaws.com
```

Open it in your browser:
- **Username**: admin
- **Password**: admin123

### 3. Check Status

```bash
cd Infra/scripts
./status-fargate.sh
```

## Management Commands

### View Logs

```bash
# Real-time logs
aws logs tail /ecs/keycloak --follow

# Last 100 lines
aws logs tail /ecs/keycloak --since 1h
```

### Restart Service

```bash
aws ecs update-service \
  --cluster camunda-keycloak-cluster \
  --service keycloak-service \
  --force-new-deployment
```

### Scale Service

```bash
# Scale up to 2 instances
aws ecs update-service \
  --cluster camunda-keycloak-cluster \
  --service keycloak-service \
  --desired-count 2

# Scale down to 0 (stop)
aws ecs update-service \
  --cluster camunda-keycloak-cluster \
  --service keycloak-service \
  --desired-count 0
```

### Destroy Infrastructure

```bash
cd Infra/scripts
./destroy-fargate.sh
```

## Security Features

1. **Network Security**
   - ALB only accepts traffic from your IP
   - ECS tasks in private networking
   - Security groups restrict access

2. **No SSH Access Needed**
   - Serverless - no servers to manage
   - Access via AWS ECS Exec if needed

3. **IAM Roles**
   - Minimal permissions for task execution
   - CloudWatch logging enabled

## Advantages Over EC2

✅ **No EC2 Permissions Required** - Bypasses SCP restrictions
✅ **Serverless** - No server management
✅ **Auto-scaling** - Can scale based on load
✅ **Pay per use** - Only pay for running time
✅ **Integrated monitoring** - CloudWatch logs included
✅ **High availability** - Runs across multiple AZs

## Limitations

⚠️ **Cold starts** - Takes 2-3 minutes to start from stopped state
⚠️ **Persistent data** - No built-in persistence (use RDS/EFS if needed)
⚠️ **Cost for 24/7** - More expensive than EC2 for always-on workloads

## Troubleshooting

### Service won't start

Check logs:
```bash
aws logs tail /ecs/keycloak --follow
```

Check task status:
```bash
aws ecs describe-tasks \
  --cluster camunda-keycloak-cluster \
  --tasks $(aws ecs list-tasks --cluster camunda-keycloak-cluster --service-name keycloak-service --query 'taskArns[0]' --output text)
```

### Can't access ALB URL

1. Verify your IP hasn't changed:
   ```bash
   curl https://api.ipify.org
   ```

2. Update security group if needed:
   ```bash
   cd Infra/terraform-fargate
   terraform apply -auto-approve
   ```

### Service is unhealthy

Wait 5-10 minutes for initial startup. Keycloak takes time to initialize.

Check health:
```bash
aws elbv2 describe-target-health \
  --target-group-arn $(aws elbv2 describe-target-groups --names keycloak-tg --query 'TargetGroups[0].TargetGroupArn' --output text)
```

## Integration with Your Application

### Update Application Configuration

In your `application.yml` or `application.properties`:

```yaml
# Keycloak Configuration
keycloak.auth-server-url=http://[your-alb-dns-name]
keycloak.realm=your-realm
keycloak.resource=your-client-id
```

### For Production

Consider:
1. **Use HTTPS** - Add ACM certificate to ALB
2. **Use RDS** - For Keycloak database persistence
3. **Use EFS** - For shared file storage
4. **Enable auto-scaling** - Based on CPU/memory
5. **Add CloudWatch alarms** - For monitoring

## Next Steps

1. ✅ Deploy infrastructure
2. ✅ Access Keycloak
3. Configure Keycloak realm and clients
4. Update your application to use Keycloak
5. Set up monitoring and alerts
6. Consider production enhancements

## Support

For issues:
1. Check CloudWatch logs
2. Verify service status
3. Review security group rules
4. Check task definition
