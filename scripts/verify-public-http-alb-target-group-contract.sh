#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="infra/terraform/floci/docker-compose.yml"
APP_DIR="infra/terraform/app/dev"
BACKEND_CONFIG="backend.hcl.example"
APP_MAIN_TERRAFORM_FILE="${APP_DIR}/main.tf"
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
  APP_MAIN_BACKUP_FILE="$(mktemp "${TMPDIR:-/tmp}/public-alb-main.XXXXXX")"
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

main() {
  trap cleanup EXIT

  require_file "${COMPOSE_FILE}"
  # Required contract files:
  # - infra/terraform/app/dev/outputs.tf
  # - scripts/verify-public-http-alb-target-group-contract.sh
  require_file "${APP_DIR}/main.tf"
  require_file "${APP_DIR}/variables.tf"
  require_file "${APP_DIR}/locals.tf"
  require_file "${APP_DIR}/outputs.tf"
  require_file "${APP_DIR}/versions.tf"
  require_file "infra/terraform/app/dev/README.md"
  require_file "${APP_DIR}/${BACKEND_CONFIG}"
  require_file "infra/terraform/floci/README.md"
  require_file "infra/terraform/floci/${BACKEND_CONFIG}"
  require_file "scripts/verify-public-http-alb-target-group-contract.sh"

  cd "${ROOT_DIR}"

  docker compose -f infra/terraform/floci/docker-compose.yml up -d floci

  create_bucket_if_missing
  create_lock_table_if_missing

  materialize_backend_config_for_verification
  terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure
  terraform -chdir=infra/terraform/app/dev validate
  AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
    terraform -chdir=infra/terraform/app/dev plan -no-color
}

main "$@"
