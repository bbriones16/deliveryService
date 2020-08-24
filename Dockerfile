FROM openjdk:8
ADD target/delivery-service.jar delivery-service.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "delivery-service.jar"]