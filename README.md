# predictor-reactive ![Coverage](.github/badges/coverage.svg) ![Branches](.github/badges/branches.svg)

Microservice architecture to predict likelihood of outcomes for football matches across europe's top leagues.  Currently predicts match results (as %) and match goals (as avg), player likelihood of goals, cards and assists.

## predictor-scraper-reactive ![Coverage](.github/badges/scraper-coverage.svg) ![Branches](.github/badges/scraper-branches.svg)

Microservice that scrapes espn for results data from the previous scraped end date.

## predictor-event-scraper-reactive ![Coverage](.github/badges/event-scraper-coverage.svg) ![Branches](.github/badges/event-scraper-branches.svg)

Microservice that scrapes espn for upcoming fixtures.

## predictor-data-reactive ![Coverage](.github/badges/data-coverage.svg) ![Branches](.github/badges/data-branches.svg)

Microservice that persists results data from the scraper service.

## predictor-event-data-reactive ![Coverage](.github/badges/event-data-coverage.svg) ![Branches](.github/badges/event-data-branches.svg)

Microservice that persists upcoming event data from the event scraper service.

## predictor-teams-reactive ![Coverage](.github/badges/teams-coverage.svg) ![Branches](.github/badges/teams-branches.svg)

Microservice that trains teams data.

## predictor-players-reactive ![Coverage](.github/badges/players-coverage.svg) ![Branches](.github/badges/players-branches.svg)

Microservice that trains players data.

## predictor-events-reactive ![Coverage](.github/badges/events-coverage.svg) ![Branches](.github/badges/events-branches.svg)

Microservice that predicts team events.

## predictor-players-events-reactive ![Coverage](.github/badges/players-events-coverage.svg) ![Branches](.github/badges/players-events-branches.svg)

Microservice that predicts players events.

## predictor-message-reactive ![Coverage](.github/badges/message-coverage.svg) ![Branches](.github/badges/message-branches.svg)

Microservice that acts as a message broker for all other services.  Includes some test apis for smoke tests, replacing the machine learning instances.

## predictor-client-reactive ![Coverage](.github/badges/client-coverage.svg) ![Branches](.github/badges/client-branches.svg)

Microservice that creates all the output for the mobile application in json format, storing in s3 and accessed from the mobile app via api gateway / lambdas.

### running locally

To run locally simply start the root compose script, wait for services to start and then execute curl command

```
docker-compose up -d
curl -X POST "http://localhost:8100/message" -H "accept: */*" -H "Content-Type: application/json" -d "{\"event\":\"START\",\"eventType\":\"ALL\"}"
```

### machine learning instances

Currently these are not part of the local setup, and linked to s3.  Will be added to local setup at some point

#### building machine learning apps

Currently they are deployed as a flask app into a maching learning tuned ec2 instance.  At some point look to dockerize these

```
python3 predictor-ml-model/setup.py sdist bdist_wheel
then install the pkg on relevant server .. ie predictor_pkg-0.0.1-py3-none-any.whl 
```
