output "terraform_apply_role_arn" {
  description = "ARN of the GitHub-assumable Terraform apply role for the protected dev environment."
  value       = aws_iam_role.terraform_apply_github_actions.arn
}

output "terraform_destroy_role_arn" {
  description = "ARN of the GitHub-assumable Terraform destroy role for the protected dev-destroy environment."
  value       = aws_iam_role.terraform_destroy_github_actions.arn
}

output "app_publish_role_arn" {
  description = "ARN of the GitHub-assumable ECR publish role for protected dev environment image publication."
  value       = aws_iam_role.app_publish_github_actions.arn
}

output "app_deploy_role_arn" {
  description = "ARN of the GitHub-assumable ECS deploy role for protected dev environment rollouts."
  value       = aws_iam_role.app_deploy_github_actions.arn
}
