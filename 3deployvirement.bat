cd L:\cours\prog\S5\banque-archithecture\java\virement-ejb
mvn clean compile package

del C:\servers\wildfly-27.0.1.Final\standalone\deployments\virement-ejb-1.0.0.jar
cd C:\servers\wildfly-27.0.1.Final\bin
standalone.bat

copy L:\cours\prog\S5\banque-archithecture\java\virement-ejb\target\virement-ejb-1.0.0.jar C:\servers\wildfly-27.0.1.Final\standalone\deployments\
cd C:\servers\wildfly-27.0.1.Final\bin
jboss-cli.bat --connect --command="deploy --force C:\servers\wildfly-27.0.1.Final\standalone\deployments\virement-ejb-1.0.0.jar"