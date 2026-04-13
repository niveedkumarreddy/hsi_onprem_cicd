# webMethods CI/CD Custom Framework Documentation

## Overview

This is a **self-contained, custom-built CI/CD framework** for webMethods Integration Server deployments. It does not depend on any external repositories or libraries. All automation is built from scratch specifically for this project.

## Architecture

### Framework Components

```
hsi_onprem_cicd/
├── cicd/                           # Custom CI/CD Framework (Core)
│   ├── build.xml                   # Main orchestrator
│   ├── build-abe.xml              # Asset Build Environment integration
│   ├── build-deployer.xml         # Deployment automation
│   ├── build-test.xml             # Test execution framework
│   └── scripts/
│       └── GenerateProjectAutomator.groovy  # Dynamic PA generator
├── .github/workflows/              # GitHub Actions pipelines
│   ├── webmethods-cicd.yml        # Main CI/CD pipeline
│   └── rollback.yml               # Rollback automation
├── packages/                       # webMethods packages (source)
├── tests/                          # WmTestSuite test packages
├── varsub/                         # Variable substitution files
│   ├── DEV/
│   ├── TEST/
│   └── PROD/
├── ENV.groovy                      # Environment definitions
└── build.properties                # Build configuration

```

## Core Framework Files

### 1. cicd/build.xml (Main Orchestrator)

**Purpose**: Central build file that imports and coordinates all sub-builds.

**Key Targets**:
- `build` - Builds webMethods packages using ABE
- `deploy` - Deploys packages to target environment
- `test` - Executes WmTestSuite tests
- `buildDeployTest` - Complete CI/CD workflow
- `clean` - Cleans temporary files
- `init` - Initializes build environment

**Usage**:
```bash
cd cicd
ant -f build.xml build -Dproject.properties=../build.properties
ant -f build.xml deploy -Dproject.properties=../deploy.properties
ant -f build.xml test -Dproject.properties=../test.properties
```

### 2. cicd/build-abe.xml (Asset Build Environment)

**Purpose**: Integrates with Software AG Asset Build Environment to build packages into File-Based Repositories (FBR).

**Key Targets**:
- `abe.init` - Validates ABE installation
- `abe.prepare` - Prepares build directories
- `abe.execute` - Executes ABE build
- `abe.verify` - Verifies build output
- `abe.clean` - Cleans ABE artifacts

**Properties Required**:
```properties
config.build.abeHome=${SAG_HOME}/common/AssetBuildEnvironment
isPackagesDir=${workspace}/packages
fbrRepoDir=${workspace}/tmp/fbr/${projectName}_${buildNumber}
```

**Build Process**:
1. Validates ABE installation at `${SAG_HOME}/common/AssetBuildEnvironment`
2. Creates FBR directory structure
3. Invokes ABE to build packages
4. Generates `build.xml` in FBR directory
5. Verifies artifacts were created

### 3. cicd/build-deployer.xml (Deployment Automation)

**Purpose**: Automates deployment using webMethods Deployer and Project Automator.

**Key Targets**:
- `deployer.init` - Validates Deployer installation
- `deployer.generatePA` - Generates Project Automator XML
- `deployer.executePA` - Executes Project Automator
- `deployer.verify` - Verifies deployment success

**Properties Required**:
```properties
config.deployer.deployerInstallationPath=${SAG_HOME}/IntegrationServer/instances/default/packages/WmDeployer/bin
config.deployer.deployerHost=deployer-server.company.com
config.deployer.deployerPort=5555
config.deployer.deployerUsername=Administrator
config.deployer.deployerPassword=manage
config.deployer.projectNamePrefix=DEV_123
config.deployer.doVarSub=true
varsubDir=${workspace}/varsub
environmentsDefinition=${workspace}/ENV.groovy
fbrRepoDir=${workspace}/tmp/fbr/${projectName}_${buildNumber}
```

**Deployment Process**:
1. Validates Deployer installation
2. Generates Project Automator XML using Groovy script
3. Executes Project Automator CLI command
4. Applies variable substitution per environment
5. Verifies deployment status

### 4. cicd/build-test.xml (Test Execution)

**Purpose**: Executes WmTestSuite tests and generates reports.

**Key Targets**:
- `test.init` - Validates test environment
- `test.discover` - Discovers test packages
- `test.run` - Executes tests
- `test.report` - Generates JUnit XML reports

**Properties Required**:
```properties
bda.targetEnv=DEV
isTestDir=${workspace}/tests
config.test.reportDir=${workspace}/report
config.test.failBuildOnTestError=true
environmentsDefinition=${workspace}/ENV.groovy
```

**Test Process**:
1. Discovers test packages in `tests/` directory
2. Connects to target Integration Server
3. Executes WmTestSuite tests
4. Generates JUnit XML reports
5. Fails build if tests fail (configurable)

### 5. cicd/scripts/GenerateProjectAutomator.groovy

**Purpose**: Dynamically generates Project Automator XML based on environment definitions.

**Input**: 
- `ENV.groovy` - Environment definitions
- Build properties (project name, build number, etc.)

**Output**:
- `Deployment_ProjectAutomator.xml` - Project Automator configuration

**Key Features**:
- Reads environment definitions from ENV.groovy
- Maps packages to target servers
- Configures deployment candidates
- Sets up variable substitution
- Handles multi-server deployments

**Generated XML Structure**:
```xml
<DeploymentProject>
  <ProjectName>DEV_123_hsi_onprem_cicd</ProjectName>
  <DeploymentSet>
    <DeploymentCandidate>
      <SourceRepositoryName>hsi_onprem_cicd_123</SourceRepositoryName>
      <MapSetName>MyBusinessPackage</MapSetName>
      <MapName>MyBusinessPackage</MapName>
      <TargetServerAlias>DEV_IS</TargetServerAlias>
    </DeploymentCandidate>
  </DeploymentSet>
  <DeploymentMap>
    <alias name="DEV_IS">
      <type>IS</type>
      <host>dev-server.company.com</host>
      <port>5555</port>
    </alias>
  </DeploymentMap>
</DeploymentProject>
```

## Environment Configuration

### ENV.groovy Structure

Defines all environments and their server configurations:

```groovy
environments {
    DEV {
        description = "Development Environment"
        integrationServers {
            DEV_IS {
                host = "dev-server.company.com"
                port = 5555
                username = "Administrator"
                password = "manage"
            }
        }
        universalMessaging {
            DEV_UM {
                host = "dev-um.company.com"
                port = 9000
            }
        }
        jdbcAdapters {
            DEV_JDBC {
                connectionAlias = "DevDB"
                driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
                url = "jdbc:sqlserver://dev-db.company.com:1433;databaseName=DevDB"
            }
        }
        as400Adapters {
            DEV_AS400 {
                connectionAlias = "DevAS400"
                host = "dev-as400.company.com"
                port = 8471
            }
        }
    }
    // TEST and PROD environments follow same structure
}
```

### Variable Substitution Files

Located in `varsub/{ENV}/` directories, these files contain environment-specific values:

**varsub/DEV/MyBusinessPackage.vs.properties**:
```properties
# Database Configuration
db.url=jdbc:sqlserver://dev-db.company.com:1433;databaseName=DevDB
db.username=dev_user
db.password=dev_password

# API Endpoints
api.endpoint=https://dev-api.company.com/v1
api.timeout=30000

# File Paths
file.input.path=C:/SoftwareAG/IntegrationServer/instances/default/replicate/inbound/dev
file.output.path=C:/SoftwareAG/IntegrationServer/instances/default/replicate/outbound/dev
```

## GitHub Actions Workflows

### Main CI/CD Pipeline (.github/workflows/webmethods-cicd.yml)

**Trigger Events**:
- Push to `main` branch (packages/**, tests/**, environments/**)
- Pull requests to `main`
- Manual workflow dispatch

**Jobs**:

1. **build** - Builds webMethods packages
   - Runs on: `[self-hosted, webmethods-build]`
   - Uses: `cicd/build.xml` with `build` target
   - Outputs: Build artifacts in FBR format
   - Artifacts: Uploaded for 30 days

2. **deploy-dev** - Auto-deploys to DEV
   - Runs on: `[self-hosted, webmethods-build]`
   - Uses: `cicd/build.xml` with `deploy` target
   - Environment: DEV (auto-approval)
   - Variable Substitution: `varsub/DEV/`

3. **test-dev** - Tests on DEV
   - Runs on: `[self-hosted, webmethods-build]`
   - Uses: `cicd/build.xml` with `test` target
   - Publishes: JUnit test results
   - Skippable: Via workflow input

4. **deploy-test** - Deploys to TEST (3 servers)
   - Runs on: `[self-hosted, webmethods-build]`
   - Strategy: Matrix (test1, test2, test3)
   - Max Parallel: 1 (sequential deployment)
   - Environment: TEST (manual approval required)
   - Variable Substitution: `varsub/TEST/`

5. **test-test** - Tests on TEST
   - Runs on: `[self-hosted, webmethods-build]`
   - Tests all 3 TEST servers
   - Publishes: Consolidated test results

6. **deploy-prod** - Deploys to PROD (3 servers)
   - Runs on: `[self-hosted, webmethods-build]`
   - Strategy: Matrix (prod1, prod2, prod3)
   - Max Parallel: 1 (sequential deployment)
   - Fail Fast: true (stops on first failure)
   - Environment: PROD (manual approval required)
   - Pre-deployment: Backup creation
   - Post-deployment: Verification
   - Wait: 2 minutes between servers

7. **create-release** - Creates GitHub release
   - Runs on: `ubuntu-latest`
   - Creates: Git tag `v{build_number}`
   - Includes: Deployment summary

### Rollback Workflow (.github/workflows/rollback.yml)

**Trigger**: Manual workflow dispatch only

**Inputs**:
- `environment` - TEST or PROD
- `build_number` - Target build to rollback to
- `reason` - Reason for rollback

**Jobs**:

1. **validate-rollback** - Validates rollback request
   - Checks build number validity
   - Ensures not rolling back to future build
   - Creates rollback summary

2. **rollback-test** / **rollback-prod** - Executes rollback
   - Downloads previous build artifacts
   - Creates pre-rollback backup
   - Deploys previous version
   - Verifies rollback success

3. **post-rollback-tests** - Verification tests
   - Runs smoke tests
   - Publishes test results
   - Confirms system stability

4. **notify-rollback** - Creates documentation
   - Creates GitHub issue
   - Documents rollback details
   - Provides next steps checklist

## Build Properties Reference

### Required Properties

```properties
# Project Information
bda.projectName=hsi_onprem_cicd
bda.buildNumber=123
bda.targetEnv=DEV

# Source Locations
isPackagesDir=/path/to/packages
isTestDir=/path/to/tests

# Build Output
config.tmpdir=tmp
config.build.buildStorageDir=tmp/fbr
fbrRepoName=hsi_onprem_cicd_123
fbrRepoDir=tmp/fbr/hsi_onprem_cicd_123

# ABE Configuration
config.build.abeHome=${SAG_HOME}/common/AssetBuildEnvironment

# Deployer Configuration
config.deployer.deployerInstallationPath=${SAG_HOME}/IntegrationServer/instances/default/packages/WmDeployer/bin
config.deployer.deployerHost=deployer-server.company.com
config.deployer.deployerPort=5555
config.deployer.deployerUsername=Administrator
config.deployer.deployerPassword=manage
config.deployer.projectNamePrefix=DEV_123
config.deployer.doVarSub=true

# Variable Substitution
varsubDir=/path/to/varsub
config.deployer.splitDelpoymentSets=false

# Environment Definition
environmentsDefinition=/path/to/ENV.groovy

# Test Configuration
config.test.reportDir=/path/to/report
config.test.failBuildOnTestError=true
```

## Deployment Process Flow

### 1. Build Phase
```
Source Code → ABE → File-Based Repository (FBR)
```
- Reads packages from `packages/` directory
- Invokes ABE to compile and package
- Creates FBR in `tmp/fbr/{projectName}_{buildNumber}/`
- Generates `build.xml` in FBR directory

### 2. Deployment Phase
```
FBR → Project Automator → Deployer → Target Servers
```
- Generates Project Automator XML from ENV.groovy
- Executes Project Automator CLI
- Deployer reads FBR and deploys to target servers
- Applies variable substitution per environment
- Verifies deployment success

### 3. Test Phase
```
Test Packages → WmTestSuite → JUnit Reports
```
- Discovers test packages in `tests/` directory
- Connects to target Integration Server
- Executes WmTestSuite tests
- Generates JUnit XML reports
- Publishes results to GitHub Actions

## Multi-Server Deployment Strategy

### Sequential Deployment (TEST & PROD)

The framework uses GitHub Actions matrix strategy with `max-parallel: 1` to ensure sequential deployment:

```yaml
strategy:
  matrix:
    server: [test1, test2, test3]
  fail-fast: false  # TEST: Continue on failure
  max-parallel: 1   # Deploy one server at a time
```

**Deployment Order**:
1. Deploy to server 1
2. Wait for completion
3. Deploy to server 2
4. Wait for completion
5. Deploy to server 3

**Wait Between Servers**:
- TEST: No wait (immediate)
- PROD: 2 minutes wait between servers

### Fail-Fast Strategy

- **TEST**: `fail-fast: false` - Continues even if one server fails
- **PROD**: `fail-fast: true` - Stops immediately on first failure

## Variable Substitution

### How It Works

1. **Build Time**: Packages are built with placeholder values
2. **Deployment Time**: Deployer replaces placeholders with environment-specific values
3. **Source**: Values from `varsub/{ENV}/{PackageName}.vs.properties`

### Supported Placeholders

In your webMethods packages, use placeholders like:
```
${db.url}
${api.endpoint}
${file.input.path}
```

These will be replaced during deployment based on the target environment.

### Variable Substitution File Format

```properties
# Format: key=value
# Keys must match placeholders in packages
db.url=jdbc:sqlserver://server:1433;databaseName=DB
api.endpoint=https://api.company.com/v1
file.input.path=C:/path/to/input
```

## Error Handling

### Build Failures
- ABE validation fails → Check ABE installation
- Package compilation fails → Check package syntax
- FBR creation fails → Check disk space and permissions

### Deployment Failures
- Deployer connection fails → Check Deployer server status
- Project Automator fails → Check PA XML syntax
- Variable substitution fails → Check varsub file format
- Target server unreachable → Check network and server status

### Test Failures
- Test discovery fails → Check test package structure
- Test execution fails → Check Integration Server connection
- Report generation fails → Check report directory permissions

## Troubleshooting

### Common Issues

**Issue**: ABE not found
```
Solution: Verify ${SAG_HOME}/common/AssetBuildEnvironment exists
Check: ls -la ${SAG_HOME}/common/AssetBuildEnvironment
```

**Issue**: Deployer CLI not found
```
Solution: Verify Deployer installation path
Check: ls -la ${SAG_HOME}/IntegrationServer/instances/default/packages/WmDeployer/bin
```

**Issue**: Project Automator generation fails
```
Solution: Check ENV.groovy syntax
Validate: groovy -c ENV.groovy
```

**Issue**: Variable substitution not working
```
Solution: Verify varsub file exists and format is correct
Check: cat varsub/DEV/MyBusinessPackage.vs.properties
```

**Issue**: Tests not discovered
```
Solution: Verify test package manifest.v3 exists
Check: ls -la tests/*/manifest.v3
```

## Best Practices

### 1. Package Development
- Keep packages small and focused
- Use meaningful package names
- Include comprehensive tests
- Document package dependencies

### 2. Environment Configuration
- Keep ENV.groovy in sync with actual environments
- Use descriptive server aliases
- Document all configuration changes
- Version control all configuration files

### 3. Variable Substitution
- Use consistent naming conventions
- Document all variables
- Keep sensitive values in GitHub Secrets
- Test variable substitution in DEV first

### 4. Testing
- Write tests for all critical flows
- Include positive and negative test cases
- Test error handling
- Maintain test data separately

### 5. Deployment
- Always deploy to DEV first
- Test thoroughly in TEST before PROD
- Use manual approvals for TEST and PROD
- Monitor deployments closely
- Keep rollback artifacts for 30 days

### 6. Rollback
- Document rollback reason clearly
- Test rollback in TEST first if possible
- Verify system stability after rollback
- Plan forward fix immediately

## Security Considerations

### Secrets Management
All sensitive values are stored in GitHub Secrets:
- `DEPLOYER_HOST`
- `DEPLOYER_PORT`
- `DEPLOYER_USERNAME`
- `DEPLOYER_PASSWORD`
- `IS_USERNAME`
- `IS_PASSWORD`

### Access Control
- GitHub Actions requires appropriate permissions
- Self-hosted runners must have access to webMethods installation
- Deployer user must have deployment permissions
- Integration Server user must have admin permissions

### Audit Trail
- All deployments are logged in GitHub Actions
- Deployment history is maintained in GitHub releases
- Rollbacks create GitHub issues for tracking
- All changes are version controlled

## Maintenance

### Regular Tasks
- Review and update ENV.groovy quarterly
- Update variable substitution files as needed
- Clean up old build artifacts (automated)
- Review and update test cases
- Update documentation

### Monitoring
- Monitor GitHub Actions workflow runs
- Check self-hosted runner health
- Monitor Deployer server performance
- Review deployment logs regularly
- Track test success rates

## Support and Troubleshooting

### Logs Location
- **Build Logs**: GitHub Actions workflow logs
- **Deployer Logs**: `${SAG_HOME}/IntegrationServer/instances/default/packages/WmDeployer/logs/`
- **Integration Server Logs**: `${SAG_HOME}/IntegrationServer/instances/default/logs/`
- **Test Reports**: `report/` directory (uploaded as artifacts)

### Debug Mode
Enable verbose logging:
```bash
ant -f build.xml build -Dproject.properties=build.properties -verbose -debug
```

### Contact
For issues or questions:
1. Check this documentation
2. Review GitHub Actions logs
3. Check Deployer logs
4. Contact DevOps team

## Appendix

### A. ANT Targets Reference

| Target | Description | Required Properties |
|--------|-------------|-------------------|
| build | Build packages with ABE | isPackagesDir, fbrRepoDir, config.build.abeHome |
| deploy | Deploy to target environment | fbrRepoDir, config.deployer.*, environmentsDefinition |
| test | Execute WmTestSuite tests | isTestDir, config.test.*, environmentsDefinition |
| buildDeployTest | Complete CI/CD workflow | All of the above |
| clean | Clean temporary files | config.tmpdir |
| init | Initialize build environment | config.tmpdir |

### B. Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| SAG_HOME | Software AG installation directory | C:/SoftwareAG |
| CICD_HOME | CI/CD framework directory | ${workspace}/cicd |
| JAVA_HOME | Java installation directory | C:/SoftwareAG/jvm/jvm |
| PROJECT_NAME | Project name | hsi_onprem_cicd |

### C. File Extensions

| Extension | Description |
|-----------|-------------|
| .xml | ANT build files, Project Automator XML |
| .groovy | Groovy scripts, environment definitions |
| .properties | Build properties, variable substitution |
| .ndf | webMethods node definitions |
| .v3 | webMethods manifest files |

### D. Glossary

- **ABE**: Asset Build Environment - Software AG tool for building packages
- **FBR**: File-Based Repository - Storage format for built packages
- **PA**: Project Automator - CLI tool for automated deployments
- **VarSub**: Variable Substitution - Environment-specific configuration
- **WmTestSuite**: webMethods testing framework
- **IS**: Integration Server
- **UM**: Universal Messaging
- **JDBC**: Java Database Connectivity
- **AS400**: IBM AS/400 system

---

**Document Version**: 1.0  
**Last Updated**: 2026-04-09  
**Framework Version**: Custom Build v1.0  
**Author**: Bob (AI Assistant)