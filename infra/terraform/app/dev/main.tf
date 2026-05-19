terraform {
  backend "s3" {}
}

resource "aws_ecs_cluster" "shared" {
  name = local.ecs_cluster_name

  tags = merge(local.common_tags, {
    Name = local.ecs_cluster_name
    Role = "shared-runtime"
  })
}

resource "aws_cloudwatch_log_group" "application" {
  name              = local.application_log_group_name
  retention_in_days = 7

  tags = merge(local.common_tags, {
    Name = local.application_log_group_name
    Role = "application-logs"
  })
}

resource "aws_iam_role" "ecs_task_execution" {
  name = local.ecs_task_execution_role_name

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name = local.ecs_task_execution_role_name
    Role = "ecs-task-execution"
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_task" {
  name = local.ecs_task_role_name

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name = local.ecs_task_role_name
    Role = "ecs-task-runtime"
  })
}

resource "aws_ecr_repository" "app" {
  name                 = local.ecr_repository_name
  image_tag_mutability = "IMMUTABLE"
  force_delete         = true

  tags = merge(local.common_tags, {
    Name           = local.ecr_repository_name
    RepositoryRole = "application-image"
  })
}

resource "aws_ecr_lifecycle_policy" "app" {
  repository = aws_ecr_repository.app.name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Expire untagged images automatically."
        selection = {
          tagStatus   = "untagged"
          countType   = "imageCountMoreThan"
          countNumber = 1
        }
        action = {
          type = "expire"
        }
      },
      {
        rulePriority = 2
        description  = "Retain the most recent 5 tagged Git SHA images."
        selection = {
          tagStatus   = "tagged"
          countType   = "imageCountMoreThan"
          countNumber = 5
        }
        action = {
          type = "expire"
        }
      }
    ]
  })
}

resource "aws_vpc" "dev" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = merge(local.common_tags, {
    Name = local.vpc_name
  })
}

resource "aws_subnet" "public" {
  for_each = local.public_subnets

  vpc_id                  = aws_vpc.dev.id
  availability_zone       = each.value.availability_zone
  cidr_block              = each.value.cidr_block
  map_public_ip_on_launch = true

  tags = merge(local.common_tags, {
    Name = each.value.name
    Role = "public"
  })
}

resource "aws_subnet" "private" {
  for_each = local.private_subnets

  vpc_id                  = aws_vpc.dev.id
  availability_zone       = each.value.availability_zone
  cidr_block              = each.value.cidr_block
  map_public_ip_on_launch = false

  tags = merge(local.common_tags, {
    Name = each.value.name
    Role = "private"
  })
}

resource "aws_internet_gateway" "dev" {
  vpc_id = aws_vpc.dev.id

  tags = merge(local.common_tags, {
    Name = "${var.environment}-igw"
  })
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.dev.id

  tags = merge(local.common_tags, {
    Name = "${var.environment}-public-routes"
    Role = "public"
  })
}

resource "aws_route" "public_default_ipv4" {
  route_table_id         = aws_route_table.public.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.dev.id
}

resource "aws_route_table_association" "public" {
  for_each = aws_subnet.public

  subnet_id      = each.value.id
  route_table_id = aws_route_table.public.id
}

resource "aws_eip" "nat" {
  domain = "vpc"

  tags = merge(local.common_tags, {
    Name = "${var.environment}-nat-eip"
  })
}

resource "aws_nat_gateway" "dev" {
  allocation_id = aws_eip.nat.id
  subnet_id     = aws_subnet.public[var.availability_zones[0]].id

  tags = merge(local.common_tags, {
    Name = "${var.environment}-nat"
  })

  depends_on = [aws_internet_gateway.dev]
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.dev.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.dev.id
  }

  tags = merge(local.common_tags, {
    Name = "${var.environment}-private-routes"
    Role = "private"
  })
}

resource "aws_route_table_association" "private" {
  for_each = aws_subnet.private

  subnet_id      = each.value.id
  route_table_id = aws_route_table.private.id
}

resource "aws_security_group" "alb" {
  name        = local.alb_security_group_name
  description = "Allows public listener access to the dev application load balancer."
  vpc_id      = aws_vpc.dev.id
  egress      = []

  tags = merge(local.common_tags, {
    Name = local.alb_security_group_name
    Role = "alb"
  })
}

resource "aws_lb" "public" {
  name               = local.public_alb_name
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = sort([for subnet in values(aws_subnet.public) : subnet.id])

  tags = merge(local.common_tags, {
    Name = local.public_alb_name
    Role = "public-entrypoint"
  })
}

resource "aws_lb_target_group" "application" {
  name        = local.application_target_group_name
  port        = 8080
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = aws_vpc.dev.id

  health_check {
    enabled             = true
    healthy_threshold   = 2
    interval            = 15
    matcher             = "200-299"
    path                = "/actuator/health"
    port                = "traffic-port"
    protocol            = "HTTP"
    timeout             = 5
    unhealthy_threshold = 3
  }

  tags = merge(local.common_tags, {
    Name = local.application_target_group_name
    Role = "application-routing"
  })
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.public.arn
  port              = var.alb_listener_port
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.application.arn
  }
}

resource "aws_vpc_security_group_ingress_rule" "alb_listener_ipv4" {
  security_group_id = aws_security_group.alb.id
  description       = "Allow public IPv4 traffic to the ALB listener port."
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = var.alb_listener_port
  ip_protocol       = "tcp"
  to_port           = var.alb_listener_port
}

resource "aws_vpc_security_group_ingress_rule" "alb_listener_ipv6" {
  security_group_id = aws_security_group.alb.id
  description       = "Allow public IPv6 traffic to the ALB listener port."
  cidr_ipv6         = "::/0"
  from_port         = var.alb_listener_port
  ip_protocol       = "tcp"
  to_port           = var.alb_listener_port
}

resource "aws_security_group" "ecs_task" {
  name        = local.ecs_task_security_group_name
  description = "Allows only ALB-originated application traffic to ECS tasks."
  vpc_id      = aws_vpc.dev.id

  tags = merge(local.common_tags, {
    Name = local.ecs_task_security_group_name
    Role = "ecs-task"
  })
}

resource "aws_vpc_security_group_ingress_rule" "ecs_task_from_alb" {
  security_group_id            = aws_security_group.ecs_task.id
  description                  = "Allow application traffic from the ALB security group."
  referenced_security_group_id = aws_security_group.alb.id
  from_port                    = 8080
  ip_protocol                  = "tcp"
  to_port                      = 8080
}

resource "aws_vpc_security_group_egress_rule" "alb_to_ecs_tasks" {
  security_group_id            = aws_security_group.alb.id
  description                  = "Allow the ALB to reach ECS tasks on the application and health-check port."
  referenced_security_group_id = aws_security_group.ecs_task.id
  from_port                    = 8080
  ip_protocol                  = "tcp"
  to_port                      = 8080
}

resource "aws_vpc_security_group_egress_rule" "ecs_task_ipv4_egress" {
  security_group_id = aws_security_group.ecs_task.id
  description       = "Keep ECS task egress open in v1 for NAT-backed dependencies."
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}

resource "aws_vpc_security_group_egress_rule" "ecs_task_ipv6_egress" {
  security_group_id = aws_security_group.ecs_task.id
  description       = "Keep ECS task IPv6 egress open in v1 for future compatibility."
  cidr_ipv6         = "::/0"
  ip_protocol       = "-1"
}
