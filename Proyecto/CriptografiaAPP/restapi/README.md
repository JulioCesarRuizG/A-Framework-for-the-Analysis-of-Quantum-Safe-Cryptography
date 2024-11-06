# jersey-sample serverless API
The jersey-sample project, created with [`aws-serverless-java-container`](https://github.com/awslabs/aws-serverless-java-container).

The starter project defines a simple `/ping` resource that can accept `GET` requests with its tests.

The project folder also includes a `sam.yaml` file. You can use this [SAM](https://github.com/awslabs/serverless-application-model) file to deploy the project to AWS Lambda and Amazon API Gateway or test in local with [SAM Local](https://github.com/awslabs/aws-sam-local). 

Using [Maven](https://maven.apache.org/), you can create an AWS Lambda-compatible jar file simply by running the maven package command from the projct folder.

```bash
$ mvn archetype:generate -DartifactId=my-jersey-api -DarchetypeGroupId=com.amazonaws.serverless.archetypes -DarchetypeArtifactId=aws-serverless-jersey-archetype -DarchetypeVersion=1.0-SNAPSHOT -DgroupId=com.sapessi.jersey -Dversion=0.1 -Dinteractive=false
$ cd my-jersey-api
$ mvn clean package

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 6.546 s
[INFO] Finished at: 2018-02-15T08:39:33-08:00
[INFO] Final Memory: XXM/XXXM
[INFO] ------------------------------------------------------------------------
```

You can use [AWS SAM Local](https://github.com/awslabs/aws-sam-local) to start your project.

First, install SAM local:

```bash
$ npm install -g aws-sam-local
```

Next, from the project root folder - where the `sam.yaml` file is located - start the API with the SAM Local CLI.

```bash
$ sam local start-api --template sam.yaml

...
Mounting my.service.StreamLambdaHandler::handleRequest (java8) at http://127.0.0.1:3000/{proxy+} [OPTIONS GET HEAD POST PUT DELETE PATCH]
...
```

Using a new shell, you can send a test ping request to your API:

```bash
$ curl -s http://127.0.0.1:3000/ping | python -m json.tool

{
    "pong": "Hello, World!"
}
``` 

You can use the [AWS CLI](https://aws.amazon.com/cli/) to quickly deploy your application to AWS Lambda and Amazon API Gateway with your SAM template.

You will need an S3 bucket to store the artifacts for deployment. Once you have created the S3 bucket, run the following command from the project's root folder - where the `sam.yaml` file is located:

```
$ aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket <YOUR S3 BUCKET NAME>
Uploading to xxxxxxxxxxxxxxxxxxxxxxxxxx  6464692 / 6464692.0  (100.00%)
Successfully packaged artifacts and wrote output template to file output-sam.yaml.
Execute the following command to deploy the packaged template
aws cloudformation deploy --template-file /your/path/output-sam.yaml --stack-name <YOUR STACK NAME>
```

As the command output suggests, you can now use the cli to deploy the application. Choose a stack name and run the `aws cloudformation deploy` command from the output of the package command.
 
```
$ aws cloudformation deploy --template-file output-sam.yaml --stack-name ServerlessJerseyApi --capabilities CAPABILITY_IAM
```

Once the application is deployed, you can describe the stack to show the API endpoint that was created. The endpoint should be the `ServerlessJerseyApi` key of the `Outputs` property:

```
$ aws cloudformation describe-stacks --stack-name ServerlessJerseyApi
{
    "Stacks": [
        {
            "StackId": "arn:aws:cloudformation:us-west-2:xxxxxxxx:stack/ServerlessJerseyApi/xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx", 
            "Description": "AWS Serverless Jersey API - my.service::jersey-sample", 
            "Tags": [], 
            "Outputs": [
                {
                    "Description": "URL for application",
                    "ExportName": "JerseySampleApi",  
                    "OutputKey": "JerseySampleApi",
                    "OutputValue": "https://xxxxxxx.execute-api.us-west-2.amazonaws.com/Prod/ping"
                }
            ], 
            "CreationTime": "2016-12-13T22:59:31.552Z", 
            "Capabilities": [
                "CAPABILITY_IAM"
            ], 
            "StackName": "ServerlessJerseyApi", 
            "NotificationARNs": [], 
            "StackStatus": "UPDATE_COMPLETE"
        }
    ]
}

```

Copy the `OutputValue` into a browser or use curl to test your first request:

```bash
$ curl -s https://xxxxxxx.execute-api.us-west-2.amazonaws.com/Prod/ping | python -m json.tool

{
    "pong": "Hello, World!"
}
```

# Samuel notes

IMPORTANTE (si no se hace todo esto, no sirve, no omitir ninguno) para cloud: 
*CAMBIAR FLAG EN Util.java que dice cloud
*Cambiar en Util.java el nombre del secreto de la base de datos
*poner el slash al final de la ruta del api en el frontend
*agrandar el timeout de la lambda function, in lambda, applications, itaustackpoc, JerseySampleFunction, general configuration, edit, timeout, do this to see other timeouts! otherwise they wont appear in log
*change security group of db, go to the db, click modify, delete default and choose your security group, do this, or you'll get timeouts. Create in a VPC a new security group with rules of all traffic.

para local:
*en tomcat, borrar la dependencia aws  serverless core

To run, open docker, en run test "CvApiTest", set a break point at the end of setUp  if you wanna keep it open to play with the frontend
the installation on premise require the cvpath property  to point to cv installation
para la url de la db on premise:
JDBC URL: jdbc:h2:file:./db/db

 for this tutorial:
https://aws.amazon.com/blogs/opensource/java-apis-aws-lambda/

run maven with the archtype in linux subsystem. the you can switch to windows to run other commands

 run this after doing 
 
not maven anymore: mvn clean package -Dmaven.test.skip=true
gradle clean jar war
gradle build -x test

create bucket in s3 called s3poc in virginia zone using default settings, now run

(for itau demo was created as s3poc1, make sure it has the full path with all packages in Handler: co.cyte.web.cvapi.handler.StreamLambdaHandler::handleRequest in sam.yaml  )
(in StreamLambdaHandler.java i als give the full path in .packages("co.cyte.web.cvapi"))
aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket s3poc1

Now run (you copy and paste this command from previous output, but add  --capabilities CAPABILITY_IAM )


the stack name was itaustackpoc
aws cloudformation deploy --template-file C:\Users\ssabo\Documents\CryptoVaultUltra\restapi\output-sam.yaml --stack-name itaustackpoc --capabilities CAPABILITY_IAM
aws cloudformation deploy --template-file C:\Users\ssabo\Documents\CryptoVaultUltra\restapi\output-sam.yaml --stack-name ServerlessJerseyApi --capabilities CAPABILITY_IAM


Now run (the query flag is removed from tutorial):

aws cloudformation describe-stacks --stack-name itaustackpoc 
aws cloudformation describe-stacks --stack-name ServerlessJerseyApi 

Note that the web.xml won´t be used, in aws it is all about  th co.cyte.web.cvapi.handler.StreamLambdaHandler::handleRequest
that's why you dont have to put mor things on the path of the url (e.g. '/webapi/')
now to access the the appi, put exactly this path:
curl -s https://iyl52yevo4.execute-api.us-east-1.amazonaws.com/Prod/cvapi/currentp


Now in the web interface, go to crypto vault init and start it


Copy the outputValue to run this command:

curl -s https://iyl52yevo4.execute-api.us-east-1.amazonaws.com/Prod/cvapi/currentpath
curl -s https://l5n2txhkr5.execute-api.us-east-1.amazonaws.com/Prod/cvapi/ 

to access a specific method, do 
https://l5n2txhkr5.execute-api.us-east-1.amazonaws.com/Prod/ping/currentpath 


Now to connect to crypto vault, add the cv jar. you can do: (i downdloaded it from realeas in cv4 github)

mvn install:install-file -Dfile="C:\Users\ssabo\Downloads\CryptoVaultAPI (2).jar" -DgroupId=cvapi -DartifactId=cvapi -Dversion=4.0.0.13 -Dpackaging=jar


mvn install:install-file -Dfile="C:\Users\ssabo\Downloads\CryptoVaultAPIinit.jar" -DgroupId=cvapi -DartifactId=CV4APIinit -Dversion=4.0.9 -Dpackaging=jar

and add to the pom:

<dependency>
   <groupId>cvapi</groupId>
   <artifactId>CV4API</artifactId>
   <version>4.0.9</version>
</dependency>


Se debe ver el yml como crea la base de datos mysql en el stack. Se debe crear installPass en other type of secret manualmente.

Delete stack:

aws cloudformation delete-stack --stack-name ServerlessJerseyApi


Configure CV cli to connect to aws mysql
All the info is in the secret in secrets manager

JDBC URL (leave empty to use default values):
jdbc:mysql://sct9mzhtkmu2bk.cmzpxxgyj1ob.us-east-1.rds.amazonaws.com:3306/CV
User:
superuser2
password:
A'Dh0Ex!$*5S7{%Q

To execute CV CL�I with mysql driver, you have to put mysql connector in libs, and force class path like this:
java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -resetdb

IMPORTANT: I chose my ip in inbound and outbound rules in security group, so no one else could access!

**
Deploy front in s3
https://www.youtube.com/watch?v=O06eyJ3vMyc 
in summary, just create the bucket but uncheck block public access
then upload files
go to properties and enable static web site, you will se the url
now go to objects , go to actions and make public all objects
put this policy pin permissions https://docs.aws.amazon.com/AmazonS3/latest/userguide/WebsiteAccessPermissionsReqd.html
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": [
                "s3:GetObject"
            ],
            "Resource": [
                "arn:aws:s3:::cvfront/*"
            ]
        }
    ]
}




http://cytecvfront.s3-website-us-east-1.amazonaws.com/home


**
Para entender cors y el resource config
https://stackoverflow.com/questions/45625925/what-exactly-is-the-resourceconfig-class-in-jersey-2

ver en secrets el password de instalacion y el access token

**
publish angular on bucket 


Para añadir dependencias locales toca con "implementation file('<ruta_del_archivo>')"
Sin las comillas dobles, acá hay un ejemplo
implementation files('fileLibs/CryptoVaultAPI.jar')

*************
H2 database

database url
jdbc:h2:~/Documents/CryptoVaultUltra/restapi/testresources/cv_04.00.13/db

credentialls (user passwors)
sa
changeit



*************
keytool notes 


first add CA cyte
keytool -importcert -keystore sam.p12 -trustcacerts -alias cacyte -keypass changeit -storepass changeit -file CA_CyTe.cer -storetype PKCS12

add certificate to p12 with keytool
keytool -importcert -keystore sam.p12 -trustcacerts -alias cvkeypairalias -keypass changeit -storepass changeit -file samcsrsigned.cer -storetype PKCS12

listar contenido:
keytool -list -v -keystore sam.p12 -storepass changeit -storetype PKCS12

borrar
keytool -delete -alias sampub -keystore sam.p12 -storepass changeit

import CA
keytool -import -alias cacyte -file CA_CyTe.cer -storetype JKS -keystore server.truststore



*************
CV NOTES

C:\Users\ssabo\Documents\CryptoVaultUltra\restapi\testresources\cv_04.00.13 aca sirve todo localmente bien!! con CryptoVault.cmd desde windows



la version de la cli debbe ser la misma que la de api cloud

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -init

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -ilic licencia_CyTe_Dev.env

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -gk samks

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -gr samks

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -ica CA_CyTe.cer

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -usarCRLVencidas

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -importarCRL CA_CyTe.crl

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -gk samks

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -exportarpropiox509 samks ./samp12

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -exportarpropiopgp samks sampgp.asc       //sampgp es de alias

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -importarllaveropublicopgp sampgpdestino sampgp.asc

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -generarCSR samks samcsr.csr

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -cifrarPGP logger.xml loggercifradoPGP.txt sampgpdestino -ns

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -descifrarpgp loggercifradoPGP.txt loggerDESPGP.xml sampgp sampgpdestino

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -cifrarPGP logger.xml loggercifradoPGPSIG.txt sampgpdestino samks

NOT WORKING java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -descifrarpgp loggercifradoPGPSIG.txt loggerDESPGP.xml samks sampgpdestino

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -importarCA cacyte CA_CyTe.cer

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -usarCRLVencidas

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -importarCRL CA_CyTe.crl

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -importarcertificadopropio samks samcsr.csr.cer

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI  -agregarLlavePublica damian damian2.cer

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -agregarLlavePublica sampubdestino sam.cer

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -eliminarLlavePublica sampubdestino sam.cer

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -cifrar logger.xml loggercifradoCV.txt sampubdestino samks

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -cifrar logger.xml loggercifradoCV.txt damian samks

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -descifrar loggercifradoCV.txt loggerDEScifradoCV.txt samks samuel


java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -dv miexcelCV.txt miexcelDD.txt samks samuel

******* 

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -c miexcel.xlsx miexcelCV.txt samuel samks

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -csmime perro.jpg perrosmime.txt samuel samks

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -ccms perro.jpg perrocms.txt samuel samks

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -dv perro2.txt perrodesci.jpg samks samuel

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -dv planocv.txt planodesci.jpg samks samuel

**PGP
java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -generarllaveropgp samprivpgp
java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -exportarpropiopgp samprivpgp sampgppub2.asc 
java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -importarllaveropublicopgp sampgpdestinopub sampgppub2.asc

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -cifrarPGP logger.xml loggercifradoPGPSIG.txt sampgpdestinopub samprivpgp
java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -descifrarpgp loggercifradoPGPSIG.txt loggerDESPGP.xml samprivpgp sampgpdestinopub

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -cifrarPGP logger.xml loggercifradoPGP.txt sampgpdestinopub -ns

java -cp "*;lib/*" co.cyte.cryptovault.cli.CryptoVaultCLI -dpgp loggercifradoPGP.txt loggerDESPGP.xml samprivpgp 


******
GENERAL NOTES

When updating cv version, update the copied folder in the testcontainers, and the cv API jar in lib.

# Fido
aca solo hay .net
https://developers.yubico.com/


see this for some generic explenation
https://fidoalliance.org/developers/ 

For a demo of WebAuthn, visit () there are some examples) https://webauthn.io/.
https://fidoalliance.org/fido2/fido2-web-authentication-webauthn/

see the slides here! https://slides.com/fidoalliance/jan-2018-fido-seminar-webauthn-tutorial#/1

project
https://github.com/fido-alliance/webauthn-demo

the piunk for the token is 6647


For JAVA

clone this repo (needs to be cloned bc of git id?)
https://github.com/Yubico/java-webauthn-server

go to demo folder, and run it with 
..\gradlew run  
be ware of the backslah on windows lol


user handler: https://developers.yubico.com/WebAuthn/WebAuthn_Developer_Guide/User_Handle.html


Nota Julio César:
Se cambió la instalación de CryptoVault y el Api a la versión en la carpeta CryptoVault4.2.3
usuario: sa
contraseñas de instalación y llaves: changeit

Se debe agregar la siguiente línea en el archivo JAVA_HOME/jre/lib/security/java.security
security.provider.#=org.bouncycastle.jce.provider.BouncyCastleProvider
Dónde # es el siguiente número en la lista de providers

En caso del error:
501: The JCE provider BC was not found. [Version: 04.02.03]

Para JAVA 8:
Se deben agregar los jars de BouncyCastle a JAVA_HOME/jre/lib/ext
https://mvnrepository.com/artifact/org.bouncycastle/bcutil-jdk18on/1.71
https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on/1.71

Para JAVA 11:
Se deben agregar los jars de BouncyCastle a TOMCAT_HOME/lib
https://mvnrepository.com/artifact/org.bouncycastle/bcutil-jdk18on/1.71
https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on/1.71

Reference: https://bugs.openjdk.org/browse/JDK-8248782

Verificar que aparezca la librería BC en el botón version en index.jsp

