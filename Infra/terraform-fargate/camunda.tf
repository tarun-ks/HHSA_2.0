# Camunda 8 Components

# CloudWatch Log Groups for Camunda
resource "aws_cloudwatch_log_group" "zeebe" {
  name              = "/ecs/zeebe"
  retention_in_days = 7

  tags = {
    Name = "zeebe-logs"
  }
}

resource "aws_cloudwatch_log_group" "operate" {
  name              = "/ecs/operate"
  retention_in_days = 7

  tags = {
    Name = "operate-logs"
  }
}

resource "aws_cloudwatch_log_group" "tasklist" {
  name              = "/ecs/tasklist"
  retention_in_days = 7

  tags = {
    Name = "tasklist-logs"
  }
}

resource "aws_cloudwatch_log_group" "elasticsearch" {
  name              = "/ecs/elasticsearch"
  retention_in_days = 7

  tags = {
    Name = "elasticsearch-logs"
  }
}

# ECS Task Definition - Camunda Stack
resource "aws_ecs_task_definition" "camunda" {
  family                   = "camunda-stack"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "1024"
  memory                   = "2048"
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([
    {
      name  = "zeebe"
      image = "camunda/zeebe:8.4.0"
      
      portMappings = [
        {
          containerPort = 26500
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "ZEEBE_BROKER_GATEWAY_SECURITY_ENABLED"
          value = "false"
        },
        {
          name  = "ZEEBE_BROKER_NETWORK_HOST"
          value = "0.0.0.0"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.zeebe.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "zeebe"
        }
      }
    },
    {
      name  = "elasticsearch"
      image = "docker.elastic.co/elasticsearch/elasticsearch:8.9.0"
      
      environment = [
        {
          name  = "discovery.type"
          value = "single-node"
        },
        {
          name  = "xpack.security.enabled"
          value = "false"
        },
        {
          name  = "ES_JAVA_OPTS"
          value = "-Xms256m -Xmx256m"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.elasticsearch.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "elasticsearch"
        }
      }
    },
    {
      name  = "operate"
      image = "camunda/operate:8.4.0"
      
      portMappings = [
        {
          containerPort = 8080
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "CAMUNDA_OPERATE_ZEEBE_GATEWAYADDRESS"
          value = "localhost:26500"
        },
        {
          name  = "CAMUNDA_OPERATE_ELASTICSEARCH_URL"
          value = "http://localhost:9200"
        },
        {
          name  = "CAMUNDA_OPERATE_ZEEBEELASTICSEARCH_URL"
          value = "http://localhost:9200"
        }
      ]

      dependsOn = [
        {
          containerName = "zeebe"
          condition     = "START"
        },
        {
          containerName = "elasticsearch"
          condition     = "START"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.operate.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "operate"
        }
      }
    },
    {
      name  = "tasklist"
      image = "camunda/tasklist:8.4.0"
      
      portMappings = [
        {
          containerPort = 8082
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "CAMUNDA_TASKLIST_ZEEBE_GATEWAYADDRESS"
          value = "localhost:26500"
        },
        {
          name  = "CAMUNDA_TASKLIST_ELASTICSEARCH_URL"
          value = "http://localhost:9200"
        },
        {
          name  = "CAMUNDA_TASKLIST_ZEEBEELASTICSEARCH_URL"
          value = "http://localhost:9200"
        }
      ]

      dependsOn = [
        {
          containerName = "zeebe"
          condition     = "START"
        },
        {
          containerName = "elasticsearch"
          condition     = "START"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.tasklist.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "tasklist"
        }
      }
    }
  ])

  tags = {
    Name = "camunda-stack-task"
  }
}

# Target Group for Camunda Operate
resource "aws_lb_target_group" "operate" {
  name        = "operate-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path                = "/actuator/health"
    healthy_threshold   = 2
    unhealthy_threshold = 10
    timeout             = 60
    interval            = 120
    matcher             = "200-399"
  }

  tags = {
    Name = "operate-tg"
  }
}

# Target Group for Camunda Tasklist
resource "aws_lb_target_group" "tasklist" {
  name        = "tasklist-tg"
  port        = 8082
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path                = "/actuator/health"
    healthy_threshold   = 2
    unhealthy_threshold = 10
    timeout             = 60
    interval            = 120
    matcher             = "200-399"
  }

  tags = {
    Name = "tasklist-tg"
  }
}

# ALB Listener Rules for Camunda
resource "aws_lb_listener_rule" "operate" {
  listener_arn = aws_lb_listener.keycloak.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.operate.arn
  }

  condition {
    path_pattern {
      values = ["/operate*", "/actuator*"]
    }
  }
}

resource "aws_lb_listener_rule" "tasklist" {
  listener_arn = aws_lb_listener.keycloak.arn
  priority     = 101

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.tasklist.arn
  }

  condition {
    path_pattern {
      values = ["/tasklist*"]
    }
  }
}

# ECS Service - Camunda
resource "aws_ecs_service" "camunda" {
  name            = "camunda-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.camunda.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [aws_subnet.public_a.id, aws_subnet.public_b.id]
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.operate.arn
    container_name   = "operate"
    container_port   = 8080
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.tasklist.arn
    container_name   = "tasklist"
    container_port   = 8082
  }

  depends_on = [
    aws_lb_listener.keycloak,
    aws_lb_listener_rule.operate,
    aws_lb_listener_rule.tasklist
  ]

  tags = {
    Name = "camunda-service"
  }
}
