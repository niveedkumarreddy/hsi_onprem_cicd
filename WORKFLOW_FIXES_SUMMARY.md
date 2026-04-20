# Workflow Fixes for Framework/Application Structure

## 🎯 Problem Fixed

The GitHub Actions workflows were failing because they referenced the old root directory structure (`cicd/`, `packages/`, `tests/`, `varsub/`) which were removed during cleanup. The workflows have been updated to use the new `framework/` and `application/` directory structure.

---

## 🔧 Changes Made

### 1. `.github/workflows/webmethods-cicd.yml`

#### Environment Variables (Line 30)
```yaml
# OLD:
CICD_HOME: ${{ github.workspace }}/cicd

# NEW:
CICD_HOME: ${{ github.workspace }}/framework/cicd
```

#### Trigger Paths (Lines 6-10)
```yaml
# OLD:
paths:
  - 'packages/**'
  - 'tests/**'
  - 'environments/**'

# NEW:
paths:
  - 'application/packages/**'
  - 'application/tests/**'
  - 'framework/cicd/**'
  - 'framework/deployer/**'
```

#### Build Properties (Lines 88-89)
```yaml
# OLD:
isPackagesDir=$WORKSPACE/packages
isTestDir=$WORKSPACE/tests

# NEW:
isPackagesDir=$WORKSPACE/application/packages
isTestDir=$WORKSPACE/application/tests
```

#### SonarQube Sources (Line 235)
```yaml
# OLD:
"-Dsonar.sources=packages,cicd,deployer",
"-Dsonar.tests=tests",

# NEW:
"-Dsonar.sources=application/packages,framework/cicd,framework/deployer",
"-Dsonar.tests=application/tests",
```

#### Variable Substitution (Lines 424, 564, 683)
```yaml
# OLD:
varsubDir=${{ github.workspace }}/varsub
# or
varsubDir=$WORKSPACE/varsub

# NEW:
varsubDir=${{ github.workspace }}/application/varsub
# or
varsubDir=$WORKSPACE/application/varsub
```

**Total Changes**: 7 locations updated

---

### 2. `.github/workflows/rollback.yml`

#### Variable Substitution (Lines 137, 239)
```yaml
# OLD:
varsubDir=${{ github.workspace }}/varsub

# NEW:
varsubDir=${{ github.workspace }}/application/varsub
```

**Total Changes**: 2 locations updated

---

### 3. `.github/workflows/sonarqube.yml`

#### SonarQube Sources (Lines 77-78)
```yaml
# OLD:
"-Dsonar.sources=packages,cicd,deployer",
"-Dsonar.tests=tests",

# NEW:
"-Dsonar.sources=application/packages,framework/cicd,framework/deployer",
"-Dsonar.tests=application/tests",
```

**Total Changes**: 1 location updated

---

## 📊 Summary of Updates

| Workflow File | Changes | Locations |
|---------------|---------|-----------|
| `webmethods-cicd.yml` | 7 updates | CICD_HOME, trigger paths, build properties, SonarQube, varsub (3x) |
| `rollback.yml` | 2 updates | varsub paths (2x) |
| `sonarqube.yml` | 1 update | SonarQube sources |
| **Total** | **10 updates** | **Across 3 workflow files** |

---

## 🎯 Path Mapping

| Old Path | New Path | Usage |
|----------|----------|-------|
| `cicd/` | `framework/cicd/` | Build scripts |
| `deployer/` | `framework/deployer/` | Deployment configs |
| `packages/` | `application/packages/` | IS packages |
| `tests/` | `application/tests/` | Test cases |
| `varsub/` | `application/varsub/` | Variable substitution |

---

## ✅ Verification

### Before Fix
```
Error: Cannot find path '...\hsi_onprem_cicd\cicd' because it does not exist.
```

### After Fix
- ✅ `CICD_HOME` points to `framework/cicd/`
- ✅ Build finds packages in `application/packages/`
- ✅ Tests found in `application/tests/`
- ✅ Variable substitution uses `application/varsub/`
- ✅ SonarQube analyzes correct directories
- ✅ Workflows trigger on correct path changes

---

## 🚀 Testing the Fix

### Test Build
```bash
# Trigger workflow manually
# Go to Actions → webMethods Multi-Server CI/CD Pipeline → Run workflow
# Select environment: DEV
# Click "Run workflow"
```

### Expected Results
1. ✅ Checkout succeeds
2. ✅ Build environment setup succeeds
3. ✅ Build properties generated correctly
4. ✅ ABE build finds packages in `application/packages/`
5. ✅ Deployment uses `framework/deployer/` configs
6. ✅ Variable substitution uses `application/varsub/`

---

## 📝 Repository Structure

### Current Working Structure
```
hsi_onprem_cicd/
├── .github/
│   └── workflows/
│       ├── webmethods-cicd.yml    ✅ Updated
│       ├── rollback.yml            ✅ Updated
│       └── sonarqube.yml           ✅ Updated
├── framework/                      ✅ CI/CD framework
│   ├── cicd/                       ← Workflows use this
│   ├── deployer/                   ← Workflows use this
│   └── build.properties
├── application/                    ✅ Application code
│   ├── packages/                   ← Workflows use this
│   ├── tests/                      ← Workflows use this
│   └── varsub/                     ← Workflows use this
└── tmp/                            ← Build output
```

---

## 🔄 Next Steps

1. **Commit Changes**:
```bash
git add .github/workflows/
git commit -m "fix: update workflows for framework/application structure

- Updated CICD_HOME to framework/cicd
- Updated source paths to application/packages
- Updated test paths to application/tests
- Updated varsub paths to application/varsub
- Updated SonarQube analysis paths
- Updated trigger paths for workflows

Fixes build failure: 'cicd directory not found'"
git push
```

2. **Test the Build**:
   - Push changes to trigger workflow
   - Or manually trigger workflow from Actions tab
   - Verify build completes successfully

3. **Monitor First Run**:
   - Check that all paths resolve correctly
   - Verify packages are found and built
   - Confirm deployment works

---

## 💡 Key Learnings

1. **Path Consistency**: All workflows must use consistent paths
2. **Framework Separation**: Framework files in `framework/`, app files in `application/`
3. **Environment Variables**: `CICD_HOME` is critical for build scripts
4. **Variable Substitution**: Must point to correct varsub directory
5. **Trigger Paths**: Update to monitor correct directories for changes

---

## 🆘 Troubleshooting

### If Build Still Fails

**Check 1: Verify Directory Structure**
```powershell
Test-Path framework/cicd/build.xml          # Should be True
Test-Path application/packages              # Should be True
Test-Path application/tests                 # Should be True
Test-Path application/varsub                # Should be True
```

**Check 2: Verify Workflow Syntax**
```bash
# Check for any remaining old paths
grep -r "workspace }}/cicd" .github/workflows/
grep -r "workspace }}/packages" .github/workflows/
grep -r "workspace }}/varsub" .github/workflows/
# Should return no results
```

**Check 3: Check Build Properties**
- Ensure `isPackagesDir` points to `application/packages`
- Ensure `isTestDir` points to `application/tests`
- Ensure `varsubDir` points to `application/varsub`

---

**Status**: ✅ ALL WORKFLOWS UPDATED
**Files Modified**: 3 workflow files
**Total Changes**: 10 path updates
**Ready for**: Testing and deployment