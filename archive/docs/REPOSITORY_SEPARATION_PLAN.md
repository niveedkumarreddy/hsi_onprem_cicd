# Repository Separation Implementation Plan

## Overview
This document outlines the implementation of separating the monolithic repository into:
1. **Framework Repository**: Reusable CI/CD framework
2. **Application Repository**: Project-specific code

## Current Repository Structure
```
hsi_onprem_cicd/
├── .github/workflows/          # CI/CD workflows → FRAMEWORK
├── cicd/                       # Build scripts → FRAMEWORK
├── deployer/                   # Deployment configs → FRAMEWORK
├── packages/                   # Application packages → APPLICATION
├── tests/                      # Application tests → APPLICATION
├── varsub/                     # Variable substitution → APPLICATION
├── build.properties            # SAG reference → FRAMEWORK
├── custom.properties           # Project config → APPLICATION
└── archive/                    # Keep in APPLICATION
```

## Target Structure

### Framework Repository: `webmethods-cicd-framework`
```
webmethods-cicd-framework/
├── .github/
│   └── workflows/
│       ├── reusable-cicd.yml           # NEW: Reusable workflow
│       ├── webmethods-cicd.yml         # Reference implementation
│       └── rollback.yml                # Reference rollback
├── cicd/
│   ├── build.xml
│   ├── build-abe.xml
│   ├── build-deployer.xml
│   └── build-test.xml
├── deployer/
│   ├── ProjectAutomator.template.xml
│   └── environments/
│       ├── DEV.properties
│       ├── TEST.properties
│       ├── QA.properties
│       └── PROD.properties
├── build.properties                    # SAG reference
├── custom.properties.template          # NEW: Template for projects
├── .gitignore
├── README.md                           # NEW: Framework docs
├── FRAMEWORK_USAGE.md                  # NEW: Usage guide
└── CHANGELOG.md                        # NEW: Version history
```

### Application Repository: `hsi-onprem-application`
```
hsi-onprem-application/
├── .cicd/                              # Git submodule → framework
├── packages/                           # Application code
│   ├── AIACommonLogClient/
│   ├── AIACommonLogger/
│   ├── AIACommonNotification/
│   ├── CustomHIPAA/
│   └── OH_Ops/
├── tests/                              # Application tests
├── varsub/                             # Variable substitution
│   ├── DEV/
│   ├── TEST/
│   └── PROD/
├── .github/
│   └── workflows/
│       └── deploy.yml                  # NEW: Uses framework workflow
├── custom.properties                   # Project-specific config
├── .gitignore
├── .gitmodules                         # NEW: Submodule config
└── README.md                           # Application docs
```

## Implementation Steps

### Phase 1: Prepare Framework Repository Structure (In Current Repo)
1. Create `framework/` directory with framework files
2. Create reusable workflow
3. Create custom.properties template
4. Create framework documentation

### Phase 2: Prepare Application Repository Structure (In Current Repo)
1. Create `application/` directory with application files
2. Create application workflow that references framework
3. Update custom.properties for application use

### Phase 3: Create Actual Repositories (Manual Git Operations)
1. Initialize framework repository
2. Initialize application repository
3. Add framework as submodule to application
4. Push both repositories

## Files to Create/Modify

### New Files for Framework:
- `.github/workflows/reusable-cicd.yml` - Reusable workflow
- `custom.properties.template` - Template for projects
- `README.md` - Framework documentation
- `FRAMEWORK_USAGE.md` - Usage guide
- `CHANGELOG.md` - Version history
- `.gitignore` - Framework-specific ignores

### New Files for Application:
- `.github/workflows/deploy.yml` - Application workflow
- `.gitmodules` - Submodule configuration
- `README.md` - Application documentation
- `.gitignore` - Application-specific ignores

### Modified Files:
- `custom.properties` - Split into template and application-specific

## Migration Commands

### Step 1: Create Framework Repository
```bash
# Create new directory for framework
mkdir ../webmethods-cicd-framework
cd ../webmethods-cicd-framework
git init

# Copy framework files from prepared structure
cp -r ../hsi_onprem_cicd/framework/* .

# Commit
git add .
git commit -m "Initial framework setup"
git remote add origin <framework-repo-url>
git push -u origin main
git tag -a v1.0.0 -m "Initial release v1.0.0"
git push origin v1.0.0
```

### Step 2: Create Application Repository
```bash
# Create new directory for application
mkdir ../hsi-onprem-application
cd ../hsi-onprem-application
git init

# Copy application files from prepared structure
cp -r ../hsi_onprem_cicd/application/* .

# Add framework as submodule
git submodule add <framework-repo-url> .cicd
git submodule update --init --recursive

# Commit
git add .
git commit -m "Initial application setup with CI/CD framework"
git remote add origin <application-repo-url>
git push -u origin main
```

### Step 3: Archive Old Repository
```bash
cd ../hsi_onprem_cicd
git tag -a archive/pre-separation -m "Archive before repository separation"
git push origin archive/pre-separation
# Update README to indicate repository has been split
```

## Testing Plan

1. **Framework Testing**:
   - Verify all build scripts work independently
   - Test reusable workflow with sample project
   - Validate property loading

2. **Application Testing**:
   - Clone application with submodule
   - Run build process
   - Deploy to DEV environment
   - Verify all packages build correctly

3. **Integration Testing**:
   - Update framework version in application
   - Test workflow triggers
   - Verify deployment to all environments

## Rollback Plan

If issues occur:
1. Keep old repository active during transition
2. Tag both new repositories as "beta"
3. Test thoroughly before making official
4. Can revert to monolithic structure if needed

## Timeline

- **Day 1**: Prepare structures in current repo
- **Day 2**: Create and test framework repository
- **Day 3**: Create and test application repository
- **Day 4**: Integration testing
- **Day 5**: Documentation and team training

## Success Criteria

- [ ] Framework repository builds successfully
- [ ] Application repository builds successfully
- [ ] Submodule integration works
- [ ] Workflows execute correctly
- [ ] Deployment to all environments works
- [ ] Documentation is complete
- [ ] Team is trained on new structure

## Notes

- Keep old repository for reference during transition
- Use semantic versioning for framework (v1.0.0, v1.1.0, etc.)
- Document all breaking changes in CHANGELOG.md
- Communicate changes to all stakeholders