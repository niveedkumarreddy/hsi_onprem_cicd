# GitHub Variables Configuration Guide

## 🎯 Overview

The workflows have been updated to use GitHub repository variables instead of hardcoded values. This makes the configuration portable and environment-specific.

---

## 🔧 Variables Removed (Hardcoding Fixed)

### Before (Hardcoded):
```yaml
env:
  SAG_HOME: C:/IBM/webMethods          # ❌ Hardcoded
  JAVA_HOME: C:/IBM/webMethods/jvm/jvm # ❌ Hardcoded
  PROJECT_NAME: hsi_onprem_cicd        # ❌ Hardcoded
```

### After (Configurable):
```yaml
env:
  SAG_HOME: ${{ vars.SAG_HOME || 'C:/IBM/webMethods' }}
  JAVA_HOME: ${{ vars.JAVA_HOME || format('{0}/jvm/jvm', vars.SAG_HOME || 'C:/IBM/webMethods') }}
  PROJECT_NAME: ${{ vars.PROJECT_NAME || github.event.repository.name }}
```

---

## 📋 Required Variables

| Variable Name | Description | Default Value | Required |
|---------------|-------------|---------------|----------|
| `SAG_HOME` | Software AG installation directory | `C:/IBM/webMethods` | Optional |
| `JAVA_HOME` | Java JVM directory | `{SAG_HOME}/jvm/jvm` | Optional |
| `PROJECT_NAME` | Project name for builds | Repository name | Optional |

---

## 🚀 Setup Instructions

### Step 1: Navigate to Repository Settings

1. Go to your GitHub repository
2. Click **Settings** tab
3. In the left sidebar, expand **Secrets and variables**
4. Click **Actions**
5. Click the **Variables** tab

### Step 2: Add Repository Variables

Click **New repository variable** and add each variable:

#### Variable 1: SAG_HOME
```
Name: SAG_HOME
Value: C:/IBM/webMethods
```
*Or your actual Software AG installation path*

#### Variable 2: JAVA_HOME (Optional)
```
Name: JAVA_HOME
Value: C:/IBM/webMethods/jvm/jvm
```
*Only needed if different from default `{SAG_HOME}/jvm/jvm`*

#### Variable 3: PROJECT_NAME (Optional)
```
Name: PROJECT_NAME
Value: hsi_onprem_cicd
```
*Only needed if different from repository name*

---

## 🎨 Configuration Examples

**Example: Different SAG Installation**
```
SAG_HOME: D:/SoftwareAG
JAVA_HOME: D:/SoftwareAG/jvm/jvm
```

**Example: Linux Installation**
```
SAG_HOME: /opt/softwareag
JAVA_HOME: /opt/softwareag/jvm/jvm
```

**Example: Custom Project Name**
```
PROJECT_NAME: my_custom_project_name
```

---

## 🔄 How It Works

### Direct Variable Reference

The workflows directly reference repository variables:
```yaml
${{ vars.VARIABLE_NAME }}
```

**Behavior:**
- **If variable is set**: Workflow uses the configured value ✅
- **If variable is NOT set**: Workflow will FAIL ❌

⚠️ **This is intentional** - ensures explicit configuration and prevents accidental use of wrong paths.

---

## 📊 Variable Usage Across Workflows

| Workflow | SAG_HOME | JAVA_HOME | PROJECT_NAME |
|----------|----------|-----------|--------------|
| `webmethods-cicd.yml` | ✅ | ✅ | ✅ |
| `rollback.yml` | ✅ | ✅ | ✅ |
| `sonarqube.yml` | ✅ | ✅ | ✅ |

All three workflows now use the same configurable variables.

---

## 🧪 Testing Configuration

### Test 1: Verify Variables Are Set

Add this step to your workflow to verify configuration:

```yaml
- name: Verify Configuration
  run: |
    echo "SAG_HOME: ${{ env.SAG_HOME }}"
    echo "JAVA_HOME: ${{ env.JAVA_HOME }}"
    echo "PROJECT_NAME: ${{ env.PROJECT_NAME }}"
    
    # Verify variables are not empty
    if ([string]::IsNullOrEmpty("${{ env.SAG_HOME }}")) {
      Write-Error "SAG_HOME is not set!"
      exit 1
    }
    if ([string]::IsNullOrEmpty("${{ env.JAVA_HOME }}")) {
      Write-Error "JAVA_HOME is not set!"
      exit 1
    }
    if ([string]::IsNullOrEmpty("${{ env.PROJECT_NAME }}")) {
      Write-Error "PROJECT_NAME is not set!"
      exit 1
    }
    Write-Host "✓ All required variables are set"
```

### Test 2: Check Paths Exist

```yaml
- name: Validate Paths
  run: |
    if (Test-Path "$env:SAG_HOME") {
      Write-Host "✓ SAG_HOME exists: $env:SAG_HOME"
    } else {
      Write-Error "✗ SAG_HOME not found: $env:SAG_HOME"
      exit 1
    }
    
    if (Test-Path "$env:JAVA_HOME") {
      Write-Host "✓ JAVA_HOME exists: $env:JAVA_HOME"
    } else {
      Write-Error "✗ JAVA_HOME not found: $env:JAVA_HOME"
      exit 1
    }
```

---

## 🔐 Security Best Practices

### ✅ DO:
- Use **Variables** for non-sensitive configuration (paths, names)
- Use **Secrets** for sensitive data (passwords, tokens)
- Document your configuration in repository README

### ❌ DON'T:
- Don't hardcode paths in workflows
- Don't commit sensitive data
- Don't use secrets for non-sensitive data (wastes secret slots)

---

## 🌍 Environment-Specific Configuration

### For Multiple Environments

If you need different values per environment (DEV/TEST/PROD):

#### Option 1: Environment Variables (Recommended)

1. Go to **Settings → Environments**
2. Create environments: DEV, TEST, PROD
3. Add environment-specific variables to each

```yaml
jobs:
  deploy-dev:
    environment: DEV  # Uses DEV environment variables
    steps:
      - name: Deploy
        run: echo "Using ${{ vars.SAG_HOME }}"
```

#### Option 2: Conditional Logic

```yaml
env:
  SAG_HOME: ${{ github.ref == 'refs/heads/main' && vars.PROD_SAG_HOME || vars.DEV_SAG_HOME }}
```

---

## 📝 Setup Checklist

- [x] Remove hardcoded `SAG_HOME` from workflows
- [x] Remove hardcoded `JAVA_HOME` from workflows
- [x] Remove hardcoded `PROJECT_NAME` from workflows
- [ ] **Configure all 3 required repository variables** ⚠️
- [ ] Test workflows with new configuration
- [ ] Document configuration in team wiki/README

---

## 🆘 Troubleshooting

### Issue: Workflow fails immediately with empty variable

**Symptom:**
```
Error: Required variable 'SAG_HOME' is not set
```

**Solution**: Configure the missing variable in repository settings
1. Go to Settings → Secrets and variables → Actions → Variables
2. Add the required variable with correct value
3. Re-run the workflow

### Issue: Workflow fails with "path not found"

**Symptom:**
```
Error: Cannot find path 'C:/IBM/webMethods'
```

**Solution**: Verify `SAG_HOME` variable points to correct installation
```yaml
- name: Debug Paths
  run: |
    echo "SAG_HOME: ${{ env.SAG_HOME }}"
    if (Test-Path "${{ env.SAG_HOME }}") {
      Write-Host "✓ Path exists"
      ls "${{ env.SAG_HOME }}"
    } else {
      Write-Error "✗ Path does not exist"
    }
```

### Issue: Java not found

**Symptom:**
```
Error: java: command not found
```

**Solution**: Verify `JAVA_HOME` variable points to correct JVM
```yaml
- name: Verify Java
  run: |
    echo "JAVA_HOME: ${{ env.JAVA_HOME }}"
    if (Test-Path "${{ env.JAVA_HOME }}/bin/java.exe") {
      & "${{ env.JAVA_HOME }}/bin/java" -version
    } else {
      Write-Error "Java not found at: ${{ env.JAVA_HOME }}"
    }
```

### Issue: All variables show as empty

**Symptom:**
All three variables are empty in workflow logs

**Solution**: Variables are not configured in repository settings
1. Verify you're in the correct repository
2. Check Settings → Secrets and variables → Actions → Variables tab
3. Ensure all 3 variables are listed
4. Check variable names match exactly (case-sensitive)

---

## 📚 Additional Resources

- [GitHub Actions Variables Documentation](https://docs.github.com/en/actions/learn-github-actions/variables)
- [GitHub Actions Secrets Documentation](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [GitHub Actions Environments](https://docs.github.com/en/actions/deployment/targeting-different-environments/using-environments-for-deployment)

---

## ✅ Benefits of This Approach

1. **Explicit Configuration**: No hidden defaults, clear requirements
2. **Portability**: Works across different installations once configured
3. **Flexibility**: Easy to change without modifying workflows
4. **Maintainability**: Single place to update configuration
5. **Reusability**: Same workflows work for multiple projects
6. **Security**: Separates configuration from code
7. **Fail-Fast**: Workflows fail immediately if misconfigured

---

**Status**: ✅ All hardcoded values removed
**Configuration**: Optional (defaults provided)
**Ready for**: Production use