# dropbox-java

####### Goal
Creating a simple file upload and viewing platform using different languages and frameworks

####### Java Spark Html project

To run:
*1) You need Postgres database (or reconfig more in point 2)
*2) change Appconfig.java and pom.xml database password (default postgres config)
*3) run mvn flyway:migrate
*4) start main in Application.java

spark runs on http://localhost:4567/


More detail

Backend: java8 - sparkjava - flyway - postgres
Frontend: html - js/jQuery - microtemplate.js - fancybox
