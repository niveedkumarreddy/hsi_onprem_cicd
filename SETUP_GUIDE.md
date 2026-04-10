# webMethods CI/CD Setup Guide

Complete step-by-step guide to implement the GitHub Actions CI/CD pipeline for webMethods.

## 📋 Prerequisites Checklist

Before starting, ensure you have:

- [ ] GitHub repository with admin access
- [ ] webMethods Integration Server 10.x or 11.x installed
- [ ] Deployer installed and configured
- [ ] Asset Build Environment (ABE) installed
- [ ] Build server with network access to all target servers
- [ ] GitHub account with Actions enabled

## 🔧 Step-by-Step Implementation

### Step 1: Repository Setup (5 minutes)

1. **Clone or initialize the repository**
   ```bash
   git clone https://github.com/YOUR_ORG/hsi_onprem_cicd.git
   cd hsi_onprem_cicd
   ```

2. **Verify project structure**
   ```bash
   tree -L 2
   ```
   
   Expected structure:
   ```
   .
   ├── .github/workflows/
   ├── packages/
   ├── tests/
   ├── varsub/
   ├── ENV.groovy
   ├── build.properties
   └── README.md
   ```

### Step 2: Configure GitHub Secrets (10 minutes)

1. Navigate to: **Settings → Secrets and variables → Actions**

2. Click **New repository secret** and add:

   | Secret Name | Value | Description |
   |------------|-------|-------------|
   | `DEPLOYER_HOST` | `deployer.company.com` | Deployer server hostname |
   | `DEPLOYER_PORT` | `5555` | Deployer server port |
   | `DEPLOYER_USERNAME` | `Administrator` | Deployer username |
   | `DEPLOYER_PASSWORD` | `manage` | Deployer password |

3. **Verify secrets are added**
   - Go to Actions secrets page
   - You should see 4 secrets listed

### Step 3: Setup Self-Hosted Runner (20 minutes)

#### On Linux Build Server:

```bash
# 1. Create runner directory
sudo mkdir -p /opt/actions-runner
cd /opt/actions-runner

# 2. Download runner (check latest version)
curl -o actions-runner-linux-x64-2.311.0.tar.gz -L \
  https://github.com/actions/runner/releases/download/v2.311.0/actions-runner-linux-x64-2.311.0.tar.gz

# 3. Extract
tar xzf ./actions-runner-linux-x64-2.311.0.tar.gz

# 4. Get registration token from GitHub
# Go to: Settings → Actions → Runners → New self-hosted runner
# Copy the token shown

# 5. Configure runner
./config.sh \
  --url https://github.com/YOUR_ORG/hsi_onprem_cicd \
  --token YOUR_REGISTRATION_TOKEN \
  --name webmethods-build-server \
  --labels webmethods-build,self-hosted,linux \
  --work _work

# 6. Install as service
sudo ./svc.sh install

# 7. Start service
sudo ./svc.sh start

# 8. Verify status
sudo ./svc.sh status
```

#### On Windows Build Server:

```powershell
# 1. Create runner directory
New-Item -Path "C:\actions-runner" -ItemType Directory
cd C:\actions-runner

# 2. Download runner
Invoke-WebRequest -Uri https://github.com/actions/runner/releases/download/v2.311.0/actions-runner-win-x64-2.311.0.zip -OutFile actions-runner-win-x64-2.311.0.zip

# 3. Extract
Expand-Archive -Path actions-runner-win-x64-2.311.0.zip -DestinationPath .

# 4. Configure (use token from GitHub)
.\config.cmd --url https://github.com/YOUR_ORG/hsi_onprem_cicd --token YOUR_TOKEN --name webmethods-build-server --labels webmethods-build,self-hosted,windows

# 5. Install as service
.\svc.sh install

# 6. Start service
.\svc.sh start
```

**Verify Runner:**
- Go to: **Settings → Actions → Runners**
- Status should show: 🟢 Idle

### Step 4: Configure Environment Protection (15 minutes)

#### Create DEV Environment

1. Go to: **Settings → Environments → New environment**
2. Name: `DEV`
3. **No protection rules** (auto-deploy)
4. Click **Configure environment**

#### Create TEST Environment

1. Go to: **Settings → Environments → New environment**
2. Name: `TEST`
3. Configure protection rules:
   - ✅ **Required reviewers**: Add 1 reviewer (QA Lead)
   - ⏱️ **Wait timer**: 0 minutes
4. **Deployment branches**: Select "Selected branches"
   - Add rule: `main`
5. Click **Save protection rules**

#### Create PROD Environment

1. Go to: **Settings → Environments → New environment**
2. Name: `PROD`
3. Configure protection rules:
   - ✅ **Required reviewers**: Add 2 reviewers (Release Manager + Tech Lead)
   - ⏱️ **Wait timer**: 30 minutes
   - ✅ **Prevent self-review**: Enabled
4. **Deployment branches**: Select "Selected branches"
   - Add rule: `main`
5. Click **Save protection rules**

### Step 5: Configure Branch Protection (10 minutes)

1. Go to: **Settings → Branches → Add branch protection rule**

2. **Branch name pattern**: `main`

3. Enable the following rules:
   - ✅ **Require a pull request before merging**
     - Required approvals: 1
     - ✅ Dismiss stale pull request approvals when new commits are pushed
   
   - ✅ **Require status checks to pass before merging**
     - ✅ Require branches to be up to date before merging
     - Add status checks:
       - `build`
       - `test-dev`
   
   - ✅ **Require conversation resolution before merging**
   
   - ✅ **Include administrators**

4. Click **Create**

### Step 6: Update Configuration Files (15 minutes)

#### Update ENV.groovy

Edit `ENV.groovy` with your actual server details:

```groovy
environments {
    DEV {
        IntegrationServers {
            IS_DEV {
                host = 'YOUR-DEV-SERVER.company.com'  // Update this
                port = '5555'
                username = 'Administrator'
                pwd = 'manage'
                // ...
            }
        }
    }
    // Update TEST and PROD similarly
}
```

#### Update Variable Substitution Files

Edit files in `varsub/` directories:

```bash
# DEV
vi varsub/DEV/MyBusinessPackage.vs.properties

# TEST
vi varsub/TEST/MyBusinessPackage.vs.properties

# PROD
vi varsub/PROD/MyBusinessPackage.vs.properties
```

Update with your actual:
- Database URLs
- API endpoints
- File paths
- Credentials (use secrets for sensitive data)

### Step 7: Test the Pipeline (20 minutes)

#### Initial Test Commit

```bash
# 1. Create a test branch
git checkout -b test/initial-setup

# 2. Make a small change
echo "# Test" >> README.md

# 3. Commit and push
git add README.md
git commit -m "test: initial pipeline test"
git push origin test/initial-setup

# 4. Create Pull Request
# Go to GitHub and create PR from test/initial-setup to main
```

#### Monitor the Pipeline

1. Go to: **Actions** tab
2. You should see workflow running
3. Monitor each job:
   - ✅ Build
   - ⏸️ Deploy to DEV (waiting for main merge)

#### Merge to Main

1. Approve and merge the PR
2. Watch the full pipeline execute:
   - ✅ Build
   - ✅ Deploy to DEV
   - ✅ Test on DEV
   - ⏸️ Deploy to TEST (waiting for approval)

3. **Approve TEST deployment**:
   - Click on workflow run
   - Click **Review deployments**
   - Select **TEST**
   - Click **Approve and deploy**

4. **Approve PROD deployment** (after 30-minute wait):
   - Same process as TEST
   - Requires 2 approvals

### Step 8: Verify Deployments (15 minutes)

#### Check DEV Server

```bash
# SSH to DEV server
ssh user@dev-server.company.com

# Check package installation
cd $SAG_HOME/IntegrationServer/instances/default/packages
ls -la | grep MyBusinessPackage

# Check logs
tail -f $SAG_HOME/IntegrationServer/instances/default/logs/server.log
```

#### Check TEST Servers

```bash
# Check all 3 TEST servers
for server in test1 test2 test3; do
  echo "Checking ${server}..."
  ssh user@${server}-server.company.com "ls -la $SAG_HOME/IntegrationServer/instances/default/packages | grep MyBusinessPackage"
done
```

#### Check PROD Servers

```bash
# Check all 3 PROD servers
for server in prod1 prod2 prod3; do
  echo "Checking ${server}..."
  ssh user@${server}-server.company.com "ls -la $SAG_HOME/IntegrationServer/instances/default/packages | grep MyBusinessPackage"
done
```

## 🎯 Quick Reference

### Common Commands

```bash
# Trigger manual deployment
# Go to: Actions → webMethods Multi-Server CI/CD Pipeline → Run workflow

# View deployment history
# Go to: Environments → Select environment → View deployment history

# Rollback deployment
# Go to: Actions → Rollback Deployment → Run workflow
# Enter: Environment, Build number, Reason

# View build artifacts
# Go to: Actions → Select workflow run → Artifacts section
```

### Troubleshooting

#### Runner Not Connecting

```bash
# Check runner status
sudo systemctl status actions.runner.*

# View runner logs
sudo journalctl -u actions.runner.* -f

# Restart runner
sudo ./svc.sh stop
sudo ./svc.sh start
```

#### Build Fails

```bash
# Check ABE installation
ls -la $SAG_HOME/common/AssetBuildEnvironment

# Verify build properties
cat build.properties

# Check workspace permissions
ls -la $GITHUB_WORKSPACE
```

#### Deployment Fails

```bash
# Check Deployer logs
tail -f $SAG_HOME/IntegrationServer/instances/default/packages/WmDeployer/logs/CLI.log

# Verify connectivity
telnet deployer.company.com 5555

# Check Project Automator output
cat tmp/*.xml
```

#### Tests Fail

```bash
# View test reports
cat report/TEST-*.xml

# Check test server connectivity
curl -u Administrator:manage http://dev-server.company.com:5555

# Run tests locally
cd sagdevops-ci-assets
ant -f build.xml test -Dproject.properties=../build.properties
```

## 📊 Monitoring & Maintenance

### Daily Checks

- [ ] Review failed workflows
- [ ] Check runner status
- [ ] Monitor deployment approvals
- [ ] Review test results

### Weekly Tasks

- [ ] Clean up old artifacts
- [ ] Review and update secrets
- [ ] Check disk space on build server
- [ ] Update runner if new version available

### Monthly Tasks

- [ ] Review and update branch protection rules
- [ ] Audit deployment history
- [ ] Update documentation
- [ ] Review and optimize pipeline performance

## 🔐 Security Best Practices

1. **Rotate Secrets Regularly**
   ```bash
   # Update secrets every 90 days
   # Go to: Settings → Secrets → Update secret
   ```

2. **Audit Access**
   ```bash
   # Review who has access to:
   # - Repository
   # - Environments
   # - Secrets
   ```

3. **Monitor Deployments**
   ```bash
   # Set up notifications for:
   # - Failed deployments
   # - Unauthorized access attempts
   # - Secret access
   ```

## 📞 Support

### Getting Help

- **Documentation**: See README.md
- **Issues**: Create GitHub issue
- **Email**: devops-team@company.com
- **Slack**: #webmethods-cicd

### Escalation Path

1. **Level 1**: DevOps Team
2. **Level 2**: Platform Team
3. **Level 3**: Software AG Support

## ✅ Post-Setup Checklist

- [ ] All secrets configured
- [ ] Self-hosted runner installed and running
- [ ] All environments created with protection rules
- [ ] Branch protection rules configured
- [ ] Configuration files updated with actual values
- [ ] Initial test deployment successful
- [ ] All team members have appropriate access
- [ ] Documentation reviewed and understood
- [ ] Monitoring and alerts configured
- [ ] Backup and rollback procedures tested

## 🎉 Success Criteria

Your CI/CD pipeline is successfully implemented when:

✅ Code pushed to main automatically deploys to DEV
✅ Tests run automatically after DEV deployment
✅ TEST deployment requires manual approval
✅ PROD deployment requires 2 approvals and 30-minute wait
✅ All deployments are logged and traceable
✅ Rollback can be performed within 5 minutes
✅ Team members can view deployment status
✅ Failed deployments trigger notifications

---

**Setup Complete!** 🚀

Your webMethods CI/CD pipeline is now ready for production use.

For questions or issues, refer to the troubleshooting section or contact the DevOps team.