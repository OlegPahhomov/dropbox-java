# dropbox-java

####### Goal
Creating a simple file upload and viewing platform using different languages and frameworks

####### Java Spark Html project

To run:
* 1) You need Postgres database (or reconfig more in point 2)
* 2) change Appconfig.java and pom.xml database password (default postgres config)
* 3) run mvn flyway:migrate
* 4) start main in Application.java

spark runs on http://localhost:4567/



####### In more detail

Backend: java8 - sparkjava - flyway - postgres - apache db utils

Frontend: html - js/jQuery - css - microtemplate.js - fancybox - jquery validation


Known issues.

* Project can be run as 2 separate projects, but it has issues:
It will always redirect to default serverside url, which can be changed in serverConfig.js
Upload form has server_link hardcoded
* Front-side validation doesn't work: it submits always.
