# Bookfinder Cloud Deployment (AWS)

This guide documents the exact deployment flow used for Bookfinder, including local MySQL → RDS migration.

## Target Architecture
- **Frontend**: S3 (private) → CloudFront
- **Backend**: ECS Fargate → ALB → API Gateway (HTTP API + VPC Link)
- **Database**: RDS MySQL in private DB subnets

## 1) Networking (VPC + Subnets + Routes)

### Subnet layout
- Public Subnets (ALB + NAT): 2
- Private App Subnets (ECS): 2
- Private DB Subnets (RDS): 2

### Route Tables
- **Public RT**:
  - `10.0.0.0/16 → local`
  - `0.0.0.0/0 → IGW`
- **Private App RT**:
  - `10.0.0.0/16 → local`
  - `0.0.0.0/0 → NAT Gateway`
- **Private DB RT**:
  - `10.0.0.0/16 → local`
  - no internet route

### NAT
- One NAT Gateway in a public subnet (two NATs for HA if needed)

## 2) Security Groups (SG)

### ALB SG (Public)
Inbound:
- TCP 80 from `0.0.0.0/0`
- TCP 443 from `0.0.0.0/0`
Outbound:
- TCP 8080 to ECS SG

### ECS SG (Private App)
Inbound:
- TCP 8080 from ALB SG
Outbound:
- TCP 3306 to RDS SG
- All outbound to `0.0.0.0/0` (for ECR + CloudWatch)

### RDS SG (Private DB)
Inbound:
- TCP 3306 from ECS SG
Outbound:
- Default (all outbound)

## 3) RDS MySQL

### Create DB Subnet Group
- Include **only DB subnets**

### Create RDS
- Engine: MySQL
- Public access: **No** (can be temporarily Yes if needed)
- Security group: RDS SG

## 4) Local MySQL → RDS Migration (mysqldump method)

### A) Dump local DB
```bash
mysqldump -u root -p   --single-transaction --routines --triggers --events   --set-gtid-purged=OFF   bookfinder > /tmp/bookfinder.sql
```

### B) Upload dump to S3
```bash
aws s3 cp /tmp/bookfinder.sql s3://coder491388423707/bookfinder.sql
```

### C) Import from EC2 (Ubuntu)
```bash
sudo apt-get update -y
sudo apt-get install -y mysql-client awscli
aws s3 cp s3://coder491388423707/bookfinder.sql .
mysql -h <RDS_ENDPOINT> -u admin -p bookfinder < bookfinder.sql
```

### D) Verify
```bash
mysql -h <RDS_ENDPOINT> -u admin -p -e "SHOW TABLES;" bookfinder
```

## 5) Backend (ECS + ALB)

### Build and push image to ECR
```bash
cd backend
aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin <ECR_URI>
docker buildx build --platform linux/amd64 -t <ECR_URI>:latest --push .
```

### ECS Task Definition env vars
```
DB_URL=jdbc:mysql://<RDS_ENDPOINT>:3306/bookfinder?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=<DB_USER>
DB_PASSWORD=<DB_PASS>
FRONTEND_ORIGIN=*
```

## 6) API Gateway (HTTP API + VPC Link)
- Create HTTP API
- Create VPC Link to ALB
- Create integration with **ALB listener ARN**
- Route: `ANY /{proxy+}`
- Stage: `prod`

API URL format:
```
https://<api-id>.execute-api.ap-south-1.amazonaws.com/prod
```

## 7) Frontend (S3 + CloudFront)

### Build and upload
```bash
cd frontend
echo "VITE_API_BASE_URL=https://<api-id>.execute-api.ap-south-1.amazonaws.com/prod" > .env
npm run build
aws s3 sync dist s3://coder491388423707 --delete
aws cloudfront create-invalidation --distribution-id <CF_ID> --paths "/*"
```

## 8) Validation
- ALB health check: `/api/health`
- API Gateway health: `https://<api-id>.execute-api.ap-south-1.amazonaws.com/prod/api/health`
- Frontend CloudFront URL should load login + books
