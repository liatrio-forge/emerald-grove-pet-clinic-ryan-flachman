locals {
  public_subnet_indexes = {
    for index, az in var.availability_zones : az => index
  }

  private_subnet_indexes = {
    for index, az in var.availability_zones : az => index + 8
  }

  public_subnets = {
    for az, index in local.public_subnet_indexes : az => {
      availability_zone = az
      cidr_block        = cidrsubnet(var.vpc_cidr, 4, index)
      name              = "${var.environment}-public-${az}"
    }
  }

  private_subnets = {
    for az, index in local.private_subnet_indexes : az => {
      availability_zone = az
      cidr_block        = cidrsubnet(var.vpc_cidr, 4, index)
      name              = "${var.environment}-private-${az}"
    }
  }

  vpc_name                     = "${var.environment}-vpc"
  public_alb_name              = "${var.environment}-public-http"
  application_target_group_name = "${var.environment}-application"
  alb_security_group_name      = "${var.environment}-application-load-balancer"
  ecs_task_security_group_name = "${var.environment}-ecs-task"
  ecr_repository_name          = "${var.environment}-petclinic"

  common_tags = {
    Application = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
    Stack       = "app-dev-network"
  }
}
