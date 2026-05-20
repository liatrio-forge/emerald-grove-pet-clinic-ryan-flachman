terraform {
  backend "s3" {}
}

data "aws_iam_policy_document" "github_actions_oidc_trust" {
  for_each = local.github_actions_subjects

  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]

    principals {
      type        = "Federated"
      identifiers = [aws_iam_openid_connect_provider.github_actions.arn]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:sub"
      values   = [each.value]
    }
  }
}

resource "aws_iam_openid_connect_provider" "github_actions" {
  url             = "https://token.actions.githubusercontent.com"
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = local.github_oidc_thumbprints

  tags = merge(local.common_tags, {
    Name = "github-actions-oidc"
    Role = "github-oidc"
  })
}

resource "aws_iam_policy" "terraform_github_actions" {
  name = "${var.environment}-terraform-github-actions"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "TerraformStateAndPlanning"
        Effect = "Allow"
        Action = [
          "cloudwatch:*",
          "ec2:*",
          "ecr:*",
          "ecs:*",
          "elasticloadbalancing:*",
          "iam:AttachRolePolicy",
          "iam:CreateRole",
          "iam:DeleteRole",
          "iam:DeleteRolePolicy",
          "iam:DetachRolePolicy",
          "iam:GetOpenIDConnectProvider",
          "iam:GetPolicy",
          "iam:GetPolicyVersion",
          "iam:GetRole",
          "iam:GetRolePolicy",
          "iam:ListAttachedRolePolicies",
          "iam:ListInstanceProfilesForRole",
          "iam:ListOpenIDConnectProviders",
          "iam:ListPolicyVersions",
          "iam:ListRolePolicies",
          "iam:PassRole",
          "iam:PutRolePolicy",
          "iam:TagOpenIDConnectProvider",
          "iam:TagPolicy",
          "iam:TagRole",
          "iam:UntagOpenIDConnectProvider",
          "iam:UntagPolicy",
          "iam:UntagRole",
          "iam:UpdateAssumeRolePolicy",
          "iam:UpdateOpenIDConnectProviderThumbprint",
          "logs:*",
          "route53:*",
          "s3:*",
        ]
        Resource = "*"
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name = "${var.environment}-terraform-github-actions"
    Role = "terraform-github-actions"
  })
}

resource "aws_iam_role" "terraform_apply_github_actions" {
  name               = local.terraform_apply_role_name
  assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust["terraform_apply"].json

  tags = merge(local.common_tags, {
    Name = local.terraform_apply_role_name
    Role = "terraform-apply"
  })
}

resource "aws_iam_role" "terraform_destroy_github_actions" {
  name               = local.terraform_destroy_role_name
  assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust["terraform_destroy"].json

  tags = merge(local.common_tags, {
    Name = local.terraform_destroy_role_name
    Role = "terraform-destroy"
  })
}

resource "aws_iam_role_policy_attachment" "terraform_apply_github_actions" {
  role       = aws_iam_role.terraform_apply_github_actions.name
  policy_arn = aws_iam_policy.terraform_github_actions.arn
}

resource "aws_iam_role_policy_attachment" "terraform_destroy_github_actions" {
  role       = aws_iam_role.terraform_destroy_github_actions.name
  policy_arn = aws_iam_policy.terraform_github_actions.arn
}

resource "aws_iam_policy" "app_deploy_github_actions" {
  name = "${var.environment}-app-deploy-github-actions"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "EcsDeploymentPath"
        Effect = "Allow"
        Action = [
          "ecs:DescribeServices",
          "ecs:DescribeTaskDefinition",
          "ecs:RegisterTaskDefinition",
          "ecs:UpdateService",
          "ecs:ListTasks",
          "ecr:BatchGetImage",
          "ecr:DescribeImages",
          "ecr:DescribeRepositories",
          "iam:GetRole",
          "iam:PassRole",
          "logs:DescribeLogGroups",
          "logs:DescribeLogStreams",
        ]
        Resource = "*"
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name = "${var.environment}-app-deploy-github-actions"
    Role = "app-deploy-github-actions"
  })
}

resource "aws_iam_role" "app_deploy_github_actions" {
  name               = local.app_deploy_role_name
  assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust["app_deploy"].json

  tags = merge(local.common_tags, {
    Name = local.app_deploy_role_name
    Role = "app-deploy"
  })
}

resource "aws_iam_role_policy_attachment" "app_deploy_github_actions" {
  role       = aws_iam_role.app_deploy_github_actions.name
  policy_arn = aws_iam_policy.app_deploy_github_actions.arn
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

resource "aws_ecs_task_definition" "application" {
  family                   = local.ecs_task_definition_family
  cpu                      = "1024"
  memory                   = "2048"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn
  task_role_arn            = aws_iam_role.ecs_task.arn

  container_definitions = jsonencode([
    {
      name      = "application"
      image     = var.bootstrap_image
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.application.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = local.ecs_task_log_stream_prefix
        }
      }
    }
  ])

  tags = merge(local.common_tags, {
    Name = local.ecs_task_definition_family
    Role = "application-runtime"
  })
}

resource "aws_ecs_service" "application" {
  name                               = local.ecs_service_name
  cluster                            = aws_ecs_cluster.shared.id
  task_definition                    = aws_ecs_task_definition.application.arn
  desired_count                      = 1
  launch_type                        = "FARGATE"
  health_check_grace_period_seconds  = 120
  deployment_minimum_healthy_percent = 0
  deployment_maximum_percent         = 100

  network_configuration {
    subnets          = sort([for subnet in values(aws_subnet.private) : subnet.id])
    security_groups  = [aws_security_group.ecs_task.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.application.arn
    container_name   = "application"
    container_port   = 8080
  }

  tags = merge(local.common_tags, {
    Name = local.ecs_service_name
    Role = "application-service"
  })
}
