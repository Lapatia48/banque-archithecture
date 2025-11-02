cd L:\cours\prog\S5\banque-archithecture\java\change-service
mvn clean compile package

del C:\servers\wildfly-27.0.1.Final\standalone\deployments\change-service.war
cd C:\servers\wildfly-27.0.1.Final\bin
standalone.bat

copy L:\cours\prog\S5\banque-archithecture\java\change-service\target\change-service.war C:\servers\wildfly-27.0.1.Final\standalone\deployments\
cd C:\servers\wildfly-27.0.1.Final\bin
jboss-cli.bat --connect --command="deploy --force C:\servers\wildfly-27.0.1.Final\standalone\deployments\change-service.war"