FROM openjdk:17-jdk
RUN groupadd  -r predictor && useradd -r predictor-user -g predictor
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
USER predictor-user:predictor

CMD ["./mvnw", "spring-boot:run"]

