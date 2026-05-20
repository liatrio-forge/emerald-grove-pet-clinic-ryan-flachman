#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="infra/terraform/floci/docker-compose.yml"
APP_DIR="infra/terraform/app/dev"
BACKEND_CONFIG="backend.hcl.example"
APP_MAIN_TERRAFORM_FILE="${APP_DIR}/main.tf"
APP_VARIABLES_FILE="${APP_DIR}/variables.tf"
APP_MAIN_BACKUP_FILE=""
LOCALSTACK_ENDPOINT="http://127.0.0.1:4566"
STATE_BUCKET="emerald-grove-pet-clinic-dev-terraform-state"
LOCK_TABLE="emerald-grove-pet-clinic-dev-terraform-locks"

cleanup() {
  restore_backend_stub
  docker compose -f infra/terraform/floci/docker-compose.yml down
  rm -rf "${ROOT_DIR}/${APP_DIR}/.terraform"
}

require_file() {
  local path="$1"

  if [[ ! -f "${ROOT_DIR}/${path}" ]]; then
    echo "missing required file: ${path}" >&2
    exit 1
  fi
}

require_bootstrap_image_contract() {
  if ! grep -q 'variable "bootstrap_image"' "${ROOT_DIR}/${APP_VARIABLES_FILE}"; then
    echo "bootstrap_image variable contract is required" >&2
    exit 1
  fi

  if ! grep -q '@sha256:' "${ROOT_DIR}/${APP_VARIABLES_FILE}"; then
    echo "bootstrap_image must stay pinned to an immutable digest" >&2
    exit 1
  fi
}

create_bucket_if_missing() {
  if AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_DEFAULT_REGION=us-east-1 \
    aws --endpoint-url="${LOCALSTACK_ENDPOINT}" s3api head-bucket --bucket "${STATE_BUCKET}" >/dev/null 2>&1; then
    return
  fi

  AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_DEFAULT_REGION=us-east-1 \
    aws --endpoint-url="${LOCALSTACK_ENDPOINT}" s3api create-bucket --bucket "${STATE_BUCKET}" >/dev/null
}

create_lock_table_if_missing() {
  if AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_DEFAULT_REGION=us-east-1 \
    aws --endpoint-url="${LOCALSTACK_ENDPOINT}" dynamodb describe-table --table-name "${LOCK_TABLE}" >/dev/null 2>&1; then
    return
  fi

  AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_DEFAULT_REGION=us-east-1 \
    aws --endpoint-url="${LOCALSTACK_ENDPOINT}" dynamodb create-table \
      --table-name "${LOCK_TABLE}" \
      --attribute-definitions AttributeName=LockID,AttributeType=S \
      --key-schema AttributeName=LockID,KeyType=HASH \
      --billing-mode PAY_PER_REQUEST >/dev/null
}

materialize_backend_config_for_verification() {
  APP_MAIN_BACKUP_FILE="$(mktemp "${TMPDIR:-/tmp}/baseline-ecs-main.XXXXXX")"
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

print_live_evidence_commands() {
  cat <<'EOF'
AWS-backed follow-up evidence after apply:
  aws ecs describe-services --cluster <cluster-name> --services <service-name>
  aws ecs list-tasks --cluster <cluster-name> --service-name <service-name>
  aws elbv2 describe-target-health --target-group-arn <target-group-arn>
  curl -fsS http://<alb-dns-name>/actuator/health
  aws logs get-log-events --log-group-name /aws/ecs/dev-application --log-stream-name <stream-name>

Scope reminders:
  - The baseline ECS service keeps assign_public_ip = false, so no direct task public-IP path exists.
  - This workflow excludes autoscaling, Secrets Manager integration, CI rollout automation, and database redesign.
EOF
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
  require_file "scripts/verify-baseline-ecs-task-definition-service-contract.sh"

  require_bootstrap_image_contract

  cd "${ROOT_DIR}"

  docker compose -f infra/terraform/floci/docker-compose.yml up -d floci

  create_bucket_if_missing
  create_lock_table_if_missing

  materialize_backend_config_for_verification
  terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure
  terraform -chdir=infra/terraform/app/dev validate
  AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
    terraform -chdir=infra/terraform/app/dev plan -no-color

  print_live_evidence_commands
}

main "$@"
