// -------------------------------------------------------------------------------- //
// - START: CONFIGURATION FILE LOADING -------------------------------------------- //
// -------------------------------------------------------------------------------- //
// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

def ENV_NAME = "${appName}.config.location"
if(!grails.config.locations || !(grails.config.locations instanceof List)) {
    grails.config.locations = []
}

println "--------------------------------------------------------------------------------"
println "- Loading configuration file                                                   -"
println "--------------------------------------------------------------------------------"

// 1: check for environment variable that has been set! This variable must point to the
// configuration file that must be used. Can be a .groovy or .properties file!
if(System.getenv(ENV_NAME) && new File(System.getenv(ENV_NAME)).exists()) {
    println("Including System Environment configuration file: " + System.getenv(ENV_NAME))
    grails.config.locations << "file:" + System.getenv(ENV_NAME)

// 2: check on local project config file in ${userHome}/.grails/${appName}...
} else if (new File("${userHome}/.grails/${appName}/${appName}-config.groovy").exists()) {
    println "*** User defined config: file:${userHome}/.grails/${appName}/${appName}-config.groovy ***"
    grails.config.locations = ["file:${userHome}/.grails/${appName}/${appName}-config.groovy"]
} else if (new File("${userHome}/.grails/${appName}/${appName}-config.properties").exists()) {
    println "*** User defined config: file:${userHome}/.grails/${appName}/${appName}-config.properties ***"
    grails.config.locations = ["file:${userHome}/.grails/${appName}/${appName}-config.properties"]

// 3: check on local project config file in the project root directory
} else if (new File("./${appName}-config.groovy").exists()) {
    println "*** User defined config: file:./${appName}-config.groovy ***"
    grails.config.locations = ["file:./${appName}-config.groovy"]
} else if (new File("./${appName}-config.properties").exists()) {
    println "*** User defined config: file:./${appName}-config.properties ***"
    grails.config.locations = ["file:./${appName}-config.groovy"]

// 4: No configuration file defined. We have a problem!!
} else {
    println "********************************************************************************"
    println "* No external configuration file defined                                       *"
    println "********************************************************************************"
}
println "(*) grails.config.locations = ${grails.config.locations}"
println "--------------------------------------------------------------------------------"
// -------------------------------------------------------------------------------- //
// - END: CONFIGURATION FILE LOADING ---------------------------------------------- //
// -------------------------------------------------------------------------------- //

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
//grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
                      all          : '*/*', // 'all' maps to '*' or the first available format in withFormat
                      atom         : 'application/atom+xml',
                      css          : 'text/css',
                      csv          : 'text/csv',
                      form         : 'application/x-www-form-urlencoded',
                      html         : ['text/html', 'application/xhtml+xml'],
                      js           : 'text/javascript',
                      json         : ['application/json', 'text/json'],
                      multipartForm: 'multipart/form-data',
                      rss          : 'application/rss+xml',
                      text         : 'text/plain',
                      hal          : ['application/hal+json', 'application/hal+xml'],
                      xml          : ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
grails.resources.adhoc.includes = ['/images/**', '/css/**', '/js/**', '/plugins/**']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = true
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    root {
        debug()
    }
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    }
    debug   'org.codehaus.groovy.grails.web.servlet',        // controllers
            'org.codehaus.groovy.grails.web.pages',          // GSP
            'org.codehaus.groovy.grails.web.sitemesh',       // layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping',        // URL mapping
            'org.codehaus.groovy.grails.commons',            // core / classloading
            'org.codehaus.groovy.grails.plugins',            // plugins
            'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate',
            'grails-app',
            'com.muzima'

    environments {
        production {
            error   'grails-app',
                    'com.muzima'
        }
    }
}

//cors config.
cors.enabled=true
cors.url.pattern = '/api/*'
cors.headers=[
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Credentials': true,
    'Access-Control-Allow-Headers': 'origin, authorization, accept, content-type, x-requested-with',
    'Access-Control-Allow-Methods': 'GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS',
    'Access-Control-Max-Age': 8000
    ]

grails.mime.use.accept.header = true
grails.mime.disable.accept.header.userAgents = []
grails.plugin.springsecurity.filterChain.chainMap = [
        '/api/message': 'anonymousAuthenticationFilter,restTokenValidationFilter,restExceptionTranslationFilter,filterInvocationInterceptor', // Stateless with anonymous allowed
        '/api/**': 'JOINED_FILTERS,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter',  // Stateless chain
        '/**': 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'                                          // Traditional chain
]
grails.plugin.springsecurity.rest.token.validation.enableAnonymousAccess = true

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.muzima.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.muzima.UserRole'
grails.plugin.springsecurity.authority.className = 'com.muzima.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	'/':                              ['permitAll'],
	'/index':                         ['permitAll'],
	'/index.gsp':                     ['permitAll'],
	'/**/js/**':                      ['permitAll'],
	'/**/css/**':                     ['permitAll'],
	'/**/images/**':                  ['permitAll'],
	'/**/favicon.ico':                ['permitAll']
]

grails.plugin.springsecurity.rememberMe.persistent = false
grails.plugin.springsecurity.rest.login.useJsonCredentials = true
grails.plugin.springsecurity.rest.login.failureStatusCode = 401
grails.plugin.springsecurity.rest.token.storage.useGorm = true
grails.plugin.springsecurity.rest.token.storage.gorm.tokenDomainClassName = 'com.muzima.AuthenticationToken'
grails.plugin.springsecurity.rest.token.storage.gorm.tokenValuePropertyName = 'token'
grails.plugin.springsecurity.rest.token.storage.gorm.usernamePropertyName = 'username'