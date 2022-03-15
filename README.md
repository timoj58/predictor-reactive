# predictor-reactive

## predictor-scraper-reactive ![Coverage](.github/badges/scraper-coverage.svg) ![Branches](.github/badges/scraper-branches.svg)

## predictor-event-scraper-reactive

![Coverage](.github/badges/event-scraper-coverage.svg)
![Branches](.github/badges/event-scraper-branches.svg)

## predictor-data-reactive

![Coverage](.github/badges/data-coverage.svg)
![Branches](.github/badges/data-branches.svg)

## predictor-event-data-reactive

![Coverage](.github/badges/event-data-coverage.svg)
![Branches](.github/badges/event-data-branches.svg)

## predictor-teams-reactive

![Coverage](.github/badges/teams-coverage.svg)
![Branches](.github/badges/teams-branches.svg)

## predictor-players-reactive

![Coverage](.github/badges/players-coverage.svg)
![Branches](.github/badges/players-branches.svg)

## predictor-events-reactive

![Coverage](.github/badges/events-coverage.svg)
![Branches](.github/badges/events-branches.svg)

## predictor-players-events-reactive

![Coverage](.github/badges/players-events-coverage.svg)
![Branches](.github/badges/players-events-branches.svg)

## predictor-message-reactive

![Coverage](.github/badges/message-coverage.svg)
![Branches](.github/badges/message-branches.svg)

## predictor-client-reactive

![Coverage](.github/badges/client-coverage.svg)
![Branches](.github/badges/client-branches.svg)


Microservice architecture to predict likelihood of outcomes for football matches across europe's top leagues.  Currently predicts match results (as %) and match goals (as avg), player likelihood of goals, cards and assists.


## running locally

To run locally simply start the root compose script, wait for services to start and then execute curl command

```
docker-compose up -d
curl -X POST "http://localhost:8100/message" -H "accept: */*" -H "Content-Type: application/json" -d "{\"event\":\"START\",\"eventType\":\"ALL\"}"
```

## machine learning instances

Currently these are not part of the local setup, and linked to s3.  Will be added to local setup at some point

### building machine learning apps

Currently they are deployed as a flask app into a maching learning tuned ec2 instance.  At some point look to dockerize these

```
python3 predictor-ml-model/setup.py sdist bdist_wheel
then install the pkg on relevant server .. ie predictor_pkg-0.0.1-py3-none-any.whl 
```
