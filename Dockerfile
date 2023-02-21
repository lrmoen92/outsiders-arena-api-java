FROM openjdk:8-jdk-alpine

COPY target/outsiders-arena-api-0.1.jar outsiders-arena-api-0.1.jar

ENTRYPOINT ["java","-jar","/outsiders-arena-api-0.1.jar","-Dspring.profiles.active=dev"]