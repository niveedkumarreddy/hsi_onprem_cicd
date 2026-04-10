# Quick Start: Simple QA Deployment

This guide shows how to use the existing framework for a **bare minimum setup** to deploy one package to QA.

---

## 🎯 Goal

Deploy a single webMethods package from Git to QA server automatically.

---

## ✅ What You Already Have

The framework is already set up with everything you need! You just need to:
1. Add your package
2. Configure QA server
3. Push to Git

---

## 📋 3-Step Setup (5 minutes)

### Step 1: Add Your Package

```bash
# Copy your webMethods package to the packages directory
cp -r /path/to/your/package packages/YourPackageName/

# Verify manifest exists
ls packages/YourPackageName/manifest.v3
```

### Step 2: Configure QA Server

**Option A: Use existing TEST environment as QA**

Edit `deployer/environments/TEST.properties`:
```properties
# QA Server Configuration (rename TEST to QA if you want)
TARGET_ENV=QA
PROJECT_PREFIX=QA
IS_HOST=your-qa-server.company.com
IS_PORT=5555
IS_USERNAME=Administrator
IS_PASSWORD=manage
UM_HOST=your-qa-um.company.com
UM_PORT=9000
DO_VARSUB=true
VARSUB_DIR=varsub/QA
```

**Option B: Create new QA environment**

Create `deployer/environments/QA.properties`:
```properties
TARGET_ENV=QA
PROJECT_PREFIX=QA
IS_HOST=qa-server.company.com
IS_PORT=5555
IS_USERNAME=Administrator
IS_PASSWORD=manage
UM_HOST=qa-um.company.com
UM_PORT=9000
DO_VARSUB=true
VARSUB_DIR=varsub/QA
```

### Step 3: Update Package in Template

Edit `deployer/ProjectAutomator.template.xml`:

Change package name from `MyBusinessPackage` to your package name:
```xml
<DeploymentCandidate>
    <SourceRepositoryName>@FBR_REPO_NAME@</SourceRepositoryName>
    <MapSetName>YourPackageName</MapSetName>  <!-- Change this -->
    <MapName>YourPackageName</MapName>        <!-- Change this -->
    <TargetServerAlias>@TARGET_ENV@_IS</TargetServerAlias>
    <DeploymentSetName>MainDeploymentSet</DeploymentSetName>
</DeploymentCandidate>
```

---

## 🚀 Deploy to QA

### Automatic Deployment (Recommended)

```bash
# 1. Commit your package
git add packages/YourPackageName/
git commit -m "Add YourPackageName"

# 2. Push to main branch
git push origin main

# ✅ GitHub Actions automatically:
#    - Builds your package
#    - Deploys to DEV (auto)
#    - Waits for TEST/QA approval
#    - Deploys to QA after approval
```

### Manual Deployment (Using Workflow Dispatch)

1. Go to GitHub: **Actions → webMethods Multi-Server CI/CD Pipeline**
2. Click **Run workflow**
3. Select:
   - Branch: `main`
   - Environment: `TEST` (or `QA` if you created it)
   - Skip tests: `true` (for quick deployment)
4. Click **Run workflow**

---

## 🎛️ Simplified Workflow (Skip DEV, Go Straight to QA)

If you want to skip DEV and deploy directly to QA, modify `.github/workflows/webmethods-cicd.yml`:

### Option 1: Comment Out DEV Jobs

```yaml
jobs:
  build:
    # ... keep this
  
  # deploy-dev:
  #   # ... comment out entire job
  
  # test-dev:
  #   # ... comment out entire job
  
  deploy-test:  # Use this as QA
    name: Deploy to QA
    needs: build  # Change from: needs: test-dev
    # ... rest of config
```

### Option 2: Rename TEST to QA

In `.github/workflows/webmethods-cicd.yml`, find and replace:
- `TEST` → `QA`
- `test1/test2/test3` → `qa` (single server)
- Update matrix to single server:

```yaml
deploy-test:
  name: Deploy to QA
  strategy:
    matrix:
      server: [qa]  # Single QA server
```

---

## 📦 Using Existing Framework for Simple QA Deploy

### Current Flow (Full):
```
Push → Build → DEV → Test DEV → TEST (3 servers) → Test TEST → PROD (3 servers)
```

### Simplified Flow (QA Only):
```
Push → Build → QA (1 server) → Done
```

### How to Achieve This:

**Edit `.github/workflows/webmethods-cicd.yml`:**

1. **Keep only these jobs:**
   - `build` - Builds the package
   - `deploy-test` - Rename to `deploy-qa`
   
2. **Remove these jobs:**
   - `deploy-dev`
   - `test-dev`
   - `test-test`
   - `deploy-prod`
   - `create-release`

3. **Simplify deploy-qa job:**

```yaml
deploy-qa:
  name: Deploy to QA
  needs: build
  runs-on: [self-hosted, webmethods-build]
  environment:
    name: QA
    url: https://qa-server.company.com:5555
  
  steps:
    - name: Checkout Source Code
      uses: actions/checkout@v4
    
    - name: Download Build Artifacts
      uses: actions/download-artifact@v4
      with:
        name: build-artifacts-${{ github.run_number }}
        path: .
    
    - name: Deploy to QA
      run: |
        cd ${CICD_HOME}
        ant -f build.xml deploy \
          -Dproject.properties=${{ github.workspace }}/build.properties \
          -Dbda.targetEnv=QA \
          -Dbda.buildNumber=${{ github.run_number }}
```

---

## 🔧 Local Testing (Before Git Push)

Test deployment locally before pushing to Git:

```bash
# 1. Navigate to cicd directory
cd cicd

# 2. Run deployment to QA
ant -f build.xml deploy \
  -Dproject.properties=../build.properties \
  -Dbda.targetEnv=QA \
  -Dbda.buildNumber=999 \
  -Dbda.projectName=hsi_onprem_cicd \
  -DfbrRepoName=hsi_onprem_cicd_999 \
  -DfbrRepoDir=../tmp/fbr/hsi_onprem_cicd_999 \
  -Dconfig.deployer.deployerHost=deployer.company.com \
  -Dconfig.deployer.deployerPort=5555 \
  -Dconfig.deployer.deployerUsername=Administrator \
  -Dconfig.deployer.deployerPassword=manage

# 3. Check generated deployment XML
cat ../tmp/ProjectAutomator_QA.xml
```

---

## 📝 Minimal Files Needed

For a simple QA deployment, you only need these files:

```
✅ Required:
├── .github/workflows/webmethods-cicd.yml  (simplified)
├── cicd/
│   ├── build.xml
│   ├── build-abe.xml
│   └── build-deployer.xml
├── deployer/
│   ├── ProjectAutomator.template.xml
│   └── environments/
│       └── QA.properties
├── packages/
│   └── YourPackageName/
│       └── manifest.v3
└── build.properties

❌ Not needed for simple QA deploy:
├── tests/                    (skip if no testing)
├── varsub/                   (skip if no variable substitution)
├── .github/workflows/rollback.yml  (skip if no rollback needed)
└── cicd/build-test.xml      (skip if no testing)
```

---

## 🎯 Ultra-Simple: Manual Deployment Script

If you don't want to use GitHub Actions at all, create a simple deployment script:

**File: `deploy-to-qa.sh`**

```bash
#!/bin/bash

# Configuration
QA_SERVER="qa-server.company.com"
QA_PORT="5555"
PACKAGE_NAME="YourPackageName"
SAG_HOME="/opt/softwareag"

echo "Deploying ${PACKAGE_NAME} to QA..."

# 1. Build package
cd ${SAG_HOME}/common/AssetBuildEnvironment/bin
./build.sh \
  -Dpackage.dir=$(pwd)/packages/${PACKAGE_NAME} \
  -Doutput.dir=$(pwd)/build

# 2. Generate deployment XML
sed -e "s/@IS_HOST@/${QA_SERVER}/g" \
    -e "s/@IS_PORT@/${QA_PORT}/g" \
    deployer/ProjectAutomator.template.xml > deploy.xml

# 3. Deploy
cd ${SAG_HOME}/IntegrationServer/instances/default/packages/WmDeployer/bin
./projectAutomator.sh -f $(pwd)/deploy.xml

echo "✅ Deployment complete!"
```

**Usage:**
```bash
chmod +x deploy-to-qa.sh
./deploy-to-qa.sh
```

---

## 🚦 Quick Checklist

Before first deployment:

- [ ] Package copied to `packages/YourPackageName/`
- [ ] QA server configured in `deployer/environments/QA.properties`
- [ ] Package name updated in `deployer/ProjectAutomator.template.xml`
- [ ] GitHub self-hosted runner is running
- [ ] Self-hosted runner has access to webMethods installation
- [ ] Deployer credentials configured in GitHub Secrets

---

## 🎉 That's It!

You now have a simple Git → QA deployment pipeline using the existing framework!

**Next push to main branch will automatically deploy to QA.**

---

## 📞 Need Help?

- **Full documentation:** See `FRAMEWORK_DOCUMENTATION.md`
- **Setup guide:** See `SETUP_GUIDE.md`
- **Troubleshooting:** See `README.md` troubleshooting section

---

**Quick Start Version:** 1.0  
**Framework Version:** 2.0 (Template-Based)  
**Complexity:** Minimal  
**Setup Time:** 5 minutes