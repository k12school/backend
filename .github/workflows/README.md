# GitHub Actions CI/CD Setup Guide

This guide explains how to set up GitHub Actions to deploy the K12 Backend directly from GitHub to your Vagrant VM staging environment.

## Overview

The GitHub Actions workflow:
1. **Builds** the Quarkus application on push to `main` or `develop`
2. **Runs tests** (unit tests, integration tests)
3. **Scans** for security vulnerabilities
4. **Pushes** Docker image to GitHub Container Registry (ghcr.io)
5. **Deploys** to staging automatically (on `main` branch)
6. **Deploys** to production manually (via workflow dispatch)

## Prerequisites

### 1. Vagrant VM Configuration

Your Vagrant VM must be accessible via SSH. Update `/home/joao/workspace/k12/devops/ops/vagrant/Vagrantfile`:

```ruby
# Add SSH port forwarding if not present
config.vm.network "forwarded_port", guest: 22, host: 2222
```

### 2. GitHub Secrets Configuration

Go to: **Repository Settings → Secrets and variables → Actions → New repository secret**

#### Required Secrets:

| Secret Name | Description | Example Value |
|--------------|-------------|----------------|
| `SSH_PRIVATE_KEY` | Private SSH key for Vagrant VM access | `-----BEGIN OPENSSH PRIVATE KEY-----...` |
| `STAGING_HOST` | Staging VM hostname or IP | `127.0.0.1:2222` or `192.168.56.10` |
| `PROD_SSH_PRIVATE_KEY` | Production SSH key | `-----BEGIN OPENSSH PRIVATE KEY-----...` |
| `PROD_HOST` | Production server hostname | `prod.k12platform.com` |
| `PROD_USER` | Production SSH user | `ubuntu` or `deploy` |
| `GITHUB_TOKEN` | (Auto-created) GitHub token for registry auth | – |

#### Generating SSH Key for Vagrant Access:

```bash
# Generate SSH key pair
ssh-keygen -t rsa -b 4096 -C "github-actions" -f ~/.ssh/github_actions_key

# Copy public key to Vagrant VM
ssh-copy-id -i ~/.ssh/github_actions_key.pub vagrant@127.0.0.1 -p 2222

# Or manually add to VM
cat ~/.ssh/github_actions_key.pub | vagrant ssh -c "cat >> ~/.ssh/authorized_keys"

# Copy private key content for GitHub secret
cat ~/.ssh/github_actions_key
```

**Copy the entire private key (including `-----BEGIN` and `-----END` lines) and paste it as the `SSH_PRIVATE_KEY` secret value.**

#### Setting STAGING_HOST:

For local Vagrant VM (development/testing):
```
STAGING_HOST = 127.0.0.1
```

For remote Vagrant VM:
```
STAGING_HOST = your-server.com
```

## Workflow Files

### Main Workflow: `.github/workflows/deploy.yml`

Located at: `/home/joao/workspace/k12/backend/.github/workflows/deploy.yml`

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`
- Manual workflow dispatch (for production)

**Jobs:**

1. **build**: Compiles Quarkus app, runs tests, builds Docker image, pushes to GHCR
2. **security-scan**: Scans Docker image for vulnerabilities with Trivy
3. **deploy-staging**: Deploys to staging automatically (on `main` branch)
4. **deploy-production**: Deploys to production (manual trigger only)

## GitHub Container Registry (GHCR)

Docker images are pushed to:
```
ghcr.io/k12school/backend:latest
ghcr.io/k12school/backend:main-<sha>
ghcr.io/k12school/backend:develop-<sha>
```

## Deployment Flow

### Development Workflow

```mermaid
graph LR
    A[Push to develop] --> B[Build & Test]
    B --> C[Security Scan]
    C --> D[Push to GHCR]
    D --> E[End]
```

### Production Workflow

```mermaid
graph LR
    A[Push to main] --> B[Build & Test]
    B --> C[Security Scan]
    C --> D[Push to GHCR]
    D --> E[Deploy to Staging]
    E --> F[Smoke Tests]
    F --> G[Manual Approval]
    G --> H[Deploy to Production]
```

## Environment-Specific Configuration

### Staging Environment

Automatically deployed from `main` branch:
- URL: `http://<STAGING_HOST>:9080`
- Credentials: From `/home/vagrant/ops/secrets/.env.staging`
- Health check: `curl http://<STAGING_HOST>:9080/api/test/db`

### Production Environment

Manual deployment only:
- URL: `https://api.k12platform.com`
- Credentials: From `/ops/secrets/.env.prod` on production server
- Requires manual approval in GitHub Actions UI

## Verification

### Check Pipeline Status

1. Go to your repository on GitHub
2. Click "Actions" tab
3. Select "Build and Deploy" workflow
4. View workflow runs

### Check Deployment

**From host machine:**
```bash
# Test staging API
curl http://localhost:9080/api/test/db

# Check Swagger UI
open http://localhost:9080/swagger-ui/

# Check container status (from VM)
vagrant ssh -c "docker ps --filter 'name=backend-api'"
```

## Rollback Procedure

If deployment fails:

### Automatic Rollback
The workflow automatically runs `make rollback` if production deployment fails.

### Manual Rollback
```bash
# SSH into production server
ssh -i ~/.ssh/prod_key user@prod-server

# Rollback
cd /ops
make rollback ENVIRONMENT=prod
```

Or via GitHub Actions UI:
1. Go to Actions tab
2. Find previous successful run
3. Click "Re-run jobs" → "Re-run all jobs"

## Troubleshooting

### SSH Connection Failed

**Error:** `Permission denied (publickey)`

**Solution:**
1. Verify SSH key is added to Vagrant VM:
   ```bash
   vagrant ssh -c "cat ~/.ssh/authorized_keys"
   ```

2. Test SSH connection:
   ```bash
   ssh -i ~/.ssh/github_actions_key vagrant@127.0.0.1 -p 2222
   ```

3. Ensure `STAGING_HOST` secret is correct

### Docker Pull Failed

**Error:** `ERROR: pull access denied`

**Solution:**
1. Verify image was built and pushed
2. Check GHCR permissions: Repository Settings → Actions → General
3. Ensure "Read and write permissions" is enabled for workflows

### Smoke Tests Failed

**Error:** `curl: (22) The requested URL returned error: 404`

**Solution:**
1. Check backend logs:
   ```bash
   vagrant ssh -c "docker logs backend-api --tail 50"
   ```

2. Verify Traefik routes are configured
3. Check health endpoint:
   ```bash
   vagrant ssh -c "docker exec backend-api wget -qO- http://localhost:8081/api/test/db"
   ```

### Security Scan Failed

**Error:** Trivy found CRITICAL vulnerabilities

**Solution:**
1. Review security tab in repository
2. Update dependencies with vulnerabilities
3. Or add exception for false positives

## Best Practices

1. **Branch Protection** (Settings → Branches):
   - Require status checks to pass before merge
   - Require "Build and Test" job to succeed
   - Require "Security Scan" job to succeed

2. **Environment Protection** (Settings → Environments):
   - Add "production" environment
   - Require reviewers for production deployment
   - Wait timer: 5 minutes before deployment

3. **Secret Rotation**:
   - Rotate SSH keys quarterly
   - Update GitHub secrets after key rotation
   - Test SSH access before committing changes

4. **Monitoring**:
   - Enable GitHub Actions logs retention (90 days)
   - Set up notifications for failed workflows
   - Monitor Traefik access logs

## Comparison: Woodpecker CI vs GitHub Actions

| Feature | Woodpecker CI (Current) | GitHub Actions (Proposed) |
|---------|-------------------------|---------------------------|
| Hosting | Self-hosted | Cloud-hosted |
| Setup | Manual server setup | Zero-config |
| GitHub Integration | Webhooks required | Native integration |
| CI/CD Storage | `/var/lib/k12-artifacts` | GitHub artifacts |
| Authentication | Shared secret | GitHub token (built-in) |
| Docker Registry | Self-hosted registry | GHCR (included) |
| Cost | Free (self-hosted) | Free (public repos), $4/user/month (private) |
| Maintenance | High (server updates) | Zero (managed service) |

## Migration Path

To migrate from Woodpecker CI to GitHub Actions:

1. ✅ Create GitHub Actions workflow file
2. ✅ Configure GitHub secrets
3. ✅ Test SSH access to Vagrant VM
4. ✅ Deploy workflow to repository
5. ⏳ Test staging deployment (push to `main`)
6. ⏳ Verify smoke tests pass
7. ⏳ Set up production environment protection
8. ⏳ Disable Woodpecker CI (after validation)
9. ⏳ Remove Woodpecker containers from docker-compose

## Next Steps

1. **Set up GitHub secrets** (required before workflow will run)
2. **Test SSH access** manually first
3. **Push workflow to repository** and monitor first run
4. **Configure branch protection rules** after successful deployment
