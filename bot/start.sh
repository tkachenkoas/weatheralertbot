mvn clean process-resources -Pliq-update,local
mvn clean verify -Plocal -DskipTests spring-boot:run -Dspring-boot.run.fork=false