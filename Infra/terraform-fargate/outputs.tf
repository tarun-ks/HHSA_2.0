output "alb_dns_name" {
  description = "DNS name of the Application Load Balancer"
  value       = aws_lb.main.dns_name
}

output "keycloak_url" {
  description = "URL to access Keycloak"
  value       = "http://${aws_lb.main.dns_name}"
}

output "camunda_operate_url" {
  description = "URL to access Camunda Operate"
  value       = "http://${aws_lb.main.dns_name}/operate"
}

output "camunda_tasklist_url" {
  description = "URL to access Camunda Tasklist"
  value       = "http://${aws_lb.main.dns_name}/tasklist"
}

output "aws_console_links" {
  description = "Links to view resources in AWS Console"
  value       = <<-EOT
    
    View in AWS Console:
    
    ECS Cluster:
    https://console.aws.amazon.com/ecs/v2/clusters/camunda-keycloak-cluster?region=${var.aws_region}
    
    Load Balancer:
    https://console.aws.amazon.com/ec2/home?region=${var.aws_region}#LoadBalancers:
    
    CloudWatch Logs:
    https://console.aws.amazon.com/cloudwatch/home?region=${var.aws_region}#logsV2:log-groups
    
  EOT
}

output "access_instructions" {
  description = "Instructions to access services"
  sensitive   = true
  value       = <<-EOT
    
    ========================================
    Deployment Complete!
    ========================================
    
    Keycloak URL: http://${aws_lb.main.dns_name}
    Username: admin
    Password: ${var.keycloak_admin_password}
    
    Camunda Operate: http://${aws_lb.main.dns_name}/operate
    Camunda Tasklist: http://${aws_lb.main.dns_name}/tasklist
    Default credentials: demo/demo
    
    Note: It may take 10-15 minutes for all services to be fully healthy.
    
    View in AWS Console:
    https://console.aws.amazon.com/ecs/v2/clusters/camunda-keycloak-cluster?region=${var.aws_region}
    
    To check service status:
      aws ecs describe-services --cluster camunda-keycloak-cluster --services keycloak-service
    
    To view logs:
      aws logs tail /ecs/keycloak --follow
    
    Estimated Monthly Cost: ~$25-30
    
    ========================================
  EOT
}

output "cluster_name" {
  description = "ECS Cluster name"
  value       = aws_ecs_cluster.main.name
}

output "service_name" {
  description = "ECS Service name"
  value       = aws_ecs_service.keycloak.name
}
