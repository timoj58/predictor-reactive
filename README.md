# predictor-reactive

![Coverage](.github/badges/scraper.svg)

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
