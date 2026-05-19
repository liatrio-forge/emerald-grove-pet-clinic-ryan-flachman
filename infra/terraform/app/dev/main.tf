terraform {
  backend "s3" {}
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

resource "aws_vpc_security_group_ingress_rule" "alb_listener_ipv4" {
  security_group_id = aws_security_group.alb.id
  description       = "Allow public IPv4 traffic to the ALB listener port."
  cidr_ipv4   = "0.0.0.0/0"
  from_port   = var.alb_listener_port
  ip_protocol = "tcp"
  to_port     = var.alb_listener_port
}

resource "aws_vpc_security_group_ingress_rule" "alb_listener_ipv6" {
  security_group_id = aws_security_group.alb.id
  description       = "Allow public IPv6 traffic to the ALB listener port."
  cidr_ipv6   = "::/0"
  from_port   = var.alb_listener_port
  ip_protocol = "tcp"
  to_port     = var.alb_listener_port
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
  cidr_ipv4   = "0.0.0.0/0"
  ip_protocol = "-1"
}

resource "aws_vpc_security_group_egress_rule" "ecs_task_ipv6_egress" {
  security_group_id = aws_security_group.ecs_task.id
  description       = "Keep ECS task IPv6 egress open in v1 for future compatibility."
  cidr_ipv6   = "::/0"
  ip_protocol = "-1"
}
