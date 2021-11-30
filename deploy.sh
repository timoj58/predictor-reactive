#!/bin/bash

build=true
deploy=false

microservices=(
predictor-scraper-reactive
predictor-event-scraper-reactive
predictor-data-reactive
predictor-event-data-reactive
predictor-teams-reactive
predictor-players-reactive
predictor-events-reactive
predictor-players-events-reactive
predictor-message-reactive
predictor-client-reactive)

ports=(
8100
8101
8102
8103
8104
8105
8106
8107
8108
8109)


health_check () {
    health='waiting'
    while [ "$health" != "{\"status\":\"UP\"}" ]
    do
      health=$(curl http://localhost:$1/actuator/health)
      echo $health
      sleep 10
    done

    return true
}

for i in "${microservices[@]}"
do
  mvn -f $i/pom.xml clean package
  if [ $? == 0 ]; then
    docker build -t timmytime/$i $i/
  else
    echo $i' failed to package service'
    build=false
  fi
done


if [ "$build" = true ] ; then
   echo 'e2e test runner'
  docker-compose up -d

  # need a timeout at some point.  to do.
  for i in "${ports[@]}"
  do
     health_check $i
  done

  echo 'services are up'
  $(curl -X POST "http://localhost:8100/message" -H "accept: */*" -H "Content-Type: application/json" -d "{\"event\":\"START\",\"eventType\":\"ALL\"}")
   # check for test pass..TODO.
   # this will also need a timeout.
  if [ "deploy" = true ] ; then
     echo 'deploy images'
     for i in "${microservices[@]}"
     do
       docker push timmytime/$i
     done
  else
     echo 'e2e test runner has failed, no deployment'
  fi
else
   echo 'microservices failed to package'
fi






