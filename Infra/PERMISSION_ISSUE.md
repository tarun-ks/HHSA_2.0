# AWS Permission Issue - EC2 Instance Creation Blocked

## Problem

Your AWS account has a **Service Control Policy (SCP)** that prevents creating EC2 instances. This is a common security restriction in enterprise AWS environments.

**Error Message:**
```
You are not authorized to perform this operation. 
User: arn:aws:sts::574894348494:assumed-role/Admin/tarun.o.sharma@accenture.com 
is not authorized to perform: ec2:RunInstances on resource: arn:aws:ec2:us-east-1:574894348494:volume/* 
with an explicit deny in a service control policy
```

## Solutions

### Option 1: Request EC2 Permissions (Recommended)

Contact your AWS administrator and request:

1. **Permission to create EC2 instances** in `us-east-1` region
2. **Instance type**: `t3.medium` (2 vCPU, 4GB RAM)
3. **Purpose**: Development environment for Camunda 8 and Keycloak
4. **Estimated cost**: ~$33/month

**Email template:**
```
Subject: Request EC2 Instance Creation Permission

Hi [AWS Admin],

I need permission to create EC2 instances for a development environment.

Requirements:
- Region: us-east-1
- Instance Type: t3.medium
- Purpose: Running Camunda 8 and Keycloak for workflow automation
- Estimated Cost: ~$33/month
- Duration: [Specify timeframe]

Current Error: Service Control Policy blocking ec2:RunInstances

Please let me know if you need any additional information.

Thanks,
[Your Name]
```

### Option 2: Use AWS ECS Fargate (Serverless)

Deploy using AWS ECS Fargate - no EC2 permissions needed!

**Pros:**
- No EC2 instance management
- Pay only for what you use
- Automatic scaling
- No SCP restrictions on Fargate

**Cons:**
- Slightly more complex setup
- May cost more for 24/7 usage

**I can create Terraform for this if you want.**

### Option 3: Use AWS Lightsail

AWS Lightsail is a simplified compute service that might not be blocked by your SCP.

**Pros:**
- Simple setup
- Fixed monthly pricing
- Often not restricted by SCPs

**Cons:**
- Less flexible than EC2
- Limited instance types

**Cost:** ~$20-40/month depending on size

### Option 4: Use Existing Infrastructure

If your organization already has:
- Kubernetes cluster (EKS)
- Docker Swarm
- Existing EC2 instances
- On-premise servers

We can deploy Camunda and Keycloak there using Docker Compose.

### Option 5: Local Development (Temporary)

While waiting for permissions, run locally:

```bash
# I can create a docker-compose.yml for local development
docker-compose up -d
```

This won't be accessible from AWS but works for development.

## Next Steps

**Which option would you like to pursue?**

1. **Request EC2 permissions** - I'll help you draft the request
2. **Try ECS Fargate** - I'll create the Terraform configuration
3. **Try AWS Lightsail** - I'll create the setup scripts
4. **Use existing infrastructure** - Tell me what you have available
5. **Run locally** - I'll create a local Docker Compose setup

Let me know and I'll proceed with that solution!
