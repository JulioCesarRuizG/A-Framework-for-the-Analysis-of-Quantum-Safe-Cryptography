# Proceso de despliegue

## Despliegue del frontend

- Ejecutar el comando "npm install" para instalar las dependencias
- Ejecutar el comando "npm run start" y navegar a la url "http://localhost:4200/#/home"

## Despliegue del war

- Ejecutar el comando "gradle clean war" en la carpeta "restapi"
- Copiar el archivo .war generado dentro de la carpeta "restapi/build/libs" a la carpeta "webapps" dentro de una instalación de tomcat 10.
- Ejecutar el comando "./catalina.sh run" o "./catalina.bat run" (dependiendo del sistema operativo en el que se ejecute) dentro de la carpeta "bin" dentro de tomcat.