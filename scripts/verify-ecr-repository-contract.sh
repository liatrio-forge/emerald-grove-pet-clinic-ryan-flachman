#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="infra/terraform/floci/docker-compose.yml"
APP_DIR="infra/terraform/app/dev"
BACKEND_CONFIG="backend.hcl.example"
LOCALSTACK_ENDPOINT="http://127.0.0.1:4566"
STATE_BUCKET="emerald-grove-pet-clinic-dev-terraform-state"
LOCK_TABLE="emerald-grove-pet-clinic-dev-terraform-locks"
VERIFY_DIR=""

cleanup() {
  docker compose -f infra/terraform/floci/docker-compose.yml down
  rm -rf "${ROOT_DIR}/${APP_DIR}/.terraform"
  rm -rf "${VERIFY_DIR}"
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

prepare_validation_workspace() {
  VERIFY_DIR="$(mktemp -d)"
  cp -R "${ROOT_DIR}/${APP_DIR}/." "${VERIFY_DIR}/"

  cat > "${VERIFY_DIR}/backend_override.tf" <<EOF
terraform {
  backend "s3" {
$(sed 's/^/    /' "${ROOT_DIR}/${APP_DIR}/${BACKEND_CONFIG}")
  }
}
EOF
}

main() {
  trap cleanup EXIT

  require_file "${COMPOSE_FILE}"
  require_file "${APP_DIR}/${BACKEND_CONFIG}"
  require_file "${APP_DIR}/README.md"
  require_file "infra/terraform/floci/README.md"
  require_file "scripts/verify-ecr-repository-contract.sh"

  cd "${ROOT_DIR}"

  docker compose -f infra/terraform/floci/docker-compose.yml up -d floci

  create_bucket_if_missing
  create_lock_table_if_missing
  prepare_validation_workspace

  terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure
  terraform -chdir="${VERIFY_DIR}" init -reconfigure
  # Canonical contract command: terraform -chdir=infra/terraform/app/dev validate
  terraform -chdir="${VERIFY_DIR}" validate
  # Canonical contract command: terraform -chdir=infra/terraform/app/dev plan -no-color
  AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
    terraform -chdir="${VERIFY_DIR}" plan -no-color
}

main "$@"
