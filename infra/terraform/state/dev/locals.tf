locals {
  state_bucket_name = "${var.project_name}-${var.environment}-terraform-state"
  lock_table_name   = "${var.project_name}-${var.environment}-terraform-locks"

  common_tags = {
    Application = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
    Stack       = "terraform-state"
  }
}
