FROM openjdk:11-jdk
RUN groupadd  -r predictor && useradd -r predictor-user -g predictor
COPY ./src/main/resources/config/lineup-scraper-rules.yml /tmp/
COPY ./src/main/resources/config/match-scraper-rules.yml /tmp/
COPY ./src/main/resources/config/results-scraper-rules.yml /tmp/
COPY ./src/main/resources/config/specialcases.yml /tmp/
COPY ./src/main/resources/config/team-scraper-rules.yml /tmp/
COPY ./src/main/resources/config/player-match-stats-rules.yml /tmp/

VOLUME /tmp

USER predictor-user:predictor
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
