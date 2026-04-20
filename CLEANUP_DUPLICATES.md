# Duplicate and Redundant Code Cleanup

## 🎯 Identified Issues

### 1. Backup Files (120+ files)
**Location**: Throughout packages directory
**Pattern**: `*.bak`, `*.backup`, `*.orig`, `*-Copy*`, `*_old*`

**Examples**:
- `packages/**/flow.xml.bak` (61 files in application/)
- `packages/**/flow.xml.bak` (42 files in packages/)
- `packages/**/manifest.bak` (multiple files)
- `packages/**/*-Copy.html` (notification templates)

### 2. Duplicate Deployer Files
**Location**: `deployer/` and `framework/deployer/`

**Duplicates**:
- `Deployment_ProjectAutomator.xml` - Generated file, should not be in repo
- `projectAutomatorReport.xml` - Generated report, should not be in repo
- `config.cnf` - Runtime configuration, should not be in repo

### 3. Redundant Documentation
**Location**: Root directory

**Potential Consolidation**:
- `REPOSITORY_SEPARATION_PLAN.md` (213 lines)
- `REPOSITORY_SEPARATION_GUIDE.md` (497 lines)
- `IMPLEMENTATION_SUMMARY.md` (363 lines)

These three documents have overlapping content and can be consolidated.

### 4. Duplicate Workflows
**Location**: `.github/workflows/` and `framework/.github/workflows/`

**Files**:
- `webmethods-cicd.yml` - Same in both locations
- `rollback.yml` - Same in both locations
- `sonarqube.yml` - Only in framework

### 5. Duplicate Packages
**Location**: `packages/` and `application/packages/`

**Status**: Complete duplication of 383 files

## 🧹 Cleanup Actions

### Action 1: Remove All Backup Files

```powershell
# Remove .bak files
Get-ChildItem -Path . -Recurse -Filter "*.bak" | Remove-Item -Force

# Remove .backup files
Get-ChildItem -Path . -Recurse -Filter "*.backup" | Remove-Item -Force

# Remove .orig files
Get-ChildItem -Path . -Recurse -Filter "*.orig" | Remove-Item -Force

# Remove *-Copy* files
Get-ChildItem -Path . -Recurse -Filter "*-Copy*" | Remove-Item -Force

# Remove *_old* files
Get-ChildItem -Path . -Recurse -Filter "*_old*" | Remove-Item -Force
```

**Impact**: Removes 120+ unnecessary backup files
**Risk**: Low (these are backup files, not source files)

### Action 2: Remove Generated Deployer Files

```powershell
# Remove generated files from deployer/
Remove-Item deployer/Deployment_ProjectAutomator.xml -Force -ErrorAction SilentlyContinue
Remove-Item deployer/projectAutomatorReport.xml -Force -ErrorAction SilentlyContinue
Remove-Item deployer/config.cnf -Force -ErrorAction SilentlyContinue
Remove-Item deployer/logs/* -Recurse -Force -ErrorAction SilentlyContinue

# Remove from framework/deployer/
Remove-Item framework/deployer/Deployment_ProjectAutomator.xml -Force -ErrorAction SilentlyContinue
Remove-Item framework/deployer/projectAutomatorReport.xml -Force -ErrorAction SilentlyContinue
Remove-Item framework/deployer/config.cnf -Force -ErrorAction SilentlyContinue
Remove-Item framework/deployer/logs/* -Recurse -Force -ErrorAction SilentlyContinue
```

**Impact**: Removes 6-8 generated files
**Risk**: None (these are regenerated during deployment)

### Action 3: Consolidate Documentation

**Keep**:
- `REPOSITORY_SEPARATION_GUIDE.md` - Most comprehensive, step-by-step guide

**Archive or Remove**:
- `REPOSITORY_SEPARATION_PLAN.md` - Merge key points into GUIDE
- `IMPLEMENTATION_SUMMARY.md` - Merge statistics into GUIDE

**Action**:
```powershell
# Move to archive
mkdir archive/docs -Force
Move-Item REPOSITORY_SEPARATION_PLAN.md archive/docs/
Move-Item IMPLEMENTATION_SUMMARY.md archive/docs/
```

**Impact**: Reduces documentation redundancy
**Risk**: Low (content preserved in archive)

### Action 4: Clean Up Duplicate Workflows

Since we're separating repositories, the duplication is intentional:
- Root `.github/workflows/` - For current monolithic repo
- `framework/.github/workflows/` - For framework repo
- `application/.github/workflows/` - For application repo

**Action**: No cleanup needed - this is by design for repository separation

### Action 5: Handle Duplicate Packages

The duplication between `packages/` and `application/packages/` is intentional for repository separation.

**Action**: No cleanup needed until repositories are actually separated

## 📋 Cleanup Script

### Complete Cleanup Script

```powershell
# cleanup-duplicates.ps1
Write-Host "Starting cleanup of duplicate and redundant files..." -ForegroundColor Green

# 1. Remove backup files
Write-Host "`n1. Removing backup files..." -ForegroundColor Yellow
$backupPatterns = @("*.bak", "*.backup", "*.orig", "*-Copy*", "*_old*")
$removedCount = 0

foreach ($pattern in $backupPatterns) {
    $files = Get-ChildItem -Path . -Recurse -Filter $pattern -File
    foreach ($file in $files) {
        Write-Host "  Removing: $($file.FullName)" -ForegroundColor Gray
        Remove-Item $file.FullName -Force
        $removedCount++
    }
}
Write-Host "  Removed $removedCount backup files" -ForegroundColor Green

# 2. Remove generated deployer files
Write-Host "`n2. Removing generated deployer files..." -ForegroundColor Yellow
$deployerFiles = @(
    "deployer/Deployment_ProjectAutomator.xml",
    "deployer/projectAutomatorReport.xml",
    "deployer/config.cnf",
    "framework/deployer/Deployment_ProjectAutomator.xml",
    "framework/deployer/projectAutomatorReport.xml",
    "framework/deployer/config.cnf"
)

foreach ($file in $deployerFiles) {
    if (Test-Path $file) {
        Write-Host "  Removing: $file" -ForegroundColor Gray
        Remove-Item $file -Force
    }
}

# Remove deployer logs
if (Test-Path "deployer/logs") {
    Write-Host "  Removing: deployer/logs/*" -ForegroundColor Gray
    Remove-Item "deployer/logs/*" -Recurse -Force -ErrorAction SilentlyContinue
}
if (Test-Path "framework/deployer/logs") {
    Write-Host "  Removing: framework/deployer/logs/*" -ForegroundColor Gray
    Remove-Item "framework/deployer/logs/*" -Recurse -Force -ErrorAction SilentlyContinue
}

Write-Host "  Removed generated deployer files" -ForegroundColor Green

# 3. Archive redundant documentation
Write-Host "`n3. Archiving redundant documentation..." -ForegroundColor Yellow
$archiveDir = "archive/docs"
if (-not (Test-Path $archiveDir)) {
    New-Item -ItemType Directory -Path $archiveDir -Force | Out-Null
}

$docsToArchive = @(
    "REPOSITORY_SEPARATION_PLAN.md",
    "IMPLEMENTATION_SUMMARY.md"
)

foreach ($doc in $docsToArchive) {
    if (Test-Path $doc) {
        Write-Host "  Archiving: $doc" -ForegroundColor Gray
        Move-Item $doc $archiveDir -Force
    }
}
Write-Host "  Archived redundant documentation" -ForegroundColor Green

# 4. Summary
Write-Host "`n=== Cleanup Summary ===" -ForegroundColor Cyan
Write-Host "✓ Removed $removedCount backup files" -ForegroundColor Green
Write-Host "✓ Removed generated deployer files" -ForegroundColor Green
Write-Host "✓ Archived redundant documentation" -ForegroundColor Green
Write-Host "`nCleanup complete!" -ForegroundColor Green
```

## 🎯 Recommended Actions

### Immediate Actions (Safe)
1. ✅ **Remove backup files** - Run cleanup script
2. ✅ **Remove generated deployer files** - Run cleanup script
3. ✅ **Update .gitignore** - Prevent future backup files

### Post-Separation Actions
4. **Remove duplicate packages** - After application repo is created
5. **Remove old workflows** - After framework repo is created
6. **Archive old repository** - After successful migration

## 📝 Updated .gitignore

Add these patterns to prevent future duplicates:

```gitignore
# Backup Files
*.bak
*.backup
*.orig
*-Copy*
*_old*
*.swp
*~

# Generated Deployer Files
deployer/Deployment_*.xml
deployer/projectAutomatorReport.xml
deployer/config.cnf
deployer/logs/

# Build Output
build/
output/
*.zip
*.acdl

# IDE
.vscode/
.idea/
*.iml
```

## 📊 Cleanup Impact

### Before Cleanup
- **Total Files**: ~850 files
- **Backup Files**: ~120 files
- **Generated Files**: ~8 files
- **Redundant Docs**: 3 files

### After Cleanup
- **Total Files**: ~720 files
- **Backup Files**: 0 files
- **Generated Files**: 0 files
- **Redundant Docs**: 1 file (consolidated)

**Space Saved**: ~15-20% reduction in repository size
**Clarity Improved**: Easier to navigate and maintain

## ✅ Verification

After cleanup, verify:

```powershell
# Check for remaining backup files
Get-ChildItem -Path . -Recurse -Include *.bak,*.backup,*.orig | Measure-Object

# Check for generated files
Test-Path deployer/Deployment_ProjectAutomator.xml
Test-Path deployer/projectAutomatorReport.xml

# Verify documentation
Get-ChildItem -Path . -Filter "*SEPARATION*.md"
```

Expected results:
- 0 backup files
- Generated files: False
- 1 separation guide document

## 🚀 Execute Cleanup

To execute the cleanup:

```powershell
# Save the script
# Copy the cleanup script above to cleanup-duplicates.ps1

# Run the script
.\cleanup-duplicates.ps1

# Verify results
git status

# Commit cleanup
git add .
git commit -m "Clean up duplicate and redundant files

- Removed 120+ backup files (*.bak, *.backup, *.orig)
- Removed generated deployer files
- Archived redundant documentation
- Updated .gitignore to prevent future duplicates"
```

---

**Status**: Ready for execution
**Risk Level**: Low
**Estimated Time**: 5 minutes
**Reversible**: Yes (via git)