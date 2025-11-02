POWERSHELL:
cd L:\cours\prog\S5\banque-archithecture\java\compte-courant-ejb
mvn clean compile package

CMD:
del C:\servers\wildfly-27.0.1.Final\standalone\deployments\compte-courant-ejb-1.0.0.jar
    cd C:\servers\wildfly-27.0.1.Final\bin
    standalone.bat

CMD:
cd C:\servers\wildfly-27.0.1.Final\bin
copy L:\cours\prog\S5\banque-archithecture\java\compte-courant-ejb\target\compte-courant-ejb-1.0.0.jar C:\servers\wildfly-27.0.1.Final\standalone\deployments\
jboss-cli.bat --connect --command="deploy --force C:\servers\wildfly-27.0.1.Final\standalone\deployments\compte-courant-ejb-1.0.0.jar"
