# Migration Guide: From Groovy to Static XML Templates

## Overview

This guide documents the migration from Groovy-based Project Automator generation to static XML templates with ANT property replacement.

## What Changed

### Before (Groovy-based)
```
Dependencies: Java + ANT + Groovy
Files: ENV.groovy + GenerateProjectAutomator.groovy
Process: Groovy script reads ENV.groovy → generates PA XML
```

### After (Template-based)
```
Dependencies: Java + ANT only
Files: ProjectAutomator.template.xml + environment properties
Process: ANT copies template → replaces tokens → generates PA XML
```

## Files Removed

### ❌ No Longer Needed:
1. `cicd/scripts/GenerateProjectAutomator.groovy` - Groovy script (can be deleted)
2. `ENV.groovy` - Groovy environment definition (can be deleted)

**Note:** These files are kept for reference but are no longer used by the framework.

## Files Added

### ✅ New Files:
1. `deployer/ProjectAutomator.template.xml` - XML template with @TOKEN@ placeholders
2. `deployer/environments/DEV.properties` - DEV environment configuration
3. `deployer/environments/TEST.properties` - TEST environment configuration
4. `deployer/environments/PROD.properties` - PROD environment configuration

## Files Modified

### 📝 Updated Files:
1. `cicd/build-deployer.xml` - Replaced Groovy execution with template copying

## How It Works Now

### 1. Template File Structure

**File:** `deployer/ProjectAutomator.template.xml`

Contains placeholders in format `@TOKEN_NAME@`:
```xml
<ProjectName>@PROJECT_PREFIX@_@BUILD_NUMBER@_@PROJECT_NAME@</ProjectName>
<host>@IS_HOST@</host>
<port>@IS_PORT@</port>
```

### 2. Environment Properties

**File:** `deployer/environments/DEV.properties`

Contains actual values:
```properties
PROJECT_PREFIX=DEV
IS_HOST=dev-server.company.com
IS_PORT=5555
```

### 3. ANT Processing

**File:** `cicd/build-deployer.xml`

ANT's `<copy>` task with `<filterset>`:
```xml
<copy file="deployer/ProjectAutomator.template.xml"
      tofile="tmp/ProjectAutomator_DEV.xml">
    <filterset>
        <filter token="PROJECT_PREFIX" value="${PROJECT_PREFIX}"/>
        <filter token="IS_HOST" value="${IS_HOST}"/>
        <filter token="IS_PORT" value="${IS_PORT}"/>
    </filterset>
</copy>
```

### 4. Result

Generated file: `tmp/ProjectAutomator_DEV.xml`
```xml
<ProjectName>DEV_123_hsi_onprem_cicd</ProjectName>
<host>dev-server.company.com</host>
<port>5555</port>
```

## Migration Steps

### Step 1: Verify New Files Exist

```bash
# Check template file
ls -la deployer/ProjectAutomator.template.xml

# Check environment properties
ls -la deployer/environments/DEV.properties
ls -la deployer/environments/TEST.properties
ls -la deployer/environments/PROD.properties

# Check updated build file
grep -A 10 "Generate Project Automator XML from Template" cicd/build-deployer.xml
```

### Step 2: Update Environment Properties

Edit `deployer/environments/*.properties` files with your actual server details:

**DEV.properties:**
```properties
IS_HOST=your-dev-server.company.com
IS_PORT=5555
IS_USERNAME=Administrator
IS_PASSWORD=your-password
UM_HOST=your-dev-um.company.com
UM_PORT=9000
```

**TEST.properties:**
```properties
IS_HOST=your-test1-server.company.com
IS_PORT=5555
# ... etc
```

**PROD.properties:**
```properties
IS_HOST=your-prod1-server.company.com
IS_PORT=5555
# ... etc
```

### Step 3: Test Locally

```bash
cd cicd

# Test DEV environment
ant -f build.xml deploy \
  -Dproject.properties=../build.properties \
  -Dbda.targetEnv=DEV \
  -Dbda.buildNumber=999 \
  -Dbda.projectName=hsi_onprem_cicd \
  -DfbrRepoName=hsi_onprem_cicd_999 \
  -DfbrRepoDir=../tmp/fbr/hsi_onprem_cicd_999

# Check generated file
cat ../tmp/ProjectAutomator_DEV.xml
```

### Step 4: Verify GitHub Actions

The GitHub Actions workflows already use the updated framework. No changes needed.

### Step 5: Remove Old Files (Optional)

Once you've verified everything works:

```bash
# Backup old files
mkdir -p archive/groovy-based
mv cicd/scripts/GenerateProjectAutomator.groovy archive/groovy-based/
mv ENV.groovy archive/groovy-based/

# Or delete them
rm cicd/scripts/GenerateProjectAutomator.groovy
rm ENV.groovy
```

## Adding New Packages

### Before (Groovy):
Edit `ENV.groovy` to add package to list:
```groovy
packages: ['MyBusinessPackage', 'MyNewPackage']
```

### After (Templates):
Edit `deployer/ProjectAutomator.template.xml`:
```xml
<DeploymentSet name="MainDeploymentSet">
    <!-- Existing package -->
    <DeploymentCandidate>
        <MapSetName>MyBusinessPackage</MapSetName>
        ...
    </DeploymentCandidate>
    
    <!-- New package -->
    <DeploymentCandidate>
        <MapSetName>MyNewPackage</MapSetName>
        <MapName>MyNewPackage</MapName>
        <TargetServerAlias>@TARGET_ENV@_IS</TargetServerAlias>
        <DeploymentSetName>MainDeploymentSet</DeploymentSetName>
    </DeploymentCandidate>
</DeploymentSet>
```

## Adding New Environments

### Before (Groovy):
Add new environment block to `ENV.groovy`:
```groovy
UAT {
    integrationServers { ... }
}
```

### After (Templates):
Create new properties file:

**File:** `deployer/environments/UAT.properties`
```properties
TARGET_ENV=UAT
PROJECT_PREFIX=UAT
IS_HOST=uat-server.company.com
IS_PORT=5555
IS_USERNAME=Administrator
IS_PASSWORD=manage
UM_HOST=uat-um.company.com
UM_PORT=9000
DO_VARSUB=true
VARSUB_DIR=varsub/UAT
```

Also create variable substitution directory:
```bash
mkdir -p varsub/UAT
cp varsub/DEV/MyBusinessPackage.vs.properties varsub/UAT/
```

## Troubleshooting

### Issue: Token not replaced

**Symptom:** Generated XML contains `@TOKEN_NAME@` instead of actual value

**Solution:**
1. Check token name matches exactly (case-sensitive)
2. Verify property is defined in environment properties file
3. Ensure property is loaded before copy task

**Example:**
```xml
<!-- In template -->
<host>@IS_HOST@</host>

<!-- In properties -->
IS_HOST=dev-server.company.com

<!-- In build.xml -->
<filter token="IS_HOST" value="${IS_HOST}"/>
```

### Issue: Property not found

**Symptom:** `${PROPERTY_NAME}` appears in generated XML

**Solution:**
1. Check property file path is correct
2. Verify property file is loaded: `<property file="..."/>`
3. Check property name spelling

### Issue: Wrong environment deployed

**Symptom:** DEV values used when deploying to TEST

**Solution:**
1. Verify `bda.targetEnv` is set correctly
2. Check property file loading: `deployer/environments/${bda.targetEnv}.properties`
3. Ensure environment properties override defaults

## Benefits of New Approach

### ✅ Advantages

1. **No Groovy Dependency**
   - One less software to install
   - Simpler environment setup
   - Fewer version compatibility issues

2. **Easier to Understand**
   - Plain XML templates
   - Simple property files
   - Standard ANT tasks

3. **Easier to Maintain**
   - Edit XML directly
   - No scripting knowledge needed
   - Clear separation of template and values

4. **Better Portability**
   - Works on any system with ANT
   - No environment-specific setup
   - Standard Java/ANT tools only

5. **Faster Execution**
   - No script interpretation
   - Direct file copy with replacement
   - Minimal overhead

### ⚠️ Trade-offs

1. **Less Dynamic**
   - Can't generate packages dynamically
   - Need to edit template for new packages
   - Less programmatic control

2. **More Verbose**
   - Need separate properties file per environment
   - Template can be lengthy
   - More files to maintain

3. **Limited Logic**
   - No conditional generation
   - No loops or iterations
   - Simple token replacement only

## When to Use Each Approach

### Use Templates (Current) When:
- ✅ Fixed set of packages
- ✅ Standard deployment patterns
- ✅ Want minimal dependencies
- ✅ Team familiar with ANT/XML

### Use Groovy (Previous) When:
- ❌ Need dynamic package selection
- ❌ Complex deployment logic required
- ❌ Groovy already available
- ❌ Team comfortable with scripting

## Comparison

| Feature | Groovy | Templates |
|---------|--------|-----------|
| **Dependencies** | Java + ANT + Groovy | Java + ANT |
| **Complexity** | Medium | Low |
| **Flexibility** | High | Medium |
| **Maintainability** | Medium | High |
| **Learning Curve** | Medium | Low |
| **Execution Speed** | Slower | Faster |
| **Debugging** | Harder | Easier |

## Rollback Plan

If you need to revert to Groovy-based approach:

1. Restore files from archive:
```bash
cp archive/groovy-based/GenerateProjectAutomator.groovy cicd/scripts/
cp archive/groovy-based/ENV.groovy .
```

2. Revert `cicd/build-deployer.xml` using git:
```bash
git checkout HEAD~1 -- cicd/build-deployer.xml
```

3. Ensure Groovy is installed:
```bash
groovy --version
```

## Conclusion

The migration from Groovy to static XML templates simplifies the framework by:
- Eliminating Groovy dependency
- Using standard ANT/XML tools
- Making configuration more transparent
- Reducing maintenance overhead

The new approach is sufficient for most deployment scenarios and provides a better balance of simplicity and functionality.

---

**Migration Date:** 2026-04-09  
**Framework Version:** 2.0 (Template-based)  
**Previous Version:** 1.0 (Groovy-based)  
**Status:** ✅ Complete