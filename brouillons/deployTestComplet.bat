cd brouillons

@REM deploy test complet
javac -cp "C:\servers\wildfly-27.0.1.Final\bin\client\jboss-client.jar;L:\cours\prog\S5\banque-archithecture\java\compte-courant-ejb\target\compte-courant-ejb-1.0.0.jar" TestComplet.java
java -cp ".;C:\servers\wildfly-27.0.1.Final\bin\client\*;L:\cours\prog\S5\banque-archithecture\java\compte-courant-ejb\target\compte-courant-ejb-1.0.0.jar" TestComplet

@REM
javac -cp "C:\servers\wildfly-27.0.1.Final\bin\client\jboss-client.jar;L:\cours\prog\S5\banque-archithecture\java\compte-courant-ejb\target\compte-courant-ejb-1.0.0.jar" TestCompletObjets.java
java -cp ".;C:\servers\wildfly-27.0.1.Final\bin\client\*;L:\cours\prog\S5\banque-archithecture\java\compte-courant-ejb\target\compte-courant-ejb-1.0.0.jar" TestCompletObjets


@REM depoly test banquier complet
javac -cp "C:\servers\wildfly-27.0.1.Final\bin\client\jboss-client.jar;L:\cours\prog\S5\banque-archithecture\java\compte-courant-ejb\target\compte-courant-ejb-1.0.0.jar;L:\cours\prog\S5\banque-archithecture\java\banquier-ejb\target\banquier-ejb-1.0.0.jar" TestBanquierComplet.java
java -cp ".;C:\servers\wildfly-27.0.1.Final\bin\client\*;L:\cours\prog\S5\banque-archithecture\java\compte-courant-ejb\target\compte-courant-ejb-1.0.0.jar;L:\cours\prog\S5\banque-archithecture\java\banquier-ejb\target\banquier-ejb-1.0.0.jar" TestBanquierComplet

@REM deploy test session ejb
javac -cp "C:\servers\wildfly-27.0.1.Final\bin\client\jboss-client.jar;L:\cours\prog\S5\banque-archithecture\java\banquier-ejb\target\banquier-ejb-1.0.0.jar" TestBanquierStateful.java
java -cp ".;C:\servers\wildfly-27.0.1.Final\bin\client\*;L:\cours\prog\S5\banque-archithecture\java\banquier-ejb\target\banquier-ejb-1.0.0.jar" TestBanquierStateful

@REM cleanup
javac -cp "C:\servers\wildfly-27.0.1.Final\bin\client\jboss-client.jar;L:\cours\prog\S5\banque-archithecture\java\banquier-ejb\target\banquier-ejb-1.0.0.jar" VoirEjb.java
java -cp ".;C:\servers\wildfly-27.0.1.Final\bin\client\*;L:\cours\prog\S5\banque-archithecture\java\banquier-ejb\target\banquier-ejb-1.0.0.jar" VoirEjb

@REM test de change
javac -cp "C:\servers\wildfly-27.0.1.Final\bin\client\jboss-client.jar;L:\cours\prog\S5\banque-archithecture\java\change-ejb\target\change-ejb-1.0.0.jar;L:\cours\prog\S5\banque-archithecture\java\compte-courant-ejb\target\compte-courant-ejb-1.0.0.jar;L:\cours\prog\S5\banque-archithecture\java\banquier-ejb\target\banquier-ejb-1.0.0.jar" TestCompletChange.java
java -cp ".;C:\servers\wildfly-27.0.1.Final\bin\client\*;L:\cours\prog\S5\banque-archithecture\java\change-ejb\target\change-ejb-1.0.0.jar;L:\cours\prog\S5\banque-archithecture\java\compte-courant-ejb\target\compte-courant-ejb-1.0.0.jar;L:\cours\prog\S5\banque-archithecture\java\banquier-ejb\target\banquier-ejb-1.0.0.jar" TestCompletChange

@REM Test docker
javac -cp "C:\servers\wildfly-27.0.1.Final\bin\client\jboss-client.jar;L:\cours\prog\S5\banque-archithecture\java\change-ejb\target\change-ejb-1.0.0.jar" TestDocker.java
java -cp ".;C:\servers\wildfly-27.0.1.Final\bin\client\*;L:\cours\prog\S5\banque-archithecture\java\change-ejb\target\change-ejb-1.0.0.jar" TestDocker

