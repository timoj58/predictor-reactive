from flask import Flask
from flask import request
import predict.player_goals_prediction as player_goals_prediction
import predict.player_assists_prediction as player_assists_prediction
import predict.player_saves_prediction as player_saves_prediction
import predict.player_minutes_prediction as player_minutes_prediction
import predict.player_conceded_prediction as player_conceded_prediction
import predict.player_red_card_prediction as player_red_card_prediction
import predict.player_yellow_card_prediction as player_yellow_card_prediction

import util.classifier_utils as classifier_utils
import util.model_utils as model_utils

import train.player_saves_train as player_saves_train
import train.player_goals_train as player_goals_train
import train.player_assists_train as player_assists_train
import train.player_minutes_train as player_minutes_train
import train.player_conceded_train as player_conceded_train
import train.player_red_card_train as player_red_card_train
import train.player_yellow_card_train as player_yellow_card_train
from util.config_utils import get_dir_cfg

import json
import logging
import threading
import traceback

app = Flask(__name__)

logging.basicConfig(filename=get_dir_cfg()['local']+'predictor.log',level=logging.NOTSET)
logger = logging.getLogger(__name__)

local_dir = get_dir_cfg()['local']

if __name__ == "__main__":
    app.run(host='0.0.0.0')

def set_init(init):
  if init == 'true':
    return True
  else:
    return False



##doesnt seem to do anything, should catch interrupted tho.
def process(thread):
    try:
     thread.start()
    except Exception as e:
      logger.error(traceback.format_exc())

# should handle errors at some point
def done_response():
    item = {}
    item['status'] = 'Done'
    return item

@app.route('/info')
def test_app():
    return json.dumps(done_response())


@app.route('/predict/init/<type>',  methods=['PUT'])
def predict_init(type):
   # load all the models ready. TODO.  saves lots of time when predicting thousands of events.
   classifier_utils.init_models(model_dir='models/'+type)
   return 'Ok'

@app.route('/predict/clear-down/<type>',  methods=['PUT'])
def predict_clear_down(type):
   # clear down all the models. TODO .. clear up space..happens on machine shut down anyway.
   return model_utils.tidy_up(
       tf_models_dir=local_dir+'/models/'+type,
       aws_model_dir=None,
       team_file=None,
       train_filename=None
   )



@app.route('/predict/goals/<init>/<receipt>',  methods=['POST'])
def predict_goals(init, receipt):
    thread = threading.Thread(target=player_goals_prediction.predict,
                              args=(json.loads(request.data), set_init(init), receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/predict/saves/<init>/<receipt>',  methods=['POST'])
def predict_saves(init, receipt):
    thread = threading.Thread(target=player_saves_prediction.predict,
                              args=(json.loads(request.data), set_init(init), receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/predict/assists/<init>/<receipt>',  methods=['POST'])
def predict_assists(init, receipt):
    thread = threading.Thread(target=player_assists_prediction.predict,
                              args=(json.loads(request.data), set_init(init), receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/predict/minutes/<init>/<receipt>',  methods=['POST'])
def predict_minutes(init, receipt):
    thread = threading.Thread(target=player_minutes_prediction.predict,
                              args=(json.loads(request.data), set_init(init), receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/predict/conceded/<init>/<receipt>',  methods=['POST'])
def predict_conceded(init, receipt):
    thread = threading.Thread(target=player_conceded_prediction.predict,
                              args=(json.loads(request.data), set_init(init), receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/predict/red-card/<init>/<receipt>',  methods=['POST'])
def predict_red(init, receipt):
    thread = threading.Thread(target=player_red_card_prediction.predict,
                              args=(json.loads(request.data), set_init(init), receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/predict/yellow-card/<init>/<receipt>',  methods=['POST'])
def predict_yellow(init, receipt):
    thread = threading.Thread(target=player_yellow_card_prediction.predict,
                              args=(json.loads(request.data), set_init(init), receipt))
    process(thread)

    return json.dumps(done_response())


# need to also schedule this -- this is for me to get it started.
@app.route('/train/conceded/<player>/<receipt>', methods=['POST'])
def train_goals_conceded(player, receipt):
    thread = threading.Thread(target=player_conceded_train.train,
                              args=(player, receipt))
    process(thread)

    return json.dumps(done_response())

@app.route('/train/goals/<player>/<receipt>', methods=['POST'])
def train_goals_scored(player, receipt):
    thread = threading.Thread(target=player_goals_train.train,
                              args=(player, receipt))
    process(thread)

    return json.dumps(done_response())

@app.route('/train/saves/<player>/<receipt>', methods=['POST'])
def train_saves(player, receipt):
    thread = threading.Thread(target=player_saves_train.train,
                              args=(player, receipt))
    process(thread)

    return json.dumps(done_response())

@app.route('/train/assists/<player>/<receipt>', methods=['POST'])
def train_assists(player, receipt):
    thread = threading.Thread(target=player_assists_train.train,
                              args=(player, receipt))
    process(thread)

    return json.dumps(done_response())

@app.route('/train/minutes/<player>/<receipt>', methods=['POST'])
def train_minutes(player, receipt):
    thread = threading.Thread(target=player_minutes_train.train,
                              args=(player, receipt))
    process(thread)

    return json.dumps(done_response())

@app.route('/train/red-card/<player>/<receipt>', methods=['POST'])
def train_red(player, receipt):
    thread = threading.Thread(target=player_red_card_train.train,
                              args=(player, receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/train/yellow-card/<player>/<receipt>', methods=['POST'])
def train_yellow(player, receipt):
    thread = threading.Thread(target=player_yellow_card_train.train,
                              args=(player, receipt))
    process(thread)

    return json.dumps(done_response())
