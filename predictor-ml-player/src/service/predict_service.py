import logging

import model.match_model as match_model
import model.model_utils as model_utils
import service.receipt_service as receipt_service
from service.config_service import get_dir_cfg

local_dir = get_dir_cfg()['local']
logger = logging.getLogger(__name__)


def predict(data, init, label, label_values, model_dir):
    classifier = match_model.create(
        train=False,
        label=label,
        label_values=label_values,
        model_dir=model_dir,
        train_filename='',
        test_filename='',
        init=init)
    responses = []

    for item in data:

        player = []
        home = []
        opponent = []
        # Generate predictions from the model

        opponent.append(item['opponent'])
        home.append(item['home'])
        player.append(item['player'])

        predict_x = {
            'player': player,
            'opponent': opponent,
            'home': home
        }

        response = model_utils.predict(
            classifier=classifier,
            predict_x=predict_x,
            label_values=label_values)

        response['id'] = item['id']
        responses.append(response)

        if init:
            logger.info('tidying up')
            match_model.tidy_up(local_dir + '/models/' + model_dir, None, None, None)

    receipt_service.put_receipt(receipt_service.PREDICT_RECEIPT_URL, None, responses)
