#!/usr/bin/env groovy
/**
 * Generate Project Automator XML for webMethods Deployer
 * This script reads ENV.groovy and generates the Project Automator definition
 */

import groovy.xml.MarkupBuilder

// Parse command line arguments
def cli = new CliBuilder(usage: 'GenerateProjectAutomator.groovy [options]')
cli.e(longOpt: 'env', args: 1, required: true, 'Environment (DEV, TEST, PROD)')
cli.p(longOpt: 'project', args: 1, required: true, 'Project name')
cli.r(longOpt: 'repo', args: 1, required: true, 'Repository path')
cli.n(longOpt: 'repoName', args: 1, required: true, 'Repository name')
cli.o(longOpt: 'output', args: 1, required: true, 'Output XML file')
cli.c(longOpt: 'config', args: 1, required: true, 'ENV.groovy file path')
cli.d(longOpt: 'deployer', args: 1, required: true, 'Deployer host:port')
cli.u(longOpt: 'user', args: 1, required: true, 'Deployer username')
cli.s(longOpt: 'password', args: 1, required: true, 'Deployer password')

def options = cli.parse(args)
if (!options) {
    return
}

// Extract parameters
def targetEnv = options.e
def projectName = options.p
def repoPath = options.r
def repoName = options.n
def outputFile = options.o
def envConfigFile = options.c
def deployerHostPort = options.d
def deployerUser = options.u
def deployerPassword = options.s

println "=== Generating Project Automator XML ==="
println "Environment: ${targetEnv}"
println "Project: ${projectName}"
println "Repository: ${repoPath}"
println "Output: ${outputFile}"

// Parse ENV.groovy
def configSlurper = new ConfigSlurper(targetEnv)
def config = configSlurper.parse(new File(envConfigFile).toURI().toURL())

// Split deployer host and port
def (deployerHost, deployerPort) = deployerHostPort.split(':')

// Generate XML
def writer = new StringWriter()
def xml = new MarkupBuilder(writer)

xml.mkp.xmlDeclaration(version: '1.0', encoding: 'UTF-8')
xml.DeployerSpec(
    'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
    'xsi:noNamespaceSchemaLocation': 'deployer.xsd',
    'name': projectName,
    'description': "Automated deployment for ${projectName} to ${targetEnv}",
    'version': '1.0'
) {
    
    // Deployer Server Configuration
    DeployerServer(
        host: deployerHost,
        port: deployerPort,
        user: deployerUser,
        pwd: deployerPassword
    )
    
    // Source Repository
    Repository(
        name: repoName,
        type: 'FlatFile',
        location: repoPath,
        description: "File-based repository for ${projectName}"
    )
    
    // Target Servers
    if (config.IntegrationServers) {
        config.IntegrationServers.each { serverName, serverConfig ->
            TargetServer(
                name: serverName,
                type: 'IS',
                host: serverConfig.host,
                port: serverConfig.port,
                user: serverConfig.username,
                pwd: serverConfig.pwd,
                useSSL: serverConfig.useSSL ?: false,
                description: serverConfig.description ?: serverName
            )
        }
    }
    
    // Deployment Set
    DeploymentSet(name: "${projectName}_${targetEnv}") {
        
        // Deployment Candidates - scan repository for packages
        def repoDir = new File(repoPath)
        if (repoDir.exists() && repoDir.isDirectory()) {
            repoDir.eachDir { packageDir ->
                DeploymentCandidate(
                    name: packageDir.name,
                    type: 'IS_PACKAGE',
                    repository: repoName,
                    srcAlias: packageDir.name
                ) {
                    // Map to all target servers
                    if (config.IntegrationServers) {
                        config.IntegrationServers.each { serverName, serverConfig ->
                            DeploymentMap(
                                targetServer: serverName,
                                targetAlias: packageDir.name
                            )
                        }
                    }
                }
            }
        }
    }
}

// Write to file
new File(outputFile).text = writer.toString()

println "=== Project Automator XML Generated Successfully ==="
println "Output file: ${outputFile}"
println "Deployment candidates: ${new File(repoPath).listFiles()?.findAll { it.isDirectory() }?.size() ?: 0}"
println "Target servers: ${config.IntegrationServers?.size() ?: 0}"

// Made with Bob
