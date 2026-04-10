/**
 * Environment Configuration for webMethods CI/CD Pipeline
 * 
 * This file defines all environment-specific configurations including:
 * - Integration Servers
 * - Universal Messaging
 * - JDBC Adapters
 * - AS400 Adapters
 * 
 * Environments: DEV, TEST (3 servers), PROD (3 servers)
 */

environments {
    DEV {
        IntegrationServers {
            IS_DEV {
                host = 'dev-server.company.com'
                port = '5555'
                username = 'Administrator'
                pwd = 'manage'
                useSSL = false
                description = 'Development Integration Server'
            }
        }
        
        UniversalMessaging {
            UM_DEV {
                host = 'dev-server.company.com'
                port = '9000'
                description = 'Development UM Server'
                realmURL = 'nsp://dev-server.company.com:9000'
            }
        }
        
        JDBCAdapters {
            JDBC_DEV {
                connectionAlias = 'DevDB'
                driverClass = 'oracle.jdbc.driver.OracleDriver'
                url = 'jdbc:oracle:thin:@dev-db.company.com:1521:DEVDB'
                username = 'dev_user'
                password = 'dev_pass'
                minPoolSize = '5'
                maxPoolSize = '20'
            }
        }
        
        AS400Adapters {
            AS400_DEV {
                connectionAlias = 'DevAS400'
                host = 'dev-as400.company.com'
                username = 'dev_as400'
                password = 'dev_pass'
                libraryList = 'DEVLIB'
            }
        }
    }
    
    TEST {
        IntegrationServers {
            IS_TEST1 {
                host = 'test1-server.company.com'
                port = '5555'
                username = 'Administrator'
                pwd = 'manage'
                useSSL = false
                description = 'Test Integration Server 1'
                clusterNode = 'test1'
            }
            IS_TEST2 {
                host = 'test2-server.company.com'
                port = '5555'
                username = 'Administrator'
                pwd = 'manage'
                useSSL = false
                description = 'Test Integration Server 2'
                clusterNode = 'test2'
            }
            IS_TEST3 {
                host = 'test3-server.company.com'
                port = '5555'
                username = 'Administrator'
                pwd = 'manage'
                useSSL = false
                description = 'Test Integration Server 3'
                clusterNode = 'test3'
            }
        }
        
        UniversalMessaging {
            UM_TEST {
                host = 'test-um.company.com'
                port = '9000'
                description = 'Test UM Cluster'
                realmURL = 'nsp://test-um.company.com:9000'
                clusterNodes = 'test-um1.company.com:9000,test-um2.company.com:9000'
            }
        }
        
        JDBCAdapters {
            JDBC_TEST {
                connectionAlias = 'TestDB'
                driverClass = 'oracle.jdbc.driver.OracleDriver'
                url = 'jdbc:oracle:thin:@test-db.company.com:1521:TESTDB'
                username = 'test_user'
                password = 'test_pass'
                minPoolSize = '10'
                maxPoolSize = '50'
            }
        }
        
        AS400Adapters {
            AS400_TEST {
                connectionAlias = 'TestAS400'
                host = 'test-as400.company.com'
                username = 'test_as400'
                password = 'test_pass'
                libraryList = 'TESTLIB'
            }
        }
    }
    
    PROD {
        IntegrationServers {
            IS_PROD1 {
                host = 'prod1-server.company.com'
                port = '5555'
                username = 'Administrator'
                pwd = 'manage'
                useSSL = true
                description = 'Production Integration Server 1'
                clusterNode = 'prod1'
            }
            IS_PROD2 {
                host = 'prod2-server.company.com'
                port = '5555'
                username = 'Administrator'
                pwd = 'manage'
                useSSL = true
                description = 'Production Integration Server 2'
                clusterNode = 'prod2'
            }
            IS_PROD3 {
                host = 'prod3-server.company.com'
                port = '5555'
                username = 'Administrator'
                pwd = 'manage'
                useSSL = true
                description = 'Production Integration Server 3'
                clusterNode = 'prod3'
            }
        }
        
        UniversalMessaging {
            UM_PROD {
                host = 'prod-um.company.com'
                port = '9000'
                description = 'Production UM Cluster'
                realmURL = 'nsp://prod-um.company.com:9000'
                clusterNodes = 'prod-um1.company.com:9000,prod-um2.company.com:9000,prod-um3.company.com:9000'
            }
        }
        
        JDBCAdapters {
            JDBC_PROD {
                connectionAlias = 'ProdDB'
                driverClass = 'oracle.jdbc.driver.OracleDriver'
                url = 'jdbc:oracle:thin:@prod-db.company.com:1521:PRODDB'
                username = 'prod_user'
                password = 'prod_pass'
                minPoolSize = '20'
                maxPoolSize = '100'
            }
        }
        
        AS400Adapters {
            AS400_PROD {
                connectionAlias = 'ProdAS400'
                host = 'prod-as400.company.com'
                username = 'prod_as400'
                password = 'prod_pass'
                libraryList = 'PRODLIB'
            }
        }
    }
}

// Made with Bob
