output "vpc_id" {
  description = "ID of the dedicated dev VPC."
  value       = aws_vpc.dev.id
}

output "public_subnet_ids" {
  description = "IDs of the public subnets reserved for ALB-facing resources."
  value       = sort([for subnet in values(aws_subnet.public) : subnet.id])
}

output "private_subnet_ids" {
  description = "IDs of the private subnets reserved for ECS-facing resources."
  value       = sort([for subnet in values(aws_subnet.private) : subnet.id])
}

output "route_table_ids" {
  description = "Route-table IDs that downstream stacks should consume by role."

  value = {
    public  = aws_route_table.public.id
    private = aws_route_table.private.id
  }
}

output "network_name_map" {
  description = "Human-readable network names for downstream documentation and tagging."

  value = {
    vpc = local.vpc_name
    public_subnets = {
      for az, subnet in local.public_subnets : az => subnet.name
    }
    private_subnets = {
      for az, subnet in local.private_subnets : az => subnet.name
    }
  }
}

output "alb_security_group_id" {
  description = "ID of the ALB security group for future listener and ALB wiring."
  value       = aws_security_group.alb.id
}

output "ecs_task_security_group_id" {
  description = "ID of the ECS task security group for future awsvpc service wiring."
  value       = aws_security_group.ecs_task.id
}

output "alb_dns_name" {
  description = "Approved v1 public endpoint identifier for the public ALB."
  value       = aws_lb.public.dns_name
}

output "alb_hosted_zone_id" {
  description = "Hosted zone ID for later Route 53 alias-record integration."
  value       = aws_lb.public.zone_id
}

output "alb_arn" {
  description = "ARN of the public ALB for downstream infrastructure wiring."
  value       = aws_lb.public.arn
}

output "alb_name" {
  description = "Human-readable name of the public ALB."
  value       = aws_lb.public.name
}

output "http_listener_arn" {
  description = "ARN of the public HTTP listener for downstream routing integrations."
  value       = aws_lb_listener.http.arn
}

output "application_target_group_arn" {
  description = "ARN of the ECS-compatible application target group."
  value       = aws_lb_target_group.application.arn
}

output "application_target_group_name" {
  description = "Human-readable name of the ECS-compatible application target group."
  value       = aws_lb_target_group.application.name
}

output "repository_uri" {
  description = "Deterministic ECR repository URI for CI image pushes."
  value       = aws_ecr_repository.app.repository_url
}

output "repository_name" {
  description = "Deterministic ECR repository name for CI and ECS references."
  value       = aws_ecr_repository.app.name
}

output "ecs_cluster_arn" {
  description = "ARN of the shared ECS cluster for later service attachment."
  value       = aws_ecs_cluster.shared.arn
}

output "ecs_cluster_name" {
  description = "Human-readable name of the shared ECS cluster."
  value       = aws_ecs_cluster.shared.name
}

output "application_log_group_name" {
  description = "CloudWatch log-group name reserved for ECS application logs."
  value       = aws_cloudwatch_log_group.application.name
}

output "ecs_task_execution_role_arn" {
  description = "ARN of the ECS task execution role for image pulls and log publishing."
  value       = aws_iam_role.ecs_task_execution.arn
}

output "ecs_task_role_arn" {
  description = "ARN of the ECS task role reserved for future application AWS access."
  value       = aws_iam_role.ecs_task.arn
}

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

output "baseline_ecs_service_name" {
  description = "Stable ECS service name for later rollout automation."
  value       = aws_ecs_service.application.name
}

output "baseline_ecs_service_arn" {
  description = "Stable ECS service identifier for later rollout automation."
  value       = aws_ecs_service.application.id
}

output "baseline_task_definition_family" {
  description = "Stable ECS task-definition family for later rollout automation."
  value       = aws_ecs_task_definition.application.family
}

output "baseline_task_definition_arn" {
  description = "Stable baseline ECS task-definition identifier for later rollout automation."
  value       = aws_ecs_task_definition.application.arn
}
