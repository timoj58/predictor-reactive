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

action_check () {
    action=false
    while [ "action" != "true" ]
    do
      action=$(curl http://localhost:8100/status/$1)
      echo $1 action
      sleep 60
    done

    return 1
}

health_check () {
    health='waiting'
    while [ "$health" != "{\"status\":\"UP\"}" ]
    do
      health=$(curl http://localhost:$1/actuator/health)
      echo $1 $health
      sleep 10
    done

    return 1
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

  action_check "SCRAPE"
  echo "scrape completed"
  action_check "TRAIN_TEAMS"
  echo "teams trained"
  action_check "PREDICT_TEAMS"
  echo "teams predicted"
  action_check "TRAIN_PLAYERS"
  echo "players trained"
  action_check "PREDICT_PLAYERS"
  echo "players predicted"
  action_check "STOP_TEAM_MACHINE"
  echo "team machine stopped"
  action_check "STOP_PLAYERS_MACHINE"
  echo "players machine stopped"
  action_check "FINALISE"
  echo "completed"

  deploy=true #fix this....needs a timeout etc.  sort of pointless at moment.

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






