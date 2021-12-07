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
    while [ "$action" != "true" ]
    do
      action=$(curl http://localhost:8100/status/$1)
      echo $1 $action
      sleep $2
    done

    return 1
}

health_check () {
    health='waiting'
    while [ "$health" != "{\"status\":\"UP\"}" ]
    do
      health=$(curl http://localhost:$1/actuator/health)
      echo $1 $health
      sleep $2
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
  sleep 30

  # need a timeout at some point.  to do.
  for i in "${ports[@]}"
  do
     health_check $i 2
  done

  echo 'services are up'
  $(curl -X POST "http://localhost:8100/message" -H "accept: */*" -H "Content-Type: application/json" -d "{\"event\":\"START\",\"eventType\":\"ALL\"}")

  action_check "SCRAPE" 5
  echo "scrape started"
  action_check "TRAIN_TEAMS" 10
  echo "teams training started"
  action_check "PREDICT_TEAMS" 10
  echo "teams predictions started"
  action_check "STOP_TEAM_MACHINE" 10
  echo "team predictions finished"
  action_check "TRAIN_PLAYERS" 30
  echo "players training started"
  action_check "PREDICT_PLAYERS" 10
  echo "players predictions started"
  action_check "STOP_PLAYERS_MACHINE" 10
  echo "players predictions finished"
  action_check "FINALISE" 30
  echo "completed...shutting down"

  # ideally may want to preserve database to review.  add better tests in future to verify it
  docker-compose down

  echo 'deploy images'
  for i in "${microservices[@]}"
  do
    docker push timmytime/$i
  done
else
   echo 'microservices failed to package'
fi

exit 0







