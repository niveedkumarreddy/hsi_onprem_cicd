# On-Premise CI/CD Pipeline for webMethods

This GitHub Actions workflow implements a complete CI/CD pipeline for webMethods Integration Server on-premise deployments.

## Pipeline Stages

The pipeline consists of 5 main stages that mirror the Jenkins pipeline structure:

### 1. **Declarative Checkout SCM** (Average: 49s)
- Checks out the source code from the repository
- Retrieves commit information
- Sets up the workspace for subsequent stages

### 2. **Build** (Average: 27s)
- Configures the Asset Build Environment (ABE)
- Updates `build.properties` with correct paths
- Executes the ABE build script to compile Integration Server packages
- Creates build artifacts in the output directory
- Uploads artifacts for downstream stages

### 3. **GenerateAutomator** (Average: 1s)
- Locates the `ProjectAutomator.xml` file
- Updates the automator XML with build output paths and deployer configuration
- Uses Ant script to dynamically replace tokens in the XML
- Prepares the deployment project configuration

### 4. **CreateProject** (Average: 5s)
- Creates the Deployer project using `projectautomator.bat`
- Configures the deployment project with source repository
- Maps target servers for deployment
- Verifies project creation

### 5. **Deploy** (Average: 17s)
- Deploys the built assets to target Integration Server instances
- Uses webMethods Deployer for deployment execution
- Verifies deployment status
- Sends notifications on completion

## Prerequisites

### Self-Hosted Runner Setup

This pipeline requires a **self-hosted GitHub Actions runner** on your on-premise server with:

1. **webMethods Installation**
   - Integration Server
   - Asset Build Environment (ABE)
   - Deployer
   - Installed at: `C:/IBM/webMethods` (or update `SAG_INSTALL_DIR` in workflow)

2. **Required Tools**
   - Git
   - PowerShell 5.1 or higher
   - Java (for Ant scripts)

3. **GitHub Actions Runner**
   - Follow [GitHub's self-hosted runner setup guide](https://docs.github.com/en/actions/hosting-your-own-runners/adding-self-hosted-runners)
   - Register the runner with your repository
   - Ensure it runs with appropriate permissions to access webMethods directories

### Directory Structure

Ensure the following directory structure exists:

```
C:/IBM/webMethods/
├── IntegrationServer/
│   ├── instances/default/
│   └── packages/
├── common/
│   ├── AssetBuildEnvironment/
│   │   ├── bin/build.bat
│   │   └── master_build/build.properties
│   └── lib/ant/bin/
│       ├── ant.bat
│       └── projectautomator.bat
└── workspace/
    └── deployerSource/
        └── [Your IS Packages]
```

## Configuration

### Environment Variables

Update these variables in the workflow file (`.github/workflows/onprem-cicd-pipeline.yml`):

```yaml
env:
  SAG_INSTALL_DIR: C:/IBM/webMethods          # Your webMethods installation path
  BUILD_OUTPUT_DIR: C:/IBM/webMethods/builds  # Where build artifacts are stored
  BUILD_SOURCE_DIR: C:/IBM/webMethods/workspace/deployerSource  # Source packages location
  BUILD_VERSION: 10.3                         # Your webMethods version
  DEPLOYER_HOST: localhost                    # Deployer server host
  DEPLOYER_PORT: 5555                         # Deployer server port
```

### build.properties Configuration

The pipeline automatically updates the following properties:
- `build.output.dir`
- `build.source.dir`
- `enable.build.IS`

Manual configuration required in `build.properties`:
```properties
sag.install.dir=C:/IBM/webMethods
build.version=10.3
is.acdl.config.dir=C:/IBM/webMethods/IntegrationServer/config
```

### ProjectAutomator.xml

Create a `deployer/Deployment_ProjectAutomator.xml` file in your repository with tokens:

```xml
<DeployerSpec>
    <DeployerServer>
        <host>%%DEPLOYER_HOST%%</host>
        <port>%%DEPLOYER_PORT%%</port>
        <user>%%USER%%</user>
        <pwd>%%PASSWORD%%</pwd>
    </DeployerServer>
    <Environment>
        <Repository>
            <repoPath>%%OUTPUT%%</repoPath>
        </Repository>
    </Environment>
</DeployerSpec>
```

Create `deployer/updateAutomator.xml` for token replacement:

```xml
<project name="UpdateAutomator" default="update">
    <target name="update">
        <replace file="Deployment_ProjectAutomator.xml" token="%%OUTPUT%%" value="${build.output.dir}"/>
        <replace file="Deployment_ProjectAutomator.xml" token="%%DEPLOYER_HOST%%" value="${deployer.host}"/>
        <replace file="Deployment_ProjectAutomator.xml" token="%%DEPLOYER_PORT%%" value="${deployer.port}"/>
    </target>
</project>
```

## Usage

### Automatic Triggers

The pipeline runs automatically on:
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

### Manual Trigger

You can manually trigger the pipeline:
1. Go to **Actions** tab in your GitHub repository
2. Select **On-Premise CI/CD Pipeline**
3. Click **Run workflow**
4. Select the branch and click **Run workflow**

## Monitoring

### Pipeline Execution

Monitor pipeline execution in the GitHub Actions tab:
- View real-time logs for each stage
- Check stage execution times
- Download artifacts from completed runs

### Stage Status

Each stage reports its status:
- ✅ Success: Stage completed successfully
- ❌ Failure: Stage failed (pipeline stops)
- ⚠️ Warning: Stage completed with warnings

### Artifacts

Build artifacts are retained for 30 days:
- `build-artifacts`: Compiled IS packages
- `project-automator`: Updated deployment configuration

## Troubleshooting

### Common Issues

1. **Build fails with "build.bat not found"**
   - Verify `SAG_INSTALL_DIR` points to correct installation
   - Check that ABE is installed

2. **Permission denied errors**
   - Ensure GitHub Actions runner has write access to build directories
   - Run runner service with appropriate user account

3. **Deployer connection fails**
   - Verify `DEPLOYER_HOST` and `DEPLOYER_PORT` are correct
   - Check Deployer service is running
   - Verify network connectivity from runner to Deployer

4. **ProjectAutomator.xml not found**
   - Ensure `deployer/` directory exists in repository
   - Verify XML files are committed to repository

### Debug Mode

Enable debug logging by adding to workflow:

```yaml
env:
  ACTIONS_STEP_DEBUG: true
  ACTIONS_RUNNER_DEBUG: true
```

## Security Considerations

### Secrets Management

Store sensitive information as GitHub Secrets:

1. Go to **Settings** → **Secrets and variables** → **Actions**
2. Add secrets:
   - `DEPLOYER_USERNAME`
   - `DEPLOYER_PASSWORD`
   - `TARGET_SERVER_CREDENTIALS`

3. Reference in workflow:
```yaml
env:
  DEPLOYER_USER: ${{ secrets.DEPLOYER_USERNAME }}
  DEPLOYER_PWD: ${{ secrets.DEPLOYER_PASSWORD }}
```

### Runner Security

- Run the self-hosted runner in an isolated environment
- Limit repository access to the runner
- Regularly update the runner software
- Monitor runner logs for suspicious activity

## Customization

### Adding Stages

To add custom stages, insert between existing jobs:

```yaml
custom-stage:
  name: Custom Stage
  runs-on: self-hosted
  needs: build  # Depends on build stage
  steps:
    - name: Custom step
      run: |
        # Your custom logic
```

### Conditional Deployment

Deploy only on specific branches:

```yaml
deploy:
  if: github.ref == 'refs/heads/main'
  # ... rest of deploy job
```

### Notifications

Add notification steps using GitHub Actions marketplace actions:
- Slack notifications
- Email notifications
- Microsoft Teams notifications

## Performance Optimization

### Caching

Add caching for build dependencies:

```yaml
- name: Cache build dependencies
  uses: actions/cache@v4
  with:
    path: ${{ env.SAG_INSTALL_DIR }}/common/lib
    key: ${{ runner.os }}-build-${{ hashFiles('**/build.properties') }}
```

### Parallel Execution

For multiple packages, consider parallel builds:

```yaml
strategy:
  matrix:
    package: [Package1, Package2, Package3]
```

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review GitHub Actions logs
3. Consult webMethods documentation
4. Contact your DevOps team

## Version History

- **v1.0** - Initial pipeline implementation with 5 stages
- Supports webMethods 10.3+
- Windows-based self-hosted runners