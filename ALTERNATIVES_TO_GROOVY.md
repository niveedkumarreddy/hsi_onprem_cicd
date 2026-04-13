# Alternatives to Groovy Script for Project Automator Generation

This document presents three alternatives to replace the Groovy script (`GenerateProjectAutomator.groovy`) with simpler solutions that don't require Groovy.

---

## Current Approach (Groovy Script)

**File:** `cicd/scripts/GenerateProjectAutomator.groovy`

**Pros:**
- Dynamic XML generation
- Reads ENV.groovy for environment definitions
- Flexible and programmable
- Can handle complex logic

**Cons:**
- ❌ Requires Groovy runtime
- ❌ Additional dependency
- ❌ Less familiar to most developers
- ❌ Harder to debug

---

## Option 1: Static XML Templates with ANT Property Replacement

### ⭐ RECOMMENDED - Simplest Solution

**Complexity:** ⭐ Low  
**Flexibility:** ⭐⭐ Medium  
**Dependencies:** ✅ None (pure ANT)

### How It Works

1. Create XML template with `@TOKEN@` placeholders
2. Use ANT's `<copy>` task with `<filterset>` to replace tokens
3. No scripting required - pure ANT/XML

### Implementation

#### Step 1: Create Template File

**File:** `deployer/ProjectAutomator_DEV.template.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<DeploymentProject>
    <ProjectName>@PROJECT_PREFIX@_@BUILD_NUMBER@_@PROJECT_NAME@</ProjectName>
    <Description>Automated deployment for @TARGET_ENV@ environment</Description>
    
    <DeploymentSet name="MainDeploymentSet">
        <!-- Package 1 -->
        <DeploymentCandidate>
            <SourceRepositoryName>@FBR_REPO_NAME@</SourceRepositoryName>
            <MapSetName>MyBusinessPackage</MapSetName>
            <MapName>MyBusinessPackage</MapName>
            <TargetServerAlias>@TARGET_ENV@_IS</TargetServerAlias>
            <DeploymentSetName>MainDeploymentSet</DeploymentSetName>
        </DeploymentCandidate>
        
        <!-- Add more packages here as needed -->
    </DeploymentSet>
    
    <DeploymentMap>
        <!-- Integration Server -->
        <alias name="@TARGET_ENV@_IS">
            <type>IS</type>
            <host>@IS_HOST@</host>
            <port>@IS_PORT@</port>
            <user>@IS_USERNAME@</user>
            <pwd>@IS_PASSWORD@</pwd>
            <useSSL>false</useSSL>
        </alias>
        
        <!-- Universal Messaging -->
        <alias name="@TARGET_ENV@_UM">
            <type>UM</type>
            <host>@UM_HOST@</host>
            <port>@UM_PORT@</port>
        </alias>
        
        <!-- JDBC Adapter -->
        <alias name="@TARGET_ENV@_JDBC">
            <type>JDBC</type>
            <host>@IS_HOST@</host>
            <port>@IS_PORT@</port>
            <user>@IS_USERNAME@</user>
            <pwd>@IS_PASSWORD@</pwd>
        </alias>
        
        <!-- AS400 Adapter -->
        <alias name="@TARGET_ENV@_AS400">
            <type>AS400</type>
            <host>@IS_HOST@</host>
            <port>@IS_PORT@</port>
            <user>@IS_USERNAME@</user>
            <pwd>@IS_PASSWORD@</pwd>
        </alias>
    </DeploymentMap>
    
    <DeploymentConfiguration>
        <variableSubstitution>@DO_VARSUB@</variableSubstitution>
        <variableSubstitutionDir>@VARSUB_DIR@</variableSubstitutionDir>
    </DeploymentConfiguration>
</DeploymentProject>
```

#### Step 2: Create Environment-Specific Property Files

**File:** `deployer/environments/DEV.properties`

```properties
# DEV Environment Configuration
TARGET_ENV=DEV
PROJECT_PREFIX=DEV
IS_HOST=dev-server.company.com
IS_PORT=5555
IS_USERNAME=Administrator
IS_PASSWORD=manage
UM_HOST=dev-um.company.com
UM_PORT=9000
DO_VARSUB=true
VARSUB_DIR=varsub/DEV
```

**File:** `deployer/environments/TEST.properties`

```properties
# TEST Environment Configuration
TARGET_ENV=TEST
PROJECT_PREFIX=TEST
IS_HOST=test1-server.company.com
IS_PORT=5555
IS_USERNAME=Administrator
IS_PASSWORD=manage
UM_HOST=test-um.company.com
UM_PORT=9000
DO_VARSUB=true
VARSUB_DIR=varsub/TEST
```

**File:** `deployer/environments/PROD.properties`

```properties
# PROD Environment Configuration
TARGET_ENV=PROD
PROJECT_PREFIX=PROD
IS_HOST=prod1-server.company.com
IS_PORT=5555
IS_USERNAME=Administrator
IS_PASSWORD=manage
UM_HOST=prod-um.company.com
UM_PORT=9000
DO_VARSUB=true
VARSUB_DIR=varsub/PROD
```

#### Step 3: Update ANT Build File

**File:** `cicd/build-deployer-simple.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project name="deployer-simple" basedir="." default="deploy">
    
    <!-- Load project properties -->
    <property file="${project.properties}"/>
    
    <!-- Load environment-specific properties -->
    <property file="${project.dir}/deployer/environments/${bda.targetEnv}.properties"/>
    
    <target name="deployer.generatePA" description="Generate Project Automator XML from template">
        <echo message="Generating Project Automator XML for ${bda.targetEnv}..."/>
        
        <!-- Copy template and replace tokens -->
        <copy file="${project.dir}/deployer/ProjectAutomator_${bda.targetEnv}.template.xml"
              tofile="${project.dir}/deployer/Deployment_ProjectAutomator.xml"
              overwrite="true">
            <filterset>
                <filter token="PROJECT_PREFIX" value="${PROJECT_PREFIX}"/>
                <filter token="BUILD_NUMBER" value="${bda.buildNumber}"/>
                <filter token="PROJECT_NAME" value="${bda.projectName}"/>
                <filter token="TARGET_ENV" value="${TARGET_ENV}"/>
                <filter token="FBR_REPO_NAME" value="${fbrRepoName}"/>
                <filter token="IS_HOST" value="${IS_HOST}"/>
                <filter token="IS_PORT" value="${IS_PORT}"/>
                <filter token="IS_USERNAME" value="${IS_USERNAME}"/>
                <filter token="IS_PASSWORD" value="${IS_PASSWORD}"/>
                <filter token="UM_HOST" value="${UM_HOST}"/>
                <filter token="UM_PORT" value="${UM_PORT}"/>
                <filter token="DO_VARSUB" value="${DO_VARSUB}"/>
                <filter token="VARSUB_DIR" value="${VARSUB_DIR}"/>
            </filterset>
        </copy>
        
        <echo message="Project Automator XML generated successfully"/>
        <echo message="Output: ${project.dir}/deployer/Deployment_ProjectAutomator.xml"/>
    </target>
    
    <target name="deploy" depends="deployer.generatePA">
        <echo message="Executing Project Automator..."/>
        <exec executable="${config.deployer.deployerInstallationPath}/projectautomator.sh"
              failonerror="true">
            <arg value="-f"/>
            <arg value="${project.dir}/deployer/Deployment_ProjectAutomator.xml"/>
        </exec>
    </target>
    
</project>
```

### Pros & Cons

**Pros:**
- ✅ No Groovy dependency
- ✅ Simple to understand
- ✅ Easy to maintain
- ✅ Pure ANT/XML solution
- ✅ Works with any ANT installation

**Cons:**
- ❌ Need separate template per environment (or one generic template)
- ❌ Less flexible for complex logic
- ❌ Manual updates when adding packages

### When to Use
- ✅ Simple deployments
- ✅ Fixed number of packages
- ✅ Want minimal dependencies
- ✅ Team familiar with ANT

---

## Option 2: XSLT Transformation

### Advanced but Flexible

**Complexity:** ⭐⭐⭐ High  
**Flexibility:** ⭐⭐⭐ High  
**Dependencies:** ✅ None (XSLT processor built into Java/ANT)

### How It Works

1. Define environments in simple XML format
2. Create XSLT stylesheet to transform XML → Project Automator XML
3. Use ANT's `<xslt>` task for transformation

### Implementation

#### Step 1: Create Environment Definition XML

**File:** `deployer/environments.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<environments>
    <environment name="DEV">
        <integrationServer alias="DEV_IS">
            <host>dev-server.company.com</host>
            <port>5555</port>
            <username>Administrator</username>
            <password>manage</password>
        </integrationServer>
        <universalMessaging alias="DEV_UM">
            <host>dev-um.company.com</host>
            <port>9000</port>
        </universalMessaging>
        <packages>
            <package name="MyBusinessPackage"/>
            <package name="MyUtilityPackage"/>
        </packages>
    </environment>
    
    <environment name="TEST">
        <integrationServer alias="TEST_IS">
            <host>test1-server.company.com</host>
            <port>5555</port>
            <username>Administrator</username>
            <password>manage</password>
        </integrationServer>
        <universalMessaging alias="TEST_UM">
            <host>test-um.company.com</host>
            <port>9000</port>
        </universalMessaging>
        <packages>
            <package name="MyBusinessPackage"/>
            <package name="MyUtilityPackage"/>
        </packages>
    </environment>
    
    <environment name="PROD">
        <integrationServer alias="PROD_IS">
            <host>prod1-server.company.com</host>
            <port>5555</port>
            <username>Administrator</username>
            <password>manage</password>
        </integrationServer>
        <universalMessaging alias="PROD_UM">
            <host>prod-um.company.com</host>
            <port>9000</port>
        </universalMessaging>
        <packages>
            <package name="MyBusinessPackage"/>
            <package name="MyUtilityPackage"/>
        </packages>
    </environment>
</environments>
```

#### Step 2: Create XSLT Stylesheet

**File:** `deployer/transform-to-pa.xsl`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <xsl:param name="targetEnv"/>
    <xsl:param name="projectName"/>
    <xsl:param name="buildNumber"/>
    <xsl:param name="fbrRepoName"/>
    <xsl:param name="varsubDir"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="environments/environment[@name=$targetEnv]"/>
    </xsl:template>
    
    <xsl:template match="environment">
        <DeploymentProject>
            <ProjectName>
                <xsl:value-of select="concat(@name, '_', $buildNumber, '_', $projectName)"/>
            </ProjectName>
            <Description>
                <xsl:value-of select="concat('Automated deployment for ', @name, ' environment')"/>
            </Description>
            
            <DeploymentSet name="MainDeploymentSet">
                <xsl:apply-templates select="packages/package"/>
            </DeploymentSet>
            
            <DeploymentMap>
                <xsl:apply-templates select="integrationServer"/>
                <xsl:apply-templates select="universalMessaging"/>
            </DeploymentMap>
            
            <DeploymentConfiguration>
                <variableSubstitution>true</variableSubstitution>
                <variableSubstitutionDir>
                    <xsl:value-of select="$varsubDir"/>
                </variableSubstitutionDir>
            </DeploymentConfiguration>
        </DeploymentProject>
    </xsl:template>
    
    <xsl:template match="package">
        <DeploymentCandidate>
            <SourceRepositoryName>
                <xsl:value-of select="$fbrRepoName"/>
            </SourceRepositoryName>
            <MapSetName>
                <xsl:value-of select="@name"/>
            </MapSetName>
            <MapName>
                <xsl:value-of select="@name"/>
            </MapName>
            <TargetServerAlias>
                <xsl:value-of select="../../integrationServer/@alias"/>
            </TargetServerAlias>
            <DeploymentSetName>MainDeploymentSet</DeploymentSetName>
        </DeploymentCandidate>
    </xsl:template>
    
    <xsl:template match="integrationServer">
        <alias name="{@alias}">
            <type>IS</type>
            <host><xsl:value-of select="host"/></host>
            <port><xsl:value-of select="port"/></port>
            <user><xsl:value-of select="username"/></user>
            <pwd><xsl:value-of select="password"/></pwd>
            <useSSL>false</useSSL>
        </alias>
    </xsl:template>
    
    <xsl:template match="universalMessaging">
        <alias name="{@alias}">
            <type>UM</type>
            <host><xsl:value-of select="host"/></host>
            <port><xsl:value-of select="port"/></port>
        </alias>
    </xsl:template>
    
</xsl:stylesheet>
```

#### Step 3: Update ANT Build File

**File:** `cicd/build-deployer-xslt.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project name="deployer-xslt" basedir="." default="deploy">
    
    <property file="${project.properties}"/>
    
    <target name="deployer.generatePA" description="Generate Project Automator XML using XSLT">
        <echo message="Generating Project Automator XML for ${bda.targetEnv} using XSLT..."/>
        
        <!-- Transform environments.xml to Project Automator XML -->
        <xslt in="${project.dir}/deployer/environments.xml"
              out="${project.dir}/deployer/Deployment_ProjectAutomator.xml"
              style="${project.dir}/deployer/transform-to-pa.xsl">
            <param name="targetEnv" expression="${bda.targetEnv}"/>
            <param name="projectName" expression="${bda.projectName}"/>
            <param name="buildNumber" expression="${bda.buildNumber}"/>
            <param name="fbrRepoName" expression="${fbrRepoName}"/>
            <param name="varsubDir" expression="${varsubDir}/${bda.targetEnv}"/>
        </xslt>
        
        <echo message="Project Automator XML generated successfully"/>
    </target>
    
    <target name="deploy" depends="deployer.generatePA">
        <echo message="Executing Project Automator..."/>
        <exec executable="${config.deployer.deployerInstallationPath}/projectautomator.sh"
              failonerror="true">
            <arg value="-f"/>
            <arg value="${project.dir}/deployer/Deployment_ProjectAutomator.xml"/>
        </exec>
    </target>
    
</project>
```

### Pros & Cons

**Pros:**
- ✅ No Groovy dependency
- ✅ Single environment definition file
- ✅ Flexible and powerful
- ✅ Standard XML/XSLT technology
- ✅ Built into Java/ANT

**Cons:**
- ❌ XSLT is complex and verbose
- ❌ Harder to learn and maintain
- ❌ Less readable than Groovy
- ❌ Debugging can be difficult

### When to Use
- ✅ Complex transformations needed
- ✅ Want single source of truth for environments
- ✅ Team familiar with XSLT
- ✅ Need maximum flexibility without scripting

---

## Option 3: Keep Groovy but Simplify

### Improve Current Approach

**Complexity:** ⭐⭐ Medium  
**Flexibility:** ⭐⭐⭐ High  
**Dependencies:** ❌ Groovy required

### Improvements to Current Groovy Script

1. **Better documentation**
2. **Simpler ENV.groovy format**
3. **Error handling**
4. **Logging**

#### Simplified ENV.groovy

**File:** `ENV-simple.groovy`

```groovy
// Simplified environment definitions
environments = [
    DEV: [
        is: [host: 'dev-server.company.com', port: 5555, user: 'Administrator', pwd: 'manage'],
        um: [host: 'dev-um.company.com', port: 9000],
        packages: ['MyBusinessPackage', 'MyUtilityPackage']
    ],
    TEST: [
        is: [host: 'test1-server.company.com', port: 5555, user: 'Administrator', pwd: 'manage'],
        um: [host: 'test-um.company.com', port: 9000],
        packages: ['MyBusinessPackage', 'MyUtilityPackage']
    ],
    PROD: [
        is: [host: 'prod1-server.company.com', port: 5555, user: 'Administrator', pwd: 'manage'],
        um: [host: 'prod-um.company.com', port: 9000],
        packages: ['MyBusinessPackage', 'MyUtilityPackage']
    ]
]
```

#### Improved Groovy Script

**File:** `cicd/scripts/GenerateProjectAutomator-improved.groovy`

```groovy
#!/usr/bin/env groovy

import groovy.xml.MarkupBuilder

// Read command line arguments
def targetEnv = args[0]
def projectName = args[1]
def buildNumber = args[2]
def fbrRepoName = args[3]
def varsubDir = args[4]
def envFile = args[5]
def outputFile = args[6]

println "Generating Project Automator XML..."
println "  Environment: ${targetEnv}"
println "  Project: ${projectName}"
println "  Build: ${buildNumber}"

// Load environment definitions
def envConfig = new ConfigSlurper().parse(new File(envFile).toURI().toURL())
def env = envConfig.environments[targetEnv]

if (!env) {
    throw new Exception("Environment '${targetEnv}' not found in ${envFile}")
}

// Generate XML
def writer = new StringWriter()
def xml = new MarkupBuilder(writer)

xml.mkp.xmlDeclaration(version: '1.0', encoding: 'UTF-8')
xml.DeploymentProject {
    ProjectName("${targetEnv}_${buildNumber}_${projectName}")
    Description("Automated deployment for ${targetEnv} environment")
    
    DeploymentSet(name: 'MainDeploymentSet') {
        env.packages.each { pkg ->
            DeploymentCandidate {
                SourceRepositoryName(fbrRepoName)
                MapSetName(pkg)
                MapName(pkg)
                TargetServerAlias("${targetEnv}_IS")
                DeploymentSetName('MainDeploymentSet')
            }
        }
    }
    
    DeploymentMap {
        // Integration Server
        alias(name: "${targetEnv}_IS") {
            type('IS')
            host(env.is.host)
            port(env.is.port)
            user(env.is.user)
            pwd(env.is.pwd)
            useSSL('false')
        }
        
        // Universal Messaging
        if (env.um) {
            alias(name: "${targetEnv}_UM") {
                type('UM')
                host(env.um.host)
                port(env.um.port)
            }
        }
    }
    
    DeploymentConfiguration {
        variableSubstitution('true')
        variableSubstitutionDir("${varsubDir}/${targetEnv}")
    }
}

// Write to file
new File(outputFile).text = writer.toString()
println "✓ Project Automator XML generated: ${outputFile}"
```

### Pros & Cons

**Pros:**
- ✅ Most flexible solution
- ✅ Easy to read and maintain
- ✅ Powerful programming capabilities
- ✅ Good error handling

**Cons:**
- ❌ Requires Groovy runtime
- ❌ Additional dependency
- ❌ May not be available in all environments

### When to Use
- ✅ Complex deployment logic needed
- ✅ Groovy already available
- ✅ Team comfortable with Groovy
- ✅ Need maximum flexibility

---

## Comparison Matrix

| Feature | Option 1: Templates | Option 2: XSLT | Option 3: Groovy |
|---------|-------------------|----------------|------------------|
| **Complexity** | ⭐ Low | ⭐⭐⭐ High | ⭐⭐ Medium |
| **Flexibility** | ⭐⭐ Medium | ⭐⭐⭐ High | ⭐⭐⭐ High |
| **Dependencies** | ✅ None | ✅ None | ❌ Groovy |
| **Readability** | ✅ Excellent | ❌ Poor | ✅ Good |
| **Maintainability** | ✅ Easy | ❌ Hard | ✅ Easy |
| **Learning Curve** | ✅ Low | ❌ High | ⭐ Medium |
| **Error Handling** | ⭐ Basic | ⭐ Basic | ✅ Advanced |
| **Dynamic Logic** | ❌ Limited | ✅ Good | ✅ Excellent |
| **Team Familiarity** | ✅ High (ANT) | ❌ Low (XSLT) | ⭐ Medium |

---

## Recommendation

### For Most Projects: **Option 1 - Static XML Templates**

**Why?**
- ✅ Simplest to implement and maintain
- ✅ No external dependencies
- ✅ Easy for any developer to understand
- ✅ Sufficient for most deployment scenarios
- ✅ Pure ANT/XML solution

**Best For:**
- Standard deployments
- Fixed package lists
- Teams familiar with ANT
- Minimal scripting requirements

### For Complex Projects: **Option 2 - XSLT**

**Why?**
- ✅ No dependencies but highly flexible
- ✅ Single source of truth
- ✅ Powerful transformation capabilities

**Best For:**
- Complex environment configurations
- Dynamic package selection
- Teams with XSLT expertise
- Need for advanced transformations

### Keep Groovy If:
- Already using Groovy elsewhere
- Team comfortable with Groovy
- Need complex deployment logic
- Groovy runtime readily available

---

## Migration Path

### From Groovy to Templates (Recommended)

1. Create template files for each environment
2. Create environment property files
3. Update `build-deployer.xml` to use templates
4. Test with DEV environment
5. Migrate TEST and PROD
6. Remove Groovy script and ENV.groovy

### From Groovy to XSLT

1. Convert ENV.groovy to environments.xml
2. Create XSLT stylesheet
3. Update `build-deployer.xml` to use XSLT
4. Test transformations
5. Remove Groovy script

---

## Conclusion

**Recommended Approach:** Option 1 (Static XML Templates)

This provides the best balance of:
- Simplicity
- No dependencies
- Easy maintenance
- Sufficient flexibility for most use cases

Choose Option 2 (XSLT) only if you need advanced transformation logic and have XSLT expertise.

Keep Option 3 (Groovy) only if Groovy is already part of your toolchain.

---

**Document Version:** 1.0  
**Last Updated:** 2026-04-09  
**Author:** Bob (AI Assistant)