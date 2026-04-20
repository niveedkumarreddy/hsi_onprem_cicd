# HSI On-Premise Application

webMethods Integration Server application for HSI on-premise integration.

## 📦 Packages

This application contains the following webMethods packages:

- **AIACommonCoreLogger**: Core logging functionality with Log4j2 integration
- **AIACommonLogClient**: Client library for publishing log events
- **AIACommonLogger**: Advanced logging services with thread pool management
- **AIACommonNotification**: Email notification services for error handling
- **CustomHIPAA**: HIPAA-compliant HL7 message processing
- **CustHipaaHL7**: Custom HIPAA HL7 services
- **OH_Ops**: Operational utilities for Trading Networks and JMS management

## 🚀 Quick Start

### Prerequisites

- webMethods Integration Server 10.x or higher
- Git with submodule support
- ANT 1.9.x or higher
- GitHub Actions self-hosted runner (for CI/CD)

### Clone Repository

```bash
git clone <repository-url>
cd hsi-onprem-application
git submodule update --init --recursive
```

### Local Build

```bash
# Build all packages
ant -f .cicd/cicd/build.xml build -Dproject.properties=custom.properties

# Deploy to DEV
ant -f .cicd/cicd/build.xml deploy -Dbda.targetEnv=DEV
```

## 📁 Project Structure

```
hsi-onprem-application/
├── .cicd/                      # CI/CD Framework (git submodule)
├── .github/
│   └── workflows/
│       └── deploy.yml          # Deployment workflow
├── packages/                   # webMethods IS packages
│   ├── AIACommonCoreLogger/
│   ├── AIACommonLogClient/
│   ├── AIACommonLogger/
│   ├── AIACommonNotification/
│   ├── CustomHIPAA/
│   ├── CustHipaaHL7/
│   └── OH_Ops/
├── tests/                      # Test cases
├── varsub/                     # Variable substitution files
│   ├── DEV/
│   ├── TEST/
│   ├── QA/
│   └── PROD/
├── custom.properties           # Project configuration
├── .gitignore
├── .gitmodules                 # Submodule configuration
└── README.md                   # This file
```

## ⚙️ Configuration

### custom.properties

Main configuration file for the project:

```properties
# Project Information
bda.projectName=hsi_onprem_cicd
project.description=HSI On-Premise Integration Application

# Source Configuration
isPackagesDir=packages
build.source.dir=${basedir}/packages

# Variable Substitution
varsubDir=varsub
config.deployer.doVarSub=true

# SAG Installation
SAGHome=C:/IBM/webMethods
```

### Environment-Specific Configuration

Variable substitution files are located in `varsub/<ENV>/`:

- `varsub/DEV/` - Development environment
- `varsub/TEST/` - Test environment
- `varsub/QA/` - QA environment
- `varsub/PROD/` - Production environment

## 🔄 CI/CD Pipeline

### Automated Deployment

The application uses GitHub Actions for automated CI/CD:

1. **Build**: Triggered on push to main/develop branches
2. **Test**: Runs automated tests (if configured)
3. **Deploy**: Deploys to target environment based on branch

### Manual Deployment

```bash
# Deploy to specific environment
ant -f .cicd/cicd/build.xml deploy \
  -Dproject.properties=custom.properties \
  -Dbda.targetEnv=TEST
```

### Rollback

Use the rollback workflow to revert to a previous version:

1. Go to Actions → Rollback
2. Select environment and build number
3. Run workflow

## 🧪 Testing

```bash
# Run tests
ant -f .cicd/cicd/build.xml test -Dproject.properties=custom.properties
```

## 🔧 Development

### Adding New Packages

1. Create package in Designer
2. Export to `packages/` directory
3. Commit and push changes
4. CI/CD will automatically build and deploy

### Updating CI/CD Framework

```bash
# Update to latest framework version
cd .cicd
git pull origin main
cd ..
git add .cicd
git commit -m "Update CI/CD framework"
git push
```

## 📊 Package Details

### AIACommonCoreLogger
- **Purpose**: Core logging with Log4j2
- **Key Services**: `initLogger`, `log`, `maskSensitiveData`
- **Dependencies**: log4j-api-2.17.1.jar, log4j-core-2.17.1.jar

### AIACommonLogClient
- **Purpose**: Log event publishing client
- **Key Services**: `publishLogEvent`, `svcStart`, `svcEnd`
- **Configuration**: loggingConfigurationsPub.xml

### AIACommonLogger
- **Purpose**: Advanced logging with async processing
- **Key Services**: `logData`, `processLogs`, thread pool management
- **Dependencies**: disruptor-3.4.2.jar

### AIACommonNotification
- **Purpose**: Email notifications for errors
- **Key Services**: `sendEmailNotification`
- **Templates**: Located in `templates/` directory

### CustomHIPAA / CustHipaaHL7
- **Purpose**: HIPAA-compliant HL7 message processing
- **Key Services**: `sampleHL7Service`, `brachingStatus`

### OH_Ops
- **Purpose**: Operational utilities
- **Key Services**: 
  - Trading Networks: `archive`, `deleteSchedulerEntries`
  - JMS: `purgeOldJmsEvents`
  - State Management: `ping`, `get`, `set`

## 🔐 Security

- Credentials stored in GitHub Secrets
- No sensitive data in repository
- Variable substitution for environment-specific values
- ACL management for service access

## 📚 Documentation

- [CI/CD Framework Documentation](../.cicd/README.md)
- [Property Configuration Guide](../PROPERTY_CONFIGURATION_GUIDE.md)
- [Repository Separation Plan](../REPOSITORY_SEPARATION_PLAN.md)

## 🆘 Support

For issues or questions:
- Check the documentation
- Review CI/CD logs in GitHub Actions
- Contact the development team

## 📝 License

Copyright © 2026. All rights reserved.

---

**Built with webMethods Integration Server** 🚀