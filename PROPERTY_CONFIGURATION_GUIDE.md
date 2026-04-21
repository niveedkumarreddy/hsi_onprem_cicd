# Property Configuration Guide

## Overview
This guide explains how properties are configured and overridden in the webMethods CI/CD framework.

**Date**: 2026-04-20

---

## 🎯 Property Hierarchy

Properties are loaded and can be overridden in this order (later overrides earlier):

1. **build.properties** (default values)
2. **Environment-specific properties** (deployer/environments/*.properties)
3. **Command-line parameters** (-Dproperty=value)
4. **GitHub workflow inline properties** (generated dynamically)

---

## 📁 Property Files

### 1. build.properties (Root Level)
**Location**: `build.properties`  
**Purpose**: Default values for local development  
**Usage**: Loaded automatically by ANT

```properties
# Software AG Installation
SAGHome=C:/IBM/webMethods

# Deployer Configuration
config.deployer.deployerHost=localhost
config.deployer.deployerPort=5555
config.deployer.deployerUsername=Administrator
config.deployer.deployerPassword=manage
```

**Note**: These are **defaults** for local development. They are overridden in CI/CD.

---

### 2. Environment-Specific Properties
**Location**: `deployer/environments/*.properties`  
**Purpose**: Environment-specific server configurations  
**Files**:
- `DEV.properties`
- `TEST.properties`
- `QA.properties`
- `PROD.properties`

**Example** (`deployer/environments/DEV.properties`):
```properties
TARGET_ENV=DEV
PROJECT_PREFIX=DEV
IS_HOST=localhost
IS_PORT=5555
IS_USERNAME=Administrator
IS_PASSWORD=manage
UM_HOST=localhost
UM_PORT=9000
DO_VARSUB=true
VARSUB_DIR=varsub/DEV
```

**Usage**: Loaded by `build-deployer.xml` based on `bda.targetEnv`

---

## 🔄 How Properties Work

### Local Development

When running locally:
```bash
cd cicd
ant -f build.xml deploy -Dbda.targetEnv=DEV
```

**Property Loading Order**:
1. Loads `build.properties` (defaults)
2. Loads `deployer/environments/DEV.properties` (environment-specific)
3. Uses command-line overrides if provided

**Result**: Uses localhost with default credentials

---

### GitHub Actions CI/CD

When running in GitHub Actions:

**Step 1: Workflow Environment Variables**
```yaml
env:
  SAG_HOME: C:/IBM/webMethods
  PROJECT_NAME: hsi_onprem_cicd
```

**Step 2: Dynamic Property Generation**
```yaml
- name: Prepare Deployment Configuration
  run: |
    @"
    SAGHome=$env:SAG_HOME
    bda.targetEnv=DEV
    bda.projectName=$env:PROJECT_NAME
    bda.buildNumber=${{ github.run_number }}
    config.deployer.deployerHost=${{ secrets.DEPLOYER_HOST }}
    config.deployer.deployerPort=${{ secrets.DEPLOYER_PORT }}
    config.deployer.deployerUsername=${{ secrets.DEPLOYER_USERNAME }}
    config.deployer.deployerPassword=${{ secrets.DEPLOYER_PASSWORD }}
    "@ | Out-File -FilePath deploy.properties
```

**Step 3: ANT Execution**
```yaml
ant -f build.xml deploy -Dproject.properties=deploy.properties
```

**Result**: 
- Uses GitHub Secrets for credentials
- Uses workflow environment variables for paths
- Completely overrides build.properties defaults

---

## 🔐 Security Model

### Local Development (build.properties)
```properties
# Safe defaults for local testing
config.deployer.deployerHost=localhost
config.deployer.deployerUsername=Administrator
config.deployer.deployerPassword=manage
```
✅ **Safe**: Only works on localhost

### CI/CD (GitHub Secrets)
```yaml
config.deployer.deployerHost=${{ secrets.DEPLOYER_HOST }}
config.deployer.deployerUsername=${{ secrets.DEPLOYER_USERNAME }}
config.deployer.deployerPassword=${{ secrets.DEPLOYER_PASSWORD }}
```
✅ **Secure**: Credentials stored in GitHub Secrets, never in code

---

## 📝 Property Override Examples

### Example 1: Override SAGHome for Local Build
```bash
ant -f build.xml build -DSAGHome=/opt/softwareag
```

### Example 2: Override Deployer Host
```bash
ant -f build.xml deploy \
  -Dbda.targetEnv=DEV \
  -Dconfig.deployer.deployerHost=dev-server.company.com
```

### Example 3: Use Custom Properties File
```bash
ant -f build.xml deploy -Dproject.properties=my-custom.properties
```

### Example 4: Override Multiple Properties
```bash
ant -f build.xml deploy \
  -Dbda.targetEnv=TEST \
  -Dconfig.deployer.deployerHost=test-server.company.com \
  -Dconfig.deployer.deployerPort=5555 \
  -Dconfig.deployer.deployerUsername=testuser \
  -Dconfig.deployer.deployerPassword=testpass
```

---

## 🎛️ Configuration Scenarios

### Scenario 1: Local Development
**Goal**: Test deployment on local Integration Server

**Setup**:
1. Update `build.properties` with your local SAGHome
2. Ensure local IS is running on localhost:5555
3. Run: `ant -f build.xml deploy -Dbda.targetEnv=DEV`

**Properties Used**:
- `build.properties` (defaults)
- `deployer/environments/DEV.properties` (localhost config)

---

### Scenario 2: Manual Deployment to Remote Server
**Goal**: Deploy to a specific remote server

**Setup**:
```bash
ant -f build.xml deploy \
  -Dbda.targetEnv=TEST \
  -Dconfig.deployer.deployerHost=test-server.company.com \
  -Dconfig.deployer.deployerUsername=deployuser \
  -Dconfig.deployer.deployerPassword=securepass
```

**Properties Used**:
- `build.properties` (base defaults)
- `deployer/environments/TEST.properties` (TEST config)
- Command-line overrides (remote server details)

---

### Scenario 3: CI/CD Automated Deployment
**Goal**: Automated deployment via GitHub Actions

**Setup**: Push to main branch

**Properties Used**:
- Workflow generates `deploy.properties` dynamically
- Uses GitHub Secrets for credentials
- Uses workflow environment variables for paths
- Completely bypasses `build.properties` defaults

---

## 🔍 Property Resolution Flow

```
┌─────────────────────────────────────┐
│  1. Load build.properties           │
│     (Default values)                │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  2. Load environment properties     │
│     (deployer/environments/*.props) │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  3. Apply command-line overrides    │
│     (-Dproperty=value)              │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  4. Use in ANT build                │
│     (Final property values)         │
└─────────────────────────────────────┘
```

---

## ⚙️ ANT Property Loading

### In build.xml
```xml
<!-- Load properties file - command-line -D has highest precedence -->
<property name="project.properties" value="build.properties"/>
<property file="${basedir}/${project.properties}"/>
```

### In build-deployer.xml
```xml
<!-- Load environment-specific properties -->
<property file="${basedir}/deployer/environments/${bda.targetEnv}.properties"/>
```

**Key Points**:
1. ANT properties are **immutable** (first value wins)
2. Command-line `-D` properties are set **before** file loading
3. Therefore, command-line overrides always win

---

## 🚀 Best Practices

### For Local Development:
1. ✅ Update `build.properties` with your local paths
2. ✅ Use localhost defaults
3. ✅ Never commit real credentials

### For CI/CD:
1. ✅ Use GitHub Secrets for credentials
2. ✅ Generate properties dynamically in workflow
3. ✅ Never hardcode credentials in workflows

### For Production:
1. ✅ Update `deployer/environments/PROD.properties` with real server names
2. ✅ Use secure credential management
3. ✅ Test in lower environments first

---

## 📋 Property Checklist

Before deployment, verify:

- [ ] SAGHome points to correct installation
- [ ] Target environment is correct (DEV/TEST/PROD)
- [ ] Deployer host/port are correct
- [ ] Credentials are valid
- [ ] FBR repository path exists
- [ ] Variable substitution directory exists
- [ ] All required properties are set

---

## 🔧 Troubleshooting

### Issue: "SAGHome not found"
**Solution**: Set SAGHome via command line:
```bash
ant -f build.xml build -DSAGHome=/path/to/sag
```

### Issue: "Cannot connect to deployer"
**Solution**: Check deployer host/port:
```bash
ant -f build.xml deploy \
  -Dconfig.deployer.deployerHost=correct-host \
  -Dconfig.deployer.deployerPort=5555
```

### Issue: "Authentication failed"
**Solution**: Verify credentials:
```bash
ant -f build.xml deploy \
  -Dconfig.deployer.deployerUsername=correct-user \
  -Dconfig.deployer.deployerPassword=correct-pass
```

---

## 📚 Related Documentation

- `build.properties` - Default property values
- `deployer/environments/*.properties` - Environment-specific configs
- `.github/workflows/webmethods-cicd.yml` - CI/CD property generation
- `HARDCODING_FIXES.md` - Hardcoding removal details

---

**Created**: 2026-04-20  
**Author**: Bob (AI Code Assistant)  
**Status**: ✅ COMPLETE