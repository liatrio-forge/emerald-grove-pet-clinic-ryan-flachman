variable "environment" {
  description = "The environment name for the identity infrastructure stack."
  type        = string
  default     = "dev"

  validation {
    condition     = var.environment == "dev"
    error_message = "The identity stack only supports the dev environment."
  }
}

variable "aws_region" {
  description = "AWS region for the dev identity infrastructure."
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Human-readable project name used in identity infrastructure names."
  type        = string
  default     = "emerald-grove-pet-clinic"
}
