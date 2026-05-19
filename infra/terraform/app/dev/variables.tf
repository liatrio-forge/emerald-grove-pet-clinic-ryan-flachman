variable "environment" {
  description = "The environment name for the app infrastructure stack."
  type        = string
  default     = "dev"

  validation {
    condition     = var.environment == "dev"
    error_message = "The app stack only supports the dev environment."
  }
}

variable "aws_region" {
  description = "AWS region for the dev application infrastructure."
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Human-readable project name used in app infrastructure names."
  type        = string
  default     = "emerald-grove-pet-clinic"
}

variable "vpc_cidr" {
  description = "CIDR block for the dev VPC."
  type        = string
  default     = "10.42.0.0/20"
}

variable "availability_zones" {
  description = "Availability Zones used for the dev VPC subnet topology."
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]

  validation {
    condition     = length(var.availability_zones) >= 2
    error_message = "Provide at least two Availability Zones for dev networking."
  }
}

variable "alb_listener_port" {
  description = "Internet-facing listener port reserved for the future ALB."
  type        = number
  default     = 80
}
