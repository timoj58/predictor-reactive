
python3 predictor-ml-model/setup.py sdist bdist_wheel
python3 predictor-ml-player/setup.py sdist bdist_wheel
mvn -f predictor-scraper-reactive/pom.xml clean package && docker build -t timmytime/predictor-scraper-reactive predictor-scraper-reactive/
mvn -f predictor-event-scraper-reactive/pom.xml clean package && docker build -t timmytime/predictor-event-scraper-reactive predictor-event-scraper-reactive/
mvn -f predictor-data-reactive/pom.xml clean package && docker build -t timmytime/predictor-data-reactive predictor-data-reactive/ 
mvn -f predictor-event-data-reactive/pom.xml clean package && docker build -t timmytime/predictor-event-data-reactive predictor-event-data-reactive/ 
mvn -f predictor-teams-reactive/pom.xml clean package && docker build -t timmytime/predictor-teams-reactive predictor-teams-reactive/ 
mvn -f predictor-events-reactive/pom.xml clean package && docker build -t timmytime/predictor-events-reactive predictor-events-reactive/
mvn -f predictor-players-reactive/pom.xml clean package && docker build -t timmytime/predictor-players-reactive predictor-players-reactive/
mvn -f predictor-players-events-reactive/pom.xml clean package && docker build -t timmytime/predictor-players-events-reactive predictor-players-events-reactive/
mvn -f predictor-client-reactive/pom.xml clean package && docker build -t timmytime/predictor-client-reactive predictor-client-reactive/
docker push timmytime/predictor-scraper-reactive
docker push timmytime/predictor-event-scraper-reactive
docker push timmytime/predictor-data-reactive
docker push timmytime/predictor-event-data-reactive
docker push timmytime/predictor-teams-reactive
docker push timmytime/predictor-events-reactive
docker push timmytime/predictor-players-reactive
docker push timmytime/predictor-players-events-reactive
docker push timmytime/predictor-client-reactive




