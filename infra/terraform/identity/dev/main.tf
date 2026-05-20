data "aws_iam_policy_document" "github_actions_oidc_trust" {
  for_each = local.github_actions_subjects

  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]

    principals {
      type        = "Federated"
      identifiers = [aws_iam_openid_connect_provider.github_actions.arn]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:sub"
      values   = [each.value]
    }
  }
}

resource "aws_iam_openid_connect_provider" "github_actions" {
  url             = "https://token.actions.githubusercontent.com"
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = local.github_oidc_thumbprints

  tags = merge(local.common_tags, {
    Name = "github-actions-oidc"
    Role = "github-oidc"
  })
}

resource "aws_iam_policy" "terraform_github_actions" {
  name = "${var.environment}-terraform-github-actions"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "TerraformStateAndPlanning"
        Effect = "Allow"
        Action = [
          "cloudwatch:*",
          "ec2:*",
          "ecr:*",
          "ecs:*",
          "elasticloadbalancing:*",
          "iam:AttachRolePolicy",
          "iam:CreateRole",
          "iam:DeleteRole",
          "iam:DeleteRolePolicy",
          "iam:DetachRolePolicy",
          "iam:GetOpenIDConnectProvider",
          "iam:GetPolicy",
          "iam:GetPolicyVersion",
          "iam:GetRole",
          "iam:GetRolePolicy",
          "iam:ListAttachedRolePolicies",
          "iam:ListInstanceProfilesForRole",
          "iam:ListOpenIDConnectProviders",
          "iam:ListPolicyVersions",
          "iam:ListRolePolicies",
          "iam:PassRole",
          "iam:PutRolePolicy",
          "iam:TagOpenIDConnectProvider",
          "iam:TagPolicy",
          "iam:TagRole",
          "iam:UntagOpenIDConnectProvider",
          "iam:UntagPolicy",
          "iam:UntagRole",
          "iam:UpdateAssumeRolePolicy",
          "iam:UpdateOpenIDConnectProviderThumbprint",
          "logs:*",
          "route53:*",
          "s3:*",
        ]
        Resource = "*"
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name = "${var.environment}-terraform-github-actions"
    Role = "terraform-github-actions"
  })
}

resource "aws_iam_role" "terraform_apply_github_actions" {
  name               = local.terraform_apply_role_name
  assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust["terraform_apply"].json

  tags = merge(local.common_tags, {
    Name = local.terraform_apply_role_name
    Role = "terraform-apply"
  })
}

resource "aws_iam_role_policy_attachment" "terraform_apply_github_actions" {
  role       = aws_iam_role.terraform_apply_github_actions.name
  policy_arn = aws_iam_policy.terraform_github_actions.arn
}

resource "aws_iam_role" "terraform_destroy_github_actions" {
  name               = local.terraform_destroy_role_name
  assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust["terraform_destroy"].json

  tags = merge(local.common_tags, {
    Name = local.terraform_destroy_role_name
    Role = "terraform-destroy"
  })
}

resource "aws_iam_role_policy_attachment" "terraform_destroy_github_actions" {
  role       = aws_iam_role.terraform_destroy_github_actions.name
  policy_arn = aws_iam_policy.terraform_github_actions.arn
}

resource "aws_iam_policy" "app_publish_github_actions" {
  name = "${var.environment}-app-publish-github-actions"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "EcrPublicationPath"
        Effect = "Allow"
        Action = [
          "ecr:BatchCheckLayerAvailability",
          "ecr:BatchGetImage",
          "ecr:CompleteLayerUpload",
          "ecr:DescribeImages",
          "ecr:DescribeRepositories",
          "ecr:GetAuthorizationToken",
          "ecr:InitiateLayerUpload",
          "ecr:PutImage",
          "ecr:UploadLayerPart",
        ]
        Resource = "*"
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name = "${var.environment}-app-publish-github-actions"
    Role = "app-publish-github-actions"
  })
}

resource "aws_iam_role" "app_publish_github_actions" {
  name               = local.app_publish_role_name
  assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust["app_publish"].json

  tags = merge(local.common_tags, {
    Name = local.app_publish_role_name
    Role = "app-publish"
  })
}

resource "aws_iam_role_policy_attachment" "app_publish_github_actions" {
  role       = aws_iam_role.app_publish_github_actions.name
  policy_arn = aws_iam_policy.app_publish_github_actions.arn
}

resource "aws_iam_policy" "app_deploy_github_actions" {
  name = "${var.environment}-app-deploy-github-actions"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "EcsDeploymentPath"
        Effect = "Allow"
        Action = [
          "ecs:DescribeServices",
          "ecs:DescribeTaskDefinition",
          "ecs:RegisterTaskDefinition",
          "ecs:UpdateService",
          "ecs:ListTasks",
          "ecr:BatchGetImage",
          "ecr:DescribeImages",
          "ecr:DescribeRepositories",
          "iam:GetRole",
          "iam:PassRole",
          "logs:DescribeLogGroups",
          "logs:DescribeLogStreams",
        ]
        Resource = "*"
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name = "${var.environment}-app-deploy-github-actions"
    Role = "app-deploy-github-actions"
  })
}

resource "aws_iam_role" "app_deploy_github_actions" {
  name               = local.app_deploy_role_name
  assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust["app_deploy"].json

  tags = merge(local.common_tags, {
    Name = local.app_deploy_role_name
    Role = "app-deploy"
  })
}

resource "aws_iam_role_policy_attachment" "app_deploy_github_actions" {
  role       = aws_iam_role.app_deploy_github_actions.name
  policy_arn = aws_iam_policy.app_deploy_github_actions.arn
}
