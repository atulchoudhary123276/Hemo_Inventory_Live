FROM maven:3.8.3-openjdk-11 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:11
COPY --from=build /target/Atul_BloodBankProject-0.0.1-SNAPSHOT.jar hemoInventory.jar
EXPOSE 8099
ENTRYPOINT ["java","-jar","hemoInventory.jar"]