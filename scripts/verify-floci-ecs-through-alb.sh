#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="infra/terraform/floci/docker-compose.yml"
APP_DIR="infra/terraform/app/dev"
BACKEND_CONFIG="backend.hcl.example"
APP_MAIN_TERRAFORM_FILE="${APP_DIR}/main.tf"
APP_VERSIONS_FILE="${APP_DIR}/versions.tf"
APP_VARIABLES_FILE="${APP_DIR}/variables.tf"
APP_MAIN_BACKUP_FILE=""
APP_VERSIONS_BACKUP_FILE=""
LOCALSTACK_ENDPOINT="http://127.0.0.1:4566"
STATE_BUCKET="emerald-grove-pet-clinic-dev-terraform-state"
LOCK_TABLE="emerald-grove-pet-clinic-dev-terraform-locks"
DEPLOY_IMAGE_TAG="floci-baseline"

# Verification contract:
# terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure
# terraform -chdir=infra/terraform/app/dev validate
# terraform -chdir=infra/terraform/app/dev apply -auto-approve
# terraform -chdir=infra/terraform/app/dev destroy -auto-approve

cleanup() {
  set +e
  destroy_stack_if_present
  restore_provider_stub
  restore_backend_stub
  docker compose -f "${ROOT_DIR}/${COMPOSE_FILE}" down
  rm -rf "${ROOT_DIR}/${APP_DIR}/.terraform"
}

require_file() {
  local path="$1"

  if [[ ! -f "${ROOT_DIR}/${path}" ]]; then
    echo "missing required file: ${path}" >&2
    exit 1
  fi
}

require_deploy_image_contract() {
  if ! grep -q 'variable "deploy_image"' "${ROOT_DIR}/${APP_VARIABLES_FILE}"; then
    echo "deploy_image variable contract is required" >&2
    exit 1
  fi

  if ! grep -q '@sha256:' "${ROOT_DIR}/${APP_VARIABLES_FILE}"; then
    echo "deploy_image must stay pinned to an immutable digest when provided" >&2
    exit 1
  fi
}

local_aws() {
  AWS_ACCESS_KEY_ID=test \
    AWS_SECRET_ACCESS_KEY=test \
    AWS_DEFAULT_REGION=us-east-1 \
    AWS_EC2_METADATA_DISABLED=true \
    aws --endpoint-url="${LOCALSTACK_ENDPOINT}" "$@"
}

local_terraform() {
  AWS_ACCESS_KEY_ID=test \
    AWS_SECRET_ACCESS_KEY=test \
    AWS_DEFAULT_REGION=us-east-1 \
    AWS_EC2_METADATA_DISABLED=true \
    terraform "$@"
}

create_bucket_if_missing() {
  if local_aws s3api head-bucket --bucket "${STATE_BUCKET}" >/dev/null 2>&1; then
    return
  fi

  local_aws s3api create-bucket --bucket "${STATE_BUCKET}" >/dev/null
}

create_lock_table_if_missing() {
  if local_aws dynamodb describe-table --table-name "${LOCK_TABLE}" >/dev/null 2>&1; then
    return
  fi

  local_aws dynamodb create-table \
    --table-name "${LOCK_TABLE}" \
    --attribute-definitions AttributeName=LockID,AttributeType=S \
    --key-schema AttributeName=LockID,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST >/dev/null
}

materialize_backend_config_for_verification() {
  APP_MAIN_BACKUP_FILE="$(mktemp "${TMPDIR:-/tmp}/floci-ecs-main.XXXXXX")"
  cp "${ROOT_DIR}/${APP_MAIN_TERRAFORM_FILE}" "${APP_MAIN_BACKUP_FILE}"

  python3 - <<'PY' "${ROOT_DIR}/${APP_MAIN_TERRAFORM_FILE}"
from pathlib import Path
import sys

path = Path(sys.argv[1])
text = path.read_text()
original = 'terraform {\n  backend "s3" {}\n}\n'
replacement = '''terraform {
  backend "s3" {
    bucket         = "emerald-grove-pet-clinic-dev-terraform-state"
    key            = "app/dev/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "emerald-grove-pet-clinic-dev-terraform-locks"
    encrypt        = true
  }
}
'''

if original not in text:
    raise SystemExit("unable to materialize backend config for verification")

path.write_text(text.replace(original, replacement, 1))
PY
}

restore_backend_stub() {
  if [[ -n "${APP_MAIN_BACKUP_FILE}" && -f "${APP_MAIN_BACKUP_FILE}" ]]; then
    cp "${APP_MAIN_BACKUP_FILE}" "${ROOT_DIR}/${APP_MAIN_TERRAFORM_FILE}"
    rm -f "${APP_MAIN_BACKUP_FILE}"
  fi
}

materialize_provider_endpoints_for_floci() {
  APP_VERSIONS_BACKUP_FILE="$(mktemp "${TMPDIR:-/tmp}/floci-ecs-versions.XXXXXX")"
  cp "${ROOT_DIR}/${APP_VERSIONS_FILE}" "${APP_VERSIONS_BACKUP_FILE}"

  python3 - <<'PY' "${ROOT_DIR}/${APP_VERSIONS_FILE}"
from pathlib import Path
import sys

path = Path(sys.argv[1])
text = path.read_text()
original = '''provider "aws" {
  region = var.aws_region

  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_region_validation      = true
  skip_requesting_account_id  = true

  default_tags {
    tags = local.common_tags
  }
}
'''
replacement = '''provider "aws" {
  region = var.aws_region

  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_region_validation      = true
  skip_requesting_account_id  = true

  endpoints {
    cloudwatchlogs = "http://127.0.0.1:4566"
    ec2            = "http://127.0.0.1:4566"
    ecr            = "http://127.0.0.1:4566"
    ecs            = "http://127.0.0.1:4566"
    elbv2          = "http://127.0.0.1:4566"
    iam            = "http://127.0.0.1:4566"
    sts            = "http://127.0.0.1:4566"
  }

  default_tags {
    tags = local.common_tags
  }
}
'''

if original not in text:
    raise SystemExit("unable to materialize provider endpoints for floci verification")

path.write_text(text.replace(original, replacement, 1))
PY
}

restore_provider_stub() {
  if [[ -n "${APP_VERSIONS_BACKUP_FILE}" && -f "${APP_VERSIONS_BACKUP_FILE}" ]]; then
    cp "${APP_VERSIONS_BACKUP_FILE}" "${ROOT_DIR}/${APP_VERSIONS_FILE}"
    rm -f "${APP_VERSIONS_BACKUP_FILE}"
  fi
}

destroy_stack_if_present() {
  if [[ ! -d "${ROOT_DIR}/${APP_DIR}/.terraform" ]]; then
    return
  fi

  local_terraform -chdir=infra/terraform/app/dev destroy -auto-approve >/dev/null 2>&1 || true
}

wait_for_output() {
  local output_name="$1"
  local attempts="${2:-30}"
  local output_value=""

  for (( attempt = 1; attempt <= attempts; attempt++ )); do
    output_value="$(local_terraform -chdir="${ROOT_DIR}/${APP_DIR}" output -raw "${output_name}" 2>/dev/null || true)"
    if [[ -n "${output_value}" && "${output_value}" != "null" ]]; then
      printf '%s\n' "${output_value}"
      return 0
    fi

    sleep 2
  done

  echo "timed out waiting for terraform output ${output_name}" >&2
  exit 1
}

wait_for_service_steady_state() {
  local cluster_name="$1"
  local service_name="$2"

  for (( attempt = 1; attempt <= 45; attempt++ )); do
    local running_count
    local desired_count
    local rollout_state

    running_count="$(local_aws ecs describe-services \
      --cluster "${cluster_name}" \
      --services "${service_name}" \
      --query 'services[0].runningCount' \
      --output text 2>/dev/null || true)"
    desired_count="$(local_aws ecs describe-services \
      --cluster "${cluster_name}" \
      --services "${service_name}" \
      --query 'services[0].desiredCount' \
      --output text 2>/dev/null || true)"
    rollout_state="$(local_aws ecs describe-services \
      --cluster "${cluster_name}" \
      --services "${service_name}" \
      --query 'services[0].deployments[0].rolloutState' \
      --output text 2>/dev/null || true)"

    if [[ "${running_count}" == "1" && "${desired_count}" == "1" && "${rollout_state}" == "COMPLETED" ]]; then
      return 0
    fi

    sleep 4
  done

  echo "timed out waiting for ECS service steady state" >&2
  local_aws ecs describe-services --cluster "${cluster_name}" --services "${service_name}"
  exit 1
}

apply_local_ecr_contract() {
  if local_terraform -chdir=infra/terraform/app/dev apply -target=aws_ecr_repository.app -target=aws_ecr_lifecycle_policy.app -auto-approve; then
    return 0
  fi

  cat >&2 <<'EOF'
floci runtime verification stopped before ECS launch.
The local AWS emulator rejected ECR repository creation, so the bootstrap
image cannot be pushed and the ECS-through-ALB path cannot be proven here.

Current blocker:
  - aws_ecr_repository.app requires working local ECR support

Next options:
  - switch floci to an emulator/runtime that supports ECR + ECS + ELBv2 end to end
  - use a live AWS workflow for the final ECS and ALB proof artifacts
EOF
  exit 1
}

wait_for_healthy_target() {
  local target_group_arn="$1"

  for (( attempt = 1; attempt <= 45; attempt++ )); do
    local target_state
    target_state="$(local_aws elbv2 describe-target-health \
      --target-group-arn "${target_group_arn}" \
      --query 'TargetHealthDescriptions[0].TargetHealth.State' \
      --output text 2>/dev/null || true)"

    if [[ "${target_state}" == "healthy" ]]; then
      return 0
    fi

    sleep 4
  done

  echo "timed out waiting for healthy target registration" >&2
  local_aws elbv2 describe-target-health --target-group-arn "${target_group_arn}"
  exit 1
}

build_and_push_deploy_image() {
  local repository_uri="$1"

  local repository_host="${repository_uri%%/*}"
  local local_image="petclinic:${DEPLOY_IMAGE_TAG}"

  docker build -t "${local_image}" "${ROOT_DIR}" >/dev/null
  local_aws ecr get-login-password | docker login --username AWS --password-stdin "${repository_host}" >/dev/null
  docker tag "${local_image}" "${repository_uri}:${DEPLOY_IMAGE_TAG}"
  docker push "${repository_uri}:${DEPLOY_IMAGE_TAG}" >/dev/null

  local digest
  digest="$(local_aws ecr describe-images \
    --repository-name "${repository_uri#*/}" \
    --image-ids imageTag="${DEPLOY_IMAGE_TAG}" \
    --query 'imageDetails[0].imageDigest' \
    --output text)"

  printf '%s@%s\n' "${repository_uri}" "${digest}"
}

verify_runtime_evidence() {
  local cluster_name="$1"
  local service_name="$2"
  local target_group_arn="$3"
  local alb_dns_name="$4"
  local log_group_name="$5"

  local_aws ecs describe-services --cluster "${cluster_name}" --services "${service_name}"
  local_aws ecs list-tasks --cluster "${cluster_name}" --service-name "${service_name}"
  local_aws elbv2 describe-target-health --target-group-arn "${target_group_arn}"

  local health_response
  health_response="$(curl -fsS "http://${alb_dns_name}/actuator/health")"
  printf '%s\n' "${health_response}"

  local task_id
  local log_stream_name
  task_id="$(local_aws ecs list-tasks \
    --cluster "${cluster_name}" \
    --service-name "${service_name}" \
    --query 'taskArns[0]' \
    --output text)"
  task_id="${task_id##*/}"
  log_stream_name="dev-petclinic/application/${task_id}"

  local_aws logs get-log-events \
    --log-group-name "${log_group_name}" \
    --log-stream-name "${log_stream_name}" \
    --limit 20
}

main() {
  trap cleanup EXIT

  require_file "${COMPOSE_FILE}"
  require_file "${APP_DIR}/main.tf"
  require_file "${APP_DIR}/variables.tf"
  require_file "${APP_DIR}/locals.tf"
  require_file "${APP_DIR}/outputs.tf"
  require_file "${APP_DIR}/versions.tf"
  require_file "infra/terraform/app/dev/README.md"
  require_file "${APP_DIR}/${BACKEND_CONFIG}"
  require_file "infra/terraform/floci/README.md"
  require_file "infra/terraform/floci/${BACKEND_CONFIG}"
  require_file "Dockerfile"
  require_file "scripts/verify-floci-ecs-through-alb.sh"

  require_deploy_image_contract

  cd "${ROOT_DIR}"

  docker compose -f infra/terraform/floci/docker-compose.yml up -d floci

  create_bucket_if_missing
  create_lock_table_if_missing

  materialize_backend_config_for_verification
  materialize_provider_endpoints_for_floci

  local_terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure
  local_terraform -chdir=infra/terraform/app/dev validate

  apply_local_ecr_contract

  local repository_uri
  local deploy_image
  local cluster_name
  local service_name
  local target_group_arn
  local alb_dns_name
  local log_group_name

  repository_uri="$(wait_for_output repository_uri)"
  deploy_image="$(build_and_push_deploy_image "${repository_uri}")"

  local_terraform -chdir=infra/terraform/app/dev apply -auto-approve -var "deploy_image=${deploy_image}"

  cluster_name="$(wait_for_output ecs_cluster_name)"
  service_name="$(wait_for_output baseline_ecs_service_name)"
  target_group_arn="$(wait_for_output application_target_group_arn)"
  alb_dns_name="$(wait_for_output alb_dns_name)"
  log_group_name="$(wait_for_output application_log_group_name)"

  wait_for_service_steady_state "${cluster_name}" "${service_name}"
  wait_for_healthy_target "${target_group_arn}"
  verify_runtime_evidence "${cluster_name}" "${service_name}" "${target_group_arn}" "${alb_dns_name}" "${log_group_name}"

  local_terraform -chdir=infra/terraform/app/dev destroy -auto-approve -var "deploy_image=${deploy_image}"
}

main "$@"
