FROM adoptopenjdk/openjdk11
EXPOSE 9999
ADD target/cs-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "/app.jar"]