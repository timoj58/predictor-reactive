import json
import logging
import model.model_utils as model_utils
import predict.player_assists_prediction as player_assists_prediction
import predict.player_goals_prediction as player_goals_prediction
import predict.player_yellow_card_prediction as player_yellow_card_prediction
import threading
import traceback
import train.player_assists_train as player_assists_train
import train.player_goals_train as player_goals_train
import train.player_yellow_card_train as player_yellow_card_train
import util.classifier_utils as classifier_utils
from flask import Flask
from flask import request
from service.config_service import get_dir_cfg

app = Flask(__name__)

logging.basicConfig(filename=get_dir_cfg()['local'] + 'predictor.log', level=logging.NOTSET)
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


@app.route('/predict/init/<type>', methods=['PUT'])
def predict_init(type):
    # load all the models ready. TODO.  saves lots of time when predicting thousands of events.
    classifier_utils.init_models(model_dir='models/' + type)
    return 'Ok'


@app.route('/predict/clear-down/<type>', methods=['PUT'])
def predict_clear_down(type):
    # clear down all the models. TODO .. clear up space..happens on machine shut down anyway.
    return model_utils.tidy_up(
        tf_models_dir=local_dir + '/models/' + type,
        aws_model_dir=None,
        team_file=None,
        train_filename=None
    )


@app.route('/predict/goals/<init>', methods=['POST'])
def predict_goals(init):
    thread = threading.Thread(target=player_goals_prediction.predict,
                              args=(json.loads(request.data), set_init(init)))
    process(thread)

    return json.dumps(done_response())


@app.route('/predict/assists/<init>', methods=['POST'])
def predict_assists(init):
    thread = threading.Thread(target=player_assists_prediction.predict,
                              args=(json.loads(request.data), set_init(init)))
    process(thread)

    return json.dumps(done_response())


@app.route('/predict/yellow-card/<init>', methods=['POST'])
def predict_yellow(init):
    thread = threading.Thread(target=player_yellow_card_prediction.predict,
                              args=(json.loads(request.data), set_init(init)))
    process(thread)

    return json.dumps(done_response())


@app.route('/train/goals/<start>/<end>/<receipt>', methods=['POST'])
def train_goals_scored(start, end, receipt):
    thread = threading.Thread(target=player_goals_train.train,
                              args=(start, end, receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/train/assists/<start>/<end>/<receipt>', methods=['POST'])
def train_assists(start, end, receipt):
    thread = threading.Thread(target=player_assists_train.train,
                              args=(start, end, receipt))
    process(thread)

    return json.dumps(done_response())


@app.route('/train/yellow-card/<start>/<end>/<receipt>', methods=['POST'])
def train_yellow(start, end, receipt):
    thread = threading.Thread(target=player_yellow_card_train.train,
                              args=(start, end, receipt))
    process(thread)

    return json.dumps(done_response())
