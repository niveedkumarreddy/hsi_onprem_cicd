# webMethods CI/CD Framework

A reusable CI/CD framework for webMethods Integration Server projects using GitHub Actions, ANT, and Asset Build Environment (ABE).

## 🎯 Features

- **Automated Build**: Build webMethods packages using Asset Build Environment (ABE)
- **Multi-Environment Deployment**: Support for DEV, TEST, QA, and PROD environments
- **Variable Substitution**: Environment-specific configuration management
- **Rollback Support**: Quick rollback to previous versions
- **GitHub Actions Integration**: Native CI/CD workflows
- **Reusable**: Use across multiple webMethods projects
- **Configurable**: Flexible property-based configuration

## 📋 Prerequisites

- webMethods Integration Server 10.x or higher
- Asset Build Environment (ABE) installed
- GitHub Actions self-hosted runner (Windows)
- ANT 1.9.x or higher
- Git

## 🚀 Quick Start

### For New Projects

1. **Create your application repository**:
```bash
mkdir my-webmethods-app
cd my-webmethods-app
git init
```

2. **Add this framework as a submodule**:
```bash
git submodule add https://github.com/your-org/webmethods-cicd-framework.git .cicd
git submodule update --init --recursive
```

3. **Copy the custom properties template**:
```bash
cp .cicd/custom.properties.template custom.properties
```

4. **Edit `custom.properties`** with your project details:
```properties
bda.projectName=my-webmethods-app
isPackagesDir=packages
varsubDir=varsub
```

5. **Create your application structure**:
```
my-webmethods-app/
├── .cicd/                    # Framework submodule
├── packages/                 # Your IS packages
├── varsub/                   # Variable substitution files
│   ├── DEV/
│   ├── TEST/
│   └── PROD/
├── custom.properties         # Project configuration
└── .github/
    └── workflows/
        └── deploy.yml        # Your workflow
```

6. **Create workflow** (`.github/workflows/deploy.yml`):
```yaml
name: Deploy Application

on:
  push:
    branches: [main, develop]

jobs:
  build:
    runs-on: [self-hosted]
    steps:
      - uses: actions/checkout@v4
        with:
          submodules: recursive
      
      - name: Build
        run: ant -f .cicd/cicd/build.xml build -Dproject.properties=custom.properties
      
      - name: Deploy to DEV
        run: ant -f .cicd/cicd/build.xml deploy -Dbda.targetEnv=DEV
```

## 📁 Framework Structure

```
webmethods-cicd-framework/
├── .github/
│   └── workflows/
│       ├── webmethods-cicd.yml      # Reference workflow
│       └── rollback.yml              # Rollback workflow
├── cicd/
│   ├── build.xml                     # Main build orchestrator
│   ├── build-abe.xml                 # Asset Build Environment
│   ├── build-deployer.xml            # Deployment logic
│   └── build-test.xml                # Testing logic
├── deployer/
│   ├── ProjectAutomator.template.xml # Deployment template
│   └── environments/
│       ├── DEV.properties            # DEV environment config
│       ├── TEST.properties           # TEST environment config
│       ├── QA.properties             # QA environment config
│       └── PROD.properties           # PROD environment config
├── build.properties                  # SAG reference properties
├── custom.properties.template        # Template for projects
└── README.md                         # This file
```

## 🔧 Configuration

### Environment Properties

Each environment has its own properties file in `deployer/environments/`:

**DEV.properties**:
```properties
TARGET_ENV=DEV
IS_HOST=dev-server.company.com
IS_PORT=5555
IS_USERNAME=Administrator
IS_PASSWORD=manage
DO_VARSUB=true
VARSUB_DIR=varsub/DEV
```

### Custom Properties

Your application's `custom.properties` file:

```properties
# Project Information
bda.projectName=my-app
project.description=My Application

# Source Configuration
isPackagesDir=packages
build.source.dir=${basedir}/packages

# Variable Substitution
varsubDir=varsub
config.deployer.doVarSub=true

# SAG Installation
SAGHome=C:/IBM/webMethods
```

## 🎬 Usage

### Build Packages

```bash
ant -f .cicd/cicd/build.xml build \
  -Dproject.properties=custom.properties
```

### Deploy to Environment

```bash
ant -f .cicd/cicd/build.xml deploy \
  -Dproject.properties=custom.properties \
  -Dbda.targetEnv=DEV
```

### Run Tests

```bash
ant -f .cicd/cicd/build.xml test \
  -Dproject.properties=custom.properties
```

### Complete CI/CD Pipeline

```bash
ant -f .cicd/cicd/build.xml all \
  -Dproject.properties=custom.properties \
  -Dbda.targetEnv=DEV
```

## 🔄 Updating the Framework

### Update to Latest Version

```bash
cd .cicd
git pull origin main
cd ..
git add .cicd
git commit -m "Update CI/CD framework"
git push
```

### Pin to Specific Version

```bash
cd .cicd
git checkout v1.2.0
cd ..
git add .cicd
git commit -m "Pin framework to v1.2.0"
git push
```

## 🌐 Multi-Server Deployment

The framework supports deploying to multiple servers per environment:

```yaml
deploy-test:
  strategy:
    matrix:
      server: [test1, test2, test3]
  steps:
    - name: Deploy to TEST-${{ matrix.server }}
      run: |
        ant -f .cicd/cicd/build.xml deploy \
          -Dconfig.deployer.deployerHost=${{ matrix.server }}-server.company.com \
          -Dbda.targetEnv=TEST
```

## 🔐 Security

### GitHub Secrets

Store sensitive information in GitHub Secrets:

1. Go to repository **Settings → Secrets → Actions**
2. Add secrets:
   - `DEPLOYER_USERNAME`
   - `DEPLOYER_PASSWORD`
   - `DEV_DEPLOYER_PASSWORD`
   - `TEST_DEPLOYER_PASSWORD`
   - `PROD_DEPLOYER_PASSWORD`

### Use in Workflows

```yaml
- name: Deploy
  run: |
    ant -f .cicd/cicd/build.xml deploy \
      -Dconfig.deployer.deployerUsername=${{ secrets.DEPLOYER_USERNAME }} \
      -Dconfig.deployer.deployerPassword=${{ secrets.DEPLOYER_PASSWORD }}
```

## 📊 Property Hierarchy

Properties are loaded in this order (later overrides earlier):

1. `build.properties` (SAG reference)
2. `custom.properties` (project-specific)
3. Environment properties (from `deployer/environments/`)
4. Workflow properties (runtime)
5. Command-line parameters (highest priority)

## 🧪 Testing

The framework includes test execution support:

```bash
# Run all tests
ant -f .cicd/cicd/build.xml test

# Skip tests
ant -f .cicd/cicd/build.xml build -Dskip.tests=true
```

## 📝 Variable Substitution

Create environment-specific files in `varsub/`:

```
varsub/
├── DEV/
│   └── config.properties
├── TEST/
│   └── config.properties
└── PROD/
    └── config.properties
```

The framework automatically substitutes values during deployment.

## 🔙 Rollback

Use the rollback workflow to revert to a previous version:

```yaml
name: Rollback
on:
  workflow_dispatch:
    inputs:
      environment:
        required: true
        type: choice
        options: [DEV, TEST, QA, PROD]
      build-number:
        required: true
        type: string
```

## 📚 Documentation

- [Framework Usage Guide](FRAMEWORK_USAGE.md)
- [Property Configuration](../PROPERTY_CONFIGURATION_GUIDE.md)
- [Repository Separation Plan](../REPOSITORY_SEPARATION_PLAN.md)

## 🆕 Versioning

This framework follows [Semantic Versioning](https://semver.org/):

- **Major** (v2.0.0): Breaking changes
- **Minor** (v1.1.0): New features, backward compatible
- **Patch** (v1.0.1): Bug fixes

See [CHANGELOG.md](CHANGELOG.md) for version history.

## 🤝 Contributing

1. Fork the framework repository
2. Create a feature branch
3. Make your changes
4. Test with a sample project
5. Submit a pull request

## 📄 License

Copyright © 2026. All rights reserved.

## 🆘 Support

For issues or questions:
- Create an issue in the framework repository
- Contact the DevOps team
- Check the documentation

## 🎓 Best Practices

1. **Pin framework versions** in production
2. **Test framework updates** in DEV first
3. **Use semantic versioning** for releases
4. **Document custom changes** in your project
5. **Keep secrets secure** using GitHub Secrets
6. **Review framework updates** before applying
7. **Maintain backward compatibility** when possible

---

**Happy Building! 🚀**