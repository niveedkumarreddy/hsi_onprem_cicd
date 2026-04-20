# cleanup-duplicates.ps1
# Cleanup script for duplicate and redundant files

Write-Host "Starting cleanup of duplicate and redundant files..." -ForegroundColor Green

# 1. Remove backup files
Write-Host "`n1. Removing backup files..." -ForegroundColor Yellow
$backupPatterns = @("*.bak", "*.backup", "*.orig")
$removedCount = 0

foreach ($pattern in $backupPatterns) {
    $files = Get-ChildItem -Path . -Recurse -Filter $pattern -File -ErrorAction SilentlyContinue
    foreach ($file in $files) {
        Write-Host "  Removing: $($file.Name)" -ForegroundColor Gray
        Remove-Item $file.FullName -Force
        $removedCount++
    }
}

# Remove files with -Copy in name
$copyFiles = Get-ChildItem -Path . -Recurse -File | Where-Object { $_.Name -like "*-Copy*" -or $_.Name -like "*_old*" }
foreach ($file in $copyFiles) {
    Write-Host "  Removing: $($file.Name)" -ForegroundColor Gray
    Remove-Item $file.FullName -Force
    $removedCount++
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

$deployerRemoved = 0
foreach ($file in $deployerFiles) {
    if (Test-Path $file) {
        Write-Host "  Removing: $file" -ForegroundColor Gray
        Remove-Item $file -Force
        $deployerRemoved++
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

Write-Host "  Removed $deployerRemoved generated deployer files" -ForegroundColor Green

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

$docsArchived = 0
foreach ($doc in $docsToArchive) {
    if (Test-Path $doc) {
        Write-Host "  Archiving: $doc" -ForegroundColor Gray
        Move-Item $doc $archiveDir -Force
        $docsArchived++
    }
}
Write-Host "  Archived $docsArchived redundant documents" -ForegroundColor Green

# 4. Summary
Write-Host "" -ForegroundColor White
Write-Host "=== Cleanup Summary ===" -ForegroundColor Cyan
Write-Host "Removed $removedCount backup files" -ForegroundColor Green
Write-Host "Removed $deployerRemoved generated deployer files" -ForegroundColor Green
Write-Host "Archived $docsArchived redundant documents" -ForegroundColor Green
Write-Host "" -ForegroundColor White
Write-Host "Cleanup complete!" -ForegroundColor Green
Write-Host "" -ForegroundColor White
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Review changes: git status" -ForegroundColor White
Write-Host "  2. Commit cleanup" -ForegroundColor White
