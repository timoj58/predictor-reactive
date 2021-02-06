import logging

import model.match_model as match_model
import model.model_utils as model_utils
import service.receipt_service as receipt_service
from service.config_service import get_dir_cfg

local_dir = get_dir_cfg()['local']
logger = logging.getLogger(__name__)


def predict(data, init, label, label_values, model_dir, receipt):
    # def create(type, country, train, label, label_values, model_dir, train_filename, test_filename, outcome, previous_vocab_date):
    # there is no guarantee the predict is on same day as the train.  so we need the history
    classifier = match_model.create(
        train=False,
        label=label,
        label_values=label_values,
        model_dir=model_dir,
        train_filename='',
        test_filename='',
        init=init)

    player = []
    home = []
    away = []

    # Generate predictions from the model

    home.append(data['home'])
    away.append(data['away'])
    player.append(data['player'])

    predict_x = {
        'player': player,
        'home': home,
        'away': away
    }

    response = model_utils.predict(
        classifier=classifier,
        predict_x=predict_x,
        label_values=label_values)

    if init:
        logger.info('tidying up')
        match_model.tidy_up(local_dir + '/models/' + model_dir, None, None, None)

    receipt_service.put_receipt(receipt_service.PREDICT_RECEIPT_URL, receipt, response)
