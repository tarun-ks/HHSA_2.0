output "instance_public_ip" {
  description = "Public IP of the EC2 instance"
  value       = aws_instance.camunda_keycloak.public_ip
}

output "instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.camunda_keycloak.id
}

output "ssh_command" {
  description = "SSH command to connect to the instance"
  value       = "ssh -i ~/.ssh/id_rsa ec2-user@${aws_instance.camunda_keycloak.public_ip}"
}

output "camunda_tunnel_command" {
  description = "SSH tunnel command for Camunda"
  value       = "ssh -i ~/.ssh/id_rsa -L 8080:localhost:8080 -N ec2-user@${aws_instance.camunda_keycloak.public_ip}"
}

output "keycloak_tunnel_command" {
  description = "SSH tunnel command for Keycloak"
  value       = "ssh -i ~/.ssh/id_rsa -L 8081:localhost:8081 -N ec2-user@${aws_instance.camunda_keycloak.public_ip}"
}

output "access_instructions" {
  description = "Instructions to access services"
  value       = <<-EOT
    1. Create SSH tunnels:
       Terminal 1: ssh -i ~/.ssh/id_rsa -L 8080:localhost:8080 -N ec2-user@${aws_instance.camunda_keycloak.public_ip}
       Terminal 2: ssh -i ~/.ssh/id_rsa -L 8081:localhost:8081 -N ec2-user@${aws_instance.camunda_keycloak.public_ip}
    
    2. Access services:
       Camunda: http://localhost:8080
       Keycloak: http://localhost:8081
  EOT
}
