output "state_bucket_name" {
  description = "Name of the S3 bucket that stores Terraform state for dev."
  value       = aws_s3_bucket.terraform_state.bucket
}

output "lock_table_name" {
  description = "Name of the DynamoDB table used for Terraform state locking."
  value       = aws_dynamodb_table.terraform_lock.name
}

output "aws_region" {
  description = "AWS region used by the dev state stack."
  value       = var.aws_region
}
