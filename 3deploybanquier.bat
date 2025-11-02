cd L:\cours\prog\S5\banque-archithecture\java\banquier-ejb
mvn clean compile package

del C:\servers\wildfly-27.0.1.Final\standalone\deployments\banquier-ejb-1.0.0.jar
cd C:\servers\wildfly-27.0.1.Final\bin
standalone.bat

cd C:\servers\wildfly-27.0.1.Final\bin
copy L:\cours\prog\S5\banque-archithecture\java\banquier-ejb\target\banquier-ejb-1.0.0.jar C:\servers\wildfly-27.0.1.Final\standalone\deployments\
jboss-cli.bat --connect --command="deploy --force C:\servers\wildfly-27.0.1.Final\standalone\deployments\banquier-ejb-1.0.0.jar"