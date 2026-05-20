locals {
  github_oidc_provider_url      = "token.actions.githubusercontent.com"
  github_oidc_provider_arn_path = "oidc-provider/${local.github_oidc_provider_url}"
  github_repository             = "liatrio-forge/emerald-grove-pet-clinic-ryan-flachman"
  github_oidc_audience          = "sts.amazonaws.com"
  github_oidc_thumbprints       = ["6938fd4d98bab03faadb97b34396831e3780aea1"]

  github_actions_subjects = {
    terraform_apply   = "repo:${local.github_repository}:environment:dev"
    terraform_destroy = "repo:${local.github_repository}:environment:dev-destroy"
    app_deploy        = "repo:${local.github_repository}:environment:dev"
  }

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

  vpc_name                      = "${var.environment}-vpc"
  public_alb_name               = "${var.environment}-public-http"
  application_target_group_name = "${var.environment}-application"
  application_log_group_name    = "/aws/ecs/${var.environment}-application"
  ecs_task_definition_family    = "${var.environment}-petclinic"
  ecs_task_log_stream_prefix    = "${var.environment}-petclinic"
  ecs_service_name              = "${var.environment}-petclinic"
  alb_security_group_name       = "${var.environment}-application-load-balancer"
  ecs_task_security_group_name  = "${var.environment}-ecs-task"
  ecs_cluster_name              = "${var.environment}-shared"
  ecs_task_execution_role_name  = "${var.environment}-ecs-task-execution"
  ecs_task_role_name            = "${var.environment}-ecs-task"
  ecr_repository_name           = "${var.environment}-petclinic"
  terraform_apply_role_name     = "terraform-apply-${var.environment}"
  terraform_destroy_role_name   = "terraform-destroy-${var.environment}"
  app_deploy_role_name          = "app-deploy-${var.environment}"

  common_tags = {
    Application = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
    Stack       = "app-dev-network"
  }
}
