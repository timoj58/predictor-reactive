from flask import Flask
from flask import request
import predict.match_result_prediction as match_result_prediction
import predict.match_goals_prediction as match_goals_prediction
import train.match_result_train as match_result_train
import train.match_goals_train as match_goals_train
from util.config_utils import get_dir_cfg

import json
import logging
import threading
import traceback

app = Flask(__name__)

logging.basicConfig(filename=get_dir_cfg()['local']+'predictor.log',level=logging.NOTSET)
logger = logging.getLogger(__name__)


if __name__ == "__main__":
    app.run(host='0.0.0.0')


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



@app.route('/predict/goals/<country>/<receipt>',  methods=['POST'])
def predict_goals(country, receipt):
    thread = threading.Thread(target=match_goals_prediction.predict,
                              args=(json.loads(request.data), country, receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/predict/result/<country>/<receipt>',  methods=['POST'])
def predict_result(country, receipt):
    thread = threading.Thread(target=match_result_prediction.predict,
                              args=(json.loads(request.data), country, receipt))
    process(thread)

    return json.dumps(done_response())


# need to also schedule this -- this is for me to get it started.
@app.route('/train/results/<receipt>', methods=['POST'])
def train_results(receipt):
    thread = threading.Thread(target=match_result_train.train,
                              args=(receipt))
    process(thread)

    return json.dumps(done_response())


# need to also schedule this -- this is for me to get it started.
@app.route('/train/results/<country>/<receipt>', methods=['POST'])
def train_country_results(country, receipt):
    thread = threading.Thread(target=match_result_train.train_country,
                          args=(country, receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/train/goals/<receipt>', methods=['POST'])
def train_total_goals(receipt):
    thread = threading.Thread(target=match_goals_train.train,
                              args=(receipt))
    process(thread)

    return json.dumps(done_response())


# need to also schedule this -- this is for me to get it started.
@app.route('/train/goals/<country>/<receipt>', methods=['POST'])
def train_country_total_goals(country, receipt):
    thread = threading.Thread(target=match_goals_train.train_country,
                              args=(country, receipt))
    process(thread)

    return json.dumps(done_response())
