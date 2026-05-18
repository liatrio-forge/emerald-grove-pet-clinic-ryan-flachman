variable "environment" {
  description = "The environment name for the state stack."
  type        = string
  default     = "dev"

  validation {
    condition     = var.environment == "dev"
    error_message = "The state stack only supports the dev environment."
  }
}

variable "aws_region" {
  description = "AWS region for the dev Terraform remote-state resources."
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Human-readable project name used in backend resource names."
  type        = string
  default     = "emerald-grove-pet-clinic"
}
