# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS build

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

COPY . /nvminh162/jobhunter
WORKDIR /nvminh162/jobhunter

#skip task: test
RUN mvn clean package -DskipTests --no-transfer-progress

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
EXPOSE 8080
COPY --from=build /nvminh162/jobhunter/target/*.jar /nvminh162/spring-boot-job-hunter.jar
ENTRYPOINT ["java", "-jar", "/nvminh162/spring-boot-job-hunter.jar"]
