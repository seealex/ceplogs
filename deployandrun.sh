#!/bin/sh

#java -jar app/app.jar

apt-get update && apt-get install -y curl unzip \
    && curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" \
    && unzip awscliv2.zip \
    && ./aws/install

# Wait until LocalStack is ready (e.g., check for ECS availability)
until aws --endpoint-url=http://localstack:4566 dynamodb list-tables; do
  echo "Waiting for LocalStack ECS service to be ready..."
  sleep 5
done

# Create CloudFormation stack for ECS resources
aws --endpoint-url=http://localstack:4566 cloudformation validate-template \
  --stack-name cep-app-stack \
  --template-body file:///etc/cloudformation/aws.yml

# Wait for the stack to be created
echo "Waiting for ECS service to be launched..."
aws --endpoint-url=http://localstack:4566 ecs update-service \
  --cluster CepEcsCluster \
  --service CepEcsService \
  --desired-count 1