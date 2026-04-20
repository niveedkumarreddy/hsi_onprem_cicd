# Repository Separation Implementation Guide

## 📋 Overview

This guide provides step-by-step instructions to separate the monolithic `hsi_onprem_cicd` repository into two repositories:

1. **webmethods-cicd-framework** - Reusable CI/CD framework
2. **hsi-onprem-application** - Application-specific code

## 🎯 Benefits

- **Reusability**: Use framework across multiple projects
- **Maintainability**: Update framework independently
- **Version Control**: Pin applications to specific framework versions
- **Separation of Concerns**: Clear boundary between infrastructure and application
- **Governance**: Centralized CI/CD standards

## 📁 Prepared Structure

The repository has been prepared with two directories:

### `framework/` Directory
Contains all CI/CD framework files ready to be moved to the framework repository:
- `.github/workflows/` - CI/CD workflows
- `cicd/` - Build scripts
- `deployer/` - Deployment configurations
- `build.properties` - SAG reference properties
- `custom.properties.template` - Template for projects
- `README.md` - Framework documentation
- `.gitignore` - Framework-specific ignores
- `CHANGELOG.md` - Version history

### `application/` Directory
Contains all application-specific files ready to be moved to the application repository:
- `packages/` - webMethods IS packages
- `tests/` - Test cases
- `varsub/` - Variable substitution files
- `custom.properties` - Project configuration
- `.github/workflows/deploy.yml` - Application workflow (if created)
- `.gitignore` - Application-specific ignores
- `.gitmodules` - Submodule configuration
- `README.md` - Application documentation

## 🚀 Implementation Steps

### Phase 1: Create Framework Repository

#### Step 1.1: Initialize Framework Repository

```bash
# Navigate to parent directory
cd ..

# Create framework repository directory
mkdir webmethods-cicd-framework
cd webmethods-cicd-framework

# Initialize git
git init

# Copy framework files from prepared structure
cp -r ../hsi_onprem_cicd/framework/* .
cp -r ../hsi_onprem_cicd/framework/.github .
cp ../hsi_onprem_cicd/framework/.gitignore .

# Verify structure
ls -la
```

#### Step 1.2: Commit Framework Files

```bash
# Add all files
git add .

# Create initial commit
git commit -m "Initial framework setup

- ANT build system
- Multi-environment deployment
- Variable substitution support
- GitHub Actions workflows
- Comprehensive documentation"

# Add remote (replace with your actual repository URL)
git remote add origin https://github.com/your-org/webmethods-cicd-framework.git

# Push to remote
git push -u origin main
```

#### Step 1.3: Create Version Tag

```bash
# Create v1.0.0 tag
git tag -a v1.0.0 -m "Initial release v1.0.0

Features:
- ANT-based build system
- Asset Build Environment integration
- Multi-environment deployment (DEV/TEST/QA/PROD)
- Variable substitution
- GitHub Actions workflows
- Rollback capability"

# Push tag
git push origin v1.0.0
```

### Phase 2: Create Application Repository

#### Step 2.1: Initialize Application Repository

```bash
# Navigate to parent directory
cd ..

# Create application repository directory
mkdir hsi-onprem-application
cd hsi-onprem-application

# Initialize git
git init

# Copy application files from prepared structure
cp -r ../hsi_onprem_cicd/application/* .
cp -r ../hsi_onprem_cicd/application/.github .
cp ../hsi_onprem_cicd/application/.gitignore .
cp ../hsi_onprem_cicd/application/.gitmodules .

# Verify structure
ls -la
```

#### Step 2.2: Add Framework as Submodule

```bash
# Add framework as submodule
git submodule add https://github.com/your-org/webmethods-cicd-framework.git .cicd

# Initialize and update submodule
git submodule update --init --recursive

# Pin to v1.0.0 (recommended for production)
cd .cicd
git checkout v1.0.0
cd ..

# Stage submodule
git add .cicd .gitmodules
```

#### Step 2.3: Commit Application Files

```bash
# Add all files
git add .

# Create initial commit
git commit -m "Initial application setup with CI/CD framework

Packages:
- AIACommonCoreLogger
- AIACommonLogClient
- AIACommonLogger
- AIACommonNotification
- CustomHIPAA
- CustHipaaHL7
- OH_Ops

CI/CD Framework: v1.0.0"

# Add remote (replace with your actual repository URL)
git remote add origin https://github.com/your-org/hsi-onprem-application.git

# Push to remote
git push -u origin main
```

### Phase 3: Configure GitHub

#### Step 3.1: Set Up GitHub Secrets (Framework Repository)

Not typically needed for framework repository unless using reusable workflows.

#### Step 3.2: Set Up GitHub Secrets (Application Repository)

1. Go to repository **Settings → Secrets and variables → Actions**
2. Add the following secrets:

**Repository Secrets:**
- `DEPLOYER_USERNAME` - Deployer username for all environments
- `DEPLOYER_PASSWORD` - Deployer password for DEV/TEST/QA

**Environment Secrets (if using different credentials per environment):**

Create environments: DEV, TEST, QA, PROD

For each environment, add:
- `DEPLOYER_USERNAME` - Environment-specific username
- `DEPLOYER_PASSWORD` - Environment-specific password
- `PROD_DEPLOYER_USERNAME` - Production username (PROD only)
- `PROD_DEPLOYER_PASSWORD` - Production password (PROD only)

#### Step 3.3: Configure Self-Hosted Runner

1. Go to **Settings → Actions → Runners**
2. Click **New self-hosted runner**
3. Follow instructions to install runner on your Windows server
4. Ensure runner has access to:
   - webMethods Installation (C:/IBM/webMethods)
   - ANT
   - Git with submodule support

### Phase 4: Test the Setup

#### Step 4.1: Test Framework Repository

```bash
cd webmethods-cicd-framework

# Verify all files are present
ls -la

# Check workflows
ls -la .github/workflows/

# Verify build scripts
ls -la cicd/
```

#### Step 4.2: Test Application Repository

```bash
cd hsi-onprem-application

# Verify submodule is initialized
git submodule status

# Test local build
ant -f .cicd/cicd/build.xml build -Dproject.properties=custom.properties

# If build succeeds, test deployment to DEV
ant -f .cicd/cicd/build.xml deploy \
  -Dproject.properties=custom.properties \
  -Dbda.targetEnv=DEV
```

#### Step 4.3: Test CI/CD Pipeline

1. Make a small change to application code
2. Commit and push to `develop` branch
3. Verify GitHub Actions workflow triggers
4. Check build and deployment logs
5. Verify deployment to DEV environment

### Phase 5: Archive Old Repository

#### Step 5.1: Create Archive Tag

```bash
cd hsi_onprem_cicd

# Create archive tag
git tag -a archive/pre-separation -m "Archive before repository separation

This repository has been split into:
- webmethods-cicd-framework: https://github.com/your-org/webmethods-cicd-framework
- hsi-onprem-application: https://github.com/your-org/hsi-onprem-application

Date: $(date)
"

# Push tag
git push origin archive/pre-separation
```

#### Step 5.2: Update README

```bash
# Update README.md
cat > README.md << 'EOF'
# HSI On-Premise CI/CD (ARCHIVED)

⚠️ **This repository has been archived and split into separate repositories:**

## New Repositories

### CI/CD Framework
**Repository**: https://github.com/your-org/webmethods-cicd-framework
**Purpose**: Reusable CI/CD framework for webMethods projects

### HSI Application
**Repository**: https://github.com/your-org/hsi-onprem-application
**Purpose**: HSI on-premise integration application

## Migration Date
April 20, 2026

## Archive Tag
`archive/pre-separation` - Contains the last state before separation

## For Historical Reference Only
This repository is kept for historical reference. All active development should use the new repositories.
EOF

# Commit and push
git add README.md
git commit -m "Archive repository - split into framework and application repos"
git push
```

#### Step 5.3: Archive Repository on GitHub

1. Go to repository **Settings**
2. Scroll to **Danger Zone**
3. Click **Archive this repository**
4. Confirm archival

## 🔄 Updating Framework in Application

### Update to Latest Version

```bash
cd hsi-onprem-application

# Update submodule to latest
cd .cicd
git checkout main
git pull origin main
cd ..

# Commit the update
git add .cicd
git commit -m "Update CI/CD framework to latest version"
git push
```

### Pin to Specific Version

```bash
cd hsi-onprem-application

# Update submodule to specific version
cd .cicd
git fetch --tags
git checkout v1.1.0
cd ..

# Commit the pinned version
git add .cicd
git commit -m "Pin CI/CD framework to v1.1.0"
git push
```

## 📊 Verification Checklist

### Framework Repository
- [ ] All CI/CD files present
- [ ] Workflows are valid
- [ ] Build scripts work independently
- [ ] Documentation is complete
- [ ] Version tag created (v1.0.0)
- [ ] Repository is public/accessible to application repos

### Application Repository
- [ ] All packages present
- [ ] Submodule initialized correctly
- [ ] Custom properties configured
- [ ] Variable substitution files present
- [ ] Local build succeeds
- [ ] Local deployment succeeds
- [ ] GitHub Actions workflow triggers
- [ ] Secrets configured correctly
- [ ] Self-hosted runner connected

### Old Repository
- [ ] Archive tag created
- [ ] README updated with new repository links
- [ ] Repository archived on GitHub

## 🆘 Troubleshooting

### Submodule Not Initialized

```bash
git submodule update --init --recursive
```

### Build Fails - Framework Not Found

```bash
# Verify submodule path
ls -la .cicd

# If empty, reinitialize
git submodule update --init --recursive
```

### Workflow Fails - Secrets Not Found

1. Verify secrets are added in GitHub repository settings
2. Check secret names match workflow references
3. Ensure environment protection rules allow workflow access

### Framework Update Breaks Application

```bash
# Revert to previous working version
cd .cicd
git checkout v1.0.0
cd ..
git add .cicd
git commit -m "Revert framework to v1.0.0"
git push
```

## 📚 Additional Resources

- [Framework README](framework/README.md)
- [Application README](application/README.md)
- [Property Configuration Guide](PROPERTY_CONFIGURATION_GUIDE.md)
- [Repository Separation Plan](REPOSITORY_SEPARATION_PLAN.md)

## 🎓 Best Practices

1. **Always pin framework versions in production**
2. **Test framework updates in DEV first**
3. **Document framework version in application README**
4. **Use semantic versioning for framework releases**
5. **Keep framework backward compatible when possible**
6. **Communicate framework updates to all teams**
7. **Maintain CHANGELOG in framework repository**

## ✅ Success Criteria

- ✅ Framework repository builds successfully
- ✅ Application repository builds successfully
- ✅ Submodule integration works
- ✅ CI/CD workflows execute correctly
- ✅ Deployment to all environments works
- ✅ Team can update framework independently
- ✅ Multiple projects can use same framework

---

**Implementation Date**: April 20, 2026
**Status**: Ready for Implementation
**Estimated Time**: 2-4 hours