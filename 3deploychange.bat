cd L:\cours\prog\S5\banque-archithecture\java\change-ejb
mvn clean compile package

del C:\servers\wildfly-27.0.1.Final\standalone\deployments\change-ejb-1.0.0.jar
cd C:\servers\wildfly-27.0.1.Final\bin
standalone.bat

copy L:\cours\prog\S5\banque-archithecture\java\change-ejb\target\change-ejb-1.0.0.jar C:\servers\wildfly-27.0.1.Final\standalone\deployments\
cd C:\servers\wildfly-27.0.1.Final\bin
jboss-cli.bat --connect --command="deploy --force C:\servers\wildfly-27.0.1.Final\standalone\deployments\change-ejb-1.0.0.jar"