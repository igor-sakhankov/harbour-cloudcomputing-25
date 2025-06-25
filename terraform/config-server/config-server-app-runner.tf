provider "aws" {
  region = "us-east-1"
}

data "aws_caller_identity" "current" {}

resource "aws_iam_role" "apprunner_ecr_access_role" {
  name = "AppRunnerECRAccessRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect = "Allow",
      Principal = {
        Service = "build.apprunner.amazonaws.com"
      },
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "apprunner_ecr_policy" {
  role       = aws_iam_role.apprunner_ecr_access_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSAppRunnerServicePolicyForECRAccess"
}

resource "aws_apprunner_service" "config_server" {
  service_name = "config-server"

  source_configuration {
    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_ecr_access_role.arn
    }

    image_repository {
      image_configuration {
        port = "8080"
      }

      image_identifier      = "${data.aws_caller_identity.current.account_id}.dkr.ecr.us-east-1.amazonaws.com/config-server:latest"
      image_repository_type = "ECR"
    }

    auto_deployments_enabled = false
  }

  tags = {
    Environment = "production"
  }


  # lifecycle {
  #   ignore_changes = [
  #     source_configuration
  #   ]
  # }
}