# Quick Reference Card

## 📍 Your URLs

### Keycloak
```
URL: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com
Username: admin
Password: admin123
```

### Camunda Operate
```
URL: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/operate
Username: demo
Password: demo
```

### Camunda Tasklist
```
URL: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/tasklist
Username: demo
Password: demo
```

### Zeebe (Workflow Engine)
```
⚠️ Internal only: localhost:26500 (gRPC)
Accessible from within ECS task network
```

**Need external Zeebe access?** I can add a Network Load Balancer (+$16/month)

---

## 🔄 Refresh AWS Credentials (When Expired)

```bash
# 1. Go to AWS Console
# 2. Click AWS application in your SSO portal
# 3. Click "Command line or programmatic access"
# 4. Copy and paste the export commands:

export AWS_ACCESS_KEY_ID=ASIA...
export AWS_SECRET_ACCESS_KEY=...
export AWS_SESSION_TOKEN=...
```

---

## ✅ Check Service Status

```bash
# Check if services are running
aws ecs describe-services \
  --cluster camunda-keycloak-cluster \
  --services keycloak-service camunda-service \
  --query 'services[].{Name:serviceName,Status:status,Running:runningCount,Desired:desiredCount}' \
  --output table
```

---

## 📋 View Logs

```bash
# Keycloak
aws logs tail /ecs/keycloak --follow

# Camunda Operate
aws logs tail /ecs/operate --follow

# Camunda Tasklist  
aws logs tail /ecs/tasklist --follow

# Zeebe
aws logs tail /ecs/zeebe --follow
```

---

## 🌐 AWS Console Links

**ECS Cluster:**
https://console.aws.amazon.com/ecs/v2/clusters/camunda-keycloak-cluster?region=us-east-1

**Load Balancer:**
https://console.aws.amazon.com/ec2/home?region=us-east-1#LoadBalancers:

**CloudWatch Logs:**
https://console.aws.amazon.com/cloudwatch/home?region=us-east-1#logsV2:log-groups

---

## 💰 Cost Management

**Current Cost:** ~$82/month (Keycloak + Camunda)

### Stop Services (Save Money)
```bash
# Stop Keycloak
aws ecs update-service --cluster camunda-keycloak-cluster \
  --service keycloak-service --desired-count 0

# Stop Camunda
aws ecs update-service --cluster camunda-keycloak-cluster \
  --service camunda-service --desired-count 0
```

### Start Services
```bash
# Start Keycloak
aws ecs update-service --cluster camunda-keycloak-cluster \
  --service keycloak-service --desired-count 1

# Start Camunda
aws ecs update-service --cluster camunda-keycloak-cluster \
  --service camunda-service --desired-count 1
```

---

## 🔧 Apply Pending Updates

If you haven't applied the Camunda updates yet:

```bash
# 1. Refresh AWS credentials (see above)

# 2. Apply updates
cd Infra/terraform-fargate
terraform apply -auto-approve

# Wait 10-15 minutes for services to start
```

---

## 🆘 Troubleshooting

### Services not responding?
```bash
# Check logs
aws logs tail /ecs/keycloak --follow

# Check task status
aws ecs list-tasks --cluster camunda-keycloak-cluster --service-name keycloak-service
```

### "HTTPS Required" error?
The fix is in pending updates. Apply them (see above).

### Can't access Zeebe?
Zeebe is internal-only by default. Let me know if you need external access.

---

## 📞 Need Help?

**Camunda not deployed yet?**
Run: `cd Infra/scripts && ./update-deployment.sh`

**Need Zeebe external access?**
Let me know and I'll add a Network Load Balancer.

**Want to destroy everything?**
Run: `cd Infra/scripts && ./destroy-fargate.sh`
