import util.receipt_utils as receipt_utils
import model.match_model as match_model
from util.config_utils import get_dir_cfg
import util.model_utils as model_utils
import logging

local_dir = get_dir_cfg()['local']
logger = logging.getLogger(__name__)


def predict(data, init, label, label_values,  model_dir, receipt):

#def create(type, country, train, label, label_values, model_dir, train_filename, test_filename, outcome, previous_vocab_date):
    # there is no guarantee the predict is on same day as the train.  so we need the history
    classifier =  match_model.create(
                   train=False,
                   label=label,
                   label_values=label_values,
                   model_dir=model_dir,
                   train_filename='',
                   test_filename='',
                   init=init)

    player = []
    home = []
    opponent = []

    # Generate predictions from the model

    opponent.append(data['opponent'])
    home.append(data['home'])
    player.append(data['player'])

    predict_x = {
        'player': player,
        'opponent': opponent,
        'home': home
    }

    response = model_utils.predict(
        classifier=classifier,
        predict_x=predict_x,
        label_values=label_values)

    if init:
     logger.info('tidying up')
     match_model.tidy_up(local_dir+'/models/'+model_dir,None, None, None)

    receipt_utils.put_receipt(receipt_utils.PREDICT_RECEIPT_URL, receipt,response)
