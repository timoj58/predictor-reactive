FROM openjdk:11-jdk
RUN groupadd  -r predictor && useradd -r predictor-user -g predictor
COPY ./src/main/resources/config/paddypower-odds.yml /tmp/
COPY ./src/main/resources/config/betway-odds.yml /tmp/

USER predictor-user:predictor
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
