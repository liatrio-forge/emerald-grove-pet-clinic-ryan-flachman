locals {
  github_oidc_provider_url = "https://token.actions.githubusercontent.com"
  github_repository        = "liatrio-forge/emerald-grove-pet-clinic-ryan-flachman"

  github_actions_subjects = {
    terraform_apply   = "repo:${local.github_repository}:environment:dev"
    terraform_destroy = "repo:${local.github_repository}:environment:dev-destroy"
    app_publish       = "repo:${local.github_repository}:environment:dev"
    app_deploy        = "repo:${local.github_repository}:environment:dev"
  }

  terraform_apply_role_name   = "terraform-apply-${var.environment}"
  terraform_destroy_role_name = "terraform-destroy-${var.environment}"
  app_publish_role_name       = "app-publish-${var.environment}"
  app_deploy_role_name        = "app-deploy-${var.environment}"

  common_tags = {
    Application = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
    Stack       = "identity-dev"
  }
}
