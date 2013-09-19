newspaper-digitisation-process-monitor
======================================

This is the system that allows a DCM to monitor the digitisation and receival process of the newpaper microfilms


The system have a rather special design.

# The modules

The frontend module contains the user interface, and the web xml of the webservice.
The backend-service contains the rest service code, but is not a webservice. It is used as a library in the frontend. Sorry about that
The backend draws upon a number of datasources. These must follow the interface defined in datasource-interfaces.
The datasource-mockup is an example of a datasource
datasource-tck is the technology compability kit, used for testing a datasource. Look in the mockup datasource for how to use it



# How the config framework works
Spring 3 is used. The webservice is configured by Spring. There is a context param "contextConfigLocation", which must
point to an ApplicationContext.xml, the config file of spring and thus this system.

You must define a list with the id="dataSourcesList", containing the datasources to be used. The datasources must be
declared as spring beans. See process-monitor-frontend/src/test/resources/test-applicationContext.xml for an example

# How the integration tests work
A maven property "{integration.test.newspaper.properties" must be set to the path to the common properties file.
This property will be made available as a java system property during integration test runtime.
At the maven phase "generate-test-resources", the properties file will be read into maven as maven properties
Later at the same phase, the test resources will be maven filtered, having the maven properties replaced into them.
At the maven phase "pre-integration-test", the a jetty server will be started with the webapp. It will be configured so that the
 "contextConfigLocation" is set to "file://${project.build.testOutputDirectory}/test-applicationContext.xml"
The BackendTest from the frontend module will then run
At the maven phase "post-integration-test" the jetty server will be stopped again.

# How the deploy works
This system will be build by jenkins with the clean deploy targets. After the system have been built, jenkins will
invoke a shell script in the devel server which will get the deployed webapp, and deploy it in a tomcat server.

