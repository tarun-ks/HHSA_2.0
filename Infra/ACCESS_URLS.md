# Access URLs and Connection Details

## 🌐 Current Deployment

**Load Balancer DNS**: `camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com`

---

## 🔐 Keycloak (Identity & Access Management)

### Web Console
- **URL**: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com
- **Username**: `admin`
- **Password**: `admin123`

### For Your Application
```yaml
keycloak:
  auth-server-url: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com
  realm: your-realm
  resource: your-client-id
```

---

## 🔄 Camunda 8 Components

### Camunda Operate (Workflow Monitoring)
- **URL**: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/operate
- **Username**: `demo`
- **Password**: `demo`
- **Purpose**: Monitor and troubleshoot workflow instances

### Camunda Tasklist (Task Management)
- **URL**: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/tasklist
- **Username**: `demo`
- **Password**: `demo`
- **Purpose**: Manage user tasks in workflows

### Zeebe (Workflow Engine) - gRPC API

⚠️ **Important**: Zeebe uses gRPC (not HTTP) on port 26500. It's currently only accessible within the ECS task network.

#### Current Status
Zeebe is running but **NOT publicly accessible** (for security). It's only accessible by:
- Camunda Operate (already connected)
- Camunda Tasklist (already connected)
- Other containers in the same ECS task

#### To Access Zeebe from Your Application

You have 3 options:

**Option 1: Deploy Your App in Same ECS Cluster (Recommended)**
```yaml
# Your application configuration
zeebe:
  client:
    broker:
      gateway-address: localhost:26500
    security:
      plaintext: true
```

**Option 2: Expose Zeebe via Network Load Balancer**
I can add a Network Load Balancer (NLB) to expose Zeebe publicly:
- Cost: +$16/month
- Security: Restricted to your IP
- Connection: Direct gRPC access

**Option 3: Use Port Forwarding (Development Only)**
```bash
# Get task ID
TASK_ID=$(aws ecs list-tasks --cluster camunda-keycloak-cluster --service-name camunda-service --query 'taskArns[0]' --output text | cut -d'/' -f3)

# Enable ECS Exec (one-time setup)
aws ecs update-service --cluster camunda-keycloak-cluster --service camunda-service --enable-execute-command

# Port forward (requires AWS Session Manager plugin)
aws ecs execute-command --cluster camunda-keycloak-cluster \
  --task $TASK_ID \
  --container zeebe \
  --interactive \
  --command "/bin/sh"
```

---

## 📊 Check Service Status

### Check if Camunda is Running
```bash
aws ecs describe-services \
  --cluster camunda-keycloak-cluster \
  --services camunda-service \
  --query 'services[0].{Status:status,Running:runningCount,Desired:desiredCount}'
```

### Check if Keycloak is Running
```bash
aws ecs describe-services \
  --cluster camunda-keycloak-cluster \
  --services keycloak-service \
  --query 'services[0].{Status:status,Running:runningCount,Desired:desiredCount}'
```

### View Logs
```bash
# Keycloak logs
aws logs tail /ecs/keycloak --follow

# Camunda Operate logs
aws logs tail /ecs/operate --follow

# Camunda Tasklist logs
aws logs tail /ecs/tasklist --follow

# Zeebe logs
aws logs tail /ecs/zeebe --follow
```

---

## 🔌 Connection Examples

### Java Application (Spring Boot)
```yaml
# application.yml
zeebe:
  client:
    broker:
      gateway-address: localhost:26500  # If deployed in same ECS task
    security:
      plaintext: true

camunda:
  operate:
    url: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/operate
  tasklist:
    url: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/tasklist
```

### Node.js Application
```javascript
const { ZBClient } = require('zeebe-node');

const zbc = new ZBClient({
  hostname: 'localhost',  // If in same ECS task
  port: 26500,
  useTLS: false
});
```

### Python Application
```python
from pyzeebe import ZeebeClient

client = ZeebeClient(
    hostname='localhost',  # If in same ECS task
    port=26500,
    secure_connection=False
)
```

---

## 🚀 Next Steps

### 1. Verify Services are Running
```bash
# Check both services
aws ecs describe-services \
  --cluster camunda-keycloak-cluster \
  --services keycloak-service camunda-service
```

### 2. Access Web Interfaces
- Open Keycloak: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com
- Open Operate: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/operate
- Open Tasklist: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/tasklist

### 3. For Zeebe Access

**Do you need to access Zeebe from outside AWS?**

If YES, I can:
- Add a Network Load Balancer for Zeebe
- Expose port 26500 (restricted to your IP)
- Cost: +$16/month

Let me know and I'll add it!

---

## 🔍 AWS Console Links

### ECS Cluster
https://console.aws.amazon.com/ecs/v2/clusters/camunda-keycloak-cluster?region=us-east-1

### Load Balancer
https://console.aws.amazon.com/ec2/home?region=us-east-1#LoadBalancers:

### CloudWatch Logs
https://console.aws.amazon.com/cloudwatch/home?region=us-east-1#logsV2:log-groups

---

## 📝 Summary

| Service | URL | Credentials | Port | Protocol |
|---------|-----|-------------|------|----------|
| Keycloak | http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com | admin/admin123 | 80 | HTTP |
| Operate | http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/operate | demo/demo | 80 | HTTP |
| Tasklist | http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/tasklist | demo/demo | 80 | HTTP |
| Zeebe | localhost:26500 (internal) | N/A | 26500 | gRPC |

---

## ❓ Questions?

**Need public Zeebe access?** Let me know and I'll add a Network Load Balancer.

**Services not responding?** Check logs:
```bash
aws logs tail /ecs/keycloak --follow
aws logs tail /ecs/operate --follow
```

**Want to stop services to save money?**
```bash
# Stop all
aws ecs update-service --cluster camunda-keycloak-cluster --service keycloak-service --desired-count 0
aws ecs update-service --cluster camunda-keycloak-cluster --service camunda-service --desired-count 0
```
